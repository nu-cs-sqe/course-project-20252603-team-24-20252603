# BVA Analysis for RiskCard
## Method: `RiskCard(CardType type, TerritoryName territory)`

- **TC1: Construct infantry card with first territory** ( :x: )
    - **State of the system**:
        - type: INFANTRY
        - territory: ALASKA
    - **Expected output**:
        - getType() == INFANTRY
        - getTerritory() == ALASKA

- **TC2: Construct cavalry card with territory** ( :x: )
    - **State of the system**:
        - type: CAVALRY
        - territory: BRAZIL
    - **Expected output**:
        - getType() == CAVALRY
        - getTerritory() == BRAZIL

- **TC3: Construct artillery card with last territory** ( :x: )
    - **State of the system**:
        - type: ARTILLERY
        - territory: INDONESIA
    - **Expected output**:
        - getType() == ARTILLERY
        - getTerritory() == INDONESIA

- **TC4: Construct wild card with no territory** ( :x: )
    - **State of the system**:
        - type: WILD
        - territory: null
    - **Expected output**:
        - getType() == WILD
        - getTerritory() == null

- **TC5: Construct card with null type** ( :x: )
    - **State of the system**:
        - type: null
        - territory: ALASKA
    - **Expected output**: throw IllegalArgumentException

- **TC6: Construct non-wild card with no territory** ( :x: )
    - **State of the system**:
        - type: INFANTRY
        - territory: null
    - **Expected output**: throw IllegalArgumentException

- **TC7: Construct wild card with territory** ( :x: )
    - **State of the system**:
        - type: WILD
        - territory: ALASKA
    - **Expected output**: throw IllegalArgumentException

## Method: `CardType getType()`

- **TC8: Get type from infantry card** ( :x: )
    - **State of the system**: card constructed with type INFANTRY
    - **Expected output**: INFANTRY

- **TC9: Get type from wild card** ( :x: )
    - **State of the system**: card constructed with type WILD
    - **Expected output**: WILD

## Method: `TerritoryName getTerritory()`

- **TC10: Get first territory from card** ( :x: )
    - **State of the system**: card constructed with territory ALASKA
    - **Expected output**: ALASKA

- **TC11: Get last territory from card** ( :x: )
    - **State of the system**: card constructed with territory INDONESIA
    - **Expected output**: INDONESIA

- **TC12: Get territory from wild card** ( :x: )
    - **State of the system**: wild card constructed with no territory
    - **Expected output**: null

## Method: `boolean isWild()`

- **TC13: Check non-wild card** ( :x: )
    - **State of the system**: card constructed with type INFANTRY
    - **Expected output**: false

- **TC14: Check wild card** ( :x: )
    - **State of the system**: card constructed with type WILD
    - **Expected output**: true

## Method: `boolean matchesTerritory(TerritoryName territory)`

- **TC15: Card matches same territory** ( :x: )
    - **State of the system**:
        - card territory: ALASKA
        - territory: ALASKA
    - **Expected output**: true

- **TC16: Card does not match different territory** ( :x: )
    - **State of the system**:
        - card territory: ALASKA
        - territory: ALBERTA
    - **Expected output**: false

- **TC17: Wild card does not match territory** ( :x: )
    - **State of the system**:
        - card is WILD
        - territory: ALASKA
    - **Expected output**: false

- **TC18: Card matching null territory** ( :x: )
    - **State of the system**:
        - card territory: ALASKA
        - territory: null
    - **Expected output**: false
