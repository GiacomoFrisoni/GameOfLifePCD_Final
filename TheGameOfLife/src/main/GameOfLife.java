package main;

import controller.GameController;
import controller.GameControllerImpl;
import javafx.application.Application;
import javafx.stage.Stage;
import view.GameOfLifeFrame;
import view.MainFrame;


/**
 * This is the launcher class for GameOfLife with MVC implementation.
 */
public final class GameOfLife extends Application {
	
	@Override
	public void start(Stage primaryStage) {		
		final GameOfLifeFrame view = new MainFrame(primaryStage);
		final GameController controller = new GameControllerImpl(view);
		view.setObserver(controller);
		view.showView();
	}
	
	public static void main(final String[] args) {
		launch(args);	
	}
	
}
