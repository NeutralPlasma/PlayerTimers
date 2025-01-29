package eu.virtusdevelops.playertimers.core.storage.sql;

import eu.virtusdevelops.playertimers.api.timer.TimerCommand;
import eu.virtusdevelops.playertimers.core.storage.GlobalTimerDao;
import eu.virtusdevelops.playertimers.core.storage.sql.mappers.*;
import eu.virtusdevelops.playertimers.core.timer.GlobalTimerImpl;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class GlobalTimerMysql implements GlobalTimerDao {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GlobalTimerMysql.class);
    private final DataSource dataSource;
    private final Logger logger;

    private final GlobalTimerMysqlMapper globalTimerMysqlMapper = new GlobalTimerMysqlMapper();
    private final GlobalTimerCommandMysqlMapper globalTimerCommandMysqlMapper = new GlobalTimerCommandMysqlMapper();
    private final GlobalTimerPlayerCommandMysqlMapper globalTimerPlayerCommandMysqlMapper = new GlobalTimerPlayerCommandMysqlMapper();
    private final LinkedPlayerMysqlMapper linkedPlayerMysqlMapper = new LinkedPlayerMysqlMapper();


    public GlobalTimerMysql(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
    }



    @Override
    public void init() {
        try(Connection connection = dataSource.getConnection()){
            // setup the 4 databases
            // timers one
            // commands one
            // player commands one
            // linked players one


            PreparedStatement timersTable = connection.prepareStatement("""
            CREATE TABLE IF NOT EXISTS global_timers (
                id CHAR(36) PRIMARY KEY,
                timer_name VARCHAR(128) NOT NULL,
                duration BIGINT NOT NULL,
                total_duration BIGINT NOT NULL,
                finished TINYINT(1) DEFAULT 0,
                executed TINYINT(1) DEFAULT 0,
                player_executed TINYINT(1) DEFAULT 0,
                paused TINYINT(1) DEFAULT 0,
                start_time BIGINT NOT NULL,
                end_time BIGINT
            )
            """);
            timersTable.execute();

            PreparedStatement commandsTable = connection.prepareStatement("""
            CREATE TABLE IF NOT EXISTS global_timer_commands (
                id CHAR(36) PRIMARY KEY,
                timer_id CHAR(36),
                command TEXT,
                FOREIGN KEY (timer_id) REFERENCES global_timers(id) ON DELETE CASCADE
            )
            """);
            commandsTable.execute();

            PreparedStatement playerCommandsTable = connection.prepareStatement("""
            CREATE TABLE IF NOT EXISTS global_timer_player_commands (
                id CHAR(36) PRIMARY KEY,
                timer_id CHAR(36),
                command TEXT,
                FOREIGN KEY (timer_id) REFERENCES global_timers(id) ON DELETE CASCADE
            )
            """);
            playerCommandsTable.execute();

            PreparedStatement linkedPlayersTable = connection.prepareStatement("""
            CREATE TABLE IF NOT EXISTS global_timer_player (
                id CHAR(36) PRIMARY KEY,
                timer_id CHAR(36),
                player_id CHAR(36),
                executed TINYINT(1) DEFAULT 0,
                FOREIGN KEY (timer_id) REFERENCES global_timers(id) ON DELETE CASCADE
            )
            """);
            linkedPlayersTable.execute();

        }catch (SQLException e){
            logger.severe("Failed initializing global timers databases");
            e.printStackTrace();
        }
    }

    @Override
    public Map<UUID, GlobalTimerImpl> getActiveTimers() {
        try(Connection connection = dataSource.getConnection()){
            var statement = connection.prepareStatement("""
            SELECT
            -- Global Timer core fields
            global_timers.id AS `global_timers.id`,
            global_timers.finished AS `global_timers.finished`,
            global_timers.timer_name AS `global_timers.timer_name`,
            global_timers.duration AS `global_timers.duration`,
            global_timers.total_duration AS `global_timers.total_duration`,
            global_timers.executed AS `global_timers.executed`,
            global_timers.player_executed AS `global_timers.player_executed`,
            global_timers.paused AS `global_timers.paused`,
            global_timers.start_time AS `global_timers.start_time`,
            global_timers.end_time AS `global_timers.end_time`,

            -- Timer Commands fields
            global_timer_commands.command AS `global_timer_commands.command`,
            global_timer_commands.id AS `global_timer_commands.id`,

            -- Player Commands fields
            global_timer_player_commands.command AS `global_timer_player_commands.command`,
            global_timer_player_commands.id AS `global_timer_player_commands.id`,

            -- Linked Player fields
            global_timer_player.id AS `global_timer_player.id`,
            global_timer_player.player_id AS `global_timer_player.player_id`,
            global_timer_player.executed AS `global_timer_player.executed`
            
            FROM global_timers
            LEFT JOIN global_timer_commands ON global_timer_commands.timer_id = global_timers.id
            LEFT JOIN global_timer_player_commands ON global_timer_player_commands.timer_id = global_timers.id
            LEFT JOIN global_timer_player ON global_timer_player.timer_id = global_timers.id
            WHERE global_timers.player_executed = ?
            OR global_timers.executed = ?
            """);

            statement.setBoolean(1, false);
            statement.setBoolean(2, false);

            var resultSet = statement.executeQuery();

            Map<UUID, GlobalTimerImpl> timersMap = new HashMap<>();

            while(resultSet.next()){
                UUID timerId = UUID.fromString(resultSet.getString("global_timers.id"));

                GlobalTimerImpl timer;
                if(timersMap.containsKey(timerId)){
                    timer = timersMap.get(timerId);
                }else{
                    // construct new
                    timer = globalTimerMysqlMapper.apply(resultSet);
                    if(timer != null)
                        timersMap.put(timerId, timer);
                }

                if(timer != null)
                    parse(timer, resultSet);

            }

            return timersMap;
        }catch (SQLException e){
            logger.severe("Failed reading active global timers from database");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public GlobalTimerImpl save(GlobalTimerImpl timer, boolean saveCommands, boolean savePlayers) {
        try (Connection connection = dataSource.getConnection()){
            var selectStatement = connection.prepareStatement("""
            SELECT COUNT(*) AS count
            FROM global_timers
            WHERE id = ?
            """);
            selectStatement.setString(1, timer.getId().toString());
            var resultSet = selectStatement.executeQuery();
            boolean exists = resultSet.next() && resultSet.getInt("count") > 0;
            selectStatement.close();

            if(exists){
                // update statement
                var updateStatement = connection.prepareStatement("""
                 UPDATE global_timers
                 SET duration = ?,
                 total_duration = ?,
                 finished = ?,
                 executed = ?,
                 player_executed = ?,
                 end_time = ?,
                 paused = ?
                 WHERE id = ?
                """);
                updateStatement.setLong(1, timer.getDuration());
                updateStatement.setLong(2, timer.getTotalDuration());
                updateStatement.setBoolean(3, timer.isFinished());
                updateStatement.setBoolean(4, timer.isExecuted());
                updateStatement.setBoolean(5, timer.isPlayerExecuted());
                updateStatement.setLong(6, timer.getEndTime());
                updateStatement.setBoolean(7, timer.isPaused());
                updateStatement.setString(8, timer.getId().toString());

                if(updateStatement.executeUpdate() < 1){
                    return null;
                }
                updateStatement.close();

            }else{
                // insert statement
                var insertStatement = connection.prepareStatement("""
                INSERT INTO global_timers (
                   id, timer_name, duration,
                   total_duration, finished,
                   executed, player_executed,
                   start_time, end_time, paused
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """);
                insertStatement.setString(1, timer.getId().toString());
                insertStatement.setString(2, timer.getName());
                insertStatement.setLong(3, timer.getDuration());
                insertStatement.setLong(4, timer.getTotalDuration());
                insertStatement.setBoolean(5, timer.isFinished());
                insertStatement.setBoolean(6, timer.isExecuted());
                insertStatement.setBoolean(7, timer.isPlayerExecuted());
                insertStatement.setLong(8, timer.getStartTime());
                insertStatement.setLong(9, timer.getEndTime());
                insertStatement.setBoolean(10, timer.isPaused());

                if(insertStatement.executeUpdate() < 1){
                    return null;
                }
                insertStatement.close();
            }


            if(saveCommands){

                // delete existing commands and save new
                var deleteStatement = connection.prepareStatement("""
                DELETE FROM global_timer_commands
                WHERE timer_id = ?
                """);
                deleteStatement.setString(1, timer.getId().toString());
                deleteStatement.executeUpdate();
                deleteStatement.close();

                // insert all
                var insertCommandStatement = connection.prepareStatement("""
                INSERT INTO global_timer_commands (id, timer_id, command)
                VALUES (?, ?, ?)
                """);

                for(TimerCommand command : timer.getCommands()){
                    insertCommandStatement.setString(1, command.getId().toString());
                    insertCommandStatement.setString(2, timer.getId().toString());
                    insertCommandStatement.setString(3, command.getCommand());
                    insertCommandStatement.addBatch();
                }
                insertCommandStatement.executeBatch();
                insertCommandStatement.close();


                // player commands
                var deleteStatement2 = connection.prepareStatement("""
                DELETE FROM global_timer_player_commands
                WHERE timer_id = ?
                """);
                deleteStatement2.setString(1, timer.getId().toString());
                deleteStatement2.executeUpdate();
                deleteStatement2.close();

                // insert all
                var insertPlayerCommandStatement = connection.prepareStatement("""
                INSERT INTO global_timer_player_commands (id, timer_id, command)
                VALUES (?, ?, ?)
                """);
                for(TimerCommand command : timer.getPlayerCommands()){
                    insertPlayerCommandStatement.setString(1, command.getId().toString());
                    insertPlayerCommandStatement.setString(2, timer.getId().toString());
                    insertPlayerCommandStatement.setString(3, command.getCommand());
                    insertPlayerCommandStatement.addBatch();
                }
                insertPlayerCommandStatement.executeBatch();
                insertPlayerCommandStatement.close();
            }


            if(savePlayers){
                // delete existing and save new
                var deleteStatement = connection.prepareStatement("""
                DELETE FROM global_timer_player
                WHERE timer_id = ?
                """);
                deleteStatement.setString(1, timer.getId().toString());
                deleteStatement.executeUpdate();
                deleteStatement.close();

                var insertStatement = connection.prepareStatement("""
                INSERT INTO global_timer_player (id, timer_id, player_id, executed)
                VALUES (?, ?, ?, ?)
                """);
                for(var player : timer.getLinkedPlayers()){
                    insertStatement.setString(1, player.getId().toString());
                    insertStatement.setString(2, timer.getId().toString());
                    insertStatement.setString(3, player.getPlayer_id().toString());
                    insertStatement.setBoolean(4, player.isExecuted());
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
                insertStatement.close();

            }


        }catch (SQLException e){
            logger.severe("Failed saving timer: " + timer.getId() + " to database");
            e.printStackTrace();
        }
        return timer;
    }



    @Override
    public GlobalTimerImpl getById(UUID id) {
        try(Connection connection = dataSource.getConnection()){
            var statement = connection.prepareStatement("""
            SELECT
            -- Global Timer core fields
            global_timers.id AS `global_timers.id`,
            global_timers.finished AS `global_timers.finished`,
            global_timers.timer_name AS `global_timers.timer_name`,
            global_timers.duration AS `global_timers.duration`,
            global_timers.total_duration AS `global_timers.total_duration`,
            global_timers.executed AS `global_timers.executed`,
            global_timers.player_executed AS `global_timers.player_executed`,
            global_timers.paused AS `global_timers.paused`,
            global_timers.start_time AS `global_timers.start_time`,
            global_timers.end_time AS `global_timers.end_time`,

            -- Timer Commands fields
            global_timer_commands.command AS `global_timer_commands.command`,
            global_timer_commands.id AS `global_timer_commands.id`,

            -- Player Commands fields
            global_timer_player_commands.command AS `global_timer_player_commands.command`,
            global_timer_player_commands.id AS `global_timer_player_commands.id`,

            -- Linked Player fields
            global_timer_player.id AS `global_timer_player.id`,
            global_timer_player.player_id AS `global_timer_player.player_id`,
            global_timer_player.executed AS `global_timer_player.executed`
            
            FROM global_timers
            LEFT JOIN global_timer_commands ON global_timer_commands.timer_id = global_timers.id
            LEFT JOIN global_timer_player_commands ON global_timer_player_commands.timer_id = global_timers.id
            LEFT JOIN global_timer_player ON global_timer_player.timer_id = global_timers.id
            WHERE id = ?
            """);

            statement.setString(1, id.toString());

            var resultSet = statement.executeQuery();

            GlobalTimerImpl timer = null;

            while(resultSet.next()){
                if(timer == null){
                    timer = globalTimerMysqlMapper.apply(resultSet);
                }

                if(timer != null)
                    parse(timer, resultSet);

            }
            return timer;
        }catch (SQLException e){
            logger.severe("Failed getting timer with id: " + id);
            e.printStackTrace();
        }
        return null;
    }



    private void parse(GlobalTimerImpl timer, ResultSet resultSet) throws SQLException {
        var command = globalTimerCommandMysqlMapper.apply(resultSet);

        if(command != null){
            boolean exists = timer.getCommands()
                    .stream()
                    .anyMatch(c -> c.getId().equals(command.getId()));
            if(!exists)
                timer.getCommands().add(command);
        }


        // parse player commands
        var playerCommand = globalTimerPlayerCommandMysqlMapper.apply(resultSet);
        if(playerCommand != null){
            boolean exists = timer.getPlayerCommands()
                    .stream()
                    .anyMatch(c -> c.getId().equals(playerCommand.getId()));
            if(!exists)
                timer.getPlayerCommands().add(playerCommand);
        }


        // parse linked players
        var linkedPlayer = linkedPlayerMysqlMapper.apply(resultSet);
        if(linkedPlayer != null){
            boolean exists = timer.getLinkedPlayers()
                    .stream()
                    .anyMatch(p -> p.getPlayer_id().equals(linkedPlayer.getPlayer_id()));
            if(!exists)
                timer.getLinkedPlayers().add(linkedPlayer);
        }


    }

    @Override
    public List<GlobalTimerImpl> getAll() {
        try(Connection connection = dataSource.getConnection()){
            var statement = connection.prepareStatement("""
            SELECT
            -- Global Timer core fields
            global_timers.id AS `global_timers.id`,
            global_timers.finished AS `global_timers.finished`,
            global_timers.timer_name AS `global_timers.timer_name`,
            global_timers.duration AS `global_timers.duration`,
            global_timers.total_duration AS `global_timers.total_duration`,
            global_timers.executed AS `global_timers.executed`,
            global_timers.player_executed AS `global_timers.player_executed`,
            global_timers.paused AS `global_timers.paused`,
            global_timers.start_time AS `global_timers.start_time`,
            global_timers.end_time AS `global_timers.end_time`,

            -- Timer Commands fields
            global_timer_commands.command AS `global_timer_commands.command`,
            global_timer_commands.id AS `global_timer_commands.id`,

            -- Player Commands fields
            global_timer_player_commands.command AS `global_timer_player_commands.command`,
            global_timer_player_commands.id AS `global_timer_player_commands.id`,

            -- Linked Player fields
            global_timer_player.id AS `global_timer_player.id`,
            global_timer_player.player_id AS `global_timer_player.player_id`,
            global_timer_player.executed AS `global_timer_player.executed`
            
            FROM global_timers
            LEFT JOIN global_timer_commands ON global_timer_commands.timer_id = global_timers.id
            LEFT JOIN global_timer_player_commands ON global_timer_player_commands.timer_id = global_timers.id
            LEFT JOIN global_timer_player ON global_timer_player.timer_id = global_timers.id
            """);
            var resultSet = statement.executeQuery();

            Map<UUID, GlobalTimerImpl> timersMap = new HashMap<>();

            while(resultSet.next()){
                UUID timerId = UUID.fromString(resultSet.getString("global_timers.id"));

                GlobalTimerImpl timer;
                if(timersMap.containsKey(timerId)){
                    timer = timersMap.get(timerId);
                }else{
                    // construct new
                    timer = globalTimerMysqlMapper.apply(resultSet);
                    timersMap.put(timerId, timer);
                }


                if(timer != null)
                    parse(timer, resultSet);


            }

            return timersMap.values().stream().toList();
        }catch (SQLException e){
            logger.severe("Failed reading active global timers from database");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public GlobalTimerImpl save(GlobalTimerImpl globalTimer) {
        return save(globalTimer, true, true);
    }

    @Override
    public boolean delete(GlobalTimerImpl globalTimer) {
        try(Connection connection = dataSource.getConnection()){
            var statement = connection.prepareStatement("""
            DELETE FROM global_timers
            WHERE id = ?
            """);
            statement.setString(1, globalTimer.getId().toString());
            if(statement.executeUpdate() < 1){
                return false;
            }
            return true;
        }catch (SQLException e){
            logger.severe("Failed deleting timer: " + globalTimer.getId() + " from database");
            e.printStackTrace();
        }
        return false;
    }
}
