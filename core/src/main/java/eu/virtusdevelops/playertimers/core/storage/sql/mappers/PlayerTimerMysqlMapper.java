package eu.virtusdevelops.playertimers.core.storage.sql.mappers;

import eu.virtusdevelops.playertimers.api.timer.PlayerTimer;
import eu.virtusdevelops.playertimers.core.timer.PlayerTimerImpl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Function;

public class PlayerTimerMysqlMapper implements Function<ResultSet, PlayerTimerImpl> {
    @Override
    public PlayerTimerImpl apply(ResultSet resultSet) {
        try{
            return new PlayerTimerImpl(
                    UUID.fromString(resultSet.getString("id")),
                    UUID.fromString(resultSet.getString("player_id")),
                    resultSet.getLong("start_time"),
                    resultSet.getLong("end_time"),
                    resultSet.getLong("duration"),
                    resultSet.getLong("total_time"),
                    resultSet.getString("timer_name"),
                    resultSet.getBoolean("offline_tick"),
                    resultSet.getBoolean("paused"),
                    new ArrayList<>()
            );
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
