package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Player {
    private final PlayerColor color;
    private final String name;
    private int armiesToPlace;
    private final List<Card> cards = new ArrayList<>();

    Player(PlayerColor color, String name, int armiesToPlace) {
        this.color = color;
        this.name = name;
        this.armiesToPlace = armiesToPlace;
    }

    PlayerColor getColor() {
        return color;
    }

    String getName() {
        return name;
    }

    int getArmiesToPlace() {
        return armiesToPlace;
    }

    boolean hasArmiesToPlace() {
        return armiesToPlace > 0;
    }

    int getCardCount() {
        return cards.size();
    }

    List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    boolean hasCards(List<Card> requested) {
        if (requested == null) {
            throw new IllegalArgumentException("cards cannot be null");
        }
        List<Card> remaining = new ArrayList<>(cards);
        for (Card card : requested) {
            if (!remaining.remove(card)) {
                return false;
            }
        }
        return true;
    }

    void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("card cannot be null");
        }
        cards.add(card);
    }

    void decreaseArmiesToPlace(int count) {
        if (count < 1 || count > armiesToPlace) {
            throw new IllegalArgumentException(
                    "count must be at least 1 and not exceed available armies");
        }
        this.armiesToPlace -= count;
    }
}