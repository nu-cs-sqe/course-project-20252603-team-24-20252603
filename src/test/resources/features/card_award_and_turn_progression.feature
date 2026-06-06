Feature: F4 Card award and turn progression
  End turn awards cards for successful captures and advances to the next active player.

  Scenario: Player completes a full draft attack capture movement fortify turn
    Given a Risk game with three players
    And RED is current player in ATTACK phase
    And RED owns ALASKA with 3 armies
    And RED owns ONTARIO with 1 army
    And RED owns QUEBEC with 2 armies
    And BLUE owns ALBERTA with 1 army
    And BLUE owns BRAZIL with 1 army
    And dice rolls favor the attacker
    When RED completes a full turn by drafting on ALASKA attacking ALBERTA moving 3 armies and fortifying from QUEBEC to ONTARIO
    Then ALBERTA is owned by RED
    And ALASKA has 3 armies
    And ALBERTA has 3 armies
    And QUEBEC has 1 army
    And ONTARIO has 2 armies
    And RED has 1 card
    And the current player is BLUE

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
