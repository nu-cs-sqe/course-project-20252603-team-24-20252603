package domain;

class Territory {
    private final TerritoryName name;

    Territory(TerritoryName name) {
        this.name = name;
    }

    TerritoryName getName() {
        return name;
    }
}