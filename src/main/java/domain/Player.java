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

    String getName() {
        return name;
    }

    int getArmiesToPlace() {
        return armiesToPlace;
    }

    boolean hasArmiesToPlace() {
        return armiesToPlace > 0;
    }

    void decreaseArmiesToPlace(int count) {
        this.armiesToPlace -= count;
    }
}