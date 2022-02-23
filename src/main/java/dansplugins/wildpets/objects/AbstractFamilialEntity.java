package dansplugins.wildpets.objects;

import java.util.HashSet;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public abstract class AbstractFamilialEntity {
    protected HashSet<UUID> parentIDs = new HashSet<>();
    protected HashSet<UUID> childIDs = new HashSet<>();

    public HashSet<UUID> getParentUUIDs() {
        return parentIDs;
    }

    public boolean addParent(UUID uuid) {
        return parentIDs.add(uuid);
    }

    public boolean removeParent(UUID uuid) {
        return parentIDs.remove(uuid);
    }

    public HashSet<UUID> getChildUUIDs() {
        return childIDs;
    }

    public boolean addChild(UUID uuid) {
        return childIDs.add(uuid);
    }

    public boolean removeChild(UUID uuid) {
        return childIDs.remove(uuid);
    }
}