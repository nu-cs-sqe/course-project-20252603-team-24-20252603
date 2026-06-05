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

## Method: `int getCardCount()`

- **TC17: Get card count when player has no cards** ( :x: )
    - **State of the system**: player has 0 cards
    - **Expected output**: 0

- **TC18: Get card count when player has 1 card** ( :x: )
    - **State of the system**: player has 1 card
    - **Expected output**: 1

- **TC19: Get card count when player has more than 1 card** ( :x: )
    - **State of the system**: player has 3 cards
    - **Expected output**: 3

## Method: `List<Card> getCards()`

- **TC20: Get cards when player has no cards** ( :x: )
    - **State of the system**: player has 0 cards
    - **Expected output**: empty list

- **TC21: Get cards when player has 1 card** ( :x: )
    - **State of the system**: player has ALASKA card
    - **Expected output**: list containing ALASKA card

- **TC22: Get cards when player has more than 1 card** ( :x: )
    - **State of the system**: player has ALASKA, ALBERTA, and BRAZIL cards
    - **Expected output**: list containing ALASKA, ALBERTA, and BRAZIL cards

## Method: `void addCard(Card card)`

- **TC23: Add 1 card when player has no cards** ( :x: )
    - **State of the system**:
        - player has 0 cards
        - card: ALASKA infantry card
    - **Expected output**: player has 1 card

- **TC24: Add 1 card when player has 1 card** ( :x: )
    - **State of the system**:
        - player has 1 card
        - card: ALBERTA cavalry card
    - **Expected output**: player has 2 cards

- **TC25: Add 1 card when player has more than 1 card** ( :x: )
    - **State of the system**:
        - player has 3 cards
        - card: BRAZIL artillery card
    - **Expected output**: player has 4 cards

- **TC26: Add null card** ( :x: )
    - **State of the system**:
        - player has any number of cards
        - card: null
    - **Expected output**: throw IllegalArgumentException

## Method: `boolean hasCards(List<Card> cards)`

- **TC27: Check player has empty card list** ( :x: )
    - **State of the system**:
        - player has 1 card
        - cards is empty
    - **Expected output**: true

- **TC28: Check player has exactly 1 requested card** ( :x: )
    - **State of the system**:
        - player has ALASKA card
        - cards contains ALASKA card
    - **Expected output**: true

- **TC29: Check player has more than 1 requested card** ( :x: )
    - **State of the system**:
        - player has ALASKA, ALBERTA, and BRAZIL cards
        - cards contains ALASKA, ALBERTA, and BRAZIL cards
    - **Expected output**: true

- **TC30: Check player missing one requested card** ( :x: )
    - **State of the system**:
        - player has ALASKA and ALBERTA cards
        - cards contains ALASKA, ALBERTA, and BRAZIL cards
    - **Expected output**: false

- **TC31: Check player has duplicate requested card only once** ( :x: )
    - **State of the system**:
        - player has 1 ALASKA card
        - cards contains the same ALASKA card twice
    - **Expected output**: false

- **TC32: Check null card list** ( :x: )
    - **State of the system**:
        - player has any number of cards
        - cards is null
    - **Expected output**: throw IllegalArgumentException

## Method: `void removeCards(List<Card> cards)`

- **TC33: Remove 1 card when player has 1 card** ( :x: )
    - **State of the system**:
        - player has ALASKA card
        - cards contains ALASKA card
    - **Expected output**: player has 0 cards

- **TC34: Remove 1 card when player has more than 1 card** ( :x: )
    - **State of the system**:
        - player has ALASKA, ALBERTA, and BRAZIL cards
        - cards contains ALASKA card
    - **Expected output**: player has ALBERTA and BRAZIL cards

- **TC35: Remove more than 1 card when player has more than 1 card** ( :x: )
    - **State of the system**:
        - player has ALASKA, ALBERTA, and BRAZIL cards
        - cards contains ALASKA, ALBERTA, and BRAZIL cards
    - **Expected output**: player has 0 cards

- **TC36: Remove zero cards** ( :x: )
    - **State of the system**:
        - player has ALASKA card
        - cards is empty
    - **Expected output**: player still has ALASKA card

- **TC37: Remove card player does not own** ( :x: )
    - **State of the system**:
        - player has ALASKA card
        - cards contains BRAZIL card
    - **Expected output**: throw IllegalArgumentException

- **TC38: Remove null card list** ( :x: )
    - **State of the system**:
        - player has any number of cards
        - cards is null
    - **Expected output**: throw IllegalArgumentException
