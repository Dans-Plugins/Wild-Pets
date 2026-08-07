# Wild Pets - User Guide

This guide provides step-by-step instructions for common scenarios and getting started with Wild Pets.

## Table of Contents
- [Getting Started](#getting-started)
- [Common Scenarios](#common-scenarios)
  - [Taming Your First Pet](#taming-your-first-pet)
  - [Selecting a Pet](#selecting-a-pet)
  - [Renaming a Pet](#renaming-a-pet)
  - [Controlling Pet Behavior](#controlling-pet-behavior)
  - [Managing Multiple Pets](#managing-multiple-pets)
  - [Locating a Pet](#locating-a-pet)
  - [Locking a Pet](#locking-a-pet)
  - [Trading a Pet](#trading-a-pet)
  - [Releasing a Pet](#releasing-a-pet)
- [Tips and Best Practices](#tips-and-best-practices)
- [Troubleshooting](#troubleshooting)

## Getting Started

### Understanding the Basics

Wild Pets allows you to:
- **Tame any entity** - Tame nearly any mob in the game, from passive animals to hostile monsters
- **Control pet behavior** - Command your pet to follow you, stay in place, or wander freely
- **Manage a collection** - Keep track of multiple pets and switch between them
- **Customize your pets** - Give your pets custom names

### Key Concepts

**Taming:**
- Each entity type requires a specific item and a certain quantity of that item
- Taming has a chance to succeed; if it fails, you may need to try again
- The `cancelTamingAfterFailedAttempt` config option controls whether failed attempts cancel the taming process

**Pet Selection:**
- You can only actively interact with one pet at a time — your "selected" pet
- Use `/wp select` to choose which pet to interact with
- With `rightClickToSelect` enabled (default), right-clicking a tamed pet selects it

**Pet Limit:**
- Each player can own up to a configurable maximum number of pets (default: 10)
- Use `/wp list` to see all your current pets

## Common Scenarios

### Taming Your First Pet

**Goal:** Tame a wild entity and make it your pet.

**Steps:**

1. **Find the entity** you want to tame and gather the required taming item. Use `/wp help` to learn about available commands, or consult the [CONFIG.md](CONFIG.md) for entity-specific requirements.

2. **Initiate taming mode:**
   ```
   /wp tame
   ```

3. **Right-click the entity** with the required taming item in hand. The plugin will attempt to tame the mob.

4. **Wait for the result.** You will receive a message indicating success or failure. If taming fails, try again.

5. **Cancel taming mode** at any time:
   ```
   /wp tame cancel
   ```

---

### Selecting a Pet

**Goal:** Select a pet so you can interact with it.

**Method 1: By Name**
```
/wp select PetName
```

**Method 2: By Right-Click (only when `rightClickToSelect` is `false`)**

> Note: This method is only available when `configOptions.rightClickToSelect` is set to `false` in the plugin config. With the default setting (`true`), right-clicking a tamed pet selects it automatically without needing to enter selection mode first.

1. Run `/wp select` to enter selection mode.
2. Right-click the pet you want to select.
3. The pet is now your active/selected pet.

**Cancel selection mode:**
```
/wp select cancel
```

---

### Renaming a Pet

**Goal:** Give your selected pet a custom name.

**Steps:**

1. **Select the pet** you want to rename (see [Selecting a Pet](#selecting-a-pet)).

2. **Rename the pet:**
   ```
   /wp rename NewName
   ```
   Pet names are limited to 20 characters by default (configurable via `petNameCharacterLimit`).

---

### Controlling Pet Behavior

**Goal:** Set your selected pet's behavior mode.

**Make your pet follow you:**
```
/wp follow
```

**Make your pet stay in place:**
```
/wp stay
```

**Make your pet wander freely:**
```
/wp wander
```

**Teleport your pet directly to you:**
```
/wp call
```

---

### Managing Multiple Pets

**Goal:** View and manage all of your pets.

**List your pets:**
```
/wp list
```

**List another player's pets** (requires `wp.list.others` permission):
```
/wp list PlayerName
```

**Teleport all your pets to you at once:**
```
/wp gather
```

---

### Locating a Pet

**Goal:** Find out where your selected pet is.

**Steps:**

1. **Select the pet** you want to locate.

2. **View the pet's last known location:**
   ```
   /wp locate
   ```

---

### Locking a Pet

**Goal:** Lock a pet to prevent others from mounting it.

**Steps:**

1. **Enter lock mode:**
   ```
   /wp lock
   ```

2. **Right-click one of your owned pets** to lock it.

**To unlock a pet:**

1. **Enter unlock mode:**
   ```
   /wp unlock
   ```

2. **Right-click one of your owned pets** to unlock it.

---

### Trading a Pet

**Goal:** Give one of your pets to another player.

**Steps:**

1. **Select the pet** you want to trade away (see [Selecting a Pet](#selecting-a-pet)).

2. **Trade the pet** to an online player:
   ```
   /wp trade PlayerName
   ```

3. **Both players are notified** once the trade succeeds. The pet leaves your pet list, your selection is cleared, and the pet counts against the other player's pet limit from then on.

> Note: The target player must be online, and you cannot trade a pet to yourself. A trade is also refused when the other player has already reached the `petLimit`. Ownership transfers immediately and cannot be undone by the plugin — the new owner has to trade the pet back.

---

### Releasing a Pet

**Goal:** Release a pet and remove it from your pet list.

**Steps:**

1. **Select the pet** you want to release.

2. **Release the pet:**
   ```
   /wp setfree
   ```

   The pet will be removed from your ownership and will no longer appear in your pet list.

---

## Tips and Best Practices

- Use `/wp info` to quickly check your currently selected pet's details.
- Use `/wp gather` when you've lost track of multiple pets across a large area.
- If you're having trouble taming a specific mob, check the entity's configuration (taming item and amount) in `config.yml`.
- Lock valuable pets to prevent other players from riding them.
- Pet names help distinguish pets of the same type — give each one a unique name.

## Troubleshooting

**I can't tame a mob:**
- Make sure you have the correct taming item and sufficient quantity in your hand.
- Check that the entity type is enabled in the plugin configuration.
- Verify you have the `wp.tame` permission.

**My pet disappeared:**
- Use `/wp locate` to find its last known location.
- Use `/wp call` to teleport the pet to you.
- Use `/wp gather` to teleport all your pets to you at once.

**I've reached my pet limit:**
- Use `/wp list` to view all your pets.
- Release pets you no longer need with `/wp setfree`.
- Alternatively, ask a server admin to increase the `petLimit` configuration value.

**Commands are not working:**
- Make sure you have the required permissions. See [COMMANDS.md](COMMANDS.md) for a full list of permissions.
- Type `/wp help` in-game for a list of available commands.
