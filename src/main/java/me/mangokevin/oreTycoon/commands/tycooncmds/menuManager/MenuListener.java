package me.mangokevin.oreTycoon.commands.tycooncmds.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlockManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import org.bukkit.ChatColor;
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
    private final TycoonBlockManager blockManager;
    public MenuListener(OreTycoon plugin) {
        this.plugin = plugin;
        this.menuManager = plugin.getMenuManager();
        this.blockManager = plugin.getBlockManager();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        Player p = (Player) event.getWhoClicked();


        if (event.getView().getTitle().equals("Tycoon Menu")){
            event.setCancelled(true);

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
//            if (pdc.has(TycoonData.TYCOON_MENU_ITEM_INDEX_KEY, PersistentDataType.INTEGER)) {
//                int index = pdc.get(TycoonData.TYCOON_MENU_ITEM_INDEX_KEY, PersistentDataType.INTEGER);
//
//                TycoonBlock tycoonBlock = blockManager.getTycoonBlockFromIndex(p, index);
//                menuManager.openTycoonGui(p, tycoonBlock, tycoonBlock.isActive());
//            }
            if (pdc.has(TycoonData.TYCOON_MENU_ITEM_UID_KEY, PersistentDataType.STRING)) {
                String blockUID = pdc.get(TycoonData.TYCOON_MENU_ITEM_UID_KEY, PersistentDataType.STRING);
                TycoonBlock tycoonBlock = blockManager.getTycoonBlock(blockUID);
                menuManager.openTycoonGui(p, tycoonBlock, tycoonBlock.isActive());
            }
        }

        if (!(event.getInventory().getHolder() instanceof TycoonHolder holder)) return;

        TycoonBlock block = holder.getTycoonBlock();
        if (event.getView().getTitle().equals("Tycoon Stats")){

            event.setCancelled(true);
            if (event.getSlot() == 13){
                block.setActive(!block.isActive());
                menuManager.refreshTycoonGui(inventory, block, block.isActive());
                p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
            }
            if (event.getSlot() == 26){
                menuManager.openTycoonMenu(p);
            }
        }

    }
}
