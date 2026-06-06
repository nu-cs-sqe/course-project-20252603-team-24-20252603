# BVA Analysis for Card
## Method: `Card(CardType type, TerritoryName territory)`

- **TC1: Construct infantry card with first territory** ( :white_check_mark: )
    - **State of the system**:
        - type: INFANTRY
        - territory: ALASKA
    - **Expected output**:
        - getType() == INFANTRY
        - getTerritory() == ALASKA

- **TC2: Construct cavalry card with territory** ( :white_check_mark: )
    - **State of the system**:
        - type: CAVALRY
        - territory: BRAZIL
    - **Expected output**:
        - getType() == CAVALRY
        - getTerritory() == BRAZIL

- **TC3: Construct artillery card with last territory** ( :white_check_mark: )
    - **State of the system**:
        - type: ARTILLERY
        - territory: INDONESIA
    - **Expected output**:
        - getType() == ARTILLERY
        - getTerritory() == INDONESIA

- **TC4: Construct wild card with no territory** ( :white_check_mark: )
    - **State of the system**:
        - type: WILD
        - territory: null
    - **Expected output**:
        - getType() == WILD
        - getTerritory() == null

- **TC5: Construct card with null type** ( :white_check_mark: )
    - **State of the system**:
        - type: null
        - territory: ALASKA
    - **Expected output**: throw IllegalArgumentException

- **TC6: Construct non-wild card with no territory** ( :white_check_mark: )
    - **State of the system**:
        - type: INFANTRY
        - territory: null
    - **Expected output**: throw IllegalArgumentException

- **TC7: Construct wild card with territory** ( :white_check_mark: )
    - **State of the system**:
        - type: WILD
        - territory: ALASKA
    - **Expected output**: throw IllegalArgumentException

## Method: `CardType getType()`

- **TC8: Get type from infantry card** ( :white_check_mark: )
    - **State of the system**: card constructed with type INFANTRY
    - **Expected output**: INFANTRY

- **TC9: Get type from wild card** ( :white_check_mark: )
    - **State of the system**: card constructed with type WILD
    - **Expected output**: WILD

## Method: `TerritoryName getTerritory()`

- **TC10: Get first territory from card** ( :white_check_mark: )
    - **State of the system**: card constructed with territory ALASKA
    - **Expected output**: ALASKA

- **TC11: Get last territory from card** ( :white_check_mark: )
    - **State of the system**: card constructed with territory INDONESIA
    - **Expected output**: INDONESIA

- **TC12: Get territory from wild card** ( :white_check_mark: )
    - **State of the system**: wild card constructed with no territory
    - **Expected output**: null

## Method: `boolean isWild()`

- **TC13: Check non-wild card** ( :white_check_mark: )
    - **State of the system**: card constructed with type INFANTRY
    - **Expected output**: false

- **TC14: Check wild card** ( :white_check_mark: )
    - **State of the system**: card constructed with type WILD
    - **Expected output**: true

## Method: `boolean matchesTerritory(TerritoryName territory)`

- **TC15: Card matches same territory** ( :white_check_mark: )
    - **State of the system**:
        - card territory: ALASKA
        - territory: ALASKA
    - **Expected output**: true

- **TC16: Card does not match different territory** ( :white_check_mark: )
    - **State of the system**:
        - card territory: ALASKA
        - territory: ALBERTA
    - **Expected output**: false

- **TC17: Wild card does not match territory** ( :white_check_mark: )
    - **State of the system**:
        - card is WILD
        - territory: ALASKA
    - **Expected output**: false

- **TC18: Card matching null territory** ( :white_check_mark: )
    - **State of the system**:
        - card territory: ALASKA
        - territory: null
    - **Expected output**: false

## Method: `Card(CardType type, TerritoryName territory)`

- **TC19: Construct card from outside domain package** ( :white_check_mark: )
    - **State of the system**:
        - code outside the domain package constructs an INFANTRY card
        - territory: ALASKA
    - **Expected output**:
        - Card is accessible outside domain
        - constructor is accessible outside domain

## Method: `CardType getType()`

- **TC20: Get card type from outside domain package** ( :white_check_mark: )
    - **State of the system**:
        - code outside the domain package has an INFANTRY card
    - **Expected output**:
        - CardType is accessible outside domain
        - getType() returns INFANTRY

## Method: `TerritoryName getTerritory()`

- **TC21: Get card territory from outside domain package** ( :white_check_mark: )
    - **State of the system**:
        - code outside the domain package has an ALASKA card
    - **Expected output**:
        - getTerritory() is accessible outside domain
        - getTerritory() returns ALASKA
