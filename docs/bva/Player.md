# BVA Analysis for Player
## Method: `PlayerColor getColor()`

- **TC1: Get color of first player color** ( :white_check_mark: )
    - **State of the system**: player constructed with RED
    - **Expected output**: RED

- **TC2: Get color of last player color** ( :white_check_mark: )
    - **State of the system**: player constructed with CYAN
    - **Expected output**: CYAN

## Method: `String getName()`

- **TC3: Get name of player** ( :white_check_mark: )
    - **State of the system**: player constructed with name "Jovy"
    - **Expected output**: "Jovy"

## Method: `int getArmiesToPlace()`

- **TC4: Get armies when player has 0 armies** ( :white_check_mark: )
    - **State of the system**: player with armiesToPlace = 0
    - **Expected output**: 0

- **TC5: Get armies when player has 1 army** ( :white_check_mark: )
    - **State of the system**: player with armiesToPlace = 1
    - **Expected output**: 1

- **TC6: Get armies when player has more than 1 army** ( :white_check_mark: )
    - **State of the system**: player with armiesToPlace = 35
    - **Expected output**: 35
  
## Method: `boolean hasArmiesToPlace()`

- **TC7: Check player with 0 armies** ( :white_check_mark: )
    - **State of the system**: player with armiesToPlace = 0
    - **Expected output**: false

- **TC8: Check player with 1 army** ( :white_check_mark: )
    - **State of the system**: player with armiesToPlace = 1
    - **Expected output**: true

- **TC9: Check player with more than 1 army** ( :white_check_mark: )
    - **State of the system**: player with armiesToPlace = 35
    - **Expected output**: true

## Method: `void decreaseArmiesToPlace(int count)`

- **TC10: Decrease by 1 when player has 1 army** ( :white_check_mark: )
    - **State of the system**:
        - armiesToPlace = 1
        - count: 1
    - **Expected output**: armiesToPlace = 0

- **TC11: Decrease by 1 when player has more than 1 army** ( :white_check_mark: )
    - **State of the system**:
        - armiesToPlace = 35
        - count: 1
    - **Expected output**: armiesToPlace = 34

- **TC12: Decrease by more than 1 when player has more than 1 army** ( :white_check_mark: )
    - **State of the system**:
        - armiesToPlace = 35
        - count: 5
    - **Expected output**: armiesToPlace = 30

- **TC13: Decrease by exact amount player has** ( :white_check_mark: )
    - **State of the system**:
        - armiesToPlace = 35
        - count: 35
    - **Expected output**: armiesToPlace = 0

- **TC14: Decrease by more than player has** ( :white_check_mark: )
    - **State of the system**:
        - armiesToPlace = 5
        - count: 6
    - **Expected output**: throw IllegalArgumentException

- **TC15: Decrease by 0** ( :white_check_mark: )
    - **State of the system**:
        - armiesToPlace = 5
        - count: 0
    - **Expected output**: throw IllegalArgumentException

- **TC16: Decrease by negative amount** ( :white_check_mark: )
    - **State of the system**:
        - armiesToPlace = 5
        - count: -1
    - **Expected output**: throw IllegalArgumentException

## Method: `void increaseArmiesToPlace(int count)`

- **TC17: Increase by 1 when player has 0 armies** ( :x: )
    - **State of the system**:
        - armiesToPlace = 0
        - count: 1
    - **Expected output**: armiesToPlace = 1

- **TC18: Increase by 1 when player has more than 0 armies** ( :x: )
    - **State of the system**:
        - armiesToPlace = 5
        - count: 1
    - **Expected output**: armiesToPlace = 6

- **TC19: Increase by more than 1** ( :x: )
    - **State of the system**:
        - armiesToPlace = 0
        - count: 3
    - **Expected output**: armiesToPlace = 3

- **TC20: Increase by 0** ( :x: )
    - **State of the system**:
        - armiesToPlace = 5
        - count: 0
    - **Expected output**: throw IllegalArgumentException

- **TC21: Increase by negative amount** ( :x: )
    - **State of the system**:
        - armiesToPlace = 5
        - count: -1
    - **Expected output**: throw IllegalArgumentException
  
- **TC22: Increase by more than 1 when player has more than 0 armies** ( :x: )
    - **State of the system**:
        - armiesToPlace = 5
        - count: 3
    - **Expected output**: armiesToPlace = 8