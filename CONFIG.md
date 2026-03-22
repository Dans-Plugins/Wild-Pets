# Wild Pets - Configuration Guide

This document provides detailed information about all configuration options available in Wild Pets.

## Table of Contents
- [General Settings](#general-settings)
- [Taming Settings](#taming-settings)
- [Pet Behavior Settings](#pet-behavior-settings)
- [Interaction Settings](#interaction-settings)
- [Entity Configurations](#entity-configurations)
  - [Entity Configuration Options](#entity-configuration-options)
  - [Default Entity Configurations](#default-entity-configurations)

## General Settings

### `version`
**Type:** String  
**Default:** (auto-generated from plugin version)  
**Description:** Plugin version number. This is automatically set during startup and should not be modified manually.

### `configOptions.debugMode`
**Type:** Boolean  
**Default:** `false`  
**Description:** When `true`, enables verbose debug logging to the server console. Useful for diagnosing issues.

## Taming Settings

### `configOptions.petLimit`
**Type:** Integer  
**Default:** `10`  
**Description:** The maximum number of pets a player can own at one time.  
**Range:** Any positive integer

### `configOptions.cancelTamingAfterFailedAttempt`
**Type:** Boolean  
**Default:** `false`  
**Description:** When `true`, a failed taming attempt automatically cancels taming mode, requiring the player to run `/wp tame` again. When `false`, taming mode remains active after a failed attempt.

### `configOptions.bornPetsEnabled`
**Type:** Boolean  
**Default:** `true`  
**Description:** When `true`, baby entities born from two tamed parents are automatically added to the owning player's pet list.

## Pet Behavior Settings

### `configOptions.damageToPetsEnabled`
**Type:** Boolean  
**Default:** `false`  
**Description:** When `true`, tamed pets can receive damage from attacks.

### `configOptions.damageFromPetsEnabled`
**Type:** Boolean  
**Default:** `false`  
**Description:** When `true`, tamed pets can deal damage to other entities.

### `configOptions.preventMountingLockedPets`
**Type:** Boolean  
**Default:** `true`  
**Description:** This option is currently ignored. Players other than the owner are always prevented from mounting a locked pet, regardless of this setting. The field is retained for backward compatibility and may be enforced in a future version.

### `configOptions.maxScheduleAttempts`
**Type:** Integer  
**Default:** `1440`  
**Description:** Reserved for future use. This option is currently stored in the config but does not affect pet behavior or command handling in the current version.  
**Range:** Any positive integer

## Interaction Settings

### `configOptions.rightClickToSelect`
**Type:** Boolean  
**Default:** `true`  
**Description:** When `true`, players can right-click a tamed pet to select it. When `false`, players must use `/wp select` to enter selection mode before right-clicking.

### `configOptions.rightClickViewCooldown`
**Type:** Integer  
**Default:** `3`  
**Description:** The cooldown in seconds between right-click interactions with pets.  
**Range:** Any non-negative integer

### `configOptions.petNameCharacterLimit`
**Type:** Integer  
**Default:** `20`  
**Description:** The maximum number of characters allowed in a pet name.  
**Range:** Any positive integer

### `configOptions.showLineageInfo`
**Type:** Boolean  
**Default:** `true`  
**Description:** When `true`, lineage information (e.g., parent pets) is displayed when viewing pet info.

## Entity Configurations

Each entity type has its own configuration section under `entityConfigurations.<ConfigKey>`. The `<ConfigKey>` must match one of the plugin's defined entity configuration identifiers (for example: `Glow_Squid`, `Piglin_Baby`, `Polar_Bear_Baby`). These keys are based on Bukkit entity type names but may include additional suffixes to distinguish variants (e.g., `Piglin_Baby` vs `Piglin_Adult`). Human-readable names like "Glow Squid" or "Piglin (Baby)" are labels only and cannot be used as YAML keys.

### Entity Configuration Options

#### `chanceToSucceed`
**Type:** Double  
**Default:** `0.5`  
**Description:** The probability (0.0 to 1.0) that a single taming attempt succeeds. `0.5` means a 50% chance per attempt.  
**Range:** `0.0` to `1.0`

#### `requiredTamingItem`
**Type:** String (Bukkit Material name)  
**Default:** Varies by entity (see below)  
**Description:** The item the player must be holding to tame this entity.  
**Example:** `WHEAT`, `BONE`, `SALMON`

#### `tamingItemAmount`
**Type:** Integer  
**Default:** Varies by entity (see below)  
**Description:** The number of the required item consumed per taming attempt.  
**Range:** Any positive integer

#### `enabled`
**Type:** Boolean  
**Default:** `true`  
**Description:** When `true`, this entity type can be tamed. Set to `false` to disable taming for a specific entity.

### Default Entity Configurations

The following table lists all entities supported by Wild Pets and their default taming requirements.

#### Passive Mobs

| Entity | Config Key | Required Item (`MATERIAL` name) | Amount | Chance |
|--------|-----------|--------------------------------|--------|--------|
| Allay | `Allay` | `COOKIE` | 8 | 50% |
| Axolotl | `Axolotl` | `KELP` | 16 | 50% |
| Bat | `Bat` | `PUMPKIN_PIE` | 1 | 50% |
| Camel | `Camel` | `CACTUS` | 8 | 50% |
| Cat | `Cat` | `SALMON` | 8 | 50% |
| Chicken | `Chicken` | `WHEAT_SEEDS` | 8 | 50% |
| Cod | `Cod` | `KELP` | 16 | 50% |
| Cow | `Cow` | `WHEAT` | 32 | 50% |
| Donkey | `Donkey` | `CARROT` | 8 | 50% |
| Fox | `Fox` | `SWEET_BERRIES` | 8 | 50% |
| Frog | `Frog` | `SLIME_BALL` | 8 | 50% |
| Glow Squid | `Glow_Squid` | `KELP` | 24 | 50% |
| Horse | `Horse` | `APPLE` | 8 | 50% |
| Mooshroom | `Mooshroom` | `RED_MUSHROOM` | 8 | 50% |
| Mule | `Mule` | `APPLE` | 8 | 50% |
| Ocelot | `Ocelot` | `COD` | 8 | 50% |
| Parrot | `Parrot` | `PUMPKIN_SEEDS` | 8 | 50% |
| Pig | `Pig` | `CARROT` | 16 | 50% |
| Piglin (Baby) | `Piglin_Baby` | `NETHER_WART` | 8 | 50% |
| Polar Bear (Baby) | `Polar_Bear_Baby` | `SALMON` | 17 | 50% |
| Pufferfish | `Pufferfish` | `KELP` | 24 | 50% |
| Rabbit | `Rabbit` | `DANDELION` | 8 | 50% |
| Salmon | `Salmon` | `KELP` | 24 | 50% |
| Sheep | `Sheep` | `WHEAT` | 8 | 50% |
| Skeleton Horse | `Skeleton_Horse` | `BONE` | 8 | 50% |
| Sniffer | `Sniffer` | `GRASS` | 32 | 50% |
| Snow Golem | `Snow_Golem` | `SNOWBALL` | 32 | 50% |
| Squid | `Squid` | `KELP` | 24 | 50% |
| Strider | `Strider` | `NETHER_WART` | 32 | 50% |
| Tropical Fish | `Tropical_Fish` | `KELP` | 24 | 50% |
| Turtle | `Turtle` | `SEAGRASS` | 16 | 50% |
| Villager | `Villager` | `POTATO` | 8 | 50% |
| Wandering Trader | `Wandering Trader` | `GOLD_INGOT` | 8 | 50% |

#### Neutral Mobs

| Entity | Config Key | Required Item (`MATERIAL` name) | Amount | Chance |
|--------|-----------|--------------------------------|--------|--------|
| Bee | `Bee` | `HONEYCOMB` | 8 | 50% |
| Cave Spider | `Cave_Spider` | `ROTTEN_FLESH` | 16 | 50% |
| Dolphin | `Dolphin` | `KELP` | 24 | 50% |
| Enderman | `Enderman` | `ENDER_PEARL` | 8 | 50% |
| Goat | `Goat` | `PAPER` | 8 | 50% |
| Iron Golem | `Iron_Golem` | `IRON_INGOT` | 24 | 50% |
| Llama | `Llama` | `BEETROOT_SEEDS` | 8 | 50% |
| Panda | `Panda` | `BAMBOO` | 16 | 50% |
| Piglin (Adult) | `Piglin_Adult` | `NETHER_WART` | 16 | 50% |
| Polar Bear | `Polar_Bear` | `COD` | 16 | 50% |
| Spider | `Spider` | `ROTTEN_FLESH` | 16 | 50% |
| Wolf | `Wolf` | `COOKED_BEEF` | 8 | 50% |
| Zombified Piglin | `Zombified_Piglin` | `NETHER_WART` | 32 | 50% |

#### Hostile Mobs

| Entity | Config Key | Required Item (`MATERIAL` name) | Amount | Chance |
|--------|-----------|--------------------------------|--------|--------|
| Blaze | `Blaze` | `BLAZE_ROD` | 16 | 50% |
| Chicken Jockey | `Chicken_Jockey` | `BONE` | 16 | 50% |
| Creeper | `Creeper` | `GUNPOWDER` | 16 | 50% |
| Drowned | `Drowned` | `KELP` | 16 | 50% |
| Elder Guardian | `Elder_Guardian` | `GLOWSTONE` | 32 | 50% |
| Endermite | `Endermite` | `ENDER_PEARL` | 16 | 50% |
| Evoker | `Evoker` | `GOLD_INGOT` | 16 | 50% |
| Ghast | `Ghast` | `GHAST_TEAR` | 16 | 50% |
| Guardian | `Guardian` | `GLOWSTONE` | 16 | 50% |
| Hoglin | `Hoglin` | `CRIMSON_ROOTS` | 16 | 50% |
| Husk | `Husk` | `SAND` | 16 | 50% |
| Magma Cube | `Magma_Cube` | `MAGMA_CREAM` | 16 | 50% |
| Phantom | `Phantom` | `SOUL_SAND` | 16 | 50% |
| Piglin | `Piglin` | `GOLD_INGOT` | 16 | 50% |
| Pillager | `Pillager` | `GOLD_INGOT` | 16 | 50% |
| Ravager | `Ravager` | `GOLD_INGOT` | 16 | 50% |
| Ravager Jockey | `Ravager_Jockey` | `GOLD_INGOT` | 16 | 50% |
| Shulker | `Shulker` | `CHEST` | 16 | 50% |
| Silverfish | `Silverfish` | `STONE` | 16 | 50% |
| Skeleton | `Skeleton` | `ARROW` | 16 | 50% |
| Skeleton Horseman | `Skeleton_Horseman` | `ARROW` | 32 | 50% |
| Slime | `Slime` | `SLIME_BALL` | 16 | 50% |
| Spider Jockey | `Spider_Jockey` | `ARROW` | 16 | 50% |
| Stray | `Stray` | `ARROW` | 16 | 50% |
| Vex | `Vex` | `IRON_SWORD` | 16 | 50% |
| Vindicator | `Vindicator` | `EMERALD` | 16 | 50% |
| Warden | `Warden` | `TOTEM_OF_UNDYING` | 1 | 50% |
| Witch | `Witch` | `BROWN_MUSHROOM` | 16 | 50% |
| Wither Skeleton | `Wither_Skeleton` | `BONE` | 16 | 50% |
| Zoglin | `Zoglin` | `ROTTEN_FLESH` | 16 | 50% |
| Zombie | `Zombie` | `ROTTEN_FLESH` | 16 | 50% |
| Zombie Villager | `Zombie_Villager` | `ROTTEN_FLESH` | 16 | 50% |

#### Boss Mobs

| Entity | Config Key | Required Item (`MATERIAL` name) | Amount | Chance |
|--------|-----------|--------------------------------|--------|--------|
| Ender Dragon | `Ender_Dragon` | `ENDER_EYE` | 64 | 50% |
| Wither | `Wither` | `WITHER_SKELETON_SKULL` | 16 | 50% |

### Example: Customizing an Entity Configuration

To change the taming requirements for a Creeper in `config.yml`:

```yaml
entityConfigurations:
  Creeper:
    chanceToSucceed: '0.25'
    requiredTamingItem: GUNPOWDER
    tamingItemAmount: '32'
    enabled: 'true'
```

To disable taming of a specific entity (e.g., the Warden):

```yaml
entityConfigurations:
  Warden:
    enabled: 'false'
```
