package dansplugins.wildpets.objects;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Pet {

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

        entity.setCustomName(name);
        entity.setPersistent(true);
        entity.setInvulnerable(true);
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
    }

}
