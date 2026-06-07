package gui;

import domain.Card;
import domain.CardType;
import domain.GamePhase;
import domain.PlayerColor;
import domain.RiskGame;
import domain.TerritoryName;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

/**
 * FXML controller for the interactive Risk board (scramble and setup phases).
 */
public final class GameBoardController {

    private static final Map<String, TerritoryName> SVG_ID_TO_TERRITORY = buildIdMap();
    private static final Map<PlayerColor, String> PLAYER_COLORS = buildColorMap();

    @FXML
    private WebView mapView;

    @FXML
    private Label phaseLabel;

    @FXML
    private Label playerLabel;

    @FXML
    private Label armiesLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Spinner<Integer> captureArmiesSpinner;

    @FXML
    private Button moveAfterCaptureButton;

    @FXML
    private Spinner<Integer> fortifyArmiesSpinner;

    @FXML
    private Button endAttackButton;

    @FXML
    private Button fortifyButton;

    @FXML
    private Button endTurnButton;

    @FXML
    private ListView<String> cardListView;

    @FXML
    private Button tradeCardsButton;

    @FXML
    private Button newGameButton;

    private RiskGame game;
    private WebEngine engine;
    private JavaBridge javaBridge;
    private TerritoryName selectedAttackFrom;
    private TerritoryName selectedFortifyFrom;
    private TerritoryName selectedFortifyTo;
    private final List<Card> visibleCards = new ArrayList<>();
    private String actionStatusMessage;

    @FXML
    private void initialize() {
        captureArmiesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1));
        fortifyArmiesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1));
        cardListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        cardListView.getSelectionModel().getSelectedIndices().addListener(
                (ListChangeListener<Integer>) change -> {
                    if (game != null) {
                        updateActionControls(game.getPhase());
                    }
                });
        updateActionControls(GamePhase.SCRAMBLE);
    }

    void initGame(RiskGame game) {
        this.game = game;
        this.engine = mapView.getEngine();
        loadMap();
    }

    private void loadMap() {
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) engine.executeScript("window");
                javaBridge = new JavaBridge(this);
                window.setMember("javaBridge", javaBridge);
                applyMapStyling();
                updateMapColors();
                updateStatusBar();
            }
        });
        engine.loadContent(buildMapHtml());
    }

    private void applyMapStyling() {
        String js = "var countryLayer = document.getElementById('layer4');"
                + "if (countryLayer) { countryLayer.style.opacity = '1'; }"
                + "var paths = document.querySelectorAll('#layer4 path[id]');"
                + "paths.forEach(function(p) {"
                + "  var id = p.id;"
                + "  p.style.cursor = 'pointer';"
                + "  p.style.fill = '#c8d8a8';"
                + "  p.style.fillOpacity = '1';"
                + "  p.style.stroke = '#555';"
                + "  p.style.strokeWidth = '1.5';"
                + "  p.style.transition = 'fill 0.15s ease';"
                + "  p.addEventListener('mouseenter', function() {"
                + "    if (!this.dataset.owner) { this.style.fill = '#e8f4c8'; }"
                + "    else { this.style.opacity = '0.8'; }"
                + "  });"
                + "  p.addEventListener('mouseleave', function() {"
                + "    this.style.opacity = '1';"
                + "    if (!this.dataset.owner) { this.style.fill = '#c8d8a8'; }"
                + "  });"
                + "  p.addEventListener('click', function() {"
                + "    window.javaBridge.onTerritoryClicked(this.id);"
                + "  });"
                + "});";
        engine.executeScript(js);
    }

    public static final class JavaBridge {
        private final WeakReference<GameBoardController> controller;

        JavaBridge(GameBoardController controller) {
            this.controller = new WeakReference<>(controller);
        }

        public void onTerritoryClicked(String svgId) {
            GameBoardController currentController = controller.get();
            if (currentController != null) {
                Platform.runLater(() -> currentController.handleTerritoryClick(svgId));
            }
        }

    }

    private void handleTerritoryClick(String svgId) {
        TerritoryName territory = SVG_ID_TO_TERRITORY.get(svgId);
        if (territory == null) {
            return;
        }

        GamePhase phase = game.getPhase();
        if (phase == GamePhase.GAME_OVER) {
            return;
        }
        try {
            actionStatusMessage = null;
            if (phase == GamePhase.SCRAMBLE) {
                game.claimTerritory(territory);
            } else if (phase == GamePhase.SETUP) {
                game.placeArmy(territory);
            } else if (phase == GamePhase.ATTACK && game.isCaptureMovementPending()) {
                actionStatusMessage = LocaleManager.getBundle()
                        .getString("status.captureMoveArmies");
            } else if (phase == GamePhase.ATTACK && mustTradeBeforeDraft()) {
                actionStatusMessage = LocaleManager.getBundle()
                        .getString("status.tradeCardsRequired");
            } else if (phase == GamePhase.ATTACK && !game.isDraftComplete()) {
                game.draftArmy(territory);
            } else if (phase == GamePhase.ATTACK) {
                handleAttackClick(territory);
            } else if (phase == GamePhase.FORTIFY) {
                handleFortifyClick(territory);
            }
            updateMapColors();
            updateCardHand();
            updateStatusBar();
            updateGameOverOverlay();
        } catch (IllegalStateException | IllegalArgumentException e) {
            statusLabel.setText(
                    LocaleManager.getBundle().getString("status.invalid.action"));
        }
    }

    private void handleAttackClick(TerritoryName territory) {
        ResourceBundle bundle = LocaleManager.getBundle();
        PlayerColor current = game.getCurrentPlayerColor();
        if (game.isOwnedBy(territory, current)) {
            selectedAttackFrom = territory;
            statusLabel.setText(MessageFormat.format(
                    bundle.getString("status.selectedToAttackFrom"),
                    formatName(territory.name())));
            return;
        }
        if (selectedAttackFrom == null) {
            statusLabel.setText(bundle.getString("status.selectOwnedFirst"));
            return;
        }
        TerritoryName from = selectedAttackFrom;
        final int fromBefore = game.getArmies(from);
        final int toBefore = game.getArmies(territory);
        PlayerColor eliminated = null;
        PlayerColor defender = getOwner(territory);
        int defenderTerritoriesBefore = defender == null ? 0 : game.getTerritoryCount(defender);
        game.attack(from, territory);
        int fromAfter = game.getArmies(from);
        int toAfter = game.getArmies(territory);
        boolean captured = game.isOwnedBy(territory, current);
        if (defender != null
                && defender != current
                && defenderTerritoriesBefore > 0
                && game.getTerritoryCount(defender) == 0) {
            eliminated = defender;
        }
        selectedAttackFrom = null;
        if (captured) {
            actionStatusMessage = MessageFormat.format(
                    bundle.getString("status.capturedFrom"),
                    formatName(territory.name()),
                    formatName(from.name()),
                    game.getMinimumCaptureMove(),
                    game.getMaximumCaptureMove());
            if (eliminated != null) {
                actionStatusMessage += " " + MessageFormat.format(
                        bundle.getString("status.eliminated"),
                        bundle.getString(PlayerColors.colorKey(eliminated)));
            }
        } else {
            actionStatusMessage = MessageFormat.format(
                    bundle.getString("status.attackResolved"),
                    formatName(from.name()), fromBefore, fromAfter,
                    formatName(territory.name()), toBefore, toAfter);
        }
    }

    private void handleFortifyClick(TerritoryName territory) {
        ResourceBundle bundle = LocaleManager.getBundle();
        PlayerColor current = game.getCurrentPlayerColor();
        if (!game.isOwnedBy(territory, current)) {
            statusLabel.setText(bundle.getString("status.selectOwnedTerritories"));
            return;
        }
        if (selectedFortifyFrom == null || selectedFortifyTo != null) {
            selectedFortifyFrom = territory;
            selectedFortifyTo = null;
            updateFortifySpinner();
            statusLabel.setText(MessageFormat.format(
                    bundle.getString("status.selectedAsMoveSource"),
                    formatName(territory.name())));
            return;
        }
        if (territory == selectedFortifyFrom) {
            actionStatusMessage = bundle.getString("status.selectDifferentFortifyTarget");
            return;
        }
        selectedFortifyTo = territory;
        updateFortifySpinner();
        statusLabel.setText(MessageFormat.format(
                bundle.getString("status.selectedAsFortifyDest"),
                formatName(territory.name())));
    }

    @FXML
    private void handleEndAttack() {
        ResourceBundle bundle = LocaleManager.getBundle();
        try {
            if (game.isCaptureMovementPending()) {
                statusLabel.setText(bundle.getString("status.captureFirst"));
                return;
            }
            clearAttackSelection();
            game.endAttack();
            updateMapColors();
            updateCardHand();
            updateStatusBar();
        } catch (IllegalStateException e) {
            statusLabel.setText(bundle.getString("status.invalid.endAttack"));
        }
    }

    @FXML
    private void handleMoveAfterCapture() {
        ResourceBundle bundle = LocaleManager.getBundle();
        try {
            if (!game.isCaptureMovementPending()) {
                statusLabel.setText(bundle.getString("status.noCapturedNeedsArmies"));
                return;
            }
            TerritoryName from = game.getPendingCaptureFrom();
            TerritoryName to = game.getPendingCaptureTo();
            int armies = captureArmiesSpinner.getValue();
            game.moveArmiesAfterCapture(from, to, armies);
            actionStatusMessage = MessageFormat.format(
                    bundle.getString("status.movedArmiesBetween"),
                    armies, formatName(from.name()), formatName(to.name()));
            updateMapColors();
            updateCardHand();
            updateStatusBar();
        } catch (IllegalStateException | IllegalArgumentException e) {
            statusLabel.setText(bundle.getString("status.invalid.capture"));
        }
    }

    @FXML
    private void handleFortify() {
        ResourceBundle bundle = LocaleManager.getBundle();
        if (selectedFortifyFrom == null || selectedFortifyTo == null) {
            statusLabel.setText(bundle.getString("status.selectMoveBeforeFortify"));
            return;
        }
        try {
            int armies = fortifyArmiesSpinner.getValue();
            TerritoryName from = selectedFortifyFrom;
            TerritoryName to = selectedFortifyTo;
            game.fortify(selectedFortifyFrom, selectedFortifyTo, armies);
            clearFortifySelection();
            actionStatusMessage = MessageFormat.format(
                    bundle.getString("status.fortifiedBetween"),
                    armies, formatName(from.name()), formatName(to.name()));
            updateMapColors();
            updateCardHand();
            updateStatusBar();
        } catch (IllegalStateException | IllegalArgumentException e) {
            statusLabel.setText(bundle.getString("status.invalid.fortify"));
        }
    }

    @FXML
    private void handleTradeCards() {
        ResourceBundle bundle = LocaleManager.getBundle();
        List<Card> selectedCards = getSelectedCards();
        try {
            int beforeDraftArmies = game.getDraftArmies();
            List<String> territoryBonuses = new ArrayList<>();
            for (Card card : selectedCards) {
                if (!card.isWild()
                        && game.isOwnedBy(card.getTerritory(), game.getCurrentPlayerColor())) {
                    territoryBonuses.add(formatName(card.getTerritory().name()));
                }
            }
            game.tradeCards(selectedCards);
            int gainedArmies = game.getDraftArmies() - beforeDraftArmies;
            cardListView.getSelectionModel().clearSelection();
            actionStatusMessage = MessageFormat.format(
                    bundle.getString("status.tradedForArmies"), gainedArmies);
            if (!territoryBonuses.isEmpty()) {
                actionStatusMessage += " " + MessageFormat.format(
                        bundle.getString("status.tradedTerritoryBonus"),
                        String.join(", ", territoryBonuses));
            }
            updateMapColors();
            updateCardHand();
            updateStatusBar();
        } catch (IllegalStateException | IllegalArgumentException e) {
            statusLabel.setText(bundle.getString("status.invalid.trade"));
        }
    }

    @FXML
    private void handleEndTurn() {
        try {
            clearAttackSelection();
            clearFortifySelection();
            game.endTurn();
            updateMapColors();
            updateCardHand();
            updateStatusBar();
        } catch (IllegalStateException e) {
            statusLabel.setText(
                    LocaleManager.getBundle().getString("status.invalid.endTurn"));
        }
    }

    @FXML
    private void handleNewGame() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/game-setup-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) phaseLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(LocaleManager.getBundle().getString("window.title.setup"));
            stage.setWidth(520);
            stage.setHeight(420);
            stage.setMinWidth(480);
            stage.setMinHeight(360);
        } catch (Exception e) {
            statusLabel.setText(LocaleManager.getBundle().getString("setup.error.reload"));
        }
    }

    private void updateMapColors() {
        for (Map.Entry<String, TerritoryName> entry : SVG_ID_TO_TERRITORY.entrySet()) {
            String svgId = entry.getKey();
            TerritoryName territory = entry.getValue();

            String fillColor = "#c8d8a8";
            String ownerData = "";

            for (PlayerColor color : PlayerColor.values()) {
                if (game.isOwnedBy(territory, color)) {
                    fillColor = PLAYER_COLORS.get(color);
                    ownerData = color.name();
                    break;
                }
            }

            int armies = game.getArmies(territory);
            boolean selected = territory == selectedAttackFrom
                    || territory == selectedFortifyFrom
                    || territory == selectedFortifyTo;
            String strokeColor = selected ? "#f8e16c" : "#555";
            String strokeWidth = selected ? "3" : "1.5";
            String armyDisplay = armies > 0 ? "block" : "none";
            String script = "(function() {"
                    + "  var el = document.getElementById('" + svgId + "');"
                    + "  if (el) {"
                    + "    el.style.fill = '" + fillColor + "';"
                    + "    el.style.stroke = '" + strokeColor + "';"
                    + "    el.style.strokeWidth = '" + strokeWidth + "';"
                    + "    el.dataset.owner = '" + ownerData + "';"
                    + "    el.dataset.armies = '" + armies + "';"
                    + "    el.title = '" + escapeJs(formatName(territory.name()))
                    + " (" + armies + ")';"
                    + "    var labelId = 'army-label-" + svgId + "';"
                    + "    var label = document.getElementById(labelId);"
                    + "    if (!label) {"
                    + "      label = document.createElementNS('http://www.w3.org/2000/svg', 'text');"
                    + "      label.id = labelId;"
                    + "      label.style.pointerEvents = 'none';"
                    + "      label.style.fontSize = '12px';"
                    + "      label.style.fontWeight = '700';"
                    + "      label.style.fill = '#111827';"
                    + "      label.style.stroke = '#f8fafc';"
                    + "      label.style.strokeWidth = '0.8px';"
                    + "      label.style.paintOrder = 'stroke';"
                    + "      label.setAttribute('text-anchor', 'middle');"
                    + "      label.setAttribute('dominant-baseline', 'central');"
                    + "      el.parentNode.appendChild(label);"
                    + "    }"
                    + "    var box = el.getBBox();"
                    + "    label.setAttribute('x', box.x + box.width / 2);"
                    + "    label.setAttribute('y', box.y + box.height / 2);"
                    + "    label.textContent = '" + armies + "';"
                    + "    label.style.display = '" + armyDisplay + "';"
                    + "  }"
                    + "})();";
            engine.executeScript(script);
        }
    }

    private void updateStatusBar() {
        GamePhase phase = game.getPhase();
        syncSelectionsWithPhase(phase);
        updateCardHand();
        ResourceBundle bundle = LocaleManager.getBundle();
        phaseLabel.setText(bundle.getString("phase." + phase.name()));
        playerLabel.setText(MessageFormat.format(
                bundle.getString("board.playerNameWithColor"),
                game.getCurrentPlayerName(),
                bundle.getString(PlayerColors.colorKey(game.getCurrentPlayerColor()))));

        if (phase == GamePhase.SCRAMBLE) {
            armiesLabel.setText(MessageFormat.format(
                    bundle.getString("board.armiesLabel"), game.getArmiesToPlace()));
            statusLabel.setText(bundle.getString("status.clickUnclaimed"));
        } else if (phase == GamePhase.SETUP) {
            armiesLabel.setText(MessageFormat.format(
                    bundle.getString("board.armiesLabel"), game.getArmiesToPlace()));
            statusLabel.setText(bundle.getString("status.clickToPlaceArmy"));
        } else if (phase == GamePhase.ATTACK) {
            armiesLabel.setText(MessageFormat.format(
                    bundle.getString("board.draftLabel"), game.getDraftArmies()));
            if (game.isCaptureMovementPending()) {
                statusLabel.setText(MessageFormat.format(
                        bundle.getString("status.moveCaptureRange"),
                        game.getMinimumCaptureMove(),
                        game.getMaximumCaptureMove(),
                        formatName(game.getPendingCaptureTo().name())));
            } else if (game.isDraftComplete()) {
                if (selectedAttackFrom == null) {
                    statusLabel.setText(bundle.getString("status.selectStartTerritory"));
                } else {
                    statusLabel.setText(bundle.getString("status.selectEnemyOrChangeStart"));
                }
            } else if (mustTradeBeforeDraft()) {
                statusLabel.setText(bundle.getString("status.tradeCardsRequired"));
            } else {
                statusLabel.setText(bundle.getString("status.clickToDraft"));
            }
        } else if (phase == GamePhase.FORTIFY) {
            armiesLabel.setText("");
            if (selectedFortifyFrom == null) {
                statusLabel.setText(bundle.getString("status.selectFortifyFrom"));
            } else if (selectedFortifyTo == null) {
                statusLabel.setText(bundle.getString("status.selectFortifyTo"));
            } else {
                statusLabel.setText(bundle.getString("status.chooseCountThenFortifyOrEnd"));
            }
        } else if (phase == GamePhase.GAME_OVER) {
            armiesLabel.setText("");
            PlayerColor winner = game.getWinner();
            String winnerText = winner == null
                    ? ""
                    : MessageFormat.format(
                            bundle.getString("status.winner"),
                            bundle.getString(PlayerColors.colorKey(winner)));
            statusLabel.setText(MessageFormat.format(
                    bundle.getString("status.gameOver"), winnerText));
        }

        if (actionStatusMessage != null && phase != GamePhase.GAME_OVER) {
            statusLabel.setText(actionStatusMessage);
        }
        updateActionControls(phase);
        String playerColor = PLAYER_COLORS.get(game.getCurrentPlayerColor());
        playerLabel.setStyle("-fx-text-fill: " + playerColor + "; -fx-font-weight: bold;");
        updateGameOverOverlay();
    }

    private void updateGameOverOverlay() {
        if (engine == null || game == null) {
            return;
        }
        if (game.getPhase() != GamePhase.GAME_OVER || game.getWinner() == null) {
            engine.executeScript("var overlay = document.getElementById('game-over-overlay');"
                    + "if (overlay) { overlay.remove(); }");
            return;
        }
        PlayerColor winner = game.getWinner();
        String winnerName = game.getPlayerName(winner);
        String winnerColor = PLAYER_COLORS.get(winner);
        String text = escapeJs(MessageFormat.format(
                LocaleManager.getBundle().getString("overlay.winner"),
                winnerName.toUpperCase()));
        String script = "var overlay = document.getElementById('game-over-overlay');"
                + "if (!overlay) {"
                + "  overlay = document.createElement('div');"
                + "  overlay.id = 'game-over-overlay';"
                + "  document.body.appendChild(overlay);"
                + "}"
                + "overlay.textContent = '" + text + "';"
                + "overlay.style.position = 'fixed';"
                + "overlay.style.inset = '0';"
                + "overlay.style.display = 'flex';"
                + "overlay.style.alignItems = 'center';"
                + "overlay.style.justifyContent = 'center';"
                + "overlay.style.zIndex = '9999';"
                + "overlay.style.pointerEvents = 'none';"
                + "overlay.style.background = 'rgba(15, 25, 35, 0.54)';"
                + "overlay.style.color = '" + winnerColor + "';"
                + "overlay.style.fontFamily = 'Arial, sans-serif';"
                + "overlay.style.fontSize = '64px';"
                + "overlay.style.fontWeight = '900';"
                + "overlay.style.letterSpacing = '2px';"
                + "overlay.style.textShadow = '0 4px 18px #000, 0 0 8px #fff';";
        engine.executeScript(script);
    }

    private void updateActionControls(GamePhase phase) {
        boolean gameOver = phase == GamePhase.GAME_OVER;
        boolean capturePending = game != null && game.isCaptureMovementPending();
        final boolean attackPhase = phase == GamePhase.ATTACK
                && game != null
                && game.isDraftComplete();
        captureArmiesSpinner.setDisable(gameOver || !capturePending);
        moveAfterCaptureButton.setDisable(gameOver || !capturePending);
        if (capturePending) {
            captureArmiesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    game.getMinimumCaptureMove(),
                    game.getMaximumCaptureMove(),
                    game.getMaximumCaptureMove()));
        }
        endAttackButton.setDisable(gameOver || !attackPhase || capturePending);

        boolean fortifyPhase = phase == GamePhase.FORTIFY;
        fortifyArmiesSpinner.setDisable(gameOver || !fortifyPhase);
        fortifyButton.setDisable(gameOver || !fortifyPhase
                || selectedFortifyFrom == null
                || selectedFortifyTo == null);
        endTurnButton.setDisable(gameOver || !fortifyPhase);

        boolean tradeReady = game != null
                && phase == GamePhase.ATTACK
                && game.canTradeCards(getSelectedCards());
        tradeCardsButton.setDisable(gameOver || !tradeReady);
        cardListView.setDisable(gameOver);
        newGameButton.setDisable(!gameOver);
    }

    private void updateFortifySpinner() {
        if (game == null || selectedFortifyFrom == null) {
            fortifyArmiesSpinner.setValueFactory(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1));
            return;
        }
        int maxArmies = Math.max(1, game.getArmies(selectedFortifyFrom) - 1);
        fortifyArmiesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxArmies, maxArmies));
    }

    private void updateCardHand() {
        visibleCards.clear();
        cardListView.getItems().clear();
        if (game == null) {
            return;
        }
        visibleCards.addAll(game.getCards(game.getCurrentPlayerColor()));
        for (Card card : visibleCards) {
            cardListView.getItems().add(formatCard(card));
        }
    }

    private List<Card> getSelectedCards() {
        List<Card> selectedCards = new ArrayList<>();
        for (Integer index : cardListView.getSelectionModel().getSelectedIndices()) {
            if (index >= 0 && index < visibleCards.size()) {
                selectedCards.add(visibleCards.get(index));
            }
        }
        return selectedCards;
    }

    private boolean mustTradeBeforeDraft() {
        return game != null && game.getCards(game.getCurrentPlayerColor()).size() >= 5;
    }

    private PlayerColor getOwner(TerritoryName territory) {
        for (PlayerColor color : PlayerColor.values()) {
            if (game.isOwnedBy(territory, color)) {
                return color;
            }
        }
        return null;
    }

    private String formatCard(Card card) {
        ResourceBundle bundle = LocaleManager.getBundle();
        String typeName = bundle.getString("card." + card.getType().name());
        if (card.getType() == CardType.WILD) {
            return typeName;
        }
        return MessageFormat.format(
                bundle.getString("card.format"),
                typeName,
                formatName(card.getTerritory().name()));
    }

    /**
     * Returns the localized display name for a {@link TerritoryName} enum
     * value, given its {@code .name()} string (e.g., {@code "NORTH_AFRICA"}).
     * Falls back to a title-cased version of the enum name if the bundle
     * does not define a translation, so adding a territory to the enum
     * before its bundle key is added does not crash the UI.
     */
    private String formatName(String name) {
        try {
            return LocaleManager.getBundle().getString("territory." + name);
        } catch (java.util.MissingResourceException missing) {
            return titleCase(name);
        }
    }

    private String titleCase(String name) {
        StringBuilder formatted = new StringBuilder();
        for (String part : name.split("_")) {
            if (formatted.length() > 0) {
                formatted.append(" ");
            }
            formatted.append(part.charAt(0));
            formatted.append(part.substring(1).toLowerCase());
        }
        return formatted.toString();
    }

    private String escapeJs(String value) {
        return value.replace("\\", "\\\\").replace("'", "\\'");
    }

    private void syncSelectionsWithPhase(GamePhase phase) {
        if (phase != GamePhase.ATTACK || !game.isDraftComplete()) {
            clearAttackSelection();
        }
        if (phase != GamePhase.FORTIFY) {
            clearFortifySelection();
        }
    }

    private void clearAttackSelection() {
        selectedAttackFrom = null;
    }

    private void clearFortifySelection() {
        selectedFortifyFrom = null;
        selectedFortifyTo = null;
    }

    private String buildMapHtml() {
        try {
            URL svgUrl = getClass().getResource("/Risk_board.svg");
            if (svgUrl == null) {
                return "<html><body>"
                        + LocaleManager.getBundle().getString("map.error.missing")
                        + "</body></html>";
            }
            Path svgPath = Paths.get(svgUrl.toURI());
            String svgContent = Files.readString(svgPath);
            svgContent = svgContent.replaceFirst("<\\?xml[^?]*\\?>", "");
            return "<!DOCTYPE html><html><head><style>"
                    + "* { margin: 0; padding: 0; box-sizing: border-box; }"
                    + "html, body { width: 100%; height: 100%;"
                    + " background: #87ceeb; overflow: hidden; }"
                    + "svg { width: 100%; height: 100%; display: block; }"
                    + "</style></head><body>"
                    + svgContent
                    + "</body></html>";
        } catch (Exception e) {
            return "<html><body>"
                    + LocaleManager.getBundle().getString("map.error.load")
                    + "</body></html>";
        }
    }

    private static Map<String, TerritoryName> buildIdMap() {
        Map<String, TerritoryName> map = new HashMap<>();
        map.put("alaska", TerritoryName.ALASKA);
        map.put("northwest_territory", TerritoryName.NORTHWEST_TERRITORY);
        map.put("greenland", TerritoryName.GREENLAND);
        map.put("alberta", TerritoryName.ALBERTA);
        map.put("ontario", TerritoryName.ONTARIO);
        map.put("quebec", TerritoryName.QUEBEC);
        map.put("western_united_states", TerritoryName.WESTERN_UNITED_STATES);
        map.put("eastern_united_states", TerritoryName.EASTERN_UNITED_STATES);
        map.put("central_america", TerritoryName.CENTRAL_AMERICA);
        map.put("venezuela", TerritoryName.VENEZUELA);
        map.put("peru", TerritoryName.PERU);
        map.put("brazil", TerritoryName.BRAZIL);
        map.put("argentina", TerritoryName.ARGENTINA);
        map.put("iceland", TerritoryName.ICELAND);
        map.put("great_britain", TerritoryName.GREAT_BRITAIN);
        map.put("western_europe", TerritoryName.WESTERN_EUROPE);
        map.put("northern_europe", TerritoryName.NORTHERN_EUROPE);
        map.put("southern_europe", TerritoryName.SOUTHERN_EUROPE);
        map.put("scandinavia", TerritoryName.SCANDINAVIA);
        map.put("ukraine", TerritoryName.UKRAINE);
        map.put("north_africa", TerritoryName.NORTH_AFRICA);
        map.put("egypt", TerritoryName.EGYPT);
        map.put("east_africa", TerritoryName.EAST_AFRICA);
        map.put("congo", TerritoryName.CONGO);
        map.put("south_africa", TerritoryName.SOUTH_AFRICA);
        map.put("madagascar", TerritoryName.MADAGASCAR);
        map.put("middle_east", TerritoryName.MIDDLE_EAST);
        map.put("afghanistan", TerritoryName.AFGHANISTAN);
        map.put("ural", TerritoryName.URAL);
        map.put("siberia", TerritoryName.SIBERIA);
        map.put("yakursk", TerritoryName.YAKUTSK);
        map.put("kamchatka", TerritoryName.KAMCHATKA);
        map.put("irkutsk", TerritoryName.IRKUTSK);
        map.put("mongolia", TerritoryName.MONGOLIA);
        map.put("japan", TerritoryName.JAPAN);
        map.put("china", TerritoryName.CHINA);
        map.put("india", TerritoryName.INDIA);
        map.put("siam", TerritoryName.SIAM);
        map.put("eastern_australia", TerritoryName.EASTERN_AUSTRALIA);
        map.put("western_australia", TerritoryName.WESTERN_AUSTRALIA);
        map.put("new_guinea", TerritoryName.NEW_GUINEA);
        map.put("indonesia", TerritoryName.INDONESIA);
        return Collections.unmodifiableMap(map);
    }

    private static Map<PlayerColor, String> buildColorMap() {
        Map<PlayerColor, String> map = new EnumMap<>(PlayerColor.class);
        map.put(PlayerColor.RED, "#e05555");
        map.put(PlayerColor.BLUE, "#5588dd");
        map.put(PlayerColor.GREEN, "#44aa66");
        map.put(PlayerColor.ORANGE, "#ee8833");
        map.put(PlayerColor.PINK, "#dd66aa");
        map.put(PlayerColor.CYAN, "#44bbcc");
        return Collections.unmodifiableMap(map);
    }
}
