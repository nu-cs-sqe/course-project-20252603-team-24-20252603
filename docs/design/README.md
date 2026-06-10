# System Design

## Package layout

- `domain` — pure game logic, no UI or framework dependencies. The GUI calls
  this package; the package never calls back into the GUI.
- `gui` — JavaFX controllers, the locale manager, and the JavaFX application
  entry point. Reads and writes the domain through `RiskGame` only.
- `i18n` (resource bundles, not a Java package) — message catalogs and the
  supported-locales manifest. See the project root `README.md`'s
  "Localization" section for the public contract.

## Enums

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

- **SCRAMBLE:** players claim unclaimed territories one at a time, placing 1
  army on each as they claim it.
- **SETUP:** players take turns placing their remaining starting armies on
  territories they already own.
- **ATTACK:** the active player drafts reinforcement armies (continent
  bonuses and card-trade bonuses included), may trade in card sets, may
  attack adjacent enemy territories any number of times, and may move armies
  into newly captured territories.
- **FORTIFY:** the active player may make at most one fortify move between
  two territories connected through an unbroken chain of territories they
  own.
- **GAME_OVER:** one player owns all 42 territories. No further actions are
  permitted.

### `CardType` (public)
**Purpose:** Represents the three troop types on Risk cards plus the wild
card.
INFANTRY, CAVALRY, ARTILLERY, WILD

## Domain classes

### `Territory` (package-private)
**Purpose:** Represents one territory on the board. Tracks army count and
ownership.

| Method | Description |
|--------|-------------|
| `TerritoryName getName()` | Returns the territory name. |
| `int getArmies()` | Returns current army count. |
| `boolean isOwnedBy(PlayerColor color)` | Returns true if owned by the given player. |
| `boolean isUnclaimed()` | Returns true if no player owns this territory. |
| `void claim(PlayerColor color)` | Assigns the initial owner. Throws `IllegalStateException` if the territory is already claimed. |
| `void transferOwner(PlayerColor color)` | Forcibly reassigns ownership without checking prior ownership. Used by `WorldMap.assignTerritory` during territory capture. |
| `void addArmies(int count)` | Adds armies. Throws `IllegalArgumentException` if `count < 1`. |
| `void removeArmies(int count)` | Removes armies. Throws `IllegalArgumentException` if `count < 1` or removing would drop armies below 0. |

### `WorldMap` (package-private)
**Purpose:** Holds all 42 territories and the standard Risk adjacency graph.
Adjacency is initialized from the canonical board on construction.

| Method | Description |
|--------|-------------|
| `boolean areNeighbors(TerritoryName first, TerritoryName second)` | Returns true if the two territories border each other on the standard Risk map. |
| `boolean areConnectedThrough(TerritoryName from, TerritoryName to, PlayerColor owner)` | Returns true if there is an unbroken chain of `owner`-owned territories from `from` to `to`. Used by `fortify` to permit moves across any owned path, not just direct neighbors. Returns false if either endpoint is not owned by `owner`. |
| `boolean isOwnedBy(TerritoryName territory, PlayerColor color)` | Returns true if `territory` is owned by `color`. |
| `boolean isUnclaimed(TerritoryName territory)` | Returns true if the territory has no owner. |
| `int getArmies(TerritoryName territory)` | Returns army count for the given territory. |
| `void claim(TerritoryName territory, PlayerColor color)` | Claims an unclaimed territory. Throws `IllegalStateException` if already claimed. |
| `void assignTerritory(TerritoryName territory, PlayerColor color)` | Forcibly reassigns ownership. Used only during capture. |
| `void addArmies(TerritoryName territory, int count)` | Adds armies to the territory. |
| `void removeArmies(TerritoryName territory, int count)` | Removes armies. Throws `IllegalArgumentException` if `count < 1` or would drop armies below 0. |
| `int countTerritoriesOwnedBy(PlayerColor color)` | Returns the number of territories owned by `color`. |
| `Set<TerritoryName> getTerritoriesOwnedBy(PlayerColor color)` | Returns a copy of the set of territories owned by `color`. |

### `Card` (public)
**Purpose:** Represents one Risk card. Each card has a territory and a troop
type, or is a wild card with no territory.

| Method | Description |
|--------|-------------|
| `Card(CardType type, TerritoryName territory)` | Constructs a card. `territory` must be null if and only if `type` is `WILD`. Throws `IllegalArgumentException` if `type` is null, if a non-wild card has a null territory, or if a wild card has a non-null territory. |
| `CardType getType()` | Returns the troop type on this card. |
| `TerritoryName getTerritory()` | Returns the territory on this card, or null for wild cards. |
| `boolean isWild()` | Returns true if this is a wild card. |
| `boolean matchesTerritory(TerritoryName territory)` | Returns true if this card's territory equals the given territory. Returns false for wild cards or if `territory` is null. |

### `Deck` (package-private)
**Purpose:** Manages the draw and discard piles of Risk cards. The initial
deck contains 42 territory cards (one per territory, 14 infantry, 14
cavalry, 14 artillery) plus 2 wild cards = 44 cards total.

| Method | Description |
|--------|-------------|
| `Deck()` | Constructs a deck with a fresh `Random`. |
| `Deck(Random random)` | Package-private constructor used by tests to inject deterministic shuffling. |
| `Card draw()` | Draws and returns the top card. If the draw pile is empty, the discard pile is reshuffled into the draw pile first. Throws `IllegalStateException` if both piles are empty. |
| `void discard(List<Card> cards)` | Adds the given cards to the discard pile. Throws `IllegalArgumentException` if `cards` is null, empty, or contains null. |
| `int getDrawPileSize()` | Returns the number of cards remaining in the draw pile. |
| `int getDiscardPileSize()` | Returns the number of cards in the discard pile. |
| `boolean containsTerritoryCard(TerritoryName territory)` | Returns true if a card matching the given territory is currently in the draw pile. |
| `int countWildCards()` | Returns the number of wild cards currently in the draw pile. |

### `Player` (package-private)
**Purpose:** Represents one player. Tracks identity, the count of starting
armies still to place, and the player's card hand.

| Method | Description |
|--------|-------------|
| `PlayerColor getColor()` | Returns the player's color. |
| `String getName()` | Returns the player's display name. |
| `int getArmiesToPlace()` | Returns the number of starting armies the player still has to place. |
| `boolean hasArmiesToPlace()` | Returns true if `armiesToPlace > 0`. |
| `void decreaseArmiesToPlace(int count)` | Decreases the army count. Throws `IllegalArgumentException` if `count < 1` or exceeds the remaining armies. |
| `int getCardCount()` | Returns the size of the player's card hand. |
| `List<Card> getCards()` | Returns an unmodifiable view of the player's current card hand. |
| `boolean hasCards(List<Card> requested)` | Returns true if the player owns all cards in the list, accounting for duplicates. Returns true for an empty list. Throws `IllegalArgumentException` if the list is null. |
| `void addCard(Card card)` | Adds a card to the player's hand. Throws `IllegalArgumentException` if `card` is null. |
| `void removeCards(List<Card> toRemove)` | Removes the given cards from the player's hand. No-op for an empty list. Throws `IllegalArgumentException` if the player does not own all listed cards or if the list is null. |

### `GameConstants` (public)
**Purpose:** Exposes the numeric constants the GUI needs to render labels,
spinners, and validation hints. Mirrors the corresponding private constants
in `RiskGame`.

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
**Purpose:** Coordinates all game logic. This is the only domain class the
GUI controllers interact with.

#### Private constants
- Player count, starting armies, and the board size live in `GameConstants`
  (so the GUI can share them) and are read as `GameConstants.MIN_PLAYERS`,
  `GameConstants.ARMIES_THREE_PLAYERS`, `GameConstants.TOTAL_TERRITORIES`, etc.
- Draft / dice: `MIN_DRAFT_ARMIES = 3`, `TERRITORIES_PER_DRAFT_ARMY = 3`,
  `MAX_ATTACK_DICE = 3`, `MAX_DEFEND_DICE = 2`, `MIN_ARMIES_TO_ATTACK = 2`,
  `DIE_SIDES = 6`
- Card trading: `CARDS_PER_TRADE_SET = 3`, `MANDATORY_TRADE_THRESHOLD = 5`,
  `CARD_TERRITORY_BONUS = 2`, `TRADE_BONUS_TABLE = {4, 6, 8, 10, 12, 15}`,
  `TRADE_BONUS_INCREMENT = 5`
- Continent bonuses: `NORTH_AMERICA_BONUS = 5`, `SOUTH_AMERICA_BONUS = 2`,
  `EUROPE_BONUS = 5`, `AFRICA_BONUS = 3`, `ASIA_BONUS = 7`,
  `AUSTRALIA_BONUS = 2`
- Continent membership lists `NORTH_AMERICA` through `AUSTRALIA`, grouped into
  `CONTINENTS` with the matching `CONTINENT_BONUSES`, used to award continent
  bonuses.

#### Construction and setup

| Method | Description |
|--------|-------------|
| `RiskGame(Map<PlayerColor, String> playerInfo)` | Public constructor. Validates that there are 3–6 players, assigns starting armies by player count, randomly selects the starting player, and sets the phase to `SCRAMBLE`. Throws `IllegalArgumentException` if `playerInfo` is null or its size is outside `[MIN_PLAYERS, MAX_PLAYERS]`. |
| `RiskGame(Map<PlayerColor, String> playerInfo, Random random)` | Package-private constructor used by tests. Accepts an injected `Random` for deterministic player ordering and dice rolls. |
| `void claimTerritory(TerritoryName territory)` | Active player claims one unclaimed territory during `SCRAMBLE`. Places 1 army on it and decrements the player's `armiesToPlace`. Advances to the next player. When the 42nd territory is claimed, transitions to `SETUP` and records the next player as the first `SETUP` placer. Throws `IllegalStateException` if the phase is not `SCRAMBLE` or the territory is already claimed. |
| `void placeArmy(TerritoryName territory)` | Active player places 1 army on a territory they own during `SETUP`. Decrements the player's `armiesToPlace`. Advances to the next player that still has armies to place. Transitions to `ATTACK` (returning to the first `SETUP` placer) once every player has placed all starting armies. Throws `IllegalStateException` if the phase is not `SETUP`. Throws `IllegalArgumentException` if the territory is not owned by the current player or the current player has no armies left. |

#### Turn flow: ATTACK phase

| Method | Description |
|--------|-------------|
| `int getDraftArmies()` | Returns the draft armies the current player has available this turn. Before the draft is initialized, returns `max(MIN_DRAFT_ARMIES, ownedTerritories / 3) + continentBonus` as a *preview*. Once `draftArmy` or `tradeCards` initializes the draft, returns the actual remaining count, which decreases as armies are placed. |
| `void draftArmy(TerritoryName territory)` | Places 1 draft army on a territory the current player owns. On first call of the turn, initializes the draft using the formula above. Throws `IllegalStateException` if the phase is not `ATTACK` or if the player holds 5 or more cards and has not yet traded (Risk rules require trading once a player reaches the 5-card limit). Throws `IllegalArgumentException` if the territory is not owned by the current player or there are no draft armies remaining. |
| `boolean isDraftComplete()` | Returns true only when the draft has been initialized this turn and `draftArmiesRemaining == 0`. Returns false before the first `draftArmy` or `tradeCards` call of the turn. |
| `boolean canTradeCards(List<Card> cards)` | Returns true if the given list is a valid trade set: exactly 3 cards, no nulls, and either all the same troop type, one of each troop type, or any combination that includes at least one wild card. Returns false for null lists, lists containing null, lists not of size 3, or any invalid combination. |
| `void tradeCards(List<Card> cards)` | Trades a valid set of 3 cards for bonus draft armies. Bonus by trade-set count: 4, 6, 8, 10, 12, 15, then `15 + 5 * (n - 6)` thereafter. If any traded card matches a territory the current player owns, that territory gains 2 armies. The cards are removed from the player's hand and discarded to the deck. If the draft was not yet initialized, this initializes it (using the same formula as `draftArmy`) and then adds the trade bonus. If the draft was already complete, the bonus armies reopen drafting until they are placed. Throws `IllegalStateException` if the phase is not `ATTACK`. Throws `IllegalArgumentException` if `cards` is null, contains null, is not a valid set, or contains cards the current player does not own. |
| `void attack(TerritoryName from, TerritoryName to)` | Active player attacks an adjacent enemy territory. The draft must be complete. The method auto-rolls dice in a loop: each iteration the attacker rolls `min(MAX_ATTACK_DICE, fromArmies - 1)` dice and the defender rolls `min(MAX_DEFEND_DICE, toArmies)` dice; dice are compared highest-to-highest, ties go to the defender, and the loser of each comparison loses 1 army. The loop continues until the defender reaches 0 armies (capture) or the attacker cannot attack any more (fewer than 2 armies on `from`). On capture, ownership is transferred via `WorldMap.assignTerritory`, the capture is recorded as `pendingCaptureFrom` / `pendingCaptureTo`, the current player is marked to receive a card at end of turn, and if the defeated player has zero territories left, all of their cards are transferred to the attacker. If the current player now owns all 42 territories the phase transitions to `GAME_OVER`. Throws `IllegalStateException` if the phase is not `ATTACK` or the draft is not complete. Throws `IllegalArgumentException` if `from` is not owned by the current player, `to` is owned by the current player, the territories are not neighbors, or `from` has fewer than 2 armies. |
| `void moveArmiesAfterCapture(TerritoryName from, TerritoryName to, int armies)` | After a successful capture, moves armies from the attacking territory into the captured territory. `armies` must be at least `min(3, fromArmies - 1)` and at most `fromArmies - 1` (the attacker must always leave 1 army on the source). Clears the pending capture. Throws `IllegalStateException` if the phase is not `ATTACK` or `(from, to)` does not match the pending capture. Throws `IllegalArgumentException` if the army count is out of range. |
| `boolean isCaptureMovementPending()` | Returns true if a capture just happened and the active player still needs to move armies into the captured territory. |
| `TerritoryName getPendingCaptureFrom()` | Returns the source of the pending capture. Throws `IllegalStateException` if no capture is pending. |
| `TerritoryName getPendingCaptureTo()` | Returns the destination of the pending capture. Throws `IllegalStateException` if no capture is pending. |
| `int getMinimumCaptureMove()` | Returns the minimum number of armies the active player must move into the captured territory: `min(3, fromArmies - 1)`. Throws `IllegalStateException` if no capture is pending. |
| `int getMaximumCaptureMove()` | Returns the maximum number of armies the active player may move into the captured territory: `fromArmies - 1` (must leave 1 army behind). Throws `IllegalStateException` if no capture is pending. |
| `void endAttack()` | Ends the attack step and transitions to `FORTIFY`. The draft must be complete. Clears any pending capture state. Throws `IllegalStateException` if the phase is not `ATTACK` or the draft is not complete. |

#### Turn flow: FORTIFY and end-of-turn

| Method | Description |
|--------|-------------|
| `void fortify(TerritoryName from, TerritoryName to, int armies)` | Moves armies between two territories the active player owns, provided they are connected through an unbroken chain of owned territories (`WorldMap.areConnectedThrough`). Only one fortify per turn; the source must retain at least 1 army. Throws `IllegalStateException` if the phase is not `FORTIFY` or the player already fortified this turn. Throws `IllegalArgumentException` if either territory is not owned by the current player, the territories are not connected through owned territory, `armies < 1`, or `armies >= fromArmies`. |
| `void endTurn()` | Ends the current player's turn. If the player captured at least one territory this turn, awards them one card from the deck. Advances to the next player who still owns at least one territory, resets draft and fortify state, and transitions phase to `ATTACK`. Throws `IllegalStateException` if the phase is not `FORTIFY`. |

#### Read-only state accessors

| Method | Description |
|--------|-------------|
| `GamePhase getPhase()` | Returns the current game phase. |
| `PlayerColor getCurrentPlayerColor()` | Returns the current player's color. |
| `String getCurrentPlayerName()` | Returns the current player's display name. |
| `String getPlayerName(PlayerColor color)` | Returns the display name of any player in the game. Throws `IllegalArgumentException` if `color` is not in the game. |
| `int getArmiesToPlace()` | Returns the current player's `armiesToPlace` (relevant during `SCRAMBLE` and `SETUP`). |
| `boolean isSetupComplete()` | Returns true when every player has placed all starting armies. |
| `boolean isOwnedBy(TerritoryName territory, PlayerColor color)` | Pass-through to `WorldMap.isOwnedBy`. |
| `boolean isUnclaimed(TerritoryName territory)` | Pass-through to `WorldMap.isUnclaimed`. |
| `int getArmies(TerritoryName territory)` | Pass-through to `WorldMap.getArmies`. |
| `int getTerritoryCount(PlayerColor color)` | Pass-through to `WorldMap.countTerritoriesOwnedBy`. |
| `List<Card> getCards(PlayerColor color)` | Returns a copy of the given player's card hand. Throws `IllegalArgumentException` if `color` is not in the game. |
| `Optional<PlayerColor> getWinner()` | Returns the player who owns all 42 territories, or an empty `Optional` if no winner yet. |

## GUI

The GUI calls the domain through `RiskGame` only and reads state through
`RiskGame`'s getters. It does not contain game logic.

### `RiskApplication`
**Purpose:** JavaFX entry point. Loads the FXML for the game-setup screen
using the bundle returned by `LocaleManager.getBundle()` and shows the
window.

### `LocaleManager`
**Purpose:** Process-wide holder for the user-selected display locale.
Reads the list of supported locales at class-init time from
`src/main/resources/i18n/locales.properties` (the locale manifest) so new
languages can be added without changing any Java code. Exposes
`SUPPORTED_LOCALES`, `getCurrentLocale()`, `setCurrentLocale(Locale)`,
`getBundle()` and `getBundle(Locale)`. See the project root `README.md`'s
"Localization" section for the public contract.

### `GameSetupController`
**Purpose:** Controls the setup screen. Collects player count and names,
exposes a `ComboBox<Locale>` populated from `LocaleManager.SUPPORTED_LOCALES`,
hot-reloads the setup scene when the locale changes, validates input,
constructs the `RiskGame`, and switches to the game-board scene.

### `GameBoardController`
**Purpose:** Controls the in-game screen. Renders the SVG world map via a
JavaFX `WebView`, handles territory clicks and drag-drafting, displays the
current player's card hand, and updates the status bar. Drives the full
domain API for every phase: `claimTerritory` and `placeArmy` for
`SCRAMBLE`/`SETUP`; `draftArmy`, `tradeCards`, `attack`,
`moveArmiesAfterCapture`, and `endAttack` for `ATTACK`; `fortify` and
`endTurn` for `FORTIFY`. All user-visible strings are looked up in the
locale bundle (`LocaleManager.getBundle()`); the controller does not embed
English text.

## Relationships

- `RiskGame` owns one `WorldMap`, one `Deck`, a list of `Player` objects,
  and a `Random` source used for player ordering, dice, and deck shuffling.
- `RiskGame` uses `GamePhase` to enforce valid state transitions and the
  continent membership lists to compute draft bonuses.
- `WorldMap` owns 42 `Territory` objects and the adjacency graph from the
  standard Risk board.
- `Territory` uses `TerritoryName` and `PlayerColor`.
- `Player` uses `PlayerColor` and holds an ordered list of `Card` objects.
- `Card` uses `TerritoryName` and `CardType`.
- `Deck` holds shuffled `Card` objects in two piles (draw + discard) and
  owns its own `Random`.
- `GameConstants` is a standalone utility class with no dependencies; it
  exposes the same numeric values as the corresponding private constants in
  `RiskGame` so the GUI can use them without depending on `RiskGame`'s
  internals.
- `LocaleManager` and the GUI controllers belong to the `gui` package and
  depend on `RiskGame`. The domain has no dependency on the GUI or the
  locale bundles.
