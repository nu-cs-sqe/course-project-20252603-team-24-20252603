package nu.csse.sqe.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Launches the Risk game setup window (JavaFX + FXML).
 */
public final class RiskApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                RiskApplication.class.getResource("game-setup-view.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Risk — Game setup");
        stage.setMinWidth(400);
        stage.setMinHeight(320);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
