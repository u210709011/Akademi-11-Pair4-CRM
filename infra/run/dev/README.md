# Dev mode

Runs infra (Postgres/Kafka/Redis/Keycloak) in containers and all 9 Spring Boot
services natively on the host, via a vendored, PowerShell-free Apache Maven
(downloaded once into `back-end\.maven\` on first run — not the project's
`mvnw.cmd`, which internally shells out to PowerShell and is blocked by Group
Policy on some machines). This is the fastest edit-and-rerun loop: no image
rebuilds, just Maven recompiling.

For fully-containerized runs (test/prod profiles) see `../test/` and `../prod/`
instead — those don't have any of the Windows-batch complexity described here.

## Commands

| Command | What it does |
|---|---|
| `start.bat` | Starts infra, then all 9 services. Each service opens its own titled console window (`CRM dev - <service>`) with Maven's live output printing directly into it. |
| `status.bat` | Live dashboard (refreshes every 5s, Ctrl+C to stop) of what's actually up. Pass `once` for a single check instead: `status.bat once`. |
| `restart.bat <service>` | Kills that one service if it's running, then relaunches it. Also works if it's *not* running yet — i.e. this is also how you start a single service on its own. Nothing else in the stack is touched. |
| `stop.bat <service>` | Stops just that one service. |
| `stop.bat` | Stops all 9 services. Infra keeps running (so the next `start.bat` is fast). |
| `stop.bat infra` | Also tears down the infra containers. |

Service names: `config-server`, `discovery-server`, `api-gateway`,
`customer-service`, `party-service`, `contact-info-service`, `order-service`,
`lookup-service`, `product-service`.

Typical loop: `start.bat` once, then edit code → `restart.bat <service>` →
check its window → repeat. `status.bat` when you need the big picture (is
Eureka up, is everything registered).

## Why each service gets its own window

Every service is launched via `wmic process call create`, not `start /B` and
not Windows Terminal tabs. Both of those were tried first and both turned out
unreliable on this project's machines in ways that are genuinely hard to
debug (silently launching nothing, `Access is denied`, a stray `Windows
cannot find '\\'` dialog — all from `start`/`wt`'s own command-line parsing,
not from anything in this repo). `wmic` spawns via WMI directly and doesn't
depend on `start`'s parsing or on any window station being available at all,
so it's the one mechanism that has actually proven reliable here. Its one
tradeoff: it always opens a visible console window for the spawned process
(no headless option the way `start /B` has) — so rather than fight that,
`_run-service.bat` leans into it and lets Maven's own output print straight
into that window, titled per-service.

`wmic`'s spawned process does **not** inherit the launching shell's
environment variables (it goes through the separate WMI provider host) — that's
why `_run-service.bat` computes its own `BACKEND`/`MVN` paths from its own
`%~dp0` instead of receiving them as arguments. Passing multiple quoted path
arguments through `wmic`'s own command-line string turned out just as fragile
as `start`'s parsing, so it deliberately takes only one plain argument (the
service name).

## `detect-engine.bat`

Shared by `start.bat`/`stop.bat` here and in `../test/`, `../prod/`. Picks
Docker if it's installed *and* actually reachable (`docker version` succeeds),
otherwise falls back to Podman. Sets `%ENGINE%` (bare CLI) and `%COMPOSE%`
(compose invocation) for the caller to use.

## Known gotcha if you're editing these scripts

Every `mvn.cmd` invocation must use `call`. `mvn.cmd` is itself a batch file —
invoking another batch file without `call` transfers control to it
*permanently*; once it finishes, the calling script doesn't get control back,
it just ends right there with no error at all. This exact bug is why
`start.bat` used to die silently right after "Building shared-contracts" with
zero output, for a long time before it was caught.
