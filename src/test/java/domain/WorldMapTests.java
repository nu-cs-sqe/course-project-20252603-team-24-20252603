package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

public class WorldMapTests {

    private Map<TerritoryName, Territory> createTerritories() {
        Map<TerritoryName, Territory> territories = new HashMap<>();
        for (TerritoryName name : TerritoryName.values()) {
            Territory mock = EasyMock.createMock(Territory.class);
            EasyMock.replay(mock);
            territories.put(name, mock);
        }
        return territories;
    }

    private Map<TerritoryName, Set<TerritoryName>> createNeighbors() {
        Map<TerritoryName, Set<TerritoryName>> neighbors = new HashMap<>();
        for (TerritoryName name : TerritoryName.values()) {
            neighbors.put(name, new HashSet<>());
        }
        return neighbors;
    }

    private void assertBidirectionalNeighbors(
            WorldMap map, TerritoryName first, TerritoryName second) {
        assertTrue(map.areNeighbors(first, second));
        assertTrue(map.areNeighbors(second, first));
    }

    @Test
    public void AreNeighbors_AlaskaAndAlberta_ReturnsTrue() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        neighbors.get(TerritoryName.ALASKA).add(TerritoryName.ALBERTA);
        neighbors.get(TerritoryName.ALBERTA).add(TerritoryName.ALASKA);
        WorldMap map = new WorldMap(territories, neighbors);

        assertTrue(map.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALBERTA));
    }

    @Test
    public void AreNeighbors_AlaskaAndBrazil_ReturnsFalse() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        WorldMap map = new WorldMap(territories, neighbors);

        assertFalse(map.areNeighbors(TerritoryName.ALASKA, TerritoryName.BRAZIL));
    }

    @Test
    public void AreNeighbors_TerritoryMissingFromNeighborMap_ReturnsFalse() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        neighbors.remove(TerritoryName.ALASKA);
        WorldMap map = new WorldMap(territories, neighbors);

        assertFalse(map.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALBERTA));
    }

    @Test
    public void AreNeighbors_AlaskaAndAlaska_ReturnsFalse() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        WorldMap map = new WorldMap(territories, neighbors);

        assertFalse(map.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALASKA));
    }

    @Test
    public void IsOwnedBy_UnclaimedTerritory_ReturnsFalse() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        EasyMock.expect(mockAlaska.isOwnedBy(PlayerColor.RED)).andStubReturn(false);
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);

        assertFalse(map.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED));
    }

    @Test
    public void IsOwnedBy_TerritoryClaimedByRed_WithRed_ReturnsTrue() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        EasyMock.expect(mockAlaska.isOwnedBy(PlayerColor.RED)).andStubReturn(true);
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);

        assertTrue(map.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED));
    }

    @Test
    public void IsOwnedBy_TerritoryClaimedByRed_WithBlue_ReturnsFalse() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        EasyMock.expect(mockAlaska.isOwnedBy(PlayerColor.BLUE)).andStubReturn(false);
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);

        assertFalse(map.isOwnedBy(TerritoryName.ALASKA, PlayerColor.BLUE));
    }

    @Test
    public void IsUnclaimed_UnclaimedTerritory_ReturnsTrue() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        EasyMock.expect(mockAlaska.isUnclaimed()).andStubReturn(true);
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);

        assertTrue(map.isUnclaimed(TerritoryName.ALASKA));
    }

    @Test
    public void IsUnclaimed_ClaimedTerritory_ReturnsFalse() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        EasyMock.expect(mockAlaska.isUnclaimed()).andStubReturn(false);
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);

        assertFalse(map.isUnclaimed(TerritoryName.ALASKA));
    }

    @Test
    public void GetArmies_TerritoryWithZeroArmies_ReturnsZero() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        EasyMock.expect(mockAlaska.getArmies()).andStubReturn(0);
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);

        assertEquals(0, map.getArmies(TerritoryName.ALASKA));
    }

    @Test
    public void GetArmies_TerritoryWithOneArmy_ReturnsOne() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        EasyMock.expect(mockAlaska.getArmies()).andStubReturn(1);
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);

        assertEquals(1, map.getArmies(TerritoryName.ALASKA));
    }

    @Test
    public void GetArmies_TerritoryWithFiveArmies_ReturnsFive() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        EasyMock.expect(mockAlaska.getArmies()).andStubReturn(5);
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);

        assertEquals(5, map.getArmies(TerritoryName.ALASKA));
    }

    @Test
    public void Claim_UnclaimedTerritory_ClaimIsCalledOnTerritory() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.claim(PlayerColor.RED);
        EasyMock.expectLastCall();
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        map.claim(TerritoryName.ALASKA, PlayerColor.RED);

        EasyMock.verify(mockAlaska);
    }

    @Test
    public void Claim_AlreadyClaimedBySamePlayer_ThrowsIllegalStateException() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.claim(PlayerColor.RED);
        EasyMock.expectLastCall().andThrow(new IllegalStateException());
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        assertThrows(IllegalStateException.class,
                () -> map.claim(TerritoryName.ALASKA, PlayerColor.RED));
        EasyMock.verify(mockAlaska);
    }

    @Test
    public void Claim_AlreadyClaimedByDifferentPlayer_ThrowsIllegalStateException() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.claim(PlayerColor.BLUE);
        EasyMock.expectLastCall().andThrow(new IllegalStateException());
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        assertThrows(IllegalStateException.class,
                () -> map.claim(TerritoryName.ALASKA, PlayerColor.BLUE));
        EasyMock.verify(mockAlaska);
    }

    @Test
    public void AddArmies_TerritoryWithZeroArmies_AddOne_AddArmiesCalledOnTerritory() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.addArmies(1);
        EasyMock.expectLastCall();
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        map.addArmies(TerritoryName.ALASKA, 1);

        EasyMock.verify(mockAlaska);
    }

    @Test
    public void AddArmies_TerritoryWithZeroArmies_AddFive_AddArmiesCalledOnTerritory() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.addArmies(5);
        EasyMock.expectLastCall();
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        map.addArmies(TerritoryName.ALASKA, 5);

        EasyMock.verify(mockAlaska);
    }

    @Test
    public void AddArmies_TerritoryWithOneArmy_AddOne_AddArmiesCalledOnTerritory() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.addArmies(1);
        EasyMock.expectLastCall();
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        map.addArmies(TerritoryName.ALASKA, 1);

        EasyMock.verify(mockAlaska);
    }

    @Test
    public void AddArmies_TerritoryWithThreeArmies_AddOne_AddArmiesCalledOnTerritory() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.addArmies(1);
        EasyMock.expectLastCall();
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        map.addArmies(TerritoryName.ALASKA, 1);

        EasyMock.verify(mockAlaska);
    }

    @Test
    public void AddArmies_TerritoryWithThreeArmies_AddFive_AddArmiesCalledOnTerritory() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.addArmies(5);
        EasyMock.expectLastCall();
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        map.addArmies(TerritoryName.ALASKA, 5);

        EasyMock.verify(mockAlaska);
    }

    @Test
    public void AddArmies_CountOfZero_ThrowsIllegalArgumentException() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.addArmies(0);
        EasyMock.expectLastCall().andThrow(new IllegalArgumentException());
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        assertThrows(IllegalArgumentException.class, () -> map.addArmies(TerritoryName.ALASKA, 0));
        EasyMock.verify(mockAlaska);
    }

    @Test
    public void AddArmies_NegativeCount_ThrowsIllegalArgumentException() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.addArmies(-1);
        EasyMock.expectLastCall().andThrow(new IllegalArgumentException());
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        assertThrows(IllegalArgumentException.class, () -> map.addArmies(TerritoryName.ALASKA, -1));
        EasyMock.verify(mockAlaska);
    }

    @Test
    public void CountTerritoriesOwnedBy_PlayerOwnsNone_ReturnsZero() {
        Map<TerritoryName, Territory> territories = new HashMap<>();
        for (TerritoryName name : TerritoryName.values()) {
            Territory mock = EasyMock.createMock(Territory.class);
            EasyMock.expect(mock.isOwnedBy(PlayerColor.RED)).andStubReturn(false);
            EasyMock.replay(mock);
            territories.put(name, mock);
        }
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        WorldMap map = new WorldMap(territories, neighbors);

        assertEquals(0, map.countTerritoriesOwnedBy(PlayerColor.RED));
    }

    @Test
    public void CountTerritoriesOwnedBy_PlayerOwnsOne_ReturnsOne() {
        Map<TerritoryName, Territory> territories = new HashMap<>();
        for (TerritoryName name : TerritoryName.values()) {
            Territory mock = EasyMock.createMock(Territory.class);
            if (name == TerritoryName.ALASKA) {
                EasyMock.expect(mock.isOwnedBy(PlayerColor.RED)).andStubReturn(true);
            } else {
                EasyMock.expect(mock.isOwnedBy(PlayerColor.RED)).andStubReturn(false);
            }
            EasyMock.replay(mock);
            territories.put(name, mock);
        }
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        WorldMap map = new WorldMap(territories, neighbors);
        assertEquals(1, map.countTerritoriesOwnedBy(PlayerColor.RED));
    }

    @Test
    public void CountTerritoriesOwnedBy_PlayerOwnsThree_ReturnsThree() {
        Map<TerritoryName, Territory> territories = new HashMap<>();
        Set<TerritoryName> ownedByRed = Set.of(
                TerritoryName.ALASKA, TerritoryName.ALBERTA, TerritoryName.ONTARIO);
        for (TerritoryName name : TerritoryName.values()) {
            Territory mock = EasyMock.createMock(Territory.class);
            EasyMock.expect(mock.isOwnedBy(PlayerColor.RED))
                    .andStubReturn(ownedByRed.contains(name));
            EasyMock.replay(mock);
            territories.put(name, mock);
        }
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        WorldMap map = new WorldMap(territories, neighbors);

        assertEquals(3, map.countTerritoriesOwnedBy(PlayerColor.RED));
    }

    @Test
    public void CountTerritoriesOwnedBy_PlayerOwnsAll_ReturnsFortyTwo() {
        Map<TerritoryName, Territory> territories = new HashMap<>();
        for (TerritoryName name : TerritoryName.values()) {
            Territory mock = EasyMock.createMock(Territory.class);
            EasyMock.expect(mock.isOwnedBy(PlayerColor.RED)).andStubReturn(true);
            EasyMock.replay(mock);
            territories.put(name, mock);
        }
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        WorldMap map = new WorldMap(territories, neighbors);

        assertEquals(42, map.countTerritoriesOwnedBy(PlayerColor.RED));
    }

    @Test
    public void GetTerritoriesOwnedBy_PlayerOwnsNone_ReturnsEmptySet() {
        Map<TerritoryName, Territory> territories = new HashMap<>();
        for (TerritoryName name : TerritoryName.values()) {
            Territory mock = EasyMock.createMock(Territory.class);
            EasyMock.expect(mock.isOwnedBy(PlayerColor.RED)).andStubReturn(false);
            EasyMock.replay(mock);
            territories.put(name, mock);
        }
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        WorldMap map = new WorldMap(territories, neighbors);

        assertTrue(map.getTerritoriesOwnedBy(PlayerColor.RED).isEmpty());
    }

    @Test
    public void GetTerritoriesOwnedBy_PlayerOwnsAlaska_ReturnsSetWithAlaska() {
        Map<TerritoryName, Territory> territories = new HashMap<>();
        for (TerritoryName name : TerritoryName.values()) {
            Territory mock = EasyMock.createMock(Territory.class);
            EasyMock.expect(mock.isOwnedBy(PlayerColor.RED))
                    .andStubReturn(name == TerritoryName.ALASKA);
            EasyMock.replay(mock);
            territories.put(name, mock);
        }
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        WorldMap map = new WorldMap(territories, neighbors);
        Set<TerritoryName> result = map.getTerritoriesOwnedBy(PlayerColor.RED);

        assertEquals(1, result.size());
        assertTrue(result.contains(TerritoryName.ALASKA));
    }

    @Test
    public void GetTerritoriesOwnedBy_PlayerOwnsThree_ReturnsSetOfThree() {
        Map<TerritoryName, Territory> territories = new HashMap<>();
        Set<TerritoryName> ownedByRed = Set.of(
                TerritoryName.ALASKA, TerritoryName.ALBERTA, TerritoryName.ONTARIO);
        for (TerritoryName name : TerritoryName.values()) {
            Territory mock = EasyMock.createMock(Territory.class);
            EasyMock.expect(mock.isOwnedBy(PlayerColor.RED))
                    .andStubReturn(ownedByRed.contains(name));
            EasyMock.replay(mock);
            territories.put(name, mock);
        }
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        WorldMap map = new WorldMap(territories, neighbors);
        Set<TerritoryName> result = map.getTerritoriesOwnedBy(PlayerColor.RED);

        assertEquals(3, result.size());
        assertTrue(result.containsAll(ownedByRed));
    }

    @Test
    public void GetTerritoriesOwnedBy_PlayerOwnsAll_ReturnsAllFortyTwo() {
        Map<TerritoryName, Territory> territories = new HashMap<>();
        for (TerritoryName name : TerritoryName.values()) {
            Territory mock = EasyMock.createMock(Territory.class);
            EasyMock.expect(mock.isOwnedBy(PlayerColor.RED)).andStubReturn(true);
            EasyMock.replay(mock);
            territories.put(name, mock);
        }
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        WorldMap map = new WorldMap(territories, neighbors);
        Set<TerritoryName> result = map.getTerritoriesOwnedBy(PlayerColor.RED);

        assertEquals(42, result.size());
        assertTrue(result.containsAll(Set.of(TerritoryName.values())));
    }

    @Test
    public void RemoveArmies_OneFromTerritoryWithOneArmy_RemoveArmiesCalledOnTerritory() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.removeArmies(1);
        EasyMock.expectLastCall();
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        map.removeArmies(TerritoryName.ALASKA, 1);

        EasyMock.verify(mockAlaska);
    }

    @Test
    public void RemoveArmies_OneFromTerritoryWithMoreThanOneArmy_RemoveArmiesCalledOnTerritory() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.removeArmies(1);
        EasyMock.expectLastCall();
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        map.removeArmies(TerritoryName.ALASKA, 1);

        EasyMock.verify(mockAlaska);
    }

    @Test
    public void RemoveArmies_MoreThanOneTerritoryWithMoreArmies_RemoveArmiesCalledOnTerritory() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.removeArmies(3);
        EasyMock.expectLastCall();
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        map.removeArmies(TerritoryName.ALASKA, 3);

        EasyMock.verify(mockAlaska);
    }

    @Test
    public void RemoveArmies_ExactAmountTerritoryHas_RemoveArmiesCalledOnTerritory() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.removeArmies(3);
        EasyMock.expectLastCall();
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        map.removeArmies(TerritoryName.ALASKA, 3);

        EasyMock.verify(mockAlaska);
    }

    @Test
    public void RemoveArmies_MoreThanTerritoryHas_ThrowsIllegalArgumentException() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.removeArmies(3);
        EasyMock.expectLastCall().andThrow(new IllegalArgumentException());
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        assertThrows(IllegalArgumentException.class,
                () -> map.removeArmies(TerritoryName.ALASKA, 3));
        EasyMock.verify(mockAlaska);
    }

    @Test
    public void RemoveArmies_CountOfZero_ThrowsIllegalArgumentException() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.removeArmies(0);
        EasyMock.expectLastCall().andThrow(new IllegalArgumentException());
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        assertThrows(IllegalArgumentException.class,
                () -> map.removeArmies(TerritoryName.ALASKA, 0));
        EasyMock.verify(mockAlaska);
    }

    @Test
    public void RemoveArmies_NegativeCount_ThrowsIllegalArgumentException() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.removeArmies(-1);
        EasyMock.expectLastCall().andThrow(new IllegalArgumentException());
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        assertThrows(IllegalArgumentException.class,
                () -> map.removeArmies(TerritoryName.ALASKA, -1));
        EasyMock.verify(mockAlaska);
    }

    @Test
    public void RemoveArmies_OneFromTerritoryWithZeroArmies_ThrowsIllegalArgumentException() {
        final Map<TerritoryName, Territory> territories = createTerritories();
        final Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.removeArmies(1);
        EasyMock.expectLastCall().andThrow(new IllegalArgumentException());
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        assertThrows(IllegalArgumentException.class,
                () -> map.removeArmies(TerritoryName.ALASKA, 1));
        EasyMock.verify(mockAlaska);
    }

    @Test
    public void AreNeighbors_DefaultMap_AlaskaAndAlbertaAreNeighbors() {
        WorldMap map = new WorldMap();
        assertTrue(map.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALBERTA));
    }

    @Test
    public void AreNeighbors_DefaultMap_AlbertaAndAlaskaAreNeighbors() {
        WorldMap map = new WorldMap();
        assertTrue(map.areNeighbors(TerritoryName.ALBERTA, TerritoryName.ALASKA));
    }

    @Test
    public void AreNeighbors_DefaultMap_AllRiskBordersAreBidirectional() {
        WorldMap map = new WorldMap();
        assertBidirectionalNeighbors(map, TerritoryName.ALASKA, TerritoryName.NORTHWEST_TERRITORY);
        assertBidirectionalNeighbors(map, TerritoryName.ALASKA, TerritoryName.ALBERTA);
        assertBidirectionalNeighbors(map, TerritoryName.ALASKA, TerritoryName.KAMCHATKA);
        assertBidirectionalNeighbors(
                map, TerritoryName.NORTHWEST_TERRITORY, TerritoryName.ALBERTA);
        assertBidirectionalNeighbors(
                map, TerritoryName.NORTHWEST_TERRITORY, TerritoryName.ONTARIO);
        assertBidirectionalNeighbors(
                map, TerritoryName.NORTHWEST_TERRITORY, TerritoryName.GREENLAND);
        assertBidirectionalNeighbors(map, TerritoryName.GREENLAND, TerritoryName.ONTARIO);
        assertBidirectionalNeighbors(map, TerritoryName.GREENLAND, TerritoryName.QUEBEC);
        assertBidirectionalNeighbors(map, TerritoryName.GREENLAND, TerritoryName.ICELAND);
        assertBidirectionalNeighbors(map, TerritoryName.ALBERTA, TerritoryName.ONTARIO);
        assertBidirectionalNeighbors(
                map, TerritoryName.ALBERTA, TerritoryName.WESTERN_UNITED_STATES);
        assertBidirectionalNeighbors(map, TerritoryName.ONTARIO, TerritoryName.QUEBEC);
        assertBidirectionalNeighbors(
                map, TerritoryName.ONTARIO, TerritoryName.WESTERN_UNITED_STATES);
        assertBidirectionalNeighbors(
                map, TerritoryName.ONTARIO, TerritoryName.EASTERN_UNITED_STATES);
        assertBidirectionalNeighbors(
                map, TerritoryName.QUEBEC, TerritoryName.EASTERN_UNITED_STATES);
        assertBidirectionalNeighbors(
                map, TerritoryName.WESTERN_UNITED_STATES, TerritoryName.EASTERN_UNITED_STATES);
        assertBidirectionalNeighbors(
                map, TerritoryName.WESTERN_UNITED_STATES, TerritoryName.CENTRAL_AMERICA);
        assertBidirectionalNeighbors(
                map, TerritoryName.EASTERN_UNITED_STATES, TerritoryName.CENTRAL_AMERICA);
        assertBidirectionalNeighbors(
                map, TerritoryName.CENTRAL_AMERICA, TerritoryName.VENEZUELA);
        assertBidirectionalNeighbors(map, TerritoryName.VENEZUELA, TerritoryName.PERU);
        assertBidirectionalNeighbors(map, TerritoryName.VENEZUELA, TerritoryName.BRAZIL);
        assertBidirectionalNeighbors(map, TerritoryName.PERU, TerritoryName.BRAZIL);
        assertBidirectionalNeighbors(map, TerritoryName.PERU, TerritoryName.ARGENTINA);
        assertBidirectionalNeighbors(map, TerritoryName.BRAZIL, TerritoryName.ARGENTINA);
        assertBidirectionalNeighbors(map, TerritoryName.BRAZIL, TerritoryName.NORTH_AFRICA);
        assertBidirectionalNeighbors(map, TerritoryName.ICELAND, TerritoryName.GREAT_BRITAIN);
        assertBidirectionalNeighbors(map, TerritoryName.ICELAND, TerritoryName.SCANDINAVIA);
        assertBidirectionalNeighbors(
                map, TerritoryName.GREAT_BRITAIN, TerritoryName.SCANDINAVIA);
        assertBidirectionalNeighbors(
                map, TerritoryName.GREAT_BRITAIN, TerritoryName.NORTHERN_EUROPE);
        assertBidirectionalNeighbors(
                map, TerritoryName.GREAT_BRITAIN, TerritoryName.WESTERN_EUROPE);
        assertBidirectionalNeighbors(
                map, TerritoryName.SCANDINAVIA, TerritoryName.NORTHERN_EUROPE);
        assertBidirectionalNeighbors(map, TerritoryName.SCANDINAVIA, TerritoryName.UKRAINE);
        assertBidirectionalNeighbors(
                map, TerritoryName.NORTHERN_EUROPE, TerritoryName.UKRAINE);
        assertBidirectionalNeighbors(
                map, TerritoryName.NORTHERN_EUROPE, TerritoryName.SOUTHERN_EUROPE);
        assertBidirectionalNeighbors(
                map, TerritoryName.NORTHERN_EUROPE, TerritoryName.WESTERN_EUROPE);
        assertBidirectionalNeighbors(
                map, TerritoryName.WESTERN_EUROPE, TerritoryName.SOUTHERN_EUROPE);
        assertBidirectionalNeighbors(
                map, TerritoryName.WESTERN_EUROPE, TerritoryName.NORTH_AFRICA);
        assertBidirectionalNeighbors(
                map, TerritoryName.SOUTHERN_EUROPE, TerritoryName.UKRAINE);
        assertBidirectionalNeighbors(
                map, TerritoryName.SOUTHERN_EUROPE, TerritoryName.NORTH_AFRICA);
        assertBidirectionalNeighbors(map, TerritoryName.SOUTHERN_EUROPE, TerritoryName.EGYPT);
        assertBidirectionalNeighbors(
                map, TerritoryName.SOUTHERN_EUROPE, TerritoryName.MIDDLE_EAST);
        assertBidirectionalNeighbors(map, TerritoryName.UKRAINE, TerritoryName.MIDDLE_EAST);
        assertBidirectionalNeighbors(map, TerritoryName.UKRAINE, TerritoryName.AFGHANISTAN);
        assertBidirectionalNeighbors(map, TerritoryName.UKRAINE, TerritoryName.URAL);
        assertBidirectionalNeighbors(map, TerritoryName.NORTH_AFRICA, TerritoryName.EGYPT);
        assertBidirectionalNeighbors(
                map, TerritoryName.NORTH_AFRICA, TerritoryName.EAST_AFRICA);
        assertBidirectionalNeighbors(map, TerritoryName.NORTH_AFRICA, TerritoryName.CONGO);
        assertBidirectionalNeighbors(map, TerritoryName.EGYPT, TerritoryName.MIDDLE_EAST);
        assertBidirectionalNeighbors(map, TerritoryName.EGYPT, TerritoryName.EAST_AFRICA);
        assertBidirectionalNeighbors(map, TerritoryName.EAST_AFRICA, TerritoryName.CONGO);
        assertBidirectionalNeighbors(map, TerritoryName.EAST_AFRICA, TerritoryName.SOUTH_AFRICA);
        assertBidirectionalNeighbors(map, TerritoryName.EAST_AFRICA, TerritoryName.MADAGASCAR);
        assertBidirectionalNeighbors(map, TerritoryName.EAST_AFRICA, TerritoryName.MIDDLE_EAST);
        assertBidirectionalNeighbors(map, TerritoryName.CONGO, TerritoryName.SOUTH_AFRICA);
        assertBidirectionalNeighbors(map, TerritoryName.SOUTH_AFRICA, TerritoryName.MADAGASCAR);
        assertBidirectionalNeighbors(map, TerritoryName.MIDDLE_EAST, TerritoryName.AFGHANISTAN);
        assertBidirectionalNeighbors(map, TerritoryName.MIDDLE_EAST, TerritoryName.INDIA);
        assertBidirectionalNeighbors(map, TerritoryName.AFGHANISTAN, TerritoryName.URAL);
        assertBidirectionalNeighbors(map, TerritoryName.AFGHANISTAN, TerritoryName.CHINA);
        assertBidirectionalNeighbors(map, TerritoryName.AFGHANISTAN, TerritoryName.INDIA);
        assertBidirectionalNeighbors(map, TerritoryName.URAL, TerritoryName.CHINA);
        assertBidirectionalNeighbors(map, TerritoryName.URAL, TerritoryName.SIBERIA);
        assertBidirectionalNeighbors(map, TerritoryName.SIBERIA, TerritoryName.CHINA);
        assertBidirectionalNeighbors(map, TerritoryName.SIBERIA, TerritoryName.MONGOLIA);
        assertBidirectionalNeighbors(map, TerritoryName.SIBERIA, TerritoryName.IRKUTSK);
        assertBidirectionalNeighbors(map, TerritoryName.SIBERIA, TerritoryName.YAKUTSK);
        assertBidirectionalNeighbors(map, TerritoryName.YAKUTSK, TerritoryName.IRKUTSK);
        assertBidirectionalNeighbors(map, TerritoryName.YAKUTSK, TerritoryName.KAMCHATKA);
        assertBidirectionalNeighbors(map, TerritoryName.KAMCHATKA, TerritoryName.IRKUTSK);
        assertBidirectionalNeighbors(map, TerritoryName.KAMCHATKA, TerritoryName.MONGOLIA);
        assertBidirectionalNeighbors(map, TerritoryName.KAMCHATKA, TerritoryName.JAPAN);
        assertBidirectionalNeighbors(map, TerritoryName.IRKUTSK, TerritoryName.MONGOLIA);
        assertBidirectionalNeighbors(map, TerritoryName.MONGOLIA, TerritoryName.CHINA);
        assertBidirectionalNeighbors(map, TerritoryName.MONGOLIA, TerritoryName.JAPAN);
        assertBidirectionalNeighbors(map, TerritoryName.CHINA, TerritoryName.INDIA);
        assertBidirectionalNeighbors(map, TerritoryName.CHINA, TerritoryName.SIAM);
        assertBidirectionalNeighbors(map, TerritoryName.INDIA, TerritoryName.SIAM);
        assertBidirectionalNeighbors(map, TerritoryName.SIAM, TerritoryName.INDONESIA);
        assertBidirectionalNeighbors(map, TerritoryName.INDONESIA, TerritoryName.NEW_GUINEA);
        assertBidirectionalNeighbors(
                map, TerritoryName.INDONESIA, TerritoryName.WESTERN_AUSTRALIA);
        assertBidirectionalNeighbors(
                map, TerritoryName.NEW_GUINEA, TerritoryName.WESTERN_AUSTRALIA);
        assertBidirectionalNeighbors(
                map, TerritoryName.NEW_GUINEA, TerritoryName.EASTERN_AUSTRALIA);
        assertBidirectionalNeighbors(
                map, TerritoryName.WESTERN_AUSTRALIA, TerritoryName.EASTERN_AUSTRALIA);
    }

    @Test
    public void AreConnectedThrough_AdjacentTerritoriesOwnedBySamePlayer_ReturnsTrue() {
        WorldMap map = new WorldMap();
        map.claim(TerritoryName.ALASKA, PlayerColor.RED);
        map.claim(TerritoryName.ALBERTA, PlayerColor.RED);
        assertTrue(map.areConnectedThrough(
                TerritoryName.ALASKA, TerritoryName.ALBERTA, PlayerColor.RED));
    }

    @Test
    public void AreConnectedThrough_NonAdjacentTerritoriesConnectedThroughOwnedChain_ReturnsTrue() {
        WorldMap map = new WorldMap();
        map.claim(TerritoryName.ALASKA, PlayerColor.RED);
        map.claim(TerritoryName.NORTHWEST_TERRITORY, PlayerColor.RED);
        map.claim(TerritoryName.ONTARIO, PlayerColor.RED);
        assertTrue(map.areConnectedThrough(
                TerritoryName.ALASKA, TerritoryName.ONTARIO, PlayerColor.RED));
    }

    @Test
    public void AreConnectedThrough_IntermediateTerritoryNotOwned_ReturnsFalse() {
        WorldMap map = new WorldMap();
        map.claim(TerritoryName.ALASKA, PlayerColor.RED);
        map.claim(TerritoryName.ONTARIO, PlayerColor.RED);
        assertFalse(map.areConnectedThrough(
                TerritoryName.ALASKA, TerritoryName.ONTARIO, PlayerColor.RED));
    }

    @Test
    public void AreConnectedThrough_DestinationNotOwnedByPlayer_ReturnsFalse() {
        WorldMap map = new WorldMap();
        map.claim(TerritoryName.ALASKA, PlayerColor.RED);
        map.claim(TerritoryName.NORTHWEST_TERRITORY, PlayerColor.RED);
        map.claim(TerritoryName.ONTARIO, PlayerColor.BLUE);
        assertFalse(map.areConnectedThrough(
                TerritoryName.ALASKA, TerritoryName.ONTARIO, PlayerColor.RED));
    }

    @Test
    public void AreConnectedThrough_SourceNotOwnedByPlayer_ReturnsFalse() {
        WorldMap map = new WorldMap();
        map.claim(TerritoryName.ALASKA, PlayerColor.BLUE);
        map.claim(TerritoryName.NORTHWEST_TERRITORY, PlayerColor.RED);
        map.claim(TerritoryName.ONTARIO, PlayerColor.RED);
        assertFalse(map.areConnectedThrough(
                TerritoryName.ALASKA, TerritoryName.ONTARIO, PlayerColor.RED));
    }
}
