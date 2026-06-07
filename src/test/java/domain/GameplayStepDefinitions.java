package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

public class GameplayStepDefinitions {
    private RiskGame game;
    private ScriptedRandom random;
    private final Map<PlayerColor, Player> players = new LinkedHashMap<>();
    private final Map<TerritoryName, Card> cards = new LinkedHashMap<>();
    private Throwable thrown;

    @Given("a Risk game with three players")
    public void aRiskGameWithThreePlayers() {
        random = new ScriptedRandom(0);
        game = new RiskGame(threePlayerInfo(), random);
        players.clear();
        players.put(PlayerColor.RED, new Player(PlayerColor.RED, "Red", 35));
        players.put(PlayerColor.BLUE, new Player(PlayerColor.BLUE, "Blue", 35));
        players.put(PlayerColor.GREEN, new Player(PlayerColor.GREEN, "Green", 35));
        game.providePlayers(new ArrayList<>(players.values()));
        cards.clear();
        thrown = null;
    }

    @Given("{word} is current player in {word} phase")
    public void playerIsCurrentPlayerInPhase(String colorName, String phaseName) {
        PlayerColor color = playerColor(colorName);
        game.setCurrentPlayer(color);
        game.setPhase(GamePhase.valueOf(phaseName));
    }

    @Given("{word} owns {word} with {int} army")
    @Given("{word} owns {word} with {int} armies")
    public void playerOwnsTerritoryWithArmies(String colorName, String territoryName, int armies) {
        game.setupTerritory(territory(territoryName), playerColor(colorName), armies);
    }

    @Given("{word} is reinforced to {int} armies")
    public void territoryIsReinforcedToArmies(String territoryName, int armies) {
        TerritoryName territory = territory(territoryName);
        PlayerColor owner = getOwner(territory);
        game.setupTerritory(territory, owner, armies);
    }

    @Given("{word} has completed drafting")
    public void playerHasCompletedDrafting(String colorName) {
        game.setCurrentPlayer(playerColor(colorName));
        game.setDraftComplete();
    }

    @Given("dice rolls favor the attacker")
    public void diceRollsFavorTheAttacker() {
        random.addRolls(5, 4, 3, 0, 5, 4, 3, 0);
    }

    @Given("dice rolls favor the defender")
    public void diceRollsFavorTheDefender() {
        random.addRolls(0, 0, 0, 5, 5, 0, 0, 0, 5, 5);
    }

    @Given("{word} has the cards {word}, {word}, and {word}")
    public void playerHasTheCards(String colorName, String first, String second, String third) {
        addCards(playerColor(colorName), List.of(first, second, third));
    }

    @Given("{word} has the cards {word} and {word}")
    public void playerHasTwoCards(String colorName, String first, String second) {
        addCards(playerColor(colorName), List.of(first, second));
    }

    @Given("{word} has five cards including a valid set")
    public void playerHasFiveCardsIncludingAValidSet(String colorName) {
        addCards(playerColor(colorName), List.of(
                "ALASKA",
                "ALBERTA",
                "BRAZIL",
                "CHINA",
                "INDIA"));
    }

    @Given("{word} owns every territory except {word}")
    public void playerOwnsEveryTerritoryExcept(String colorName, String exceptionName) {
        PlayerColor color = playerColor(colorName);
        TerritoryName exception = territory(exceptionName);
        for (TerritoryName territory : TerritoryName.values()) {
            if (territory != exception) {
                int armies = territory == TerritoryName.ALASKA ? 3 : 1;
                game.setupTerritory(territory, color, armies);
            }
        }
    }

    @When("{word} trades the cards {word}, {word}, and {word}")
    public void playerTradesTheCards(String colorName, String first, String second, String third) {
        runAction(() -> game.tradeCards(List.of(
                card(first),
                card(second),
                card(third))));
    }

    @When("{word} drafts {int} army on {word}")
    @When("{word} drafts {int} armies on {word}")
    public void playerDraftsArmiesOnTerritory(String colorName, int armies, String territoryName) {
        game.setCurrentPlayer(playerColor(colorName));
        runAction(() -> {
            for (int i = 0; i < armies; i++) {
                game.draftArmy(territory(territoryName));
            }
        });
    }

    @When("{word} attacks from {word} to {word}")
    public void playerAttacksFromTo(String colorName, String fromName, String toName) {
        game.setCurrentPlayer(playerColor(colorName));
        runAction(() -> game.attack(territory(fromName), territory(toName)));
    }

    @When("{word} moves {int} army from {word} to {word} after capture")
    @When("{word} moves {int} armies from {word} to {word} after capture")
    public void playerMovesArmiesAfterCapture(
            String colorName,
            int armies,
            String fromName,
            String toName) {
        game.setCurrentPlayer(playerColor(colorName));
        runAction(() -> game.moveArmiesAfterCapture(
                territory(fromName),
                territory(toName),
                armies));
    }

    @When("{word} fortifies {int} army from {word} to {word}")
    @When("{word} fortifies {int} armies from {word} to {word}")
    public void playerFortifiesArmies(
            String colorName, int armies, String fromName, String toName) {
        game.setCurrentPlayer(playerColor(colorName));
        runAction(() -> game.fortify(territory(fromName), territory(toName), armies));
    }

    @When("{word} completes an attack turn from {word} to {word}")
    public void playerCompletesAnAttackTurn(String colorName, String fromName, String toName) {
        game.setCurrentPlayer(playerColor(colorName));
        runAction(() -> {
            game.attack(territory(fromName), territory(toName));
            game.endAttack();
            game.endTurn();
        });
    }

    @When("{word} completes an attack turn with captures from {word} to {word} and {word}")
    public void playerCompletesAnAttackTurnWithTwoCaptures(
            String colorName,
            String fromName,
            String firstToName,
            String secondToName) {
        game.setCurrentPlayer(playerColor(colorName));
        runAction(() -> {
            game.attack(territory(fromName), territory(firstToName));
            game.attack(territory(fromName), territory(secondToName));
            game.endAttack();
            game.endTurn();
        });
    }

    @When("{word} completes a full turn by drafting on {word} attacking {word} "
            + "moving {int} armies and fortifying from {word} to {word}")
    public void playerCompletesFullTurn(
            String colorName,
            String draftTerritoryName,
            String attackedTerritoryName,
            int movedArmies,
            String fortifyFromName,
            String fortifyToName) {
        PlayerColor color = playerColor(colorName);
        TerritoryName draftTerritory = territory(draftTerritoryName);
        TerritoryName attackedTerritory = territory(attackedTerritoryName);
        TerritoryName fortifyFrom = territory(fortifyFromName);
        TerritoryName fortifyTo = territory(fortifyToName);
        game.setCurrentPlayer(color);
        runAction(() -> {
            while (!game.isDraftComplete()) {
                game.draftArmy(draftTerritory);
            }
            game.attack(draftTerritory, attackedTerritory);
            game.moveArmiesAfterCapture(draftTerritory, attackedTerritory, movedArmies);
            game.endAttack();
            game.fortify(fortifyFrom, fortifyTo, 1);
            game.endTurn();
        });
    }

    @Then("{word} has {int} card")
    @Then("{word} has {int} cards")
    public void playerHasCards(String colorName, int count) {
        assertEquals(count, game.getCards(playerColor(colorName)).size());
    }

    @Then("the deck discard pile has {int} cards")
    public void deckDiscardPileHasCards(int count) {
        assertEquals(count, game.getDeckDiscardPileSize());
    }

    @Then("{word} has {int} draft armies")
    public void playerHasDraftArmies(String colorName, int count) {
        game.setCurrentPlayer(playerColor(colorName));
        assertEquals(count, game.getDraftArmies());
    }

    @Then("{word} has {int} army")
    @Then("{word} has {int} armies")
    public void territoryHasExpectedArmies(String territoryName, int armies) {
        assertEquals(armies, game.getArmies(territory(territoryName)));
    }

    @Then("{word} is owned by {word}")
    public void territoryIsOwnedBy(String territoryName, String colorName) {
        assertTrue(game.isOwnedBy(territory(territoryName), playerColor(colorName)));
    }

    @Then("the current player is {word}")
    public void currentPlayerIs(String colorName) {
        assertEquals(playerColor(colorName), game.getCurrentPlayerColor());
    }

    @Then("the game phase is {word}")
    public void gamePhaseIs(String phaseName) {
        assertEquals(GamePhase.valueOf(phaseName), game.getPhase());
    }

    @Then("the winner is {word}")
    public void winnerIs(String colorName) {
        assertEquals(playerColor(colorName), game.getWinner());
    }

    @Then("the action fails with IllegalStateException")
    public void actionFailsWithIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> {
            if (thrown != null) {
                throw thrown;
            }
        });
    }

    @Then("the action fails with IllegalArgumentException")
    public void actionFailsWithIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            if (thrown != null) {
                throw thrown;
            }
        });
    }

    private void runAction(Action action) {
        thrown = null;
        try {
            action.run();
        } catch (Throwable t) {
            thrown = t;
        }
    }

    private void addCards(PlayerColor color, List<String> territoryNames) {
        Player player = players.get(color);
        for (String territoryName : territoryNames) {
            Card card = new Card(cardType(cards.size()), territory(territoryName));
            cards.put(territory(territoryName), card);
            player.addCard(card);
        }
    }

    private Card card(String territoryName) {
        return cards.get(territory(territoryName));
    }

    private PlayerColor getOwner(TerritoryName territory) {
        for (PlayerColor color : PlayerColor.values()) {
            if (game.isOwnedBy(territory, color)) {
                return color;
            }
        }
        return PlayerColor.RED;
    }

    private static Map<PlayerColor, String> threePlayerInfo() {
        Map<PlayerColor, String> playerInfo = new LinkedHashMap<>();
        playerInfo.put(PlayerColor.RED, "Red");
        playerInfo.put(PlayerColor.BLUE, "Blue");
        playerInfo.put(PlayerColor.GREEN, "Green");
        return playerInfo;
    }

    private static PlayerColor playerColor(String colorName) {
        return PlayerColor.valueOf(colorName);
    }

    private static TerritoryName territory(String territoryName) {
        return TerritoryName.valueOf(territoryName);
    }

    private static CardType cardType(int index) {
        CardType[] types = {CardType.INFANTRY, CardType.CAVALRY, CardType.ARTILLERY};
        return types[index % types.length];
    }

    private interface Action {
        void run() throws Throwable;
    }

    private static final class ScriptedRandom extends Random {
        private final Queue<Integer> rolls = new ArrayDeque<>();

        ScriptedRandom(int... values) {
            addRolls(values);
        }

        void addRolls(int... values) {
            for (int value : values) {
                rolls.add(value);
            }
        }

        @Override
        public int nextInt(int bound) {
            if (rolls.isEmpty()) {
                return 0;
            }
            return Math.floorMod(rolls.remove(), bound);
        }
    }
}
