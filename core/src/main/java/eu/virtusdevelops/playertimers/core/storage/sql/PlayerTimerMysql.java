package eu.virtusdevelops.playertimers.core.storage.sql;

import com.zaxxer.hikari.HikariDataSource;
import eu.virtusdevelops.playertimers.api.timer.PlayerTimer;
import eu.virtusdevelops.playertimers.core.storage.PlayerTimerDao;
import eu.virtusdevelops.playertimers.core.storage.sql.mappers.CommandMapperMysql;
import eu.virtusdevelops.playertimers.core.storage.sql.mappers.PlayerTimerMysqlMapper;
import eu.virtusdevelops.playertimers.core.timer.PlayerTimerImpl;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class PlayerTimerMysql implements PlayerTimerDao {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PlayerTimerMysql.class);
    private final HikariDataSource dataSource;
    private final Logger logger;

    private PlayerTimerMysqlMapper playerTimerMapperMysql;
    private CommandMapperMysql commandMapperMysql;

    public PlayerTimerMysql(HikariDataSource dataSource,
                            PlayerTimerMysqlMapper playerTimerMapperMysql,
                            CommandMapperMysql commandMapperMysql,
                            Logger logger) {
        this.dataSource = dataSource;
        this.playerTimerMapperMysql = playerTimerMapperMysql;
        this.commandMapperMysql = commandMapperMysql;
        this.logger = logger;
    }

    @Override
    public void init() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                        CREATE TABLE IF NOT EXISTS pt_timers (
                            id CHAR(36) PRIMARY KEY,
                            timer_name CHAR(128) NOT NULL,
                            player_id CHAR(36),
                            start_time BIGINT NOT NULL,
                            end_time BIGINT DEFAULT 0,
                            duration BIGINT NOT NULL,
                            total_time BIGINT DEFAULT 0,
                            offline_tick TINYINT(1) DEFAULT 0,
                            executed TINYINT(1) DEFAULT 0,
                            paused TINYINT(1) DEFAULT 0
                        );
                        """
            );
            statement.execute();


            PreparedStatement statement2 = connection.prepareStatement(
                    """
                        CREATE TABLE IF NOT EXISTS pt_timer_commands (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            timer_id CHAR(36),
                            command TEXT,
                            FOREIGN KEY (timer_id) REFERENCES pt_timers(id) ON DELETE CASCADE
                        );
                        """
            );
            statement2.execute();

        } catch (SQLException e) {
            logger.severe("Could not create tables");
            e.printStackTrace();
        }
    }

    @Override
    public PlayerTimerImpl getById(UUID id) {
        try(Connection connection = dataSource.getConnection()){
            var statement = connection.prepareStatement("""
            SELECT pt_timers.*
            FROM pt_timers
            LEFT JOIN pt_timer_commands ON pt_timer_commands.timer_id = pt_timers.id
            WHERE id = ?
            """);
            statement.setString(1, id.toString());
            var resultSet = statement.executeQuery();

            PlayerTimerImpl playerTimer = null;

            // filter out all the timers and its commands use the mappers provided
            while (resultSet.next()) {
                if (playerTimer == null) {
                    // Map the main PlayerTimer object the first time
                    playerTimer = playerTimerMapperMysql.apply(resultSet);
                }

                // Map and add commands to the PlayerTimer object
                var command = commandMapperMysql.apply(resultSet);
                if (command != null) {
                    playerTimer.getCommands().add(command); // Assuming PlayerTimer has a method to add commands
                }
            }
            return playerTimer;
        }catch (SQLException e){
            logger.severe("Could not load timer: " + id);
            return null;
        }
    }

    @Override
    public List<PlayerTimerImpl> getAll() {
        try (Connection connection = dataSource.getConnection()) {
            var statement = connection.prepareStatement("""
            SELECT pt_timers.*, pt_timer_commands.*
            FROM pt_timers
            LEFT JOIN pt_timer_commands ON pt_timer_commands.timer_id = pt_timers.id
            """);

            var resultSet = statement.executeQuery();

            // Map to store timers with their UUIDs for aggregation
            Map<UUID, PlayerTimerImpl> timersMap = new HashMap<>();


            while (resultSet.next()) {
                UUID timerId = UUID.fromString(resultSet.getString("id"));

                PlayerTimerImpl playerTimer;
                // Check if a timer for this ID already exists in the map
                if(timersMap.containsKey(timerId)){
                    playerTimer = timersMap.get(timerId);
                }else{
                    playerTimer = playerTimerMapperMysql.apply(resultSet);
                    timersMap.put(timerId, playerTimer);
                }


                // Add commands to the timer if they exist
                var command = commandMapperMysql.apply(resultSet);
                if (command != null) {
                    playerTimer.getCommands().add(command); // Assuming PlayerTimer has an `addCommand()` method
                }
            }

            // Return all PlayerTimers as a List
            return new ArrayList<>(timersMap.values());


        } catch (SQLException e) {
            logger.severe("Couldn't load timers from database");
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public PlayerTimerImpl save(PlayerTimerImpl playerTimer) {
        return save(playerTimer, true);
    }

    @Override
    public PlayerTimerImpl save(PlayerTimerImpl playerTimer, boolean saveCommands) {
        try (Connection connection = dataSource.getConnection()) {
            // Check if the timer already exists in the database
            var checkStatement = connection.prepareStatement("""
            SELECT COUNT(*) AS count FROM pt_timers WHERE id = ?
            """);
            checkStatement.setString(1, playerTimer.getId().toString());
            var resultSet = checkStatement.executeQuery();

            boolean exists = false;
            if (resultSet.next() && resultSet.getInt("count") > 0) {
                exists = true;
            }

            if (exists) {
                // Update the existing timer with the necessary fields
                var updateStatement = connection.prepareStatement("""
                UPDATE pt_timers
                SET
                    end_time = ?,
                    duration = ?,
                    executed = ?,
                    offline_tick = ?,
                    paused = ?,
                    total_time = ?
                WHERE id = ?
                """);

                updateStatement.setLong(1, playerTimer.getEndTime());
                updateStatement.setLong(2, playerTimer.getDuration());
                updateStatement.setBoolean(3, playerTimer.isExecuted());
                updateStatement.setBoolean(4, playerTimer.isOfflineTick());
                updateStatement.setBoolean(5, playerTimer.isPaused());
                updateStatement.setLong(6, playerTimer.getTotalDuration());
                updateStatement.setString(7, playerTimer.getId().toString());
                updateStatement.executeUpdate();
            } else {
                // Insert a new timer
                var insertStatement = connection.prepareStatement("""
                INSERT INTO pt_timers (
                    id, timer_name, player_id, start_time,
                    end_time, duration, total_time,
                    offline_tick, executed, paused
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """);

                insertStatement.setString(1, playerTimer.getId().toString());
                insertStatement.setString(2, playerTimer.getName());
                insertStatement.setString(3, playerTimer.getPlayerID().toString());
                insertStatement.setLong(4, playerTimer.getStartTime());
                insertStatement.setLong(5, playerTimer.getEndTime());
                insertStatement.setLong(6, playerTimer.getDuration());
                insertStatement.setLong(7, playerTimer.getTotalDuration());
                insertStatement.setBoolean(8, playerTimer.isOfflineTick());
                insertStatement.setBoolean(9, playerTimer.isExecuted());
                insertStatement.setBoolean(10, playerTimer.isPaused());
                insertStatement.executeUpdate();
            }

            if (saveCommands) {
                // Delete existing commands for the timer
                var deleteCommandsStatement = connection.prepareStatement("""
                DELETE FROM pt_timer_commands WHERE timer_id = ?
                """);
                deleteCommandsStatement.setString(1, playerTimer.getId().toString());
                deleteCommandsStatement.executeUpdate();

                // Insert new commands for the timer
                var insertCommandStatement = connection.prepareStatement("""
                INSERT INTO pt_timer_commands (timer_id, command)
                VALUES (?, ?)
                """);

                for (String command : playerTimer.getCommands()) {
                    insertCommandStatement.setString(1, playerTimer.getId().toString());
                    insertCommandStatement.setString(2, command);
                    insertCommandStatement.addBatch(); // Batch insert for efficiency
                }
                insertCommandStatement.executeBatch(); // Execute all commands in a single operation
            }

            return playerTimer; // Return the saved PlayerTimer object
        } catch (SQLException e) {
            logger.severe("Couldn't save timer: " + playerTimer.getId());
            e.printStackTrace();
            return null; // Return null in case of an error
        }
    }

    @Override
    public boolean delete(PlayerTimerImpl playerTimer) {
        try(Connection connection = dataSource.getConnection()){
            var statement = connection.prepareStatement("""
                DELETE 
                FROM pt_timers
                WHERE id = ?
            """);

            statement.setString(1, playerTimer.getId().toString());
            var result = statement.executeUpdate();
            if(result != 0){
                return true;
            }
        }catch (SQLException e){
            logger.severe("Couldn't delete timer: " + playerTimer.getId());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Map<UUID, List<PlayerTimerImpl>> getActiveTimers() {
        try (Connection connection = dataSource.getConnection()) {
            var statement = connection.prepareStatement("""
            SELECT pt_timers.*, pt_timer_commands.command
            FROM pt_timers
            LEFT JOIN pt_timer_commands ON pt_timer_commands.timer_id = pt_timers.id
            WHERE executed = ?
            """);
            statement.setBoolean(1, false); // Only get timers where `executed = false`

            var resultSet = statement.executeQuery();

            // Map to store timers per player
            Map<UUID, List<PlayerTimerImpl>> playerTimersMap = new HashMap<>();
            Map<UUID, PlayerTimerImpl> timerCache = new HashMap<>(); // Temporary cache for deduplicating timers


            while (resultSet.next()) {
                // Get the IDs
                UUID timerId = UUID.fromString(resultSet.getString("id")); // Timer ID
                UUID playerId = resultSet.getString("player_id") != null ? UUID.fromString(resultSet.getString("player_id")) : null;

                // Skip if there's no player ID (we can't map it)
                if (playerId == null) {
                    logger.severe("Failed getting player id from timer?");
                    continue;
                }
                PlayerTimerImpl playerTimer;
                // Check if the timer is already cached
                if(timerCache.containsKey(timerId)){
                    playerTimer = timerCache.get(timerId);
                }else{
                    playerTimer = playerTimerMapperMysql.apply(resultSet);
                    timerCache.put(timerId, playerTimer);
                }

                // Map commands to the timer
                var command = commandMapperMysql.apply(resultSet);
                if (command != null) {
                    playerTimer.getCommands().add(command);
                }

            }

            // add timers to playerTimersMap
            for(var entry : timerCache.entrySet()){
                if(!playerTimersMap.containsKey(entry.getValue().getPlayerID()))
                    playerTimersMap.put(entry.getValue().getPlayerID(), new ArrayList<>());
                playerTimersMap.get(entry.getValue().getPlayerID()).add(entry.getValue());
                logger.info("Loaded new timer: " + entry.getKey());
            }

            // Return the map
            return playerTimersMap;

            //return new HashMap<>();
        } catch (SQLException e) {
            logger.severe("Couldn't load timers from database");
            e.printStackTrace();
            return null;
        }
    }
}
