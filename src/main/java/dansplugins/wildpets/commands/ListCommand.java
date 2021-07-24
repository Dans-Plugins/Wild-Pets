package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.PersistentData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommand {

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        PersistentData.getInstance().sendListOfPetsToPlayer(player);
        return true;
    }

}
