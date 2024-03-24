package dansplugins.wildpets.config;

import org.bukkit.Material;
import org.junit.Test;

public class EntityConfigTest {

    @Test
    public void testInitialization() {
        EntityConfig entityConfig = new EntityConfig("Chicken", 0.5, Material.KELP, 16, true);
        assert(entityConfig.getType().equals("Chicken"));
        assert(entityConfig.getChanceToSucceed() == 0.5);
        assert(entityConfig.getRequiredTamingItem().equals(Material.KELP));
        assert(entityConfig.getTamingItemAmount() == 16);
        assert(entityConfig.isEnabled());
    }

    @Test
    public void testInitialization_Disabled() {
        EntityConfig entityConfig = new EntityConfig("Chicken", 0.5, Material.KELP, 16, false);
        assert(entityConfig.getType().equals("Chicken"));
        assert(entityConfig.getChanceToSucceed() == 0.5);
        assert(entityConfig.getRequiredTamingItem().equals(Material.KELP));
        assert(entityConfig.getTamingItemAmount() == 16);
        assert(!entityConfig.isEnabled());
    }
}
