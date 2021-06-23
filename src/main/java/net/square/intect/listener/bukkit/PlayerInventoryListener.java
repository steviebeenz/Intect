package net.square.intect.listener.bukkit;

import net.square.intect.menu.MainMenu;
import net.square.intect.menu.ResourceMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PlayerInventoryListener implements Listener
{

    @EventHandler
    public void handle(InventoryClickEvent event)
    {

        if (event.getInventory() == null) return;

        if (event.getInventory().getName() == null) return;

        if (!(event.getWhoClicked() instanceof Player)) return;

        if (event.getCurrentItem() == null)
        {
            event.setCancelled(true);
            return;
        }

        if (event.getCurrentItem().getType() == null)
        {
            event.setCancelled(true);
            return;
        }

        if (event.getCurrentItem().getType() == null)
        {
            event.setCancelled(true);
            return;
        }

        if (event.getCurrentItem() == null)
        {
            event.setCancelled(true);
            return;
        }

        if (event.getCurrentItem().getItemMeta() == null)
        {
            event.setCancelled(true);
            return;
        }

        if (event.getCurrentItem().getItemMeta().getDisplayName() == null)
        {
            event.setCancelled(true);
            return;
        }

        String inventoryName = event.getInventory().getName();
        Player player = (Player) event.getWhoClicked();

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        Material type = event.getCurrentItem().getType();

        if (inventoryName.equalsIgnoreCase(MainMenu.mainTitle))
        {
            if (type == Material.COMMAND && itemName.equalsIgnoreCase("§9Resources"))
            {
                ResourceMenu.build(player);
            }
            else if (type == Material.PAPER && itemName.equalsIgnoreCase("§9Information"))
            {
                MainMenu.init(player);
            }
        }
        else if (inventoryName.equalsIgnoreCase(ResourceMenu.title))
        {
            if (type == Material.COMPASS && itemName.equalsIgnoreCase("§eRefresh"))
            {
                ResourceMenu.build(player);
            }
            else if (type == Material.ARROW && itemName.equalsIgnoreCase("§cBack"))
            {
                MainMenu.init(player);
            }
        }
        event.setCancelled(true);
    }
}
