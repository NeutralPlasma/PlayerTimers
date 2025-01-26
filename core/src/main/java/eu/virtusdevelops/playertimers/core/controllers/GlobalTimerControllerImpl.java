package eu.virtusdevelops.playertimers.core.controllers;

import eu.virtusdevelops.playertimers.api.controllers.GlobalTimersController;
import eu.virtusdevelops.playertimers.api.timer.GlobalTimer;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GlobalTimerControllerImpl implements GlobalTimersController {

    private Map<UUID, GlobalTimer> activeTimers;
    private Map<UUID, GlobalTimer> awaitingExecutionTimers;

    private final BukkitTask tickingTask;
    private final BukkitTask saveTask;


    public GlobalTimerControllerImpl(JavaPlugin plugin){



        tickingTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tickTimers, 0L, 20L);
        saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveAllTimers, 0L, 1200L);

    }


    public void stop(){
        tickingTask.cancel();
        saveTask.cancel();
        saveAllTimers();
    }


    private void tickTimers(){


        // use iterator instead

        for(Iterator<UUID> iterator = activeTimers.keySet().iterator(); iterator.hasNext();){
            UUID uuid = iterator.next();
            var timer = activeTimers.get(uuid);

            timer.tick();

            // check if finished then execute normal commands and move to awaiitng execution
            if(timer.isFinished()){
                timer.getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
                timer.finish();
                iterator.remove();
                awaitingExecutionTimers.put(uuid, timer);
            }
        }


        for(Iterator<UUID> iterator = awaitingExecutionTimers.keySet().iterator(); iterator.hasNext();){
            UUID uuid = iterator.next();
            var timer = activeTimers.get(uuid);



            // check if finished then execute normal commands and move to awaiitng execution

            for(var awaitingPlayer : timer.getLinkedPlayers()){
                var player = awaitingPlayer.getPlayer();
                if(player != null && player.isOnline()){
                    timer.getCommands().forEach(command -> Bukkit.dispatchCommand(player, PlaceholderAPI.setPlaceholders(player, command)));
                    timer.finish();
                    iterator.remove();
                    activeTimers.put(uuid, timer);
                }
            }
        }
    }


    private void saveAllTimers(){
        // call the dao and do saving watever
    }



    @Override
    public List<GlobalTimer> getActiveTimers() {
        return (List<GlobalTimer>) activeTimers.values();
    }

    @Override
    public List<GlobalTimer> getAwaitingExecutionTimers() {
        return (List<GlobalTimer>) awaitingExecutionTimers.values();
    }

    @Override
    public GlobalTimer getTimer(String name) {
        for(GlobalTimer timer : activeTimers.values()){
            if(timer.getName().equals(name)) return timer;
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
        return null;
    }
}
