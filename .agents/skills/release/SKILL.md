---
name: release
description: Cut a new chess-core release — bump the version, finalize CHANGELOG.md, tag, and create a GitHub release that triggers Maven Central publishing. Use when the user asks to release, publish, or bump the version.
---

# Release chess-core

Publishing is done by CI: `.github/workflows/publish.yml` runs `publishToMavenCentral`
when a GitHub release is created. Never run `./gradlew publish` or
`publishToMavenCentral` locally.

## Steps

1. **Preflight.** Must be on `main`, clean working tree, up to date with `origin/main`.
   Run `./gradlew allTests` and stop if anything fails.
2. **Pick the bump.** Read the current `version = "X.Y.Z"` in `chess-core/build.gradle.kts`
   and the `[Unreleased]` section of `CHANGELOG.md`. Propose major / minor / patch
   (breaking API change / new feature / fixes only) and confirm with the user.
   If `[Unreleased]` is empty, ask what the release contains before continuing.
3. **Bump the version** in `chess-core/build.gradle.kts`. Also update the version in the
   dependency snippet in `README.md`.
4. **Finalize the changelog.** Rename `## [Unreleased]` to `## [X.Y.Z] - YYYY-MM-DD`
   (today's date) and add a fresh empty `## [Unreleased]` above it.
5. **Verify** with `./gradlew publishToMavenLocal` (signing is skipped when no key is set).
6. **Commit and tag** (confirm with the user before pushing):
   ```bash
   git add chess-core/build.gradle.kts CHANGELOG.md README.md
   git commit -m "Release X.Y.Z"
   git tag -a vX.Y.Z -m "Release X.Y.Z"
   git push origin main vX.Y.Z
   ```
7. **Create the GitHub release**, using the changelog section as notes:
   ```bash
   gh release create vX.Y.Z --title "X.Y.Z" --notes "<changelog section for X.Y.Z>"
   ```
   Add `--prerelease` for `-alpha`/`-beta`/`-rc` versions.
8. **Watch the publish run** with `gh run watch` and report the outcome. The artifact
   `xyz.daaren:chess-core:X.Y.Z` can take a while to appear on Maven Central.
