package model;

public class GenerationResult {
	
	private final long generationNumber;
	private final boolean[][] cellsStates;
	private final long cellsAlive;
	private final long computationTime;
	
	public GenerationResult(final long generationNumber, final boolean[][] cellsStates,
			final long cellsAlive, final long computationTime) {
		this.generationNumber = generationNumber;
		this.cellsStates = cellsStates;
		this.cellsAlive = cellsAlive;
		this.computationTime = computationTime;
	}
	
	public long getGenerationNumber() {
		return this.generationNumber;
	}
	
	public long getCellsAlive() {
		return this.cellsAlive;
	}
	
	public boolean[][] getCellsStates() {
		return this.cellsStates;
	}
	
	public long getComputationTime() {
		return this.computationTime;
	}
	
}
