# AGENTS.md

Kotlin Multiplatform chess library: rules, legal move generation, FEN/PGN/SAN/UCI,
game state. Published as `xyz.daaren:chess-core`. No runtime dependencies.

## Targets

JVM, Android, iOS (x64, arm64, simulatorArm64), wasmJs (browser + Node), Linux (x64, arm64).
iOS targets only compile and test on macOS hosts.

## Layout

Single module `chess-core/`, package `xyz.daaren.chess.core`:

- `src/commonMain/kotlin/` — `board`, `game`, `move` (incl. `move/piece`), `piece`, `utils` (FEN/PGN/SAN), `exception`
- `src/commonTest/kotlin/` — tests that run on every target (put new tests here)
- `src/jvmTest/kotlin/` — JVM-only tests, e.g. `move/PerftTest.kt`
- `dokka/` — module/package docs (`dokka.md`) and HTML assets for Dokka

Versions live in `gradle/libs.versions.toml`; the library version is set in
`chess-core/build.gradle.kts`.

## Commands

```bash
./gradlew jvmTest               # fastest feedback loop
./gradlew allTests              # tests on all targets this host supports
./gradlew assemble              # build all targets
./gradlew publishToMavenLocal   # local publish check (signing skipped without a key)
./gradlew ktlintCheck           # lint (ktlint_official style, see .editorconfig)
./gradlew ktlintFormat          # auto-fix lint violations
./gradlew dokkaGenerate         # API docs → build/dokka (fails on warnings)
```

Note: `./gradlew test` only runs the Android unit tests — use `allTests` for full coverage.

## Conventions

- Test-first: add a failing test, then the fix. Move-generation changes must keep `PerftTest` passing.
- Every behavioural change gets a line under `## [Unreleased]` in `CHANGELOG.md` (Keep a Changelog format).
- CI (`.github/workflows/gradle.yml`) runs tests per target on push/PR to `main`.

## Releasing

Use the `release` skill (`.agents/skills/release/SKILL.md`). Publishing to Maven Central happens
in CI when a GitHub release is created — never publish locally.
