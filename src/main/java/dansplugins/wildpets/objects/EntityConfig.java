package dansplugins.wildpets.objects;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class EntityConfig {

    private String type;
    private double chanceToSucceed;
    private Material requiredTamingItem;
    private int tamingItemAmount;

    public EntityConfig(String inputType, double inputChanceToSucceed, Material inputRequiredTamingItem, int inputTamingItemAmount) {
        type = inputType;
        chanceToSucceed = inputChanceToSucceed;
        requiredTamingItem = inputRequiredTamingItem;
        tamingItemAmount = inputTamingItemAmount;
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

}
