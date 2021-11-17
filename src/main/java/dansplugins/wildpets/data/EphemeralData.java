package dansplugins.wildpets.data;

import dansplugins.wildpets.objects.Pet;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.HashMap;

public class EphemeralData {

    private static EphemeralData instance;

    // action lists
    private HashSet<Player> tamingPlayers = new HashSet<>();
    private HashSet<Player> selectingPlayers = new HashSet<>();
    private HashSet<Player> lockingPlayers = new HashSet<>();
    private HashSet<Player> unlockingPlayers = new HashSet<>();
    private HashSet<Player> accessCheckingPlayers = new HashSet<>();
    private HashSet<Player> accessGrantingPlayers = new HashSet<>(); // TODO: add associated methods
    private HashSet<Player> accessRevokingPlayers = new HashSet<>(); // TODO: add associated methods

    // selections list
    private HashMap<Player, Pet> selections = new HashMap<>();

    // cooldown lists
    private HashSet<Player> playersWithRightClickCooldown = new HashSet<>();

    private EphemeralData() {

    }

    public static EphemeralData getInstance() {
        if (instance == null) {
            instance = new EphemeralData();
        }
        return instance;
    }

    // -----

    public void setPlayerAsTaming(Player player) {
        if (!isPlayerTaming(player)) {
            clearPlayerFromActionLists(player);
            tamingPlayers.add(player);
        }
    }

    public void setPlayerAsNotTaming(Player player) {
        tamingPlayers.remove(player);
    }

    public boolean isPlayerTaming(Player player) {
        return tamingPlayers.contains(player);
    }

    // -----

    public void setPlayerAsSelecting(Player player) {
        if (!isPlayerSelecting(player)) {
            clearPlayerFromActionLists(player);
            selectingPlayers.add(player);
        }
    }

    public void setPlayerAsNotSelecting(Player player) {
        selectingPlayers.remove(player);
    }

    public boolean isPlayerSelecting(Player player) {
        return selectingPlayers.contains(player);
    }


    // -----

    public void setPlayerAsLocking(Player player) {
        lockingPlayers.add(player);
    }

    public void setPlayerAsNotLocking(Player player) {
        lockingPlayers.remove(player);
    }

    public boolean isPlayerLocking(Player player) {
        return lockingPlayers.contains(player);
    }

    // -----

    public void setPlayerAsUnlocking(Player player) {
        unlockingPlayers.add(player);
    }

    public void setPlayerAsNotUnlocking(Player player) {
        unlockingPlayers.remove(player);
    }

    public boolean isPlayerUnlocking(Player player) {
        return unlockingPlayers.contains(player);
    }

    // -----

    public void setPlayerAsCheckingAccess(Player player) {
        accessCheckingPlayers.add(player);
    }

    public void setPlayerAsNotCheckingAccess(Player player) {
        accessCheckingPlayers.remove(player);
    }

    public boolean isPlayerCheckingAccess(Player player) {
        return accessCheckingPlayers.contains(player);
    }

    // -----

    public void selectPetForPlayer(Pet pet, Player player) {
        if (!selections.containsKey(player)) {
            selections.put(player, pet);
        }
        else {
            selections.replace(player, pet);
        }
    }

    public Pet getPetSelectionForPlayer(Player player) {
        return selections.getOrDefault(player, null);
    }

    public void clearPetSelectionForPlayer(Player player) {
        selections.remove(player);
    }


    // -----

    public void setRightClickCooldown(Player player, boolean flag) {
        if (flag) {
            if (!playersWithRightClickCooldown.contains(player)) {
                playersWithRightClickCooldown.add(player);
            }
        }
        else {
            playersWithRightClickCooldown.remove(player);
        }
    }

    public boolean hasRightClickCooldown(Player player) {
        return playersWithRightClickCooldown.contains(player);
    }

    // ----- private methods

    private void clearPlayerFromActionLists(Player player) {
        setPlayerAsNotTaming(player);
        setPlayerAsNotSelecting(player);
    }

}
