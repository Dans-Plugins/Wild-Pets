package dansplugins.wildpets.managers;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.objects.EntityConfig;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class EntityConfigManager {

    private boolean debug = true;

    private static EntityConfigManager instance;

    private ArrayList<EntityConfig> entityConfigs = new ArrayList<>();

    private EntityConfigManager() {

    }

    public static EntityConfigManager getInstance() {
        if (instance == null) {
            instance = new EntityConfigManager();
        }
        return instance;
    }

    public EntityConfig acquireConfiguration(Entity entity) {
        for (EntityConfig entityConfig : entityConfigs) {
            if (entity.getType().name().equalsIgnoreCase(entityConfig.getType())) {
                if (debug) { System.out.println("Configuration for " + entity.getType().name() + " found!"); }
                return entityConfig;
            }
        }
        if (debug) { System.out.println("Configuration for " + entity.getType().name() + " not found! Using default configuration."); }
        return getDefaultConfiguration();
    }

    public ArrayList<EntityConfig> getEntityConfigurations() {
        return entityConfigs;
    }

    public void initializeWithDefaults() {
        if (debug) { System.out.println("[DEBUG] Initializing with defaults."); }

        // passive mobs
        entityConfigs.add(new EntityConfig("Axolotl", 0.5, Material.KELP, 16, true));
        entityConfigs.add(new EntityConfig("Bat", 0.5, Material.PUMPKIN_PIE, 1, true));
        entityConfigs.add(new EntityConfig("Cat", 0.5, Material.SALMON, 8, true));
        entityConfigs.add(new EntityConfig("Chicken", 0.5, Material.WHEAT_SEEDS, 8, true));
        entityConfigs.add(new EntityConfig("Cod", 0.5, Material.KELP, 16, true));
        entityConfigs.add(new EntityConfig("Cow", 0.5, Material.WHEAT, 32, true));
        entityConfigs.add(new EntityConfig("Donkey", 0.5, Material.CARROT, 8, true));
        entityConfigs.add(new EntityConfig("Fox", 0.5, Material.SWEET_BERRIES, 8, true));
        entityConfigs.add(new EntityConfig("Glow_Squid", 0.5, Material.KELP, 24, true));
        entityConfigs.add(new EntityConfig("Horse", 0.5, Material.APPLE, 8, true));
        entityConfigs.add(new EntityConfig("Mooshroom", 0.5, Material.RED_MUSHROOM, 8, true));
        entityConfigs.add(new EntityConfig("Mule", 0.5, Material.APPLE, 8, true));
        entityConfigs.add(new EntityConfig("Ocelot", 0.5, Material.COD, 8, true));
        entityConfigs.add(new EntityConfig("Parrot", 0.5, Material.PUMPKIN_SEEDS, 8, true));
        entityConfigs.add(new EntityConfig("Pig", 0.5, Material.CARROT, 16, true));
        entityConfigs.add(new EntityConfig("Piglin_Baby", 0.5, Material.NETHER_WART, 8, true));
        entityConfigs.add(new EntityConfig("Polar_Bear_Baby", 0.5, Material.SALMON, 17, true));
        entityConfigs.add(new EntityConfig("Pufferfish", 0.5, Material.KELP, 24, true));
        entityConfigs.add(new EntityConfig("Rabbit", 0.5, Material.DANDELION, 8, true));
        entityConfigs.add(new EntityConfig("Salmon", 0.5, Material.KELP, 24, true));
        entityConfigs.add(new EntityConfig("Sheep", 0.5, Material.WHEAT, 8, true));
        entityConfigs.add(new EntityConfig("Skeleton_Horse", 0.5, Material.BONE, 8, true));
        entityConfigs.add(new EntityConfig("Snow_Golem", 0.5, Material.SNOWBALL, 32, true));
        entityConfigs.add(new EntityConfig("Squid", 0.5, Material.KELP, 24, true));
        entityConfigs.add(new EntityConfig("Strider", 0.5, Material.NETHER_WART, 32, true));
        entityConfigs.add(new EntityConfig("Tropical_Fish", 0.5, Material.KELP, 24, true));
        entityConfigs.add(new EntityConfig("Turtle", 0.5, Material.SEAGRASS, 16, true));
        entityConfigs.add(new EntityConfig("Villager", 0.5, Material.POTATO, 8, false));
        entityConfigs.add(new EntityConfig("Wandering Trader", 0.5, Material.GOLD_INGOT, 8, false));

        // neutral mobs
        entityConfigs.add(new EntityConfig("Bee", 0.5, Material.HONEYCOMB, 8, true));
        entityConfigs.add(new EntityConfig("Cave_Spider", 0.5, Material.ROTTEN_FLESH, 16, true));
        entityConfigs.add(new EntityConfig("Dolphin", 0.5, Material.KELP, 24, true));
        entityConfigs.add(new EntityConfig("Enderman", 0.5, Material.ENDER_PEARL, 8, true));
        entityConfigs.add(new EntityConfig("Goat", 0.5, Material.PAPER, 8, true));
        entityConfigs.add(new EntityConfig("Iron_Golem", 0.5, Material.IRON_INGOT, 24, true));
        entityConfigs.add(new EntityConfig("Llama", 0.5, Material.BEETROOT_SEEDS, 8, true));
        entityConfigs.add(new EntityConfig("Piglin_Adult", 0.5, Material.NETHER_WART, 16, true));
        entityConfigs.add(new EntityConfig("Panda", 0.5, Material.BAMBOO, 16, true));
        entityConfigs.add(new EntityConfig("Polar_Bear", 0.5, Material.COD, 16, true));
        entityConfigs.add(new EntityConfig("Spider", 0.5, Material.ROTTEN_FLESH, 16, true));
        entityConfigs.add(new EntityConfig("Wolf", 0.5, Material.COOKED_BEEF, 8, true));
        entityConfigs.add(new EntityConfig("Zombified_Piglin", 0.5, Material.NETHER_WART, 32, true));

        // hostile mobs
        entityConfigs.add(new EntityConfig("Blaze", 0.5, Material.BLAZE_ROD, 16, false));
        entityConfigs.add(new EntityConfig("Chicken_Jockey", 0.5, Material.BONE, 16, false));
        entityConfigs.add(new EntityConfig("Creeper", 0.5, Material.GUNPOWDER, 16, false));
        entityConfigs.add(new EntityConfig("Drowned", 0.5, Material.KELP, 16, false));
        entityConfigs.add(new EntityConfig("Elder_Guardian", 0.5, Material.GLOWSTONE, 32, false));
        entityConfigs.add(new EntityConfig("Endermite", 0.5, Material.ENDER_PEARL, 16, false));
        entityConfigs.add(new EntityConfig("Evoker", 0.5, Material.GOLD_INGOT, 16, false));
        entityConfigs.add(new EntityConfig("Endermite", 0.5, Material.ENDER_PEARL, 16, false));
        entityConfigs.add(new EntityConfig("Ghast", 0.5, Material.GHAST_TEAR, 16, false));
        entityConfigs.add(new EntityConfig("Guardian", 0.5, Material.GLOWSTONE, 16, false));
        entityConfigs.add(new EntityConfig("Hoglin", 0.5, Material.CRIMSON_ROOTS, 16, false));
        entityConfigs.add(new EntityConfig("Husk", 0.5, Material.SAND, 16, false));
        entityConfigs.add(new EntityConfig("Magma_Cube", 0.5, Material.MAGMA_CREAM, 16, false));
        entityConfigs.add(new EntityConfig("Phantom", 0.5, Material.SOUL_SAND, 16, false));
        entityConfigs.add(new EntityConfig("Piglin", 0.5, Material.GOLD_INGOT, 16, false));
        entityConfigs.add(new EntityConfig("Pillager", 0.5, Material.GOLD_INGOT, 16, false));
        entityConfigs.add(new EntityConfig("Ravager", 0.5, Material.GOLD_INGOT, 16, false));
        entityConfigs.add(new EntityConfig("Ravager_Jockey", 0.5, Material.GOLD_INGOT, 16, false));
        entityConfigs.add(new EntityConfig("Shulker", 0.5, Material.CHEST, 16, false));
        entityConfigs.add(new EntityConfig("Silverfish", 0.5, Material.STONE, 16, false));
        entityConfigs.add(new EntityConfig("Skeleton", 0.5, Material.ARROW, 16, false));
        entityConfigs.add(new EntityConfig("Skeleton_Horseman", 0.5, Material.ARROW, 32, false));
        entityConfigs.add(new EntityConfig("Slime", 0.5, Material.SLIME_BALL, 16, false));
        entityConfigs.add(new EntityConfig("Spider_Jockey", 0.5, Material.ARROW, 16, false));
        entityConfigs.add(new EntityConfig("Stray", 0.5, Material.ARROW, 16, false));
        entityConfigs.add(new EntityConfig("Vex", 0.5, Material.IRON_SWORD, 16, false));
        entityConfigs.add(new EntityConfig("Vindicator", 0.5, Material.EMERALD, 16, false));
        entityConfigs.add(new EntityConfig("Witch", 0.5, Material.BROWN_MUSHROOM, 16, false));
        entityConfigs.add(new EntityConfig("Wither_Skeleton", 0.5, Material.BONE, 16, false));
        entityConfigs.add(new EntityConfig("Zoglin", 0.5, Material.ROTTEN_FLESH, 16, false));
        entityConfigs.add(new EntityConfig("Zombie", 0.5, Material.ROTTEN_FLESH, 16, false));
        entityConfigs.add(new EntityConfig("Zombie_Villager", 0.5, Material.ROTTEN_FLESH, 16, false));

        // boss mobs
        entityConfigs.add(new EntityConfig("Ender_Dragon", 0.5, Material.ENDER_EYE, 64, false));
        entityConfigs.add(new EntityConfig("Wither", 0.5, Material.WITHER_SKELETON_SKULL, 16, false));

        if (debug) {
            printEntityConfigurations();
        }

    }

    public void initializeWithConfig() {
        if (debug) { System.out.println("[DEBUG] Initializing with config."); }

        Set<String> keys = WildPets.getInstance().getConfig().getConfigurationSection("entityConfigurations").getKeys(false);

        for (String key : keys) {
            if (debug) { System.out.println("Looking at entity configuration for " + key); }
            HashMap<String, String> options = new HashMap<>();

            Set<String> configOptions = WildPets.getInstance().getConfig().getConfigurationSection("entityConfigurations." + key).getKeys(false);

            for (String configOption : configOptions) {
                String value = WildPets.getInstance().getConfig().getString("entityConfigurations." + key + "." + configOption);
                if (debug) { System.out.println("Looking at config option " + configOption + ". Value: " + value); }
                options.put(configOption, value);
            }

            entityConfigs.add(new EntityConfig(key, Double.parseDouble(options.get("chanceToSucceed")), Material.getMaterial(options.get("requiredTamingItem")), Integer.parseInt(options.get("tamingItemAmount")), Boolean.parseBoolean(options.get("enabled"))));
        }

        if (debug) {
            printEntityConfigurations();
        }
    }

    public EntityConfig getDefaultConfiguration() {
        return new EntityConfig("default", 0.25, Material.WHEAT, 10, true);
    }

    private void printEntityConfigurations() {
        System.out.println("Entity Configurations: ");
        for (EntityConfig config : entityConfigs) {
            System.out.println(config.getType() + ", " + config.getChanceToSucceed() + ", " + config.getRequiredTamingItem().name() + ", " + config.getTamingItemAmount() + ", " + config.isEnabled());
        }
    }
}
