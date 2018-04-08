package controller;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import model.ConwayCellMap;
import model.GenerationResult;
import view.GameOfLifeFrame;

/**
 * This class models a Game of Life Producer.
 * It enumerates the updated cells for each game generation and then
 * put the results on the queue.
 *
 */
public class GameOfLifeProducer extends Thread {
	
	private final int CHUNK_SIZE = 100;
	
	private final BlockingQueue<GenerationResult> queue;
	private final ExecutorService executor;
	private final ConwayCellMap model;
	private final GameOfLifeFrame view;
	private final Flag stopFlag;

	
	/**
	 * Constructs a new Game of Life producer.
	 * 
	 * @param queue
	 * 		the producer / consumer queue
	 * @param executor
	 * 		the executor service
	 * @param model
	 * 		the application model
	 * @param view
	 * 		the application view
	 * @param stopFlag
	 * 		the stop flag
	 */
	public GameOfLifeProducer(final BlockingQueue<GenerationResult> queue, final ExecutorService executor,
			final ConwayCellMap model, final GameOfLifeFrame view, final Flag stopFlag) {
		this.queue = queue;
		this.executor = executor;
		this.model = model;
		this.view = view;
		this.stopFlag = stopFlag;
	}
	
	@Override
	public void run() {
		try {
			final Chrono cron = new Chrono();
			long cellsAlive;
			while (!stopFlag.isOn()) {
				cron.start();
				cellsAlive = 0;
				
				// Creates the list for the management of computational tasks
				final List<Callable<List<Boolean>>> tasks = new ArrayList<>();
		        final List<Point> cellsToEvaluate = this.model.getCellsToEvaluate();
		        final List<List<Point>> cellsChunks = new ArrayList<>();
		        
		        // Subdivides the work in chunks
		        final int size = cellsToEvaluate.size();
		        for (int i = 0; i < size; i+= CHUNK_SIZE) {
		        	cellsChunks.add(cellsToEvaluate.subList(i, Math.min(i + CHUNK_SIZE, size)));
		        }
		        
		        // Prepares a task for each chunk
		        for (final List<Point> cellChunk : cellsChunks) {
		        	tasks.add(new ComputeListTask(model, cellChunk));
		        }
		        
		        // Waits for tasks' results
		        final List<Future<List<Boolean>>> res = this.executor.invokeAll(tasks);
		        
		        // Counts the number of alive cells
		        for (final Future<List<Boolean>> f : res) {
		          for (final Boolean value : f.get()) {
		            if (value) {
		              cellsAlive++;
		            }
		          }
		        }
				
				cron.stop();
				
				// Prepares the new generation of the game
				this.model.nextGeneration();
				// Saves the generation results and statistics
				final GenerationResult generationResult = new GenerationResult(this.model.getGenerationNumber(),
						this.model.getCellMapStates(), cellsAlive, cron.getTime());
				/*
				 * The put() method will block if the queue is full, waiting for space becomes available.
				 * While waiting, it will throw InterruptedException if the current thread is interrupted.
				 */
				queue.put(generationResult);
			}
		} catch (InterruptedException | ExecutionException ie) {
			view.showAlert("Thread error", "Someone killed the producer when was waiting for something. Please reset.\n\n" + ie.getMessage());
		}
	}
	
}
