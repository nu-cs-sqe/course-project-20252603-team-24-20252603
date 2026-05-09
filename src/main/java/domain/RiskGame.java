package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RiskGame {
    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 6;
    private static final int ARMIES_THREE_PLAYERS = 35;
    private static final int ARMIES_FOUR_PLAYERS = 30;
    private static final int ARMIES_FIVE_PLAYERS = 25;
    private static final int ARMIES_SIX_PLAYERS = 20;
    private static final int TOTAL_TERRITORIES = 42;

    private GamePhase phase;
    private WorldMap worldMap;
    private List<Player> players;
    private int currentPlayerIndex;
    private Random random;
    private int territoriesClaimed;

    public RiskGame(Map<PlayerColor, String> playerInfo) {
        this(playerInfo, new Random());
    }

    RiskGame(Map<PlayerColor, String> playerInfo, Random random) {
        this.random = random;
        validatePlayerCount(playerInfo);
        this.worldMap = new WorldMap();
        this.players = new ArrayList<>();
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