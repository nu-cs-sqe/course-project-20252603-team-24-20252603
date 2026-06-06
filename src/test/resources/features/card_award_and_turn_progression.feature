Feature: F4 Card award and turn progression
  End turn awards cards for successful captures and advances to the next active player.

  Scenario: One capture awards one card at end of turn
    Given a Risk game with three players
    And RED is current player in ATTACK phase
    And RED owns ALASKA with 3 armies
    And BLUE owns ALBERTA with 1 army
    And BLUE owns BRAZIL with 1 army
    And RED has completed drafting
    And dice rolls favor the attacker
    When RED completes an attack turn from ALASKA to ALBERTA
    Then RED has 1 card
    And the current player is BLUE

  Scenario: Multiple captures award one card at end of turn
    Given a Risk game with three players
    And RED is current player in ATTACK phase
    And RED owns ALASKA with 3 armies
    And BLUE owns ALBERTA with 1 army
    And BLUE owns NORTHWEST_TERRITORY with 1 army
    And BLUE owns BRAZIL with 1 army
    And RED has completed drafting
    And dice rolls favor the attacker
    When RED completes an attack turn with captures from ALASKA to ALBERTA and NORTHWEST_TERRITORY
    Then RED has 1 card
    And the current player is BLUE

  Scenario: No capture awards no card at end of turn
    Given a Risk game with three players
    And RED is current player in ATTACK phase
    And RED owns ALASKA with 2 armies
    And BLUE owns ALBERTA with 1 army
    And BLUE owns BRAZIL with 1 army
    And RED has completed drafting
    And dice rolls favor the defender
    When RED completes an attack turn from ALASKA to ALBERTA
    Then RED has 0 cards
    And the current player is BLUE
