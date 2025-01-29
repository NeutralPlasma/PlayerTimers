package eu.virtusdevelops.playertimers.core.storage.sql.mappers;

import eu.virtusdevelops.playertimers.core.timer.TimerCommandImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Function;

public class GlobalTimerCommandMysqlMapper implements Function<ResultSet, TimerCommandImpl> {
    @Override
    public TimerCommandImpl apply(ResultSet resultSet) {
        if(resultSet == null) return null;

        try{
            if(resultSet.getString("global_timer_commands.id") == null) return null;

            return new TimerCommandImpl(
                    UUID.fromString(resultSet.getString("global_timer_commands.id")),
                    resultSet.getString("global_timer_commands.command")
            );
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }

    }
}
