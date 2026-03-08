package dansplugins.wildpets.helpers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Utility class for looking up player names and UUIDs.
 */
public class UUIDChecker {

    /**
     * Method to obtain the name of a player based on their UUID.
     *
     * @param playerUUID used to find the name.
     * @return Name of the player as a {@link String}
     * @throws IllegalArgumentException if the UUID provided is null.
     */
    public String findPlayerNameBasedOnUUID(UUID playerUUID) {
        if (playerUUID == null) {
            throw new IllegalArgumentException("Player UUID cannot be null!");
        }
        final Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            return player.getName();
        }
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        final String name = offlinePlayer.getName();
        return name == null || !offlinePlayer.hasPlayedBefore() ? "" : name;
    }

    /**
     * Method to obtain the UUID of a player based on their known name.
     *
     * @param playerName used to find the UUID.
     * @return {@link UUID} (Unique ID) of the Player.
     * @throws IllegalArgumentException if the name is null.
     */
    @SuppressWarnings("deprecation")
    public UUID findUUIDBasedOnPlayerName(String playerName) {
        if (playerName == null) {
            throw new IllegalArgumentException("Player Name cannot be null!");
        }
        final Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            return player.getUniqueId();
        }
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        final String name = offlinePlayer.getName();
        return name == null || !offlinePlayer.hasPlayedBefore() ? null : offlinePlayer.getUniqueId();
    }
}
