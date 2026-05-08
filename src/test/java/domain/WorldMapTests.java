package domain;

import org.junit.jupiter.api.Test;
import org.easymock.EasyMock;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void AreNeighbors_AlaskaAndAlberta_ReturnsTrue() {
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        neighbors.get(TerritoryName.ALASKA).add(TerritoryName.ALBERTA);
        neighbors.get(TerritoryName.ALBERTA).add(TerritoryName.ALASKA);
        WorldMap map = new WorldMap(territories, neighbors);

        assertTrue(map.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALBERTA));
    }

    @Test
    public void AreNeighbors_AlaskaAndBrazil_ReturnsFalse() {
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        WorldMap map = new WorldMap(territories, neighbors);

        assertFalse(map.areNeighbors(TerritoryName.ALASKA, TerritoryName.BRAZIL));
    }

    @Test
    public void AreNeighbors_AlaskaAndAlaska_ReturnsFalse() {
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        WorldMap map = new WorldMap(territories, neighbors);

        assertFalse(map.areNeighbors(TerritoryName.ALASKA, TerritoryName.ALASKA));
    }

    @Test
    public void IsOwnedBy_UnclaimedTerritory_ReturnsFalse() {
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        EasyMock.expect(mockAlaska.isOwnedBy(PlayerColor.RED)).andStubReturn(false);
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);

        assertFalse(map.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED));
    }

    @Test
    public void IsOwnedBy_TerritoryClaimedByRed_WithRed_ReturnsTrue() {
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        EasyMock.expect(mockAlaska.isOwnedBy(PlayerColor.RED)).andStubReturn(true);
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);

        assertTrue(map.isOwnedBy(TerritoryName.ALASKA, PlayerColor.RED));
    }

    @Test
    public void IsOwnedBy_TerritoryClaimedByRed_WithBlue_ReturnsFalse() {
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        EasyMock.expect(mockAlaska.isOwnedBy(PlayerColor.BLUE)).andStubReturn(false);
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);

        assertFalse(map.isOwnedBy(TerritoryName.ALASKA, PlayerColor.BLUE));
    }

    @Test
    public void IsUnclaimed_UnclaimedTerritory_ReturnsTrue() {
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        EasyMock.expect(mockAlaska.isUnclaimed()).andStubReturn(true);
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);

        assertTrue(map.isUnclaimed(TerritoryName.ALASKA));
    }

    @Test
    public void IsUnclaimed_ClaimedTerritory_ReturnsFalse() {
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        EasyMock.expect(mockAlaska.isUnclaimed()).andStubReturn(false);
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);

        assertFalse(map.isUnclaimed(TerritoryName.ALASKA));
    }

    @Test
    public void GetArmies_TerritoryWithZeroArmies_ReturnsZero() {
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        EasyMock.expect(mockAlaska.getArmies()).andStubReturn(0);
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);

        assertEquals(0, map.getArmies(TerritoryName.ALASKA));
    }

    @Test
    public void GetArmies_TerritoryWithOneArmy_ReturnsOne() {
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        EasyMock.expect(mockAlaska.getArmies()).andStubReturn(1);
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);

        assertEquals(1, map.getArmies(TerritoryName.ALASKA));
    }

    @Test
    public void GetArmies_TerritoryWithFiveArmies_ReturnsFive() {
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        EasyMock.expect(mockAlaska.getArmies()).andStubReturn(5);
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);

        assertEquals(5, map.getArmies(TerritoryName.ALASKA));
    }

    @Test
    public void Claim_UnclaimedTerritory_ClaimIsCalledOnTerritory() {
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
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
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.claim(PlayerColor.RED);
        EasyMock.expectLastCall().andThrow(new IllegalStateException());
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        assertThrows(IllegalStateException.class, () -> map.claim(TerritoryName.ALASKA, PlayerColor.RED));
        EasyMock.verify(mockAlaska);
    }

    @Test
    public void Claim_AlreadyClaimedByDifferentPlayer_ThrowsIllegalStateException() {
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        Territory mockAlaska = EasyMock.createMock(Territory.class);
        mockAlaska.claim(PlayerColor.BLUE);
        EasyMock.expectLastCall().andThrow(new IllegalStateException());
        EasyMock.replay(mockAlaska);
        territories.put(TerritoryName.ALASKA, mockAlaska);
        WorldMap map = new WorldMap(territories, neighbors);
        assertThrows(IllegalStateException.class, () -> map.claim(TerritoryName.ALASKA, PlayerColor.BLUE));
        EasyMock.verify(mockAlaska);
    }

    @Test
    public void AddArmies_TerritoryWithZeroArmies_AddOne_AddArmiesCalledOnTerritory() {
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
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
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
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
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
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
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
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
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
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
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
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
        Map<TerritoryName, Territory> territories = createTerritories();
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
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
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
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
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
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
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
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
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
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
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
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
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
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
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
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
        Map<TerritoryName, Set<TerritoryName>> neighbors = createNeighbors();
        WorldMap map = new WorldMap(territories, neighbors);
        Set<TerritoryName> result = map.getTerritoriesOwnedBy(PlayerColor.RED);

        assertEquals(42, result.size());
        assertTrue(result.containsAll(Set.of(TerritoryName.values())));
    }
}