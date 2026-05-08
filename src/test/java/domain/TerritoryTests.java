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

    @Test
    public void GetArmies_TerritoryWithOneArmy_ReturnsOne() {
        Territory territory = new Territory(TerritoryName.ALASKA);
        territory.addArmies(1);
        assertEquals(1, territory.getArmies());
    }

    @Test
    public void GetArmies_TerritoryWithFiveArmies_ReturnsFive() {
        Territory territory = new Territory(TerritoryName.ALASKA);
        territory.addArmies(5);
        assertEquals(5, territory.getArmies());
    }

    @Test
    public void IsUnclaimed_NewTerritory_ReturnsTrue() {
        Territory territory = new Territory(TerritoryName.ALASKA);
        assertTrue(territory.isUnclaimed());
    }

    @Test
    public void IsUnclaimed_ClaimedTerritory_ReturnsFalse() {
        Territory territory = new Territory(TerritoryName.ALASKA);
        territory.claim(PlayerColor.RED);
        assertFalse(territory.isUnclaimed());
    }

    @Test
    public void IsOwnedBy_UnclaimedTerritory_ReturnsFalse() {
        Territory territory = new Territory(TerritoryName.ALASKA);
        assertFalse(territory.isOwnedBy(PlayerColor.RED));
    }

    @Test
    public void IsOwnedBy_TerritoryClaimedByRed_WithRed_ReturnsTrue() {
        Territory territory = new Territory(TerritoryName.ALASKA);
        territory.claim(PlayerColor.RED);
        assertTrue(territory.isOwnedBy(PlayerColor.RED));
    }

    @Test
    public void IsOwnedBy_TerritoryClaimedByRed_WithBlue_ReturnsFalse() {
        Territory territory = new Territory(TerritoryName.ALASKA);
        territory.claim(PlayerColor.RED);
        assertFalse(territory.isOwnedBy(PlayerColor.BLUE));
    }

    @Test
    public void Claim_UnclaimedTerritory_TerritoryIsOwnedByRed() {
        Territory territory = new Territory(TerritoryName.ALASKA);
        territory.claim(PlayerColor.RED);
        assertFalse(territory.isUnclaimed());
        assertTrue(territory.isOwnedBy(PlayerColor.RED));
    }

    @Test
    public void Claim_AlreadyClaimedBySamePlayer_ThrowsIllegalStateException() {
        Territory territory = new Territory(TerritoryName.ALASKA);
        territory.claim(PlayerColor.RED);
        assertThrows(IllegalStateException.class, () -> territory.claim(PlayerColor.RED));
    }
}
