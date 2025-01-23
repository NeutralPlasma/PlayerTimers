package eu.virtusdevelops.playertimers.controllers;

import eu.virtusdevelops.playertimers.PlayerTimers;
import eu.virtusdevelops.playertimers.models.PlayerTimer;
import eu.virtusdevelops.playertimers.storage.SQLStorage;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class TimerController {
    private final PlayerTimers plugin;
    private final SQLStorage storage;
    private final Map<UUID, List<PlayerTimer>> playerTimers = new HashMap<>();
    private final Map<UUID, List<PlayerTimer>> toExecute = new HashMap<>();


    private BukkitTask tickingTask;

    public TimerController(PlayerTimers plugin) {
        this.plugin = plugin;
        this.storage = plugin.getStorage();
        loadTimers();


        // start updater
        tickingTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tickTimers, 0L, 20L);
    }

    public void stop(){
        tickingTask.cancel();
        // save to execute
        // save other

    }


    private void loadTimers(){
        var temp = storage.getTimers();
        plugin.getLogger().info("Loaded " + temp.values().stream().map(List::size).toList().stream().mapToInt(Integer::intValue).sum() + " timers");
        for(var timer : temp.keySet()){
            playerTimers.put(timer, temp.get(timer));

            var timers = temp.get(timer).stream().map(it -> it.getName()).toList();

            plugin.getLogger().info( String.join(",", timers));

        }
        temp.clear();
    }

    public List<PlayerTimer> getPlayerTimers(UUID player){
        return playerTimers.get(player);
    }

    public List<PlayerTimer> getPlayerAwaitngTimers(UUID player){
        return toExecute.get(player);
    }

    public PlayerTimer getTimer(UUID player, String name){
        if (playerTimers.get(player) == null)
            return null;

        for(var timer : playerTimers.get(player)){
            if(timer.getName().equals(name))
                return timer;
        }
        return null;
    }


    public PlayerTimer createTimer(UUID player, String timerName, long duration, boolean offlineTick){
        var timer = new PlayerTimer(
                UUID.randomUUID(),
                player,
                System.currentTimeMillis(),
                0L,
                duration,
                timerName,
                offlineTick,
                new ArrayList<>()
        );

        if(!playerTimers.containsKey(player))
            playerTimers.put(player, new ArrayList<>());

        playerTimers.get(player).add(timer);


        // sql save
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            storage.addTimer(timer);
        });
        return timer;
    }

    public void saveTimer(PlayerTimer timer, boolean updateCommands){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            storage.updateTimer(timer, updateCommands);
        });
    }



    private void tickTimers(){

        for(var timers : toExecute.values()){
            var iterator = timers.iterator();
            while(iterator.hasNext()){
                var timer = iterator.next();
                executeTimerCommands(timer);
                if(timer.isExecuted()){
                    iterator.remove();
                    saveTimer(timer, false);
                }
            }
        }



        for(var timers : playerTimers.values()){
            var iterator = timers.iterator();
            while(iterator.hasNext()){
                var timer = iterator.next();
                timer.tick();

                if(timer.isFinished()){
                    if(!toExecute.containsKey(timer.getPlayerID()))
                        toExecute.put(timer.getPlayerID(), new ArrayList<>());

                    toExecute.get(timer.getPlayerID()).add(timer);
                    iterator.remove();
                }

                saveTimer(timer, false);
            }
        }
    }

    private void executeTimerCommands(PlayerTimer timer){
        Player player = timer.getPlayer();
        if(player != null && player.isOnline() && !timer.isExecuted()){
            for(var command : timer.getCommands()){
                Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        PlaceholderAPI.setPlaceholders(player, command)
                );
            }
            timer.setExecuted(true);
            saveTimer(timer, false);
        }
    }
}
