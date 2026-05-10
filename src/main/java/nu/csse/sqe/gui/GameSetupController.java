package nu.csse.sqe.gui;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * FXML controller for the game setup UI (stub only until setup is wired to the domain).
 */
public final class GameSetupController {

    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 6;
    private static final int DEFAULT_PLAYERS = 3;

    @FXML
    private Label headingLabel;

    @FXML
    private Spinner<Integer> playerCountSpinner;

    @FXML
    private VBox nameFieldsContainer;

    private final List<TextField> nameFields = new ArrayList<>();

    @FXML
    private void initialize() {
        headingLabel.setText("New game");
        headingLabel.setFont(Font.font(null, FontWeight.BOLD, 18));

        SpinnerValueFactory.IntegerSpinnerValueFactory factory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        MIN_PLAYERS, MAX_PLAYERS, DEFAULT_PLAYERS);
        playerCountSpinner.setValueFactory(factory);
        playerCountSpinner.setEditable(false);
        playerCountSpinner.valueProperty().addListener((obs, ignored, next) ->
                rebuildNameFields(next.intValue()));

        rebuildNameFields(factory.getValue());
    }

    @FXML
    private void handleStartGame() {
    }

    @FXML
    private void handleQuit() {
        Platform.exit();
    }

    private void rebuildNameFields(int count) {
        nameFieldsContainer.getChildren().clear();
        nameFields.clear();

        for (int i = 1; i <= count; i++) {
            Label label = new Label("Player " + i);
            label.setPrefWidth(72);

            TextField field = new TextField("Player " + i);
            HBox.setHgrow(field, Priority.ALWAYS);
            nameFields.add(field);

            HBox row = new HBox(8, label, field);
            row.setPrefWidth(Double.MAX_VALUE);
            nameFieldsContainer.getChildren().add(row);
        }
    }
}
