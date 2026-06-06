Feature: F2 Batch attack and capture movement
  A player chooses source and target territories while the backend resolves the full attack.

  Scenario: Batch attack captures a territory and moves the minimum armies
    Given a Risk game with three players
    And RED is current player in ATTACK phase
    And RED owns ALASKA with 4 armies
    And BLUE owns ALBERTA with 1 army
    And RED has completed drafting
    And dice rolls favor the attacker
    When RED attacks from ALASKA to ALBERTA
    And RED moves 3 armies from ALASKA to ALBERTA after capture
    Then ALASKA has 1 army
    And ALBERTA has 3 armies
    And ALBERTA is owned by RED

  Scenario: Batch attack stops when the attacker has one army left
    Given a Risk game with three players
    And RED is current player in ATTACK phase
    And RED owns ALASKA with 3 armies
    And BLUE owns ALBERTA with 2 armies
    And RED has completed drafting
    And dice rolls favor the defender
    When RED attacks from ALASKA to ALBERTA
    Then ALASKA has 1 army
    And ALBERTA has 2 armies
    And ALBERTA is owned by BLUE
