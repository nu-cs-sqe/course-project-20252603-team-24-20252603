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
}