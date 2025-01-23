package eu.virtusdevelops.playertimers.core.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.virtusdevelops.playertimers.core.timer.PlayerTimerImpl;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class SQLStorage {
    private final JavaPlugin plugin;
    private final Logger logger;
    private HikariDataSource dataSource;


    public SQLStorage(JavaPlugin plugin) throws InvalidConfigurationException {
        this.plugin = plugin;
        this.logger = plugin.getLogger();


        String path = plugin.getDataFolder().getPath();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.sqlite.JDBC");
        hikariConfig.setJdbcUrl("jdbc:sqlite:" + path + "/db.sqlite");
        hikariConfig.setPoolName("PlayerTimers");
        hikariConfig.setMaximumPoolSize(60000);
        hikariConfig.setMaximumPoolSize(10);

        this.dataSource = new HikariDataSource(hikariConfig);
        logger.info("Creating database tables...");
        createTables();
    }


    private void createTables(){
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
                    offline_tick TINYINT(1) DEFAULT 0,
                    executed TINYINT(1) DEFAULT 0
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

    private List<String> getTimerCommands(Connection connection, UUID timerID) throws SQLException {
        var commands = new ArrayList<String>();
        PreparedStatement statement = connection.prepareStatement(
        """
            SELECT *
            FROM pt_timer_commands
            WHERE timer_id = ?
            """
        );
        statement.setString(1, timerID.toString());
        var resultSet = statement.executeQuery();
        while(resultSet.next()){
            commands.add(resultSet.getString("command"));
        }
        return commands;
    }

    public Map<UUID, List<PlayerTimerImpl>> getTimers(){
        Map<UUID, List<PlayerTimerImpl>> timers = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
            """
                SELECT *
                FROM pt_timers
                WHERE executed = ?
                """
            );
            statement.setInt(1, 0);
            var resultSet = statement.executeQuery();

            while(resultSet.next()){
                var timer = new PlayerTimerImpl(
                        UUID.fromString(resultSet.getString("id")),
                        UUID.fromString(resultSet.getString("player_id")),
                        resultSet.getLong("start_time"),
                        resultSet.getLong("end_time"),
                        resultSet.getLong("duration"),
                        resultSet.getString("timer_name"),
                        resultSet.getBoolean("offline_tick"),
                        new ArrayList<>()
                );
                timer.getCommands().addAll(getTimerCommands(connection, timer.getId()));

                timers.computeIfAbsent(timer.getPlayerID(), k -> new ArrayList<>());

                timers.get(timer.getPlayerID()).add(timer);


            }
        } catch (SQLException e) {
            logger.severe("Could not load timers");
            e.printStackTrace();
        }

        return timers;
    }



    private void insertTimerCommands(PlayerTimerImpl timer, Connection connection) throws SQLException{
        for(var command : timer.getCommands()){
            PreparedStatement statement = connection.prepareStatement(
            """
            INSERT INTO pt_timer_commands
                (timer_id, command)
            VALUES
                (?, ?)
            """);
            statement.setString(1, timer.getId().toString());
            statement.setString(2, command);
            statement.execute();
        }
    }

    private void removeTimerCommands(PlayerTimerImpl timer, Connection connection) throws SQLException{
        PreparedStatement statement = connection.prepareStatement(
                """
                DELETE FROM pt_timer_commands
                WHERE timer_id = ?
                """);
        statement.setString(1, timer.getId().toString());
        statement.execute();
    }


    public void addTimer(PlayerTimerImpl timer){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
            """
                INSERT INTO
                    pt_timers(id, timer_name, player_id, start_time, end_time, duration, executed, offline_tick)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """
            );
            statement.setString(1, timer.getId().toString());
            statement.setString(2, timer.getName());
            statement.setString(3, timer.getPlayerID().toString());
            statement.setLong(4, timer.getStartTime());
            statement.setLong(5, timer.getEndTime());
            statement.setLong(6, timer.getDuration());
            statement.setBoolean(7, timer.isExecuted());
            statement.setBoolean(8, timer.isOfflineTick());

            statement.execute();

            insertTimerCommands(timer, connection);


        } catch (SQLException e) {
            logger.severe("Could not load timers");
            e.printStackTrace();
        }
    }

    public void updateTimer(PlayerTimerImpl timer, boolean updateCommands){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    """
                        UPDATE pt_timers
                        SET
                            end_time = ?,
                            duration = ?,
                            executed = ?,
                            offline_tick = ?
                        WHERE
                            id = ?
                        """
            );
            statement.setLong(1, timer.getEndTime());
            statement.setLong(2, timer.getDuration());
            statement.setBoolean(3, timer.isExecuted());
            statement.setBoolean(4, timer.isOfflineTick());
            statement.setString(5, timer.getId().toString());
            statement.execute();
            if(updateCommands){
                removeTimerCommands(timer, connection);
                insertTimerCommands(timer, connection);
            }
        } catch (SQLException e) {
            logger.severe("Could not load timers");
            e.printStackTrace();
        }
    }
}
