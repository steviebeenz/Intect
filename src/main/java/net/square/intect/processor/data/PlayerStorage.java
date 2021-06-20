package net.square.intect.processor.data;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.Getter;
import net.square.intect.checks.objectable.Check;
import net.square.intect.processor.custom.*;
import net.square.intect.processor.manager.ModuleManager;
import net.square.intect.utils.objectable.EvictingList;
import net.square.intect.utils.objectable.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

@Data
public class PlayerStorage {

    public static HashMap<Player, PlayerStorage> storageHashMap = Maps.newHashMap();

    private final EvictingList<Pair<Location, Integer>> targetLocations = new EvictingList<>(40);

    private final Player player;
    @Getter
    private final List<Check> checks = ModuleManager.loadChecks(this);

    private final ActionProcessor actionProcessor = new ActionProcessor(this);
    private final CombatProcessor combatProcessor = new CombatProcessor(this);
    private final PositionProcessor positionProcessor = new PositionProcessor(this);
    private final RotationProcessor rotationProcessor = new RotationProcessor(this);

    public PlayerStorage(Player player) {
        this.player = player;
    }
}


