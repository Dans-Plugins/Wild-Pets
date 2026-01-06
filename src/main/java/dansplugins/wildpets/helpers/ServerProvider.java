package dansplugins.wildpets.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Server;

/**
 * Provides access to the Bukkit server instance.
 * This wrapper allows for easier mocking in tests.
 */
public class ServerProvider {

    public Server get() {
        return Bukkit.getServer();
    }
}
