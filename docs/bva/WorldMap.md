# BVA Analysis for WorldMap
## Method: `boolean areNeighbors(TerritoryName first, TerritoryName second)`

- **TC1: Check two adjacent territories** ( :white_check_mark: )
    - **State of the system**:
        - first: ALASKA
        - second: ALBERTA
    - **Expected output**: true

- **TC2: Check two non-adjacent territories** ( :white_check_mark: )
    - **State of the system**:
        - first: ALASKA
        - second: BRAZIL
    - **Expected output**: false

- **TC3: Check same territory against itself** ( :white_check_mark: )
    - **State of the system**:
        - first: ALASKA
        - second: ALASKA
    - **Expected output**: false

## Method: `boolean isOwnedBy(TerritoryName territory, PlayerColor color)`

- **TC4: Check ownership on unclaimed territory** ( :white_check_mark: )
    - **State of the system**:
        - territory: ALASKA unclaimed
        - color: RED
    - **Expected output**: false

- **TC5: Check ownership with matching owner** ( :white_check_mark: )
    - **State of the system**:
        - territory: ALASKA claimed by RED
        - color: RED
    - **Expected output**: true

- **TC6: Check ownership with different owner** ( :white_check_mark: )
    - **State of the system**:
        - territory: ALASKA claimed by RED
        - color: BLUE
    - **Expected output**: false

## Method: `boolean isUnclaimed(TerritoryName territory)`

- **TC7: Check unclaimed territory** ( :white_check_mark: )
    - **State of the system**: ALASKA unclaimed
    - **Expected output**: true

- **TC8: Check claimed territory** ( :white_check_mark: )
    - **State of the system**: ALASKA claimed by RED
    - **Expected output**: false

## Method: `int getArmies(TerritoryName territory)`

- **TC9: Get armies on territory with 0 armies** ( :white_check_mark: )
    - **State of the system**: ALASKA with armies = 0
    - **Expected output**: 0

- **TC10: Get armies on territory with 1 army** ( :white_check_mark: )
    - **State of the system**: ALASKA with armies = 1
    - **Expected output**: 1

- **TC11: Get armies on territory with more than 1 army** ( :white_check_mark: )
    - **State of the system**: ALASKA with armies = 5
    - **Expected output**: 5

## Method: `void claim(TerritoryName territory, PlayerColor color)`

- **TC12: Claim an unclaimed territory** ( :white_check_mark: )
    - **State of the system**:
        - territory: ALASKA unclaimed
        - color: RED
    - **Expected output**:
        - ALASKA is no longer unclaimed
        - ALASKA is owned by RED

- **TC13: Claim territory already claimed by same player** ( :white_check_mark: )
    - **State of the system**:
        - territory: ALASKA claimed by RED
        - color: RED
    - **Expected output**: throw IllegalStateException

- **TC14: Claim territory already claimed by different player** ( :white_check_mark: )
    - **State of the system**:
        - territory: ALASKA claimed by RED
        - color: BLUE
    - **Expected output**: throw IllegalStateException

## Method: `void addArmies(TerritoryName territory, int count)`

- **TC15: Add 1 army to territory with 0 armies** ( :white_check_mark: )
    - **State of the system**:
        - territory: ALASKA with armies = 0
        - count: 1
    - **Expected output**: ALASKA armies = 1

- **TC16: Add more than 1 army to territory with 0 armies** ( :white_check_mark: )
    - **State of the system**:
        - territory: ALASKA with armies = 0
        - count: 5
    - **Expected output**: ALASKA armies = 5

- **TC17: Add 1 army to territory with 1 army** ( :white_check_mark: )
    - **State of the system**:
        - territory: ALASKA with armies = 1
        - count: 1
    - **Expected output**: ALASKA armies = 2

- **TC18: Add 1 army to territory with more than 1 army** ( :white_check_mark: )
    - **State of the system**:
        - territory: ALASKA with armies = 3
        - count: 1
    - **Expected output**: ALASKA armies = 4

- **TC19: Add more than 1 army to territory with more than 1 army** ( :white_check_mark: )
    - **State of the system**:
        - territory: ALASKA with armies = 3
        - count: 5
    - **Expected output**: ALASKA armies = 8

- **TC20: Add 0 armies** ( :white_check_mark: )
    - **State of the system**:
        - territory: ALASKA with armies = 3
        - count: 0
    - **Expected output**: throw IllegalArgumentException

- **TC21: Add negative armies** ( :white_check_mark: )
    - **State of the system**:
        - territory: ALASKA with armies = 3
        - count: -1
    - **Expected output**: throw IllegalArgumentException

## Method: `int countTerritoriesOwnedBy(PlayerColor color)`

- **TC22: Count territories when player owns none** ( :white_check_mark: )
    - **State of the system**:
        - color: RED
        - RED owns 0 territories
    - **Expected output**: 0

- **TC23: Count territories when player owns 1** ( :white_check_mark: )
    - **State of the system**:
        - color: RED
        - RED owns ALASKA
    - **Expected output**: 1

- **TC24: Count territories when player owns more than 1** ( :white_check_mark: )
    - **State of the system**:
        - color: RED
        - RED owns ALASKA, ALBERTA, ONTARIO
    - **Expected output**: 3

- **TC25: Count territories when player owns all 42** ( :white_check_mark: )
    - **State of the system**:
        - color: RED
        - RED owns all 42 territories
    - **Expected output**: 42

## Method: `Set<TerritoryName> getTerritoriesOwnedBy(PlayerColor color)`

- **TC26: Get territories when player owns none** ( :white_check_mark: )
    - **State of the system**:
        - color: RED
        - RED owns 0 territories
    - **Expected output**: empty set

- **TC27: Get territories when player owns 1** ( :white_check_mark: )
    - **State of the system**:
        - color: RED
        - RED owns ALASKA
    - **Expected output**: set containing ALASKA

- **TC28: Get territories when player owns more than 1** ( :white_check_mark: )
    - **State of the system**:
        - color: RED
        - RED owns ALASKA, ALBERTA, ONTARIO
    - **Expected output**: set containing ALASKA, ALBERTA, ONTARIO

- **TC29: Get territories when player owns all 42** ( :white_check_mark: )
    - **State of the system**:
        - color: RED
        - RED owns all 42 territories
    - **Expected output**: set containing all 42 territories

## Method: `void removeArmies(TerritoryName territory, int count)`

- **TC30: Remove 1 army from territory with 1 army** ( :white_check_mark: )
    - **State of the system**:
        - ALASKA with armies = 1
        - count: 1
    - **Expected output**: ALASKA armies = 0

- **TC31: Remove 1 army from territory with more than 1 army** ( :white_check_mark: )
    - **State of the system**:
        - ALASKA with armies = 3
        - count: 1
    - **Expected output**: ALASKA armies = 2

- **TC32: Remove more than 1 army from territory with more than 1 army** ( :white_check_mark: )
    - **State of the system**:
        - ALASKA with armies = 5
        - count: 3
    - **Expected output**: ALASKA armies = 2

- **TC33: Remove exact amount territory has** ( :white_check_mark: )
    - **State of the system**:
        - ALASKA with armies = 3
        - count: 3
    - **Expected output**: ALASKA armies = 0

- **TC34: Remove more than territory has** ( :white_check_mark: )
    - **State of the system**:
        - ALASKA with armies = 2
        - count: 3
    - **Expected output**: throw IllegalArgumentException

- **TC35: Remove 0 armies** ( :white_check_mark: )
    - **State of the system**:
        - ALASKA with armies = 3
        - count: 0
    - **Expected output**: throw IllegalArgumentException

- **TC36: Remove negative armies** ( :white_check_mark: )
    - **State of the system**:
        - ALASKA with armies = 3
        - count: -1
    - **Expected output**: throw IllegalArgumentException

- **TC37: Remove 1 army from territory with 0 armies** ( :white_check_mark: )
    - **State of the system**:
        - ALASKA with armies = 0
        - count: 1
    - **Expected output**: throw IllegalArgumentException

## Method: `boolean areNeighbors(TerritoryName first, TerritoryName second)`

- **TC38: Default map has adjacent territories** ( :x: )
    - **State of the system**:
        - WorldMap constructed with default constructor (real map)
        - first: ALASKA
        - second: ALBERTA
    - **Expected output**: true

- **TC39: Default map adjacency is bidirectional** ( :x: )
    - **State of the system**:
        - WorldMap constructed with default constructor (real map)
        - first: ALBERTA
        - second: ALASKA
    - **Expected output**: true

## Method: `boolean areConnectedThrough(TerritoryName from, TerritoryName to, PlayerColor owner)`

- **TC40: Adjacent territories owned by same player are connected** ( :x: )
    - **State of the system**:
        - ALASKA owned by RED
        - ALBERTA owned by RED
        - from: ALASKA
        - to: ALBERTA
        - owner: RED
    - **Expected output**: true

- **TC41: Non-adjacent territories connected through owned chain are connected** ( :x: )
    - **State of the system**:
        - ALASKA owned by RED
        - NORTHWEST_TERRITORY owned by RED
        - ONTARIO owned by RED
        - from: ALASKA
        - to: ONTARIO
        - owner: RED
    - **Expected output**: true

- **TC42: Broken owned chain is not connected** ( :x: )
    - **State of the system**:
        - ALASKA owned by RED
        - NORTHWEST_TERRITORY owned by BLUE
        - ONTARIO owned by RED
        - from: ALASKA
        - to: ONTARIO
        - owner: RED
    - **Expected output**: false

- **TC43: Destination not owned by player is not connected** ( :x: )
    - **State of the system**:
        - ALASKA owned by RED
        - NORTHWEST_TERRITORY owned by RED
        - ONTARIO owned by BLUE
        - from: ALASKA
        - to: ONTARIO
        - owner: RED
    - **Expected output**: false

- **TC44: Source not owned by player is not connected** ( :x: )
    - **State of the system**:
        - ALASKA owned by BLUE
        - NORTHWEST_TERRITORY owned by RED
        - ONTARIO owned by RED
        - from: ALASKA
        - to: ONTARIO
        - owner: RED
    - **Expected output**: false
