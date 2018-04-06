package controller;

/**
 * This class represents a monitor flag.
 * It guarantees that only one process at a time can be running on a method.
 */
public class Flag {
	
	private boolean status;
	
	/**
	 * Constructs a new flag.
	 */
	public Flag() {
		this.status = false;
	}
	
	/**
	 * Sets the flag.
	 */
	public synchronized void setOn() {
		this.status = true;
	}
	
	/**
	 * Resets the flag.
	 */
	public synchronized void setOff() {
		this.status = false;
	}
	
	/**
	 * @return the status of the flag.
	 */
	public synchronized boolean isOn() {
		return this.status;
	}
	
}
