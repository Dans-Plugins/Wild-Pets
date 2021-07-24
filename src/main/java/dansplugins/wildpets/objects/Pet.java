package dansplugins.wildpets.objects;

import dansplugins.wildpets.WildPets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Pet {

    private boolean debug = true;

    private int entityID;
    private UUID uniqueID;
    private EntityType entityType;
    private Player owner;
    private String name;

    private String movementState;

    private Location stayingLocation;
    private int teleportTaskID = -1;

    public Pet(Entity entity, Player playerOwner) {
        entityID = entity.getEntityId();
        uniqueID = entity.getUniqueId();
        entityType = entity.getType();
        owner = playerOwner;
        name = owner.getDisplayName() + "'s Pet";
        movementState = "Wandering";

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

    public void deconstruct() {
        Entity entity = Bukkit.getEntity(uniqueID);

        if (entity != null) {
            entity.setCustomName("");
            entity.setPersistent(false);
            entity.setInvulnerable(false);
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
        player.sendMessage(ChatColor.AQUA + "Name: " + name);
        player.sendMessage(ChatColor.AQUA + "Type: " + entityType.name());
        player.sendMessage(ChatColor.AQUA + "Owner: " + owner.getDisplayName());
        player.sendMessage(ChatColor.AQUA + "State: " + movementState);
    }

    public void setWandering() {
        movementState = "Wandering";
        cancelTeleportTask(); // TODO: find a better solution for this
    }

    public void setStaying() {
        movementState = "Staying";
        scheduleTeleportTask(); // TODO: find a better solution for this
    }

    private void scheduleTeleportTask() {
        if (teleportTaskID != -1) {
            return;
        }

        Entity entity = Bukkit.getEntity(uniqueID);

        if (entity != null) {
            stayingLocation = entity.getLocation();

            double secondsUntilRepeat = 0.1;
            teleportTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(WildPets.getInstance(), new Runnable() {
                @Override
                public void run() {
                    float yaw = entity.getLocation().getYaw();
                    float pitch = entity.getLocation().getPitch();
                    entity.teleport(new Location(stayingLocation.getWorld(), stayingLocation.getX(), stayingLocation.getY(), stayingLocation.getZ(), yaw, pitch));
                }
            }, 0, (int)(secondsUntilRepeat * 20));
        }
    }

    private void cancelTeleportTask() {
        Bukkit.getScheduler().cancelTask(teleportTaskID);
        teleportTaskID = -1;
    }

}