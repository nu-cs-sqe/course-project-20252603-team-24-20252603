package domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
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

    boolean areConnectedThrough(TerritoryName from, TerritoryName to, PlayerColor owner) {
        if (!isOwnedBy(from, owner)) {
            return false;
        }
        Set<TerritoryName> visited = new HashSet<>();
        Queue<TerritoryName> queue = new LinkedList<>();
        queue.add(from);
        visited.add(from);
        while (!queue.isEmpty()) {
            TerritoryName current = queue.poll();
            for (TerritoryName neighbor : neighbors.get(current)) {
                if (neighbor.equals(to)) {
                    return isOwnedBy(to, owner);
                }
                if (isOwnedBy(neighbor, owner) && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return false;
    }

    private void initializeTerritories() {
        for (TerritoryName name : TerritoryName.values()) {
            territories.put(name, new Territory(name));
            neighbors.put(name, new HashSet<>());
        }
    }

    private void initializeNeighbors() {
        //North America
        addNeighbors(TerritoryName.ALASKA, TerritoryName.NORTHWEST_TERRITORY);
        addNeighbors(TerritoryName.ALASKA, TerritoryName.ALBERTA);
        addNeighbors(TerritoryName.ALASKA, TerritoryName.KAMCHATKA);
        addNeighbors(TerritoryName.NORTHWEST_TERRITORY, TerritoryName.ALBERTA);
        addNeighbors(TerritoryName.NORTHWEST_TERRITORY, TerritoryName.ONTARIO);
        addNeighbors(TerritoryName.NORTHWEST_TERRITORY, TerritoryName.GREENLAND);
        addNeighbors(TerritoryName.GREENLAND, TerritoryName.ONTARIO);
        addNeighbors(TerritoryName.GREENLAND, TerritoryName.QUEBEC);
        addNeighbors(TerritoryName.GREENLAND, TerritoryName.ICELAND);
        addNeighbors(TerritoryName.ALBERTA, TerritoryName.ONTARIO);
        addNeighbors(TerritoryName.ALBERTA, TerritoryName.WESTERN_UNITED_STATES);
        addNeighbors(TerritoryName.ONTARIO, TerritoryName.QUEBEC);
        addNeighbors(TerritoryName.ONTARIO, TerritoryName.WESTERN_UNITED_STATES);
        addNeighbors(TerritoryName.ONTARIO, TerritoryName.EASTERN_UNITED_STATES);
        addNeighbors(TerritoryName.QUEBEC, TerritoryName.EASTERN_UNITED_STATES);
        addNeighbors(TerritoryName.WESTERN_UNITED_STATES, TerritoryName.EASTERN_UNITED_STATES);
        addNeighbors(TerritoryName.WESTERN_UNITED_STATES, TerritoryName.CENTRAL_AMERICA);
        addNeighbors(TerritoryName.EASTERN_UNITED_STATES, TerritoryName.CENTRAL_AMERICA);
        addNeighbors(TerritoryName.CENTRAL_AMERICA, TerritoryName.VENEZUELA);

        //South America
        addNeighbors(TerritoryName.VENEZUELA, TerritoryName.PERU);
        addNeighbors(TerritoryName.VENEZUELA, TerritoryName.BRAZIL);
        addNeighbors(TerritoryName.PERU, TerritoryName.BRAZIL);
        addNeighbors(TerritoryName.PERU, TerritoryName.ARGENTINA);
        addNeighbors(TerritoryName.BRAZIL, TerritoryName.ARGENTINA);
        addNeighbors(TerritoryName.BRAZIL, TerritoryName.NORTH_AFRICA);

        //Europe
        addNeighbors(TerritoryName.ICELAND, TerritoryName.GREAT_BRITAIN);
        addNeighbors(TerritoryName.ICELAND, TerritoryName.SCANDINAVIA);
        addNeighbors(TerritoryName.GREAT_BRITAIN, TerritoryName.SCANDINAVIA);
        addNeighbors(TerritoryName.GREAT_BRITAIN, TerritoryName.NORTHERN_EUROPE);
        addNeighbors(TerritoryName.GREAT_BRITAIN, TerritoryName.WESTERN_EUROPE);
        addNeighbors(TerritoryName.SCANDINAVIA, TerritoryName.NORTHERN_EUROPE);
        addNeighbors(TerritoryName.SCANDINAVIA, TerritoryName.UKRAINE);
        addNeighbors(TerritoryName.NORTHERN_EUROPE, TerritoryName.UKRAINE);
        addNeighbors(TerritoryName.NORTHERN_EUROPE, TerritoryName.SOUTHERN_EUROPE);
        addNeighbors(TerritoryName.NORTHERN_EUROPE, TerritoryName.WESTERN_EUROPE);
        addNeighbors(TerritoryName.WESTERN_EUROPE, TerritoryName.SOUTHERN_EUROPE);
        addNeighbors(TerritoryName.WESTERN_EUROPE, TerritoryName.NORTH_AFRICA);
        addNeighbors(TerritoryName.SOUTHERN_EUROPE, TerritoryName.UKRAINE);
        addNeighbors(TerritoryName.SOUTHERN_EUROPE, TerritoryName.NORTH_AFRICA);
        addNeighbors(TerritoryName.SOUTHERN_EUROPE, TerritoryName.EGYPT);
        addNeighbors(TerritoryName.SOUTHERN_EUROPE, TerritoryName.MIDDLE_EAST);
        addNeighbors(TerritoryName.UKRAINE, TerritoryName.MIDDLE_EAST);
        addNeighbors(TerritoryName.UKRAINE, TerritoryName.AFGHANISTAN);
        addNeighbors(TerritoryName.UKRAINE, TerritoryName.URAL);

        //Africa
        addNeighbors(TerritoryName.NORTH_AFRICA, TerritoryName.EGYPT);
        addNeighbors(TerritoryName.NORTH_AFRICA, TerritoryName.EAST_AFRICA);
        addNeighbors(TerritoryName.NORTH_AFRICA, TerritoryName.CONGO);
        addNeighbors(TerritoryName.EGYPT, TerritoryName.MIDDLE_EAST);
        addNeighbors(TerritoryName.EGYPT, TerritoryName.EAST_AFRICA);
        addNeighbors(TerritoryName.EAST_AFRICA, TerritoryName.CONGO);
        addNeighbors(TerritoryName.EAST_AFRICA, TerritoryName.SOUTH_AFRICA);
        addNeighbors(TerritoryName.EAST_AFRICA, TerritoryName.MADAGASCAR);
        addNeighbors(TerritoryName.EAST_AFRICA, TerritoryName.MIDDLE_EAST);
        addNeighbors(TerritoryName.CONGO, TerritoryName.SOUTH_AFRICA);
        addNeighbors(TerritoryName.SOUTH_AFRICA, TerritoryName.MADAGASCAR);

        //Asia
        addNeighbors(TerritoryName.MIDDLE_EAST, TerritoryName.AFGHANISTAN);
        addNeighbors(TerritoryName.MIDDLE_EAST, TerritoryName.INDIA);
        addNeighbors(TerritoryName.AFGHANISTAN, TerritoryName.URAL);
        addNeighbors(TerritoryName.AFGHANISTAN, TerritoryName.CHINA);
        addNeighbors(TerritoryName.AFGHANISTAN, TerritoryName.INDIA);
        addNeighbors(TerritoryName.URAL, TerritoryName.CHINA);
        addNeighbors(TerritoryName.URAL, TerritoryName.SIBERIA);
        addNeighbors(TerritoryName.SIBERIA, TerritoryName.CHINA);
        addNeighbors(TerritoryName.SIBERIA, TerritoryName.MONGOLIA);
        addNeighbors(TerritoryName.SIBERIA, TerritoryName.IRKUTSK);
        addNeighbors(TerritoryName.SIBERIA, TerritoryName.YAKUTSK);
        addNeighbors(TerritoryName.YAKUTSK, TerritoryName.IRKUTSK);
        addNeighbors(TerritoryName.YAKUTSK, TerritoryName.KAMCHATKA);
        addNeighbors(TerritoryName.KAMCHATKA, TerritoryName.IRKUTSK);
        addNeighbors(TerritoryName.KAMCHATKA, TerritoryName.MONGOLIA);
        addNeighbors(TerritoryName.KAMCHATKA, TerritoryName.JAPAN);
        addNeighbors(TerritoryName.IRKUTSK, TerritoryName.MONGOLIA);
        addNeighbors(TerritoryName.MONGOLIA, TerritoryName.CHINA);
        addNeighbors(TerritoryName.MONGOLIA, TerritoryName.JAPAN);
        addNeighbors(TerritoryName.CHINA, TerritoryName.INDIA);
        addNeighbors(TerritoryName.CHINA, TerritoryName.SIAM);
        addNeighbors(TerritoryName.INDIA, TerritoryName.SIAM);
        addNeighbors(TerritoryName.SIAM, TerritoryName.INDONESIA);

        //Australia
        addNeighbors(TerritoryName.INDONESIA, TerritoryName.NEW_GUINEA);
        addNeighbors(TerritoryName.INDONESIA, TerritoryName.WESTERN_AUSTRALIA);
        addNeighbors(TerritoryName.NEW_GUINEA, TerritoryName.WESTERN_AUSTRALIA);
        addNeighbors(TerritoryName.NEW_GUINEA, TerritoryName.EASTERN_AUSTRALIA);
        addNeighbors(TerritoryName.WESTERN_AUSTRALIA, TerritoryName.EASTERN_AUSTRALIA);
    }

    private void addNeighbors(TerritoryName a, TerritoryName b) {
        neighbors.get(a).add(b);
        neighbors.get(b).add(a);
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

    void removeArmies(TerritoryName territory, int count) {
        territories.get(territory).removeArmies(count);
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

    Set<TerritoryName> getTerritoriesOwnedBy(PlayerColor color) {
        Set<TerritoryName> owned = new HashSet<>();
        for (Map.Entry<TerritoryName, Territory> entry : territories.entrySet()) {
            if (entry.getValue().isOwnedBy(color)) {
                owned.add(entry.getKey());
            }
        }
        return owned;
    }

    void assignTerritory(TerritoryName territory, PlayerColor color) {
        territories.get(territory).assignTerritory(color);
    }
}