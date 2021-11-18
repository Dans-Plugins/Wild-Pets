package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.managers.ConfigManager;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class RenameCommand extends AbstractCommand {

    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("rename"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("wp.rename"));

    @Override
    public ArrayList<String> getNames() {
        return names;
    }

    @Override
    public ArrayList<String> getPermissions() {
        return permissions;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /wp rename (new name)");
            return false;
        }

        String newName = args[0];

        Pet pet = EphemeralData.getInstance().getPetSelectionForPlayer(player.getUniqueId());

        if (pet == null) {
            player.sendMessage(ChatColor.RED + "No pet selected.");
            return false;
        }

        if (PersistentData.getInstance().getPetList(player.getUniqueId()).isNameTaken(newName)) {
            player.sendMessage(ChatColor.RED + "That name is already taken by one of your pets.");
            return false;
        }

        if (newName.equalsIgnoreCase("cancel")) {
            player.sendMessage(ChatColor.RED + "You can't name your pet 'cancel'.");
            return false;
        }

        int characterLimit = ConfigManager.getInstance().getInt("petNameCharacterLimit");
        if (newName.length() > characterLimit) {
            player.sendMessage(ChatColor.RED + "Your pet's name can't contain more than " + characterLimit + " characters.");
            return false;
        }

        pet.setName(newName);
        player.sendMessage(ChatColor.GREEN + "Renamed.");
        return true;
    }

}
