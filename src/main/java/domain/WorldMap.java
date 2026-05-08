package domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class WorldMap {
    private final Map<TerritoryName, Territory> territories;
    private final Map<TerritoryName, Set<TerritoryName>> neighbors;

    WorldMap() {
        this.territories = new HashMap<>();
        this.neighbors = new HashMap<>();
        initializeTerritories();
        initializeNeighbors();
    }

    WorldMap(Map<TerritoryName, Territory> territories, Map<TerritoryName, Set<TerritoryName>> neighbors) {
        this.territories = territories;
        this.neighbors = neighbors;
    }

    boolean areNeighbors(TerritoryName first, TerritoryName second) {
        return neighbors.containsKey(first) &&
                neighbors.get(first).contains(second);
    }

    private void initializeTerritories() {
        for (TerritoryName name : TerritoryName.values()) {
            territories.put(name, new Territory(name));
            neighbors.put(name, new HashSet<>());
        }
    }

    private void initializeNeighbors() {
    }

    boolean isOwnedBy(TerritoryName territory, PlayerColor color) {
        return territories.get(territory).isOwnedBy(color);
    }

    boolean isUnclaimed(TerritoryName territory) {
        return territories.get(territory).isUnclaimed();
    }

    int getArmies(TerritoryName territory) {
        return territories.get(territory).getArmies();
    }

    void claim(TerritoryName territory, PlayerColor color) {
        territories.get(territory).claim(color);
    }

    void addArmies(TerritoryName territory, int count) {
        territories.get(territory).addArmies(count);
    }

    int countTerritoriesOwnedBy(PlayerColor color) {
        int count = 0;
        for (Territory territory : territories.values()) {
            if (territory.isOwnedBy(color)) {
                count++;
            }
        }
        return count;
    }
}