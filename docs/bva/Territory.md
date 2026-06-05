# BVA Analysis for Territory
## Method: `TerritoryName getName()`

- **TC1: Get name of first territory** ( :white_check_mark: )
    - **State of the system**: territory constructed with ALASKA
    - **Expected output**: ALASKA

- **TC2: Get name of last territory** ( :white_check_mark: )
    - **State of the system**: territory constructed with INDONESIA
    - **Expected output**: INDONESIA

## Method: `int getArmies()`

- **TC3: Get armies on territory with 0 armies** ( :white_check_mark: )
    - **State of the system**: territory with armies = 0
    - **Expected output**: 0

- **TC4: Get armies on territory with 1 army** ( :white_check_mark: )
    - **State of the system**: territory with armies = 1
    - **Expected output**: 1

- **TC5: Get armies on territory with more than 1 army** ( :white_check_mark: )
    - **State of the system**: territory with armies = 5
    - **Expected output**: 5

## Method: `boolean isUnclaimed()`

- **TC6: Check unclaimed territory** ( :white_check_mark: )
    - **State of the system**: territory with no owner
    - **Expected output**: true

- **TC7: Check claimed territory** ( :white_check_mark: )
    - **State of the system**: territory claimed by RED
    - **Expected output**: false

## Method: `boolean isOwnedBy(PlayerColor color)`

- **TC8: Check ownership on unclaimed territory** ( :white_check_mark: )
    - **State of the system**:
        - territory with no owner
        - color: RED
    - **Expected output**: false

- **TC9: Check ownership with matching owner** ( :white_check_mark: )
    - **State of the system**:
        - territory claimed by RED
        - color: RED
    - **Expected output**: true

- **TC10: Check ownership with different owner** ( :white_check_mark: )
    - **State of the system**:
        - territory claimed by RED
        - color: BLUE
    - **Expected output**: false

## Method: `void claim(PlayerColor color)`

- **TC11: Claim an unclaimed territory** ( :white_check_mark: )
    - **State of the system**:
        - territory with no owner
        - color: RED
    - **Expected output**:
        - territory is no longer unclaimed
        - territory is owned by RED

- **TC12: Claim territory already claimed by same player** ( :white_check_mark: )
    - **State of the system**:
        - territory already claimed by RED
        - color: RED
    - **Expected output**: throw IllegalStateException

- **TC13: Claim territory already claimed by different player** ( :white_check_mark: )
    - **State of the system**:
        - territory already claimed by RED
        - color: BLUE
    - **Expected output**: throw IllegalStateException

## Method: `void addArmies(int count)`

- **TC14: Add 1 army to territory with 0 armies** ( :white_check_mark: )
    - **State of the system**:
        - territory with armies = 0
        - count: 1
    - **Expected output**: territory armies = 1

- **TC15: Add more than 1 army to territory with 0 armies** ( :white_check_mark: )
    - **State of the system**:
        - territory with armies = 0
        - count: 5
    - **Expected output**: territory armies = 5

- **TC16: Add 1 army to territory with 1 army** ( :white_check_mark: )
    - **State of the system**:
        - territory with armies = 1
        - count: 1
    - **Expected output**: territory armies = 2

- **TC17: Add 1 army to territory with more than 1 army** ( :white_check_mark: )
    - **State of the system**:
        - territory with armies = 3
        - count: 1
    - **Expected output**: territory armies = 4

- **TC18: Add more than 1 army to territory with more than 1 army** ( :white_check_mark: )
    - **State of the system**:
        - territory with armies = 3
        - count: 5
    - **Expected output**: territory armies = 8

- **TC19: Add 0 armies** ( :white_check_mark: )
    - **State of the system**:
        - territory with armies = 3
        - count: 0
    - **Expected output**: throw IllegalArgumentException

- **TC20: Add negative armies** ( :white_check_mark: )
    - **State of the system**:
        - territory with armies = 3
        - count: -1
    - **Expected output**: throw IllegalArgumentException

## Method: `void removeArmies(int count)`

- **TC21: Remove 1 army from territory with 1 army** ( :white_check_mark: )
    - **State of the system**:
        - territory with armies = 1
        - count: 1
    - **Expected output**: territory armies = 0

- **TC22: Remove 1 army from territory with more than 1 army** ( :white_check_mark: )
    - **State of the system**:
        - territory with armies = 3
        - count: 1
    - **Expected output**: territory armies = 2

- **TC23: Remove more than 1 army from territory with more than 1 army** ( :white_check_mark: )
    - **State of the system**:
        - territory with armies = 5
        - count: 3
    - **Expected output**: territory armies = 2

- **TC24: Remove exact amount territory has** ( :white_check_mark: )
    - **State of the system**:
        - territory with armies = 3
        - count: 3
    - **Expected output**: territory armies = 0

- **TC25: Remove more than territory has** ( :white_check_mark: )
    - **State of the system**:
        - territory with armies = 2
        - count: 3
    - **Expected output**: throw IllegalArgumentException

- **TC26: Remove 0 armies** ( :white_check_mark: )
    - **State of the system**:
        - territory with armies = 3
        - count: 0
    - **Expected output**: throw IllegalArgumentException

- **TC27: Remove negative armies** ( :white_check_mark: )
    - **State of the system**:
        - territory with armies = 3
        - count: -1
    - **Expected output**: throw IllegalArgumentException

- **TC28: Remove 1 army from territory with 0 armies** ( :white_check_mark: )
    - **State of the system**:
        - territory with armies = 0
        - count: 1
    - **Expected output**: throw IllegalArgumentException