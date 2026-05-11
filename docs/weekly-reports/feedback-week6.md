# Week 6 Project Feedback by PM/TA

**Dedicated PM/TA**: Jiahao Yu

## How to Read This Feedback
> [!NOTE]
> **Purpose.** This feedback focuses on your team's progress and collaboration. It is meant as guidance, not judgement.

> [!IMPORTANT]
> **Scope.** For the BVA and TDD items, the PM/TA evaluates only the `main` branch. Ongoing work in feature branches will be evaluated after it is merged. If you'd like early feedback on work in progress, please reach out to your PM/TA directly.

> [!TIP]
> **Mistakes are expected :).** As the instructor mentioned in class, early mistakes are part of the learning process. As long as your team addresses the issues after you get the feedback, your grade will not suffer from them.

## Checklist
Status:
- ✅: All done/Good job!
- ⚠️: Attention needed
- ❌: Significant issue found
- ➖: No basis to evaluate

### Past Feedback
| # | Item                                                                                                 | Status | Reviewer Notes | Source Instructions or Resources |
|---|------------------------------------------------------------------------------------------------------|:------:|----------------|----------------------------------|
| 0 | The team has closed and merged the past Feedback PR(s), indicating that they have read the feedback. |   ✅   | Week 5 feedback PR #6 has been merged into `main`. More importantly, the Week 5 concerns are now being addressed with visible BVA docs, setup-phase implementation PRs, and merged test/code work on `main`. | |

### Software Process Quality
| # | Item                                                                                                                                         | Status | Reviewer Notes | Source Instructions or Resources                                                                              |
|---|----------------------------------------------------------------------------------------------------------------------------------------------|:------:|----------------|---------------------------------------------------------------------------------------------------------------|
| 1 | Checkstyle: Checkstyle is set up or there is a to-do item planned in the Project Management board for this task. (needed for Letter Grade B) |   ✅   | This work is now clearly planned and in progress: issue #27 is closed and open PR #32 is dedicated to Checkstyle/SpotBugs setup. It is not on `main` yet, so please merge it soon. | Week 6 Monday lecture (Lecture 11); the build script and config file in the repository for Lab: Code Coverage |
| 2 | SpotBugs: SpotBugs is set up or there is a to-do item planned in the Project Management board for this task. (needed for Letter Grade B)     |   ✅   | SpotBugs is covered by the same issue/PR workflow (#27 and PR #32). As with Checkstyle, the plan is visible, but the configuration is not on `main` yet. | Week 6 Monday lecture (Lecture 11); the build script and config file in the repository for Lab: Code Coverage |

### Planning & Progress Evaluation
| # | Item                                                                                                                                                         |  Status   | Reviewer Notes      | Source Instructions or Resources                                                  |
|---|--------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------:|---------------------|-----------------------------------------------------------------------------------|
| 4 | The team documents every week's planning and progress evaluation professionally. (needed for Letter Grade B)                                                 |     ⚠️     | `main` currently stops at the Week 5 report. A Week 6/7 update exists in open PR #24, but it has not been merged into `main` yet. Please merge the updated report so the main branch stays current. | Week 4 Wednesday Lecture (Lecture 08), Project grading rubrics                    |

### Progress & Collaboration
| # | Item                                                                                                                                                                                   |  Status   | Reviewer Notes      | Source Instructions or Resources                 |
|---|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------:|---------------------|--------------------------------------------------|
| 5 | Overall development progress (recall the recommended order is: Game Setup Phase -> One turn of the game -> Multiple turns -> One win condition -> Other win conditions (if applicable) |     ⚠️     | This is a real improvement from last week. `main` now includes setup-phase BVA documents, design/rules updates, JavaFX setup UI, and merged TDD-style `Player` and `Territory` implementation with tests. However, the Game Setup Phase is still not complete on `main`: key setup pieces such as `WorldMap` / `RiskGame` and the newer GUI setup work are still in open branches/PRs. Please keep prioritizing merging the remaining setup-phase slices before moving too far ahead. | Canvas assignment Project: Week 4 and 5 Guidance |

### The following items are not checked by the reviewer this week as they were checked in the previous weeks
If your team wants the reviewer to check any of these for any reasons, please contact them or the instructor via either email or tag/mention them in the feedback PR.

| #   | Item                                                                                                                                                         |  Status   | Reviewer Notes      | Source Instructions or Resources                                                  |
|-----|--------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------:|---------------------|-----------------------------------------------------------------------------------|
| 1   | GitHub repository branch protection rules are fully set up so that people cannot push into main without a pull request approval. (needed for Letter Grade C) |     ➖     | Not rechecked this week. | Canvas assignment Project: Setup, Project grading rubrics                         |
| 2   | Continuous Integration (CI) is fully set up from the beginning. (needed for Letter Grade B)                                                                  |     ➖     | Not rechecked this week. | Canvas assignment Project: Setup, Project grading rubrics                         |
| 3   | The team uses the project management board steadily and frequently, and the description of each task is detailed. (needed for Letter Grade B)                | See below | See breakdown below | Week 4 Wednesday Lecture (Lecture 08), Canvas assignment Project: Week 4 Guidance |
| 3.1 | Every functionality-related work item on the management board includes a user story, and optionally one or more use cases.                                   |     ➖     | Not rechecked in full this week. | Week 4 Wednesday Lecture (Lecture 08), Canvas assignment Project: Week 4 Guidance |
| 3.2 | The design is documented somewhere, either in the work item description, or in a separate design document.                                                   |     ➖     | Not rechecked in full this week. | Week 4 Wednesday Lecture (Lecture 08), Canvas assignment Project: Week 4 Guidance |
| 3.3 | Task assignments are documented clearly in the management board.                                                                                             |     ➖     | Not rechecked in full this week. | Week 4 Wednesday Lecture (Lecture 08), Canvas assignment Project: Week 4 Guidance |
| 4   | Each active feature branch has an open draft PR against main.                                                                                                |     ➖     | Not rechecked this week. The current setup/game work is at least visible now through open PRs such as #33 and #34. | Week 4 Wednesday Lecture (Lecture 08)                                             |
| 5   | The team has a “definition of done” (BVA) fully documented for the part of the system that is done. (needed for Letter Grade D)                              |     ➖     | Not rechecked this week. The repository now has concrete BVA docs for setup-phase classes, which is good progress from last week. | Project grading rubrics                                                           |
| 6   | GitHub commit history demonstrates evidence of a TDD/BDD workflow for all the non-UI code. (needed for Letter Grade C)                                       |     ➖     | Not rechecked this week. The merged `Player` and `Territory` histories on `main` do show test-case-sized TDD-style commits. | Project grading rubrics                                                           |
| 7   | Collaboration: Quality of discussion in PR reviews and work item comments on the board.                                                                      |     ➖     | Not rechecked this week. |                                                                                   |

## Additional Comments
This week is a clear step forward from Week 5. The biggest improvement is that there is now visible, reviewable work in the repository. That is exactly the kind of recovery I hoped to see after last week's feedback.

The next goal is to convert this into a cleaner main-branch story. Right now the work is split across several open PRs: weekly report updates, Checkstyle/SpotBugs, JaCoCo/Pitest, world map / risk game setup, and newer GUI setup work. Please keep merging these setup-phase slices steadily so that `main` reflects one coherent completed phase. Also, collaboration is more visible than last week, but the written PR review comments are still fairly brief; if possible, add a little more substance about what was checked and why a PR is ready.

## Review Snapshot (Just used for tracking purposes, not for feedback)
- Reviewed latest `main` commit: `bb6dba5`
- Commit summary: `DecreaseArmiesToPlace_NegativeCount_ThrowsIllegalArgumentException passes`
- Review date: 2026-05-11
- Verification: `bash ./gradlew test` passed with Java 11
