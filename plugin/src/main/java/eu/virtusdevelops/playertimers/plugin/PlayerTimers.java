package eu.virtusdevelops.playertimers.plugin;

import eu.virtusdevelops.playertimers.api.PlayerTimersAPI;
import eu.virtusdevelops.playertimers.api.controllers.TimersController;
import eu.virtusdevelops.playertimers.core.controllers.TimerControllerImpl;
import eu.virtusdevelops.playertimers.plugin.commands.CommandsRegistry;
import eu.virtusdevelops.playertimers.plugin.controllers.PlaceholdersController;
import eu.virtusdevelops.playertimers.core.storage.SQLStorage;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.minecraft.extras.caption.ComponentCaptionFormatter;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import static net.kyori.adventure.text.Component.text;

public final class PlayerTimers extends JavaPlugin implements eu.virtusdevelops.playertimers.api.PlayerTimers {

    private MinecraftHelp<CommandSender> minecraftHelp;

    private TimerControllerImpl timerController;
    private SQLStorage storage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            storage = new SQLStorage(this);
        } catch (InvalidConfigurationException e) {
            // throw error
        }
        timerController = new TimerControllerImpl(this, storage);
        setupCommands();

        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new PlaceholdersController(this).register();
        }

        PlayerTimersAPI.load(this);
    }

    @Override
    public void onDisable() {
        timerController.stop();
    }


    private void setupCommands(){
        var bukkitAudiences = BukkitAudiences.create(this);
        final LegacyPaperCommandManager<CommandSender> manager = LegacyPaperCommandManager.createNative(
                this,
                ExecutionCoordinator.simpleCoordinator()
        );

        if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            manager.registerBrigadier();
        } else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            manager.registerAsynchronousCompletions();
        }

        MinecraftExceptionHandler.create(bukkitAudiences::sender)
                .defaultHandlers()
                .decorator(
                        component -> text()
                            .append(text("[", NamedTextColor.DARK_GRAY))
                            .append(text("Timers", NamedTextColor.GOLD))
                            .append(text("] ", NamedTextColor.DARK_GRAY))
                            .append(component)
                            .build()
                )
                .registerTo(manager);

        this.minecraftHelp = MinecraftHelp.<CommandSender>builder()
                .commandManager(manager)
                .audienceProvider(bukkitAudiences::sender)
                .commandPrefix("/ptimers help")
                .messageProvider(MinecraftHelp.captionMessageProvider(
                        manager.captionRegistry(),
                        ComponentCaptionFormatter.miniMessage()
                ))
                .build();
        manager.captionRegistry().registerProvider(MinecraftHelp.defaultCaptionsProvider());

        new CommandsRegistry(this, manager);
    }


    public MinecraftHelp<CommandSender> getMinecraftHelp() {
        return minecraftHelp;
    }


    @Override
    public TimersController getTimersController() {
        return timerController;
    }
}
