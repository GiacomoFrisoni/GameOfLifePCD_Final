package view;

import java.awt.Dimension;
import java.util.concurrent.CountDownLatch;

import controller.GameController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class CellMapViewer extends BorderPane {

	@FXML
	private CellMap cellMap;
	
	@FXML
	private Button left, top, right, bottom;
	
	
	private GameController controller;
	private MainFrame container;
	private int xPosition = 0, yPosition = 0;
	private int mapXLimit = 1, mapYLimit = 1;
	
	
	/**
	 * Create a new CellMapViewer
	 */
	public CellMapViewer() {
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CellMapViewer.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		
        try {
            fxmlLoader.load();
        } catch (Exception exception) {
        	MessageViewer.showExceptionAndExit(
        			"FXML Loading Exception", 
        			"CellMapViewer.fxml could not be loaded.", 
        			exception.getMessage());
        }  
	}
	
	/**
	 * Initialize a cellMapViewer
	 * @param controller
	 * 		controller of the main frame
	 * @param container
	 * 		container of the cellMapViewer
	 */
	public void init(final GameController controller, final MainFrame container) {
		//Controllers & containers
		this.controller = controller;
		this.container = container;
		
		//Action listeners
		this.left.setOnMouseClicked(e -> {
			if (this.xPosition > 0) {
				this.xPosition -= 1;
				updateMenuState();
			}
		});
		
		this.top.setOnMouseClicked(e -> {
			if (this.yPosition > 0) {
				this.yPosition -= 1;
				updateMenuState();
			}
		});
		
		this.right.setOnMouseClicked(e -> {
			if (this.xPosition < this.mapXLimit) {
				this.xPosition += 1;
				updateMenuState();
			}
		});
		
		this.bottom.setOnMouseClicked(e -> {
			if (this.yPosition < this.mapYLimit) {
				this.yPosition += 1;
				updateMenuState();
			}
		});
		
		this.cellMap.setContainer(this);
	}
	
	/**
	 * Draw the cells
	 * @param cells
	 * 		cells to draw
	 */
	public void drawCells(final boolean[][] cells, CountDownLatch latch) {
		this.cellMap.setCellsToDraw(cells, latch);
	}
	
	/**
	 * Calculates the x and y limit for the cell map rendering.
	 */
	public void calculateMapLimits() {
		final Dimension mapDimension = controller.getCellMapDimension();
		this.mapXLimit = (int) (mapDimension.width / cellMap.getDrawableXCellsNumber());
		this.mapYLimit = (int) (mapDimension.height / cellMap.getDrawableYCellsNumber());
	}
	
	/**
	 * Get the width of the center panel
	 * @return
	 * 		width of the center panel
	 */
	public double getCenterPanelX() {
		return this.getWidth() - this.left.getWidth() - this.right.getWidth();
	}
	
	/**
	 * Get the height of the center panel
	 * @return
	 * 		height of the center panel
	 */
	public double getCenterPanelY() {
		return this.getHeight() - this.top.getHeight() - this.bottom.getHeight();
	}
	
	/**
	 * Get the number of drawable cells in preview in width
	 * @return
	 * 		drawable cells in preview in width
	 */
	public int getDrawableXCellsNumber() {
		return this.cellMap.getDrawableXCellsNumber();
	}
	
	/**
	 * Get the number of drawable cells in preview in height
	 * @return
	 * 		drawable cells in preview in height
	 */
	public int getDrawableYCellsNumber() {
		return this.cellMap.getDrawableYCellsNumber();
	}

	/**
	 * Get the actual X position of the preview
	 * @return the X position of preview
	 */
	public int getXposition() {
		return xPosition;
	}
	
	/**
	 * Get the actual Y position of the preview
	 * @return the Y position of preview
	 */
	public int getYposition() {
		return yPosition;
	}
	
	public int getXLimit() {
		return mapXLimit;
	}
	
	public int getYLimit() {
		return mapYLimit;
	}
	
	/**
	 * Reset the component to initial state
	 */
	public void reset() {
		this.cellMap.reset();
		this.xPosition = 0;
		this.yPosition = 0;
		this.updateMenuState();
	}
	
	/**
	 * Performing operation when the preview change position
	 */
	private void updateMenuState() {
		this.cellMap.updatePosition(xPosition, yPosition);
		this.container.getMenuPanel().setCurrentPosition(xPosition, yPosition);
	}

}
