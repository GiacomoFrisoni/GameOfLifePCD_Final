package controller;

import java.util.concurrent.Callable;

import model.ConwayCellMap;

/**
 * This class represents a task aimed at initializing a cell of the Game Of Life.
 *
 */
public class InitTask implements Callable<Void> {

	private final ConwayCellMap model;
	
	/**
	 * Constructs a new initializing task.
	 * 
	 * @param model
	 * 		the application model
	 */
	public InitTask(final ConwayCellMap model) {
		this.model = model;
	}
	
	@Override
	public Void call() throws Exception {
		this.model.randomInitCell();
		return null;
	}

}
