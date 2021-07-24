package dansplugins.wildpets.objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Pet {

    private boolean debug = true;

    private int entityID;
    private UUID uniqueID;
    private EntityType entityType;
    private Player owner;
    private String name;

    public Pet(Entity entity, Player playerOwner) {
        entityID = entity.getEntityId();
        uniqueID = entity.getUniqueId();
        entityType = entity.getType();
        owner = playerOwner;
        name = owner.getDisplayName() + "'s Pet";

        entity.setCustomName(ChatColor.GREEN + name);
        entity.setPersistent(true);
        entity.setInvulnerable(true);

        entity.playEffect(EntityEffect.LOVE_HEARTS);

        if (debug) {
            System.out.println("[DEBUG] Pet instantiated!");
            System.out.println("[DEBUG] Entity ID: " + entityID);
            System.out.println("[DEBUG] Unique ID: " + uniqueID.toString());
            System.out.println("[DEBUG] Entity Type: " + entityType.name());
            System.out.println("[DEBUG] Owner: " + owner.getDisplayName());
            System.out.println("[DEBUG] Name: " + name);
        }
    }

    public int getEntityID() {
        return entityID;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

        Entity entity = Bukkit.getEntity(uniqueID);

        if (entity != null) {
            entity.setCustomName(ChatColor.GREEN + name);
        }
    }

    public void sendInfoToPlayer(Player player) {
        player.sendMessage(ChatColor.AQUA + "=== Pet Info ===");
        player.sendMessage("Name: " + name);
        player.sendMessage("Type: " + entityType.name());
        player.sendMessage("Owner: " + owner.getDisplayName());
    }

}
