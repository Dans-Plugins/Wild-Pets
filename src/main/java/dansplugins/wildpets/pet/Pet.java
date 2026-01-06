
package dansplugins.wildpets.pet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.location.WpLocation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import preponderous.ponder.misc.abs.Lockable;
import preponderous.ponder.misc.abs.Savable;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Daniel McCoy Stephenson
 */
public class Pet extends AbstractFamilialEntity implements Lockable<UUID>, Savable {

    private UUID uniqueID;
    private UUID ownerUUID;
    private int assignedID;
    private String name;
    private String movementState;
    private WpLocation lastKnownLocation;
    private boolean locked = false;
    private HashSet<UUID> accessList = new HashSet<>();
    private ServerProvider serverProvider;

    public Pet(UUID entityUniqueId, UUID playerOwnerUniqueId, String playerOwnerName) {
        this(entityUniqueId, playerOwnerUniqueId, playerOwnerName, new ServerProvider());
    }

    public Pet(UUID entityUniqueId, UUID playerOwnerUniqueId, String playerOwnerName, ServerProvider serverProvider) {
        uniqueID = entityUniqueId;
        ownerUUID = playerOwnerUniqueId;
        name = playerOwnerName + "'s_Pet";
        movementState = "Wandering";
        accessList.add(playerOwnerUniqueId);
        setLastKnownLocation(new WpLocation(0, 0, 0));
        this.serverProvider = serverProvider;
    }

    public Pet(Map<String, String> petData) {
        this(petData, new ServerProvider());
    }

    public Pet(Map<String, String> petData, ServerProvider serverProvider) {
        this.serverProvider = serverProvider;
        this.load(petData);
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
    }

    public int getAssignedID() {
        return assignedID;
    }

    public void setAssignedID(int assignedID) {
        this.assignedID = assignedID;
    }

    public void setWandering() {
        movementState = "Wandering";
        applyAIState();
    }

    public void setFollowing() {
        movementState = "Following";
        applyAIState();
    }

    public void setStaying() {
        movementState = "Staying";
        applyAIState();
    }

    /**
     * Applies the appropriate AI state based on the pet's movement state.
     * Disables AI for pets in stay mode, enables it for follow and wander modes.
     * Returns silently if the entity is not currently loaded or is not a {@link Mob} instance.
     */
    public void applyAIState() {
        if (serverProvider == null) {
            // Server provider not available; cannot apply AI state safely
            return;
        }
        var server = serverProvider.get();
        if (server == null) {
            // Server not available; cannot apply AI state safely
            return;
        }
        Entity entity = server.getEntity(uniqueID);
        if (entity == null) {
            // Entity not found (e.g., in unloaded chunk) - will be applied when entity loads
            return;
        }
        if (entity instanceof Mob) {
            Mob mob = (Mob) entity;
            // Disable AI for stay mode, enable for follow and wander modes
            mob.setAware(!movementState.equals("Staying"));
        }
    }

    public String getMovementState() {
        return movementState;
    }

    public void setLastKnownLocation(WpLocation location) {
        lastKnownLocation = location;
    }

    public WpLocation getLastKnownLocation() {
        return lastKnownLocation;
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

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean b) {
        locked = b;
    }

    public String getParentsUUIDsSeparatedByCommas() {
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

    public String getChildrenUUIDsSeparatedByCommas() {
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

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("uniqueID", gson.toJson(uniqueID));
        saveMap.put("owner", gson.toJson(ownerUUID));
        saveMap.put("assignedID", gson.toJson(assignedID));
        saveMap.put("name", gson.toJson(name));
        saveMap.put("movementState", gson.toJson(movementState));
        saveMap.put("lastKnownX", gson.toJson(lastKnownLocation.getX()));
        saveMap.put("lastKnownY", gson.toJson(lastKnownLocation.getY()));
        saveMap.put("lastKnownZ", gson.toJson(lastKnownLocation.getZ()));
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
        int lastKnownX = Integer.parseInt(gson.fromJson(data.getOrDefault("lastKnownX", "-1"), String.class));
        int lastKnownY = Integer.parseInt(gson.fromJson(data.getOrDefault("lastKnownY", "-1"), String.class));
        int lastKnownZ = Integer.parseInt(gson.fromJson(data.getOrDefault("lastKnownZ", "-1"), String.class));
        lastKnownLocation = new WpLocation(lastKnownX, lastKnownY, lastKnownZ);

        String state = gson.fromJson(data.get("movementState"), String.class);

        if (state.equalsIgnoreCase("Wandering")) {
            setWandering();
        }
        else if (state.equalsIgnoreCase("Following")) {
            setFollowing();
        }
        else if (state.equalsIgnoreCase("Staying")) {
            setStaying();
        }
        else {
            setWandering();
        }

        locked = Boolean.parseBoolean(data.getOrDefault("locked", "false"));
        accessList = gson.fromJson(data.getOrDefault("accessList", "[]"), hashsetTypeUUID);

        parentIDs = gson.fromJson(data.getOrDefault("parentIDs", "[]"), hashsetTypeUUID);
        childIDs =  gson.fromJson(data.getOrDefault("childIDs", "[]"), hashsetTypeUUID);
    }
}