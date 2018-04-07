package model;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * This class represents the Model, as it contains all game of life elements.
 * Implementation of {@link ConwayCellMap}.
 * 
 */
public class ConwayCellMapImpl implements ConwayCellMap {
	
	private final Dimension mapDimension;
	private long generation;
	
	private static final int MIN_NEIGHBORS = 0;
	private static final int MAX_NEIGHBORS = 8;
	
	private boolean[] cells;
	private boolean[] nextCells;
	private byte[] neighbors;
	private AtomicIntegerArray nextNeighbors;
	
	private List<Point> cellsToEvaluate;
	private long computedCells;
	
	
	/**
	 * Conway's cell map constructor.
	 * 
	 * @param width
	 * 		the width of the cell map
	 * @param height
	 * 		the height of the cell map
	 */
	public ConwayCellMapImpl(final int width, final int height) {
		// Checks cell map dimension
		if (width < 1) {
			throw new IllegalArgumentException("Cell map width must be positive");
		}
		if (height < 1) {
			throw new IllegalArgumentException("Cell map height must be positive");
		}
		
		// Sets cell map dimension
		this.mapDimension = new Dimension(width, height);
		
		final int cellsNumber = width * height;
		
		// Creates the cell map
		this.cells = new boolean[cellsNumber];
		
		// Creates an unaltered version of the cell map from which to work
		this.nextCells = new boolean[cellsNumber];
		
		// Creates the structure for on-neighbors memorization and its unaltered version
		this.neighbors = new byte[cellsNumber];
		this.nextNeighbors = new AtomicIntegerArray(cellsNumber);
		
		// Creates the list with the cells to evaluate for the current generation
		this.cellsToEvaluate = new ArrayList<>();
				
		// Initializes number of generations
		this.generation = 0;
		
		// Initializes number of computed cells
		this.computedCells = 0;
	}
	
	@Override
	public Dimension getCellMapDimension() {
		return new Dimension(this.mapDimension);
	}
	
	@Override
	public long getGenerationNumber() {
		return this.generation;
	}
	
	/*
	 * Converts 2D array index to 1D, according to the dimension of the cell map.
	 */
	private int encode(final int x, final int y) {
		return y * this.mapDimension.width + x;
	}
	
	@Override
	public boolean[][] getCellMapStates() {
		final int height = this.mapDimension.height;
		final int width = this.mapDimension.width;
		final boolean[][] res = new boolean[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				res[i][j] = this.cells[encode(j, i)];
			}
		}
		return res;
	}
	
	/*
	 * Calculates the cells to evaluate for the current generation
	 * (it excludes off-cells with no alive neighbor).
	 */
	private void calculatesCellsToEvaluate() {
		this.cellsToEvaluate.clear();
		final int height = this.mapDimension.height;
		final int width = this.mapDimension.width;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				final int cellIndex = encode(j, i);
				final boolean state = this.cells[cellIndex];
				if (state || (!state && this.neighbors[cellIndex] > 0)) {
					this.cellsToEvaluate.add(new Point(j, i));
				}
			}
		}
	}
	
	@Override
	public List<Point> getCellsToEvaluate() {
		return this.cellsToEvaluate;
	}
	
	/*
	* Check if a specified position is inside the cell map.
	*/
	private boolean isInsideCellMap(final int x, final int y) {
		return x >= 0 && x < this.mapDimension.width && y >= 0 && y < this.mapDimension.height;
	}
	
	/*
	 * Turns an off-cell on, incrementing the on-neighbor count for
	 * the eight neighboring cells.
	 */
	private void setCellStateOn(final int x, final int y) {
		final int cellIndex = encode(x, y);
		final boolean state = this.cells[cellIndex];
		if (!state) {
			// Turns on the cell
			this.nextCells[cellIndex] = true;
			// Increments the on-neighbor count for each neighbor
			for (int i = y - 1; i <= y + 1; i++) {
				for (int j = x - 1; j <= x + 1; j++) {
					if (isInsideCellMap(j, i) && (i != y || j != x)) {
						final int neighborIndex = encode(j, i);
						this.nextNeighbors.set(neighborIndex,
								Math.min(this.nextNeighbors.incrementAndGet(neighborIndex), MAX_NEIGHBORS));
					}
				}
			}
		}
	}
	
	/*
	 * Turns an on-cell off, decrementing the on-neighbor count for
	 * the eight neighboring cells.
	 */
	private void setCellStateOff(int x, int y) {
		final int cellIndex = encode(x, y);
		final boolean state = this.cells[cellIndex];
		if (state) {
			// Turns off the cell
			this.nextCells[cellIndex] = false;
			// Decrements the on-neighbor count for each neighbor
			for (int i = y - 1; i <= y + 1; i++) {
				for (int j = x - 1; j <= x + 1; j++) {
					if (isInsideCellMap(j, i) && (i != y || j != x)) {
						final int neighborIndex = encode(j, i);
						this.nextNeighbors.set(neighborIndex,
								Math.max(this.nextNeighbors.decrementAndGet(neighborIndex), MIN_NEIGHBORS));
					}
				}
			}
		}
	}
	
	@Override
	public List<Boolean> computeCells(final List<Point> cells) {
		final List<Boolean> res = new ArrayList<>();
		for (final Point p : cells) {        
			final int cellIndex = encode(p.x, p.y);
	  		final boolean state = this.cells[cellIndex];
	  		boolean nextState = state;
	  		byte onNeighborCount = this.neighbors[cellIndex];
	  		if (state) {
	  			if ((onNeighborCount < 2) || (onNeighborCount > 3)) {
	  				setCellStateOff(p.x, p.y);
	  				nextState = false;
	  			}
	  		} else {
	  			if (onNeighborCount == 3) {
	  				setCellStateOn(p.x, p.y);
	  				nextState = true;
	  			}
	  		}
	  		this.computedCells++;
	  		res.add(nextState);
		}
		return res;
	}
	
	@Override
	public double getPercentageCompletion() {
		return (double) this.computedCells / (double) this.cellsToEvaluate.size();
	}
	
	@Override
	public void nextGeneration() {
		this.computedCells = 0;
		// Sets current cell map states = next cell map states
		System.arraycopy(this.nextCells, 0, this.cells, 0, this.cells.length);
		// Sets current cell map neighbors = next cell map neighbors
		for (int i = 0; i < this.nextNeighbors.length(); i++) {
			this.neighbors[i] = (byte) this.nextNeighbors.get(i);
		}
		// Calculates cells to evaluate in the new generation
		calculatesCellsToEvaluate();
		// Increments generation number
		this.generation++;
	}
	
	@Override
	public void clear() {
		Arrays.fill(this.cells, false);
		Arrays.fill(this.nextCells, false);
		Arrays.fill(this.neighbors, (byte) 0);
		for (int i = 0; i < this.nextNeighbors.length(); i++) {
			this.nextNeighbors.set(i, 0);
		}
		this.cellsToEvaluate.clear();
		this.generation = 0;
		this.computedCells = 0;
	}
	
	@Override
	public void randomInitCell() {
		final int x = ThreadLocalRandom.current().nextInt(0, this.mapDimension.width);
		final int y = ThreadLocalRandom.current().nextInt(0, this.mapDimension.height);
		synchronized (this.nextCells) {
			if (!this.nextCells[encode(x, y)]) {
				setCellStateOn(x, y);
			}
		}
	}
	
	@Override
	public String toString() {
		final StringBuilder res = new StringBuilder();
		res.append("Cell map at generation " + this.generation + "\n");
		final int height = this.mapDimension.height;
		final int width = this.mapDimension.width;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				final int cellIndex = encode(j, i);
				res.append(" " + (this.cells[cellIndex] ? "O" : "X"));
				res.append("(" + this.neighbors[cellIndex] + ")");
			}
			res.append("\n");
		}
		return res.toString();
	}
}
