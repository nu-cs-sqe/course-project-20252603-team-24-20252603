package nu.csse.sqe.gui;

import domain.PlayerColor;
import domain.RiskGame;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class GameSetupController {

    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 6;
    private static final int DEFAULT_PLAYERS = 3;

    private static final PlayerColor[] COLORS = {
            PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN,
            PlayerColor.ORANGE, PlayerColor.PINK, PlayerColor.CYAN
    };

    @FXML private Label headingLabel;
    @FXML private Spinner<Integer> playerCountSpinner;
    @FXML private VBox nameFieldsContainer;
    @FXML private Label errorLabel;

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
        Map<PlayerColor, String> playerInfo = new LinkedHashMap<>();
        for (int i = 0; i < nameFields.size(); i++) {
            String name = nameFields.get(i).getText().trim();
            if (name.isEmpty()) {
                showError("Player " + (i + 1) + " name cannot be empty.");
                return;
            }
            playerInfo.put(COLORS[i], name);
        }

        RiskGame game;
        try {
            game = new RiskGame(playerInfo);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return;
        }

        switchToGameBoard(game);
    }

    @FXML
    private void handleQuit() {
        Platform.exit();
    }

    private void switchToGameBoard(RiskGame game) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/game-board-view.fxml"));
            Parent root = loader.load();

            GameBoardController controller = loader.getController();
            controller.initGame(game);

            Stage stage = (Stage) headingLabel.getScene().getWindow();
            Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(scene);
            stage.setTitle("Risk");
        } catch (Exception e) {
            showError("Failed to load game board: " + e.getMessage());
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
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