# BVA Analysis for Deck
## Method: `Deck()`

- **TC1: Construct deck with all Risk cards** ( :white_check_mark: )
    - **State of the system**: new deck constructed
    - **Expected output**:
        - getDrawPileSize() == 44
        - getDiscardPileSize() == 0

- **TC2: Construct deck with one card for every territory** ( :white_check_mark: )
    - **State of the system**: new deck constructed
    - **Expected output**: deck contains 42 territory cards

- **TC3: Construct deck with two wild cards** ( :white_check_mark: )
    - **State of the system**: new deck constructed
    - **Expected output**: deck contains 2 wild cards

## Method: `int getDrawPileSize()`

- **TC4: Get draw pile size from full deck** ( :white_check_mark: )
    - **State of the system**: deck has 44 cards in draw pile
    - **Expected output**: 44

- **TC5: Get draw pile size after drawing one card** ( :white_check_mark: )
    - **State of the system**: deck has 43 cards in draw pile
    - **Expected output**: 43

- **TC6: Get draw pile size from empty draw pile** ( :white_check_mark: )
    - **State of the system**: deck has 0 cards in draw pile
    - **Expected output**: 0

## Method: `int getDiscardPileSize()`

- **TC7: Get discard pile size from empty discard pile** ( :white_check_mark: )
    - **State of the system**: deck has 0 cards in discard pile
    - **Expected output**: 0

- **TC8: Get discard pile size with one discarded card** ( :white_check_mark: )
    - **State of the system**: deck has 1 card in discard pile
    - **Expected output**: 1

- **TC9: Get discard pile size with more than one discarded card** ( :white_check_mark: )
    - **State of the system**: deck has 3 cards in discard pile
    - **Expected output**: 3

## Method: `Card draw()`

- **TC10: Draw one card from full deck** ( :white_check_mark: )
    - **State of the system**: deck has 44 cards in draw pile
    - **Expected output**:
        - returns one Card
        - getDrawPileSize() == 43

- **TC11: Draw last card from draw pile** ( :white_check_mark: )
    - **State of the system**: deck has exactly 1 card in draw pile
    - **Expected output**:
        - returns one Card
        - getDrawPileSize() == 0

- **TC12: Draw from empty draw pile with one discarded card** ( :white_check_mark: )
    - **State of the system**:
        - draw pile has 0 cards
        - discard pile has 1 card
    - **Expected output**:
        - discarded card moves back to draw pile
        - returns one Card
        - getDrawPileSize() == 0
        - getDiscardPileSize() == 0

- **TC13: Draw from empty draw pile with more than one discarded card** ( :white_check_mark: )
    - **State of the system**:
        - draw pile has 0 cards
        - discard pile has 3 cards
    - **Expected output**:
        - discarded cards move back to draw pile
        - returns one Card
        - getDrawPileSize() == 2
        - getDiscardPileSize() == 0

- **TC14: Draw from empty draw pile with empty discard pile** ( :white_check_mark: )
    - **State of the system**:
        - draw pile has 0 cards
        - discard pile has 0 cards
    - **Expected output**: throw IllegalStateException

## Method: `void discard(List<Card> cards)`

- **TC15: Discard one card into empty discard pile** ( :white_check_mark: )
    - **State of the system**:
        - discard pile has 0 cards
        - cards has 1 card
    - **Expected output**: getDiscardPileSize() == 1

- **TC16: Discard more than one card into empty discard pile** ( :white_check_mark: )
    - **State of the system**:
        - discard pile has 0 cards
        - cards has 3 cards
    - **Expected output**: getDiscardPileSize() == 3

- **TC17: Discard one card into non-empty discard pile** ( :white_check_mark: )
    - **State of the system**:
        - discard pile has 1 card
        - cards has 1 card
    - **Expected output**: getDiscardPileSize() == 2

- **TC18: Discard zero cards** ( :white_check_mark: )
    - **State of the system**:
        - discard pile has 1 card
        - cards is empty
    - **Expected output**: throw IllegalArgumentException

- **TC19: Discard null cards list** ( :white_check_mark: )
    - **State of the system**:
        - discard pile has any number of cards
        - cards is null
    - **Expected output**: throw IllegalArgumentException

- **TC20: Discard list containing null card** ( :white_check_mark: )
    - **State of the system**:
        - discard pile has any number of cards
        - cards contains null
    - **Expected output**: throw IllegalArgumentException

- **TC21: Construct deck with fourteen infantry territory cards** ( :white_check_mark: )
    - **State of the system**: new deck constructed
    - **Expected output**: deck contains 14 non-wild cards with type INFANTRY

- **TC22: Construct deck with fourteen cavalry territory cards** ( :white_check_mark: )
    - **State of the system**: new deck constructed
    - **Expected output**: deck contains 14 non-wild cards with type CAVALRY

- **TC23: Construct deck with fourteen artillery territory cards** ( :white_check_mark: )
    - **State of the system**: new deck constructed
    - **Expected output**: deck contains 14 non-wild cards with type ARTILLERY

- **TC24: Construct deck has balanced territory card type distribution** ( :white_check_mark: )
    - **State of the system**: new deck constructed
    - **Expected output**:
        - deck contains 14 INFANTRY territory cards
        - deck contains 14 CAVALRY territory cards
        - deck contains 14 ARTILLERY territory cards
        - deck contains 2 WILD cards

- **TC25: Construct deck assigns territory card types at index boundaries** ( :white_check_mark: )
    - **State of the system**: new deck constructed
    - **Expected output**:
        - first 14 territory cards are INFANTRY
        - next 14 territory cards are CAVALRY
        - final 14 territory cards are ARTILLERY

- **TC26: Drawn territory card is no longer contained in draw pile** ( :white_check_mark: )
    - **State of the system**:
        - new deck constructed
        - draw one territory card from the deck
    - **Expected output**: containsTerritoryCard(drawn territory) == false

- **TC27: Draw from empty draw pile shuffles discard pile back into draw pile** ( :white_check_mark: )
    - **State of the system**:
        - draw pile has 0 cards
        - discard pile has more than 1 card
    - **Expected output**:
        - discard pile becomes empty
        - one card is returned
        - remaining discarded cards are available in draw pile
