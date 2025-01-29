package eu.virtusdevelops.playertimers.core.storage;

import eu.virtusdevelops.playertimers.core.timer.GlobalTimerImpl;
import eu.virtusdevelops.playertimers.core.timer.PlayerTimerImpl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface GlobalTimerDao extends DaoCrud<GlobalTimerImpl, UUID> {


    Map<UUID, GlobalTimerImpl> getActiveTimers();


    GlobalTimerImpl save(GlobalTimerImpl timer, boolean saveCommands, boolean savePlayers);

}
