package domain;

class Territory {
    private final TerritoryName name;
    private int armies;
    private PlayerColor owner;

    Territory(TerritoryName name) {
        this.name = name;
        this.armies = 0;
        this.owner = null;
    }

    TerritoryName getName() {
        return name;
    }

    int getArmies() {
        return armies;
    }

    boolean isUnclaimed() {
        return owner == null;
    }

    void addArmies(int count) {
        this.armies += count;
    }

    void claim(PlayerColor color) {
        if (owner != null) {
            throw new IllegalStateException("Territory is already claimed");
        }
        this.owner = color;
    }
    boolean isOwnedBy(PlayerColor color) {
        return color == owner;
    }
}