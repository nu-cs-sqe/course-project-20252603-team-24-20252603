package gui;

import domain.GameConstants;
import domain.PlayerColor;
import domain.RiskGame;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * FXML controller for the game setup UI. Builds the {@code Map<PlayerColor, String>}
 * payload expected by {@code RiskGame} when the user starts a game.
 */
public final class GameSetupController {

    private static final int DEFAULT_PLAYERS = 3;

    /** Last successfully validated start payload; {@code null} until a valid start. */
    private Map<PlayerColor, String> validatedPlayerInfo;

    @FXML
    private Label headingLabel;

    @FXML
    private Spinner<Integer> playerCountSpinner;

    @FXML
    private VBox nameFieldsContainer;

    @FXML
    private ComboBox<Locale> localeComboBox;

    private final List<TextField> nameFields = new ArrayList<>();

    @FXML
    private void initialize() {
        ResourceBundle bundle = LocaleManager.getBundle();
        headingLabel.setText(bundle.getString("setup.heading"));
        headingLabel.setFont(Font.font(null, FontWeight.BOLD, 18));

        initializeLocalePicker();

        SpinnerValueFactory.IntegerSpinnerValueFactory factory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        GameConstants.MIN_PLAYERS,
                        GameConstants.MAX_PLAYERS,
                        DEFAULT_PLAYERS);
        playerCountSpinner.setValueFactory(factory);
        playerCountSpinner.setEditable(false);
        playerCountSpinner.valueProperty().addListener((obs, ignored, next) ->
                rebuildNameFields(next.intValue()));

        rebuildNameFields(factory.getValue());
    }

    private void initializeLocalePicker() {
        localeComboBox.setItems(
                FXCollections.observableArrayList(LocaleManager.SUPPORTED_LOCALES));
        localeComboBox.setConverter(new StringConverter<Locale>() {
            @Override
            public String toString(Locale loc) {
                if (loc == null) {
                    return "";
                }
                return displayNameFor(loc);
            }

            @Override
            public Locale fromString(String s) {
                return null;
            }
        });
        localeComboBox.setCellFactory(list -> new ListCell<Locale>() {
            @Override
            protected void updateItem(Locale loc, boolean empty) {
                super.updateItem(loc, empty);
                setText(loc == null || empty ? null : displayNameFor(loc));
            }
        });
        localeComboBox.setValue(LocaleManager.getCurrentLocale());
        localeComboBox.valueProperty().addListener((obs, oldLoc, newLoc) -> {
            if (newLoc != null && !newLoc.equals(oldLoc)) {
                LocaleManager.setCurrentLocale(newLoc);
                reloadSetupScene();
            }
        });
    }

    /**
     * Returns the native display name for {@code loc} (e.g., "English",
     * "Español", "Français") by reading {@code locale.displayName} from the
     * locale's own bundle. No central mapping is required, so adding a new
     * locale needs no Java changes.
     */
    private static String displayNameFor(Locale loc) {
        return LocaleManager.getBundle(loc).getString("locale.displayName");
    }

    private void reloadSetupScene() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/game-setup-view.fxml"),
                    LocaleManager.getBundle());
            Parent root = loader.load();
            Stage stage = (Stage) headingLabel.getScene().getWindow();
            stage.setTitle(LocaleManager.getBundle().getString("window.title.setup"));
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            showError("Failed to reload setup screen: " + e.getMessage());
        }
    }

    /**
     * Returns the player map from the last successful {@link #handleStartGame()}, or {@code null}.
     */
    public Map<PlayerColor, String> getValidatedPlayerInfo() {
        return validatedPlayerInfo;
    }

    @FXML
    private void handleStartGame() {
        ResourceBundle bundle = LocaleManager.getBundle();
        int count = playerCountSpinner.getValue();
        if (nameFields.size() != count) {
            showError(bundle.getString("setup.error.outOfSync"));
            return;
        }

        PlayerColor[] colors = PlayerColor.values();
        Map<PlayerColor, String> map = new LinkedHashMap<>(count);
        Set<String> seenLower = new HashSet<>(count);

        for (int i = 0; i < count; i++) {
            PlayerColor color = colors[i];
            TextField field = nameFields.get(i);
            String raw = field.getText();
            String name = raw == null ? "" : raw.trim();
            if (name.isEmpty()) {
                showError(MessageFormat.format(
                        bundle.getString("setup.error.emptyName"),
                        displayName(color), i + 1));
                return;
            }
            String dedupeKey = name.toLowerCase(Locale.ROOT);
            if (!seenLower.add(dedupeKey)) {
                showError(MessageFormat.format(
                        bundle.getString("setup.error.duplicateName"), name));
                return;
            }
            map.put(color, name);
        }

        validatedPlayerInfo = Collections.unmodifiableMap(map);

        final RiskGame game;
        try {
            game = new RiskGame(validatedPlayerInfo);
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
        ResourceBundle bundle = LocaleManager.getBundle();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/game-board-view.fxml"),
                    bundle);
            Parent root = loader.load();

            GameBoardController controller = loader.getController();
            controller.initGame(game);

            Stage stage = (Stage) headingLabel.getScene().getWindow();
            Scene scene = new Scene(root, 1100, 750);
            stage.setScene(scene);
            stage.setTitle(bundle.getString("window.title.game"));
            stage.setMinWidth(900);
            stage.setMinHeight(600);
        } catch (Exception e) {
            showError(MessageFormat.format(
                    bundle.getString("setup.error.loadBoard"), e.getMessage()));
        }
    }

    private void rebuildNameFields(int count) {
        nameFieldsContainer.getChildren().clear();
        nameFields.clear();

        ResourceBundle bundle = LocaleManager.getBundle();
        String slotPattern = bundle.getString("setup.playerSlot");
        String defaultNamePattern = bundle.getString("setup.defaultName");

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

            Label slotLabel = new Label(MessageFormat.format(slotPattern, i));
            slotLabel.setPrefWidth(64);

            TextField field = new TextField(MessageFormat.format(defaultNamePattern, i));
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
        return LocaleManager.getBundle().getString(colorKey(color));
    }

    private static String colorKey(PlayerColor color) {
        switch (color) {
            case RED:
                return "color.red";
            case BLUE:
                return "color.blue";
            case GREEN:
                return "color.green";
            case ORANGE:
                return "color.orange";
            case PINK:
                return "color.pink";
            case CYAN:
                return "color.cyan";
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

    private static void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(LocaleManager.getBundle().getString("setup.errorTitle"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
