package eu.virtusdevelops.playertimers.core.controllers;

import eu.virtusdevelops.playertimers.api.controllers.TimersController;
import eu.virtusdevelops.playertimers.api.timer.PlayerTimer;
import eu.virtusdevelops.playertimers.core.storage.SQLStorage;
import eu.virtusdevelops.playertimers.core.timer.PlayerTimerImpl;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TimerControllerImpl implements TimersController {
    private final JavaPlugin plugin;
    private final SQLStorage storage;
    private final Map<UUID, List<PlayerTimerImpl>> playerTimers = new HashMap<>();
    private final Map<UUID, List<PlayerTimerImpl>> toExecute = new HashMap<>();


    private BukkitTask tickingTask;
    private BukkitTask saveTask;

    public TimerControllerImpl(JavaPlugin plugin, SQLStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
        loadTimers();


        // start updater
        tickingTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tickTimers, 0L, 20L);
        saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveAllTimers, 0L, 1200L);
    }

    public void stop(){
        tickingTask.cancel();
        saveTask.cancel();
        saveAllTimers();
    }


    private void loadTimers(){
        var temp = storage.getTimers();
        plugin.getLogger().info("Loaded " + temp.values().stream().map(List::size).toList().stream().mapToInt(Integer::intValue).sum() + " timers");
        for(var timer : temp.keySet()){
            playerTimers.put(timer, temp.get(timer));
        }
        temp.clear();
    }





    public PlayerTimerImpl getTimer(UUID player, String name){
        if (playerTimers.get(player) == null)
            return null;

        for(var timer : playerTimers.get(player)){
            if(timer.getName().equals(name))
                return timer;
        }
        return null;
    }


    public PlayerTimerImpl createTimer(UUID player, String timerName, long duration, boolean offlineTick){
        var timer = new PlayerTimerImpl(
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

    private void saveAllTimers(){
        for(var timers : playerTimers.values()){
            for(var timer : timers){
                if(timer.isUpdated()){
                    storage.updateTimer(timer, false);
                    timer.setUpdated(false);
                }
            }
        }
    }


    public void saveTimer(PlayerTimerImpl timer, boolean updateCommands){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            storage.updateTimer(timer, updateCommands);
            timer.setUpdated(false);
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

                //saveTimer(timer, false);
            }
        }
    }

    private void executeTimerCommands(PlayerTimerImpl timer){
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



    // implementation

    @Override
    public List<PlayerTimer> getPlayerTimers(UUID player) {
        // Use getOrDefault to return an empty list if there is no entry for the player
        return Collections.unmodifiableList(playerTimers.getOrDefault(player, Collections.emptyList()));
    }

    @Override
    public List<PlayerTimer> getAwaitingPlayerTimers(UUID player) {
        // Return an unmodifiable copy of the list to prevent accidental modification
        return Collections.unmodifiableList(toExecute.getOrDefault(player, Collections.emptyList()));
    }

    @Override
    public boolean cancelTimer(PlayerTimer timer) {
        if(timer instanceof PlayerTimerImpl nTimer){
            nTimer.setExecuted(true);
            nTimer.setDuration(0);
            saveTimer(nTimer, false);
            return true;
        }
        return false;
    }

    @Override
    public boolean stopTimer(PlayerTimer timer) {
        if(timer instanceof PlayerTimerImpl nTimer){
            nTimer.setDuration(0);
            saveTimer(nTimer, false);
            return true;
        }
        return false;
    }

    @Override
    public PlayerTimer addTime(PlayerTimer timer, long duration) {
        if(timer instanceof PlayerTimerImpl nTimer){
            nTimer.addTime(duration);
            saveTimer(nTimer, false);
            return nTimer;
        }
        return null;
    }

    @Override
    public PlayerTimer removeTime(PlayerTimer timer, long duration) {
        if(timer instanceof PlayerTimerImpl nTimer){
            nTimer.removeTime(duration);
            saveTimer(nTimer, false);
            return nTimer;
        }
        return null;
    }

    @Override
    public PlayerTimer addCommands(PlayerTimer timer, List<String> commands) {
        if(timer instanceof PlayerTimerImpl nTimer){
            nTimer.getCommands().addAll(commands);
            saveTimer(nTimer, true);
            return nTimer;
        }
        return null;
    }

    @Override
    public PlayerTimer addCommand(PlayerTimer timer, String command) {
        if(timer instanceof PlayerTimerImpl nTimer){
            nTimer.getCommands().add(command);
            saveTimer(nTimer, true);
            return nTimer;
        }
        return null;
    }
}
