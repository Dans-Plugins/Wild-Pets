package dansplugins.wildpets.data;

import dansplugins.wildpets.objects.Pet;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class EphemeralData {

    private static EphemeralData instance;

    // action lists
    private ArrayList<Player> tamingPlayers = new ArrayList<>();
    private ArrayList<Player> selectingPlayers = new ArrayList<>();

    // selections list
    private HashMap<Player, Pet> selections = new HashMap<>();

    // cooldown lists
    private ArrayList<Player> playersWithRightClickCooldown = new ArrayList<>();

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
