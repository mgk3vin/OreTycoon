package me.mangokevin.oreTycoon.commands.tycooncmds.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.PriceUtility;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class TycoonInventory implements MenuInterface {

    private final TycoonBlock tycoonBlock;
    private final OreTycoon plugin;

    public TycoonInventory(TycoonBlock tycoonBlock, OreTycoon plugin) {
        this.tycoonBlock = tycoonBlock;
        this.plugin = plugin;
    }

    @Override
    public void open(Player player) {
        Inventory inventory = tycoonBlock.getInventory();
        refresh(player, inventory);
        player.openInventory(inventory);
    }

    @Override
    public void refresh(Player player, Inventory inventory) {
        for (int i = 27; i < 36; i++) {
            ItemStack item = MenuManager.createItemstack(
                    Material.GRAY_STAINED_GLASS_PANE,
                    1,
                    " ",
                    null,
                    false,
                    true
            );
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;
            meta.getPersistentDataContainer().set(TycoonData.MENU_ITEM_KEY, PersistentDataType.STRING, "menu_item");
            meta.getPersistentDataContainer().set(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING, "filler_item");
            item.setItemMeta(meta);
            inventory.setItem(i, item);
        }
        double currentWorth = PriceUtility.calculateWorth(inventory);
        ItemStack item = MenuManager.createItemstack(
                Material.GREEN_STAINED_GLASS_PANE,
                1,
                ChatColor.GREEN + "Sell all: " + "$" + PriceUtility.formatMoney(currentWorth) ,
                null,
                false,
                true
        );
        ItemStack backToMenu = MenuManager.createItemstack(
                Material.BARRIER,
                1,
                ChatColor.RED + "<- Back",
                null,
                false,
                true
        );
        ItemMeta backToMenuItemMeta = backToMenu.getItemMeta();
        if (backToMenuItemMeta == null) return;
        backToMenuItemMeta.getPersistentDataContainer().set(TycoonData.MENU_ITEM_KEY, PersistentDataType.STRING, "menu_item");
        backToMenuItemMeta.getPersistentDataContainer().set(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING, "return");
        backToMenu.setItemMeta(backToMenuItemMeta);
        inventory.setItem(35, backToMenu);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(TycoonData.MENU_ITEM_KEY, PersistentDataType.STRING, "menu_item");
        meta.getPersistentDataContainer().set(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING, "sell_all");
        item.setItemMeta(meta);
        inventory.setItem(31, item);
    }
    public boolean addItem(ItemStack item){
        Inventory inv = tycoonBlock.getInventory();
        for (int i = 0; i < 27; i++){
            ItemStack slotItem = inv.getItem(i);

            // 1. Slot ist leer
            if (slotItem == null || slotItem.getType() == Material.AIR) {
                inv.setItem(i, item);
                return true;
            }

            // 2. Slot hat das gleiche Item und noch Platz
            if (slotItem.isSimilar(item)) {
                int canAdd = slotItem.getMaxStackSize() - slotItem.getAmount();
                if (canAdd >= item.getAmount()) {
                    slotItem.setAmount(slotItem.getAmount() + item.getAmount());
                    return true;
                } else if (canAdd > 0) {
                    // Teilweise füllen und Rest weitersuchen (optional)
                    slotItem.setAmount(slotItem.getMaxStackSize());
                    item.setAmount(item.getAmount() - canAdd);
                }
            }
        }
        return false;
    }

    @Override
    public void handleAction(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        ItemStack item = event.getCurrentItem();
        if (item == null|| item.getType() == Material.AIR) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String action = pdc.get(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING);
        switch (action) {
            case "sell_all":
                tycoonBlock.sellInventory(inventory, player);
                refresh(player, inventory);
                break;
            case "return":
                new StatsMenu(tycoonBlock, plugin).open(player);
            case "filler_item":
                break;
            case null:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + action);
        }
    }
    public TycoonBlock getTycoonBlock() {
        return tycoonBlock;
    }
}
