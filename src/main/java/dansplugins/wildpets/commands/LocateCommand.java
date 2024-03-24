package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.location.WpLocation;
import dansplugins.wildpets.pet.Pet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

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

    public LocateCommand(EphemeralData ephemeralData) {
        super(new ArrayList<>(Arrays.asList("locate")), new ArrayList<>(Arrays.asList("wp.locate")));
        this.ephemeralData = ephemeralData;
    }

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        Pet pet = ephemeralData.getPetSelectionForPlayer(player.getUniqueId());

        if (pet == null) {
            player.sendMessage(ChatColor.RED + "No pet selected.");
            return false;
        }

        Entity entity = Bukkit.getEntity(pet.getUniqueID());

        if (entity != null) {
            Location bukkitLocation = entity.getLocation();
            WpLocation wpLocation = new WpLocation(bukkitLocation.getX(), bukkitLocation.getY(), bukkitLocation.getZ());
            pet.setLastKnownLocation(wpLocation);
        }

        WpLocation lastKnownLocation = pet.getLastKnownLocation();

        player.sendMessage(ChatColor.AQUA + pet.getName() + String.format("'s last known location is (%s, %s, %s).", lastKnownLocation.getX(), lastKnownLocation.getY(), lastKnownLocation.getZ()));
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}