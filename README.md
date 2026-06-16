# Fabric.SK

**A simple Skript interpreter for Fabric.**

![Version](https://img.shields.io/badge/version-0.2.5ALPHA-blue)
![Minecraft](https://img.shields.io/badge/minecraft-1.21+-green)
![Java](https://img.shields.io/badge/java-21)


Fabric.SK allows Minecraft servers and clients running Fabric to load and execute Skript-style scripts without requiring Bukkit, Paper, Spigot, or any Bukkit-based API.

> Current Version: 0.2.5-ALPHA

---

## Features

### Script Support

- `.sk` scripts
- `.heavysk` packages

### Heavy Skript

A `.heavysk` file is simply a renamed ZIP archive.

Example:

```text
Economy.heavysk
```

Contents:

```text
manifest.json
main.sk
commands.sk
events.sk
```

Supported manifests:

- `manifest.json`
- `package.skmeta` (JSON5)
- `manifest.sk.toml`

---

## Commands

### Main Command

```text
/fabricskript
```

Aliases:

```text
/skript
/fsk
/sk
```

---

### Script Management

```text
/fsk load <script>
/fsk unload <script>
/fsk reload <script>
/fsk reloadall
```

Examples:

```text
/fsk load welcome.sk
/fsk reload economy.heavysk
```

---

### Information

```text
/fsk list
/fsk info <script>
/fsk version
/fsk help
```

---

### Validation

```text
/fsk validate <script>
```

Example:

```text
/fsk validate welcome.sk
```

---

### Errors

```text
/fsk errors
```

Displays recent parser and runtime errors.

---

### Syntax Correction

```text
/fsk toggle syntaxcorrect auto <script>
/fsk toggle syntaxcorrect partial <script>
```

Modes:

| Mode | Description |
|--------|--------|
| auto | Automatically fixes simple syntax issues |
| partial | Suggests fixes only |

---

## Script Folder

Fabric.SK automatically creates:

```text
.minecraft/fabricsk/scripts/
```

Example:

```text
fabricsk/
└── scripts/
    ├── welcome.sk
    ├── admin.sk
    └── economy.heavysk
```

---

## Example Script

```sk
command /hello:
    trigger:
        send "Hello World!"
```

---

## Example Event

```sk
on join:
    send "Welcome!"
```

---

## Error Reporting

Fabric.SK includes:

- Line mapping
- Error codes
- Context display
- Error logs

Example:

```text
[Fabric.SK] FSK001

File: welcome.sk
Line: 14

sends "Hello"
^

Unknown syntax
```

Error logs are stored in:

```text
.minecraft/fabricsk/logs/script-errors.log
```

---

## Configuration

Config file:

```text
.minecraft/config/fabricsk.json
```

Example:

```json
{
  "version": "0.2.5-ALPHA",
  "autoLoadScripts": true,
  "enableHeavySk": true,
  "enableErrorLogging": true
}
```

---

## Supported Minecraft Versions

Fabric.SK 0.2.5-ALPHA supports:

- Minecraft 1.21.x
- Minecraft 26.1.x

---

> **WARNING**
> Fabric.SK 0.2.5-ALPHA is an experimental development build.
> APIs, script syntax, and package formats may change before the 0.3.0 release. 
