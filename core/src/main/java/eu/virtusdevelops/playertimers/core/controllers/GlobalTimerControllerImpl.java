package eu.virtusdevelops.playertimers.core.controllers;

import eu.virtusdevelops.playertimers.api.controllers.GlobalTimersController;
import eu.virtusdevelops.playertimers.api.timer.GlobalTimer;
import eu.virtusdevelops.playertimers.api.timer.LinkedPlayer;
import eu.virtusdevelops.playertimers.core.storage.GlobalTimerDao;
import eu.virtusdevelops.playertimers.core.timer.GlobalTimerImpl;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class GlobalTimerControllerImpl implements GlobalTimersController {

    private final Map<UUID, GlobalTimerImpl> activeTimers = new HashMap<>();

    private final GlobalTimerDao globalTimerDao;
    private final JavaPlugin javaPlugin;

    private final BukkitTask tickingTask;
    private final BukkitTask saveTask;


    public GlobalTimerControllerImpl(JavaPlugin plugin, GlobalTimerDao globalTimerDao){
        this.javaPlugin = plugin;
        this.globalTimerDao = globalTimerDao;


        loadTimers();

        tickingTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tickTimers, 0L, 20L);
        saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveAllTimers, 0L, 1200L);

    }

    private void loadTimers(){
        var tempTimers = globalTimerDao.getActiveTimers();
        if(tempTimers == null) return;

        for(var timer : tempTimers.values()){

            activeTimers.put(timer.getId(), timer);

        }
    }


    public void stop(){
        tickingTask.cancel();
        saveTask.cancel();
        saveAllTimers();
    }

    private final List<UUID> toRemove = new ArrayList<>();

    private void tickTimers() {

        // Use entrySet to avoid unnecessary multiple lookups
        for (Map.Entry<UUID, GlobalTimerImpl> entry : activeTimers.entrySet()) {
            GlobalTimerImpl timer = entry.getValue();

            timer.tick();

            // Check if finished, then execute normal commands and move to awaiting execution
            if (timer.isFinished() && !timer.isExecuted()) {
                timer.getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.getCommand()));
                timer.setExecuted(true);
            }

            if (timer.isFinished() && timer.isExecuted()) {

                if (timer.isPlayerExecuted()) {
                    Bukkit.getScheduler().runTaskAsynchronously(javaPlugin, () -> saveTimer(timer));
                    toRemove.add(timer.getId());
                    continue;
                }


                Collection<LinkedPlayer> linkedPlayers = timer.getLinkedPlayers();
                if (linkedPlayers == null) continue; // Safeguard against null collections

                for (var awaitingPlayer : linkedPlayers) {
                    var player = awaitingPlayer.getPlayer();
                    if (player != null && player.isOnline()) {
                        timer.getPlayerCommands().forEach(command ->
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(player, command.getCommand()))
                        );
                        awaitingPlayer.execute();
                    }
                }
            }
        }

        for(var uuid : toRemove){
            activeTimers.remove(uuid);
        }
        toRemove.clear();

    }


    private void saveAllTimers(){
        for(var timer : activeTimers.values()){
            if(!timer.isUpdated()) continue;
            saveTimer(timer);
        }
    }

    private void saveTimer(GlobalTimerImpl timer){
        globalTimerDao.save(timer, timer.isCommandsUpdated(), timer.isPlayersUpdated());
        timer.setUpdated(false);
        timer.setPlayersUpdated(false);
        timer.setCommandsUpdated(false);
    }


    @Override
    public List<GlobalTimer> getActiveTimers() {
        return new ArrayList<>(activeTimers.values());
    }


    @Override
    public GlobalTimer getTimer(String name) {
        for(GlobalTimer timer : activeTimers.values()){
            if(timer.getName().equals(name)) return timer;
        }

        return null;
    }

    @Override
    public GlobalTimer getActiveTimer(String name) {
        for(GlobalTimer timer : activeTimers.values()){
            if(timer.getName().equals(name) && !timer.isExecuted()) return timer;
        }
        return null;
    }

    @Override
    public GlobalTimer getTimer(UUID uuid) {
        if(activeTimers.containsKey(uuid)) return activeTimers.get(uuid);
        return null;
    }

    @Override
    public GlobalTimer createTimer(String name, long duration) {
        var timer = new GlobalTimerImpl(UUID.randomUUID(), name, duration);
        activeTimers.put(timer.getId(), timer);

        Bukkit.getScheduler().runTaskAsynchronously(javaPlugin, () -> {
            globalTimerDao.save(timer);
        });

        return timer;
    }
}
