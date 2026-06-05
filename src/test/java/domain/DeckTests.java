package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DeckTests {

    @Test
    public void ConstructDeckWithAllRiskCards_ReturnsFullDrawPileAndEmptyDiscardPile() {
        Deck deck = new Deck();
        assertEquals(44, deck.getDrawPileSize());
        assertEquals(0, deck.getDiscardPileSize());
    }
}
