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

### `configOptions.version`
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
**Description:** When `true`, players other than the owner are prevented from mounting a locked pet.

### `configOptions.maxScheduleAttempts`
**Type:** Integer  
**Default:** `1440`  
**Description:** The maximum number of scheduling attempts for pet behavior tasks. Increase this value if pets stop following commands on long-running servers.  
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

Each entity type has its own configuration section under `entityConfigurations.<EntityType>`. These settings control how a specific entity can be tamed.

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

| Entity | Required Item | Amount | Chance |
|--------|--------------|--------|--------|
| Allay | Cookie | 8 | 50% |
| Axolotl | Kelp | 16 | 50% |
| Bat | Pumpkin Pie | 1 | 50% |
| Camel | Cactus | 8 | 50% |
| Cat | Salmon | 8 | 50% |
| Chicken | Wheat Seeds | 8 | 50% |
| Cod | Kelp | 16 | 50% |
| Cow | Wheat | 32 | 50% |
| Donkey | Carrot | 8 | 50% |
| Fox | Sweet Berries | 8 | 50% |
| Frog | Slime Ball | 8 | 50% |
| Glow Squid | Kelp | 24 | 50% |
| Horse | Apple | 8 | 50% |
| Mooshroom | Red Mushroom | 8 | 50% |
| Mule | Apple | 8 | 50% |
| Ocelot | Cod | 8 | 50% |
| Parrot | Pumpkin Seeds | 8 | 50% |
| Pig | Carrot | 16 | 50% |
| Piglin (Baby) | Nether Wart | 8 | 50% |
| Polar Bear (Baby) | Salmon | 17 | 50% |
| Pufferfish | Kelp | 24 | 50% |
| Rabbit | Dandelion | 8 | 50% |
| Salmon | Kelp | 24 | 50% |
| Sheep | Wheat | 8 | 50% |
| Skeleton Horse | Bone | 8 | 50% |
| Sniffer | Grass | 32 | 50% |
| Snow Golem | Snowball | 32 | 50% |
| Squid | Kelp | 24 | 50% |
| Strider | Nether Wart | 32 | 50% |
| Tropical Fish | Kelp | 24 | 50% |
| Turtle | Seagrass | 16 | 50% |
| Villager | Potato | 8 | 50% |
| Wandering Trader | Gold Ingot | 8 | 50% |

#### Neutral Mobs

| Entity | Required Item | Amount | Chance |
|--------|--------------|--------|--------|
| Bee | Honeycomb | 8 | 50% |
| Cave Spider | Rotten Flesh | 16 | 50% |
| Dolphin | Kelp | 24 | 50% |
| Enderman | Ender Pearl | 8 | 50% |
| Goat | Paper | 8 | 50% |
| Iron Golem | Iron Ingot | 24 | 50% |
| Llama | Beetroot Seeds | 8 | 50% |
| Panda | Bamboo | 16 | 50% |
| Piglin (Adult) | Nether Wart | 16 | 50% |
| Polar Bear | Cod | 16 | 50% |
| Spider | Rotten Flesh | 16 | 50% |
| Wolf | Cooked Beef | 8 | 50% |
| Zombified Piglin | Nether Wart | 32 | 50% |

#### Hostile Mobs

| Entity | Required Item | Amount | Chance |
|--------|--------------|--------|--------|
| Blaze | Blaze Rod | 16 | 50% |
| Chicken Jockey | Bone | 16 | 50% |
| Creeper | Gunpowder | 16 | 50% |
| Drowned | Kelp | 16 | 50% |
| Elder Guardian | Glowstone | 32 | 50% |
| Endermite | Ender Pearl | 16 | 50% |
| Evoker | Gold Ingot | 16 | 50% |
| Ghast | Ghast Tear | 16 | 50% |
| Guardian | Glowstone | 16 | 50% |
| Hoglin | Crimson Roots | 16 | 50% |
| Husk | Sand | 16 | 50% |
| Magma Cube | Magma Cream | 16 | 50% |
| Phantom | Soul Sand | 16 | 50% |
| Piglin | Gold Ingot | 16 | 50% |
| Pillager | Gold Ingot | 16 | 50% |
| Ravager | Gold Ingot | 16 | 50% |
| Ravager Jockey | Gold Ingot | 16 | 50% |
| Shulker | Chest | 16 | 50% |
| Silverfish | Stone | 16 | 50% |
| Skeleton | Arrow | 16 | 50% |
| Skeleton Horseman | Arrow | 32 | 50% |
| Slime | Slime Ball | 16 | 50% |
| Spider Jockey | Arrow | 16 | 50% |
| Stray | Arrow | 16 | 50% |
| Vex | Iron Sword | 16 | 50% |
| Vindicator | Emerald | 16 | 50% |
| Warden | Totem of Undying | 1 | 50% |
| Witch | Brown Mushroom | 16 | 50% |
| Wither Skeleton | Bone | 16 | 50% |
| Zoglin | Rotten Flesh | 16 | 50% |
| Zombie | Rotten Flesh | 16 | 50% |
| Zombie Villager | Rotten Flesh | 16 | 50% |

#### Boss Mobs

| Entity | Required Item | Amount | Chance |
|--------|--------------|--------|--------|
| Ender Dragon | Ender Eye | 64 | 50% |
| Wither | Wither Skeleton Skull | 16 | 50% |

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
