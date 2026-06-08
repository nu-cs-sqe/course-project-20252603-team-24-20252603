package gui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Launches the Risk game setup window (JavaFX + FXML).
 */
public final class RiskApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        GuiSceneNavigation.showSetupScreen(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
