package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class PlayerTests {

    @Test
    public void GetColor_PlayerConstructedWithRed_ReturnsRed() {
        Player player = new Player(PlayerColor.RED, "Justin", 35);
        assertEquals(PlayerColor.RED, player.getColor());
    }

    @Test
    public void GetColor_PlayerConstructedWithCyan_ReturnsCyan() {
        Player player = new Player(PlayerColor.CYAN, "Justin", 35);
        assertEquals(PlayerColor.CYAN, player.getColor());
    }

    @Test
    public void GetName_PlayerConstructedWithJovy_ReturnsJovy() {
        Player player = new Player(PlayerColor.RED, "Jovy", 35);
        assertEquals("Jovy", player.getName());
    }

    @Test
    public void GetArmiesToPlace_PlayerWithZeroArmies_ReturnsZero() {
        Player player = new Player(PlayerColor.RED, "Justin", 0);
        assertEquals(0, player.getArmiesToPlace());
    }

    @Test
    public void GetArmiesToPlace_PlayerWithOneArmy_ReturnsOne() {
        Player player = new Player(PlayerColor.RED, "Justin", 1);
        assertEquals(1, player.getArmiesToPlace());
    }

    @Test
    public void GetArmiesToPlace_PlayerWithThirtyFiveArmies_ReturnsThirtyFive() {
        Player player = new Player(PlayerColor.RED, "Justin", 35);
        assertEquals(35, player.getArmiesToPlace());
    }

    @Test
    public void HasArmiesToPlace_PlayerWithZeroArmies_ReturnsFalse() {
        Player player = new Player(PlayerColor.RED, "Justin", 0);
        assertFalse(player.hasArmiesToPlace());
    }

    @Test
    public void HasArmiesToPlace_PlayerWithOneArmy_ReturnsTrue() {
        Player player = new Player(PlayerColor.RED, "Justin", 1);
        assertTrue(player.hasArmiesToPlace());
    }

    @Test
    public void HasArmiesToPlace_PlayerWithThirtyFiveArmies_ReturnsTrue() {
        Player player = new Player(PlayerColor.RED, "Justin", 35);
        assertTrue(player.hasArmiesToPlace());
    }

    @Test
    public void DecreaseArmiesToPlace_PlayerWithOneArmy_DecreaseByOne_ReturnsZero() {
        Player player = new Player(PlayerColor.RED, "Justin", 1);
        player.decreaseArmiesToPlace(1);
        assertEquals(0, player.getArmiesToPlace());
    }

    @Test
    public void DecreaseArmiesToPlace_PlayerWithThirtyFiveArmies_DecreaseByOne_ReturnsThirtyFour() {
        Player player = new Player(PlayerColor.RED, "Justin", 35);
        player.decreaseArmiesToPlace(1);
        assertEquals(34, player.getArmiesToPlace());
    }

    @Test
    public void DecreaseArmiesToPlace_PlayerWithThirtyFiveArmies_DecreaseByFive_ReturnsThirty() {
        Player player = new Player(PlayerColor.RED, "Justin", 35);
        player.decreaseArmiesToPlace(5);
        assertEquals(30, player.getArmiesToPlace());
    }

    @Test
    public void DecreaseArmiesToPlace_PlayerWithThirtyFiveArmies_ByThirtyFive_ReturnsZero() {
        Player player = new Player(PlayerColor.RED, "Justin", 35);
        player.decreaseArmiesToPlace(35);
        assertEquals(0, player.getArmiesToPlace());
    }

    @Test
    public void DecreaseArmiesToPlace_CountMoreThanAvailable_ThrowsIllegalArgumentException() {
        Player player = new Player(PlayerColor.RED, "Justin", 5);
        assertThrows(IllegalArgumentException.class, () -> player.decreaseArmiesToPlace(6));
    }

    @Test
    public void DecreaseArmiesToPlace_CountOfZero_ThrowsIllegalArgumentException() {
        Player player = new Player(PlayerColor.RED, "Justin", 5);
        assertThrows(IllegalArgumentException.class, () -> player.decreaseArmiesToPlace(0));
    }

    @Test
    public void DecreaseArmiesToPlace_NegativeCount_ThrowsIllegalArgumentException() {
        Player player = new Player(PlayerColor.RED, "Justin", 5);
        assertThrows(IllegalArgumentException.class, () -> player.decreaseArmiesToPlace(-1));
    }

    @Test
    public void GetCardCount_PlayerHasNoCards_ReturnsZero() {
        Player player = new Player(PlayerColor.RED, "Justin", 35);
        assertEquals(0, player.getCardCount());
    }

    @Test
    public void GetCardCount_PlayerHasOneCard_ReturnsOne() {
        Player player = new Player(PlayerColor.RED, "Justin", 35);
        player.addCard(new Card(CardType.INFANTRY, TerritoryName.ALASKA));
        assertEquals(1, player.getCardCount());
    }

    @Test
    public void GetCardCount_PlayerHasThreeCards_ReturnsThree() {
        Player player = new Player(PlayerColor.RED, "Justin", 35);
        player.addCard(new Card(CardType.INFANTRY, TerritoryName.ALASKA));
        player.addCard(new Card(CardType.CAVALRY, TerritoryName.ALBERTA));
        player.addCard(new Card(CardType.ARTILLERY, TerritoryName.BRAZIL));
        assertEquals(3, player.getCardCount());
    }

    @Test
    public void GetCards_PlayerHasNoCards_ReturnsEmptyList() {
        Player player = new Player(PlayerColor.RED, "Justin", 35);
        assertEquals(List.of(), player.getCards());
    }

    @Test
    public void GetCards_PlayerHasOneCard_ReturnsListWithThatCard() {
        Player player = new Player(PlayerColor.RED, "Justin", 35);
        Card alaska = new Card(CardType.INFANTRY, TerritoryName.ALASKA);
        player.addCard(alaska);
        assertEquals(List.of(alaska), player.getCards());
    }
}