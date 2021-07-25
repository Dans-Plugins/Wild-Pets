package dansplugins.wildpets.wpentities;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public abstract class WPEntity {

    private EntityType entityType;
    private double chanceToSucceed;
    private Material requiredTamingItem;
    private int tamingItemAmount;

    public WPEntity(EntityType type, double succeedingChance, Material tamingItem, int itemAmount) {
        entityType = type;
        chanceToSucceed = succeedingChance;
        requiredTamingItem = tamingItem;
        tamingItemAmount = itemAmount;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public double getChanceToSucceed() {
        return chanceToSucceed;
    }

    public Material getRequiredTamingItem() {
        return requiredTamingItem;
    }

    public int getTamingItemAmount() {
        return tamingItemAmount;
    }

}
