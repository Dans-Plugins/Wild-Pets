package dansplugins.wildpets.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.utils.UUIDChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Pet {

    private boolean debug = WildPets.getInstance().isDebugEnabled();

    private UUID uniqueID; // saved
    private UUID ownerUUID; // saved
    private int assignedID;
    private String name; // saved
    private String movementState; // saved
    private int lastKnownX = -1;
    private int lastKnownY = -1;
    private int lastKnownZ = -1;

    private Location stayingLocation;
    private int teleportTaskID = -1;

    public Pet(Entity entity, UUID playerOwner) {
        uniqueID = entity.getUniqueId();
        ownerUUID = playerOwner;
        assignedID = PersistentData.getInstance().getPetList(ownerUUID).getNewID();
        name = UUIDChecker.getInstance().findPlayerNameBasedOnUUID(ownerUUID) + "'s_Pet_" + assignedID;
        movementState = "Wandering";
        setLastKnownLocation(entity.getLocation());

        entity.setCustomName(ChatColor.GREEN + name);
        entity.setPersistent(true);
        // entity.setInvulnerable(true); // this has been replaced by the DamageEffectsAndDeathHandler class

        entity.playEffect(EntityEffect.LOVE_HEARTS);

        if (debug) {
            System.out.println("[DEBUG] Pet instantiated!");
        }
    }

    public Pet(Map<String, String> petData) {
        this.load(petData);
    }

    public void deconstruct() {
        Entity entity = Bukkit.getEntity(uniqueID);

        if (entity != null) {
            entity.setCustomName("");
            entity.setPersistent(false);
            entity.setInvulnerable(false);
        }
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
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

    public int getAssignedID() {
        return assignedID;
    }

    public void sendInfoToPlayer(Player player) {
        player.sendMessage(ChatColor.AQUA + "=== Pet Info ===");
        player.sendMessage(ChatColor.AQUA + "Name: " + name);
        player.sendMessage(ChatColor.AQUA + "Owner: " + UUIDChecker.getInstance().findPlayerNameBasedOnUUID(ownerUUID));
        player.sendMessage(ChatColor.AQUA + "State: " + movementState);
        if (debug) {
            player.sendMessage(ChatColor.AQUA + "[DEBUG] uniqueID: " + uniqueID.toString());
            player.sendMessage(ChatColor.AQUA + "[DEBUG] ownerUUID: " + ownerUUID.toString());
            player.sendMessage(ChatColor.AQUA + "[DEBUG] assignedID: " + assignedID);
        }
    }

    public void sendLocationToPlayer(Player player) {
        Entity entity = Bukkit.getEntity(uniqueID);

        if (entity != null) {
            setLastKnownLocation(entity.getLocation());
        }

        player.sendMessage(ChatColor.AQUA + getName() + String.format("'s last known location is (%s, %s, %s).", lastKnownX, lastKnownY, lastKnownZ));
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

    private void setLastKnownLocation(Location location) {
        lastKnownX = (int) location.getX();
        lastKnownY = (int) location.getY();
        lastKnownZ = (int) location.getZ();
    }

    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();;

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("uniqueID", gson.toJson(uniqueID));
        saveMap.put("owner", gson.toJson(ownerUUID));
        saveMap.put("assignedID", gson.toJson(assignedID));
        saveMap.put("name", gson.toJson(name));
        saveMap.put("movementState", gson.toJson(movementState));
        saveMap.put("lastKnownX", gson.toJson(lastKnownX));
        saveMap.put("lastKnownY", gson.toJson(lastKnownY));
        saveMap.put("lastKnownZ", gson.toJson(lastKnownZ));


        return saveMap;
    }

    private void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        uniqueID = UUID.fromString(gson.fromJson(data.get("uniqueID"), String.class));
        ownerUUID = UUID.fromString(gson.fromJson(data.get("owner"), String.class));
        assignedID = Integer.parseInt(gson.fromJson(data.get("assignedID"), String.class));
        name = gson.fromJson(data.get("name"), String.class);
        lastKnownX = Integer.parseInt(gson.fromJson(data.get("lastKnownX"), String.class));
        lastKnownY = Integer.parseInt(gson.fromJson(data.get("lastKnownY"), String.class));
        lastKnownZ = Integer.parseInt(gson.fromJson(data.get("lastKnownZ"), String.class));

        String state = gson.fromJson(data.get("movementState"), String.class);

        if (state.equalsIgnoreCase("Wandering")) {
            setWandering();
        }
        else if (state.equalsIgnoreCase("Staying")) {
            setStaying();
        }
    }

}