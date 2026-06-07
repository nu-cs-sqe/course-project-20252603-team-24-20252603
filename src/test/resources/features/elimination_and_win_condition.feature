Feature: F5 Player elimination and win condition
  Capturing a final territory transfers cards and can end the game.

  Scenario: Capturing a defeated player's final territory transfers cards
    Given a Risk game with three players
    And RED is current player in ATTACK phase
    And RED owns ALASKA with 3 armies
    And BLUE owns ALBERTA with 1 army
    And BLUE has the cards CHINA and INDIA
    And RED has completed drafting
    And dice rolls favor the attacker
    When RED attacks from ALASKA to ALBERTA
    Then BLUE has 0 cards
    And RED has 2 cards

  Scenario: Capturing all territories ends the game
    Given a Risk game with three players
    And RED is current player in ATTACK phase
    And RED owns every territory except ALBERTA
    And BLUE owns ALBERTA with 1 army
    And RED has completed drafting
    And dice rolls favor the attacker
    When RED attacks from ALASKA to ALBERTA
    Then the game phase is GAME_OVER
    And the winner is RED
