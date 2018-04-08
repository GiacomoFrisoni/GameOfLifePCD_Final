package controller;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import model.GenerationResult;
import view.GameOfLifeFrame;
import view.ProgressType;

/**
 * This class models a Game of Life Consumer.
 * It picks up the results of the computations from the buffer and shows them
 * on video with the minimum frequency desired.
 *
 */
public class GameOfLifeConsumer extends Thread {
	
	// Time when last update happened. Used for controlling the frame rate
	private long lastUpdate;
	
	// Desired frame duration
	private volatile int minTickTime;
	
	private final BlockingQueue<GenerationResult> queue;
	private final GameOfLifeFrame view;
	private CountDownLatch latch;
	private final Flag stopFlag;
	
	
	/**
	 * Constructs a new Game of Life consumer.
	 * 
	 * @param queue
	 * 		the producer / consumer queue
	 * @param view
	 * 		the application view
	 * @param stopFlag
	 * 		the stop flag
	 */
	public GameOfLifeConsumer(final BlockingQueue<GenerationResult> queue, final GameOfLifeFrame view,
			final Flag stopFlag, final int defaultMinTickTime) {
		this.queue = queue;
		this.view = view;
		this.stopFlag = stopFlag;
		this.minTickTime = defaultMinTickTime;
	}
	
	/**
	 * Counts time that passed since last game update
	 * and sleeps for a while if this time was shorter than target frame time.
	 * @throws InterruptedException
	 */
	private void limitFPS() throws InterruptedException {
		final long now = System.currentTimeMillis();
		if (lastUpdate > 0) {
			final long delta = now - lastUpdate;
			if (delta < this.minTickTime) {
				Thread.sleep(this.minTickTime - delta);
			}
		} else {
			Thread.sleep(this.minTickTime);
		}
		lastUpdate = System.currentTimeMillis();
	}
	
	/**
	 * @return the current minimum delay used by the view consumer.
	 */
	public int getConsumerSpeed() {
		return this.minTickTime;
	}
	
	/**
	 * Sets the minimum delay between each view consumer representation.
	 * 
	 * @param minTickTime
	 * 		the minimum delay between each frame
	 */
	public void setConsumerSpeed(final int minTickTime) {
		this.minTickTime = minTickTime;
	}
	
	@Override
	public void run() {
		GenerationResult res;
		while (!stopFlag.isOn()) {
			try {
				// Waits for minimum view updating frequency
				limitFPS();
				
				// Retrieves a generation result, waiting if necessary until an element becomes available.
				if (!stopFlag.isOn())
					this.view.setProgress(ProgressType.INDETERMINATE, "Computing next generation...");
				res = queue.take();
				
				// Updates view
				this.view.setGenerationInfo(res.getGenerationNumber(), res.getComputationTime(), res.getAliveCells());
				this.view.updateProgress(0);
				this.latch = new CountDownLatch(1);
				this.view.drawCells(res.getCellsStates(), this.latch);
				this.latch.await();
			} catch (InterruptedException ie) {
				view.showAlert("Thread error", "Someone killed the consumer when was waiting for something. Please reset.\n\n" + ie.getMessage());
			}
		}
	}
	
}
