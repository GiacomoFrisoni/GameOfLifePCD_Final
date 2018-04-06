package model;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;

import org.magicwerk.brownies.collections.BigList;

/**
 * This interface handles a cell map for the Game Of Life.
 *
 */
public interface ConwayCellMap {
	
	/**
	 * @return the dimension (width and height) of the cell map.
	 */
	Dimension getCellMapDimension();

	/**
	 * @return current generation number.
	 */
	long getGenerationNumber();
	
	/**
	 * @return the state (alive or death) of all the cells in the cell map.
	 */
	boolean[][] getCellMapStates();
	
	/**
	 * @return the cells to evaluate for current generation completion.
	 */
	List<Point> getCellsToEvaluate();
	
	/**
	 * Applies game of life rules to the specified cell.
	 * 
	 * @param x
	 * 		x coordinate of the cell to compute
	 * @param y
	 * 		y coordinate of the cell to compute
	 * @return the next status of the cell after the computation.
	 */
	boolean computeCell(int x, int y);
	
	/**
	 * @return the number of computed cells on the total expected for the
	 * current generation.
	 */
	double getPercentageCompletion();
	
	/**
	 * Goes to the next generation of the game.
	 */
	void nextGeneration();
	
	/**
	 * Resets all data.
	 */
	void clear();
	
	/**
	 * Randomly initializes the status of a cell in the map.
	 */
	void randomInitCell();
	
	
	List<Boolean> computeCells(final List<Point> cells);
}
