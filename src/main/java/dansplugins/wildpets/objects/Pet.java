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
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Pet {

    private UUID uniqueID; // saved
    private UUID ownerUUID; // saved
    private int assignedID;
    private String name; // saved
    private String movementState; // saved
    private int lastKnownX = -1;
    private int lastKnownY = -1;
    private int lastKnownZ = -1;

    private Location stayingLocation;
    private int schedulerTaskID = -1;
    private int scheduleAttempts = 0;
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

        if (WildPets.getInstance().isDebugEnabled()) {
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
        if (WildPets.getInstance().isDebugEnabled()) {
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
        attemptToScheduleTeleportTask(); // TODO: find a better solution for this
    }

    public void setFollowing() {
        if (movementState != null && movementState.equals("Staying")) {
            cancelTeleportTask();
        }
        movementState = "Following";
    }

    public String getMovementState() {
        return movementState;
    }

    // scheduling methods ----------------------------------

    private void attemptToScheduleTeleportTask() {
        if (teleportTaskID != -1) {
            return;
        }

        if (WildPets.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Attempting to scheduling teleport task for " + getName() + "."); }

        int secondsUntilRepeat = WildPets.getInstance().getConfig().getInt("configOptions." + "secondsBetweenSchedulingAttempts");
        schedulerTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(WildPets.getInstance(), new Runnable() {
            @Override
            public void run() {
                Entity entity = Bukkit.getEntity(uniqueID);

                if (entity != null) {
                    if (teleportTaskID != -1) {
                        if (WildPets.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Teleport task has already been scheduled! Cancelling scheduling task!"); }
                        cancelSchedulingTask();
                        return;
                    }
                    if (WildPets.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] The entity " + getName() + " was found! Scheduling teleport now."); }
                    scheduleTeleport(entity);

                    cancelSchedulingTask();
                }
                else {
                    if (WildPets.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] The entity '" + getName() + "' cannot be found! Cannot schedule teleport task. Will retry in " + secondsUntilRepeat + " seconds."); }
                    scheduleAttempts++;

                    int maxScheduleAttempts = WildPets.getInstance().getConfig().getInt("configOptions." + "maxScheduleAttempts");
                    if (scheduleAttempts > maxScheduleAttempts) {
                        if (WildPets.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] The entity '" + getName() + "' wasn't able to be found more than " + maxScheduleAttempts + " times. Cannot schedule."); }
                        cancelSchedulingTask();
                    }
                }
            }
        }, 0, secondsUntilRepeat * 20);
    }

    private void cancelSchedulingTask() {
        if (WildPets.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Cancelling scheduling task with ID " + schedulerTaskID); }
        Bukkit.getScheduler().cancelTask(schedulerTaskID);
        schedulerTaskID = -1;
    }

    private void scheduleTeleport(Entity entity) {
        stayingLocation = entity.getLocation();

        double secondsUntilRepeat = WildPets.getInstance().getConfig().getDouble("configOptions." + "secondsBetweenStayTeleports");
        teleportTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(WildPets.getInstance(), new Runnable() {
            @Override
            public void run() {
                float yaw = entity.getLocation().getYaw();
                float pitch = entity.getLocation().getPitch();
                entity.teleport(new Location(stayingLocation.getWorld(), stayingLocation.getX(), stayingLocation.getY(), stayingLocation.getZ(), yaw, pitch));
            }
        }, 0, (int)(secondsUntilRepeat * 20));
        if (WildPets.getInstance().isDebugEnabled()) {
            if (teleportTaskID != -1) {
                System.out.println("[DEBUG] Scheduled!");
            }
        }
    }

    private void cancelTeleportTask() {
        Bukkit.getScheduler().cancelTask(teleportTaskID);
        teleportTaskID = -1;
    }

    // end of scheduling methods ----------------------------------

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
        lastKnownX = Integer.parseInt(gson.fromJson(data.getOrDefault("lastKnownX", "-1"), String.class));
        lastKnownY = Integer.parseInt(gson.fromJson(data.getOrDefault("lastKnownY", "-1"), String.class));
        lastKnownZ = Integer.parseInt(gson.fromJson(data.getOrDefault("lastKnownZ", "-1"), String.class));

        String state = gson.fromJson(data.get("movementState"), String.class);

        if (state.equalsIgnoreCase("Wandering")) {
            setWandering();
        }
        else if (state.equalsIgnoreCase("Staying")) {
            setStaying();
        }
        else if (state.equalsIgnoreCase("Following")) {
            setFollowing();
        }
    }
}