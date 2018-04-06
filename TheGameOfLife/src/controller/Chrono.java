package controller;

/**
 * This class handles a custom timer.
 *
 */
public class Chrono {

	private boolean running;
	private long startTime;

	/**
	 * Constructs a new timer.
	 */
	public Chrono(){
		this.running = false;
	}
	
	/**
	 * Starts the timer.
	 */
	public void start(){
		this.running = true;
		this.startTime = System.currentTimeMillis();
	}
	
	/**
	 * Stops the timer.
	 */
	public void stop(){
		this.startTime = getTime();
		this.running = false;
	}

	/**
	 * @return the time elapsed between start and stop in milliseconds.
	 */
	public long getTime(){
		if (this.running){
			return 	System.currentTimeMillis() - startTime;
		} else {
			return startTime;
		}
	}
}
