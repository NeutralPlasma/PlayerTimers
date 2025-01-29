package eu.virtusdevelops.playertimers.core.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.virtusdevelops.playertimers.core.storage.sql.GlobalTimerMysql;
import eu.virtusdevelops.playertimers.core.storage.sql.PlayerTimerMysql;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.LoggerFactory;

import java.util.logging.Logger;

public class SQLStorage {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SQLStorage.class);
    private final JavaPlugin plugin;
    private final Logger logger;
    private final HikariDataSource dataSource;
    private PlayerTimerDao playerTimerDao;
    private GlobalTimerDao globalTimerDao;


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

        setupDAOs();
        initDAOs();
    }

    private void setupDAOs(){
        logger.info("Setting up DAOs");
        playerTimerDao = new PlayerTimerMysql(dataSource, plugin.getLogger());
        globalTimerDao = new GlobalTimerMysql(dataSource, plugin.getLogger());
    }


    private void initDAOs(){
        logger.info("Initializing DAOs");
        playerTimerDao.init();
        globalTimerDao.init();
    }

    public PlayerTimerDao getPlayerTimerDao() {
        return playerTimerDao;
    }

    public GlobalTimerDao getGlobalTimerDao() {
        return globalTimerDao;
    }
}
