# Week 4 Project Feedback by PM/TA

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

### Software Process Quality
| #   | Item                                                                                                                                                         |  Status   | Reviewer Notes      | Source Instructions or Resources                                                  |
|-----|--------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------:|---------------------|-----------------------------------------------------------------------------------|
| 1   | GitHub repository branch protection rules are fully set up so that people cannot push into main without a pull request approval. (needed for Letter Grade C) |     ✅     | Great setup: `main` requires pull request review and the Gradle status check before merge. | Canvas assignment Project: Setup, Project grading rubrics                         |
| 2   | Continuous Integration (CI) is fully set up from the beginning. (needed for Letter Grade B)                                                                  |     ✅     | The Gradle Build workflow is configured and the recent runs are passing. | Canvas assignment Project: Setup, Project grading rubrics                         |
| 3   | The team uses the project management board steadily and frequently, and the description of each task is detailed. (needed for Letter Grade B)                | See below | See breakdown below | Week 4 Wednesday Lecture (Lecture 08), Canvas assignment Project: Week 4 Guidance |
| 3.1 | Every functionality-related work item on the management board includes a user story, and optionally one or more use cases.                                   |     ➖     | I could not find Week 4 work items on the GitHub Project board yet. Once you create setup-phase tasks, please include user stories and/or use cases in the descriptions. | Week 4 Wednesday Lecture (Lecture 08), Canvas assignment Project: Week 4 Guidance |
| 3.2 | The design is documented somewhere, either in the work item description, or in a separate design document.                                                   |     ➖     | I could not find setup-phase design documentation yet. Please add a design document under `docs/design/` or include the design in the relevant board items. | Week 4 Wednesday Lecture (Lecture 08), Canvas assignment Project: Week 4 Guidance |
| 3.3 | Task assignments are documented clearly in the management board.                                                                                             |     ➖     | I could not find task assignments on the GitHub Project board yet. Please split the setup-phase work and assign owners so the team can track progress clearly. | Week 4 Wednesday Lecture (Lecture 08), Canvas assignment Project: Week 4 Guidance |
| 4   | Each active feature branch has an open draft PR against main.                                                                                                |     ➖     | I did not find active feature branches or open PRs at the time of review. When work starts, please open draft PRs early so progress is visible. | Week 4 Wednesday Lecture (Lecture 08)                                             |
| 5   | The team has a “definition of done” (BVA) fully documented for the part of the system that is done. (needed for Letter Grade D)                              |     ➖     | I could not find setup-phase implementation merged into `main` yet, so there is no completed behavior to evaluate. Please add BVA docs as soon as the first setup task is defined. | Project grading rubrics                                                           |
| 6   | GitHub commit history demonstrates evidence of a TDD/BDD workflow for all the non-UI code. (needed for Letter Grade C)                                       |     ➖     | I could not find non-UI implementation or tests merged into `main` yet. Once coding starts, please keep tests and small TDD-style commits visible in PRs. | Project grading rubrics                                                           |

### Planning & Progress Evaluation
| # | Item                                                                                                                                                         |  Status   | Reviewer Notes      | Source Instructions or Resources                                                  |
|---|--------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------:|---------------------|-----------------------------------------------------------------------------------|
| 7 | The team documents every week’s planning and progress evaluation professionally. (needed for Letter Grade B)                                                 |     ⚠️     | I could not find a Week 4 planning/progress entry in `docs/weekly-reports/report.md` yet. Please add the Week 4 meeting/planning notes and keep this file updated weekly. | Week 4 Wednesday Lecture (Lecture 08), Project grading rubrics                    |

### Progress & Collaboration
| #  | Item                                                                                                                                                             |  Status   | Reviewer Notes      | Source Instructions or Resources                                                  |
|----|------------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------:|---------------------|-----------------------------------------------------------------------------------|
| 8  | README is updated properly, including the project name, contributors, and build status badge. In addition, the team should specify the GUI library if appliable. |     ⚠️     | The README has contributors and the build badge, but the project name is still the placeholder `PROJECT NAME`. Please update it. | Canvas assignment Project: Setup                                                  |
| 9  | Overall progress on "Game Setup Phase"                                                                                                                           |     ⚠️     | I do not see visible Week 4 setup-phase work yet. A good next step is to create the Game Setup Phase user story, design, BVA plan, and first draft PR. | Canvas assignment Project: Week 4 Guidance                                        |
| 10 | Collaboration: Quality of discussion in PR reviews and work item comments on the board.                                                                          |     ➖     | I did not find open/merged PRs or project-board comments for Week 4 work yet, so there is not much collaboration activity to evaluate. |                                                                                   |

## Additional Comments
The repository infrastructure is in good shape. That foundation will help once the team starts the setup-phase work.

For the next step, I would suggest that you focus on making Week 4 progress visible: update the README project name, add Week 4 planning notes, create GitHub Project board items for the Game Setup Phase, document the design/BVA plan, and open draft PRs for active work. Even if the implementation is still early, having these artifacts visible will make collaboration and review much smoother.

## Review Snapshot (Just used for tracking purposes, not for feedback)
- Reviewed latest `main` commit: `feae603c1aa935fbe18e36e098ace62d4afc55e1`
- Commit summary: Update contributors list in README.md
- Review date: 2026-04-28
