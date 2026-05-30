package domain;

import org.junit.jupiter.api.Test;
import org.easymock.EasyMock;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.LinkedHashMap;
import static org.junit.jupiter.api.Assertions.*;

public class RiskGameTests {

    private Map<PlayerColor, String> threePlayerMap() {
        Map<PlayerColor, String> players = new LinkedHashMap<>();
        players.put(PlayerColor.RED, "Jonathan");
        players.put(PlayerColor.BLUE, "Justin");
        players.put(PlayerColor.GREEN, "Prashant");
        return players;
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
        mockMap.claim(EasyMock.anyObject(), EasyMock.anyObject());
        EasyMock.expectLastCall().times(42);
        mockMap.addArmies(EasyMock.anyObject(), EasyMock.anyInt());
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
    public void GetCurrentPlayerColor_AtGameStart_WithRandomZero_ReturnsRed() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        assertEquals(PlayerColor.RED, game.getCurrentPlayerColor());
    }

    @Test
    public void GetCurrentPlayerColor_AfterTurnEnds_ReturnsDifferentPlayer() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        mockMap.claim(EasyMock.anyObject(), EasyMock.anyObject());
        EasyMock.expectLastCall();
        mockMap.addArmies(EasyMock.anyObject(), EasyMock.anyInt());
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
        assertEquals("Jonathan", game.getCurrentPlayerName());
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
        mockMap.claim(EasyMock.anyObject(), EasyMock.anyObject());
        EasyMock.expectLastCall();
        mockMap.addArmies(EasyMock.anyObject(), EasyMock.anyInt());
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
        mockMap.claim(TerritoryName.ALASKA, PlayerColor.RED);
        EasyMock.expectLastCall();
        mockMap.addArmies(TerritoryName.ALASKA, 1);
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

    @Test
    public void PlaceArmy_NoArmiesLeft_ThrowsIllegalArgumentException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.SETUP);
        Player redPlayer = new Player(PlayerColor.RED, "Jonathan", 0);
        Player bluePlayer = new Player(PlayerColor.BLUE, "Justin", 0);
        Player greenPlayer = new Player(PlayerColor.GREEN, "Prashant", 0);
        game.providePlayers(List.of(redPlayer, bluePlayer, greenPlayer));
        game.setCurrentPlayer(PlayerColor.RED);
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        assertThrows(IllegalArgumentException.class, () -> game.placeArmy(TerritoryName.ALASKA));
    }

    @Test
    public void PlaceArmy_LastArmyPlaced_TransitionsToAttackAndSetupComplete() {
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
        assertTrue(game.isSetupComplete());
        EasyMock.verify(mockMap);
    }

    @Test
    public void ClaimTerritory_UnclaimedTerritory_AddsOneArmy() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setCurrentPlayer(PlayerColor.RED);
        game.claimTerritory(TerritoryName.ALASKA);
        assertEquals(1, game.getArmies(TerritoryName.ALASKA));
    }

    @Test
    public void ClaimTerritory_UnclaimedTerritory_TerritoryOwnedByCurrentPlayer() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setCurrentPlayer(PlayerColor.RED);
        game.claimTerritory(TerritoryName.ALASKA);
        assertTrue(game.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED));
    }

    @Test
    public void PlaceArmy_OwnedTerritory_ArmyCountIncreases() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setCurrentPlayer(PlayerColor.RED);
        game.claimTerritory(TerritoryName.ALASKA);
        game.setPhase(GamePhase.SETUP);
        game.setCurrentPlayer(PlayerColor.RED);
        int before = game.getArmies(TerritoryName.ALASKA);
        game.placeArmy(TerritoryName.ALASKA);
        assertEquals(before + 1, game.getArmies(TerritoryName.ALASKA));
    }

    @Test
    public void PlaceArmy_SkipsPlayerWithNoArmies_AdvancesToNextPlayerWithArmies() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        Player redPlayer = new Player(PlayerColor.RED, "Jonathan", 1);
        Player bluePlayer = new Player(PlayerColor.BLUE, "Justin", 0);
        Player greenPlayer = new Player(PlayerColor.GREEN, "Prashant", 1);
        game.providePlayers(List.of(redPlayer, bluePlayer, greenPlayer));
        game.setPhase(GamePhase.SETUP);
        game.setCurrentPlayer(PlayerColor.RED);
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.anyObject())).andStubReturn(true);
        mockMap.addArmies(EasyMock.anyObject(), EasyMock.anyInt());
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.placeArmy(TerritoryName.ALASKA);
        assertEquals(PlayerColor.GREEN, game.getCurrentPlayerColor());
        EasyMock.verify(mockMap);
    }

    @Test
    public void GetDraftArmies_PlayerOwnsOneTerritory_ReturnsThree() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(1);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(3, game.getDraftArmies());
        EasyMock.verify(mockMap);
    }

    @Test
    public void GetDraftArmies_PlayerOwnsElevenTerritories_ReturnsThree() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(11);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(3, game.getDraftArmies());
        EasyMock.verify(mockMap);
    }

    @Test
    public void GetDraftArmies_PlayerOwnsTwelveTerritories_ReturnsFour() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(12);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(4, game.getDraftArmies());
        EasyMock.verify(mockMap);
    }

    @Test
    public void GetDraftArmies_PlayerOwnsAllFortyTwoTerritories_ReturnsFourteen() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(42);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(14, game.getDraftArmies());
        EasyMock.verify(mockMap);
    }

    @Test
    public void DraftArmy_OwnedTerritoryDuringAttack_PlacesArmyAndDecrementsDraft() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(1);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        mockMap.addArmies(TerritoryName.ALASKA, 1);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.draftArmy(TerritoryName.ALASKA);
        assertEquals(2, game.getDraftArmies());
        EasyMock.verify(mockMap);
    }

    @Test
    public void DraftArmy_LastDraftArmy_IsDraftCompleteReturnsTrue() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(1);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        mockMap.addArmies(TerritoryName.ALASKA, 1);
        EasyMock.expectLastCall().times(3);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.draftArmy(TerritoryName.ALASKA);
        game.draftArmy(TerritoryName.ALASKA);
        game.draftArmy(TerritoryName.ALASKA);
        assertTrue(game.isDraftComplete());
        EasyMock.verify(mockMap);
    }

    @Test
    public void DraftArmy_WrongPhase_ThrowsIllegalStateException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.FORTIFY);
        assertThrows(IllegalStateException.class, () -> game.draftArmy(TerritoryName.ALASKA));
    }

    @Test
    public void DraftArmy_TerritoryNotOwnedByCurrentPlayer_ThrowsIllegalArgumentException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(1);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(false);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalArgumentException.class, () -> game.draftArmy(TerritoryName.ALASKA));
    }

    @Test
    public void DraftArmy_NoArmiesRemain_ThrowsIllegalArgumentException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(1);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        mockMap.addArmies(TerritoryName.ALASKA, 1);
        EasyMock.expectLastCall().times(3);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.draftArmy(TerritoryName.ALASKA);
        game.draftArmy(TerritoryName.ALASKA);
        game.draftArmy(TerritoryName.ALASKA);
        assertThrows(IllegalArgumentException.class, () -> game.draftArmy(TerritoryName.ALASKA));
    }

    @Test
    public void EndAttack_DuringAttackPhase_TransitionsToFortify() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.ATTACK);
        game.endAttack();
        assertEquals(GamePhase.FORTIFY, game.getPhase());
    }

    @Test
    public void EndAttack_WrongPhase_ThrowsIllegalStateException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.FORTIFY);
        assertThrows(IllegalStateException.class, () -> game.endAttack());
    }

    @Test
    public void Fortify_OneArmyBetweenAdjacentOwnedTerritories_MovesArmy() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(3);
        mockMap.removeArmies(TerritoryName.ALASKA, 1);
        EasyMock.expectLastCall();
        mockMap.addArmies(TerritoryName.ALBERTA, 1);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        game.fortify(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1);
        EasyMock.verify(mockMap);
    }

    @Test
    public void Fortify_MaxArmiesLeavesOneBehind_MovesAllButOne() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(5);
        mockMap.removeArmies(TerritoryName.ALASKA, 4);
        EasyMock.expectLastCall();
        mockMap.addArmies(TerritoryName.ALBERTA, 4);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        game.fortify(TerritoryName.ALASKA, TerritoryName.ALBERTA, 4);
        EasyMock.verify(mockMap);
    }

    @Test
    public void Fortify_WrongPhase_ThrowsIllegalStateException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.ATTACK);
        assertThrows(IllegalStateException.class,
                () -> game.fortify(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void Fortify_FromTerritoryNotOwnedByCurrentPlayer_ThrowsIllegalArgumentException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(false);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalArgumentException.class,
                () -> game.fortify(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void Fortify_ToTerritoryNotOwnedByCurrentPlayer_ThrowsIllegalArgumentException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(false);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalArgumentException.class,
                () -> game.fortify(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void Fortify_NonAdjacentTerritories_ThrowsIllegalArgumentException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.BRAZIL, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.areNeighbors(TerritoryName.ALASKA, TerritoryName.BRAZIL)).andStubReturn(false);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalArgumentException.class,
                () -> game.fortify(TerritoryName.ALASKA, TerritoryName.BRAZIL, 1));
    }

    @Test
    public void Fortify_ZeroArmies_ThrowsIllegalArgumentException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(3);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalArgumentException.class,
                () -> game.fortify(TerritoryName.ALASKA, TerritoryName.ALBERTA, 0));
    }

    @Test
    public void Fortify_AllArmiesNoneLeftBehind_ThrowsIllegalArgumentException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(3);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalArgumentException.class,
                () -> game.fortify(TerritoryName.ALASKA, TerritoryName.ALBERTA, 3));
    }

    private RiskGame attackReadyGame(WorldMap mockMap) {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        return game;
    }

    @Test
    public void Attack_WrongPhase_ThrowsIllegalStateException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.FORTIFY);
        assertThrows(IllegalStateException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void Attack_BeforeDraftComplete_ThrowsIllegalStateException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(1);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        mockMap.addArmies(TerritoryName.ALASKA, 1);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.draftArmy(TerritoryName.ALASKA);
        assertFalse(game.isDraftComplete());
        assertThrows(IllegalStateException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void Attack_FromTerritoryNotOwnedByCurrentPlayer_ThrowsIllegalArgumentException() {
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(false);
        EasyMock.replay(mockMap);
        RiskGame game = attackReadyGame(mockMap);
        assertThrows(IllegalArgumentException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void Attack_TerritoryOwnedByCurrentPlayer_ThrowsIllegalArgumentException() {
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.replay(mockMap);
        RiskGame game = attackReadyGame(mockMap);
        assertThrows(IllegalArgumentException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void Attack_NonAdjacentTerritory_ThrowsIllegalArgumentException() {
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.BRAZIL, PlayerColor.RED)).andStubReturn(false);
        EasyMock.expect(mockMap.areNeighbors(TerritoryName.ALASKA, TerritoryName.BRAZIL)).andStubReturn(false);
        EasyMock.replay(mockMap);
        RiskGame game = attackReadyGame(mockMap);
        assertThrows(IllegalArgumentException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.BRAZIL, 1));
    }

    @Test
    public void Attack_ZeroAttackers_ThrowsIllegalArgumentException() {
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(false);
        EasyMock.expect(mockMap.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(2);
        EasyMock.replay(mockMap);
        RiskGame game = attackReadyGame(mockMap);
        assertThrows(IllegalArgumentException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA, 0));
    }

    @Test
    public void Attack_FourAttackersExceedsMax_ThrowsIllegalArgumentException() {
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(false);
        EasyMock.expect(mockMap.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(5);
        EasyMock.replay(mockMap);
        RiskGame game = attackReadyGame(mockMap);
        assertThrows(IllegalArgumentException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA, 4));
    }

    @Test
    public void Attack_FromTerritoryWithOnlyOneArmy_ThrowsIllegalArgumentException() {
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(false);
        EasyMock.expect(mockMap.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(1);
        EasyMock.replay(mockMap);
        RiskGame game = attackReadyGame(mockMap);
        assertThrows(IllegalArgumentException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void Attack_NumAttackersEqualsFromArmies_ThrowsIllegalArgumentException() {
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(false);
        EasyMock.expect(mockMap.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(2);
        EasyMock.replay(mockMap);
        RiskGame game = attackReadyGame(mockMap);
        assertThrows(IllegalArgumentException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA, 2));
    }

    /** Builds a Random mock that returns the given sequence from nextInt(). */
    private Random scriptedDice(int... rolls) {
        Random rand = EasyMock.createMock(Random.class);
        for (int roll : rolls) {
            EasyMock.expect(rand.nextInt(EasyMock.anyInt())).andReturn(roll);
        }
        EasyMock.replay(rand);
        return rand;
    }

    @Test
    public void Attack_OneAttackerExecutes_DiceRolledAndArmiesAdjusted() {
        // TC43: defender wins all (attacker rolls 1, defender rolls 6 -> defender beats attacker)
        // First scripted value (0) is consumed by RiskGame constructor for starting player.
        RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 0, 5));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(false);
        EasyMock.expect(mockMap.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(2);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALBERTA)).andStubReturn(1);
        mockMap.removeArmies(TerritoryName.ALASKA, 1);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1);
        EasyMock.verify(mockMap);
    }

    @Test
    public void Attack_ThreeAttackersExecutes_DiceRolledAndBothSidesLoseArmies() {
        // TC44: attacker rolls (6,5,4), defender rolls (6,1)
        // Pair 1: 6 vs 6 -> tie, defender wins -> attacker -1
        // Pair 2: 5 vs 1 -> attacker wins -> defender -1
        // First scripted value (0) is consumed by RiskGame constructor for starting player.
        RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 4, 3, 5, 0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(false);
        EasyMock.expect(mockMap.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(4);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALBERTA)).andStubReturn(2);
        mockMap.removeArmies(TerritoryName.ALASKA, 1);
        EasyMock.expectLastCall();
        mockMap.removeArmies(TerritoryName.ALBERTA, 1);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA, 3);
        EasyMock.verify(mockMap);
    }

    @Test
    public void Attack_AttackerWinsAllDice_CapturesTerritory() {
        // TC54: 2 attackers vs 1 defender, attacker wins all dice.
        // First scripted value (0) consumed by constructor.
        // Attacker rolls 2 dice (values 6, 5), defender rolls 1 die (value 1).
        // Pair 1: 6 vs 1 -> defender loses (-1). Defender at 0 -> captured.
        // Captured: ownership transferred, attacker moves armies = numAttackers (2) into ALBERTA.
        RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 4, 0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(false);
        EasyMock.expect(mockMap.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(3);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALBERTA)).andStubReturn(1);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(2);
        mockMap.removeArmies(TerritoryName.ALBERTA, 1);
        EasyMock.expectLastCall();
        mockMap.claim(TerritoryName.ALBERTA, PlayerColor.RED);
        EasyMock.expectLastCall();
        mockMap.removeArmies(TerritoryName.ALASKA, 2);
        EasyMock.expectLastCall();
        mockMap.addArmies(TerritoryName.ALBERTA, 2);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA, 2);
        EasyMock.verify(mockMap);
    }

    @Test
    public void Attack_DefenderWinsAllDice_AttackerLosesArmies() {
        // TC55: 2 attackers vs defender with 2 armies, defender wins all.
        // First scripted value (0) consumed by constructor.
        // Attacker rolls 2 dice (values 1, 1), defender rolls 2 dice (values 6, 6).
        // Pair 1: 1 vs 6 -> attacker -1. Pair 2: 1 vs 6 -> attacker -1. Total attacker -2.
        RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 0, 0, 5, 5));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(false);
        EasyMock.expect(mockMap.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(3);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALBERTA)).andStubReturn(2);
        mockMap.removeArmies(TerritoryName.ALASKA, 2);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA, 2);
        EasyMock.verify(mockMap);
    }

    @Test
    public void EndTurn_DuringFortifyPhase_TransitionsToAttackAndAdvancesPlayer() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        PlayerColor before = game.getCurrentPlayerColor();
        game.endTurn();
        assertEquals(GamePhase.ATTACK, game.getPhase());
        assertNotEquals(before, game.getCurrentPlayerColor());
    }

    @Test
    public void EndTurn_WrongPhase_ThrowsIllegalStateException() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.ATTACK);
        assertThrows(IllegalStateException.class, () -> game.endTurn());
    }

    @Test
    public void EndTurn_NextPlayerOwnsTwelveTerritories_GetDraftArmiesReturnsFour() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.BLUE)).andStubReturn(12);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        game.endTurn();
        assertEquals(PlayerColor.BLUE, game.getCurrentPlayerColor());
        assertEquals(4, game.getDraftArmies());
        EasyMock.verify(mockMap);
    }

    @Test
    public void GetWinner_TerritoriesDistributedAmongMultiplePlayers_ReturnsNull() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(14);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.BLUE)).andStubReturn(14);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.GREEN)).andStubReturn(14);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        assertNull(game.getWinner());
        EasyMock.verify(mockMap);
    }

    @Test
    public void GetWinner_OnePlayerOwnsAllFortyTwoTerritories_ReturnsThatPlayer() {
        RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(42);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.BLUE)).andStubReturn(0);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.GREEN)).andStubReturn(0);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        assertEquals(PlayerColor.RED, game.getWinner());
        EasyMock.verify(mockMap);
    }
}