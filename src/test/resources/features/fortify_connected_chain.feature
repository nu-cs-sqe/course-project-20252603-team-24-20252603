Feature: F3 Fortify connected chain
  Players may fortify between owned territories connected by an owned chain.

  Scenario: Fortifying through an owned chain succeeds
    Given a Risk game with three players
    And RED is current player in FORTIFY phase
    And RED owns ALASKA with 4 armies
    And RED owns NORTHWEST_TERRITORY with 1 army
    And RED owns ONTARIO with 1 army
    When RED fortifies 2 armies from ALASKA to ONTARIO
    Then ALASKA has 2 armies
    And ONTARIO has 3 armies

  Scenario: Fortifying through a broken chain is rejected
    Given a Risk game with three players
    And RED is current player in FORTIFY phase
    And RED owns ALASKA with 4 armies
    And BLUE owns NORTHWEST_TERRITORY with 1 army
    And RED owns ONTARIO with 1 army
    When RED fortifies 1 army from ALASKA to ONTARIO
    Then the action fails with IllegalArgumentException
