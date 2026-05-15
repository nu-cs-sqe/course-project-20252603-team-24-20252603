Source: https://www.hasbro.com/common/instruct/risk.pdf

## Scope

This document covers the **First Turn of the Game** phase, which begins immediately after the
Game Setup phase (SCRAMBLE + SETUP) is complete. It encompasses two sequential phases that
the active player executes in order: **Attacking** and **Fortifying**, matching the `ATTACK`
and `FORTIFY` values of the `GamePhase` enum defined in the system design.

Out of scope for this implementation:
- Risk Cards (card earning, trade-in, and bonus armies)
- Continent bonuses
- Secret Mission Risk, 2-Player Risk, Capital Risk

This document does NOT cover the Setup phase (territory claiming and initial army placement),
which is specified in `game-rules.md`.

## Key Rule References

| Rulebook section         | Page | Used for                                                  |
|--------------------------|------|-----------------------------------------------------------|
| World Domination Risk    | 5–6  | Turn structure, reinforcement formula, attacking, fortify |
| Attacking                | 5    | Dice mechanics, capturing territories, army movement      |
| Fortifying Your Position | 6    | End-of-turn army transfer between adjacent territories    |

## Summary of the First Turn

At the start of each player's turn (beginning with the first player determined during setup),
two steps occur in this fixed order:

1. **Attacking** (`ATTACK` phase) — The active player receives reinforcement armies based on
   territories held, places them on their own territories, then may optionally attack adjacent
   enemy territories using dice. The attacker rolls up to 3 dice; the defender rolls up to 2
   dice. Combat continues until the attacker stops, captures the territory, or cannot attack.
2. **Fortifying** (`FORTIFY` phase) — Once per turn, the active player may move any number of
   armies from one owned territory to one adjacent owned territory.

After Fortify, the turn passes to the next player and the phase returns to ATTACK.

## User Story

As the first active player in a game of Risk (3–6 players), I want to receive reinforcements,
optionally attack enemy territories, and optionally fortify my position, so that I can begin
expanding my territorial control in a rules-compliant way.

### Acceptance Criteria

1. :white_check_mark: The first turn begins only after all 42 territories are claimed and all
   starting armies are placed (i.e., game phase transitions to ATTACK).
2. :white_check_mark: At the start of their ATTACK phase, the active player receives
   reinforcement armies equal to max(3, floor(territoriesOwned / 3)).
3. :white_check_mark: The active player must place all received reinforcement armies on their
   own territories before initiating any attack.
4. :white_check_mark: A player cannot place reinforcement armies on a territory they do not own.
5. :white_check_mark: The active player may attack any enemy territory adjacent to one of their
   own territories that has at least 2 armies.
6. :white_check_mark: The attacker rolls 1–3 dice (never more than armies on the attacking
   territory minus 1); the defender rolls 1–2 dice (never more than armies on the defending
   territory).
7. :white_check_mark: Dice are compared highest-to-highest (and second-highest-to-second-highest
   if both sides rolled at least 2 dice); ties go to the defender.
8. :white_check_mark: Each die comparison removes exactly 1 army from the losing side.
9. :white_check_mark: If the defender loses all armies, the attacker captures the territory and
   must immediately move at least as many armies as dice they rolled into the captured territory.
10. :white_check_mark: The active player may choose to stop attacking at any time and advance to
    the FORTIFY phase.
11. :white_check_mark: If the active player captures all 42 territories, the game phase
    transitions to GAME_OVER and that player is declared the winner.
12. :white_check_mark: During FORTIFY, the active player may move any number of armies (minimum
    1) from one owned territory to one adjacent owned territory; this action is optional and can
    only happen once per turn.
13. :white_check_mark: A territory must retain at least 1 army after any move (attack or fortify).
14. :white_check_mark: After Fortify (or if skipped), the phase returns to ATTACK and the turn
    advances to the next player in the established order.

## Use Case 3: Receive and Place Reinforcements

- Actor: Player
- Preconditions:
  - Game phase is ATTACK
  - It is the active player's turn
  - Active player has not yet placed reinforcements this turn
- Main Flow:
  1. System calculates reinforcement armies: max(3, floor(territoriesOwned / 3)).
  2. System adds the calculated armies to the active player's `armiesToPlace` count.
  3. System prompts the active player to place armies on their owned territories.
  4. Player selects a territory they own and the number of armies to place there.
  5. System places the armies on the selected territory and decrements the player's
     `armiesToPlace` accordingly.
  6. Repeat steps 4–5 until the active player's `armiesToPlace` reaches 0.
  7. Active player may now initiate attacks (Use Case 4) or skip to Fortify (Use Case 5).
- Alternate Flows:
  - 4a. Player selects a territory they do not own — System rejects the action and re-prompts.
- Postconditions:
  - Active player's `armiesToPlace` is 0.
  - All newly placed armies are on territories owned by the active player.

## Use Case 4: Attack an Enemy Territory

- Actor: Player
- Preconditions:
  - Game phase is ATTACK
  - Active player's `armiesToPlace` is 0 (all reinforcements placed)
  - Active player owns at least one territory with ≥ 2 armies adjacent to an enemy territory
- Main Flow:
  1. Player selects one of their own territories with ≥ 2 armies as the attacking territory.
  2. Player selects an adjacent enemy territory as the target.
  3. Player declares the number of attack dice (1–3, not exceeding attacking territory armies − 1).
  4. System rolls the declared number of attack dice for the attacker.
  5. System rolls defense dice for the defender (1–2, not exceeding defending territory armies).
  6. System compares dice highest-to-highest; ties go to the defender.
  7. System removes 1 army from the loser of each comparison.
  8. If the defending territory still has armies ≥ 1, return to step 3 (player may continue or stop).
  9. If the defending territory reaches 0 armies, the attacker captures it:
     a. System transfers ownership of the territory to the attacker's `PlayerColor`.
     b. System prompts the attacker to move armies into the captured territory
        (minimum = number of attack dice rolled in the final battle;
        maximum = attacking territory armies − 1).
     c. System calls `addArmies` on the captured territory and reduces armies on the
        attacking territory accordingly.
  10. Player may choose to attack again (return to step 1) or stop and proceed to Fortify.
- Alternate Flows:
  - 1a. Active player has no territory with ≥ 2 armies adjacent to an enemy territory —
    System automatically advances to FORTIFY phase (Use Case 5).
  - 3a. Player declares a number of dice outside the valid range — System rejects and re-prompts.
  - 10a. Active player now owns all 42 territories — System transitions phase to GAME_OVER
    and declares the active player the winner.
- Postconditions:
  - Each captured territory is owned by the attacker's `PlayerColor` and has ≥ 1 army.
  - Armies on the attacking territory have been reduced by the number moved into any
    captured territory.
  - The active player may proceed to Fortify.

## Use Case 5: Fortify Position

- Actor: Player
- Preconditions:
  - Game phase is FORTIFY
  - Active player has finished attacking (or chose to skip attacking)
- Main Flow:
  1. System transitions phase to FORTIFY and prompts the active player to optionally fortify.
  2. Player selects one territory they own as the source (must have ≥ 2 armies).
  3. Player selects one territory they own that is adjacent to the source as the destination.
  4. Player declares the number of armies to move (1 to sourceArmies − 1).
  5. System calls `addArmies` on the destination and reduces armies on the source accordingly.
  6. System advances the turn to the next player and transitions phase back to ATTACK.
- Alternate Flows:
  - 2a. Player chooses to skip Fortify — proceed directly to step 6.
  - 3a. Destination territory is not adjacent to source — System rejects and re-prompts.
  - 4a. Player declares 0 armies or more than sourceArmies − 1 — System rejects and re-prompts.
- Postconditions:
  - Source territory has ≥ 1 army remaining.
  - Destination territory army count has increased by the declared amount.
  - Phase is ATTACK and the next player is now active.
