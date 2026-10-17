# Fabric.SK 0.2.5

Fabric.SK is a Fabric mod project that provides a Skript-inspired scripting engine for Minecraft. It loads `.sk` scripts from the game configuration directory and supports a subset of Skript-like commands, events, expressions, and effects. It is **not a drop-in replacement for the Bukkit/Paper Skript plugin**; syntax support depends on the implementation in each release.

## Supported project versions

This repository contains separate Gradle projects for each Minecraft version. Open the folder matching your Minecraft version and build that project independently.

| Minecraft version | Project folder | Build status |
|---|---|---|
| 1.21.7 | `FabricSK-0.2.5-MC1.21.7` | Needs final build confirmation |
| 1.21.8 | `FabricSK-0.2.5-MC1.21.8` | Needs final build confirmation |
| 1.21.9 | `FabricSK-0.2.5-MC1.21.9` | Reported compiled successfully |
| 1.21.10 | `FabricSK-0.2.5-MC1.21.10` | Reported compiled successfully |
| 1.21.11 | `FabricSK-0.2.5-MC1.21.11` | Release source included |
| 26.1 | `FabricSK-0.2.5-MC26.1` | Reported compiled successfully |
| 26.1.1 | `FabricSK-0.2.5-MC26.1.1` | Reported compiled successfully |
| 26.1.2 | `FabricSK-0.2.5-MC26.1.2` | Reported compiled successfully |
| 26.2 | `FabricSK-0.2.5-MC26.2` | Fix included; build confirmation pending |
| 26.3 | `FabricSK-0.2.5-MC26.3` | Fix included; build confirmation pending |

Build status reflects user-reported results and has not been independently verified in this packaging environment. In particular, the 26.2 and 26.3 source fixes are included but should be rebuilt and tested before being described as stable releases.

## Requirements

- Minecraft/Fabric Loader versions appropriate to the selected project.
- Java 21 for Minecraft 1.21.x projects.
- Java 25 for Minecraft 26.1+ projects.
- Gradle installed, or use the included setup wrapper where applicable.

## Build

In PowerShell or a terminal, change into the project folder for your Minecraft version and run:

```powershell
gradle build
```

The compiled JARs are normally produced under the project's `fabric-sk/build/libs/` directory. Use the JAR intended for installation, not the `-sources` or `-dev` artifact if one is generated.

## Scripts

Place `.sk` files in:

```text
config/fabricsk/scripts/
```

Use the Fabric.SK reload command in game to reload scripts. Example scripts are included in each project under `examples/` where available.

## Engine packaging

The project is split into the Fabric.SK mod and its embedded engine subproject. The intended engine version string is `0.2x`; keep it as-is when packaging the release.

## Compatibility note

Fabric.SK implements a subset of Skript-like syntax for Fabric. Not every Bukkit Skript expression, event, condition, effect, addon, or plugin API is available. Check the example scripts and test scripts on the exact Minecraft version and server environment you use.
