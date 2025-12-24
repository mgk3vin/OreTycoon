package me.mangokevin.oreTycoon.commands.tycooncmds.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class MenuListener implements Listener {

    private final OreTycoon plugin;
    private final MenuManager menuManager;
    public MenuListener(OreTycoon plugin) {
        this.plugin = plugin;
        this.menuManager = plugin.getMenuManager();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        Player p = (Player) event.getWhoClicked();

        if (!(event.getInventory().getHolder() instanceof TycoonHolder holder)) return;

        TycoonBlock block = holder.getTycoonBlock();
        if (event.getView().getTitle().equals("Tycoon Stats")){

            event.setCancelled(true);
            if (event.getSlot() == 13){
                block.setActive(!block.isActive());
                menuManager.refreshTycoonGui(inventory, block, block.isActive());
                p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
            }
        }

//        PersistentDataContainer pdc = meta.getPersistentDataContainer();
//        if (event.getView().getTitle().equals("Tycoon Stats")) {
//            event.setCancelled(true);
//            if (pdc.has(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING)) {
//                String value = pdc.get(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING);
//                if (value == null) return;
//                if (value.equalsIgnoreCase("tycoon_toggle")) {
//                    block.setActive(!block.isActive());
//                }
//            }
//
//        }



    }
}
