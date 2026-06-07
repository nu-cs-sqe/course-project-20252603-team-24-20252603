package domainapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import domain.Card;
import domain.CardType;
import domain.TerritoryName;
import org.junit.jupiter.api.Test;

public class CardPublicApiTests {

    @Test
    public void ConstructCardFromOutsideDomainPackage_ReturnsCard() {
        Card card = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        assertNotNull(card);
    }

    @Test
    public void GetCardTypeFromOutsideDomainPackage_ReturnsInfantry() {
        Card card = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        assertEquals(CardType.INFANTRY, card.getType());
    }

    @Test
    public void GetCardTerritoryFromOutsideDomainPackage_ReturnsAlaska() {
        Card card = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        assertEquals(TerritoryName.ALASKA, card.getTerritory());
    }
}
