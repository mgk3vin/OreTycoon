package me.mangokevin.oreTycoon.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuListener implements Listener {

    public MenuListener() {

    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        Player p = (Player) event.getWhoClicked();

//        if (event.getView().getTitle().equals("Tycoon Menu")){
//            event.setCancelled(true);
//            TycoonHolder tycoonHolder = (TycoonHolder) event.getInventory().getHolder();
//            PersistentDataContainer pdc = meta.getPersistentDataContainer();
////            if (pdc.has(TycoonData.TYCOON_MENU_ITEM_INDEX_KEY, PersistentDataType.INTEGER)) {
////                int index = pdc.get(TycoonData.TYCOON_MENU_ITEM_INDEX_KEY, PersistentDataType.INTEGER);
////
////                TycoonBlock tycoonBlock = blockManager.getTycoonBlockFromIndex(p, index);
////                menuManager.openTycoonGui(p, tycoonBlock, tycoonBlock.isActive());
////            }
//            if (pdc.has(TycoonData.TYCOON_MENU_ITEM_UID_KEY, PersistentDataType.STRING)) {
//                String blockUID = pdc.get(TycoonData.TYCOON_MENU_ITEM_UID_KEY, PersistentDataType.STRING);
//                TycoonBlock tycoonBlock = blockManager.getTycoonBlock(blockUID);
//                //menuManager.openTycoonGui(p, tycoonBlock, tycoonBlock.isActive());
//            }
//            if (event.getSlot() == 49){
//                List<TycoonBlock> blockList = blockManager.getTycoonBlocksFromPlayer(p.getUniqueId());
//
//                for (TycoonBlock block : blockList){
//                    if (item.getType() == Material.LIME_CONCRETE) {
//                        block.setActive(true);
//                        menuManager.refreshTycoonMenu(inventory, p, tycoonHolder.getPage(), true);
//                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.2f, 1.0f);
//                    }else if (item.getType() == Material.RED_CONCRETE) {
//                        block.setActive(false);
//                        menuManager.refreshTycoonMenu(inventory, p,tycoonHolder.getPage(), false);
//                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 1.0f);
//                    }
//                }
//            }
//            // 2. Action auslesen
//            if (pdc.has(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING)) {
//                String action = pdc.get(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING);
//                int currentPage = tycoonHolder.getPage();
//
//                switch (action) {
//                    case "page_next":
//                        menuManager.openTycoonMenu(p, currentPage + 1);
//                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
//                        break;
//
//                    case "page_prev":
//                        menuManager.openTycoonMenu(p, currentPage - 1);
//                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
//                        break;
//
//                    case "tycoon_toggle":
//                        // Hier deine Logik für den Toggle des einzelnen Tycoons
//                        break;
//                }
//            }
        }
//        if (event.getView().getTitle().equals("Tycoon Stats")){
//            if (!(event.getInventory().getHolder() instanceof TycoonHolder holder)) return;
//            TycoonBlock block = holder.getTycoonBlock();
//            event.setCancelled(true);
//            if (event.getSlot() == 13){
//                block.setActive(!block.isActive());
//                menuManager.refreshTycoonGui(inventory, block, block.isActive());
//                p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
//            }
//            if (event.getSlot() == 26){
//                menuManager.openTycoonMenu(p, 0);
//            }
//        }
    }

