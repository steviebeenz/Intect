package net.square.intect.menu;

import com.google.common.collect.Lists;
import net.square.intect.Intect;
import net.square.intect.checks.objectable.Check;
import net.square.intect.menu.item.ItemAPI;
import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MainMenu
{

    private static final ItemStack BLACK_GLASS_PANE = ItemAPI.getItem(Material.STAINED_GLASS_PANE, 15);

    public static final String mainTitle = Intect.getIntect().getPrefix() + "Menu";

    public static void init(Player player)
    {
        final Inventory inventory = Bukkit.createInventory(player, 3 * 9, mainTitle);

        for (int i = 0; i < 27; i++)
        {
            inventory.setItem(i, BLACK_GLASS_PANE);
        }

        //  0  1  2  3  4  5  6  7  8
        //  9 10 11 12 13 14 15 16 17
        // 18 19 20 21 22 23 24 25 26

        List<String> checksLore = Lists.newArrayList();
        List<String> cache = Lists.newArrayList();
        int id = 0;
        checksLore.add("");

        for (Check check : PlayerStorage.storageHashMap.get(player).getChecks())
        {
            String name = check.getCheckInfo().name();
            if (!cache.contains(name))
            {
                checksLore.add(String.format("§f%s §7(ID: %d)", name, id));
                cache.add(name);
                id++;
            }
        }

        inventory.setItem(11, ItemAPI.getItem(Material.BOOK, "§9Checks", 1, checksLore));

        List<String> information = Lists.newArrayList();
        information.add("");
        information.add("§9Adapter: §f" + Intect.getIntect().getPacketManager().getPacketReceivor().getName());
        information.add("§9Build: §fBuild#" + Intect.getIntect().getDescription().getVersion());
        information.add("§9Storage: §fSQLITE");
        information.add("§9Discord: §cDisabled");
        information.add("");
        information.add("§9Total Bans: §cNo database");
        information.add("§9Total Logs: §cNo database");
        information.add("");
        information.add("§7Click to refresh");
        inventory.setItem(13, ItemAPI.getItem(Material.PAPER, "§9Information", 1, information));


        List<String> resources = Lists.newArrayList();
        resources.add("");
        resources.add("§7Take a look at your workload");
        inventory.setItem(15, ItemAPI.getItem(Material.COMMAND, "§9Resources", 1, resources));

        player.openInventory(inventory);
    }
}