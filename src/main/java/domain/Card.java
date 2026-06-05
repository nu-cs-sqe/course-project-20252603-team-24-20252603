package domain;

class Card {
    private final CardType type;
    private final TerritoryName territory;

    Card(CardType type, TerritoryName territory) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
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
