package domain;

import java.util.ArrayList;
import java.util.List;

class Deck {
    private static final int WILD_CARD_COUNT = 2;
    private static final int CARDS_PER_TYPE = 14;

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

    Card draw() {
        if (drawPile.isEmpty()) {
            if (discardPile.isEmpty()) {
                throw new IllegalStateException("no cards available to draw");
            }
            drawPile.addAll(discardPile);
            discardPile.clear();
        }
        return drawPile.remove(0);
    }

    void discard(List<Card> cards) {
        if (cards == null) {
            throw new IllegalArgumentException("cards cannot be null");
        }
        if (cards.isEmpty()) {
            throw new IllegalArgumentException("cards cannot be empty");
        }
        for (Card card : cards) {
            if (card == null) {
                throw new IllegalArgumentException("cards cannot contain null");
            }
        }
        discardPile.addAll(cards);
    }

    boolean containsTerritoryCard(TerritoryName territory) {
        for (Card card : drawPile) {
            if (card.matchesTerritory(territory)) {
                return true;
            }
        }
        return false;
    }

    int countWildCards() {
        int count = 0;
        for (Card card : drawPile) {
            if (card.isWild()) {
                count++;
            }
        }
        return count;
    }

    private void initializeDrawPile() {
        TerritoryName[] territories = TerritoryName.values();
        for (int i = 0; i < territories.length; i++) {
            drawPile.add(new Card(getCardType(i), territories[i]));
        }
        for (int i = 0; i < WILD_CARD_COUNT; i++) {
            drawPile.add(new Card(CardType.WILD, null));
        }
    }

    private CardType getCardType(int index) {
        if (index < CARDS_PER_TYPE) {
            return CardType.INFANTRY;
        }
        if (index < CARDS_PER_TYPE * 2) {
            return CardType.CAVALRY;
        }
        return CardType.ARTILLERY;
    }
}
