package gui;

import domain.PlayerColor;

final class PlayerColors {

    private PlayerColors() {
    }

    static String colorKey(PlayerColor color) {
        switch (color) {
            case RED:
                return "color.red";
            case BLUE:
                return "color.blue";
            case GREEN:
                return "color.green";
            case ORANGE:
                return "color.orange";
            case PINK:
                return "color.pink";
            case CYAN:
                return "color.cyan";
            default:
                throw new IllegalArgumentException(color.toString());
        }
    }
}
