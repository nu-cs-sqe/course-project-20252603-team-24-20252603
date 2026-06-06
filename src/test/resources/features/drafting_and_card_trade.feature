Feature: F1 Drafting and card trade-ins
  Players trade card sets during drafting to receive draft armies and territory bonuses.

  Scenario: Trading owned territory cards adds draft armies and territory bonuses
    Given a Risk game with three players
    And RED is current player in ATTACK phase
    And RED owns ALASKA with 3 armies
    And RED owns ALBERTA with 1 army
    And RED owns BRAZIL with 1 army
    And RED has the cards ALASKA, ALBERTA, and BRAZIL
    When RED trades the cards ALASKA, ALBERTA, and BRAZIL
    Then RED has 0 cards
    And the deck discard pile has 3 cards
    And RED has 7 draft armies
    And ALASKA has 5 armies
    And ALBERTA has 3 armies
    And BRAZIL has 3 armies

  Scenario: Player with five cards must trade before drafting
    Given a Risk game with three players
    And RED is current player in ATTACK phase
    And RED owns ALASKA with 3 armies
    And RED has five cards including a valid set
    When RED drafts 1 army on ALASKA
    Then the action fails with IllegalStateException

  Scenario: Forced card trade allows drafting afterward
    Given a Risk game with three players
    And RED is current player in ATTACK phase
    And RED owns ALASKA with 3 armies
    And RED has five cards including a valid set
    When RED trades the cards ALASKA, ALBERTA, and BRAZIL
    And RED drafts 7 armies on ALASKA
    Then RED has 2 cards
    And ALASKA has 12 armies

  Scenario: Trading after draft completion is rejected
    Given a Risk game with three players
    And RED is current player in ATTACK phase
    And RED owns ALASKA with 3 armies
    And RED has the cards ALASKA, ALBERTA, and BRAZIL
    And RED has completed drafting
    When RED trades the cards ALASKA, ALBERTA, and BRAZIL
    Then the action fails with IllegalStateException
