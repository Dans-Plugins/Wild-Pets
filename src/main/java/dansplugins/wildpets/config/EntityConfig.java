package dansplugins.wildpets.config;

import org.bukkit.Material;

/**
 * @author Daniel McCoy Stephenson
 */
public class EntityConfig {
    private final String type;
    private final double chanceToSucceed;
    private final Material requiredTamingItem;
    private final int tamingItemAmount;
    private final boolean enabled;

    public EntityConfig(String inputType, double inputChanceToSucceed, Material inputRequiredTamingItem, int inputTamingItemAmount, boolean inputEnabled) {
        type = inputType;
        chanceToSucceed = inputChanceToSucceed;
        requiredTamingItem = inputRequiredTamingItem;
        tamingItemAmount = inputTamingItemAmount;
        enabled = inputEnabled;
    }

    public String getType() {
        return type;
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

    public boolean isEnabled() {
        return enabled;
    }
}