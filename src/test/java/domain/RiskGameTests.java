package domain;

import org.junit.jupiter.api.Test;
import org.easymock.EasyMock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class RiskGameTests {

    private Map<PlayerColor, String> threePlayerMap() {
        return Map.of(
                PlayerColor.RED, "Jonathan",
                PlayerColor.BLUE, "Justin",
                PlayerColor.GREEN, "Prashant"
        );
    }

    private Random stubbedRandom(int returnValue) {
        Random rand = EasyMock.createMock(Random.class);
        EasyMock.expect(rand.nextInt(EasyMock.anyInt())).andStubReturn(returnValue);
        EasyMock.replay(rand);
        return rand;
    }

    @Test
    public void Constructor_ThreePlayers_PhaseIsScramble() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        assertEquals(GamePhase.SCRAMBLE, game.getPhase());
    }

    @Test
    public void Constructor_FourPlayers_PhaseIsScramble() {
        Map<PlayerColor, String> players = Map.of(
                PlayerColor.RED, "Jonathan",
                PlayerColor.BLUE, "Justin",
                PlayerColor.GREEN, "Prashant",
                PlayerColor.ORANGE, "David"
        );
        RiskGame game = new RiskGame(players, stubbedRandom(0));
        assertEquals(GamePhase.SCRAMBLE, game.getPhase());
    }

    @Test
    public void Constructor_FivePlayers_PhaseIsScramble() {
        Map<PlayerColor, String> players = Map.of(
                PlayerColor.RED, "Jovy",
                PlayerColor.BLUE, "Justin",
                PlayerColor.GREEN, "Prashant",
                PlayerColor.ORANGE, "David",
                PlayerColor.PINK, "Jonathan"
        );
        RiskGame game = new RiskGame(players, stubbedRandom(0));
        assertEquals(GamePhase.SCRAMBLE, game.getPhase());
    }

    @Test
    public void Constructor_SixPlayers_PhaseIsScramble() {
        Map<PlayerColor, String> players = Map.of(
                PlayerColor.RED, "Jovy",
                PlayerColor.BLUE, "Justin",
                PlayerColor.GREEN, "Prashant",
                PlayerColor.ORANGE, "David",
                PlayerColor.PINK, "Jonathan",
                PlayerColor.CYAN, "Bob"
        );
        RiskGame game = new RiskGame(players, stubbedRandom(0));
        assertEquals(GamePhase.SCRAMBLE, game.getPhase());
    }

    @Test
    public void Constructor_TwoPlayers_ThrowsIllegalArgumentException() {
        Map<PlayerColor, String> players = Map.of(
                PlayerColor.RED, "Jonathan",
                PlayerColor.BLUE, "Justin"
        );
        assertThrows(IllegalArgumentException.class, () -> new RiskGame(players));
    }

    @Test
    public void Constructor_EmptyMap_ThrowsIllegalArgumentException() {
        Map<PlayerColor, String> players = Map.of();
        assertThrows(IllegalArgumentException.class, () -> new RiskGame(players));
    }

    @Test
    public void GetPhase_AllTerritoriesClaimed_ReturnsSetup() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isUnclaimed(EasyMock.anyObject())).andStubReturn(true);
        mockMap.claim(EasyMock.anyObject(), EasyMock.anyObject());
        EasyMock.expectLastCall().times(42);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        TerritoryName[] allTerritories = TerritoryName.values();
        for (int i = 0; i < 42; i++) {
            game.claimTerritory(allTerritories[i]);
        }
        assertEquals(GamePhase.SETUP, game.getPhase());
        EasyMock.verify(mockMap);
    }

    @Test
    public void GetPhase_AllArmiesPlaced_ReturnsAttack() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.anyObject())).andStubReturn(true);
        mockMap.addArmies(EasyMock.anyObject(), EasyMock.anyInt());
        EasyMock.expectLastCall().times(3);
        EasyMock.replay(mockMap);
        Player redPlayer = new Player(PlayerColor.RED, "Jonathan", 1);
        Player bluePlayer = new Player(PlayerColor.BLUE, "Justin", 1);
        Player greenPlayer = new Player(PlayerColor.GREEN, "Prashant", 1);
        game.provideWorldMap(mockMap);
        game.providePlayers(List.of(redPlayer, bluePlayer, greenPlayer));
        game.setPhase(GamePhase.SETUP);
        game.setCurrentPlayer(PlayerColor.RED);
        game.placeArmy(TerritoryName.ALASKA);
        game.placeArmy(TerritoryName.ALASKA);
        game.placeArmy(TerritoryName.ALASKA);
        assertEquals(GamePhase.ATTACK, game.getPhase());
        EasyMock.verify(mockMap);
    }

    @Test
    public void GetCurrentPlayerColor_AtGameStart_ReturnsValidColor() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        PlayerColor current = game.getCurrentPlayerColor();
        assertTrue(current == PlayerColor.RED ||
                    current == PlayerColor.BLUE ||
                    current == PlayerColor.GREEN);
    }

    @Test
    public void GetCurrentPlayerColor_AfterTurnEnds_ReturnsDifferentPlayer() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isUnclaimed(EasyMock.anyObject())).andStubReturn(true);
        mockMap.claim(EasyMock.anyObject(), EasyMock.anyObject());
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        PlayerColor before = game.getCurrentPlayerColor();
        game.claimTerritory(TerritoryName.ALASKA);
        PlayerColor after = game.getCurrentPlayerColor();
        assertNotEquals(before, after);
        EasyMock.verify(mockMap);
    }

    @Test
    public void GetCurrentPlayerName_MatchesCurrentPlayerColor() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals("Justin", game.getCurrentPlayerName());
    }

    @Test
    public void GetArmiesToPlace_ThreePlayers_Returns35() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(35, game.getArmiesToPlace());
    }

    @Test
    public void GetArmiesToPlace_FourPlayers_Returns30() {
        Map<PlayerColor, String> fourPlayers = Map.of(
                PlayerColor.RED, "JOnathan",
                PlayerColor.BLUE, "Justin",
                PlayerColor.GREEN, "Prashant",
                PlayerColor.ORANGE, "David"
        );
        RiskGame game = new RiskGame(fourPlayers, stubbedRandom(0));
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(30, game.getArmiesToPlace());
    }

    @Test
    public void GetArmiesToPlace_FivePlayers_Returns25() {
        Map<PlayerColor, String> fivePlayers = Map.of(
                PlayerColor.RED, "Jonathan",
                PlayerColor.BLUE, "Justin",
                PlayerColor.GREEN, "Prashant",
                PlayerColor.ORANGE, "David",
                PlayerColor.PINK, "Bob"
        );
        RiskGame game = new RiskGame(fivePlayers, stubbedRandom(0));
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(25, game.getArmiesToPlace());
    }

    @Test
    public void GetArmiesToPlace_SixPlayers_Returns20() {
        Map<PlayerColor, String> sixPlayers = Map.of(
                PlayerColor.RED, "Jovy",
                PlayerColor.BLUE, "Justin",
                PlayerColor.GREEN, "Prashant",
                PlayerColor.ORANGE, "David",
                PlayerColor.PINK, "Jonathan",
                PlayerColor.CYAN, "Bob"
        );
        RiskGame game = new RiskGame(sixPlayers, stubbedRandom(0));
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(20, game.getArmiesToPlace());
    }

    @Test
    public void GetArmiesToPlace_AfterClaimingOneTerritory_Returns34() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setCurrentPlayer(PlayerColor.RED);
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isUnclaimed(EasyMock.anyObject())).andStubReturn(true);
        mockMap.claim(EasyMock.anyObject(), EasyMock.anyObject());
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.claimTerritory(TerritoryName.ALASKA);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(34, game.getArmiesToPlace());
        EasyMock.verify(mockMap);
    }

    @Test
    public void IsSetupComplete_AtGameStart_ReturnsFalse() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        assertFalse(game.isSetupComplete());
    }

    @Test
    public void IsSetupComplete_AllArmiesPlaced_ReturnsTrue() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        Player redPlayer = new Player(PlayerColor.RED, "Jonathan", 0);
        Player bluePlayer = new Player(PlayerColor.BLUE, "Justin", 0);
        Player greenPlayer = new Player(PlayerColor.GREEN, "Prashant", 0);
        game.providePlayers(List.of(redPlayer, bluePlayer, greenPlayer));
        assertTrue(game.isSetupComplete());
    }

    @Test
    public void ClaimTerritory_UnclaimedTerritoryDuringScramble_ClaimsAndAdvancesTurn() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setCurrentPlayer(PlayerColor.RED);
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isUnclaimed(EasyMock.anyObject())).andStubReturn(true);
        mockMap.claim(TerritoryName.ALASKA, PlayerColor.RED);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        PlayerColor before = game.getCurrentPlayerColor();
        game.claimTerritory(TerritoryName.ALASKA);
        assertNotEquals(before, game.getCurrentPlayerColor());
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(34, game.getArmiesToPlace());
        EasyMock.verify(mockMap);
    }

    @Test
    public void ClaimTerritory_WrongPhase_ThrowsIllegalStateException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.SETUP);
        assertThrows(IllegalStateException.class, () -> game.claimTerritory(TerritoryName.ALASKA));
    }

    @Test
    public void ClaimTerritory_AlreadyClaimedTerritory_ThrowsIllegalStateException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isUnclaimed(EasyMock.anyObject())).andStubReturn(true);
        mockMap.claim(EasyMock.anyObject(), EasyMock.anyObject());
        EasyMock.expectLastCall().andThrow(new IllegalStateException());
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        assertThrows(IllegalStateException.class, () -> game.claimTerritory(TerritoryName.ALASKA));
        EasyMock.verify(mockMap);
    }

    @Test
    public void PlaceArmy_OwnedTerritoryDuringSetup_PlacesArmyAndAdvancesTurn() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.SETUP);
        game.setCurrentPlayer(PlayerColor.RED);
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        mockMap.addArmies(TerritoryName.ALASKA, 1);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        PlayerColor before = game.getCurrentPlayerColor();
        game.placeArmy(TerritoryName.ALASKA);
        assertNotEquals(before, game.getCurrentPlayerColor());
        EasyMock.verify(mockMap);
    }

    @Test
    public void PlaceArmy_WrongPhase_ThrowsIllegalStateException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        assertThrows(IllegalStateException.class, () -> game.placeArmy(TerritoryName.ALASKA));
    }

    @Test
    public void PlaceArmy_TerritoryNotOwnedByCurrentPlayer_ThrowsIllegalArgumentException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.SETUP);
        game.setCurrentPlayer(PlayerColor.RED);
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(false);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        assertThrows(IllegalArgumentException.class, () -> game.placeArmy(TerritoryName.ALASKA));
    }
}