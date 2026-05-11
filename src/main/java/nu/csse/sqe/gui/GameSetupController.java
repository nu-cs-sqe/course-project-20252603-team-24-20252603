package nu.csse.sqe.gui;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import nu.csse.sqe.game.PlayerColor;

/**
 * FXML controller for the game setup UI (stub only until setup is wired to the domain).
 */
public final class GameSetupController {

    private static final int MIN_PLAYERS = 3;
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
    private void handleStartGame() {}

    @FXML
    private void handleQuit() {
        Platform.exit();
    }

    private void rebuildNameFields(int count) {
        nameFieldsContainer.getChildren().clear();
        nameFields.clear();

        PlayerColor[] colors = PlayerColor.values();

        for (int i = 1; i <= count; i++) {
            PlayerColor color = colors[i - 1];

            Region swatch = new Region();
            swatch.setMinSize(20, 20);
            swatch.setPrefSize(20, 20);
            swatch.setMaxSize(20, 20);
            String hex = toCssHex(fxColor(color));
            swatch.setStyle(
                    "-fx-background-color: " + hex + "; "
                            + "-fx-border-color: #333333; -fx-border-width: 1px;");

            Label colorLabel = new Label(displayName(color));
            colorLabel.setPrefWidth(64);

            Label slotLabel = new Label("Player " + i);
            slotLabel.setPrefWidth(64);

            TextField field = new TextField("Player " + i);
            HBox.setHgrow(field, Priority.ALWAYS);
            nameFields.add(field);

            HBox row = new HBox(8, swatch, colorLabel, slotLabel, field);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPrefWidth(Double.MAX_VALUE);
            nameFieldsContainer.getChildren().add(row);
        }
    }

    private static Color fxColor(PlayerColor color) {
        switch (color) {
            case RED:
                return Color.web("#c62828");
            case BLUE:
                return Color.web("#1565c0");
            case GREEN:
                return Color.web("#2e7d32");
            case ORANGE:
                return Color.web("#ef6c00");
            case PINK:
                return Color.web("#ad1457");
            case CYAN:
                return Color.web("#00838f");
            default:
                throw new IllegalArgumentException(color.toString());
        }
    }

    private static String displayName(PlayerColor color) {
        switch (color) {
            case RED:
                return "Red";
            case BLUE:
                return "Blue";
            case GREEN:
                return "Green";
            case ORANGE:
                return "Orange";
            case PINK:
                return "Pink";
            case CYAN:
                return "Cyan";
            default:
                throw new IllegalArgumentException(color.toString());
        }
    }

    private static String toCssHex(Color color) {
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);
        return String.format("#%02x%02x%02x", r, g, b);
    }
}
