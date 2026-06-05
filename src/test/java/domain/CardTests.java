package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class CardTests {

    @Test
    public void ConstructInfantryCardWithFirstTerritory_ReturnsInfantryAndAlaska() {
        Card card = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        assertEquals(CardType.INFANTRY, card.getType());
        assertEquals(TerritoryName.ALASKA, card.getTerritory());
    }

    @Test
    public void ConstructCavalryCardWithTerritory_ReturnsCavalryAndBrazil() {
        Card card = new Card(CardType.CAVALRY, TerritoryName.BRAZIL);
        assertEquals(CardType.CAVALRY, card.getType());
        assertEquals(TerritoryName.BRAZIL, card.getTerritory());
    }

    @Test
    public void ConstructArtilleryCardWithLastTerritory_ReturnsArtilleryAndIndonesia() {
        Card card = new Card(CardType.ARTILLERY, TerritoryName.INDONESIA);
        assertEquals(CardType.ARTILLERY, card.getType());
        assertEquals(TerritoryName.INDONESIA, card.getTerritory());
    }

    @Test
    public void ConstructWildCardWithNoTerritory_ReturnsWildAndNullTerritory() {
        Card card = new Card(CardType.WILD, null);
        assertEquals(CardType.WILD, card.getType());
        assertEquals(null, card.getTerritory());
    }

    @Test
    public void ConstructCardWithNullType_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Card(null, TerritoryName.ALASKA));
    }

    @Test
    public void ConstructNonWildCardWithNoTerritory_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Card(CardType.INFANTRY, null));
    }

    @Test
    public void ConstructWildCardWithTerritory_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Card(CardType.WILD, TerritoryName.ALASKA));
    }

    @Test
    public void GetType_InfantryCard_ReturnsInfantry() {
        Card card = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        assertEquals(CardType.INFANTRY, card.getType());
    }

    @Test
    public void GetType_WildCard_ReturnsWild() {
        Card card = new Card(CardType.WILD, null);
        assertEquals(CardType.WILD, card.getType());
    }
}
