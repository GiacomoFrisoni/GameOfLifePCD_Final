package model;

/**
 * This class represents a set of statistics and data
 * for a generation of the game.
 *
 */
public class GenerationResult {
	
	private final long generationNumber;
	private final boolean[][] cellsStates;
	private final long aliveCells;
	private final long computationTime;
	
	/**
	 * Constructs a new GenerationResult.
	 * 
	 * @param generationNumber
	 * 		the number of the generation
	 * @param cellsStates
	 * 		the states of the cells
	 * @param aliveCells
	 * 		the number of alive cells
	 * @param computationTime
	 * 		the elapsed time for the computation
	 */
	public GenerationResult(final long generationNumber, final boolean[][] cellsStates,
			final long aliveCells, final long computationTime) {
		this.generationNumber = generationNumber;
		this.cellsStates = cellsStates;
		this.aliveCells = aliveCells;
		this.computationTime = computationTime;
	}
	
	/**
	 * @return the number of the generation
	 */
	public long getGenerationNumber() {
		return this.generationNumber;
	}
	
	/**
	 * @return the number of alive cells
	 */
	public long getAliveCells() {
		return this.aliveCells;
	}
	
	/**
	 * @return the states of the cells in the generation
	 */
	public boolean[][] getCellsStates() {
		return this.cellsStates;
	}
	
	/**
	 * @return the elapsed time for the computation
	 */
	public long getComputationTime() {
		return this.computationTime;
	}
	
}
