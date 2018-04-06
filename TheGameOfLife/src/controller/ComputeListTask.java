package controller;

import java.awt.Point;
import java.util.List;
import java.util.concurrent.Callable;

import model.ConwayCellMap;

/*This

class represents
a task
aimed at
computing a cell for
the current*
generation of
the Game
Of Life.**/

public class ComputeListTask implements Callable<List<Boolean>> {

	private final ConwayCellMap model;
	private final List<Point> cells;

	public ComputeListTask(final ConwayCellMap model, final List<Point> cells) {
		this.model = model;
		this.cells = cells;
	}

	@Override
	public List<Boolean> call() {
		return this.model.computeCells(this.cells);
	}
}

