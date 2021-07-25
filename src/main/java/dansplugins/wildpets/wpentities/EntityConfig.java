package dansplugins.wildpets.wpentities;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public abstract class EntityConfig {

    private String type;
    private double chanceToSucceed;
    private Material requiredTamingItem;
    private int tamingItemAmount;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getChanceToSucceed() {
        return chanceToSucceed;
    }

    public void setChanceToSucceed(double chanceToSucceed) {
        this.chanceToSucceed = chanceToSucceed;
    }

    public Material getRequiredTamingItem() {
        return requiredTamingItem;
    }

    public void setRequiredTamingItem(Material requiredTamingItem) {
        this.requiredTamingItem = requiredTamingItem;
    }

    public int getTamingItemAmount() {
        return tamingItemAmount;
    }

    public void setTamingItemAmount(int tamingItemAmount) {
        this.tamingItemAmount = tamingItemAmount;
    }

}
