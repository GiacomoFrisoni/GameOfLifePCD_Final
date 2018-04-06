package controller;

import java.awt.Dimension;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.magicwerk.brownies.collections.BigList;

import model.ConwayCellMap;
import model.ConwayCellMapImpl;
import model.GenerationResult;
import view.GameOfLifeFrame;
import view.ProgressType;

/**
 * Implementation of {@link GameController}.
 */
public class GameControllerImpl implements GameController {

	private static final int BUFFER_SIZE = 100;
	private static final int PROGRESS_PERIOD = 250;
	private static final int DEFAULT_MIN_TICK_TIME = 1500;
	
	private ConwayCellMap model;
	private final GameOfLifeFrame view;
	private final BlockingQueue<GenerationResult> queue;
	private final ExecutorService executor;
	private final Flag stopFlag;
	private Optional<GameOfLifeProducer> producer;
	private Optional<GameOfLifeConsumer> consumer;
	private int minTickTime;
	private boolean isMapInitialized;
	
	private Optional<ScheduledFuture<?>> updatingPool;
	
	
	/**
	 * Constructs a new game controller.
	 * 
	 * @param view
	 * 		the Game Of Life view
	 */
	public GameControllerImpl(final GameOfLifeFrame view) {
		Objects.requireNonNull(view);
		this.view = view;
		this.isMapInitialized = false;
		// Calculates the pool size for tasks executor, according to the processors number
		final int poolSize = Runtime.getRuntime().availableProcessors() + 1;
		// Initializes the executor
		this.executor = Executors.newFixedThreadPool(poolSize);
		// Initializes the stop flag
		this.stopFlag = new Flag();
		this.stopFlag.setOn();
		// Initializes the updating schedule
		this.updatingPool = Optional.empty();
		// Creates the producer / consumer queue
		this.queue = new ArrayBlockingQueue<>(BUFFER_SIZE);
		// Initializes the producer and the consumer
		this.producer = Optional.empty();
		this.consumer = Optional.empty();
		this.minTickTime = DEFAULT_MIN_TICK_TIME;
	}
	
	
	/*
	 * Randomly initializes the model cell map to about 50% on-cells.
	 * Creates tasks for the initialization and awaits their end. 
	 */
	private void initCellMap() {
		Objects.requireNonNull(this.model);
		this.model.clear();
		this.view.setProgress(ProgressType.INDETERMINATE, "Initializing...");
		try {
			final BigList<Callable<Void>> initTasks = new BigList<>();
			final Dimension cellMapDimension = this.model.getCellMapDimension();
			int initLength = (cellMapDimension.width * cellMapDimension.height) / 2;
			this.view.setProgress(ProgressType.INDETERMINATE, "Creating tasks...");
			do {
				initTasks.add(new InitTask(model));
			} while (--initLength > 0);
			
			this.view.setProgress(ProgressType.INDETERMINATE, "Executing...");
			this.executor.invokeAll(initTasks);
		} catch (InterruptedException e) {
			view.showErrorAlert("Init failed", "Failed to do the init", e.getMessage());
		}
		
		this.view.setProgress(ProgressType.INDETERMINATE, "Computing next generation...");
		this.model.nextGeneration();
		this.isMapInitialized = true;
		this.view.reset();
		this.view.drawCells(this.model.getCellMapStates(), null);
	}
	
	
	/*
	 * Gets the map dimension specified as input from the view and
	 * initializes the model. Return true if the operation is successful,
	 * false otherwise.
	 */
	private boolean initModel() {
		final Optional<Dimension> mapDimension = view.getMapDimension();
		if (mapDimension.isPresent()) {
			this.model = new ConwayCellMapImpl(mapDimension.get().width, mapDimension.get().height);
			return true;
		}
		return false;
	}
	
	
	@Override
	public void start() {
		new Thread(() -> {
			
			if (!isMapInitialized) {
				if (initModel()) {
					initCellMap();
				} else {
					view.showAlert("Failed to init", "Failed to start. Maybye some input field are empty");
				}
			}
			
			if (isMapInitialized) {
				stopFlag.setOff();
				
				// Starts producer and consumer threads
				producer = Optional.of(new GameOfLifeProducer(queue, executor, model, stopFlag, view));
				consumer = Optional.of(new GameOfLifeConsumer(queue, view, stopFlag, minTickTime));
				producer.get().start();
				consumer.get().start();
				
				updatingPool = Optional.of(Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Thread(new Runnable() {
					@Override
					public void run() {
						if (queue.isEmpty()) {
							view.updateProgress(model.getPercentageCompletion());
						}
					}
				}), 0, PROGRESS_PERIOD, TimeUnit.MILLISECONDS));
				
				
				view.setStarted();
			}
			
		}).start();	
	}
	
	@Override
	public void stop() {
		new Thread(new Runnable() {		
			@Override
			public void run() {
				view.setProgress(ProgressType.INDETERMINATE, "Stopping...");
				stopFlag.setOn();
				view.setStopped();
				view.setProgress(ProgressType.IDLE, "(Stopped) Idle");
				producer = Optional.empty();
				consumer = Optional.empty();
				updatingPool.ifPresent(p -> p.cancel(true));
				updatingPool = Optional.empty();
			}
		}).start();
	}

	@Override
	public void reset() {
		new Thread(new Runnable() {		
			@Override
			public void run() {
				view.setProgress(ProgressType.INDETERMINATE, "Resetting...");
				stopFlag.setOn();
				queue.clear();
				isMapInitialized = false;
				view.reset();
				view.setProgress(ProgressType.IDLE, "Idle");
				producer = Optional.empty();
				consumer = Optional.empty();
				updatingPool.ifPresent(p -> p.cancel(true));
				updatingPool = Optional.empty();
			}
		}).start();
	}
	
	@Override
	public Dimension getCellMapDimension() {
		return this.model.getCellMapDimension();
	}

	@Override
	public int getViewSpeed() {
		return this.minTickTime;
	}
	
	@Override
	public void setViewSpeed(final int minimumDelay) {
		this.minTickTime = minimumDelay;
		this.consumer.ifPresent(c -> c.setConsumerSpeed(minimumDelay));
	}
	
}
