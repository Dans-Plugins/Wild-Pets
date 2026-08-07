# Wild Pets - Commands Reference

This document describes the primary commands available in the Wild Pets plugin.

## Table of Contents
- [Command Aliases](#command-aliases)
- [General Commands](#general-commands)
- [Taming](#taming)
- [Pet Selection](#pet-selection)
- [Pet Information](#pet-information)
- [Pet Behavior](#pet-behavior)
- [Pet Management](#pet-management)
- [Trading](#trading)
- [Locking](#locking)
- [Access Control](#access-control)
- [Administration](#administration)

## Command Aliases

The main command can be accessed using either of the following aliases:
- `/wildpets`
- `/wp`

## General Commands

### `/wp help`
**Permission:** `wp.help` (default: true)  
**Description:** Displays a list of available commands and usage information.  
**Usage:** `/wp help`

### `/wp stats`
**Permission:** `wp.stats` (default: true)  
**Description:** Displays plugin statistics.  
**Usage:** `/wp stats`

## Taming

### `/wp tame`
**Permission:** `wp.tame` (default: true)  
**Description:** Enters taming mode. Right-click an entity to attempt to tame it. You must be holding the required taming item.  
**Usage:** `/wp tame`

### `/wp tame cancel`
**Permission:** `wp.tame` (default: true)  
**Description:** Cancels an active taming attempt.  
**Usage:** `/wp tame cancel`

## Pet Selection

### `/wp select [petName]`
**Permission:** `wp.select` (default: true)  
**Description:** Selects a pet by name. When `configOptions.rightClickToSelect` is set to `false`, this command can also enter a temporary selection mode to select a pet by right-clicking it. Only one pet can be selected at a time.  
**Usage:**
- `/wp select PetName` - Select a pet by name
- `/wp select` - (Only when `rightClickToSelect` is `false`) Enter selection mode, then right-click a pet to select it. When `rightClickToSelect` is `true` (the default), running `/wp select` without a pet name will result in a usage error.

### `/wp select cancel`
**Permission:** `wp.select` (default: true)  
**Description:** Cancels an active pet selection attempt. Only available when `configOptions.rightClickToSelect` is `false`.  
**Usage:** `/wp select cancel`

## Pet Information

### `/wp info`
**Permission:** `wp.info` (default: true)  
**Description:** Displays information about your currently selected pet.  
**Usage:** `/wp info`

### `/wp list [playerName]`
**Permission:** `wp.list` (default: true), `wp.list.others` (default: true)  
**Description:** Lists your tamed pets. If a player name is provided and you have the `wp.list.others` permission, lists that player's pets instead.  
**Usage:**
- `/wp list` - List your own pets
- `/wp list PlayerName` - List another player's pets

### `/wp locate`
**Permission:** `wp.locate` (default: true)  
**Description:** Shows the last known location of your currently selected pet.  
**Usage:** `/wp locate`

## Pet Behavior

### `/wp follow`
**Permission:** `wp.follow` (default: true)  
**Description:** Commands your selected pet to follow you.  
**Usage:** `/wp follow`

### `/wp stay`
**Permission:** `wp.stay` (not defined in `plugin.yml`; operators have it by default, other players must be granted it explicitly)  
**Description:** Commands your selected pet to stay in its current location.  
**Usage:** `/wp stay`

### `/wp wander`
**Permission:** `wp.wander` (default: true)  
**Description:** Commands your selected pet to wander freely.  
**Usage:** `/wp wander`

### `/wp call`
**Permission:** `wp.call` (default: true)  
**Description:** Teleports your selected pet to your current location.  
**Usage:** `/wp call`

### `/wp gather`
**Permission:** `wp.gather` (default: true)  
**Description:** Teleports all of your pets to your current location.  
**Usage:** `/wp gather`

## Pet Management

### `/wp rename <newName>`
**Permission:** `wp.rename` (default: true)  
**Description:** Renames your currently selected pet. Pet names are limited to 20 characters by default (configurable via `petNameCharacterLimit`).  
**Usage:** `/wp rename NewName`

### `/wp setfree`
**Permission:** `wp.setfree` (default: true)  
**Description:** Releases your currently selected pet, removing it from your ownership.  
**Usage:** `/wp setfree`

## Trading

### `/wp trade <playerName>`
**Permission:** `wp.trade` (default: true)  
**Description:** Transfers ownership of your currently selected pet to another online player. The target player must be online, must not be yourself, and must be below the `petLimit`; you must also own the selected pet. Once the trade succeeds, the pet is removed from your pet list, your selection is cleared, and both players are notified.  
**Usage:** `/wp trade PlayerName`

## Locking

### `/wp lock`
**Permission:** `wp.lock` (default: true)  
**Description:** Enters lock mode. Right-click one of your owned pets to lock it, preventing other players from mounting it.  
**Usage:** `/wp lock`

### `/wp unlock`
**Permission:** `wp.unlock` (default: true)  
**Description:** Enters unlock mode. Right-click one of your owned pets to unlock it.  
**Usage:** `/wp unlock`

## Access Control

### `/wp checkaccess`
**Permission:** `wp.checkaccess` (not defined in `plugin.yml`; operators have it by default, other players must be granted it explicitly)  
**Description:** Enters access-check mode. Right-click a pet to view who has access to it.  
**Usage:** `/wp checkaccess`

## Administration

### `/wp config show`
**Permission:** `wp.config` (default: op)  
**Description:** Displays all current plugin configuration values.  
**Usage:** `/wp config show`

### `/wp config set <option> <value>`
**Permission:** `wp.config` (default: op)  
**Description:** Sets a plugin configuration option to the specified value.  
**Usage:** `/wp config set petLimit 15`

See [CONFIG.md](CONFIG.md) for a full list of available configuration options.
