package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TameCommand {

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        EphemeralData.getInstance().setPlayerAsTaming(player);
        return true;
    }

}
