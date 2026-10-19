package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.location.WpLocation;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.utils.MessageFormat;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class LocateCommand extends AbstractPluginCommand {
    private final EphemeralData ephemeralData;
    private final ServerProvider serverProvider;

    public LocateCommand(EphemeralData ephemeralData) {
        this(ephemeralData, new ServerProvider());
    }

    public LocateCommand(EphemeralData ephemeralData, ServerProvider serverProvider) {
        super(new ArrayList<>(Arrays.asList("locate")), new ArrayList<>(Arrays.asList("wp.locate")));
        this.ephemeralData = ephemeralData;
        this.serverProvider = serverProvider;
    }

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        Pet pet = ephemeralData.getPetSelectionForPlayer(player.getUniqueId());

        if (pet == null) {
            player.sendMessage(ChatColor.RED + "No pet selected.");
            return false;
        }

        Entity entity = serverProvider.get().getEntity(pet.getUniqueID());

        if (entity != null) {
            Location bukkitLocation = entity.getLocation();
            WpLocation wpLocation = new WpLocation(bukkitLocation.getX(), bukkitLocation.getY(), bukkitLocation.getZ());
            pet.setLastKnownLocation(wpLocation);
        }

        WpLocation lastKnownLocation = pet.getLastKnownLocation();

        player.sendMessage("");
        player.sendMessage(MessageFormat.header("Wild Pets", "Locate"));
        player.sendMessage(MessageFormat.line(ChatColor.GRAY + "Pet:      " + ChatColor.WHITE + pet.getName()));
        player.sendMessage(MessageFormat.line(ChatColor.GRAY + "Location: " + ChatColor.WHITE + String.format("(%s, %s, %s)", lastKnownLocation.getX(), lastKnownLocation.getY(), lastKnownLocation.getZ())));
        player.sendMessage(MessageFormat.footer());
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
