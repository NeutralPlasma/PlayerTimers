package eu.virtusdevelops.playertimers;

import eu.virtusdevelops.playertimers.commands.CommandsManager;
import eu.virtusdevelops.playertimers.controllers.PlaceholdersController;
import eu.virtusdevelops.playertimers.controllers.TimerController;
import eu.virtusdevelops.playertimers.storage.SQLStorage;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.minecraft.extras.caption.ComponentCaptionFormatter;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.paper.PaperCommandManager;

import static net.kyori.adventure.text.Component.text;

public final class PlayerTimers extends JavaPlugin {

    private BukkitAudiences bukkitAudiences;
    private MinecraftHelp<CommandSender> minecraftHelp;

    private TimerController timerController;
    private SQLStorage storage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            storage = new SQLStorage(this);
        } catch (InvalidConfigurationException e) {
            // throw error
        }
        timerController = new TimerController(this);
        setupCommands();

        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new PlaceholdersController(this).register();
        }
    }

    @Override
    public void onDisable() {
    }


    private void setupCommands(){
        final LegacyPaperCommandManager<CommandSender> manager = LegacyPaperCommandManager.createNative(
                this,
                ExecutionCoordinator.simpleCoordinator()
        );

        if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            // Register Brigadier mappings for rich completions;
            manager.registerBrigadier();
        } else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            // Use Paper async completions API (see Javadoc for why we don't use this with Brigadier)
            manager.registerAsynchronousCompletions();
        }




        this.bukkitAudiences = BukkitAudiences.create(this);



        MinecraftExceptionHandler.create(this.bukkitAudiences::sender)
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
                .audienceProvider(this.bukkitAudiences()::sender)
                .commandPrefix("/ptimers help")
                .messageProvider(MinecraftHelp.captionMessageProvider(
                        manager.captionRegistry(),
                        ComponentCaptionFormatter.miniMessage()
                ))
                .build();

        manager.captionRegistry().registerProvider(MinecraftHelp.defaultCaptionsProvider());


        new CommandsManager(this, manager);
    }

    public @NonNull BukkitAudiences bukkitAudiences() {
        return this.bukkitAudiences;
    }


    public MinecraftHelp<CommandSender> getMinecraftHelp() {
        return minecraftHelp;
    }

    public TimerController getTimerController() {
        return timerController;
    }

    public SQLStorage getStorage() {
        return storage;
    }
}
