package dansplugins.wildpets;

import dansplugins.wildpets.bstats.Metrics;
import dansplugins.wildpets.commands.*;
import dansplugins.wildpets.eventhandlers.DamageEffectsAndDeathHandler;
import dansplugins.wildpets.eventhandlers.InteractionHandler;
import dansplugins.wildpets.eventhandlers.JoinAndQuitHandler;
import dansplugins.wildpets.eventhandlers.MoveHandler;
import dansplugins.wildpets.managers.ConfigManager;
import dansplugins.wildpets.managers.EntityConfigManager;
import dansplugins.wildpets.managers.StorageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import preponderous.ponder.AbstractPonderPlugin;
import preponderous.ponder.misc.PonderAPI_Integrator;
import preponderous.ponder.misc.specification.ICommand;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public final class WildPets extends AbstractPonderPlugin {
  
    private static WildPets instance;

    public static WildPets getInstance() {
        return instance;
    }

    private final String version = "v1.2";

    @Override
    public void onEnable() {
        instance = this;
        ponderAPI_integrator = new PonderAPI_Integrator(this);
        toolbox = getPonderAPI().getToolbox();
        registerEventHandlers();
        initializeCommandService();
        getPonderAPI().setDebug(false);

        // create/load config
        if (!(new File("./plugins/WildPets/config.yml").exists())) {
            EntityConfigManager.getInstance().initializeWithDefaults();
            ConfigManager.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                ConfigManager.getInstance().saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
            EntityConfigManager.getInstance().initializeWithConfig();
        }

        // schedule auto save
        Scheduler.getInstance().scheduleAutosave();

        // load save files
        StorageManager.getInstance().load();

        // bStats
        int pluginId = 12332;
        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {
        StorageManager.getInstance().save();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            DefaultCommand defaultCommand = new DefaultCommand();
            return defaultCommand.execute(sender);
        }

        return getPonderAPI().getCommandService().interpretCommand(sender, label, args);
    }

    public String getVersion() {
        return version;
    }

    public boolean isDebugEnabled() {
        return getConfig().getBoolean("configOptions.debugMode");
    }

    public boolean isVersionMismatched() {
        return !getConfig().getString("version").equalsIgnoreCase(getVersion());
    }

    private void registerEventHandlers() {
        ArrayList<Listener> listeners = new ArrayList<>();
        listeners.add(new DamageEffectsAndDeathHandler());
        listeners.add(new InteractionHandler());
        listeners.add(new JoinAndQuitHandler());
        listeners.add(new MoveHandler());
        getToolbox().getEventHandlerRegistry().registerEventHandlers(listeners, this);
    }

    private void initializeCommandService() {
        ArrayList<ICommand> commands = new ArrayList<>(Arrays.asList(
                new CallCommand(), new CheckAccessCommand(), new ConfigCommand(),
                new FollowCommand(), new GrantAccessCommand(), new HelpCommand(),
                new InfoCommand(), new ListCommand(), new LocateCommand(),
                new LockCommand(), new RenameCommand(), new RevokeAccessCommand(),
                new SelectCommand(), new SetFreeCommand(), new StatsCommand(),
                new StayCommand(), new TameCommand(), new UnlockCommand(),
                new WanderCommand()
        ));
        getPonderAPI().getCommandService().initialize(commands, "That command wasn't found.");
    }
}