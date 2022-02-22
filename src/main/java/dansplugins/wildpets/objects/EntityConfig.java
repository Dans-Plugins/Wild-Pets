package dansplugins.wildpets.objects;

import org.bukkit.Material;

/**
 * @author Daniel McCoy Stephenson
 */
public class EntityConfig {
    private String type;
    private double chanceToSucceed;
    private Material requiredTamingItem;
    private int tamingItemAmount;
    private boolean enabled;

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