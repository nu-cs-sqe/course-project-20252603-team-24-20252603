Source: https://www.hasbro.com/common/instruct/risk.pdf

## Scope
- World Domination Risk (Regular game play): :white_check_mark:
- Risk for 2 players: :x:
- Secret Mission Risk: :x:
- Capital Risk: :x:

## Player Count
- This implementation supports 3 to 6 players

## Key Rule References
The following sections of the rulebook are the source for our game rules

| Rulebook section      | Page | Used for                                                     | 
|-----------------------|------|--------------------------------------------------------------|
| Equipment             | 3    | Board structure, card counts                                 | 
| World Domination Risk | 5    | Player count, turn order, territory claiming, army placement |
| Risk Cards            | 7    | Card composition                                             |

## Summary of Our Setup
- Starting infantry depends on player count
  - 3 players -> 35
  - 4 players -> 30
  - 5 players -> 25
  - 6 players -> 20
- Play order is determined by a single die roll (highest goes first)
- After territories are claimed, players take turns to place one army at a time onto territories they already occupy, until all armies are placed
- The player who placed the first army takes the first turn

## User Story:
As a group of 3 to 6 players of Risk, I want the game to be properly initialized (players registered, territories distributed, initial armies placed),
so that we can begin the first turn ready to play with all setups correctly implemented.

### Acceptance Criteria:
1. :white_check_mark: The game must not start unless there are 3 to 6 players
2. :white_check_mark: Each player must select a unique color from the available color set
3. :white_check_mark: Each player must receive the correct starting Infantry count based on player count as mentioned above
4. :white_check_mark: Each player rolls one die and the highest roller is designated first player.
5. :white_check_mark: Subsequent turn order proceeds in descending order of the die number
6. :white_check_mark: All territories must be distributed randomly among players
7. :white_check_mark: After territory distribution, players take turns placing exactly 1 infantry onto any territory they already occupy
8. :white_check_mark: A player cannot place an army on a territory they do not own
9. :white_check_mark: Once all initial armies are placed, the game setup phase is complete and the card deck is shuffled

## Use Case 1: Start New Game
- Actor: Player
- Preconditions:
  - The game application is launched
- Main Flow:
  1. Player clicks "Start New Game"
  2. System asks for the total number of player
  3. The player enters the number of players
  4. System asks each player for a name and color selection
  5. Each player enters a name and chooses a color
  6. System allocates the correct number of starting Infantry per player
  7. Each player rolls a die to determine turn order
  8. System determines the order starting from the highest roller
  9. System randomly and evenly assigns territories to players
  10. Players manually place infantries onto their own territories (see Use Case 2)
  11. System shuffles the deck
  12. System initializes the game and sets the initial turn order
- Alternate Flows:
  - 3a. System indicates that the number of players the user enters is invalid
  - 8a. If two or more players tie for highest roll, System has only the tied players re-roll
- Postconditions:
  - All territories are claimed, each by exactly one player
  - Every player has placed all their starting infantry
  - The card deck is shuffled and ready
  - Turn order is set and the first player is active
  - The game is ready for the first turn

## Use Case 2: Manual Initial Troop Placement
- Actor: Player
- PreconditionAll
  - All territories are distributed 
  - Players have unplaced Infantry remaining
- Main Flow:
  1. System highlights 'current player' and shows their remaining Infantry count
  2. System prompts the current player to select one of their own territories
  3. Player selects a territory
  4. System places 1 Infantry on the selected territory and decrements the player's remaining Infantry by 1
  5. System advances to the next player
  6. Repeat steps 1-5 until all players have 0 unplaced Infantry.
- Alternate Flows:
  - 3a. Player attempting to place on a territory owned by another player is rejected
  - 5a. If current player has 0 remaining Infantry but other players still have some, System skips this player
- Postconditions:
  - All players have placed all their Infantry
  - System is ready to initialize the card deck (Use Case 1 Step 11)