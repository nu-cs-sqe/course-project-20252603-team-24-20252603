package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CardTests {

    @Test
    public void ConstructInfantryCardWithFirstTerritory_ReturnsInfantryAndAlaska() {
        Card card = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        assertEquals(CardType.INFANTRY, card.getType());
        assertEquals(TerritoryName.ALASKA, card.getTerritory());
    }
}
