package view;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import controller.GameController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainFrame extends BorderPane implements GameOfLifeFrame {
	
	private static final int FRAME_SCALE = 70;
	private static final String WINDOW_TITLE = "\"The Game Of Life - Giacomo Frisoni & Marcin Pabich\"";
	
	private GameController controller;
	private final Stage stage;
	
	@FXML
	private CellMapViewer cellMapViewer;
	
	@FXML
	private MenuPanel menuPanel;
	

	/**
	 * Creates a new frame for the game rendering.
	 */
	public MainFrame(final Stage stage) {
		Objects.requireNonNull(stage);
        this.stage = stage;
        
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainFrame.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		
        try {
            fxmlLoader.load();
        } catch (Exception exception) {
        	MessageViewer.showExceptionAndExit(
        			"FXML Loading Exception",
        			"MainFrame.fxml could not be loaded",
        			exception.getMessage());
        }
	}
	
	@Override
	public void setObserver(final GameController observer) {
		this.controller = observer;
	}

	private void initView() {
		final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    	this.setWidth((gd.getDisplayMode().getWidth() * FRAME_SCALE) / 100);
    	this.setHeight((gd.getDisplayMode().getHeight() * FRAME_SCALE) / 100);
    	this.setMinWidth((gd.getDisplayMode().getWidth() * FRAME_SCALE) / 100);
    	this.setMinHeight((gd.getDisplayMode().getHeight() * FRAME_SCALE) / 100);
	}

	@Override
	public void showView() {
		initView();
		
		final Scene scene = new Scene(this);
		
		this.stage.setOnCloseRequest(e -> {
			this.closeView();
	        System.exit(0);
	        Platform.exit();
		});

		this.stage.setTitle(WINDOW_TITLE);
		this.stage.setScene(scene);
		this.stage.setResizable(false);
		this.stage.getIcons().addAll(
				new Image(("file:res/icon16x16.png")),
				new Image(("file:res/icon32x32.png")),
				new Image(("file:res/icon64x64.png")));
		this.stage.show();	

		
    	this.cellMapViewer.init(this.controller, this);
    	this.menuPanel.init(this.controller);
    	this.menuPanel.setViewableCells(this.cellMapViewer.getDrawableXCellsNumber(), this.cellMapViewer.getDrawableYCellsNumber());
	}
	
	@Override
	public void closeView() {
		this.stage.close();
	}

	@Override
	public void drawCells(final boolean[][] cells, CountDownLatch latch) {		
		this.cellMapViewer.drawCells(cells, latch);
	}

	@Override
	public void setGenerationInfo(final long generation, final long elapsedTime, final long cellsAlive) {
		this.menuPanel.setGenerationInfo(generation, elapsedTime, cellsAlive);
	}

	@Override
	public void setStarted() {
		this.cellMapViewer.calculateMapLimits();
		this.menuPanel.setStarted();
		this.menuPanel.setLimits(this.cellMapViewer.getXLimit(), this.cellMapViewer.getYLimit());
	}

	@Override
	public void setStopped() {
		this.menuPanel.setStopped();
	}

	@Override
	public void reset() {
		this.menuPanel.reset();
		this.cellMapViewer.reset();	
	}

	@Override
	public Optional<Dimension> getMapDimension() {
		return this.menuPanel.getMapDimension();
	}
	
	@Override
	public void setProgress(final ProgressType progressType, final String title) {
		this.menuPanel.setProgress(progressType, title);
	}

	@Override
	public void updateProgress(final double value) {
		this.menuPanel.updateProgress(value);
	}
	
	/**
	 * Get the menu panel of the main frame
	 * @return
	 * 		the menu panel of the main frame
	 */
	public MenuPanel getMenuPanel() {
		return this.menuPanel;
	}

	@Override
	public void showErrorAlert(final String header, final String message, final String exception) {
		Platform.runLater(() -> {
			MessageViewer.showExceptionAndExit(header, message, exception);
		});
	}

	@Override
	public void showAlert(String header, String message) {
		Platform.runLater(() -> {
			MessageViewer.showMessage(AlertType.INFORMATION, "Warning", header, message, "", null);
		});
	}

	@Override
	public int getMinRefreshTime() {
		return this.menuPanel.getMinRefreshTime();
	}
	
}
