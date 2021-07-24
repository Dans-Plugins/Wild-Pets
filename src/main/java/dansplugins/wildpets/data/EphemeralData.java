package dansplugins.wildpets.data;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class EphemeralData {

    private static EphemeralData instance;

    private ArrayList<Player> tamingPlayers = new ArrayList<>();

    private EphemeralData() {

    }

    public static EphemeralData getInstance() {
        if (instance == null) {
            instance = new EphemeralData();
        }
        return instance;
    }

    public void setPlayerAsTaming(Player player) {
        if (!tamingPlayers.contains(player)) {
            tamingPlayers.add(player);
        }
    }

    public void setPlayerAsNotTaming(Player player) {
        tamingPlayers.remove(player);
    }

    public boolean isPlayerTaming(Player player) {
        return tamingPlayers.contains(player);
    }

}
