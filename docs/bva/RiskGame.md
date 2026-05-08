# BVA Analysis for RiskGame
## Method: `RiskGame(Map<PlayerColor, String> playerInfo)` (constructor)

- **TC1: Construct game with minimum valid players (3)** ( ❌ )
    - **State of the system**:
        - playerInfo: {RED="Jonathan", BLUE="Justin", GREEN="Prashant"}
    - **Expected output**: getPhase() == SCRAMBLE

- **TC2: Construct game with 4 players** ( ❌ )
    - **State of the system**:
        - playerInfo: {RED="Jonathan", BLUE="Justin", GREEN="Prashant", ORANGE="David"}
    - **Expected output**: getPhase() == SCRAMBLE

- **TC3: Construct game with 5 players** ( ❌ )
    - **State of the system**:
        - playerInfo: {RED="Jonathan", BLUE="Justin", GREEN="Prashant",
          ORANGE="David", PINK="Alice"}
    - **Expected output**: getPhase() == SCRAMBLE

- **TC4: Construct game with maximum valid players (6)** ( ❌ )
    - **State of the system**:
        - playerInfo: {RED="Jonathan", BLUE="Justin", GREEN="Prashant",
          ORANGE="David", PINK="Alice", CYAN="Bob"}
    - **Expected output**: getPhase() == SCRAMBLE

- **TC5: Construct game with too few players (2)** ( ❌ )
    - **State of the system**:
        - playerInfo: {RED="Jonathan", BLUE="Justin"}
    - **Expected output**: throw IllegalArgumentException

- **TC6: Construct game with empty map** ( ❌ )
    - **State of the system**:
        - playerInfo: {}
    - **Expected output**: throw IllegalArgumentException

## Method: `GamePhase getPhase()`

- **TC7: Get phase after all territories claimed** ( ❌ )
    - **State of the system**: all 42 territories claimed
    - **Expected output**: SETUP

- **TC8: Get phase after all armies placed** ( ❌ )
    - **State of the system**: all players have placed all armies
    - **Expected output**: ATTACK

## Method: `PlayerColor getCurrentPlayerColor()`

- **TC9: Get current player color at game start** ( ❌ )
    - **State of the system**:
        - game constructed with RED, BLUE, GREEN
        - Random mocked to make RED go first
    - **Expected output**: RED

- **TC10: Get current player color after turn ends** ( ❌ )
    - **State of the system**:
        - RED claims a territory, turn advances
        - Random mocked so BLUE goes second
    - **Expected output**: BLUE

## Method: `String getCurrentPlayerName()`

- **TC11: Get current player name matches current player color** ( ❌ )
    - **State of the system**:
        - game constructed with RED="Jonathan", BLUE="Justin", GREEN="Prashant"
        - Random mocked to make RED go first
    - **Expected output**: "Jonathan"

## Method: `int getArmiesToPlace()`

- **TC12: Get armies to place at game start for 3 players** ( ❌ )
    - **State of the system**: game just constructed with 3 players
    - **Expected output**: 35

- **TC13: Get armies to place at game start for 4 players** ( ❌ )
    - **State of the system**: game just constructed with 4 players
    - **Expected output**: 30

- **TC14: Get armies to place at game start for 5 players** ( ❌ )
    - **State of the system**: game just constructed with 5 players
    - **Expected output**: 25

- **TC15: Get armies to place at game start for 6 players** ( ❌ )
    - **State of the system**: game just constructed with 6 players
    - **Expected output**: 20

- **TC16: Get armies after claiming 1 territory** ( ❌ )
    - **State of the system**:
        - game constructed with 3 players
        - current player claims 1 territory
    - **Expected output**: 34

## Method: `boolean isSetupComplete()`

- **TC17: Check setup not complete at game start** ( ❌ )
    - **State of the system**: game just constructed
    - **Expected output**: false

- **TC18: Check setup complete after all armies placed** ( ❌ )
    - **State of the system**: all players have placed all armies
    - **Expected output**: true

## Method: `void claimTerritory(TerritoryName territory)`

- **TC19: Claim unclaimed territory during SCRAMBLE phase** ( ❌ )
    - **State of the system**:
        - phase: SCRAMBLE
        - territory: ALASKA unclaimed
        - current player: RED
    - **Expected output**:
        - ALASKA owned by RED
        - RED armies decreased by 1
        - turn advances to next player

- **TC20: Claim territory in wrong phase** ( ❌ )
    - **State of the system**:
        - phase: SETUP
        - territory: ALASKA unclaimed
    - **Expected output**: throw IllegalStateException

- **TC21: Claim already claimed territory** ( ❌ )
    - **State of the system**:
        - phase: SCRAMBLE
        - territory: ALASKA already claimed by RED
    - **Expected output**: throw IllegalStateException

- **TC22: Claim last unclaimed territory transitions to SETUP** ( ❌ )
    - **State of the system**:
        - phase: SCRAMBLE
        - 41 territories already claimed
        - territory: last unclaimed territory
    - **Expected output**:
        - all 42 territories claimed
        - phase transitions to SETUP

## Method: `void placeArmy(TerritoryName territory)`

- **TC23: Place army on owned territory during SETUP phase** ( ❌ )
    - **State of the system**:
        - phase: SETUP
        - ALASKA owned by current player RED
        - RED has armies to place
    - **Expected output**:
        - ALASKA armies increase by 1
        - RED armies decrease by 1
        - turn advances to next player

- **TC24: Place army in wrong phase** ( ❌ )
    - **State of the system**:
        - phase: SCRAMBLE
        - ALASKA owned by current player
    - **Expected output**: throw IllegalStateException

- **TC25: Place army on territory not owned by current player** ( ❌ )
    - **State of the system**:
        - phase: SETUP
        - ALASKA owned by BLUE
        - current player is RED
    - **Expected output**: throw IllegalArgumentException

- **TC26: Place army when player has no armies left** ( ❌ )
    - **State of the system**:
        - phase: SETUP
        - ALASKA owned by current player
        - current player armiesToPlace = 0
    - **Expected output**: throw IllegalArgumentException

- **TC27: Last army placed transitions to ATTACK** ( ❌ )
    - **State of the system**:
        - phase: SETUP
        - all players have 1 army left
        - current player places last army
    - **Expected output**:
        - phase transitions to ATTACK
        - isSetupComplete() returns true