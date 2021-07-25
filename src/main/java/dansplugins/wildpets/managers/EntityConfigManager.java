package dansplugins.wildpets.managers;

import dansplugins.wildpets.objects.EntityConfig;
import org.bukkit.Material;

import java.util.ArrayList;

public class EntityConfigManager {

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

    private void initialize() {
        entityConfigs.add(new EntityConfig("default", 0.25, Material.WHEAT, 10));
    }

}
