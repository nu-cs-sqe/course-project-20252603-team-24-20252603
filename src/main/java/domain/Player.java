package domain;

class Player {
    private final PlayerColor color;
    private final String name;
    private int armiesToPlace;

    Player(PlayerColor color, String name, int armiesToPlace) {
        this.color = color;
        this.name = name;
        this.armiesToPlace = armiesToPlace;
    }

    PlayerColor getColor() {
        return color;
    }
}