package view;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import controller.GameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainFrame2 extends BorderPane implements GameOfLifeFrame {

	private Stage stage;
	private GameController gc;
	 
	public MainFrame2(final Stage stage) {
		Objects.requireNonNull(stage);
        this.stage = stage;

	}
	
	@Override
	public void setObserver(GameController observer) {
		gc = observer;
	}
	

	@Override
	public void showView() {		
		final Scene scene = new Scene(this);
		
		this.stage.setOnCloseRequest(e -> {
			this.closeView();
	        System.exit(0);
		});

		this.stage.setTitle("fcoians");
		this.stage.setScene(scene);
		this.stage.show();	
		this.stage.setResizable(false);
		gc.start();
	}

	@Override
	public void closeView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawCells(boolean[][] cells, CountDownLatch latch) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGenerationInfo(long generation, long elapsedTime, long cellsAlive) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStarted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStopped() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProgress(ProgressType progressType, String title) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateProgress(double value) {
		// TODO Auto-generated method stub

	}

	@Override
	public Optional<Dimension> getMapDimension() {
		return Optional.of(new Dimension(2500, 2500));
	}

	@Override
	public int getMinRefreshTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void showErrorAlert(String header, String message, String exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showAlert(String header, String message) {
		// TODO Auto-generated method stub

	}

}
