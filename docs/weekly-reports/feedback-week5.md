# Week 5 Project Feedback by PM/TA

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
| 0 | The team has closed and merged the past Feedback PR(s), indicating that they have read the feedback. |   ⚠️   | Week 4 feedback PR #1 was merged, and the README project name / weekly report were updated. However, the main Week 4 action items are still largely unresolved: I still do not see setup-phase design documentation, BVA, draft implementation PRs, or completed setup work on `main`. | |

### Software Process Quality
| # | Item                                                                                                                                                         |  Status   | Reviewer Notes      | Source Instructions or Resources                                                  |
|---|--------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------:|---------------------|-----------------------------------------------------------------------------------|
| 1 | Each active feature branch has an open draft PR against main.                                                                                                |     ❌     | The Week 5 report lists design/BVA/TDD/setup implementation as in progress, but I do not see any open draft PRs or active feature branches for that work. Please open draft PRs as soon as work starts so teammates can track and review progress. | Week 4 Wednesday Lecture (Lecture 08)                                             |
| 2 | The team has a "definition of done" (BVA) fully documented for the part of the system that is done. (needed for Letter Grade D)                              |     ❌     | I do not see any setup-phase BVA document beyond the template README. Please create concrete BVA docs for the first setup behavior before or alongside implementation. | Project grading rubrics                                                           |
| 3 | GitHub commit history demonstrates evidence of a TDD/BDD workflow for all the non-UI code. (needed for Letter Grade C)                                       |     ➖     | There is no non-UI implementation or test code merged into `main` yet, so there is no TDD history to evaluate. | Project grading rubrics                                                           |

### Planning & Progress Evaluation
| # | Item                                                                                                                                                         |  Status   | Reviewer Notes      | Source Instructions or Resources                                                  |
|---|--------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------:|---------------------|-----------------------------------------------------------------------------------|
| 4 | The team documents every week's planning and progress evaluation professionally. (needed for Letter Grade B)                                                 |     ⚠️     | The weekly report now includes Week 3-5 entries, which is an improvement. Please add links to issues/PRs and keep statuses aligned with visible repo activity; right now several Week 5 items are marked in progress but have no corresponding branch, PR, files, or comments. | Week 4 Wednesday Lecture (Lecture 08), Project grading rubrics                    |

### Progress & Collaboration
| # | Item                                                                                                                                                                                   |  Status   | Reviewer Notes      | Source Instructions or Resources                 |
|---|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------:|---------------------|--------------------------------------------------|
| 5 | Overall development progress (recall the recommended order is: Game Setup Phase -> One turn of the game -> Multiple turns -> One win condition -> Other win conditions (if applicable) |     ❌     | I still do not see visible Game Setup Phase progress on `main`: no setup requirements/design beyond placeholders, no BVA, no tests, and no implementation. This should be the team's highest priority next week. | Canvas assignment Project: Week 4 and 5 Guidance |
| 6 | Collaboration: Quality of discussion in PR reviews and work item comments on the board.                                                                                                |     ⚠️     | There is some collaboration evidence: Justin/hmin approved PRs, and Prashant opened issues #2 and #3. However, the issue descriptions are empty, PR review comments are minimal, and the visible repository work is heavily concentrated in Jonathan's commits. Please make each teammate's planning, implementation, review, or documentation contributions visible in issues and PRs. | |

### The following items are not checked by the reviewer as they were checked in the previous weeks
But if your team wants the reviewer to check any of these for any reasons, please contact them or the instructor via either email or tagging them in the feedback PR.

| #   | Item                                                                                                                                                         |  Status   | Reviewer Notes      | Source Instructions or Resources                                                  |
|-----|--------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------:|---------------------|-----------------------------------------------------------------------------------|
| 1   | GitHub repository branch protection rules are fully set up so that people cannot push into main without a pull request approval. (needed for Letter Grade C) |     ➖     | Not rechecked this week. | Canvas assignment Project: Setup, Project grading rubrics                         |
| 2   | Continuous Integration (CI) is fully set up from the beginning. (needed for Letter Grade B)                                                                  |     ➖     | Not rechecked this week. | Canvas assignment Project: Setup, Project grading rubrics                         |
| 3   | The team uses the project management board steadily and frequently, and the description of each task is detailed. (needed for Letter Grade B)                | See below | See breakdown below | Week 4 Wednesday Lecture (Lecture 08), Canvas assignment Project: Week 4 Guidance |
| 3.1 | Every functionality-related work item on the management board includes a user story, and optionally one or more use cases.                                   |     ⚠️     | I see issues #2 and #3, but their descriptions are empty. Please add user stories, acceptance criteria, and/or use cases to each functionality-related work item. | Week 4 Wednesday Lecture (Lecture 08), Canvas assignment Project: Week 4 Guidance |
| 3.2 | The design is documented somewhere, either in the work item description, or in a separate design document.                                                   |     ⚠️     | I still do not see a setup-phase design document under `docs/design/`, and issue #2 does not yet contain design details. | Week 4 Wednesday Lecture (Lecture 08), Canvas assignment Project: Week 4 Guidance |
| 3.3 | Task assignments are documented clearly in the management board.                                                                                             |     ⚠️     | Issues #2 and #3 have assignees, which is a start. Please split the setup-phase work into more concrete tasks with owners, expected outputs, and current status. | Week 4 Wednesday Lecture (Lecture 08), Canvas assignment Project: Week 4 Guidance |

## Additional Comments
The most important concern this week is visibility of real progress. The repository shows that Jonathan has been carrying most of the visible commits so far, while the other team members' work is mostly not visible beyond a couple of approvals/issues. Please rebalance the workload and make contributions visible through branches, draft PRs, issue updates, BVA docs, tests, and review comments. Even a small first setup slice with BVA + tests + minimal implementation would be much better than another week of only planning notes.

## Review Snapshot (Just used for tracking purposes, not for feedback)
- Reviewed latest `main` commit: `75b0f7a`
- Commit summary: Merge pull request #5 from `nu-cs-sqe/jonathanfangg-patch-2`
- Review date: 2026-05-05
- Verification: `bash ./gradlew test` passed with Java 11 on `main`; there are currently no test sources on `main`
