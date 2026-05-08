package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTests {

    @Test
    public void GetColor_PlayerConstructedWithRed_ReturnsRed() {
        Player player = new Player(PlayerColor.RED, "Jovy", 35);
        assertEquals(PlayerColor.RED, player.getColor());
    }

    @Test
    public void GetColor_PlayerConstructedWithCyan_ReturnsCyan() {
        Player player = new Player(PlayerColor.CYAN, "Jovy", 35);
        assertEquals(PlayerColor.CYAN, player.getColor());
    }

    @Test
    public void GetName_PlayerConstructedWithJovy_ReturnsJovy() {
        Player player = new Player(PlayerColor.RED, "Jovy", 35);
        assertEquals("Jovy", player.getName());
    }

    @Test
    public void GetArmiesToPlace_PlayerWithZeroArmies_ReturnsZero() {
        Player player = new Player(PlayerColor.RED, "Jovy", 0);
        assertEquals(0, player.getArmiesToPlace());
    }

    @Test
    public void GetArmiesToPlace_PlayerWithOneArmy_ReturnsOne() {
        Player player = new Player(PlayerColor.RED, "Jovy", 1);
        assertEquals(1, player.getArmiesToPlace());
    }
}