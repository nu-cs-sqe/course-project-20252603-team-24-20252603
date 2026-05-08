package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TerritoryTests {

    @Test
    public void GetName_TerritoryConstructedWithAlaska_ReturnsAlaska() {
        Territory territory = new Territory(TerritoryName.ALASKA);
        assertEquals(TerritoryName.ALASKA, territory.getName());
    }

    @Test
    public void GetName_TerritoryConstructedWithIndonesia_ReturnsIndonesia() {
        Territory territory = new Territory(TerritoryName.INDONESIA);
        assertEquals(TerritoryName.INDONESIA, territory.getName());
    }

    @Test
    public void GetArmies_NewTerritory_ReturnsZero() {
        Territory territory = new Territory(TerritoryName.ALASKA);
        assertEquals(0, territory.getArmies());
    }
}
