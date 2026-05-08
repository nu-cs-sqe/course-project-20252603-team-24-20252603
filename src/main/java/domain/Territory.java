package domain;

class Territory {
    private final TerritoryName name;
    private int armies;

    Territory(TerritoryName name) {
        this.name = name;
        this.armies = 0;
    }

    TerritoryName getName() {
        return name;
    }

    int getArmies() {
        return armies;
    }
}