package controller;

import java.util.concurrent.Callable;

import model.ConwayCellMap;

/**
 * This class represents a task aimed at computing a cell for the current
 * generation of the Game Of Life.
 *
 */
public class ComputeTask implements Callable<Boolean> {

	private final ConwayCellMap model;
	private final int cellX;
	private final int cellY;
	
	/**
	 * Constructs a new compute task.
	 * 
	 * @param model
	 * 		the application model
	 * @param cellX
	 * 		the x coordinate of the cell
	 * @param cellY
	 * 		the y coordinate of the cell
	 */
	public ComputeTask(final ConwayCellMap model, final int cellX, final int cellY) {
		this.model = model;
		this.cellX = cellX;
		this.cellY = cellY;
	}
	
	@Override
	public Boolean call() {
		return this.model.computeCell(this.cellX, this.cellY);
	}

}
