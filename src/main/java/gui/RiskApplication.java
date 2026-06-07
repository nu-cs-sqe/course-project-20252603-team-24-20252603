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

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                RiskApplication.class.getResource("/game-setup-view.fxml"),
                LocaleManager.getBundle());

        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle(LocaleManager.getBundle().getString("window.title.setup"));
        stage.setWidth(520);
        stage.setHeight(420);
        stage.setMinWidth(480);
        stage.setMinHeight(360);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
