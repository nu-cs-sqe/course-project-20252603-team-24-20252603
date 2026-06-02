package domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    private GamePhase phase;
    private WorldMap worldMap;
    private List<Player> players;
    private int currentPlayerIndex;
    private int territoriesClaimed;
    private int draftArmiesRemaining;
    private boolean isDraftInitialized;
    private Random random;

    public RiskGame(Map<PlayerColor, String> playerInfo) {
        this(playerInfo, new Random());
    }

    RiskGame(Map<PlayerColor, String> playerInfo, Random random) {
        validatePlayerCount(playerInfo);
        this.worldMap = new WorldMap();
        this.players = new ArrayList<>();
        this.random = random;
        initializePlayers(playerInfo);
        this.currentPlayerIndex = random.nextInt(players.size());
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
        if (territoriesClaimed == TOTAL_TERRITORIES) {
            phase = GamePhase.SETUP;
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
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
            if (p.hasArmiesToPlace()) return false;
        }
        return true;
    }

    public String getCurrentPlayerName() {
        return players.get(currentPlayerIndex).getName();
    }

    public int getArmiesToPlace() {
        return players.get(currentPlayerIndex).getArmiesToPlace();
    }

    public int getDraftArmies() {
        if (draftArmiesRemaining > 0) {
            return draftArmiesRemaining;
        }
        int owned = worldMap.countTerritoriesOwnedBy(getCurrentPlayerColor());
        return Math.max(MIN_DRAFT_ARMIES, owned / 3);
    }

    public void draftArmy(TerritoryName territory) {
        if (phase != GamePhase.ATTACK) {
            throw new IllegalStateException("can only draft armies during ATTACK phase");
        }
        if (draftArmiesRemaining == 0 && !isDraftInitialized) {
            int owned = worldMap.countTerritoriesOwnedBy(getCurrentPlayerColor());
            draftArmiesRemaining = Math.max(MIN_DRAFT_ARMIES, owned / 3);
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

    public void attack(TerritoryName from, TerritoryName to, int numAttackers) {
        if (phase != GamePhase.ATTACK) {
            throw new IllegalStateException("can only attack during ATTACK phase");
        }
        if (isDraftInitialized && draftArmiesRemaining > 0) {
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
        if (numAttackers < MIN_ATTACK_DICE || numAttackers > MAX_ATTACK_DICE) {
            throw new IllegalArgumentException("numAttackers must be between 1 and 3");
        }
        if (numAttackers >= worldMap.getArmies(from)) {
            throw new IllegalArgumentException("must leave at least 1 army behind");
        }
        int defenderArmies = worldMap.getArmies(to);
        int numDefenders = Math.min(MAX_DEFEND_DICE, defenderArmies);
        int[] attackerRolls = rollDiceDescending(numAttackers);
        int[] defenderRolls = rollDiceDescending(numDefenders);
        int comparisons = Math.min(numAttackers, numDefenders);
        int attackerLosses = 0;
        int defenderLosses = 0;
        for (int i = 0; i < comparisons; i++) {
            if (attackerRolls[i] > defenderRolls[i]) {
                defenderLosses++;
            } else {
                attackerLosses++;
            }
        }
        if (attackerLosses > 0) {
            worldMap.removeArmies(from, attackerLosses);
        }
        if (defenderLosses > 0) {
            worldMap.removeArmies(to, defenderLosses);
        }
        if (defenderLosses == defenderArmies) {
            captureTerritory(from, to, numAttackers);
        }
    }

    private void captureTerritory(TerritoryName from, TerritoryName to, int armiesToMove) {
        worldMap.assignTerritory(to, getCurrentPlayerColor());
        worldMap.removeArmies(from, armiesToMove);
        worldMap.addArmies(to, armiesToMove);
        if (getWinner() != null) {
            phase = GamePhase.GAME_OVER;
        }
    }

    private int[] rollDiceDescending(int count) {
        Integer[] rolls = new Integer[count];
        for (int i = 0; i < count; i++) {
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
        phase = GamePhase.FORTIFY;
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
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        phase = GamePhase.ATTACK;
        draftArmiesRemaining = 0;
        isDraftInitialized = false;
    }

    public void fortify(TerritoryName from, TerritoryName to, int armies) {
        if (phase != GamePhase.FORTIFY) {
            throw new IllegalStateException("can only fortify during FORTIFY phase");
        }
        PlayerColor current = getCurrentPlayerColor();
        if (!worldMap.isOwnedBy(from, current)) {
            throw new IllegalArgumentException("from territory not owned by current player");
        }
        if (!worldMap.isOwnedBy(to, current)) {
            throw new IllegalArgumentException("to territory not owned by current player");
        }
        if (!worldMap.areNeighbors(from, to)) {
            throw new IllegalArgumentException("territories are not neighbors");
        }
        if (armies < 1) {
            throw new IllegalArgumentException("armies must be at least 1");
        }
        if (armies >= worldMap.getArmies(from)) {
            throw new IllegalArgumentException("must leave at least 1 army behind");
        }
        worldMap.removeArmies(from, armies);
        worldMap.addArmies(to, armies);
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

    private void advanceToNextPlayer() {
        if (isSetupComplete()) {
            phase = GamePhase.ATTACK;
            return;
        }
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (!players.get(currentPlayerIndex).hasArmiesToPlace());
    }

    void setupTerritory(TerritoryName territory, PlayerColor owner, int armies) {
        worldMap.claim(territory, owner);
        worldMap.addArmies(territory, armies);
    }

    // helpers
    void provideWorldMap(WorldMap map) { this.worldMap = map; }
    void providePlayers(List<Player> players) { this.players = players; }
    void setPhase(GamePhase phase) { this.phase = phase; }
    void setCurrentPlayer(PlayerColor color) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getColor() == color) {
                this.currentPlayerIndex = i;
                return;
            }
        }
    }
}