package domain;

class Card {
    private final CardType type;
    private final TerritoryName territory;

    Card(CardType type, TerritoryName territory) {
        this.type = type;
        this.territory = territory;
    }

    CardType getType() {
        return type;
    }

    TerritoryName getTerritory() {
        return territory;
    }
}
