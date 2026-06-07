package domain;

class Territory {
    private final TerritoryName name;
    private int armies;
    private PlayerColor owner;

    Territory(TerritoryName name) {
        this.name = name;
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
        if (count < 1) {
            throw new IllegalArgumentException("count must be at least 1");
        }
        this.armies += count;
    }

    void removeArmies(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("count must be at least 1");
        }
        if (count > armies) {
            throw new IllegalArgumentException("count cannot exceed current armies");
        }
        this.armies -= count;
    }

    void claim(PlayerColor color) {
        if (owner != null) {
            throw new IllegalStateException("territory is already claimed");
        }
        this.owner = color;
    }

    boolean isOwnedBy(PlayerColor color) {
        return color == owner;
    }

    void transferOwner(PlayerColor color) {
        this.owner = color;
    }
}