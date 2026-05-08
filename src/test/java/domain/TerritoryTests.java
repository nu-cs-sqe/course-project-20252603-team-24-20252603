package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TerritoryTests {

    @Test
    public void GetName_TerritoryConstructedWithAlaska_ReturnsAlaska() {
        Territory territory = new Territory(TerritoryName.ALASKA);
        assertEquals(TerritoryName.ALASKA, territory.getName());
    }
}
