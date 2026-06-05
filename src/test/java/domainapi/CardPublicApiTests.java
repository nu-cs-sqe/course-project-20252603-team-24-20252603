package domainapi;

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
}
