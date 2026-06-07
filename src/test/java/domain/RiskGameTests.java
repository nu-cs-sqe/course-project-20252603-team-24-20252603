package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

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

    private Set<TerritoryName> territories(TerritoryName... territories) {
        return Set.of(territories);
    }

    @Test
    public void Constructor_ThreePlayers_PhaseIsScramble() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
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
        final RiskGame game = new RiskGame(players, stubbedRandom(0));
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
        final RiskGame game = new RiskGame(players, stubbedRandom(0));
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
        final RiskGame game = new RiskGame(players, stubbedRandom(0));
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
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
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
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.anyObject()))
                .andStubReturn(true);
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
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        assertEquals(PlayerColor.RED, game.getCurrentPlayerColor());
    }

    @Test
    public void GetCurrentPlayerColor_AfterTurnEnds_ReturnsDifferentPlayer() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
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
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals("Jonathan", game.getCurrentPlayerName());
    }

    @Test
    public void GetArmiesToPlace_ThreePlayers_Returns35() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
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
        final RiskGame game = new RiskGame(fourPlayers, stubbedRandom(0));
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
        final RiskGame game = new RiskGame(fivePlayers, stubbedRandom(0));
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
        final RiskGame game = new RiskGame(sixPlayers, stubbedRandom(0));
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(20, game.getArmiesToPlace());
    }

    @Test
    public void GetArmiesToPlace_AfterClaimingOneTerritory_Returns34() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
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
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        assertFalse(game.isSetupComplete());
    }

    @Test
    public void IsSetupComplete_AllArmiesPlaced_ReturnsTrue() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        Player redPlayer = new Player(PlayerColor.RED, "Jonathan", 0);
        Player bluePlayer = new Player(PlayerColor.BLUE, "Justin", 0);
        Player greenPlayer = new Player(PlayerColor.GREEN, "Prashant", 0);
        game.providePlayers(List.of(redPlayer, bluePlayer, greenPlayer));
        assertTrue(game.isSetupComplete());
    }

    @Test
    public void ClaimTerritory_UnclaimedTerritoryDuringScramble_ClaimsAndAdvancesTurn() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
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
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.SETUP);
        assertThrows(IllegalStateException.class, () -> game.claimTerritory(TerritoryName.ALASKA));
    }

    @Test
    public void ClaimTerritory_AlreadyClaimedTerritory_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
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
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.SETUP);
        game.setCurrentPlayer(PlayerColor.RED);
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
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
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        assertThrows(IllegalStateException.class, () -> game.placeArmy(TerritoryName.ALASKA));
    }

    @Test
    public void PlaceArmy_TerritoryNotOwnedByCurrentPlayer_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.SETUP);
        game.setCurrentPlayer(PlayerColor.RED);
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        assertThrows(IllegalArgumentException.class, () -> game.placeArmy(TerritoryName.ALASKA));
    }

    @Test
    public void PlaceArmy_NoArmiesLeft_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.SETUP);
        Player redPlayer = new Player(PlayerColor.RED, "Jonathan", 0);
        Player bluePlayer = new Player(PlayerColor.BLUE, "Justin", 0);
        Player greenPlayer = new Player(PlayerColor.GREEN, "Prashant", 0);
        game.providePlayers(List.of(redPlayer, bluePlayer, greenPlayer));
        game.setCurrentPlayer(PlayerColor.RED);
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        assertThrows(IllegalArgumentException.class, () -> game.placeArmy(TerritoryName.ALASKA));
    }

    @Test
    public void PlaceArmy_LastArmyPlaced_TransitionsToAttackAndSetupComplete() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.anyObject()))
                .andStubReturn(true);
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
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setCurrentPlayer(PlayerColor.RED);
        game.claimTerritory(TerritoryName.ALASKA);
        assertEquals(1, game.getArmies(TerritoryName.ALASKA));
    }

    @Test
    public void ClaimTerritory_UnclaimedTerritory_TerritoryOwnedByCurrentPlayer() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setCurrentPlayer(PlayerColor.RED);
        game.claimTerritory(TerritoryName.ALASKA);
        assertTrue(game.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED));
    }

    @Test
    public void PlaceArmy_OwnedTerritory_ArmyCountIncreases() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
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
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        Player redPlayer = new Player(PlayerColor.RED, "Jonathan", 1);
        Player bluePlayer = new Player(PlayerColor.BLUE, "Justin", 0);
        Player greenPlayer = new Player(PlayerColor.GREEN, "Prashant", 1);
        game.providePlayers(List.of(redPlayer, bluePlayer, greenPlayer));
        game.setPhase(GamePhase.SETUP);
        game.setCurrentPlayer(PlayerColor.RED);
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.anyObject()))
                .andStubReturn(true);
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
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(1);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(3, game.getDraftArmies());
        EasyMock.verify(mockMap);
    }

    @Test
    public void GetDraftArmies_PlayerOwnsElevenTerritories_ReturnsThree() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(11);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(3, game.getDraftArmies());
        EasyMock.verify(mockMap);
    }

    @Test
    public void GetDraftArmies_PlayerOwnsTwelveTerritories_ReturnsFour() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(12);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(4, game.getDraftArmies());
        EasyMock.verify(mockMap);
    }

    @Test
    public void GetDraftArmies_PlayerOwnsAllFortyTwoTerritories_ReturnsThirtyEight() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(42);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories(TerritoryName.values()));
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(38, game.getDraftArmies());
        EasyMock.verify(mockMap);
    }

    @Test
    public void GetDraftArmies_NoCompleteContinent_UsesOnlyTerritoryCount() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setupTerritory(TerritoryName.ALASKA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.ALBERTA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.ONTARIO, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.QUEBEC, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.VENEZUELA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.PERU, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.BRAZIL, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.ICELAND, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.GREAT_BRITAIN, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.NORTH_AFRICA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.EGYPT, PlayerColor.RED, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(3, game.getDraftArmies());
    }

    @Test
    public void GetDraftArmies_WithSouthAmericaBonus_ReturnsFive() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setupTerritory(TerritoryName.VENEZUELA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.PERU, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.BRAZIL, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.ARGENTINA, PlayerColor.RED, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(5, game.getDraftArmies());
    }

    @Test
    public void GetDraftArmies_WithAustraliaBonus_ReturnsFive() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setupTerritory(TerritoryName.EASTERN_AUSTRALIA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.WESTERN_AUSTRALIA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.NEW_GUINEA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.INDONESIA, PlayerColor.RED, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(5, game.getDraftArmies());
    }

    @Test
    public void GetDraftArmies_WithAfricaBonus_ReturnsSix() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setupTerritory(TerritoryName.NORTH_AFRICA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.EGYPT, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.EAST_AFRICA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.CONGO, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.SOUTH_AFRICA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.MADAGASCAR, PlayerColor.RED, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(6, game.getDraftArmies());
    }

    @Test
    public void GetDraftArmies_WithNorthAmericaBonus_ReturnsEight() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setupTerritory(TerritoryName.ALASKA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.NORTHWEST_TERRITORY, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.GREENLAND, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.ALBERTA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.ONTARIO, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.QUEBEC, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.WESTERN_UNITED_STATES, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.EASTERN_UNITED_STATES, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.CENTRAL_AMERICA, PlayerColor.RED, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(8, game.getDraftArmies());
    }

    @Test
    public void GetDraftArmies_WithEuropeBonus_ReturnsEight() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setupTerritory(TerritoryName.ICELAND, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.GREAT_BRITAIN, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.WESTERN_EUROPE, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.NORTHERN_EUROPE, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.SOUTHERN_EUROPE, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.SCANDINAVIA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.UKRAINE, PlayerColor.RED, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(8, game.getDraftArmies());
    }

    @Test
    public void GetDraftArmies_WithAsiaBonus_ReturnsEleven() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setupTerritory(TerritoryName.MIDDLE_EAST, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.AFGHANISTAN, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.URAL, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.SIBERIA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.YAKUTSK, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.KAMCHATKA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.IRKUTSK, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.MONGOLIA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.JAPAN, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.CHINA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.INDIA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.SIAM, PlayerColor.RED, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(11, game.getDraftArmies());
    }

    @Test
    public void GetDraftArmies_WithSouthAmericaAndAustraliaBonuses_ReturnsSeven() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setupTerritory(TerritoryName.VENEZUELA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.PERU, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.BRAZIL, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.ARGENTINA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.EASTERN_AUSTRALIA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.WESTERN_AUSTRALIA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.NEW_GUINEA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.INDONESIA, PlayerColor.RED, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(7, game.getDraftArmies());
    }

    @Test
    public void GetDraftArmies_WithPartialContinent_ReturnsThree() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setupTerritory(TerritoryName.VENEZUELA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.PERU, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.BRAZIL, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.ARGENTINA, PlayerColor.BLUE, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(3, game.getDraftArmies());
    }

    @Test
    public void DraftArmy_OwnedTerritoryDuringAttack_PlacesArmyAndDecrementsDraft() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(1);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
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
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(1);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
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
    public void DraftArmy_WithSouthAmericaBonus_DraftsFiveArmiesBeforeComplete() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setupTerritory(TerritoryName.VENEZUELA, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.PERU, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.BRAZIL, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.ARGENTINA, PlayerColor.RED, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        for (int i = 0; i < 5; i++) {
            game.draftArmy(TerritoryName.VENEZUELA);
        }
        assertTrue(game.isDraftComplete());
    }

    @Test
    public void DraftArmy_WrongPhase_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.FORTIFY);
        assertThrows(IllegalStateException.class, () -> game.draftArmy(TerritoryName.ALASKA));
    }

    @Test
    public void DraftArmy_TerritoryNotOwnedByCurrentPlayer_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(1);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalArgumentException.class, () -> game.draftArmy(TerritoryName.ALASKA));
    }

    @Test
    public void DraftArmy_NoArmiesRemain_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(1);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
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
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.ATTACK);
        game.setDraftComplete();
        game.endAttack();
        assertEquals(GamePhase.FORTIFY, game.getPhase());
    }

    @Test
    public void EndAttack_WrongPhase_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.FORTIFY);
        assertThrows(IllegalStateException.class, () -> game.endAttack());
    }

    @Test
    public void Fortify_NonAdjacentButConnectedThroughOwnedChain_Succeeds() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setupTerritory(TerritoryName.ALASKA, PlayerColor.RED, 3);
        game.setupTerritory(TerritoryName.NORTHWEST_TERRITORY, PlayerColor.RED, 1);
        game.setupTerritory(TerritoryName.ONTARIO, PlayerColor.RED, 1);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        game.fortify(TerritoryName.ALASKA, TerritoryName.ONTARIO, 1);
        assertEquals(2, game.getArmies(TerritoryName.ALASKA));
        assertEquals(2, game.getArmies(TerritoryName.ONTARIO));
    }

    @Test
    public void Fortify_OneArmyBetweenAdjacentOwnedTerritories_MovesArmy() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.areConnectedThrough(
                TerritoryName.ALASKA, TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(true);
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
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.areConnectedThrough(
                TerritoryName.ALASKA, TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(true);
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
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.ATTACK);
        assertThrows(IllegalStateException.class,
                () -> game.fortify(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void Fortify_FromTerritoryNotOwnedByCurrentPlayer_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalArgumentException.class,
                () -> game.fortify(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void Fortify_ToTerritoryNotOwnedByCurrentPlayer_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalArgumentException.class,
                () -> game.fortify(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void Fortify_NonAdjacentTerritories_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.BRAZIL, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.areConnectedThrough(
                TerritoryName.ALASKA, TerritoryName.BRAZIL, PlayerColor.RED)).andStubReturn(false);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalArgumentException.class,
                () -> game.fortify(TerritoryName.ALASKA, TerritoryName.BRAZIL, 1));
    }

    @Test
    public void Fortify_ZeroArmies_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.areConnectedThrough(
                TerritoryName.ALASKA, TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(true);
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
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.areConnectedThrough(
                TerritoryName.ALASKA, TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(3);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalArgumentException.class,
                () -> game.fortify(TerritoryName.ALASKA, TerritoryName.ALBERTA, 3));
    }

    private RiskGame attackReadyGame(WorldMap mockMap) {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        return game;
    }

    @Test
    public void Attack_WrongPhase_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.FORTIFY);
        assertThrows(IllegalStateException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA));
    }

    @Test
    public void Attack_BeforeDraftComplete_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(1);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        mockMap.addArmies(TerritoryName.ALASKA, 1);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.draftArmy(TerritoryName.ALASKA);
        assertFalse(game.isDraftComplete());
        assertThrows(IllegalStateException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA));
    }

    @Test
    public void Attack_FromTerritoryNotOwnedByCurrentPlayer_ThrowsIllegalArgumentException() {
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.replay(mockMap);
        RiskGame game = attackReadyGame(mockMap);
        assertThrows(IllegalArgumentException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA));
    }

    @Test
    public void Attack_TerritoryOwnedByCurrentPlayer_ThrowsIllegalArgumentException() {
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.replay(mockMap);
        RiskGame game = attackReadyGame(mockMap);
        assertThrows(IllegalArgumentException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA));
    }

    @Test
    public void Attack_NonAdjacentTerritory_ThrowsIllegalArgumentException() {
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.BRAZIL, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.expect(mockMap.areNeighbors(TerritoryName.ALASKA, TerritoryName.BRAZIL))
                .andStubReturn(false);
        EasyMock.replay(mockMap);
        RiskGame game = attackReadyGame(mockMap);
        assertThrows(IllegalArgumentException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.BRAZIL));
    }

    @Test
    public void Attack_FromTerritoryWithOnlyOneArmy_ThrowsIllegalArgumentException() {
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.expect(mockMap.areNeighbors(
                TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(1);
        EasyMock.replay(mockMap);
        RiskGame game = attackReadyGame(mockMap);
        assertThrows(IllegalArgumentException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA));
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
    public void Attack_AttackerWinsAllDice_CapturesTerritory() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 4, 0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.BLUE))
                .andStubReturn(true);
        EasyMock.expect(mockMap.areNeighbors(
                TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(3);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALBERTA))
                .andReturn(1).andReturn(1).andReturn(0).andReturn(0);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(2);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.BLUE)).andStubReturn(20);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.GREEN)).andStubReturn(20);
        mockMap.removeArmies(TerritoryName.ALBERTA, 1);
        EasyMock.expectLastCall();
        mockMap.assignTerritory(TerritoryName.ALBERTA, PlayerColor.RED);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        EasyMock.verify(mockMap);
    }

    @Test
    public void Attack_DefenderWinsAllDice_AttackerLosesArmies() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 0, 0, 5, 5));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.expect(mockMap.areNeighbors(
                TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA))
                .andReturn(3).andReturn(3).andReturn(3).andReturn(1);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALBERTA)).andStubReturn(2);
        mockMap.removeArmies(TerritoryName.ALASKA, 1);
        EasyMock.expectLastCall().times(2);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        EasyMock.verify(mockMap);
    }

    @Test
    public void Attack_AttackerDiceRolledOutOfOrder_SortsDescendingBeforeComparing() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 0, 5, 2, 3));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.BLUE))
                .andStubReturn(true);
        EasyMock.expect(mockMap.areNeighbors(
                TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(4);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALBERTA))
                .andReturn(1).andReturn(1).andReturn(0).andReturn(0);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(2);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.BLUE)).andStubReturn(20);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.GREEN)).andStubReturn(20);
        mockMap.removeArmies(TerritoryName.ALBERTA, 1);
        EasyMock.expectLastCall();
        mockMap.assignTerritory(TerritoryName.ALBERTA, PlayerColor.RED);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        EasyMock.verify(mockMap);
    }

    @Test
    public void EndTurn_DuringFortifyPhase_TransitionsToAttackAndAdvancesPlayer() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        PlayerColor before = game.getCurrentPlayerColor();
        game.endTurn();
        assertEquals(GamePhase.ATTACK, game.getPhase());
        assertNotEquals(before, game.getCurrentPlayerColor());
    }

    @Test
    public void EndTurn_WrongPhase_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.ATTACK);
        assertThrows(IllegalStateException.class, () -> game.endTurn());
    }

    @Test
    public void EndTurn_NextPlayerOwnsTwelveTerritories_GetDraftArmiesReturnsFour() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.BLUE)).andStubReturn(12);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.BLUE))
                .andStubReturn(territories());
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
    public void EndTurn_NextPlayerHasNoTerritories_SkipsToActivePlayer() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.BLUE)).andStubReturn(0);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.GREEN)).andStubReturn(1);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        game.endTurn();
        assertEquals(GamePhase.ATTACK, game.getPhase());
        assertEquals(PlayerColor.GREEN, game.getCurrentPlayerColor());
        EasyMock.verify(mockMap);
    }

    @Test
    public void EndTurn_LastPlayerWithFirstPlayerActive_WrapsToFirstActivePlayer() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(1);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.GREEN);
        game.endTurn();
        assertEquals(GamePhase.ATTACK, game.getPhase());
        assertEquals(PlayerColor.RED, game.getCurrentPlayerColor());
        EasyMock.verify(mockMap);
    }

    @Test
    public void EndTurn_MultiplePlayersHaveNoTerritories_SkipsToNextActivePlayer() {
        Map<PlayerColor, String> players = new LinkedHashMap<>();
        players.put(PlayerColor.RED, "Jonathan");
        players.put(PlayerColor.BLUE, "Justin");
        players.put(PlayerColor.GREEN, "Prashant");
        players.put(PlayerColor.ORANGE, "David");
        final RiskGame game = new RiskGame(players, stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.BLUE)).andStubReturn(0);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.GREEN)).andStubReturn(0);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.ORANGE)).andStubReturn(1);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        game.endTurn();
        assertEquals(GamePhase.ATTACK, game.getPhase());
        assertEquals(PlayerColor.ORANGE, game.getCurrentPlayerColor());
        EasyMock.verify(mockMap);
    }

    @Test
    public void EndTurn_OnlyCurrentPlayerOwnsTerritories_RemainsActivePlayer() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(1);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.BLUE)).andStubReturn(0);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.GREEN)).andStubReturn(0);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        game.endTurn();
        assertEquals(GamePhase.ATTACK, game.getPhase());
        assertEquals(PlayerColor.RED, game.getCurrentPlayerColor());
        EasyMock.verify(mockMap);
    }

    @Test
    public void GetWinner_TerritoriesDistributedAmongMultiplePlayers_ReturnsNull() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
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
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(42);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.BLUE)).andStubReturn(0);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.GREEN)).andStubReturn(0);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        assertEquals(PlayerColor.RED, game.getWinner());
        EasyMock.verify(mockMap);
    }

    @Test
    public void EndTurn_LastPlayerInRotation_WrapsBackToFirstPlayer() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.GREEN);
        game.endTurn();
        assertEquals(GamePhase.ATTACK, game.getPhase());
        assertEquals(PlayerColor.RED, game.getCurrentPlayerColor());
    }

    @Test
    public void IsDraftComplete_NewTurnDraftNotInitialized_ReturnsFalse() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.GREEN);
        game.endTurn();
        assertFalse(game.isDraftComplete());
    }

    @Test
    public void Attack_CapturesLastTerritory_TransitionsToGameOver() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.BLUE))
                .andStubReturn(true);
        EasyMock.expect(mockMap.areNeighbors(
                TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(2);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALBERTA))
                .andReturn(1).andReturn(1).andReturn(0).andReturn(0);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(42);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.BLUE)).andStubReturn(0);
        mockMap.removeArmies(TerritoryName.ALBERTA, 1);
        EasyMock.expectLastCall();
        mockMap.assignTerritory(TerritoryName.ALBERTA, PlayerColor.RED);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        assertEquals(GamePhase.GAME_OVER, game.getPhase());
        assertEquals(PlayerColor.RED, game.getWinner());
        EasyMock.verify(mockMap);
    }

    @Test
    public void DraftArmy_GameOverPhase_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.GAME_OVER);
        assertThrows(IllegalStateException.class, () -> game.draftArmy(TerritoryName.ALASKA));
    }

    @Test
    public void Attack_GameOverPhase_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.GAME_OVER);
        assertThrows(IllegalStateException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA));
    }

    @Test
    public void EndAttack_GameOverPhase_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.GAME_OVER);
        assertThrows(IllegalStateException.class, () -> game.endAttack());
    }

    @Test
    public void Fortify_GameOverPhase_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.GAME_OVER);
        assertThrows(IllegalStateException.class,
                () -> game.fortify(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void EndTurn_GameOverPhase_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.GAME_OVER);
        assertThrows(IllegalStateException.class, () -> game.endTurn());
    }

    @Test
    public void Attack_RealMap_CapturesAlberta_AlbertaOwnedByRed() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 4, 0));
        game.setupTerritory(TerritoryName.ALASKA, PlayerColor.RED, 3);
        game.setupTerritory(TerritoryName.ALBERTA, PlayerColor.BLUE, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        assertTrue(game.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED));
        assertEquals(0, game.getArmies(TerritoryName.ALBERTA));
        assertTrue(game.getArmies(TerritoryName.ALASKA) >= 1);
    }

    @Test
    public void IsCaptureMovementPending_NoPendingCapture_ReturnsFalse() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        assertFalse(game.isCaptureMovementPending());
    }

    @Test
    public void Attack_CapturesTerritory_ExposesPendingCaptureDetails() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 4, 3, 0));
        game.setupTerritory(TerritoryName.ALASKA, PlayerColor.RED, 4);
        game.setupTerritory(TerritoryName.ALBERTA, PlayerColor.BLUE, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        assertEquals("Jonathan", game.getPlayerName(PlayerColor.RED));
        assertTrue(game.isCaptureMovementPending());
        assertEquals(TerritoryName.ALASKA, game.getPendingCaptureFrom());
        assertEquals(TerritoryName.ALBERTA, game.getPendingCaptureTo());
        assertEquals(3, game.getMinimumCaptureMove());
        assertEquals(3, game.getMaximumCaptureMove());
        assertEquals(2, game.getTerritoryCount(PlayerColor.RED));
        assertFalse(game.isOwnedBy(TerritoryName.CENTRAL_AMERICA, PlayerColor.RED));
        assertTrue(game.isUnclaimed(TerritoryName.CENTRAL_AMERICA));
    }

    @Test
    public void IsUnclaimed_OwnedTerritory_ReturnsFalse() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setupTerritory(TerritoryName.ALASKA, PlayerColor.RED, 1);
        assertFalse(game.isUnclaimed(TerritoryName.ALASKA));
    }

    @Test
    public void Attack_CapturesFromTwoArmyTerritory_MinimumCaptureMoveIsOne() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 0));
        game.setupTerritory(TerritoryName.ALASKA, PlayerColor.RED, 2);
        game.setupTerritory(TerritoryName.ALBERTA, PlayerColor.BLUE, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        assertEquals(1, game.getMinimumCaptureMove());
        assertEquals(1, game.getMaximumCaptureMove());
    }

    @Test
    public void MoveArmiesAfterCapture_MovesAdditionalArmiesBeyondMinimum_Succeeds() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 4, 3, 0));
        game.setupTerritory(TerritoryName.ALASKA, PlayerColor.RED, 4);
        game.setupTerritory(TerritoryName.ALBERTA, PlayerColor.BLUE, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        game.moveArmiesAfterCapture(TerritoryName.ALASKA, TerritoryName.ALBERTA, 3);
        assertEquals(1, game.getArmies(TerritoryName.ALASKA));
        assertEquals(3, game.getArmies(TerritoryName.ALBERTA));
    }

    @Test
    public void MoveArmiesAfterCapture_WrongPhase_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalStateException.class, () ->
                game.moveArmiesAfterCapture(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void MoveArmiesAfterCapture_NoPriorCapture_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        assertThrows(IllegalStateException.class, () ->
                game.moveArmiesAfterCapture(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void MoveArmiesAfterCapture_ZeroArmies_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 4, 3, 0));
        game.setupTerritory(TerritoryName.ALASKA, PlayerColor.RED, 4);
        game.setupTerritory(TerritoryName.ALBERTA, PlayerColor.BLUE, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        assertThrows(IllegalArgumentException.class, () ->
                game.moveArmiesAfterCapture(TerritoryName.ALASKA, TerritoryName.ALBERTA, 0));
    }

    @Test
    public void MoveArmiesAfterCapture_AllArmiesNoneLeftBehind_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 4, 3, 0));
        game.setupTerritory(TerritoryName.ALASKA, PlayerColor.RED, 4);
        game.setupTerritory(TerritoryName.ALBERTA, PlayerColor.BLUE, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        assertThrows(IllegalArgumentException.class, () ->
                game.moveArmiesAfterCapture(TerritoryName.ALASKA, TerritoryName.ALBERTA, 4));
    }

    @Test
    public void MoveArmiesAfterCapture_WrongTerritories_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 4, 3, 0));
        game.setupTerritory(TerritoryName.ALASKA, PlayerColor.RED, 4);
        game.setupTerritory(TerritoryName.ALBERTA, PlayerColor.BLUE, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        assertThrows(IllegalStateException.class, () ->
                game.moveArmiesAfterCapture(TerritoryName.ALASKA, TerritoryName.ONTARIO, 3));
    }

    @Test
    public void MoveArmiesAfterCapture_AboveMaximum_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 4, 3, 0));
        game.setupTerritory(TerritoryName.ALASKA, PlayerColor.RED, 6);
        game.setupTerritory(TerritoryName.ALBERTA, PlayerColor.BLUE, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        assertThrows(IllegalArgumentException.class, () ->
                game.moveArmiesAfterCapture(TerritoryName.ALASKA, TerritoryName.ALBERTA, 6));
    }

    @Test
    public void MoveArmiesAfterCapture_AtMaximumLeavesOneArmyBehind_Succeeds() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 4, 3, 0));
        game.setupTerritory(TerritoryName.ALASKA, PlayerColor.RED, 6);
        game.setupTerritory(TerritoryName.ALBERTA, PlayerColor.BLUE, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        game.moveArmiesAfterCapture(TerritoryName.ALASKA, TerritoryName.ALBERTA, 5);
        assertEquals(1, game.getArmies(TerritoryName.ALASKA));
        assertEquals(5, game.getArmies(TerritoryName.ALBERTA));
        assertThrows(IllegalStateException.class, () ->
                game.moveArmiesAfterCapture(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void MoveArmiesAfterCapture_SourceHasThreeArmies_MovesMinimumTwo_Succeeds() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 4, 0));
        game.setupTerritory(TerritoryName.ALASKA, PlayerColor.RED, 3);
        game.setupTerritory(TerritoryName.ALBERTA, PlayerColor.BLUE, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        game.moveArmiesAfterCapture(TerritoryName.ALASKA, TerritoryName.ALBERTA, 2);
        assertEquals(1, game.getArmies(TerritoryName.ALASKA));
        assertEquals(2, game.getArmies(TerritoryName.ALBERTA));
        assertThrows(IllegalStateException.class, () ->
                game.moveArmiesAfterCapture(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void MoveArmiesAfterCapture_SourceHasTwoArmies_MovesMinimumOne_Succeeds() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 0));
        game.setupTerritory(TerritoryName.ALASKA, PlayerColor.RED, 2);
        game.setupTerritory(TerritoryName.ALBERTA, PlayerColor.BLUE, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        game.moveArmiesAfterCapture(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1);
        assertEquals(1, game.getArmies(TerritoryName.ALASKA));
        assertEquals(1, game.getArmies(TerritoryName.ALBERTA));
        assertThrows(IllegalStateException.class, () ->
                game.moveArmiesAfterCapture(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void MoveArmiesAfterCapture_CalledTwice_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 4, 3, 0));
        game.setupTerritory(TerritoryName.ALASKA, PlayerColor.RED, 4);
        game.setupTerritory(TerritoryName.ALBERTA, PlayerColor.BLUE, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        game.moveArmiesAfterCapture(TerritoryName.ALASKA, TerritoryName.ALBERTA, 3);
        assertThrows(IllegalStateException.class, () ->
                game.moveArmiesAfterCapture(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
    }

    @Test
    public void MoveArmiesAfterCapture_BelowMinimum_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 4, 3, 0));
        game.setupTerritory(TerritoryName.ALASKA, PlayerColor.RED, 4);
        game.setupTerritory(TerritoryName.ALBERTA, PlayerColor.BLUE, 1);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        assertThrows(IllegalArgumentException.class, () ->
                game.moveArmiesAfterCapture(TerritoryName.ALASKA, TerritoryName.ALBERTA, 2));
    }

    @Test
    public void Fortify_FirstFortifySucceeds_ArmiesMove() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.areConnectedThrough(
                TerritoryName.ALASKA, TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(true);
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
    public void Fortify_CalledTwiceInSameTurn_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.areConnectedThrough(
                TerritoryName.ALASKA, TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(true);
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
        assertThrows(IllegalStateException.class,
                () -> game.fortify(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1));
        EasyMock.verify(mockMap);
    }

    @Test
    public void Fortify_AfterEndTurn_NewPlayerCanFortify() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.areConnectedThrough(
                TerritoryName.ALASKA, TerritoryName.ALBERTA, PlayerColor.RED)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(3);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.BLUE))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.BLUE))
                .andStubReturn(true);
        EasyMock.expect(mockMap.areConnectedThrough(
                TerritoryName.ALASKA, TerritoryName.ALBERTA, PlayerColor.BLUE)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(3);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.BLUE)).andStubReturn(1);
        mockMap.removeArmies(TerritoryName.ALASKA, 1);
        EasyMock.expectLastCall().times(2);
        mockMap.addArmies(TerritoryName.ALBERTA, 1);
        EasyMock.expectLastCall().times(2);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        game.fortify(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1);
        game.endTurn();
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.BLUE);
        game.fortify(TerritoryName.ALASKA, TerritoryName.ALBERTA, 1);
        EasyMock.verify(mockMap);
    }

    @Test
    public void Attack_DraftNeverInitialized_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.expect(mockMap.areNeighbors(
                TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(3);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalStateException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA));
        EasyMock.verify(mockMap);
    }

    @Test
    public void Attack_AfterDraftComplete_Succeeds() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 4, 0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(1);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.BLUE))
                .andStubReturn(true);
        EasyMock.expect(mockMap.areNeighbors(
                TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(3);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALBERTA))
                .andReturn(1).andReturn(1).andReturn(0).andReturn(0);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.BLUE)).andStubReturn(20);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.GREEN)).andStubReturn(20);
        mockMap.addArmies(TerritoryName.ALASKA, 1);
        EasyMock.expectLastCall().times(3);
        mockMap.removeArmies(TerritoryName.ALBERTA, 1);
        EasyMock.expectLastCall();
        mockMap.assignTerritory(TerritoryName.ALBERTA, PlayerColor.RED);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.draftArmy(TerritoryName.ALASKA);
        game.draftArmy(TerritoryName.ALASKA);
        game.draftArmy(TerritoryName.ALASKA);
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        EasyMock.verify(mockMap);
    }

    @Test
    public void Attack_AfterEndTurn_NewPlayerDraftNotInitialized_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.BLUE))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.BLUE))
                .andStubReturn(false);
        EasyMock.expect(mockMap.areNeighbors(
                TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(3);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.BLUE)).andStubReturn(1);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        game.endTurn();
        assertEquals(PlayerColor.BLUE, game.getCurrentPlayerColor());
        assertThrows(IllegalStateException.class,
                () -> game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA));
        EasyMock.verify(mockMap);
    }

    @Test
    public void GetDraftArmies_AfterAllDraftArmiesPlaced_ReturnsZero() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(1);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
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
        assertEquals(0, game.getDraftArmies());
        EasyMock.verify(mockMap);
    }

    @Test
    public void EndAttack_BeforeDraftComplete_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.ATTACK);
        assertThrows(IllegalStateException.class, () -> game.endAttack());
    }

    @Test
    public void PlaceArmy_LastSetupArmy_ReturnsFirstSetupPlayerToAttack() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.anyObject()))
                .andStubReturn(true);
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
        assertEquals(PlayerColor.RED, game.getCurrentPlayerColor());
        EasyMock.verify(mockMap);
    }

    @Test
    public void GetCards_PlayerHasNoCards_ReturnsEmptyList() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        assertEquals(List.of(), game.getCards(PlayerColor.RED));
    }

    @Test
    public void GetCards_PlayerHasOneCard_ReturnsListWithThatCard() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        Card alaska = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(alaska);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(List.of(alaska), game.getCards(PlayerColor.RED));
    }

    @Test
    public void Attack_FirstCapture_AwardsOneCardOnEndTurn() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.BLUE))
                .andStubReturn(true);
        EasyMock.expect(mockMap.areNeighbors(
                TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(2);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALBERTA))
                .andReturn(1).andReturn(1).andReturn(0).andReturn(0);
        mockMap.removeArmies(TerritoryName.ALBERTA, 1);
        EasyMock.expectLastCall();
        mockMap.assignTerritory(TerritoryName.ALBERTA, PlayerColor.RED);
        EasyMock.expectLastCall();
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(1);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.BLUE)).andStubReturn(1);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.GREEN)).andStubReturn(1);
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        assertEquals(0, game.getCards(PlayerColor.RED).size());
        game.endAttack();
        game.endTurn();
        assertEquals(1, game.getCards(PlayerColor.RED).size());
        EasyMock.verify(mockMap);
    }

    @Test
    public void CanTradeCards_ThreeOfSameType_ReturnsTrue() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        List<Card> cards = List.of(
                new Card(CardType.INFANTRY, TerritoryName.ALASKA),
                new Card(CardType.INFANTRY, TerritoryName.ALBERTA),
                new Card(CardType.INFANTRY, TerritoryName.BRAZIL));
        assertTrue(game.canTradeCards(cards));
    }

    @Test
    public void CanTradeCards_OneOfEachType_ReturnsTrue() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        List<Card> cards = List.of(
                new Card(CardType.INFANTRY, TerritoryName.ALASKA),
                new Card(CardType.CAVALRY, TerritoryName.ALBERTA),
                new Card(CardType.ARTILLERY, TerritoryName.BRAZIL));
        assertTrue(game.canTradeCards(cards));
    }

    @Test
    public void TradeCards_BeforeDraftInitialized_TotalIncludesTerritoryAndCardBonus() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(3);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.eq(PlayerColor.RED)))
                .andStubReturn(true);
        mockMap.addArmies(EasyMock.anyObject(), EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(mockMap);
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.INFANTRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.INFANTRY, TerritoryName.BRAZIL);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.tradeCards(List.of(c1, c2, c3));
        for (int i = 0; i < 7; i++) {
            game.draftArmy(TerritoryName.ALASKA);
        }
        assertTrue(game.isDraftComplete());
        EasyMock.verify(mockMap);
    }

    @Test
    public void TradeCards_FirstTrade_AddsFourDraftArmies() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(3);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.eq(PlayerColor.RED)))
                .andStubReturn(true);
        mockMap.addArmies(EasyMock.anyObject(), EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(mockMap);
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.INFANTRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.INFANTRY, TerritoryName.BRAZIL);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.tradeCards(List.of(c1, c2, c3));
        for (int i = 0; i < 7; i++) {
            game.draftArmy(TerritoryName.ALASKA);
        }
        assertTrue(game.isDraftComplete());
        EasyMock.verify(mockMap);
    }

    @Test
    public void TradeCards_SecondTrade_AddsSixDraftArmies() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(3);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.eq(PlayerColor.RED)))
                .andStubReturn(true);
        mockMap.addArmies(EasyMock.anyObject(), EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(mockMap);
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.INFANTRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.INFANTRY, TerritoryName.BRAZIL);
        Card c4 = new Card(CardType.CAVALRY, TerritoryName.CHINA);
        Card c5 = new Card(CardType.CAVALRY, TerritoryName.INDIA);
        Card c6 = new Card(CardType.CAVALRY, TerritoryName.JAPAN);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        red.addCard(c4);
        red.addCard(c5);
        red.addCard(c6);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setTradeSetCount(1);
        game.tradeCards(List.of(c4, c5, c6));
        for (int i = 0; i < 9; i++) {
            game.draftArmy(TerritoryName.ALASKA);
        }
        assertTrue(game.isDraftComplete());
        EasyMock.verify(mockMap);
    }

    @Test
    public void TradeCards_ThirdTrade_AddsEightDraftArmies() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(3);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.eq(PlayerColor.RED)))
                .andStubReturn(true);
        mockMap.addArmies(EasyMock.anyObject(), EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(mockMap);
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.INFANTRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.INFANTRY, TerritoryName.BRAZIL);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setTradeSetCount(2);
        game.tradeCards(List.of(c1, c2, c3));
        for (int i = 0; i < 11; i++) {
            game.draftArmy(TerritoryName.ALASKA);
        }
        assertTrue(game.isDraftComplete());
        EasyMock.verify(mockMap);
    }

    @Test
    public void TradeCards_FourthTrade_AddsTenDraftArmies() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(3);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.eq(PlayerColor.RED)))
                .andStubReturn(true);
        mockMap.addArmies(EasyMock.anyObject(), EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(mockMap);
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.INFANTRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.INFANTRY, TerritoryName.BRAZIL);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setTradeSetCount(3);
        game.tradeCards(List.of(c1, c2, c3));
        for (int i = 0; i < 13; i++) {
            game.draftArmy(TerritoryName.ALASKA);
        }
        assertTrue(game.isDraftComplete());
        EasyMock.verify(mockMap);
    }

    @Test
    public void TradeCards_FifthTrade_AddsTwelveDraftArmies() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(3);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.eq(PlayerColor.RED)))
                .andStubReturn(true);
        mockMap.addArmies(EasyMock.anyObject(), EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(mockMap);
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.INFANTRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.INFANTRY, TerritoryName.BRAZIL);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setTradeSetCount(4);
        game.tradeCards(List.of(c1, c2, c3));
        for (int i = 0; i < 15; i++) {
            game.draftArmy(TerritoryName.ALASKA);
        }
        assertTrue(game.isDraftComplete());
        EasyMock.verify(mockMap);
    }

    @Test
    public void TradeCards_SixthTrade_AddsFifteenDraftArmies() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(3);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.eq(PlayerColor.RED)))
                .andStubReturn(true);
        mockMap.addArmies(EasyMock.anyObject(), EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(mockMap);
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.INFANTRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.INFANTRY, TerritoryName.BRAZIL);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setTradeSetCount(5);
        game.tradeCards(List.of(c1, c2, c3));
        for (int i = 0; i < 18; i++) {
            game.draftArmy(TerritoryName.ALASKA);
        }
        assertTrue(game.isDraftComplete());
        EasyMock.verify(mockMap);
    }

    @Test
    public void TradeCards_AfterSixthTrade_IncreasesBy5() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(3);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.eq(PlayerColor.RED)))
                .andStubReturn(true);
        mockMap.addArmies(EasyMock.anyObject(), EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(mockMap);
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.INFANTRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.INFANTRY, TerritoryName.BRAZIL);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setTradeSetCount(6);
        game.tradeCards(List.of(c1, c2, c3));
        for (int i = 0; i < 23; i++) {
            game.draftArmy(TerritoryName.ALASKA);
        }
        assertTrue(game.isDraftComplete());
        EasyMock.verify(mockMap);
    }

    @Test
    public void TradeCards_NinthTrade_AddsThirtyDraftArmies() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(3);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.eq(PlayerColor.RED)))
                .andStubReturn(true);
        mockMap.addArmies(EasyMock.anyObject(), EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(mockMap);
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.INFANTRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.INFANTRY, TerritoryName.BRAZIL);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setTradeSetCount(8);
        game.tradeCards(List.of(c1, c2, c3));
        for (int i = 0; i < 33; i++) {
            game.draftArmy(TerritoryName.ALASKA);
        }
        assertTrue(game.isDraftComplete());
        EasyMock.verify(mockMap);
    }

    @Test
    public void TradeCards_CardMatchesOwnedTerritory_AddsTwoArmiesToTerritory() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(3);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.BRAZIL, PlayerColor.RED))
                .andStubReturn(false);
        mockMap.addArmies(TerritoryName.ALASKA, 2);
        EasyMock.expectLastCall().once();
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.eq(PlayerColor.RED)))
                .andStubReturn(true);
        mockMap.addArmies(EasyMock.anyObject(), EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(mockMap);
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.CAVALRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.ARTILLERY, TerritoryName.BRAZIL);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.tradeCards(List.of(c1, c2, c3));
        EasyMock.verify(mockMap);
    }

    @Test
    public void TradeCards_CardMatchesUnownedTerritory_NoTerritoryBonus() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(3);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.eq(PlayerColor.RED)))
                .andStubReturn(false);
        EasyMock.replay(mockMap);
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.CAVALRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.ARTILLERY, TerritoryName.BRAZIL);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.tradeCards(List.of(c1, c2, c3));
        EasyMock.verify(mockMap);
    }

    @Test
    public void TradeCards_MultipleCardsMatchOwnedTerritories_AddsTwoArmiesToEachOwnedTerritory() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(3);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED)).andReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED)).andReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.BRAZIL, PlayerColor.RED)).andReturn(false);
        mockMap.addArmies(TerritoryName.ALASKA, 2);
        EasyMock.expectLastCall().once();
        mockMap.addArmies(TerritoryName.ALBERTA, 2);
        EasyMock.expectLastCall().once();
        EasyMock.replay(mockMap);
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.CAVALRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.ARTILLERY, TerritoryName.BRAZIL);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.tradeCards(List.of(c1, c2, c3));
        EasyMock.verify(mockMap);
    }

    @Test
    public void TradeCards_InvalidSet_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.INFANTRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.CAVALRY, TerritoryName.BRAZIL);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalArgumentException.class, () -> game.tradeCards(List.of(c1, c2, c3)));
    }

    @Test
    public void TradeCards_CardsNotOwnedByPlayer_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        final Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        final Card c2 = new Card(CardType.INFANTRY, TerritoryName.ALBERTA);
        final Card c3 = new Card(CardType.INFANTRY, TerritoryName.BRAZIL);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalArgumentException.class, () -> game.tradeCards(List.of(c1, c2, c3)));
    }

    @Test
    public void TradeCards_WrongPhase_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.INFANTRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.INFANTRY, TerritoryName.BRAZIL);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalStateException.class, () -> game.tradeCards(List.of(c1, c2, c3)));
    }

    @Test
    public void TradeCards_AfterDraftComplete_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.CAVALRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.ARTILLERY, TerritoryName.BRAZIL);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        assertThrows(IllegalStateException.class, () -> game.tradeCards(List.of(c1, c2, c3)));
    }

    @Test
    public void TradeCards_NullList_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.providePlayers(List.of(
                new Player(PlayerColor.RED, "Jonathan", 35),
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalArgumentException.class, () -> game.tradeCards(null));
    }

    @Test
    public void TradeCards_ListContainingNull_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.providePlayers(List.of(
                new Player(PlayerColor.RED, "Jonathan", 35),
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        List<Card> cards = new java.util.ArrayList<>();
        cards.add(new Card(CardType.INFANTRY, TerritoryName.ALASKA));
        cards.add(null);
        cards.add(new Card(CardType.INFANTRY, TerritoryName.BRAZIL));
        assertThrows(IllegalArgumentException.class, () -> game.tradeCards(cards));
    }

    @Test
    public void TradeCards_FewerThanThreeCards_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.INFANTRY, TerritoryName.ALBERTA);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalArgumentException.class, () -> game.tradeCards(List.of(c1, c2)));
    }

    @Test
    public void TradeCards_MoreThanThreeCards_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.INFANTRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.INFANTRY, TerritoryName.BRAZIL);
        Card c4 = new Card(CardType.INFANTRY, TerritoryName.CHINA);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        red.addCard(c4);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalArgumentException.class,
                () -> game.tradeCards(List.of(c1, c2, c3, c4)));
    }

    @Test
    public void TradeCards_ValidSet_MovesTradedCardsToDeckDiscardPile() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.INFANTRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.INFANTRY, TerritoryName.BRAZIL);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.tradeCards(List.of(c1, c2, c3));
        assertEquals(0, game.getCards(PlayerColor.RED).size());
        assertEquals(3, game.getDeckDiscardPileSize());
    }

    @Test
    public void DraftArmy_PlayerHasFourCards_DraftSucceeds() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(3);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        mockMap.addArmies(TerritoryName.ALASKA, 1);
        EasyMock.expectLastCall().once();
        EasyMock.replay(mockMap);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(new Card(CardType.INFANTRY, TerritoryName.ALASKA));
        red.addCard(new Card(CardType.CAVALRY, TerritoryName.ALBERTA));
        red.addCard(new Card(CardType.ARTILLERY, TerritoryName.BRAZIL));
        red.addCard(new Card(CardType.INFANTRY, TerritoryName.CHINA));
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.draftArmy(TerritoryName.ALASKA);
        EasyMock.verify(mockMap);
    }

    @Test
    public void DraftArmy_PlayerHasFiveCards_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(new Card(CardType.INFANTRY, TerritoryName.ALASKA));
        red.addCard(new Card(CardType.CAVALRY, TerritoryName.ALBERTA));
        red.addCard(new Card(CardType.ARTILLERY, TerritoryName.BRAZIL));
        red.addCard(new Card(CardType.INFANTRY, TerritoryName.CHINA));
        red.addCard(new Card(CardType.CAVALRY, TerritoryName.INDIA));
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalStateException.class, () -> game.draftArmy(TerritoryName.ALASKA));
    }

    @Test
    public void DraftArmy_PlayerHasSixCards_ThrowsIllegalStateException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(new Card(CardType.INFANTRY, TerritoryName.ALASKA));
        red.addCard(new Card(CardType.CAVALRY, TerritoryName.ALBERTA));
        red.addCard(new Card(CardType.ARTILLERY, TerritoryName.BRAZIL));
        red.addCard(new Card(CardType.INFANTRY, TerritoryName.CHINA));
        red.addCard(new Card(CardType.CAVALRY, TerritoryName.INDIA));
        red.addCard(new Card(CardType.ARTILLERY, TerritoryName.JAPAN));
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        assertThrows(IllegalStateException.class, () -> game.draftArmy(TerritoryName.ALASKA));
    }

    @Test
    public void DraftArmy_PlayerHasFiveCardsAfterValidTrade_DraftSucceeds() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(3);
        EasyMock.expect(mockMap.getTerritoriesOwnedBy(PlayerColor.RED))
                .andStubReturn(territories());
        EasyMock.expect(mockMap.isOwnedBy(EasyMock.anyObject(), EasyMock.eq(PlayerColor.RED)))
                .andStubReturn(true);
        mockMap.addArmies(EasyMock.anyObject(), EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(mockMap);
        Card c1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card c2 = new Card(CardType.INFANTRY, TerritoryName.ALBERTA);
        Card c3 = new Card(CardType.INFANTRY, TerritoryName.BRAZIL);
        Card c4 = new Card(CardType.CAVALRY, TerritoryName.CHINA);
        Card c5 = new Card(CardType.CAVALRY, TerritoryName.INDIA);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(c1);
        red.addCard(c2);
        red.addCard(c3);
        red.addCard(c4);
        red.addCard(c5);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.tradeCards(List.of(c1, c2, c3));
        game.draftArmy(TerritoryName.ALASKA);
        EasyMock.verify(mockMap);
    }

    @Test
    public void CanTradeCards_ListContainingNull_ReturnsFalse() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        List<Card> cards = new java.util.ArrayList<>();
        cards.add(new Card(CardType.INFANTRY, TerritoryName.ALASKA));
        cards.add(new Card(CardType.CAVALRY, TerritoryName.ALBERTA));
        cards.add(null);
        assertFalse(game.canTradeCards(cards));
    }

    @Test
    public void CanTradeCards_NullList_ReturnsFalse() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        assertFalse(game.canTradeCards(null));
    }

    @Test
    public void CanTradeCards_MoreThanThreeCards_ReturnsFalse() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        List<Card> cards = List.of(
                new Card(CardType.INFANTRY, TerritoryName.ALASKA),
                new Card(CardType.INFANTRY, TerritoryName.ALBERTA),
                new Card(CardType.INFANTRY, TerritoryName.BRAZIL),
                new Card(CardType.CAVALRY, TerritoryName.CHINA));
        assertFalse(game.canTradeCards(cards));
    }

    @Test
    public void CanTradeCards_FewerThanThreeCards_ReturnsFalse() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        List<Card> cards = List.of(
                new Card(CardType.INFANTRY, TerritoryName.ALASKA),
                new Card(CardType.INFANTRY, TerritoryName.ALBERTA));
        assertFalse(game.canTradeCards(cards));
    }

    @Test
    public void CanTradeCards_TwoSameAndOneDifferentNonWild_ReturnsFalse() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        List<Card> cards = List.of(
                new Card(CardType.INFANTRY, TerritoryName.ALASKA),
                new Card(CardType.INFANTRY, TerritoryName.ALBERTA),
                new Card(CardType.CAVALRY, TerritoryName.BRAZIL));
        assertFalse(game.canTradeCards(cards));
    }

    @Test
    public void CanTradeCards_OneCardAndTwoWilds_ReturnsTrue() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        List<Card> cards = List.of(
                new Card(CardType.INFANTRY, TerritoryName.ALASKA),
                new Card(CardType.WILD, null),
                new Card(CardType.WILD, null));
        assertTrue(game.canTradeCards(cards));
    }

    @Test
    public void CanTradeCards_TwoDifferentTypeAndOneWild_ReturnsTrue() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        List<Card> cards = List.of(
                new Card(CardType.INFANTRY, TerritoryName.ALASKA),
                new Card(CardType.CAVALRY, TerritoryName.ALBERTA),
                new Card(CardType.WILD, null));
        assertTrue(game.canTradeCards(cards));
    }

    @Test
    public void CanTradeCards_TwoSameTypeAndOneWild_ReturnsTrue() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        List<Card> cards = List.of(
                new Card(CardType.INFANTRY, TerritoryName.ALASKA),
                new Card(CardType.INFANTRY, TerritoryName.ALBERTA),
                new Card(CardType.WILD, null));
        assertTrue(game.canTradeCards(cards));
    }

    @Test
    public void EndTurn_AfterCardAward_ResetsCaptureFlagForNextPlayer() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setCapturedThisTurn(true);
        game.endTurn();
        game.setPhase(GamePhase.FORTIFY);
        game.endTurn();
        assertEquals(0, game.getCards(PlayerColor.BLUE).size());
    }

    @Test
    public void EndTurn_AfterMoreThanOneCapture_AwardsOneCard() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setCapturedThisTurn(true);
        game.endTurn();
        assertEquals(1, game.getCards(PlayerColor.RED).size());
    }

    @Test
    public void EndTurn_AfterOneCapture_AwardsOneCard() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setCapturedThisTurn(true);
        game.endTurn();
        assertEquals(1, game.getCards(PlayerColor.RED).size());
    }

    @Test
    public void EndTurn_AfterNoCaptures_AwardsNoCard() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        game.setPhase(GamePhase.FORTIFY);
        game.setCurrentPlayer(PlayerColor.RED);
        game.endTurn();
        assertEquals(0, game.getCards(PlayerColor.BLUE).size());
    }

    @Test
    public void Attack_CapturesFinalTerritory_TransfersDefeatedPlayerCards() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 0));
        Card blueCard1 = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card blueCard2 = new Card(CardType.CAVALRY, TerritoryName.CHINA);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        Player blue = new Player(PlayerColor.BLUE, "Justin", 35);
        blue.addCard(blueCard1);
        blue.addCard(blueCard2);
        Player green = new Player(PlayerColor.GREEN, "Prashant", 35);
        game.providePlayers(List.of(red, blue, green));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.BLUE))
                .andStubReturn(true);
        EasyMock.expect(mockMap.areNeighbors(
                TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(2);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALBERTA))
                .andReturn(1).andReturn(1).andReturn(0).andReturn(0);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(2);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.BLUE)).andStubReturn(0);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.GREEN)).andStubReturn(10);
        mockMap.removeArmies(TerritoryName.ALBERTA, 1);
        EasyMock.expectLastCall();
        mockMap.assignTerritory(TerritoryName.ALBERTA, PlayerColor.RED);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        assertEquals(0, game.getCards(PlayerColor.BLUE).size());
        assertEquals(2, game.getCards(PlayerColor.RED).size());
        EasyMock.verify(mockMap);
    }

    @Test
    public void Attack_TwoCapturesSameTurn_AwardsOneCardOnEndTurn() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 5, 4, 0, 5, 4, 0));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.BLUE))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.NORTHWEST_TERRITORY, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.NORTHWEST_TERRITORY, PlayerColor.BLUE))
                .andStubReturn(true);
        EasyMock.expect(mockMap.areNeighbors(
                TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.areNeighbors(
                TerritoryName.ALASKA, TerritoryName.NORTHWEST_TERRITORY))
                .andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA)).andStubReturn(3);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALBERTA))
                .andReturn(1).andReturn(1).andReturn(0).andReturn(0);
        EasyMock.expect(mockMap.getArmies(TerritoryName.NORTHWEST_TERRITORY))
                .andReturn(1).andReturn(1).andReturn(0).andReturn(0);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.RED)).andStubReturn(2);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.BLUE)).andStubReturn(10);
        EasyMock.expect(mockMap.countTerritoriesOwnedBy(PlayerColor.GREEN)).andStubReturn(10);
        mockMap.removeArmies(TerritoryName.ALBERTA, 1);
        EasyMock.expectLastCall();
        mockMap.assignTerritory(TerritoryName.ALBERTA, PlayerColor.RED);
        EasyMock.expectLastCall();
        mockMap.removeArmies(TerritoryName.NORTHWEST_TERRITORY, 1);
        EasyMock.expectLastCall();
        mockMap.assignTerritory(TerritoryName.NORTHWEST_TERRITORY, PlayerColor.RED);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        game.attack(TerritoryName.ALASKA, TerritoryName.NORTHWEST_TERRITORY);
        game.endAttack();
        game.endTurn();
        assertEquals(1, game.getCards(PlayerColor.RED).size());
        EasyMock.verify(mockMap);
    }

    @Test
    public void Attack_WithoutCapture_DoesNotMarkCardAward() {
        final RiskGame game = new RiskGame(threePlayerMap(), scriptedDice(0, 0, 5));
        WorldMap mockMap = EasyMock.createMock(WorldMap.class);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED))
                .andStubReturn(true);
        EasyMock.expect(mockMap.isOwnedBy(TerritoryName.ALBERTA, PlayerColor.RED))
                .andStubReturn(false);
        EasyMock.expect(mockMap.areNeighbors(
                TerritoryName.ALASKA, TerritoryName.ALBERTA)).andStubReturn(true);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALASKA))
                .andReturn(2).andReturn(2).andReturn(2).andReturn(1);
        EasyMock.expect(mockMap.getArmies(TerritoryName.ALBERTA)).andStubReturn(1);
        mockMap.removeArmies(TerritoryName.ALASKA, 1);
        EasyMock.expectLastCall();
        EasyMock.replay(mockMap);
        game.provideWorldMap(mockMap);
        game.setPhase(GamePhase.ATTACK);
        game.setCurrentPlayer(PlayerColor.RED);
        game.setDraftComplete();
        game.attack(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        assertEquals(0, game.getCards(PlayerColor.RED).size());
        EasyMock.verify(mockMap);
    }

    @Test
    public void GetCards_ColorNotInGame_ThrowsIllegalArgumentException() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        assertThrows(IllegalArgumentException.class, () -> game.getCards(PlayerColor.CYAN));
    }

    @Test
    public void GetCards_PlayerHasThreeCards_ReturnsListWithAllThreeCards() {
        final RiskGame game = new RiskGame(threePlayerMap(), stubbedRandom(0));
        Card alaska = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card alberta = new Card(CardType.CAVALRY, TerritoryName.ALBERTA);
        Card brazil = new Card(CardType.ARTILLERY, TerritoryName.BRAZIL);
        Player red = new Player(PlayerColor.RED, "Jonathan", 35);
        red.addCard(alaska);
        red.addCard(alberta);
        red.addCard(brazil);
        game.providePlayers(List.of(red,
                new Player(PlayerColor.BLUE, "Justin", 35),
                new Player(PlayerColor.GREEN, "Prashant", 35)));
        game.setCurrentPlayer(PlayerColor.RED);
        assertEquals(List.of(alaska, alberta, brazil), game.getCards(PlayerColor.RED));
    }
}
