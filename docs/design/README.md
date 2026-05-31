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
- ATTACK: current player drafts reinforcements, optionally attacks adjacent enemy territories
- FORTIFY: current player may move armies between two adjacent owned territories once
- GAME_OVER: one player owns all 42 territories, no further actions permitted

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
| `void removeArmies(int count)` | Removes armies, throws IllegalArgumentException if count < 1 or count would reduce armies below 0 |

### `WorldMap` (package-private)
**Purpose:** Holds all 42 territories and their neighbors.

| Method | Description |
|--------|-------------|
| `boolean areNeighbors(TerritoryName first, TerritoryName second)` | Returns true if two territories border each other |
| `boolean isOwnedBy(TerritoryName territory, PlayerColor color)` | Returns true if territory is owned by player |
| `boolean isUnclaimed(TerritoryName territory)` | Returns true if territory has no owner |
| `int getArmies(TerritoryName territory)` | Returns army count for territory |
| `void claim(TerritoryName territory, PlayerColor color)` | Claims territory for player |
| `void addArmies(TerritoryName territory, int count)` | Adds armies to territory |
| `void removeArmies(TerritoryName territory, int count)` | Removes armies from territory, throws IllegalArgumentException if count < 1 or would reduce armies below 0 |
| `int countTerritoriesOwnedBy(PlayerColor color)` | Returns number of territories owned by player |
| `Set<TerritoryName> getTerritoriesOwnedBy(PlayerColor color)` | Returns a copy of territories owned by player |

### `Player` (package-private)
**Purpose:** Represents one player in the game. Tracks identity and remaining armies to place.

| Method | Description |
|--------|-------------|
| `PlayerColor getColor()` | Returns player color |
| `String getName()` | Returns player name |
| `int getArmiesToPlace()` | Returns remaining armies to place |
| `boolean hasArmiesToPlace()` | Returns true if armiesToPlace > 0 |
| `void decreaseArmiesToPlace(int count)` | Decreases army count, throws IllegalArgumentException if count < 1 or count > armiesToPlace |
| `void increaseArmiesToPlace(int count)` | Increases army count by count, throws IllegalArgumentException if count < 1 |

### `RiskGame` (public)
**Purpose:** Coordinates game logic. The only class UI controllers interact with.

#### Constants
- `MIN_PLAYERS = 3`, `MAX_PLAYERS = 6`
- `ARMIES_THREE_PLAYERS = 35`
- `ARMIES_FOUR_PLAYERS = 30`
- `ARMIES_FIVE_PLAYERS = 25`
- `ARMIES_SIX_PLAYERS = 20`
- `TOTAL_TERRITORIES = 42`
- `MIN_ATTACK_DICE = 1`
- `MAX_ATTACK_DICE = 3`
- `MAX_DEFEND_DICE = 2`
- `MIN_ARMIES_TO_ATTACK = 2`

#### Methods

| Method | Description |
|--------|-------------|
| `RiskGame(Map<PlayerColor, String> playerInfo)` | Validates 3-6 players, assigns starting armies, randomly selects starting player, sets phase to SCRAMBLE. Throws IllegalArgumentException if playerInfo is null or player count is out of range. |
| `RiskGame(Map<PlayerColor, String> playerInfo, Random random)` | Package-private constructor used by tests. Accepts injected Random for controlled randomness. |
| `void claimTerritory(TerritoryName territory)` | Current player claims one unclaimed territory during SCRAMBLE phase. Places 1 army automatically. Throws IllegalStateException if wrong phase or territory already claimed. Advances to next player. Transitions to SETUP when all 42 territories claimed. |
| `void placeArmy(TerritoryName territory)` | Current player places 1 army on an owned territory during SETUP phase. Throws IllegalStateException if wrong phase. Throws IllegalArgumentException if territory not owned by current player or no armies left. Advances to next player. Transitions to ATTACK when all players have 0 armies left. |
| `int getDraftArmies()` | Returns the number of draft reinforcement armies the current player has remaining to place this turn. Equals max(3, floor(ownedTerritories / 3)) at the start of each ATTACK turn. Decrements as draftArmy() is called. |
| `void draftArmy(TerritoryName territory)` | Current player places 1 draft army on an owned territory during ATTACK phase. Throws IllegalStateException if phase is not ATTACK. Throws IllegalArgumentException if territory not owned by current player or draftArmiesRemaining == 0. |
| `boolean isDraftComplete()` | Returns true when current player has placed all draft armies (draftArmiesRemaining == 0). |
| `void attack(TerritoryName from, TerritoryName to, int numAttackers)` | Current player attacks an adjacent enemy territory. Rolls dice internally. Removes armies from both sides based on outcome. If defender reaches 0 armies, attacker captures the territory. Throws IllegalStateException if phase is not ATTACK or draft is not complete. Throws IllegalArgumentException if from is not owned by current player, to is owned by current player, territories are not neighbors, numAttackers < 1, numAttackers > 3, or numAttackers >= from.armies. Checks for GAME_OVER after capture. |
| `void endAttack()` | Current player ends the attack step. Transitions phase from ATTACK to FORTIFY. Throws IllegalStateException if phase is not ATTACK or draft is not complete. |
| `void fortify(TerritoryName from, TerritoryName to, int armies)` | Current player moves armies from one owned territory to an adjacent owned territory. from must retain at least 1 army. Throws IllegalStateException if phase is not FORTIFY. Throws IllegalArgumentException if territories are not neighbors, either territory is not owned by current player, armies < 1, or armies >= from.armies. |
| `void endTurn()` | Ends current player's turn. Advances to next player, transitions phase to ATTACK, and sets draftArmiesRemaining for the new current player. Throws IllegalStateException if phase is not FORTIFY. |
| `PlayerColor getCurrentPlayerColor()` | Returns current player's color |
| `String getCurrentPlayerName()` | Returns current player's name |
| `GamePhase getPhase()` | Returns current game phase |
| `int getArmiesToPlace()` | Returns current player's remaining armies to place during SETUP phase |
| `boolean isSetupComplete()` | Returns true when all players have placed all armies |
| `boolean isOwnedBy(TerritoryName territory, PlayerColor color)` | Returns true if territory is owned by given player |
| `boolean isUnclaimed(TerritoryName territory)` | Returns true if territory has no owner |
| `int getArmies(TerritoryName territory)` | Returns army count for territory |
| `PlayerColor getWinner()` | Returns the PlayerColor of the player who owns all 42 territories, or null if no winner yet |

## Relationships
- `RiskGame` owns one `WorldMap` and a list of `Player` objects
- `RiskGame` uses `GamePhase` to enforce valid state transitions
- `WorldMap` owns 42 `Territory` objects and their neighbor relationships
- `Territory` uses `TerritoryName` and `PlayerColor`
- `Player` uses `PlayerColor`