package eu.virtusdevelops.playertimers.core.storage;

import eu.virtusdevelops.playertimers.api.timer.PlayerTimer;
import eu.virtusdevelops.playertimers.core.timer.PlayerTimerImpl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PlayerTimerDao extends DaoCrud<PlayerTimerImpl, UUID> {


    Map<UUID, List<PlayerTimerImpl>> getActiveTimers();


    PlayerTimerImpl save(PlayerTimerImpl timer, boolean saveCommands);
}
