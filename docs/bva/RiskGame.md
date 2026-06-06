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
1. On entering `ATTACK` phase (after setup or after `endTurn()`), `getDraftArmies()` reports
   `max(3, floor(ownedTerritories / 3))` for the current player.
2. Player places all draft armies via `draftArmy()`.
3. Player may call `attack()` zero or more times only after all draft armies are placed.
4. Player calls `endAttack()` to transition to `FORTIFY`.
5. Player optionally calls `fortify()` once.
6. Player calls `endTurn()` to advance to the next player.

## Method: `int getDraftArmies()`

Returns the number of reinforcement armies the current player has remaining to place this turn.
At the start of each `ATTACK` turn, this equals `max(3, floor(ownedTerritories / 3))`.
Calling `getDraftArmies()` is informational only; it does not place armies and does not make
the draft complete. The returned value decrements as `draftArmy()` is called.

- **TC32: Draft armies for player owning 1 territory** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, start of turn
        - current player owns 1 territory
    - **Expected output**:
        - returns 3 (minimum — floor(1/3) = 0, clamped to 3)
        - draft is not complete until all draft armies are placed with `draftArmy()`

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

- **TC72: Draft not complete at start of a brand-new turn** ( :white_check_mark: )
    - **State of the system**:
        - GREEN just called `endTurn()`, RED is now the current player
        - RED has not yet called `draftArmy()` (draft not initialized for this turn)
    - **Expected output**: false
    - **Note**: This is distinct from TC41. TC41 tests `draftArmiesRemaining > 0`;
      this tests the case where the draft has not been initialized at all for the new turn
      (`isDraftInitialized == false`, `draftArmiesRemaining == 0`).

## Method: `void attack(TerritoryName from, TerritoryName to, int numAttackers)` — additional case

- **TC73: Capturing the final enemy territory transitions phase to GAME_OVER** ( :white_check_mark: )
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

- **TC74: `draftArmy()` called in GAME_OVER phase throws IllegalStateException** ( :white_check_mark: )
    - **State of the system**:
        - phase: GAME_OVER
    - **Expected output**: throw IllegalStateException

- **TC75: `attack()` called in GAME_OVER phase throws IllegalStateException** ( :white_check_mark: )
    - **State of the system**:
        - phase: GAME_OVER
    - **Expected output**: throw IllegalStateException

- **TC76: `endAttack()` called in GAME_OVER phase throws IllegalStateException** ( :white_check_mark: )
    - **State of the system**:
        - phase: GAME_OVER
    - **Expected output**: throw IllegalStateException

- **TC77: `fortify()` called in GAME_OVER phase throws IllegalStateException** ( :white_check_mark: )
    - **State of the system**:
        - phase: GAME_OVER
    - **Expected output**: throw IllegalStateException

- **TC78: `endTurn()` called in GAME_OVER phase throws IllegalStateException** ( :white_check_mark: )
    - **State of the system**:
        - phase: GAME_OVER
    - **Expected output**: throw IllegalStateException

- **TC79: Attacker captures already-owned defender territory with real map** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, isDraftComplete: true
        - real WorldMap used (not mocked)
        - ALASKA owned by RED (current player), armies = 3
        - ALBERTA owned by BLUE, armies = 1
        - ALASKA and ALBERTA are neighbors (real adjacency)
        - numAttackers: 1
        - Random mocked so attacker wins all dice
    - **Expected output**:
        - no exception thrown
        - ALBERTA owned by RED
        - ALBERTA armies = 1
        - ALASKA armies >= 1

- **TC80: Fortify once succeeds and hasFortifiedThisTurn is set** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY
        - ALASKA owned by RED (current player), armies = 3
        - ALBERTA owned by RED (current player)
        - ALASKA and ALBERTA are neighbors
        - armies: 1
        - hasFortifiedThisTurn: false
    - **Expected output**:
        - ALASKA armies decrease by 1
        - ALBERTA armies increase by 1
        - hasFortifiedThisTurn set to true

- **TC81: Fortify called twice in same turn throws IllegalStateException** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY
        - ALASKA owned by RED (current player), armies = 3
        - ALBERTA owned by RED (current player)
        - ALASKA and ALBERTA are neighbors
        - first fortify() already succeeded this turn
        - hasFortifiedThisTurn: true
    - **Expected output**: throw IllegalStateException

- **TC82: hasFortifiedThisTurn resets after endTurn** ( :white_check_mark: )
    - **State of the system**:
        - RED successfully called fortify() this turn
        - RED calls endTurn()
        - BLUE is now current player
        - BLUE calls fortify() on valid adjacent owned territories
    - **Expected output**: fortify succeeds — no exception thrown

- **TC83: Attack before draft ever initialized throws IllegalStateException** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK
        - current player has not called `draftArmy()` this turn
        - current player: RED
        - ALASKA owned by RED, armies = 3
        - ALBERTA owned by BLUE
        - ALASKA and ALBERTA are neighbors
        - numAttackers: 1
    - **Expected output**: throw IllegalStateException

- **TC84: Attack after draft fully complete succeeds** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK
        - isDraftInitialized == true
        - draftArmiesRemaining == 0
        - current player: RED
        - ALASKA owned by RED, armies = 3
        - ALBERTA owned by BLUE, armies = 1
        - ALASKA and ALBERTA are neighbors
        - numAttackers: 1
        - Random mocked so attacker wins all dice
    - **Expected output**: attack executes — no exception thrown

- **TC85: After endTurn new player cannot attack without drafting** ( :white_check_mark: )
    - **State of the system**:
        - RED ends turn, BLUE is new current player
        - BLUE has not called draftArmy()
        - valid owned source, enemy target, adjacent territories
        - numAttackers: 1
    - **Expected output**: throw IllegalStateException

## Method: `int getDraftArmies()` — additional case

- **TC86: Draft armies after all draft armies are placed returns zero** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK
        - current player: RED
        - RED owns 1 territory
        - RED has placed all 3 draft armies with `draftArmy()`
        - isDraftComplete() returns true
    - **Expected output**: getDraftArmies() == 0

## Method: `void endAttack()` — additional case

- **TC87: End attack before draft is complete throws IllegalStateException** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK
        - current player: RED
        - RED has not placed all draft armies
    - **Expected output**: throw IllegalStateException

## Method: `void placeArmy(TerritoryName territory)` — additional case

- **TC88: Last setup army transitions to ATTACK with first setup player active** ( :white_check_mark: )
    - **State of the system**:
        - phase: SETUP
        - 3-player game: RED, BLUE, GREEN (in that order)
        - first setup player: RED
        - RED, BLUE, and GREEN each have 1 setup army left
        - each player places their final setup army in turn
    - **Expected output**:
        - phase == ATTACK

## Method: `void fortify(TerritoryName from, TerritoryName to, int armies)` — additional case

- **TC89: Fortify between non-adjacent territories connected through owned chain** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY
        - ALASKA owned by RED (current player), armies = 3
        - NORTHWEST_TERRITORY owned by RED (current player), armies = 1
        - ONTARIO owned by RED (current player), armies = 1
        - ALASKA and ONTARIO are not direct neighbors; path: ALASKA→NORTHWEST_TERRITORY→ONTARIO
        - armies: 1
    - **Expected output**:
        - ALASKA armies = 2
        - ONTARIO armies = 2

## Method: `boolean canTradeCards(List<Card> cards)`

Returns true if the given list of 3 cards forms a valid tradeable set. Valid sets: three of the same type, one of each type, or any set containing at least one wild card.

- **TC90: null list** ( :white_check_mark: )
    - **State of the system**: cards == null
    - **Expected output**: false

- **TC91: list containing null card** ( :white_check_mark: )
    - **State of the system**: cards = [INFANTRY/ALASKA, CAVALRY/ALBERTA, null]
    - **Expected output**: false

- **TC92: fewer than 3 cards (boundary: 2)** ( :white_check_mark: )
    - **State of the system**: cards = [INFANTRY/ALASKA, INFANTRY/ALBERTA]
    - **Expected output**: false

- **TC93: more than 3 cards (boundary: 4)** ( :white_check_mark: )
    - **State of the system**: cards = [INFANTRY/ALASKA, INFANTRY/ALBERTA, INFANTRY/BRAZIL, CAVALRY/CHINA]
    - **Expected output**: false

- **TC94: three of the same type** ( :white_check_mark: )
    - **State of the system**: cards = [INFANTRY/ALASKA, INFANTRY/ALBERTA, INFANTRY/BRAZIL]
    - **Expected output**: true

- **TC95: one of each type** ( :white_check_mark: )
    - **State of the system**: cards = [INFANTRY/ALASKA, CAVALRY/ALBERTA, ARTILLERY/BRAZIL]
    - **Expected output**: true

- **TC96: two same type and one different non-wild** ( :white_check_mark: )
    - **State of the system**: cards = [INFANTRY/ALASKA, INFANTRY/ALBERTA, CAVALRY/BRAZIL]
    - **Expected output**: false

- **TC97: one territory card and two wilds** ( :white_check_mark: )
    - **State of the system**: cards = [INFANTRY/ALASKA, WILD, WILD]
    - **Expected output**: true

- **TC98: two different types and one wild** ( :white_check_mark: )
    - **State of the system**: cards = [INFANTRY/ALASKA, CAVALRY/ALBERTA, WILD]
    - **Expected output**: true

- **TC99: two same type and one wild** ( :white_check_mark: )
    - **State of the system**: cards = [INFANTRY/ALASKA, INFANTRY/ALBERTA, WILD]
    - **Expected output**: true

## Method: `void tradeCards(List<Card> cards)`

Current player trades exactly 3 cards for draft armies. The bonus follows the sequence 4, 6, 8, 10, 12, 15, 20, 25, ... (increases by 5 after the 6th trade). If the draft has not yet been initialized, territory armies are also computed at this point. If a traded card's territory is owned by the current player, 2 bonus armies are placed on that territory.

- **TC100: wrong phase** ( :white_check_mark: )
    - **State of the system**: phase: FORTIFY; player has valid tradeable cards
    - **Expected output**: throw IllegalStateException

- **TC101: null list** ( :white_check_mark: )
    - **State of the system**: phase: ATTACK; cards == null
    - **Expected output**: throw IllegalArgumentException

- **TC102: list containing null** ( :white_check_mark: )
    - **State of the system**: phase: ATTACK; cards = [INFANTRY/ALASKA, null, INFANTRY/BRAZIL]
    - **Expected output**: throw IllegalArgumentException

- **TC103: fewer than 3 cards (boundary: 2)** ( :white_check_mark: )
    - **State of the system**: phase: ATTACK; cards = 2 valid cards owned by player
    - **Expected output**: throw IllegalArgumentException

- **TC104: more than 3 cards (boundary: 4)** ( :white_check_mark: )
    - **State of the system**: phase: ATTACK; cards = 4 valid cards owned by player
    - **Expected output**: throw IllegalArgumentException

- **TC105: invalid card set** ( :white_check_mark: )
    - **State of the system**: phase: ATTACK; cards = [INFANTRY/ALASKA, INFANTRY/ALBERTA, CAVALRY/BRAZIL] (2+1, no wild)
    - **Expected output**: throw IllegalArgumentException

- **TC106: cards not owned by player** ( :white_check_mark: )
    - **State of the system**: phase: ATTACK; 3 valid card types presented but player has no cards
    - **Expected output**: throw IllegalArgumentException

- **TC107: trade before draft initialized — total armies = territory armies + card bonus** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK, isDraftInitialized: false
        - current player: RED, owns 3 territories (territory armies = max(3, 3/3) = 3)
        - RED holds 3 INFANTRY cards (valid trade set, 1st trade = +4 bonus)
    - **Expected output**:
        - total draft armies = 3 (territory) + 4 (bonus) = 7
        - after 7 calls to draftArmy(), isDraftComplete() == true

- **TC108: first trade adds 4 draft armies** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK; tradeSetCount = 0 before trade
        - RED holds a valid set; isDraftInitialized already true (territory armies already set to 3)
    - **Expected output**: draftArmiesRemaining increases by 4; total = 7; isDraftComplete after 7 draftArmy calls

- **TC109: second trade adds 6 draft armies** ( :white_check_mark: )
    - **State of the system**: tradeSetCount = 1 before trade
    - **Expected output**: draftArmiesRemaining increases by 6

- **TC110: third trade adds 8 draft armies** ( :white_check_mark: )
    - **State of the system**: tradeSetCount = 2 before trade
    - **Expected output**: draftArmiesRemaining increases by 8

- **TC111: fourth trade adds 10 draft armies** ( :white_check_mark: )
    - **State of the system**: tradeSetCount = 3 before trade
    - **Expected output**: draftArmiesRemaining increases by 10

- **TC112: fifth trade adds 12 draft armies** ( :white_check_mark: )
    - **State of the system**: tradeSetCount = 4 before trade
    - **Expected output**: draftArmiesRemaining increases by 12

- **TC113: sixth trade adds 15 draft armies** ( :white_check_mark: )
    - **State of the system**: tradeSetCount = 5 before trade
    - **Expected output**: draftArmiesRemaining increases by 15

- **TC114: seventh trade (after sixth) adds 20 draft armies** ( :white_check_mark: )
    - **State of the system**: tradeSetCount = 6 before trade (post-table: 15 + 5×1 = 20)
    - **Expected output**: draftArmiesRemaining increases by 20

- **TC115: traded card matches owned territory — 2 bonus armies on that territory** ( :white_check_mark: )
    - **State of the system**:
        - RED owns ALASKA; one of the 3 traded cards is for ALASKA
    - **Expected output**: ALASKA armies increase by 2

- **TC116: traded card matches unowned territory — no territory bonus** ( :white_check_mark: )
    - **State of the system**:
        - RED does not own the territory on any traded card
    - **Expected output**: no territory armies added

- **TC117: traded cards moved to deck discard pile** ( :white_check_mark: )
    - **State of the system**: RED trades 3 cards; discard pile was empty
    - **Expected output**:
        - RED has 0 cards
        - deck discard pile size == 3

## Method: `void moveArmiesAfterCapture(TerritoryName from, TerritoryName to, int armies)`

After capturing a territory, the current player may move additional armies (beyond the minimum auto-moved at capture) from the attacking territory into the captured one. Must be called during ATTACK phase and only after a capture was just made from `from` to `to`.

- **TC118: wrong phase** ( :white_check_mark: )
    - **State of the system**: phase: FORTIFY; no pending capture
    - **Expected output**: throw IllegalStateException

- **TC119: no prior capture (called without having captured)** ( :white_check_mark: )
    - **State of the system**: phase: ATTACK; no capture has occurred this attack
    - **Expected output**: throw IllegalStateException

- **TC120: 0 armies (boundary: below minimum)** ( :white_check_mark: )
    - **State of the system**: phase: ATTACK; prior capture from ALASKA to ALBERTA; armies = 0
    - **Expected output**: throw IllegalArgumentException

- **TC121: armies >= from.armies (none left behind — boundary)** ( :white_check_mark: )
    - **State of the system**: phase: ATTACK; prior capture; ALASKA has 2 armies; armies = 2
    - **Expected output**: throw IllegalArgumentException

- **TC122: valid additional movement beyond minimum** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK; ALASKA (RED, 4 armies) attacked ALBERTA (BLUE, 1 army) with 1 die
        - attacker won; captureTerritory auto-moved 1 army: ALASKA=3, ALBERTA=1
        - armies = 2
    - **Expected output**:
        - ALASKA armies = 1
        - ALBERTA armies = 3
        - current player == RED

## Method: `List<Card> getCards(PlayerColor color)`

Returns the list of cards held by the player with the given color.

- **TC123: player with no cards returns empty list** ( :white_check_mark: )
    - **State of the system**: RED has no cards
    - **Expected output**: empty list

- **TC124: player with one card returns list with that card** ( :white_check_mark: )
    - **State of the system**: RED holds exactly 1 card (INFANTRY/ALASKA)
    - **Expected output**: list containing that card

- **TC125: player with three cards returns all three** ( :white_check_mark: )
    - **State of the system**: RED holds 3 cards (INFANTRY/ALASKA, CAVALRY/ALBERTA, ARTILLERY/BRAZIL)
    - **Expected output**: list of all three cards in insertion order

- **TC126: color not in game throws IllegalArgumentException** ( :white_check_mark: )
    - **State of the system**: 3-player game (RED, BLUE, GREEN); color = CYAN
    - **Expected output**: throw IllegalArgumentException

## Method: `void draftArmy(TerritoryName territory)` — additional cases

- **TC127: player with 4 cards may draft** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK
        - RED holds 4 cards (boundary: one below the 5-card forced-trade threshold)
        - RED owns ALASKA
    - **Expected output**: draftArmy succeeds; 1 army placed on ALASKA

- **TC128: player with 5 cards must trade before drafting** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK
        - RED holds exactly 5 cards (boundary: at the forced-trade threshold)
    - **Expected output**: throw IllegalStateException

- **TC129: player with 6 cards must trade before drafting** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK
        - RED holds 6 cards (above the forced-trade threshold)
    - **Expected output**: throw IllegalStateException

## Method: `void endTurn()` — additional card-award cases

- **TC130: capturing one territory awards one card on endTurn** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY; capturedThisTurn = true
        - RED is current player
    - **Expected output**: RED receives 1 card; RED card count == 1

- **TC131: capturing more than one territory still awards only one card** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY; capturedThisTurn = true (set after multiple captures)
        - RED is current player
    - **Expected output**: RED receives exactly 1 card

- **TC132: no capture this turn awards no card** ( :white_check_mark: )
    - **State of the system**:
        - phase: FORTIFY; capturedThisTurn = false
        - RED is current player
    - **Expected output**: next player (BLUE) receives no card; BLUE card count == 0

- **TC133: capture flag resets for next player after endTurn** ( :white_check_mark: )
    - **State of the system**:
        - RED captures a territory and calls endTurn (capturedThisTurn = true → reset)
        - BLUE does not capture; calls endTurn
    - **Expected output**: BLUE card count == 0 (capture flag was reset, not inherited)

## Method: `void attack(TerritoryName from, TerritoryName to, int numAttackers)` — card-related cases

- **TC134: capture marks card award for end of turn** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK; attacker wins and captures ALBERTA
        - endAttack() then endTurn() called
    - **Expected output**: RED receives 1 card on endTurn

- **TC135: two captures in same turn still awards one card** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK; RED captures ALBERTA then NORTHWEST_TERRITORY in the same turn
        - endTurn() called
    - **Expected output**: RED receives exactly 1 card

- **TC136: failed attack (no capture) does not mark card award** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK; defender wins — attacker loses armies, no territory captured
    - **Expected output**: RED card count remains 0

- **TC137: capturing the last territory of a defeated player transfers their cards** ( :white_check_mark: )
    - **State of the system**:
        - phase: ATTACK; BLUE owns 0 territories after RED captures their last one
        - BLUE held 2 cards before capture
    - **Expected output**:
        - BLUE card count == 0
        - RED card count == 2 (BLUE's cards transferred immediately)
