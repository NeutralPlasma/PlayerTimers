package eu.virtusdevelops.playertimers.core.storage.sql.mappers;

import eu.virtusdevelops.playertimers.api.timer.LinkedPlayer;
import eu.virtusdevelops.playertimers.core.timer.GlobalTimerImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class GlobalTimerMysqlMapper implements Function<ResultSet, GlobalTimerImpl> {
    @Override
    public GlobalTimerImpl apply(ResultSet resultSet) {

        try{
            return new GlobalTimerImpl(
                    UUID.fromString(resultSet.getString("global_timers.id")),
                    resultSet.getString("global_timers.timer_name"),
                    resultSet.getLong("global_timers.duration"),
                    resultSet.getLong("global_timers.total_duration"),
                    resultSet.getBoolean("global_timers.finished"),
                    resultSet.getBoolean("global_timers.executed"),
                    resultSet.getBoolean("global_timers.player_executed"),
                    resultSet.getBoolean("global_timers.paused"),
                    resultSet.getLong("global_timers.start_time"),
                    resultSet.getLong("global_timers.end_time"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>()
            );
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
