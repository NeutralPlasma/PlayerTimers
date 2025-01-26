package eu.virtusdevelops.playertimers.core.storage.sql.mappers;

import java.sql.ResultSet;
import java.util.function.Function;

public class CommandMapperMysql implements Function<ResultSet, String> {

    @Override
    public String apply(ResultSet resultSet) {
        try{
            return resultSet.getString("command");
        }catch (Exception e){
            return null;
        }
    }
}
