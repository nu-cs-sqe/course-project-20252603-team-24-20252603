package gui;

import domain.Card;
import domain.CardType;
import domain.GamePhase;
import domain.PlayerColor;
import domain.RiskGame;
import domain.TerritoryName;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
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
        try {
            actionStatusMessage = null;
            if (phase == GamePhase.SCRAMBLE) {
                game.claimTerritory(territory);
            } else if (phase == GamePhase.SETUP) {
                game.placeArmy(territory);
            } else if (phase == GamePhase.ATTACK && game.isCaptureMovementPending()) {
                actionStatusMessage = "Move armies into the captured territory.";
            } else if (phase == GamePhase.ATTACK && mustTradeBeforeDraft()) {
                actionStatusMessage = "Select a valid card set to trade before drafting.";
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
        } catch (IllegalStateException | IllegalArgumentException e) {
            statusLabel.setText("Invalid: " + e.getMessage());
        }
    }

    private void handleAttackClick(TerritoryName territory) {
        PlayerColor current = game.getCurrentPlayerColor();
        if (game.isOwnedBy(territory, current)) {
            selectedAttackFrom = territory;
            statusLabel.setText("Selected " + formatName(territory.name())
                    + " to attack from.");
            return;
        }
        if (selectedAttackFrom == null) {
            statusLabel.setText("Select one of your territories before choosing a target.");
            return;
        }
        TerritoryName from = selectedAttackFrom;
        int fromBefore = game.getArmies(from);
        int toBefore = game.getArmies(territory);
        game.attack(from, territory);
        int fromAfter = game.getArmies(from);
        int toAfter = game.getArmies(territory);
        boolean captured = game.isOwnedBy(territory, current);
        selectedAttackFrom = null;
        if (captured) {
            actionStatusMessage = "Captured " + formatName(territory.name())
                    + " from " + formatName(from.name()) + ". Move "
                    + game.getMinimumCaptureMove() + "-"
                    + game.getMaximumCaptureMove() + " armies.";
        } else {
            actionStatusMessage = "Attack resolved: " + formatName(from.name())
                    + " " + fromBefore + "->" + fromAfter
                    + ", " + formatName(territory.name()) + " "
                    + toBefore + "->" + toAfter + ".";
        }
    }

    private void handleFortifyClick(TerritoryName territory) {
        PlayerColor current = game.getCurrentPlayerColor();
        if (!game.isOwnedBy(territory, current)) {
            statusLabel.setText("Select territories owned by the current player.");
            return;
        }
        if (selectedFortifyFrom == null || selectedFortifyTo != null) {
            selectedFortifyFrom = territory;
            selectedFortifyTo = null;
            statusLabel.setText("Selected " + formatName(territory.name())
                    + " as fortify source.");
            return;
        }
        if (territory == selectedFortifyFrom) {
            actionStatusMessage = "Select a different territory to fortify to.";
            return;
        }
        selectedFortifyTo = territory;
        statusLabel.setText("Selected " + formatName(territory.name())
                + " as fortify destination.");
    }

    @FXML
    private void handleEndAttack() {
        try {
            if (game.isCaptureMovementPending()) {
                statusLabel.setText("Move armies into the captured territory first.");
                return;
            }
            clearAttackSelection();
            game.endAttack();
            updateMapColors();
            updateCardHand();
            updateStatusBar();
        } catch (IllegalStateException e) {
            statusLabel.setText("Invalid: " + e.getMessage());
        }
    }

    @FXML
    private void handleMoveAfterCapture() {
        try {
            if (!game.isCaptureMovementPending()) {
                statusLabel.setText("No captured territory needs armies.");
                return;
            }
            TerritoryName from = game.getPendingCaptureFrom();
            TerritoryName to = game.getPendingCaptureTo();
            int armies = captureArmiesSpinner.getValue();
            game.moveArmiesAfterCapture(from, to, armies);
            actionStatusMessage = "Moved " + armies + " armies from "
                    + formatName(from.name()) + " to " + formatName(to.name()) + ".";
            updateMapColors();
            updateCardHand();
            updateStatusBar();
        } catch (IllegalStateException | IllegalArgumentException e) {
            statusLabel.setText("Invalid: " + e.getMessage());
        }
    }

    @FXML
    private void handleFortify() {
        if (selectedFortifyFrom == null || selectedFortifyTo == null) {
            statusLabel.setText("Select a source and destination before fortifying.");
            return;
        }
        try {
            int armies = fortifyArmiesSpinner.getValue();
            TerritoryName from = selectedFortifyFrom;
            TerritoryName to = selectedFortifyTo;
            game.fortify(selectedFortifyFrom, selectedFortifyTo, armies);
            clearFortifySelection();
            actionStatusMessage = "Fortified " + armies + " from "
                    + formatName(from.name()) + " to " + formatName(to.name()) + ".";
            updateMapColors();
            updateCardHand();
            updateStatusBar();
        } catch (IllegalStateException | IllegalArgumentException e) {
            statusLabel.setText("Invalid: " + e.getMessage());
        }
    }

    @FXML
    private void handleTradeCards() {
        List<Card> selectedCards = getSelectedCards();
        try {
            game.tradeCards(selectedCards);
            cardListView.getSelectionModel().clearSelection();
            updateMapColors();
            updateCardHand();
            updateStatusBar();
        } catch (IllegalStateException | IllegalArgumentException e) {
            statusLabel.setText("Invalid: " + e.getMessage());
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
            statusLabel.setText("Invalid: " + e.getMessage());
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
                    + "    el.title = '" + formatName(territory.name()) + " (" + armies + ")';"
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
        phaseLabel.setText(phase.name());
        playerLabel.setText(game.getCurrentPlayerName()
                + " (" + game.getCurrentPlayerColor().name() + ")");

        if (phase == GamePhase.SCRAMBLE) {
            armiesLabel.setText("Armies: " + game.getArmiesToPlace());
            statusLabel.setText("Click an unclaimed territory to claim it.");
        } else if (phase == GamePhase.SETUP) {
            armiesLabel.setText("Armies: " + game.getArmiesToPlace());
            statusLabel.setText("Click one of your territories to place an army.");
        } else if (phase == GamePhase.ATTACK) {
            armiesLabel.setText("Draft: " + game.getDraftArmies());
            if (game.isCaptureMovementPending()) {
                statusLabel.setText("Move " + game.getMinimumCaptureMove() + "-"
                        + game.getMaximumCaptureMove()
                        + " armies into " + formatName(game.getPendingCaptureTo().name()) + ".");
            } else if (game.isDraftComplete()) {
                if (selectedAttackFrom == null) {
                    statusLabel.setText("Select one of your territories as the starting territory.");
                } else {
                    statusLabel.setText("Select an enemy target or choose a different source.");
                }
            } else if (mustTradeBeforeDraft()) {
                statusLabel.setText("Select a valid card set to trade before drafting.");
            } else {
                statusLabel.setText("Click your territories to place draft armies.");
            }
        } else if (phase == GamePhase.FORTIFY) {
            armiesLabel.setText("");
            if (selectedFortifyFrom == null) {
                statusLabel.setText("Select one of your territories to fortify from.");
            } else if (selectedFortifyTo == null) {
                statusLabel.setText("Select one of your territories to fortify to.");
            } else {
                statusLabel.setText("Choose army count, then fortify or end turn.");
            }
        } else if (phase == GamePhase.GAME_OVER) {
            armiesLabel.setText("");
            PlayerColor winner = game.getWinner();
            String winnerText = winner == null ? "Winner pending" : winner.name() + " wins";
            statusLabel.setText("Game over! " + winnerText + ".");
        }

        if (actionStatusMessage != null && phase != GamePhase.GAME_OVER) {
            statusLabel.setText(actionStatusMessage);
        }
        updateActionControls(phase);
        String playerColor = PLAYER_COLORS.get(game.getCurrentPlayerColor());
        playerLabel.setStyle("-fx-text-fill: " + playerColor + "; -fx-font-weight: bold;");
    }

    private void updateActionControls(GamePhase phase) {
        boolean capturePending = game != null && game.isCaptureMovementPending();
        boolean attackPhase = phase == GamePhase.ATTACK && game != null && game.isDraftComplete();
        captureArmiesSpinner.setDisable(!capturePending);
        moveAfterCaptureButton.setDisable(!capturePending);
        if (capturePending) {
            captureArmiesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    game.getMinimumCaptureMove(),
                    game.getMaximumCaptureMove(),
                    game.getMinimumCaptureMove()));
        }
        endAttackButton.setDisable(!attackPhase || capturePending);

        boolean fortifyPhase = phase == GamePhase.FORTIFY;
        fortifyArmiesSpinner.setDisable(!fortifyPhase);
        fortifyButton.setDisable(!fortifyPhase
                || selectedFortifyFrom == null
                || selectedFortifyTo == null);
        endTurnButton.setDisable(!fortifyPhase);

        boolean tradeReady = game != null
                && phase == GamePhase.ATTACK
                && !game.isDraftComplete()
                && game.canTradeCards(getSelectedCards());
        tradeCardsButton.setDisable(!tradeReady);

        boolean gameOver = phase == GamePhase.GAME_OVER;
        cardListView.setDisable(gameOver);
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

    private String formatCard(Card card) {
        if (card.getType() == CardType.WILD) {
            return "Wild";
        }
        return formatName(card.getType().name())
                + " - " + formatName(card.getTerritory().name());
    }

    private String formatName(String name) {
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
            java.net.URL svgUrl = getClass().getResource("/Risk_board.svg");
            if (svgUrl == null) {
                return "<html><body>Error: Risk_board.svg not found in resources.</body></html>";
            }
            java.nio.file.Path svgPath = java.nio.file.Paths.get(svgUrl.toURI());
            String svgContent = java.nio.file.Files.readString(svgPath);
            svgContent = svgContent.replaceFirst("<\\?xml[^?]*\\?>", "");
            return "<!DOCTYPE html><html><head><style>"
                    + "* { margin: 0; padding: 0; box-sizing: border-box; }"
                    + "html, body { width: 100%; height: 100%;"
                    + " background: #1a2633; overflow: hidden; }"
                    + "svg { width: 100%; height: 100%; display: block; }"
                    + "</style></head><body>"
                    + svgContent
                    + "</body></html>";
        } catch (Exception e) {
            return "<html><body>Error loading map: " + e.getMessage() + "</body></html>";
        }
    }

    private static Map<String, TerritoryName> buildIdMap() {
        Map<String, TerritoryName> map = new java.util.HashMap<>();
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
        return java.util.Collections.unmodifiableMap(map);
    }

    private static Map<PlayerColor, String> buildColorMap() {
        Map<PlayerColor, String> map = new java.util.EnumMap<>(PlayerColor.class);
        map.put(PlayerColor.RED, "#e05555");
        map.put(PlayerColor.BLUE, "#5588dd");
        map.put(PlayerColor.GREEN, "#44aa66");
        map.put(PlayerColor.ORANGE, "#ee8833");
        map.put(PlayerColor.PINK, "#dd66aa");
        map.put(PlayerColor.CYAN, "#44bbcc");
        return java.util.Collections.unmodifiableMap(map);
    }
}
