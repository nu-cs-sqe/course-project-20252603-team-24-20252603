package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Launches the Risk game setup window (JavaFX + FXML).
 */
public final class RiskApplication extends Application {

    static final int SETUP_WIDTH = 520;
    static final int SETUP_HEIGHT = 420;
    static final int SETUP_MIN_WIDTH = 480;
    static final int SETUP_MIN_HEIGHT = 360;

    static final int BOARD_WIDTH = 1100;
    static final int BOARD_HEIGHT = 750;
    static final int BOARD_MIN_WIDTH = 900;
    static final int BOARD_MIN_HEIGHT = 600;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                RiskApplication.class.getResource("/game-setup-view.fxml"),
                LocaleManager.getBundle());

        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle(LocaleManager.getBundle().getString("window.title.setup"));
        stage.setWidth(SETUP_WIDTH);
        stage.setHeight(SETUP_HEIGHT);
        stage.setMinWidth(SETUP_MIN_WIDTH);
        stage.setMinHeight(SETUP_MIN_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
