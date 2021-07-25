package dansplugins.wildpets.managers;

import dansplugins.wildpets.objects.EntityConfig;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

import java.util.ArrayList;

public class EntityConfigManager {

    private boolean debug = true;

    private static EntityConfigManager instance;

    private ArrayList<EntityConfig> entityConfigs = new ArrayList<>();

    private EntityConfigManager() {
        initialize();
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

    private void initialize() {

        // passive mobs
        entityConfigs.add(new EntityConfig("Axilotl", 0.10, Material.KELP, 20));
        entityConfigs.add(new EntityConfig("Bat", 0.25, Material.PUMPKIN_PIE, 1));
        entityConfigs.add(new EntityConfig("Cat", 0.25, Material.SALMON, 5));
        entityConfigs.add(new EntityConfig("Chicken", 0.30, Material.WHEAT_SEEDS, 10));
        entityConfigs.add(new EntityConfig("Cod", 0.10, Material.KELP, 20));
        entityConfigs.add(new EntityConfig("Cow", 0.25, Material.WHEAT, 30));
        entityConfigs.add(new EntityConfig("Donkey", 0.15, Material.CARROT, 5));
        entityConfigs.add(new EntityConfig("Fox", 0.25, Material.SWEET_BERRIES, 10));
        entityConfigs.add(new EntityConfig("Glow_Squid", 0.10, Material.KELP, 20));
        entityConfigs.add(new EntityConfig("Horse", 0.15, Material.APPLE, 5));
        entityConfigs.add(new EntityConfig("Mooshroom", 0.25, Material.RED_MUSHROOM, 10));
        entityConfigs.add(new EntityConfig("Mule", 0.15, Material.APPLE, 5));
        entityConfigs.add(new EntityConfig("Ocelot", 0.25, Material.COD, 10));
        entityConfigs.add(new EntityConfig("Parrot", 0.30, Material.PUMPKIN_SEEDS, 10));
        entityConfigs.add(new EntityConfig("Pig", 0.25, Material.CARROT, 15));
        entityConfigs.add(new EntityConfig("Piglin_Baby", 0.10, Material.NETHER_WART, 5));
        entityConfigs.add(new EntityConfig("Polar_Bear_Baby", 0.10, Material.SALMON, 15));
        entityConfigs.add(new EntityConfig("Pufferfish", 0.10, Material.KELP, 20));
        entityConfigs.add(new EntityConfig("Rabbit", 0.25, Material.DANDELION, 5));
        entityConfigs.add(new EntityConfig("Salmon", 0.10, Material.KELP, 20));
        entityConfigs.add(new EntityConfig("Sheep", 0.25, Material.WHEAT, 10));
        entityConfigs.add(new EntityConfig("Skeleton_Horse", 0.10, Material.BONE, 10));
        entityConfigs.add(new EntityConfig("Snow_Golem", 0.05, Material.SNOWBALL, 30));
        entityConfigs.add(new EntityConfig("Squid", 0.10, Material.KELP, 20));
        entityConfigs.add(new EntityConfig("Strider", 0.05, Material.NETHER_WART, 30));
        entityConfigs.add(new EntityConfig("Tropical_Fish", 0.10, Material.KELP, 20));
        entityConfigs.add(new EntityConfig("Turtle", 0.25, Material.SEAGRASS, 15));

    }

    private EntityConfig getDefaultConfiguration() {
        return new EntityConfig("default", 0.25, Material.WHEAT, 10);
    }

}
