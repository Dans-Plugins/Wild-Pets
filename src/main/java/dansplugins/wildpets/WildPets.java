package dansplugins.wildpets;

import dansplugins.wildpets.bstats.Metrics;
import dansplugins.wildpets.commands.*;
import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.eventhandlers.*;
import dansplugins.wildpets.services.ConfigService;
import dansplugins.wildpets.services.EntityConfigService;
import dansplugins.wildpets.services.StorageService;
import dansplugins.wildpets.utils.Scheduler;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.abs.PonderBukkitPlugin;
import preponderous.ponder.minecraft.bukkit.services.CommandService;
import preponderous.ponder.minecraft.bukkit.tools.EventHandlerRegistry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public final class WildPets extends PonderBukkitPlugin {
    private final String pluginVersion = "v" + getDescription().getVersion();
    private CommandService commandService = new CommandService(getPonder());

    private final EphemeralData ephemeralData = new EphemeralData();
    private final ConfigService configService = new ConfigService(this, entityConfigService); // TODO: fix circular dependency
    private final PersistentData persistentData = new PersistentData(this, configService);
    private final StorageService storageService = new StorageService(configService, this, persistentData);
    private final EntityConfigService entityConfigService = new EntityConfigService(this, configService);
    private final Scheduler scheduler = new Scheduler(this, ephemeralData, storageService);


    
     /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        registerEventHandlers();
        initializeCommandService();
        initializeConfig();
        scheduler.scheduleAutosave();
        storageService.load();
        handlebStatsIntegration();
    }

    private void initializeConfig() {
        if (configFileExists()) {
            performCompatibilityChecks();
        }
        else {
            entityConfigService.initializeWithDefaults();
            configService.saveMissingConfigDefaultsIfNotPresent();
        }
    }

    private boolean configFileExists() {
        return new File("./plugins/" + getName() + "/config.yml").exists();
    }

    private void performCompatibilityChecks() {
        if (isVersionMismatched()) {
            configService.saveMissingConfigDefaultsIfNotPresent();
        }
        reloadConfig();
        entityConfigService.initializeWithConfig();
    }

    /**
     * This runs when the server stops.
     */
    @Override
    public void onDisable() {
        storageService.save();
    }

    /**
     * This method handles commands sent to the minecraft server and interprets them if the label matches one of the core commands.
     * @param sender The sender of the command.
     * @param cmd The command that was sent. This is unused.
     * @param label The core command that has been invoked.
     * @param args Arguments of the core command. Often sub-commands.
     * @return A boolean indicating whether the execution of the command was successful.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            DefaultCommand defaultCommand = new DefaultCommand(this);
            return defaultCommand.execute(sender);
        }

        return commandService.interpretAndExecuteCommand(sender, label, args);
    }

    /**
     * This can be used to get the version of the plugin.
     * @return A string containing the version preceded by 'v'
     */
    public String getVersion() {
        return pluginVersion;
    }

    /**
     * Checks if debug is enabled.
     * @return Whether debug is enabled.
     */
    public boolean isDebugEnabled() {
        return configService.getBoolean("debugMode");
    }

    /**
     * Checks if the version is mismatched.
     * @return A boolean indicating if the version is mismatched.
     */
    public boolean isVersionMismatched() {
        String configVersion = this.getConfig().getString("version");
        if (configVersion == null || this.getVersion() == null) {
            return false;
        } else {
            return !configVersion.equalsIgnoreCase(this.getVersion());
        }
    }

    private void handlebStatsIntegration() {
        int pluginId = 12332;
        new Metrics(this, pluginId);
    }

    /**
     * Registers the event handlers of the plugin using Ponder.
     */
    private void registerEventHandlers() {
        ArrayList<Listener> listeners = new ArrayList<>();
        listeners.add(new DamageEffectsAndDeathHandler(configService, persistentData, this));
        listeners.add(new InteractionHandler(entityConfigService, persistentData, ephemeralData, this, configService, scheduler));
        listeners.add(new JoinAndQuitHandler(persistentData, ephemeralData));
        listeners.add(new MoveHandler(persistentData));
        listeners.add(new BreedEventHandler(persistentData, configService, ephemeralData));
        EventHandlerRegistry eventHandlerRegistry = new EventHandlerRegistry();
        eventHandlerRegistry.registerEventHandlers(listeners, this);
    }

    /**
     * Initializes Ponder's command service with the plugin's commands.
     */
    private void initializeCommandService() {
        ArrayList<AbstractPluginCommand> commands = new ArrayList<>(Arrays.asList(
                new CallCommand(ephemeralData), new CheckAccessCommand(ephemeralData), new ConfigCommand(configService),
                new FollowCommand(ephemeralData), new HelpCommand(configService),
                new InfoCommand(ephemeralData), new ListCommand(persistentData), new LocateCommand(ephemeralData),
                new LockCommand(ephemeralData), new RenameCommand(ephemeralData, persistentData, configService),
                new SelectCommand(configService, ephemeralData, persistentData), new SetFreeCommand(ephemeralData, persistentData), new StatsCommand(persistentData),
                new TameCommand(ephemeralData), new UnlockCommand(ephemeralData), new WanderCommand(ephemeralData),
                new GatherCommand(persistentData)
        ));
        commandService.initialize(commands, "That command wasn't found.");
    }
}