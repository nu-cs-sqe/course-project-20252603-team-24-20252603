# System Design

## enum

### `TerritoryName` (public)
**Purpose:** Represents all 42 territories on the Risk board as constants.
ALASKA, NORTHWEST_TERRITORY, GREENLAND, ALBERTA, ONTARIO, QUEBEC,
WESTERN_UNITED_STATES, EASTERN_UNITED_STATES, CENTRAL_AMERICA,
VENEZUELA, PERU, BRAZIL, ARGENTINA,
ICELAND, GREAT_BRITAIN, WESTERN_EUROPE, NORTHERN_EUROPE,
SOUTHERN_EUROPE, SCANDINAVIA, UKRAINE,
NORTH_AFRICA, EGYPT, EAST_AFRICA, CONGO, SOUTH_AFRICA, MADAGASCAR,
MIDDLE_EAST, AFGHANISTAN, URAL, SIBERIA, YAKUTSK, KAMCHATKA,
IRKUTSK, MONGOLIA, JAPAN, CHINA, INDIA, SIAM,
EASTERN_AUSTRALIA, WESTERN_AUSTRALIA, NEW_GUINEA, INDONESIA

### `PlayerColor` (public)
**Purpose:** Represents the six possible player colors.
RED, BLUE, GREEN, ORANGE, PINK, CYAN

### `GamePhase` (public)
**Purpose:** Tracks the current phase of the game.
SCRAMBLE, SETUP, ATTACK, FORTIFY, GAME_OVER

- SCRAMBLE: players claim unclaimed territories one at a time
- SETUP: players place remaining armies on owned territories
- ATTACK, FORTIFY, GAME_OVER: future phases

## Classes

### `Territory` (package-private)
**Purpose:** Represents one territory on the board. Tracks army count and ownership.

| Method | Description |
|--------|-------------|
| `TerritoryName getName()` | Returns the territory name |
| `int getArmies()` | Returns current army count |
| `boolean isOwnedBy(PlayerColor color)` | Returns true if owned by given player |
| `boolean isUnclaimed()` | Returns true if no player owns this territory |
| `void claim(PlayerColor color)` | Assigns owner, throws IllegalStateException if already claimed |
| `void addArmies(int count)` | Adds armies, throws IllegalArgumentException if count < 1 |

### `WorldMap` (game) (package-private)
**Purpose:** Holds all 42 territories and their neighbors.

| Method | Description |
|--------|-------------|
| `boolean areNeighbors(TerritoryName first, TerritoryName second)` | Returns true if two territories border each other |
| `boolean isOwnedBy(TerritoryName territory, PlayerColor color)` | Returns true if territory is owned by player |
| `boolean isUnclaimed(TerritoryName territory)` | Returns true if territory has no owner |
| `int getArmies(TerritoryName territory)` | Returns army count for territory |
| `void claim(TerritoryName territory, PlayerColor color)` | Claims territory for player |
| `void addArmies(TerritoryName territory, int count)` | Adds armies to territory |
| `int countTerritoriesOwnedBy(PlayerColor color)` | Returns number of territories owned by player |
| `Set<TerritoryName> getTerritoriesOwnedBy(PlayerColor color)` | Returns a copy of territories owned by player |

### `Player` (game) (package-private)
**Purpose:** Represents one player in the game. Tracks identity and remaining armies to place.

| Method | Description |
|--------|-------------|
| `PlayerColor getColor()` | Returns player color |
| `String getName()` | Returns player name |
| `int getArmiesToPlace()` | Returns remaining armies to place |
| `boolean hasArmiesToPlace()` | Returns true if armiesToPlace > 0 |
| `void decreaseArmiesToPlace(int count)` | Decreases army count, throws IllegalArgumentException if count > armiesToPlace |

### `RiskGame` (game) (public)
**Purpose:** Coordinates game logic.
The only class UI controllers interact with.

Constants:
- `MIN_PLAYERS = 3`, `MAX_PLAYERS = 6`
- `ARMIES_THREE_PLAYERS = 35`
- `ARMIES_FOUR_PLAYERS = 30`
- `ARMIES_FIVE_PLAYERS = 25`
- `ARMIES_SIX_PLAYERS = 20`
- `TOTAL_TERRITORIES = 42`

| Method | Description |
|--------|-------------|
| `RiskGame(Map<PlayerColor, String> playerInfo)` | Validates 3-6 players, assigns starting armies, rolls dice for turn order, sets phase to SCRAMBLE. |
| `RiskGame(Map<PlayerColor, String> playerInfo, Random random)` | Package-private constructor used by tests. Accepts injected Random for controlled dice rolls. Calls private helpers: validatePlayerCount, initializePlayers, assignStartingArmies, determinePlayerOrder. Sets phase to SCRAMBLE. |
| `void claimTerritory(TerritoryName territory)` | Current player claims one unclaimed territory during SCRAMBLE phase. Places 1 army automatically. Throws IllegalStateException if wrong phase or territory already claimed. Advances to next player. Transitions to SETUP when all 42 territories claimed. |
| `void placeArmy(TerritoryName territory)` | Current player places 1 army on an owned territory during SETUP phase. Throws IllegalStateException if wrong phase. Throws IllegalArgumentException if territory not owned by current player or no armies left. Advances to next player. Transitions to ATTACK when all players have 0 armies left. |
| `PlayerColor getCurrentPlayerColor()` | Returns current player's color |
| `String getCurrentPlayerName()` | Returns current player's name |
| `GamePhase getPhase()` | Returns current game phase |
| `int getArmiesToPlace()` | Returns current player's remaining armies to place |
| `boolean isSetupComplete()` | Returns true when all players have placed all armies |

## Relationships
- `RiskGame` owns one `WorldMap`, a list of `Player` objects, and a `Random`
- `RiskGame` uses `GamePhase` to enforce valid state transitions
- `WorldMap` owns 42 `Territory` objects
- `Territory` uses `TerritoryName` and `PlayerColor`
- `Player` uses `PlayerColor`
