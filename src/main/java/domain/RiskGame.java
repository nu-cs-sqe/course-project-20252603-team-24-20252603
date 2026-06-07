package domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class RiskGame {
    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 6;
    private static final int ARMIES_THREE_PLAYERS = 35;
    private static final int ARMIES_FOUR_PLAYERS = 30;
    private static final int ARMIES_FIVE_PLAYERS = 25;
    private static final int ARMIES_SIX_PLAYERS = 20;
    private static final int TOTAL_TERRITORIES = 42;
    private static final int MIN_DRAFT_ARMIES = 3;
    private static final int MIN_ATTACK_DICE = 1;
    private static final int MAX_ATTACK_DICE = 3;
    private static final int MAX_DEFEND_DICE = 2;
    private static final int DIE_SIDES = 6;
    private static final int NORTH_AMERICA_BONUS = 5;
    private static final int SOUTH_AMERICA_BONUS = 2;
    private static final int EUROPE_BONUS = 5;
    private static final int AFRICA_BONUS = 3;
    private static final int ASIA_BONUS = 7;
    private static final int AUSTRALIA_BONUS = 2;
    private static final List<TerritoryName> NORTH_AMERICA = List.of(
            TerritoryName.ALASKA,
            TerritoryName.NORTHWEST_TERRITORY,
            TerritoryName.GREENLAND,
            TerritoryName.ALBERTA,
            TerritoryName.ONTARIO,
            TerritoryName.QUEBEC,
            TerritoryName.WESTERN_UNITED_STATES,
            TerritoryName.EASTERN_UNITED_STATES,
            TerritoryName.CENTRAL_AMERICA
    );
    private static final List<TerritoryName> SOUTH_AMERICA = List.of(
            TerritoryName.VENEZUELA,
            TerritoryName.PERU,
            TerritoryName.BRAZIL,
            TerritoryName.ARGENTINA
    );
    private static final List<TerritoryName> EUROPE = List.of(
            TerritoryName.ICELAND,
            TerritoryName.GREAT_BRITAIN,
            TerritoryName.WESTERN_EUROPE,
            TerritoryName.NORTHERN_EUROPE,
            TerritoryName.SOUTHERN_EUROPE,
            TerritoryName.SCANDINAVIA,
            TerritoryName.UKRAINE
    );
    private static final List<TerritoryName> AFRICA = List.of(
            TerritoryName.NORTH_AFRICA,
            TerritoryName.EGYPT,
            TerritoryName.EAST_AFRICA,
            TerritoryName.CONGO,
            TerritoryName.SOUTH_AFRICA,
            TerritoryName.MADAGASCAR
    );
    private static final List<TerritoryName> ASIA = List.of(
            TerritoryName.MIDDLE_EAST,
            TerritoryName.AFGHANISTAN,
            TerritoryName.URAL,
            TerritoryName.SIBERIA,
            TerritoryName.YAKUTSK,
            TerritoryName.KAMCHATKA,
            TerritoryName.IRKUTSK,
            TerritoryName.MONGOLIA,
            TerritoryName.JAPAN,
            TerritoryName.CHINA,
            TerritoryName.INDIA,
            TerritoryName.SIAM
    );
    private static final List<TerritoryName> AUSTRALIA = List.of(
            TerritoryName.EASTERN_AUSTRALIA,
            TerritoryName.WESTERN_AUSTRALIA,
            TerritoryName.NEW_GUINEA,
            TerritoryName.INDONESIA
    );

    private GamePhase phase;
    private WorldMap worldMap;
    private List<Player> players;
    private int currentPlayerIndex;
    private int firstSetupPlayerIndex;
    private int territoriesClaimed;
    private int draftArmiesRemaining;
    private boolean isDraftInitialized;
    private int tradeSetCount;
    private boolean hasFortifiedThisTurn;
    private boolean capturedThisTurn;
    private TerritoryName pendingCaptureFrom;
    private TerritoryName pendingCaptureTo;
    private Deck deck;
    private Random random;

    public RiskGame(Map<PlayerColor, String> playerInfo) {
        this(playerInfo, new Random());
    }

    RiskGame(Map<PlayerColor, String> playerInfo, Random random) {
        validatePlayerCount(playerInfo);
        this.worldMap = new WorldMap();
        this.players = new ArrayList<>();
        this.random = random;
        this.deck = new Deck();
        initializePlayers(playerInfo);
        this.currentPlayerIndex = random.nextInt(players.size());
        this.firstSetupPlayerIndex = currentPlayerIndex;
        this.phase = GamePhase.SCRAMBLE;
        this.territoriesClaimed = 0;
    }

    public GamePhase getPhase() {
        return phase;
    }

    private void validatePlayerCount(Map<PlayerColor, String> playerInfo) {
        if (playerInfo == null) {
            throw new IllegalArgumentException("playerInfo cannot be null");
        }
        // The `> MAX_PLAYERS` branch is structurally unreachable because
        // PlayerColor only defines 6 values, so a Map<PlayerColor, String>
        // can never have more than 6 entries. Kept as a defensive guard in
        // case PlayerColor ever grows. JaCoCo flags it as a missed branch;
        // see README "Coverage exceptions" for the equivalent-mutant note.
        if (playerInfo.size() < MIN_PLAYERS || playerInfo.size() > MAX_PLAYERS) {
            throw new IllegalArgumentException("player count must be between 3 and 6");
        }
    }

    private void initializePlayers(Map<PlayerColor, String> playerInfo) {
        int armies = getStartingArmies(playerInfo.size());
        for (Map.Entry<PlayerColor, String> entry : playerInfo.entrySet()) {
            players.add(new Player(entry.getKey(), entry.getValue(), armies));
        }
    }

    private int getStartingArmies(int playerCount) {
        switch (playerCount) {
            case 3: return ARMIES_THREE_PLAYERS;
            case 4: return ARMIES_FOUR_PLAYERS;
            case 5: return ARMIES_FIVE_PLAYERS;
            default: return ARMIES_SIX_PLAYERS;
        }
    }

    public void claimTerritory(TerritoryName territory) {
        if (phase != GamePhase.SCRAMBLE) {
            throw new IllegalStateException("can only claim territories during SCRAMBLE phase");
        }
        worldMap.claim(territory, getCurrentPlayerColor());
        worldMap.addArmies(territory, 1);
        players.get(currentPlayerIndex).decreaseArmiesToPlace(1);
        territoriesClaimed++;
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        if (territoriesClaimed == TOTAL_TERRITORIES) {
            phase = GamePhase.SETUP;
            firstSetupPlayerIndex = currentPlayerIndex;
        }
    }

    public PlayerColor getCurrentPlayerColor() {
        return players.get(currentPlayerIndex).getColor();
    }

    public void placeArmy(TerritoryName territory) {
        if (phase != GamePhase.SETUP) {
            throw new IllegalStateException("can only place armies during SETUP phase");
        }
        if (!worldMap.isOwnedBy(territory, getCurrentPlayerColor())) {
            throw new IllegalArgumentException("territory not owned by current player");
        }
        if (!players.get(currentPlayerIndex).hasArmiesToPlace()) {
            throw new IllegalArgumentException("no armies left to place");
        }
        worldMap.addArmies(territory, 1);
        players.get(currentPlayerIndex).decreaseArmiesToPlace(1);
        advanceToNextPlayer();
    }

    public boolean isSetupComplete() {
        for (Player p : players) {
            if (p.hasArmiesToPlace()) {
                return false;
            }
        }
        return true;
    }

    public String getCurrentPlayerName() {
        return players.get(currentPlayerIndex).getName();
    }

    public String getPlayerName(PlayerColor color) {
        return getPlayer(color).getName();
    }

    public int getArmiesToPlace() {
        return players.get(currentPlayerIndex).getArmiesToPlace();
    }

    public int getDraftArmies() {
        if (isDraftInitialized) {
            return draftArmiesRemaining;
        }
        int owned = worldMap.countTerritoriesOwnedBy(getCurrentPlayerColor());
        return Math.max(MIN_DRAFT_ARMIES, owned / 3) + getContinentBonus();
    }

    public void draftArmy(TerritoryName territory) {
        if (phase != GamePhase.ATTACK) {
            throw new IllegalStateException("can only draft armies during ATTACK phase");
        }
        if (players.get(currentPlayerIndex).getCardCount() >= 5) {
            throw new IllegalStateException("must trade cards before drafting");
        }
        if (draftArmiesRemaining == 0 && !isDraftInitialized) {
            int owned = worldMap.countTerritoriesOwnedBy(getCurrentPlayerColor());
            draftArmiesRemaining = Math.max(MIN_DRAFT_ARMIES, owned / 3) + getContinentBonus();
            isDraftInitialized = true;
        }
        if (!worldMap.isOwnedBy(territory, getCurrentPlayerColor())) {
            throw new IllegalArgumentException("territory not owned by current player");
        }
        if (draftArmiesRemaining == 0) {
            throw new IllegalArgumentException("no draft armies remaining");
        }
        worldMap.addArmies(territory, 1);
        draftArmiesRemaining--;
    }

    public boolean isDraftComplete() {
        return isDraftInitialized && draftArmiesRemaining == 0;
    }

    public void attack(TerritoryName from, TerritoryName to) {
        if (phase != GamePhase.ATTACK) {
            throw new IllegalStateException("can only attack during ATTACK phase");
        }
        if (!isDraftInitialized || draftArmiesRemaining > 0) {
            throw new IllegalStateException("must complete draft before attacking");
        }
        PlayerColor current = getCurrentPlayerColor();
        if (!worldMap.isOwnedBy(from, current)) {
            throw new IllegalArgumentException("from territory not owned by current player");
        }
        if (worldMap.isOwnedBy(to, current)) {
            throw new IllegalArgumentException("cannot attack own territory");
        }
        if (!worldMap.areNeighbors(from, to)) {
            throw new IllegalArgumentException("territories are not neighbors");
        }
        if (worldMap.getArmies(from) < 2) {
            throw new IllegalArgumentException("must have at least 2 armies to attack");
        }
        pendingCaptureFrom = null;
        pendingCaptureTo = null;
        while (worldMap.getArmies(from) > 1 && worldMap.getArmies(to) > 0) {
            int numAttackers = Math.min(MAX_ATTACK_DICE, worldMap.getArmies(from) - 1);
            int numDefenders = Math.min(MAX_DEFEND_DICE, worldMap.getArmies(to));
            int[] attackerRolls = rollDiceDescending(numAttackers);
            int[] defenderRolls = rollDiceDescending(numDefenders);
            int comparisons = Math.min(numAttackers, numDefenders);
            for (int i = 0; i < comparisons; i++) {
                if (attackerRolls[i] > defenderRolls[i]) {
                    worldMap.removeArmies(to, 1);
                } else {
                    worldMap.removeArmies(from, 1);
                }
            }
        }
        if (worldMap.getArmies(to) == 0) {
            captureTerritory(from, to);
        }
    }

    private void captureTerritory(TerritoryName from, TerritoryName to) {
        final PlayerColor defenderColor = getOwnerOf(to);
        worldMap.assignTerritory(to, getCurrentPlayerColor());
        capturedThisTurn = true;
        pendingCaptureFrom = from;
        pendingCaptureTo = to;
        if (defenderColor != null && worldMap.countTerritoriesOwnedBy(defenderColor) == 0) {
            transferCards(defenderColor, getCurrentPlayerColor());
        }
        if (getWinner() != null) {
            phase = GamePhase.GAME_OVER;
        }
    }

    private PlayerColor getOwnerOf(TerritoryName territory) {
        for (Player p : players) {
            if (worldMap.isOwnedBy(territory, p.getColor())) {
                return p.getColor();
            }
        }
        return null;
    }

    private void transferCards(PlayerColor from, PlayerColor to) {
        Player fromPlayer = getPlayer(from);
        Player toPlayer = getPlayer(to);
        List<Card> cards = new ArrayList<>(fromPlayer.getCards());
        fromPlayer.removeCards(cards);
        for (Card card : cards) {
            toPlayer.addCard(card);
        }
    }

    private Player getPlayer(PlayerColor color) {
        for (Player p : players) {
            if (p.getColor() == color) {
                return p;
            }
        }
        throw new IllegalArgumentException("player color not in game");
    }

    private int getContinentBonus() {
        Set<TerritoryName> owned = worldMap.getTerritoriesOwnedBy(getCurrentPlayerColor());
        int bonus = 0;
        if (ownsAll(owned, NORTH_AMERICA)) {
            bonus += NORTH_AMERICA_BONUS;
        }
        if (ownsAll(owned, SOUTH_AMERICA)) {
            bonus += SOUTH_AMERICA_BONUS;
        }
        if (ownsAll(owned, EUROPE)) {
            bonus += EUROPE_BONUS;
        }
        if (ownsAll(owned, AFRICA)) {
            bonus += AFRICA_BONUS;
        }
        if (ownsAll(owned, ASIA)) {
            bonus += ASIA_BONUS;
        }
        if (ownsAll(owned, AUSTRALIA)) {
            bonus += AUSTRALIA_BONUS;
        }
        return bonus;
    }

    private boolean ownsAll(Set<TerritoryName> owned, List<TerritoryName> territories) {
        return owned.containsAll(territories);
    }

    private void advanceToNextActivePlayer() {
        int fallbackIndex = (currentPlayerIndex + 1) % players.size();
        for (int i = 1; i <= players.size(); i++) {
            int candidateIndex = (currentPlayerIndex + i) % players.size();
            PlayerColor candidate = players.get(candidateIndex).getColor();
            if (worldMap.countTerritoriesOwnedBy(candidate) > 0) {
                currentPlayerIndex = candidateIndex;
                return;
            }
        }
        currentPlayerIndex = fallbackIndex;
    }

    private int[] rollDiceDescending(int count) {
        Integer[] rolls = new Integer[count];
        for (int i = 0; i < count; i++) {
            // Pitest flags the `+ 1` as a surviving mutant (replaced with
            // `- 1`). Dice values are only compared to one another, so a
            // constant offset is unobservable; equivalent mutant. See
            // README "Coverage exceptions".
            rolls[i] = random.nextInt(DIE_SIDES) + 1;
        }
        Arrays.sort(rolls, Collections.reverseOrder());
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = rolls[i];
        }
        return result;
    }

    public void endAttack() {
        if (phase != GamePhase.ATTACK) {
            throw new IllegalStateException("can only end attack during ATTACK phase");
        }
        if (!isDraftComplete()) {
            throw new IllegalStateException("must complete draft before ending attack");
        }
        pendingCaptureFrom = null;
        pendingCaptureTo = null;
        phase = GamePhase.FORTIFY;
    }

    public void moveArmiesAfterCapture(TerritoryName from, TerritoryName to, int armies) {
        if (phase != GamePhase.ATTACK) {
            throw new IllegalStateException(
                    "can only move armies after capture during ATTACK phase");
        }
        if (pendingCaptureFrom == null
                || !pendingCaptureFrom.equals(from)
                || !pendingCaptureTo.equals(to)) {
            throw new IllegalStateException("no pending capture from the specified territories");
        }
        int minMove = Math.min(3, worldMap.getArmies(from) - 1);
        if (armies < minMove) {
            throw new IllegalArgumentException("armies must be at least " + minMove);
        }
        if (armies >= worldMap.getArmies(from)) {
            throw new IllegalArgumentException("must leave at least 1 army behind");
        }
        worldMap.removeArmies(from, armies);
        worldMap.addArmies(to, armies);
        pendingCaptureFrom = null;
        pendingCaptureTo = null;
    }

    public boolean isCaptureMovementPending() {
        return pendingCaptureFrom != null;
    }

    public TerritoryName getPendingCaptureFrom() {
        return pendingCaptureFrom;
    }

    public TerritoryName getPendingCaptureTo() {
        return pendingCaptureTo;
    }

    public int getMinimumCaptureMove() {
        if (!isCaptureMovementPending()) {
            throw new IllegalStateException("no pending capture");
        }
        return Math.min(3, worldMap.getArmies(pendingCaptureFrom) - 1);
    }

    public int getMaximumCaptureMove() {
        if (!isCaptureMovementPending()) {
            throw new IllegalStateException("no pending capture");
        }
        return worldMap.getArmies(pendingCaptureFrom) - 1;
    }

    public PlayerColor getWinner() {
        for (Player p : players) {
            if (worldMap.countTerritoriesOwnedBy(p.getColor()) == TOTAL_TERRITORIES) {
                return p.getColor();
            }
        }
        return null;
    }

    public void endTurn() {
        if (phase != GamePhase.FORTIFY) {
            throw new IllegalStateException("can only end turn during FORTIFY phase");
        }
        if (capturedThisTurn) {
            players.get(currentPlayerIndex).addCard(deck.draw());
        }
        capturedThisTurn = false;
        advanceToNextActivePlayer();
        phase = GamePhase.ATTACK;
        draftArmiesRemaining = 0;
        isDraftInitialized = false;
        hasFortifiedThisTurn = false;
    }

    public void fortify(TerritoryName from, TerritoryName to, int armies) {
        if (phase != GamePhase.FORTIFY) {
            throw new IllegalStateException("can only fortify during FORTIFY phase");
        }

        if (hasFortifiedThisTurn) {
            throw new IllegalStateException("can only fortify once per turn");
        }

        PlayerColor current = getCurrentPlayerColor();
        if (!worldMap.isOwnedBy(from, current)) {
            throw new IllegalArgumentException("from territory not owned by current player");
        }
        if (!worldMap.isOwnedBy(to, current)) {
            throw new IllegalArgumentException("to territory not owned by current player");
        }
        if (!worldMap.areConnectedThrough(from, to, current)) {
            throw new IllegalArgumentException("territories are not connected through owned chain");
        }
        if (armies < 1) {
            throw new IllegalArgumentException("armies must be at least 1");
        }
        if (armies >= worldMap.getArmies(from)) {
            throw new IllegalArgumentException("must leave at least 1 army behind");
        }
        worldMap.removeArmies(from, armies);
        worldMap.addArmies(to, armies);
        hasFortifiedThisTurn = true;
    }

    public boolean canTradeCards(List<Card> cards) {
        if (cards == null) {
            return false;
        }
        if (cards.size() != 3) {
            return false;
        }
        for (Card card : cards) {
            if (card == null) {
                return false;
            }
        }
        int wildcards = 0;
        int infantry = 0;
        int cavalry = 0;
        int artillery = 0;
        for (Card card : cards) {
            if (card.isWild()) {
                wildcards++;
            } else if (card.getType() == CardType.INFANTRY) {
                infantry++;
            } else if (card.getType() == CardType.CAVALRY) {
                // Pitest flags negating this conditional as a surviving
                // mutant. The downstream checks are symmetric in cavalry
                // and artillery, so swapping their counts produces the
                // same return value; equivalent mutant. See README
                // "Coverage exceptions".
                cavalry++;
            } else {
                artillery++;
            }
        }
        if (wildcards > 0) {
            return true;
        }
        if (infantry == 3 || cavalry == 3 || artillery == 3) {
            return true;
        }
        // At this point cards.size() == 3 (checked above) and wildcards == 0,
        // so infantry + cavalry + artillery == 3. If infantry == 1 and
        // cavalry == 1, then artillery == 1 necessarily; the false-branch of
        // the third condition is structurally unreachable. JaCoCo flags this
        // as a missed branch; see README "Coverage exceptions".
        return infantry == 1 && cavalry == 1 && artillery == 1;
    }

    public void tradeCards(List<Card> cards) {
        if (phase != GamePhase.ATTACK) {
            throw new IllegalStateException("can only trade cards during ATTACK phase");
        }
        if (isDraftComplete()) {
            throw new IllegalStateException("cannot trade cards after draft is complete");
        }
        if (cards == null) {
            throw new IllegalArgumentException("cards cannot be null");
        }
        for (Card card : cards) {
            if (card == null) {
                throw new IllegalArgumentException("cards cannot contain null");
            }
        }
        if (!canTradeCards(cards)) {
            throw new IllegalArgumentException("invalid card set");
        }
        Player current = players.get(currentPlayerIndex);
        if (!current.hasCards(cards)) {
            throw new IllegalArgumentException("player does not own all specified cards");
        }
        current.removeCards(cards);
        deck.discard(cards);
        tradeSetCount++;
        int[] bonusTable = {4, 6, 8, 10, 12, 15};
        // Pitest flags the boundary mutation `<= 6` -> `< 6` as a
        // surviving mutant. `bonusTable[5] == 15` matches the formula
        // evaluated at the boundary (`15 + 5 * (6 - 6) == 15`), so the
        // two branches produce identical output at every tradeSetCount;
        // equivalent mutant. See README "Coverage exceptions".
        int bonus = tradeSetCount <= 6
                ? bonusTable[tradeSetCount - 1]
                : 15 + 5 * (tradeSetCount - 6);
        if (!isDraftInitialized) {
            int owned = worldMap.countTerritoriesOwnedBy(getCurrentPlayerColor());
            draftArmiesRemaining = Math.max(MIN_DRAFT_ARMIES, owned / 3) + getContinentBonus();
            isDraftInitialized = true;
        }
        draftArmiesRemaining += bonus;
        for (Card card : cards) {
            if (!card.isWild()
                    && worldMap.isOwnedBy(card.getTerritory(), getCurrentPlayerColor())) {
                worldMap.addArmies(card.getTerritory(), 2);
            }
        }
    }

    public List<Card> getCards(PlayerColor color) {
        for (Player p : players) {
            if (p.getColor() == color) {
                return p.getCards();
            }
        }
        throw new IllegalArgumentException("player color not in game");
    }

    public boolean isOwnedBy(TerritoryName territory, PlayerColor color) {
        return worldMap.isOwnedBy(territory, color);
    }

    public boolean isUnclaimed(TerritoryName territory) {
        return worldMap.isUnclaimed(territory);
    }

    public int getArmies(TerritoryName territory) {
        return worldMap.getArmies(territory);
    }

    public int getTerritoryCount(PlayerColor color) {
        return worldMap.countTerritoriesOwnedBy(color);
    }

    private void advanceToNextPlayer() {
        if (isSetupComplete()) {
            currentPlayerIndex = firstSetupPlayerIndex;
            phase = GamePhase.ATTACK;
            return;
        }
        // The loop always returns from inside before the natural exit
        // condition `i > players.size()` becomes true: isSetupComplete() was
        // false just above, so at least one player still has armies and the
        // `if` inside will fire within one full rotation. The implicit
        // fallthrough on the closing brace is structurally unreachable.
        // JaCoCo flags it as a missed branch and missed line; see README
        // "Coverage exceptions".
        for (int i = 1; i <= players.size(); i++) {
            int candidateIndex = (currentPlayerIndex + i) % players.size();
            if (players.get(candidateIndex).hasArmiesToPlace()) {
                currentPlayerIndex = candidateIndex;
                return;
            }
        }
    }

    void setupTerritory(TerritoryName territory, PlayerColor owner, int armies) {
        worldMap.claim(territory, owner);
        worldMap.addArmies(territory, armies);
    }

    void setCapturedThisTurn(boolean value) {
        this.capturedThisTurn = value;
    }

    int getDeckDiscardPileSize() {
        return deck.getDiscardPileSize();
    }

    void setTradeSetCount(int count) {
        this.tradeSetCount = count;
    }

    void provideWorldMap(WorldMap map) {
        this.worldMap = map;
    }

    void providePlayers(List<Player> players) {
        this.players = players;
    }

    void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    void setCurrentPlayer(PlayerColor color) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getColor() == color) {
                this.currentPlayerIndex = i;
                return;
            }
        }
    }

    void setDraftComplete() {
        this.isDraftInitialized = true;
        this.draftArmiesRemaining = 0;
    }
}
