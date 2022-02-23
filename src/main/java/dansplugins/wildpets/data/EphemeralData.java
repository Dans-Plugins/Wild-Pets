package dansplugins.wildpets.data;

import dansplugins.wildpets.objects.Pet;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public class EphemeralData {

    private static EphemeralData instance;

    // action lists
    private HashSet<UUID> tamingPlayers = new HashSet<>();
    private HashSet<UUID> selectingPlayers = new HashSet<>();
    private HashSet<UUID> lockingPlayers = new HashSet<>();
    private HashSet<UUID> unlockingPlayers = new HashSet<>();
    private HashSet<UUID> accessCheckingPlayers = new HashSet<>();
    private HashMap<UUID, UUID> accessGrantingPlayers = new HashMap<>(); // TODO: add associated methods
    private HashMap<UUID, UUID> accessRevokingPlayers = new HashMap<>(); // TODO: add associated methods

    // selections list
    private HashMap<UUID, Pet> selections = new HashMap<>();

    // cooldown lists
    private HashSet<UUID> playersWithRightClickCooldown = new HashSet<>();

    private EphemeralData() {

    }

    public static EphemeralData getInstance() {
        if (instance == null) {
            instance = new EphemeralData();
        }
        return instance;
    }

    // -----

    public void setPlayerAsTaming(UUID player) {
        if (!isPlayerTaming(player)) {
            clearPlayerFromActionLists(player);
            tamingPlayers.add(player);
        }
    }

    public void setPlayerAsNotTaming(UUID player) {
        tamingPlayers.remove(player);
    }

    public boolean isPlayerTaming(UUID player) {
        return tamingPlayers.contains(player);
    }

    // -----

    public void setPlayerAsSelecting(UUID player) {
        if (!isPlayerSelecting(player)) {
            clearPlayerFromActionLists(player);
            selectingPlayers.add(player);
        }
    }

    public void setPlayerAsNotSelecting(UUID player) {
        selectingPlayers.remove(player);
    }

    public boolean isPlayerSelecting(UUID player) {
        return selectingPlayers.contains(player);
    }


    // -----

    public void setPlayerAsLocking(UUID player) {
        lockingPlayers.add(player);
    }

    public void setPlayerAsNotLocking(UUID player) {
        lockingPlayers.remove(player);
    }

    public boolean isPlayerLocking(UUID player) {
        return lockingPlayers.contains(player);
    }

    // -----

    public void setPlayerAsUnlocking(UUID player) {
        unlockingPlayers.add(player);
    }

    public void setPlayerAsNotUnlocking(UUID player) {
        unlockingPlayers.remove(player);
    }

    public boolean isPlayerUnlocking(UUID player) {
        return unlockingPlayers.contains(player);
    }

    // -----

    public void setPlayerAsCheckingAccess(UUID player) {
        accessCheckingPlayers.add(player);
    }

    public void setPlayerAsNotCheckingAccess(UUID player) {
        accessCheckingPlayers.remove(player);
    }

    public boolean isPlayerCheckingAccess(UUID player) {
        return accessCheckingPlayers.contains(player);
    }

    // -----

    public void setPlayerAsGrantingAccess(UUID player, UUID target) {
        accessGrantingPlayers.put(player, target);
    }

    public void setPlayerAsNotGrantingAccess(UUID player) {
        accessGrantingPlayers.remove(player);
    }

    public boolean isPlayerGrantingAccess(UUID player) {
        return accessGrantingPlayers.containsKey(player);
    }

    public UUID getGrantee(UUID player) {
        return accessGrantingPlayers.get(player);
    }

    // -----

    public void setPlayerAsRevokingAccess(UUID player, UUID target) {
        accessRevokingPlayers.put(player, target);
    }

    public void setPlayerAsNotRevokingAccess(UUID player) {
        accessRevokingPlayers.remove(player);
    }

    public boolean isPlayerRevokingAccess(UUID player) {
        return accessRevokingPlayers.containsKey(player);
    }

    public UUID getRevokee(UUID player) {
        return accessRevokingPlayers.get(player);
    }

    // -----

    public void selectPetForPlayer(Pet pet, UUID player) {
        if (!selections.containsKey(player)) {
            selections.put(player, pet);
        }
        else {
            selections.replace(player, pet);
        }
    }

    public Pet getPetSelectionForPlayer(UUID player) {
        return selections.getOrDefault(player, null);
    }

    public void clearPetSelectionForPlayer(UUID player) {
        selections.remove(player);
    }


    // -----

    public void setRightClickCooldown(UUID player, boolean flag) {
        if (flag) {
            if (!playersWithRightClickCooldown.contains(player)) {
                playersWithRightClickCooldown.add(player);
            }
        }
        else {
            playersWithRightClickCooldown.remove(player);
        }
    }

    public boolean hasRightClickCooldown(UUID player) {
        return playersWithRightClickCooldown.contains(player);
    }

    // -----

    public void clearPlayerFromLists(Player player) {
        setPlayerAsNotTaming(player.getUniqueId());
        setPlayerAsNotSelecting(player.getUniqueId());
        setPlayerAsNotLocking(player.getUniqueId());
        setPlayerAsNotCheckingAccess(player.getUniqueId());
        setPlayerAsNotGrantingAccess(player.getUniqueId());
        setPlayerAsNotRevokingAccess(player.getUniqueId());
    }

    // ----- private methods

    private void clearPlayerFromActionLists(UUID player) {
        setPlayerAsNotTaming(player);
        setPlayerAsNotSelecting(player);
    }
}