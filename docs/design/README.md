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

### `CardType` (public)
**Purpose:** Represents the three troop types on Risk cards plus the wild card.
INFANTRY, CAVALRY, ARTILLERY, WILD

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
| `void assignTerritory(PlayerColor color)` | Forcibly reassigns ownership without checking prior ownership. Used only during territory capture. |
| `void addArmies(int count)` | Adds armies, throws IllegalArgumentException if count < 1 |
| `void removeArmies(int count)` | Removes armies, throws IllegalArgumentException if count < 1 or count would reduce armies below 0 |

### `WorldMap` (package-private)
**Purpose:** Holds all 42 territories and their neighbors. Adjacency is initialized from the standard Risk board on construction.

| Method | Description |
|--------|-------------|
| `boolean areNeighbors(TerritoryName first, TerritoryName second)` | Returns true if two territories border each other |
| `boolean isOwnedBy(TerritoryName territory, PlayerColor color)` | Returns true if territory is owned by player |
| `boolean isUnclaimed(TerritoryName territory)` | Returns true if territory has no owner |
| `int getArmies(TerritoryName territory)` | Returns army count for territory |
| `void claim(TerritoryName territory, PlayerColor color)` | Claims territory for player, throws IllegalStateException if already claimed |
| `void assignTerritory(TerritoryName territory, PlayerColor color)` | Forcibly reassigns ownership of a territory. Used only during capture. |
| `void addArmies(TerritoryName territory, int count)` | Adds armies to territory |
| `void removeArmies(TerritoryName territory, int count)` | Removes armies from territory, throws IllegalArgumentException if count < 1 or would reduce armies below 0 |
| `int countTerritoriesOwnedBy(PlayerColor color)` | Returns number of territories owned by player |
| `Set<TerritoryName> getTerritoriesOwnedBy(PlayerColor color)` | Returns a copy of territories owned by player |

### `Card` (public)
**Purpose:** Represents one Risk card. Each card has a territory and a troop type, or is a wild card with no territory.

| Method | Description |
|--------|-------------|
| `Card(CardType type, TerritoryName territory)` | Constructs a card. territory must be null if and only if type is WILD. Throws IllegalArgumentException if type is null, if a non-wild card has a null territory, or if a wild card has a non-null territory. |
| `TerritoryName getTerritory()` | Returns the territory on this card, or null if wild |
| `CardType getType()` | Returns the troop type on this card |
| `boolean isWild()` | Returns true if this is a wild card |
| `boolean matchesTerritory(TerritoryName territory)` | Returns true if this card's territory equals the given territory. Returns false for wild cards or if territory is null. |

### `Deck` (package-private)
**Purpose:** Holds and manages the draw and discard piles of Risk cards. One card per territory plus two wild cards.

| Method | Description |
|--------|-------------|
| `Card draw()` | Draws and returns the top card. If the draw pile is empty, reshuffles the discard pile into it first. Throws IllegalStateException if both piles are empty. |
| `int getDrawPileSize()` | Returns the number of cards remaining in the draw pile |
| `int getDiscardPileSize()` | Returns the number of cards in the discard pile |
| `void discard(List<Card> cards)` | Adds the given cards to the discard pile. Throws IllegalArgumentException if list is null, empty, or contains null. |

### `Player` (package-private)
**Purpose:** Represents one player in the game. Tracks identity, armies to place, and card hand.

| Method | Description |
|--------|-------------|
| `PlayerColor getColor()` | Returns player color |
| `String getName()` | Returns player name |
| `int getArmiesToPlace()` | Returns remaining armies to place |
| `boolean hasArmiesToPlace()` | Returns true if armiesToPlace > 0 |
| `void decreaseArmiesToPlace(int count)` | Decreases army count, throws IllegalArgumentException if count < 1 or count > armiesToPlace |
| `void increaseArmiesToPlace(int count)` | Increases army count, throws IllegalArgumentException if count < 1 |
| `int getCardCount()` | Returns the number of cards in the player's hand |
| `List<Card> getCards()` | Returns a copy of the player's current card hand |
| `void addCard(Card card)` | Adds a card to the player's hand, throws IllegalArgumentException if card is null |
| `boolean hasCards(List<Card> cards)` | Returns true if the player owns all cards in the list (accounting for duplicates). Returns true for an empty list. Throws IllegalArgumentException if list is null. |
| `void removeCards(List<Card> cards)` | Removes the given cards from the player's hand. No-op for an empty list. Throws IllegalArgumentException if player does not own all cards or list is null. |

### `GameConstants` (public)
**Purpose:** Exposes public constants for use by GUI and other non-domain code.

| Constant | Value |
|----------|-------|
| `MIN_PLAYERS` | 3 |
| `MAX_PLAYERS` | 6 |
| `TOTAL_TERRITORIES` | 42 |
| `ARMIES_THREE_PLAYERS` | 35 |
| `ARMIES_FOUR_PLAYERS` | 30 |
| `ARMIES_FIVE_PLAYERS` | 25 |
| `ARMIES_SIX_PLAYERS` | 20 |

### `RiskGame` (public)
**Purpose:** Coordinates all game logic. The only class GUI controllers interact with.

#### Constants
- `MIN_PLAYERS = 3`, `MAX_PLAYERS = 6`
- `ARMIES_THREE_PLAYERS = 35`, `ARMIES_FOUR_PLAYERS = 30`, `ARMIES_FIVE_PLAYERS = 25`, `ARMIES_SIX_PLAYERS = 20`
- `TOTAL_TERRITORIES = 42`
- `MIN_DRAFT_ARMIES = 3`
- `MIN_ATTACK_DICE = 1`, `MAX_ATTACK_DICE = 3`, `MAX_DEFEND_DICE = 2`, `DIE_SIDES = 6`

#### Methods

| Method | Description |
|--------|-------------|
| `RiskGame(Map<PlayerColor, String> playerInfo)` | Validates 3-6 players, assigns starting armies by player count, randomly selects starting player, sets phase to SCRAMBLE. Throws IllegalArgumentException if playerInfo is null or player count out of range. |
| `RiskGame(Map<PlayerColor, String> playerInfo, Random random)` | Package-private constructor for tests. Accepts injected Random for controlled randomness. |
| `void claimTerritory(TerritoryName territory)` | Current player claims one unclaimed territory during SCRAMBLE. Places 1 army automatically and decreases armiesToPlace by 1. Advances to next player. Transitions to SETUP when all 42 territories are claimed. Throws IllegalStateException if wrong phase or territory already claimed. |
| `void placeArmy(TerritoryName territory)` | Current player places 1 army on an owned territory during SETUP. Decreases armiesToPlace by 1. Advances to next player, skipping players with 0 armies remaining. Transitions to ATTACK when all players have 0 armies left. Throws IllegalStateException if wrong phase. Throws IllegalArgumentException if territory not owned by current player or no armies left. |
| `int getDraftArmies()` | Returns draft armies remaining this turn. At turn start equals max(3, floor(ownedTerritories / 3)) plus any continent bonuses. Decrements as draftArmy() is called. Returns 0 once all draft armies are placed. |
| `void draftArmy(TerritoryName territory)` | Places 1 draft army on an owned territory during ATTACK. Initializes draft on first call. Throws IllegalStateException if phase is not ATTACK or player has 5 or more cards and has not traded. Throws IllegalArgumentException if territory not owned by current player or no draft armies remaining. |
| `boolean isDraftComplete()` | Returns true when draft has been initialized and all draft armies placed. Returns false if draft not yet initialized. |
| `void attack(TerritoryName from, TerritoryName to, int numAttackers)` | Current player attacks an adjacent enemy territory. Draft must be complete. Rolls dice internally. Removes armies from both sides. If defender reaches 0 armies, captures territory via assignTerritory() and moves numAttackers armies into it. Marks player for one card award this turn if not already marked. Transfers defeated player's cards to attacker if that player is eliminated. Transitions to GAME_OVER if one player now owns all 42 territories. Throws IllegalStateException if phase is not ATTACK or draft not complete. Throws IllegalArgumentException if from not owned by current player, to owned by current player, not neighbors, numAttackers < 1, numAttackers > 3, or numAttackers >= from.armies. |
| `void endAttack()` | Ends attack step and transitions to FORTIFY. Draft must be complete. Throws IllegalStateException if phase is not ATTACK or draft not complete. |
| `void fortify(TerritoryName from, TerritoryName to, int armies)` | Moves armies from one owned territory to an adjacent owned territory. Only one fortify per turn. from retains at least 1 army. Throws IllegalStateException if phase is not FORTIFY or player already fortified this turn. Throws IllegalArgumentException if territories not neighbors, either not owned by current player, armies < 1, or armies >= from.armies. |
| `void endTurn()` | Ends current player's turn. Awards one card from deck if player captured at least one territory this turn. Advances to next active player, skipping eliminated players with 0 territories. Resets draft state and fortify state. Transitions phase to ATTACK. Throws IllegalStateException if phase is not FORTIFY. |
| `List<Card> getCards(PlayerColor color)` | Returns a copy of the given player's card hand. Throws IllegalArgumentException if color is not in the game. |
| `boolean canTradeCards(List<Card> cards)` | Returns true if the 3 cards form a valid trade set: three of the same type, one of each type, or any two plus a wild. Returns false if list is null, contains null, is not exactly 3 cards, or is not a valid set. |
| `void tradeCards(List<Card> cards)` | Trades a valid set of 3 cards for draft armies. Army count increases by trade sequence: 4, 6, 8, 10, 12, 15, then +5 each trade after. If any traded card matches a territory owned by current player, that territory gains 2 armies. Removes cards from player's hand and returns them to deck. Throws IllegalStateException if phase is not ATTACK. Throws IllegalArgumentException if cards are null, contain null, are not exactly 3, are not a valid set, or are not all owned by current player. |
| `PlayerColor getCurrentPlayerColor()` | Returns current player's color |
| `String getCurrentPlayerName()` | Returns current player's name |
| `GamePhase getPhase()` | Returns current game phase |
| `int getArmiesToPlace()` | Returns current player's remaining armies to place during SETUP |
| `boolean isSetupComplete()` | Returns true when all players have placed all armies |
| `boolean isOwnedBy(TerritoryName territory, PlayerColor color)` | Returns true if territory is owned by given player |
| `boolean isUnclaimed(TerritoryName territory)` | Returns true if territory has no owner |
| `int getArmies(TerritoryName territory)` | Returns army count for territory |
| `PlayerColor getWinner()` | Returns the PlayerColor of the player who owns all 42 territories, or null if no winner yet |

## GUI

The GUI calls the domain API and displays state. It does not contain game logic.

### `RiskApplication`
**Purpose:** JavaFX entry point. Launches the game setup window.

### `GameSetupController`
**Purpose:** Controls the setup screen. Collects player count and names, validates input, constructs a `RiskGame`, and switches to the game board scene.

### `GameBoardController`
**Purpose:** Controls the game board screen. Renders the SVG map via WebView, handles territory clicks, and updates the status bar. Calls `claimTerritory()` and `placeArmy()` during SCRAMBLE and SETUP. Will call `draftArmy()`, `attack()`, `endAttack()`, `fortify()`, and `endTurn()` during ATTACK and FORTIFY phases once those controls are wired up.

## Relationships
- `RiskGame` owns one `WorldMap`, one `Deck`, and a list of `Player` objects
- `RiskGame` uses `GamePhase` to enforce valid state transitions
- `WorldMap` owns 42 `Territory` objects and their neighbor relationships
- `Territory` uses `TerritoryName` and `PlayerColor`
- `Player` uses `PlayerColor` and holds a list of `Card` objects
- `Card` uses `TerritoryName` and `CardType`
- `Deck` holds a shuffled list of `Card` objects
- `GameConstants` is a standalone utility class with no dependencies
- GUI controllers call `RiskGame` methods only and read state through `RiskGame` getters
