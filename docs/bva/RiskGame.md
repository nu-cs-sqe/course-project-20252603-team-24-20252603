# BVA Analysis for RiskGame
## Method: `RiskGame(Map<PlayerColor, String> playerInfo)` (constructor)

- **TC1: Construct game with minimum valid players (3)** ( :white_check_mark: )
    - **State of the system**:
        - playerInfo: {RED="Jonathan", BLUE="Justin", GREEN="Prashant"}
    - **Expected output**: getPhase() == SCRAMBLE

- **TC2: Construct game with 4 players** ( :white_check_mark: )
    - **State of the system**:
        - playerInfo: {RED="Jonathan", BLUE="Justin", GREEN="Prashant", ORANGE="David"}
    - **Expected output**: getPhase() == SCRAMBLE

- **TC3: Construct game with 5 players** ( :white_check_mark: )
    - **State of the system**:
        - playerInfo: {RED="Jonathan", BLUE="Justin", GREEN="Prashant",
          ORANGE="David", PINK="Alice"}
    - **Expected output**: getPhase() == SCRAMBLE

- **TC4: Construct game with maximum valid players (6)** ( :white_check_mark: )
    - **State of the system**:
        - playerInfo: {RED="Jonathan", BLUE="Justin", GREEN="Prashant",
          ORANGE="David", PINK="Alice", CYAN="Bob"}
    - **Expected output**: getPhase() == SCRAMBLE

- **TC5: Construct game with too few players (2)** ( :white_check_mark: )
    - **State of the system**:
        - playerInfo: {RED="Jonathan", BLUE="Justin"}
    - **Expected output**: throw IllegalArgumentException

- **TC6: Construct game with empty map** ( :white_check_mark: )
    - **State of the system**:
        - playerInfo: {}
    - **Expected output**: throw IllegalArgumentException

## Method: `GamePhase getPhase()`

- **TC7: Get phase after all territories claimed** ( :white_check_mark: )
    - **State of the system**: all 42 territories claimed
    - **Expected output**: SETUP

- **TC8: Get phase after all armies placed** ( :white_check_mark: )
    - **State of the system**: all players have placed all armies
    - **Expected output**: ATTACK

## Method: `PlayerColor getCurrentPlayerColor()`

- **TC9: Get current player color at game start with random zero** ( :white_check_mark: )
    - **State of the system**:
        - game constructed with RED, BLUE, GREEN via LinkedHashMap
        - Random mocked to return 0
    - **Expected output**: RED

- **TC10: Get current player color after turn ends** ( :white_check_mark: )
    - **State of the system**:
        - current player claims a territory
        - turn advances
    - **Expected output**: current player is different from the previous current player

## Method: `String getCurrentPlayerName()`

- **TC11: Get current player name matches current player color** ( :white_check_mark: )
    - **State of the system**:
        - game constructed with RED="Jonathan", BLUE="Justin", GREEN="Prashant"
        - Random mocked to make RED go first
    - **Expected output**: "Jonathan"

## Method: `int getArmiesToPlace()`

- **TC12: Get armies to place at game start for 3 players** ( :white_check_mark: )
    - **State of the system**: game just constructed with 3 players
    - **Expected output**: 35

- **TC13: Get armies to place at game start for 4 players** ( :white_check_mark: )
    - **State of the system**: game just constructed with 4 players
    - **Expected output**: 30

- **TC14: Get armies to place at game start for 5 players** ( :white_check_mark: )
    - **State of the system**: game just constructed with 5 players
    - **Expected output**: 25

- **TC15: Get armies to place at game start for 6 players** ( :white_check_mark: )
    - **State of the system**: game just constructed with 6 players
    - **Expected output**: 20

- **TC16: Get armies after claiming 1 territory** ( :white_check_mark: )
    - **State of the system**:
        - game constructed with 3 players
        - current player claims 1 territory
    - **Expected output**: 34

## Method: `boolean isSetupComplete()`

- **TC17: Check setup not complete at game start** ( :white_check_mark: )
    - **State of the system**: game just constructed
    - **Expected output**: false

- **TC18: Check setup complete after all armies placed** ( :white_check_mark: )
    - **State of the system**: all players have placed all armies
    - **Expected output**: true

## Method: `void claimTerritory(TerritoryName territory)`

- **TC19: Claim unclaimed territory during SCRAMBLE phase** ( :white_check_mark: )
    - **State of the system**:
        - phase: SCRAMBLE
        - territory: ALASKA unclaimed
        - current player: RED
    - **Expected output**:
        - ALASKA owned by RED
        - RED armies decreased by 1
        - turn advances to next player

- **TC20: Claim territory in wrong phase** ( :white_check_mark: )
    - **State of the system**:
        - phase: SETUP
        - territory: ALASKA unclaimed
    - **Expected output**: throw IllegalStateException

- **TC21: Claim already claimed territory** ( :white_check_mark: )
    - **State of the system**:
        - phase: SCRAMBLE
        - territory: ALASKA already claimed by RED
    - **Expected output**: throw IllegalStateException

- **TC22: Claim last unclaimed territory transitions to SETUP** ( :white_check_mark: )
    - **State of the system**:
        - phase: SCRAMBLE
        - 41 territories already claimed
        - territory: last unclaimed territory
    - **Expected output**:
        - all 42 territories claimed
        - phase transitions to SETUP

- **TC23: Claim unclaimed territory adds 1 army** ( :white_check_mark: )
    - **State of the system**:
        - phase: SCRAMBLE
        - territory: ALASKA unclaimed
        - current player: RED
    - **Expected output**: ALASKA armies = 1

- **TC24: Claim unclaimed territory owned by current player** ( :white_check_mark: )
    - **State of the system**:
        - phase: SCRAMBLE
        - territory: ALASKA unclaimed
        - current player: RED
    - **Expected output**: ALASKA owned by RED

## Method: `void placeArmy(TerritoryName territory)`

- **TC25: Place army on owned territory during SETUP phase** ( :white_check_mark: )
    - **State of the system**:
        - phase: SETUP
        - ALASKA owned by current player RED
        - RED has armies to place
    - **Expected output**:
        - ALASKA armies increase by 1
        - RED armies decrease by 1
        - turn advances to next player

- **TC26: Place army in wrong phase** ( :white_check_mark: )
    - **State of the system**:
        - phase: SCRAMBLE
        - ALASKA owned by current player
    - **Expected output**: throw IllegalStateException

- **TC27: Place army on territory not owned by current player** ( :white_check_mark: )
    - **State of the system**:
        - phase: SETUP
        - ALASKA owned by BLUE
        - current player is RED
    - **Expected output**: throw IllegalArgumentException

- **TC28: Place army when player has no armies left** ( :white_check_mark: )
    - **State of the system**:
        - phase: SETUP
        - ALASKA owned by current player
        - current player armiesToPlace = 0
    - **Expected output**: throw IllegalArgumentException

- **TC29: All final armies placed transitions to ATTACK** ( :white_check_mark: )
    - **State of the system**:
        - phase: SETUP
        - RED, BLUE, and GREEN each have 1 army left
    - **Expected output**:
        - phase transitions to ATTACK
        - setup is complete

- **TC30: Place army on owned territory increases army count** ( :white_check_mark: )
    - **State of the system**:
        - phase: SETUP
        - ALASKA owned by RED
        - RED has armies to place
    - **Expected output**: ALASKA armies increase by 1

- **TC31: Place army skips player with no armies** ( :white_check_mark: )
    - **State of the system**:
        - phase: SETUP
        - RED has 1 army, BLUE has 0 armies, GREEN has 1 army
        - current player: RED
    - **Expected output**: after RED places, current player is GREEN

---

# One Turn of the Game

The following methods cover one complete turn: the current player drafts reinforcement armies, optionally attacks, optionally fortifies, then ends their turn. These methods are all active during the `ATTACK` or `FORTIFY` phase.

**Turn flow:**
1. On entering `ATTACK` phase (after setup or after `endTurn()`), `draftArmiesRemaining` is set to `max(3, floor(ownedTerritories / 3))`.
2. Player places all draft armies via `draftArmy()`.
3. Player may call `attack()` zero or more times.
4. Player calls `endAttack()` to transition to `FORTIFY`.
5. Player optionally calls `fortify()` once.
6. Player calls `endTurn()` to advance to the next player.

## Method: `int getDraftArmies()`

Returns the number of reinforcement armies the current player has remaining to place this turn. At the start of each `ATTACK` turn, this equals `max(3, floor(ownedTerritories / 3))` and decrements as `draftArmy()` is called.

- **TC32: Draft armies for player owning 1 territory** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, start of turn
        - current player owns 1 territory
    - **Expected output**: 3 (minimum — floor(1/3) = 0, clamped to 3)

- **TC33: Draft armies for player owning 11 territories** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, start of turn
        - current player owns 11 territories
    - **Expected output**: 3 (floor(11/3) = 3, equal to minimum — last count that stays at 3)

- **TC34: Draft armies for player owning 12 territories** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, start of turn
        - current player owns 12 territories
    - **Expected output**: 4 (floor(12/3) = 4 — first territory count that exceeds the minimum of 3)

- **TC35: Draft armies for player owning 42 territories** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, start of turn
        - current player owns all 42 territories
    - **Expected output**: 14 (floor(42/3) = 14 — maximum possible)

## Method: `void draftArmy(TerritoryName territory)`

Places 1 draft army on a territory owned by the current player during the `ATTACK` phase.

- **TC36: Place draft army on owned territory** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK
        - ALASKA owned by current player RED
        - draftArmiesRemaining = 3
    - **Expected output**:
        - ALASKA armies increase by 1
        - draftArmiesRemaining decreases to 2

- **TC37: Place last draft army** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK
        - ALASKA owned by current player RED
        - draftArmiesRemaining = 1
    - **Expected output**:
        - ALASKA armies increase by 1
        - isDraftComplete() returns true

- **TC38: Place draft army in wrong phase** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY
    - **Expected output**: throw IllegalStateException

- **TC39: Place draft army on territory not owned by current player** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK
        - ALASKA owned by BLUE
        - current player: RED
        - draftArmiesRemaining = 3
    - **Expected output**: throw IllegalArgumentException

- **TC40: Place draft army when none remain** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK
        - ALASKA owned by current player RED
        - draftArmiesRemaining = 0
    - **Expected output**: throw IllegalArgumentException

## Method: `boolean isDraftComplete()`

Returns `true` when the current player has placed all their draft armies (`draftArmiesRemaining == 0`).

- **TC41: Draft not yet complete** ( :white_check_mark: )
    - **State of the system**: draftArmiesRemaining > 0
    - **Expected output**: false

- **TC42: Draft complete** ( :white_check_mark: )
    - **State of the system**: draftArmiesRemaining = 0
    - **Expected output**: true

## Method: `void attack(TerritoryName from, TerritoryName to, int numAttackers)`

Current player attacks an adjacent enemy territory. Dice are rolled internally. Armies are removed from both sides based on dice outcome. If all defending armies are eliminated, the attacker captures the territory.

- **TC43: Attack with 1 attacker (minimum valid)** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, isDraftComplete: true
        - ALASKA owned by RED (current player), armies = 2
        - ALBERTA owned by BLUE, armies = 1
        - ALASKA and ALBERTA are neighbors
        - numAttackers: 1
    - **Expected output**: attack executes — dice rolled, armies adjusted on both sides

- **TC44: Attack with 3 attackers (maximum valid)** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, isDraftComplete: true
        - ALASKA owned by RED (current player), armies = 4
        - ALBERTA owned by BLUE, armies = 2
        - ALASKA and ALBERTA are neighbors
        - numAttackers: 3
    - **Expected output**: attack executes — dice rolled, armies adjusted on both sides

- **TC45: Attack in wrong phase** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY
    - **Expected output**: throw IllegalStateException

- **TC46: Attack before completing draft** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, isDraftComplete: false (draftArmiesRemaining > 0)
        - ALASKA owned by RED, armies = 3
        - ALBERTA owned by BLUE
        - numAttackers: 1
    - **Expected output**: throw IllegalStateException

- **TC47: Attack from territory not owned by current player** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, isDraftComplete: true
        - ALASKA owned by BLUE
        - current player: RED
        - numAttackers: 1
    - **Expected output**: throw IllegalArgumentException

- **TC48: Attack territory owned by current player** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, isDraftComplete: true
        - ALASKA owned by RED (current player), armies = 3
        - ALBERTA owned by RED (current player)
        - ALASKA and ALBERTA are neighbors
        - numAttackers: 1
    - **Expected output**: throw IllegalArgumentException

- **TC49: Attack non-adjacent territory** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, isDraftComplete: true
        - ALASKA owned by RED (current player), armies = 3
        - BRAZIL owned by BLUE
        - ALASKA and BRAZIL are not neighbors
        - numAttackers: 1
    - **Expected output**: throw IllegalArgumentException

- **TC50: Attack with 0 attackers** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, isDraftComplete: true
        - ALASKA owned by RED (current player), armies = 2
        - ALBERTA owned by BLUE
        - ALASKA and ALBERTA are neighbors
        - numAttackers: 0
    - **Expected output**: throw IllegalArgumentException

- **TC51: Attack with 4 attackers (exceeds maximum of 3)** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, isDraftComplete: true
        - ALASKA owned by RED (current player), armies = 5
        - ALBERTA owned by BLUE
        - ALASKA and ALBERTA are neighbors
        - numAttackers: 4
    - **Expected output**: throw IllegalArgumentException

- **TC52: Attack from territory with only 1 army (cannot attack)** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, isDraftComplete: true
        - ALASKA owned by RED (current player), armies = 1
        - ALBERTA owned by BLUE
        - ALASKA and ALBERTA are neighbors
        - numAttackers: 1
    - **Expected output**: throw IllegalArgumentException (numAttackers must be strictly less than from.armies)

- **TC53: Attack where numAttackers equals from.armies** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, isDraftComplete: true
        - ALASKA owned by RED (current player), armies = 2
        - ALBERTA owned by BLUE
        - ALASKA and ALBERTA are neighbors
        - numAttackers: 2
    - **Expected output**: throw IllegalArgumentException (must leave at least 1 army behind)

- **TC54: Attacker wins and captures territory** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, isDraftComplete: true
        - ALASKA owned by RED (current player), armies = 3
        - ALBERTA owned by BLUE, armies = 1
        - ALASKA and ALBERTA are neighbors
        - numAttackers: 2
        - Random mocked so attacker wins all dice
    - **Expected output**:
        - ALBERTA owned by RED
        - ALASKA armies reduced by attacker losses (>= 1 army remains on ALASKA)

- **TC55: Defender wins and repels attack** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, isDraftComplete: true
        - ALASKA owned by RED (current player), armies = 3
        - ALBERTA owned by BLUE, armies = 2
        - ALASKA and ALBERTA are neighbors
        - numAttackers: 2
        - Random mocked so defender wins all dice
    - **Expected output**:
        - ALBERTA still owned by BLUE
        - ALASKA armies reduced by attacker losses
        - ALBERTA armies reduced by defender losses

## Method: `void endAttack()`

Transitions the game from `ATTACK` phase to `FORTIFY` phase. Must be called even if the player did not attack.

- **TC56: End attack during ATTACK phase** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK
        - isDraftComplete: true
    - **Expected output**: phase == FORTIFY

- **TC57: End attack in wrong phase** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY
    - **Expected output**: throw IllegalStateException

## Method: `void fortify(TerritoryName from, TerritoryName to, int armies)`

Current player moves armies from one owned territory to an adjacent owned territory. Exactly one fortify move is allowed per turn.

- **TC58: Fortify with 1 army (minimum valid)** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY
        - ALASKA owned by RED (current player), armies = 3
        - ALBERTA owned by RED (current player)
        - ALASKA and ALBERTA are neighbors
        - armies: 1
    - **Expected output**:
        - ALASKA armies decrease by 1
        - ALBERTA armies increase by 1

- **TC59: Fortify with from.armies - 1 (maximum valid — leaves 1 army behind)** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY
        - ALASKA owned by RED (current player), armies = 5
        - ALBERTA owned by RED (current player)
        - ALASKA and ALBERTA are neighbors
        - armies: 4
    - **Expected output**:
        - ALASKA armies = 1
        - ALBERTA armies increase by 4

- **TC60: Fortify in wrong phase** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK
    - **Expected output**: throw IllegalStateException

- **TC61: Fortify from territory not owned by current player** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY
        - ALASKA owned by BLUE
        - current player: RED
        - armies: 1
    - **Expected output**: throw IllegalArgumentException

- **TC62: Fortify to territory not owned by current player** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY
        - ALASKA owned by RED (current player), armies = 3
        - ALBERTA owned by BLUE
        - ALASKA and ALBERTA are neighbors
        - armies: 1
    - **Expected output**: throw IllegalArgumentException

- **TC63: Fortify between non-adjacent territories** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY
        - ALASKA owned by RED (current player), armies = 3
        - BRAZIL owned by RED (current player)
        - ALASKA and BRAZIL are not neighbors
        - armies: 1
    - **Expected output**: throw IllegalArgumentException

- **TC64: Fortify with 0 armies** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY
        - ALASKA owned by RED (current player), armies = 3
        - ALBERTA owned by RED (current player)
        - ALASKA and ALBERTA are neighbors
        - armies: 0
    - **Expected output**: throw IllegalArgumentException

- **TC65: Fortify with all armies (none left behind)** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY
        - ALASKA owned by RED (current player), armies = 3
        - ALBERTA owned by RED (current player)
        - ALASKA and ALBERTA are neighbors
        - armies: 3 (= from.armies)
    - **Expected output**: throw IllegalArgumentException (must leave at least 1 army)

## Method: `void endTurn()`

Ends the current player's turn. Advances to the next player and returns to `ATTACK` phase with the new player's draft armies calculated.

- **TC66: End turn during FORTIFY phase** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY
        - current player: RED (in a 3-player game with RED, BLUE, GREEN)
    - **Expected output**:
        - phase == ATTACK
        - current player is different from RED

- **TC67: End turn in wrong phase** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK
    - **Expected output**: throw IllegalStateException

- **TC68: Draft armies reset for new player after endTurn** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY
        - RED calls endTurn()
        - next player (BLUE) owns 12 territories
    - **Expected output**:
        - getDraftArmies() == 4 (floor(12/3) = 4, computed for BLUE)

## Method: `PlayerColor getWinner()`

Returns the `PlayerColor` of the player who owns all 42 territories, or `null` if no winner yet.

- **TC69: No winner — territories distributed among multiple players** ( :white_check_mark: )
    - **State of the system**: territories distributed among at least 2 players
    - **Expected output**: null

- **TC70: One player owns all 42 territories** ( :white_check_mark: )
    - **State of the system**:
        - RED owns all 42 territories
    - **Expected output**: RED

---

# Multiple Turns

The following test cases cover behaviors that emerge only across more than one complete turn:
turn-order wrap-around, fresh draft state at the start of each new turn, the `GAME_OVER`
transition triggered when the final territory is captured, and the enforcement that no
further game actions are permitted once `GAME_OVER` is set.

## Method: `void endTurn()` — additional case

- **TC71: Turn order wraps from last player back to first player** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY
        - 3-player game: RED, BLUE, GREEN (in that order)
        - current player: GREEN (last in rotation)
    - **Expected output**:
        - phase == ATTACK
        - current player == RED

## Method: `boolean isDraftComplete()` — additional case

- **TC72: Draft not complete at start of a brand-new turn** ( :x: )
    - **State of the system**:
        - GREEN just called `endTurn()`, RED is now the current player
        - RED has not yet called `draftArmy()` (draft not initialized for this turn)
    - **Expected output**: false
    - **Note**: This is distinct from TC41. TC41 tests `draftArmiesRemaining > 0`;
      this tests the case where the draft has not been initialized at all for the new turn
      (`isDraftInitialized == false`, `draftArmiesRemaining == 0`).

## Method: `void attack(TerritoryName from, TerritoryName to, int numAttackers)` — additional case

- **TC73: Capturing the final enemy territory transitions phase to GAME_OVER** ( :x: )
    - **State of the system**:
        - phase: ATTACK, isDraftComplete: true
        - RED owns 41 territories; BLUE owns only ALBERTA (armies = 1)
        - ALASKA owned by RED (current player), armies = 2
        - ALASKA and ALBERTA are neighbors
        - numAttackers: 1
        - Random mocked so attacker wins all dice
    - **Expected output**:
        - ALBERTA owned by RED
        - phase == GAME_OVER
        - getWinner() == RED

## Actions blocked in GAME_OVER phase

- **TC74: `draftArmy()` called in GAME_OVER phase throws IllegalStateException** ( :x: )
    - **State of the system**:
        - phase: GAME_OVER
    - **Expected output**: throw IllegalStateException

- **TC75: `attack()` called in GAME_OVER phase throws IllegalStateException** ( :x: )
    - **State of the system**:
        - phase: GAME_OVER
    - **Expected output**: throw IllegalStateException

- **TC76: `endAttack()` called in GAME_OVER phase throws IllegalStateException** ( :x: )
    - **State of the system**:
        - phase: GAME_OVER
    - **Expected output**: throw IllegalStateException

- **TC77: `fortify()` called in GAME_OVER phase throws IllegalStateException** ( :x: )
    - **State of the system**:
        - phase: GAME_OVER
    - **Expected output**: throw IllegalStateException

- **TC78: `endTurn()` called in GAME_OVER phase throws IllegalStateException** ( :x: )
    - **State of the system**:
        - phase: GAME_OVER
    - **Expected output**: throw IllegalStateException