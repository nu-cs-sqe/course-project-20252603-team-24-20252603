package domain;

import java.util.ArrayList;
import java.util.List;

class Deck {
    private static final int WILD_CARD_COUNT = 2;

    private final List<Card> drawPile;
    private final List<Card> discardPile;

    Deck() {
        this.drawPile = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        initializeDrawPile();
    }

    int getDrawPileSize() {
        return drawPile.size();
    }

    int getDiscardPileSize() {
        return discardPile.size();
    }

    boolean containsTerritoryCard(TerritoryName territory) {
        for (Card card : drawPile) {
            if (card.matchesTerritory(territory)) {
                return true;
            }
        }
        return false;
    }

    private void initializeDrawPile() {
        for (TerritoryName territory : TerritoryName.values()) {
            drawPile.add(new Card(CardType.INFANTRY, territory));
        }
        for (int i = 0; i < WILD_CARD_COUNT; i++) {
            drawPile.add(new Card(CardType.WILD, null));
        }
    }
}
