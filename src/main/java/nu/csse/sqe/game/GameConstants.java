package nu.csse.sqe.game;

/**
 * Numeric constants for Risk setup and board size. Full game logic lives in {@code RiskGame} later.
 */
public final class GameConstants {

    private GameConstants() {
    }

    public static final int MIN_PLAYERS = 3;
    public static final int MAX_PLAYERS = 6;

    public static final int TOTAL_TERRITORIES = 42;

    public static final int ARMIES_THREE_PLAYERS = 35;
    public static final int ARMIES_FOUR_PLAYERS = 30;
    public static final int ARMIES_FIVE_PLAYERS = 25;
    public static final int ARMIES_SIX_PLAYERS = 20;
}
