package eu.virtusdevelops.playertimers.core.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.virtusdevelops.playertimers.core.storage.sql.PlayerTimerMysql;
import eu.virtusdevelops.playertimers.core.storage.sql.mappers.CommandMapperMysql;
import eu.virtusdevelops.playertimers.core.storage.sql.mappers.PlayerTimerMysqlMapper;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class SQLStorage {
    private final JavaPlugin plugin;
    private final Logger logger;
    private HikariDataSource dataSource;
    private PlayerTimerDao playerTimerDao;


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

        setupDaos();
        initDaos();
    }

    private void setupDaos(){
        playerTimerDao = new PlayerTimerMysql(dataSource, new PlayerTimerMysqlMapper(), new CommandMapperMysql(), plugin.getLogger());
    }


    private void initDaos(){
        playerTimerDao.init();
    }

    public PlayerTimerDao getPlayerTimerDao() {
        return playerTimerDao;
    }
}
