package gui;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

final class GuiSceneNavigation {

    private static final String SETUP_FXML = "/game-setup-view.fxml";

    static final int SETUP_WIDTH = 520;
    static final int SETUP_HEIGHT = 420;
    static final int SETUP_MIN_WIDTH = 480;
    static final int SETUP_MIN_HEIGHT = 360;

    private GuiSceneNavigation() {
    }

    static void showSetupScreen(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                GuiSceneNavigation.class.getResource(SETUP_FXML),
                LocaleManager.getBundle());
        Parent root = loader.load();

        Scene scene = stage.getScene();
        if (scene == null) {
            stage.setScene(new Scene(root));
        } else {
            scene.setRoot(root);
        }

        stage.setTitle(LocaleManager.getBundle().getString("window.title.setup"));
        stage.setWidth(SETUP_WIDTH);
        stage.setHeight(SETUP_HEIGHT);
        stage.setMinWidth(SETUP_MIN_WIDTH);
        stage.setMinHeight(SETUP_MIN_HEIGHT);
    }
}
