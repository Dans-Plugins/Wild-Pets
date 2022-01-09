package dansplugins.wildpets.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansplugins.wildpets.Scheduler;
import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.spigot.modifiers.Lockable;
import preponderous.ponder.minecraft.spigot.tools.UUIDChecker;
import preponderous.ponder.misc.Savable;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Daniel McCoy Stephenson
 */
public class Pet extends AbstractFamilialEntity implements Lockable, Savable {

    // persistent
    private UUID uniqueID;
    private UUID ownerUUID;
    private int assignedID;
    private String name;
    private String movementState;
    private int lastKnownX = -1;
    private int lastKnownY = -1;
    private int lastKnownZ = -1;
    private boolean locked = false;
    private HashSet<UUID> accessList = new HashSet<>();

    // ephemeral
    private Location stayingLocation;
    private int schedulerTaskID = -1;
    private int scheduleAttempts = 0;
    private int teleportTaskID = -1;

    public Pet(Entity entity, UUID playerOwner) {
        UUIDChecker uuidChecker = new UUIDChecker();
        uniqueID = entity.getUniqueId();
        ownerUUID = playerOwner;
        assignedID = PersistentData.getInstance().getPetList(ownerUUID).getNewID();
        name = uuidChecker.findPlayerNameBasedOnUUID(ownerUUID) + "'s_Pet_" + assignedID;
        movementState = "Wandering";
        setLastKnownLocation(entity.getLocation());

        entity.setCustomName(ChatColor.GREEN + name);
        entity.setPersistent(true);
        // entity.setInvulnerable(true); // this has been replaced by the DamageEffectsAndDeathHandler class

        entity.playEffect(EntityEffect.LOVE_HEARTS);

        accessList.add(playerOwner);

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

        getPetRecord().setName(name);
    }

    public int getAssignedID() {
        return assignedID;
    }

    public void sendInfoToPlayer(Player player) {
        UUIDChecker uuidChecker = new UUIDChecker();
        player.sendMessage(ChatColor.AQUA + "=== Pet Info ===");
        player.sendMessage(ChatColor.AQUA + "Name: " + name);
        player.sendMessage(ChatColor.AQUA + "Owner: " + uuidChecker.findPlayerNameBasedOnUUID(ownerUUID));
        player.sendMessage(ChatColor.AQUA + "State: " + movementState);
        player.sendMessage(ChatColor.AQUA + "Locked: " + locked);
        if (ConfigManager.getInstance().getBoolean("showLineageInfo")) {
            if (parentIDs.size() > 0) {
                player.sendMessage(ChatColor.AQUA + "Parents: " + getParentNamesSeparatedByCommas());
            }
            if (childIDs.size() > 0) {
                player.sendMessage(ChatColor.AQUA + "Children: " + getChildrenNamesSeparatedByCommas());
            }
        }
        if (WildPets.getInstance().isDebugEnabled()) {
            player.sendMessage(ChatColor.AQUA + "[DEBUG] uniqueID: " + uniqueID.toString());
            player.sendMessage(ChatColor.AQUA + "[DEBUG] ownerUUID: " + ownerUUID.toString());
            player.sendMessage(ChatColor.AQUA + "[DEBUG] assignedID: " + assignedID);
            if (parentIDs.size() > 0) {
                player.sendMessage(ChatColor.AQUA + "[DEBUG] Parents: " + getParentsUUIDsSeparatedByCommas());
            }
            if (childIDs.size() > 0) {
                player.sendMessage(ChatColor.AQUA + "[DEBUG] Children: " + getChildrenUUIDsSeparatedByCommas());
            }
            player.sendMessage(ChatColor.AQUA + "[DEBUG] Pet Record Existent: " + (PersistentData.getInstance().getPetRecord(uniqueID) != null));
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
        Scheduler.getInstance().cancelTeleportTask(this); // TODO: find a better solution for this
    }

    public void setStaying() {
        movementState = "Staying";
        Scheduler.getInstance().attemptToScheduleTeleportTask(this); // TODO: find a better solution for this
    }

    public void setFollowing() {
        if (movementState != null && movementState.equals("Staying")) {
            Scheduler.getInstance().cancelTeleportTask(this);
        }
        movementState = "Following";
    }

    public String getMovementState() {
        return movementState;
    }

    public int getSchedulerTaskID() {
        return schedulerTaskID;
    }

    public void setSchedulerTaskID(int schedulerTaskID) {
        this.schedulerTaskID = schedulerTaskID;
    }

    public int getScheduleAttempts() {
        return scheduleAttempts;
    }

    public void incrementScheduleAttempts() {
        this.scheduleAttempts++;
    }

    public int getTeleportTaskID() {
        return teleportTaskID;
    }

    public void setTeleportTaskID(int teleportTaskID) {
        this.teleportTaskID = teleportTaskID;
    }

    public Location getStayingLocation() {
        return stayingLocation;
    }

    public void setStayingLocation(Location location) {
        stayingLocation = location;
    }

    private void setLastKnownLocation(Location location) {
        lastKnownX = (int) location.getX();
        lastKnownY = (int) location.getY();
        lastKnownZ = (int) location.getZ();
    }

    @Override
    public void setOwner(UUID uuid) {
        this.ownerUUID = uuid;
    }

    @Override
    public UUID getOwner() {
        return ownerUUID;
    }

    @Override
    public void addToAccessList(UUID uuid) {
        accessList.add(uuid);
    }

    @Override
    public void removeFromAccessList(UUID uuid) {
        accessList.remove(uuid);
    }

    @Override
    public boolean hasAccess(UUID uuid) {
        return accessList.contains(uuid);
    }

    @Override
    public ArrayList<UUID> getAccessList() {
        return new ArrayList<>(accessList);
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean b) {
        locked = b;
    }

    public PetRecord getPetRecord() { // should this have @NotNull?
        return PersistentData.getInstance().getPetRecord(uniqueID);
    }

    private String getParentsUUIDsSeparatedByCommas() {
        String toReturn = "";
        int count = 0;
        for (UUID uuid : parentIDs) {
            toReturn = toReturn + uuid.toString();
            count++;
            if (count != parentIDs.size()) {
                toReturn = toReturn + ", ";
            }
        }
        return toReturn;
    }

    private String getChildrenUUIDsSeparatedByCommas() {
        String toReturn = "";
        int count = 0;
        for (UUID uuid : childIDs) {
            toReturn = toReturn + uuid.toString();
            count++;
            if (count != childIDs.size()) {
                toReturn = toReturn + ", ";
            }
        }
        return toReturn;
    }

    private String getParentNamesSeparatedByCommas() {
        String toReturn = "";
        int count = 0;
        for (UUID uuid : parentIDs) {
            PetRecord petRecord = PersistentData.getInstance().getPetRecord(uuid);
            if (petRecord == null) {
                continue;
            }
            toReturn = toReturn + petRecord.getName();
            count++;
            if (count != parentIDs.size()) {
                toReturn = toReturn + ", ";
            }
        }
        return toReturn;
    }

    private String getChildrenNamesSeparatedByCommas() {
        String toReturn = "";
        int count = 0;
        for (UUID uuid : childIDs) {
            PetRecord petRecord = PersistentData.getInstance().getPetRecord(uuid);
            if (petRecord == null) {
                continue;
            }
            toReturn = toReturn + petRecord.getName();
            count++;
            if (count != childIDs.size()) {
                toReturn = toReturn + ", ";
            }
        }
        return toReturn;
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("uniqueID", gson.toJson(uniqueID));
        saveMap.put("owner", gson.toJson(ownerUUID));
        saveMap.put("assignedID", gson.toJson(assignedID));
        saveMap.put("name", gson.toJson(name));
        saveMap.put("movementState", gson.toJson(movementState));
        saveMap.put("lastKnownX", gson.toJson(lastKnownX));
        saveMap.put("lastKnownY", gson.toJson(lastKnownY));
        saveMap.put("lastKnownZ", gson.toJson(lastKnownZ));
        saveMap.put("locked", gson.toJson(locked));
        saveMap.put("accessList", gson.toJson(accessList));
        saveMap.put("parentIDs", gson.toJson(this.parentIDs));
        saveMap.put("childIDs", gson.toJson(this.childIDs));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Type hashsetTypeUUID = new TypeToken<HashSet<UUID>>(){}.getType();

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

        locked = Boolean.parseBoolean(data.getOrDefault("locked", "false"));
        accessList = gson.fromJson(data.getOrDefault("accessList", "[]"), hashsetTypeUUID);

        parentIDs = gson.fromJson(data.getOrDefault("parentIDs", "[]"), hashsetTypeUUID);
        childIDs =  gson.fromJson(data.getOrDefault("childIDs", "[]"), hashsetTypeUUID);
    }
}