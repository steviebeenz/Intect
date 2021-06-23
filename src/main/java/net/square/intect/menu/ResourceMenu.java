package net.square.intect.menu;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.square.intect.Intect;
import net.square.intect.menu.item.ItemAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ResourceMenu {

    private static final ItemStack BLACK_GLASS_PANE = ItemAPI.getItem(Material.STAINED_GLASS_PANE, 15);

    public static final String title = Intect.getIntect().getPrefix() + "§7Resources";

    public static void build(Player player) {

        Inventory inventory = Bukkit.createInventory(player, 9 * 3, title);

        for (int i = 0; i < 27; i++)
        {
            inventory.setItem(i, BLACK_GLASS_PANE);
        }

        inventory.setItem(8, ItemAPI.getItem(Material.ARROW, "§cBack", 1));
        inventory.setItem(7, ItemAPI.getItem(Material.COMPASS, "§eRefresh", 1));

        int cores = Runtime.getRuntime().availableProcessors();

        int mb = 1024 * 1024;

        long freeMemory = Runtime.getRuntime().freeMemory() / mb;

        long maxMemory = Runtime.getRuntime().maxMemory() / mb;

        List<String> lore = new ArrayList<>();

        lore.add(String.format("§9Free: §f%d MB", freeMemory));
        lore.add(String.format("§9Max: §f%d MB", maxMemory));

        inventory.setItem(11, ItemAPI.getItem(Material.COMMAND, "§9Cores", cores));

        List<String> ticks = new ArrayList<>();

        int i = 0;

        for (double recentTp : MinecraftServer.getServer().recentTps) {
            ticks.add(String.format("§8(%s) §f%.2f", i, recentTp));
            i++;
        }

        inventory.setItem(13, ItemAPI.getItem(Material.BOOK, "§9Ticks", 1, ticks));

        inventory.setItem(15, ItemAPI.getItem(Material.PAPER, "§9RAM", 1, lore));

        player.openInventory(inventory);
    }
}
