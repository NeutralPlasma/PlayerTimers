package eu.virtusdevelops.playertimers.core.storage.sql.mappers;

import eu.virtusdevelops.playertimers.api.timer.LinkedPlayer;
import eu.virtusdevelops.playertimers.core.timer.LinkedPlayerImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Function;

public class LinkedPlayerMysqlMapper implements Function<ResultSet, LinkedPlayerImpl> {

    @Override
    public LinkedPlayerImpl apply(ResultSet resultSet) {
        try{
            var uuidString = resultSet.getString("global_timer_player.id");
            var playerUUid = resultSet.getString("global_timer_player.player_id");
            if(uuidString == null ||uuidString.isEmpty()) return null;
            if(playerUUid == null ||playerUUid.isEmpty()) return null;

            return new LinkedPlayerImpl(
                UUID.fromString(uuidString),
                UUID.fromString(playerUUid),
                resultSet.getBoolean("global_timer_player.executed")
            );
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
