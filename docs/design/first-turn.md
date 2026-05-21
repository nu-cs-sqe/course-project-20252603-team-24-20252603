# First Turn Design

## enum

### `GamePhase` (updated descriptions)

The `ATTACK` and `FORTIFY` values already exist in the enum. Their roles are now fully defined:

- `ATTACK`: The active player receives and places reinforcements, then may optionally attack
  adjacent enemy territories. Transitions to `FORTIFY` when the player ends their attacks,
  or to `GAME_OVER` if the active player owns all 42 territories.
- `FORTIFY`: The active player may move armies between two adjacent owned territories exactly
  once. Transitions back to `ATTACK` after the move (or skip), and advances the turn to the
  next player.
- `GAME_OVER`: One player owns all 42 territories. No further actions are permitted.

---

## Classes

### `Territory` (package-private) — no changes required

All existing methods are sufficient for the first turn phase. `addArmies` and `isOwnedBy` are
used directly by the new `RiskGame` methods below.

### `WorldMap` (package-private) — no changes required

All existing methods are sufficient. `areNeighbors`, `getArmies`, `addArmies`, `isOwnedBy`,
and `countTerritoriesOwnedBy` are used directly by the new `RiskGame` methods below.

### `Player` (package-private) — new method added

| Method | Description |
|--------|-------------|
| `void increaseArmiesToPlace(int count)` | Adds `count` to `armiesToPlace`. Throws `IllegalArgumentException` if `count < 1`. Used when granting reinforcements at the start of the ATTACK phase. |

### `RiskGame` (public) — new constants and methods added

#### New Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `MIN_ATTACK_DICE = 1` | 1 | Minimum dice an attacker may roll |
| `MAX_ATTACK_DICE = 3` | 3 | Maximum dice an attacker may roll |
| `MAX_DEFEND_DICE = 2` | 2 | Maximum dice a defender may roll |
| `MIN_ARMIES_TO_ATTACK = 2` | 2 | Minimum armies a territory must have for its owner to attack from it |

#### New Methods

| Method | Description |
|--------|-------------|
| `void startTurn()` | Called at the beginning of each ATTACK phase turn. Calculates reinforcements as `Math.max(3, map.countTerritoriesOwnedBy(currentPlayer) / 3)`, then calls `player.increaseArmiesToPlace(count)`. Throws `IllegalStateException` if phase is not ATTACK or if `armiesToPlace > 0` (reinforcements already granted this turn). |
| `void placeReinforcement(TerritoryName territory, int count)` | Active player places `count` reinforcement armies onto a territory they own. Calls `map.addArmies` and `player.decreaseArmiesToPlace`. Throws `IllegalStateException` if phase is not ATTACK. Throws `IllegalArgumentException` if territory not owned by current player, `count < 1`, or `count > armiesToPlace`. |
| `int[] rollAttack(TerritoryName attacker, TerritoryName defender, int numAttackDice)` | Rolls dice for one round of combat. Validates that phase is ATTACK, `armiesToPlace == 0`, territories are neighbors, attacker is owned by current player, defender is owned by a different player, attacker has ≥ 2 armies, and `numAttackDice` is between 1 and `min(MAX_ATTACK_DICE, attacker.armies − 1)`. Rolls `numAttackDice` dice for attacker and `min(MAX_DEFEND_DICE, defender.armies)` dice for defender. Resolves each highest-to-highest comparison (ties go to defender), removing 1 army per loss via a new private helper `removeArmy`. Returns `int[2]` where `[0]` = armies lost by attacker, `[1]` = armies lost by defender. If defender reaches 0 armies, does NOT yet transfer ownership — caller must invoke `captureTerritory`. Throws `IllegalStateException` if wrong phase or `armiesToPlace > 0`. Throws `IllegalArgumentException` for any invalid territory or dice count. |
| `void captureTerritory(TerritoryName attacker, TerritoryName defender, int armiesToMove)` | Transfers ownership of `defender` to the current player and moves `armiesToMove` armies from `attacker` to `defender`. `armiesToMove` must be ≥ `lastAttackDiceCount` (tracked as private field) and ≤ `attacker.armies − 1`. Calls `Territory.claim` equivalent via `map` and `map.addArmies`. Throws `IllegalStateException` if defender still has armies (not yet defeated), phase is not ATTACK, or ownership transfer conditions are not met. Throws `IllegalArgumentException` if `armiesToMove` is out of range. Checks if current player now owns all 42 territories; if so, transitions phase to `GAME_OVER`. |
| `void endAttack()` | Active player voluntarily ends the ATTACK phase. Transitions phase to FORTIFY. Throws `IllegalStateException` if phase is not ATTACK or `armiesToPlace > 0`. |
| `void fortify(TerritoryName source, TerritoryName destination, int count)` | Moves `count` armies from `source` to `destination`. Both must be owned by the current player and be neighbors. `source` must retain ≥ 1 army (i.e., `count ≤ source.armies − 1`). Calls `map.addArmies(destination, count)` and removes armies from source via private helper `removeArmy`. Transitions phase to ATTACK and advances turn to next player. Throws `IllegalStateException` if phase is not FORTIFY. Throws `IllegalArgumentException` if territories are not neighbors, not both owned by current player, or `count` is out of range. |
| `void skipFortify()` | Active player skips the FORTIFY step. Transitions phase to ATTACK and advances turn to next player. Throws `IllegalStateException` if phase is not FORTIFY. |
| `boolean isGameOver()` | Returns true if phase is GAME_OVER. |
| `PlayerColor getWinner()` | Returns the `PlayerColor` of the player who owns all 42 territories. Returns `null` if game is not over. |

#### New Private Helpers (not part of the public API)

| Helper | Description |
|--------|-------------|
| `void removeArmy(TerritoryName territory, int count)` | Reduces army count on a territory by `count`. Used internally by `rollAttack`, `captureTerritory`, and `fortify`. Throws `IllegalArgumentException` if `count` would reduce armies below 0. |
| `void advanceTurn()` | Moves `currentPlayerIndex` to the next player in the ordered list. Reused from Setup phase. |
| `int[] resolveDice(int[] attackRolls, int[] defendRolls)` | Sorts both arrays descending, compares pair-by-pair, returns `int[2]` losses (attacker losses at `[0]`, defender losses at `[1]`). |

---

## Relationships (additions)

No new classes or ownership relationships are introduced. The updated interaction flow is:

- `RiskGame.startTurn()` → `WorldMap.countTerritoriesOwnedBy()` → `Player.increaseArmiesToPlace()`
- `RiskGame.placeReinforcement()` → `WorldMap.addArmies()` + `Player.decreaseArmiesToPlace()`
- `RiskGame.rollAttack()` → `WorldMap.areNeighbors()`, `WorldMap.getArmies()`, `resolveDice()`, `removeArmy()`
- `RiskGame.captureTerritory()` → `WorldMap.claim()`, `WorldMap.addArmies()`, `removeArmy()`, `WorldMap.countTerritoriesOwnedBy()` (check for GAME_OVER)
- `RiskGame.endAttack()` → phase transition: ATTACK → FORTIFY
- `RiskGame.fortify()` → `WorldMap.areNeighbors()`, `WorldMap.addArmies()`, `removeArmy()`, `advanceTurn()`, phase transition: FORTIFY → ATTACK
- `RiskGame.skipFortify()` → `advanceTurn()`, phase transition: FORTIFY → ATTACK
