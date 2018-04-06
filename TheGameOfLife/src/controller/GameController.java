package controller;

import java.awt.Dimension;

/**
 * This class represents the controller of the application.
 *
 */
public interface GameController {
	
	/**
	 * Starts the game.
	 */
	void start();
	
	/**
	 * Stops the game.
	 */
	void stop();
	
	/**
	 * Resets the game.
	 */
	void reset();
	
	/**
	 * @return the dimension (width and height) of the cell map.
	 */
	Dimension getCellMapDimension();
	
	/**
	 * @return the current minimum delay used by the view consumer.
	 */
	int getViewSpeed();
	
	/**
	 * Sets the minimum delay between each view consumer representation.
	 * 
	 * @param minimumDelay
	 * 		the minimum delay between each frame
	 */
	void setViewSpeed(int minimumDelay);
	
}
