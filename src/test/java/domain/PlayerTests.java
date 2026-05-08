package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTests {

    @Test
    public void GetColor_PlayerConstructedWithRed_ReturnsRed() {
        Player player = new Player(PlayerColor.RED, "Jovy", 35);
        assertEquals(PlayerColor.RED, player.getColor());
    }
}