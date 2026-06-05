package domain;

public class Card {
    private final CardType type;
    private final TerritoryName territory;

    public Card(CardType type, TerritoryName territory) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        if (type != CardType.WILD && territory == null) {
            throw new IllegalArgumentException("non-wild cards must have a territory");
        }
        if (type == CardType.WILD && territory != null) {
            throw new IllegalArgumentException("wild cards cannot have a territory");
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

    boolean isWild() {
        return type == CardType.WILD;
    }

    boolean matchesTerritory(TerritoryName territory) {
        return this.territory == territory;
    }
}
