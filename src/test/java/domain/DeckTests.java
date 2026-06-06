package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Random;

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
    public void Draw_FirstTenCardsFromShuffledDeck_NotAllInfantry() {
        Deck deck = new Deck(new Random(0L));
        boolean foundNonInfantry = false;
        for (int i = 0; i < 10; i++) {
            Card card = deck.draw();
            if (!card.isWild() && card.getType() != CardType.INFANTRY) {
                foundNonInfantry = true;
                break;
            }
        }
        assertTrue(foundNonInfantry);
    }

    @Test
    public void ConstructDeckWithFourteenInfantryTerritoryCards_ReturnsFourteenInfantryCards() {
        Deck deck = new Deck();
        int infantryCards = 0;
        for (int i = 0; i < 44; i++) {
            Card card = deck.draw();
            if (!card.isWild() && card.getType() == CardType.INFANTRY) {
                infantryCards++;
            }
        }
        assertEquals(14, infantryCards);
    }

    @Test
    public void ConstructDeckWithFourteenCavalryTerritoryCards_ReturnsFourteenCavalryCards() {
        Deck deck = new Deck();
        int cavalryCards = 0;
        for (int i = 0; i < 44; i++) {
            Card card = deck.draw();
            if (!card.isWild() && card.getType() == CardType.CAVALRY) {
                cavalryCards++;
            }
        }
        assertEquals(14, cavalryCards);
    }

    @Test
    public void ConstructDeckWithFourteenArtilleryTerritoryCards_ReturnsFourteenArtilleryCards() {
        Deck deck = new Deck();
        int artilleryCards = 0;
        for (int i = 0; i < 44; i++) {
            Card card = deck.draw();
            if (!card.isWild() && card.getType() == CardType.ARTILLERY) {
                artilleryCards++;
            }
        }
        assertEquals(14, artilleryCards);
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

    @Test
    public void DrawFromEmptyDrawPileWithOneDiscardedCard_ReturnsCardAndClearsDiscardPile() {
        Deck deck = new Deck();
        for (int i = 0; i < 44; i++) {
            deck.draw();
        }
        Card card = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        deck.discard(List.of(card));
        Card drawn = deck.draw();
        assertNotNull(drawn);
        assertEquals(0, deck.getDrawPileSize());
        assertEquals(0, deck.getDiscardPileSize());
    }

    @Test
    public void DrawFromEmptyDrawPileWithMoreThanOneDiscardedCard_ReturnsCardAndLeavesTwoCards() {
        Deck deck = new Deck();
        for (int i = 0; i < 44; i++) {
            deck.draw();
        }
        Card first = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card second = new Card(CardType.CAVALRY, TerritoryName.ALBERTA);
        Card third = new Card(CardType.ARTILLERY, TerritoryName.BRAZIL);
        deck.discard(List.of(first, second, third));
        Card drawn = deck.draw();
        assertNotNull(drawn);
        assertEquals(2, deck.getDrawPileSize());
        assertEquals(0, deck.getDiscardPileSize());
    }

    @Test
    public void DrawFromEmptyDrawPileWithEmptyDiscardPile_ThrowsIllegalStateException() {
        Deck deck = new Deck();
        for (int i = 0; i < 44; i++) {
            deck.draw();
        }
        assertThrows(IllegalStateException.class, deck::draw);
    }

    @Test
    public void DiscardOneCardIntoEmptyDiscardPile_ReturnsOneDiscardedCard() {
        Deck deck = new Deck();
        deck.discard(List.of(new Card(CardType.INFANTRY, TerritoryName.ALASKA)));
        assertEquals(1, deck.getDiscardPileSize());
    }

    @Test
    public void DiscardMoreThanOneCardIntoEmptyDiscardPile_ReturnsThreeDiscardedCards() {
        Deck deck = new Deck();
        Card first = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card second = new Card(CardType.CAVALRY, TerritoryName.ALBERTA);
        Card third = new Card(CardType.ARTILLERY, TerritoryName.BRAZIL);
        deck.discard(List.of(first, second, third));
        assertEquals(3, deck.getDiscardPileSize());
    }

    @Test
    public void DiscardOneCardIntoNonEmptyDiscardPile_ReturnsTwoDiscardedCards() {
        Deck deck = new Deck();
        Card first = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        Card second = new Card(CardType.CAVALRY, TerritoryName.ALBERTA);
        deck.discard(List.of(first));
        deck.discard(List.of(second));
        assertEquals(2, deck.getDiscardPileSize());
    }

    @Test
    public void DiscardZeroCards_ThrowsIllegalArgumentException() {
        Deck deck = new Deck();
        deck.discard(List.of(new Card(CardType.INFANTRY, TerritoryName.ALASKA)));
        assertThrows(IllegalArgumentException.class, () -> deck.discard(List.of()));
    }

    @Test
    public void DiscardNullCardsList_ThrowsIllegalArgumentException() {
        Deck deck = new Deck();
        assertThrows(IllegalArgumentException.class, () -> deck.discard(null));
    }

    @Test
    public void DiscardListContainingNullCard_ThrowsIllegalArgumentException() {
        Deck deck = new Deck();
        assertThrows(IllegalArgumentException.class, () -> deck.discard(Collections.singletonList(null)));
    }
}
