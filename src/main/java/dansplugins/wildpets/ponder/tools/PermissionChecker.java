package dansplugins.wildpets.ponder.tools;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

/**
 * Utility class for checking command permissions.
 */
public class PermissionChecker {

    /**
     * @param sender The sender of the command.
     * @param permissions The permissions to check.
     * @return Whether the sender has one of the specified permissions.
     */
    public boolean checkPermission(CommandSender sender, ArrayList<String> permissions) {
        for (String permission : permissions) {
            if (checkPermission(sender, permission)) {
                return true;
            }
        }
        informSenderTheyDoNotHaveOneOfTheRequiredPermissions(sender, permissions);
        return false;
    }

    /**
     * @param sender The sender of the command.
     * @param permission The permission to check.
     * @return Whether the sender has the specified permission.
     */
    public boolean checkPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    private void informSenderTheyDoNotHaveOneOfTheRequiredPermissions(CommandSender sender, ArrayList<String> permissions) {
        sender.sendMessage(ChatColor.RED + "In order to use this command, you need one of the following permissions: '" + permissions + "'");
    }
}
