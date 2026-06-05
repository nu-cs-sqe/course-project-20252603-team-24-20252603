package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class DeckTests {

    @Test
    public void ConstructDeckWithAllRiskCards_ReturnsFullDrawPileAndEmptyDiscardPile() {
        Deck deck = new Deck();
        assertEquals(44, deck.getDrawPileSize());
        assertEquals(0, deck.getDiscardPileSize());
    }

    @Test
    public void ConstructDeckWithOneCardForEveryTerritory_ReturnsFortyTwoTerritoryCards() {
        Deck deck = new Deck();
        for (TerritoryName territory : TerritoryName.values()) {
            assertTrue(deck.containsTerritoryCard(territory));
        }
    }

    @Test
    public void ConstructDeckWithTwoWildCards_ReturnsTwoWildCards() {
        Deck deck = new Deck();
        assertEquals(2, deck.countWildCards());
    }

    @Test
    public void GetDrawPileSize_FullDeck_ReturnsFortyFour() {
        Deck deck = new Deck();
        assertEquals(44, deck.getDrawPileSize());
    }

    @Test
    public void GetDrawPileSize_AfterDrawingOneCard_ReturnsFortyThree() {
        Deck deck = new Deck();
        deck.draw();
        assertEquals(43, deck.getDrawPileSize());
    }

    @Test
    public void GetDrawPileSize_EmptyDrawPile_ReturnsZero() {
        Deck deck = new Deck();
        for (int i = 0; i < 44; i++) {
            deck.draw();
        }
        assertEquals(0, deck.getDrawPileSize());
    }

    @Test
    public void GetDiscardPileSize_EmptyDiscardPile_ReturnsZero() {
        Deck deck = new Deck();
        assertEquals(0, deck.getDiscardPileSize());
    }

    @Test
    public void GetDiscardPileSize_WithOneDiscardedCard_ReturnsOne() {
        Deck deck = new Deck();
        Card card = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        deck.discard(List.of(card));
        assertEquals(1, deck.getDiscardPileSize());
    }

    @Test
    public void GetDiscardPileSize_WithMoreThanOneDiscardedCard_ReturnsThree() {
        Deck deck = new Deck();
        Card first = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card second = new Card(CardType.CAVALRY, TerritoryName.ALBERTA);
        Card third = new Card(CardType.ARTILLERY, TerritoryName.BRAZIL);
        deck.discard(List.of(first, second, third));
        assertEquals(3, deck.getDiscardPileSize());
    }

    @Test
    public void DrawOneCardFromFullDeck_ReturnsCardAndLeavesFortyThreeCards() {
        Deck deck = new Deck();
        Card card = deck.draw();
        assertNotNull(card);
        assertEquals(43, deck.getDrawPileSize());
    }

    @Test
    public void DrawLastCardFromDrawPile_ReturnsCardAndLeavesEmptyDrawPile() {
        Deck deck = new Deck();
        Card card = null;
        for (int i = 0; i < 44; i++) {
            card = deck.draw();
        }
        assertNotNull(card);
        assertEquals(0, deck.getDrawPileSize());
    }
}
