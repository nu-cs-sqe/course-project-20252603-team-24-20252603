# Integration Test Features

## F1: Drafting and Card Trade-Ins
- Players receive draft armies from territory count and continent ownership.
- Players may trade valid card sets during drafting.
- Players with 5 or more cards must trade before drafting.
- Card trades add the trade bonus and 2 armies to each owned matching territory.

## F2: Batch Attack and Capture Movement
- A player chooses only the attacking and defending territories.
- The backend resolves dice rounds until the defender reaches 0 armies or the
  attacker reaches 1 army.
- Capturing a territory creates pending capture movement.
- Capture movement enforces the required minimum and maximum army movement.

## F3: Fortify Connected Chain
- A player may fortify through a connected chain of owned territories.
- A broken owned chain prevents fortification.
- Only one fortification is allowed per turn.

## F4: Card Award and Turn Progression
- A player earns one card after one or more captures during a turn.
- A player earns no card after a turn with no capture.
- End turn advances to the next active player.

## F5: Player Elimination and Win Condition
- Capturing a defeated player's final territory transfers that player's cards.
- Eliminated players are skipped in turn order.
- Capturing all territories transitions the game to GAME_OVER.
