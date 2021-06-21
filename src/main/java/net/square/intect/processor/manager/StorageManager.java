package net.square.intect.processor.manager;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

public class StorageManager
{

    @Getter
    private final List<Player> verboseMode = Lists.newArrayList();

}
