package gui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Launches the Risk game setup window (JavaFX + FXML).
 */
public final class RiskApplication extends Application {

    static final int BOARD_WIDTH = 1100;
    static final int BOARD_HEIGHT = 750;
    static final int BOARD_MIN_WIDTH = 900;
    static final int BOARD_MIN_HEIGHT = 600;

    @Override
    public void start(Stage stage) throws Exception {
        GuiSceneNavigation.showSetupScreen(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
