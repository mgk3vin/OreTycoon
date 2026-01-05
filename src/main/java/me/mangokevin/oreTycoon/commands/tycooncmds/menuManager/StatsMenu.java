package me.mangokevin.oreTycoon.commands.tycooncmds.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class StatsMenu implements MenuInterface {
    private final OreTycoon plugin;
    private final TycoonBlock tycoonBlock;
    private final MenuManager menuManager;
    private final TycoonBlockManager blockManager;

    public StatsMenu(TycoonBlock tycoonBlock, OreTycoon plugin) {
        this.plugin = plugin;
        this.tycoonBlock = tycoonBlock;
        this.menuManager = plugin.getMenuManager();
        this.blockManager = plugin.getBlockManager();
    }
    @Override
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(new TycoonHolder(this), 27, ChatColor.DARK_GRAY + "Tycoon Stats");

        refresh(player, inventory);

        player.openInventory(inventory);
    }
    @Override
    public void refresh(Player player, Inventory inventory) {
        MenuManager.addFiller(inventory, Material.GRAY_STAINED_GLASS_PANE);

        inventory.setItem(13, menuManager.createTycoonItem(tycoonBlock));
        inventory.setItem(26, MenuManager.createItemstack(Material.BARRIER, 1, ChatColor.RED + "Back to Overview", null, false, true));
        if (tycoonBlock.isAutoMinerEnabled()) {
            inventory.setItem(22, MenuManager.createItemstack(Material.IRON_PICKAXE, 1, ChatColor.GREEN + "Auto Miner Enabled", null, true, true));
        } else {
            inventory.setItem(22, MenuManager.createItemstack(Material.IRON_PICKAXE, 1, ChatColor.RED + "Auto Miner Disabled", null, false, true));
        }
        double currentWorth = PriceUtility.calculateWorth(tycoonBlock.getInventory());
        ItemStack worth = MenuManager.createItemstack(
                Material.GREEN_STAINED_GLASS_PANE,
                1,
                ChatColor.GREEN + "Sell all: " + currentWorth + "$",
                null,
                false,
                true
        );
        inventory.setItem(24, worth);
        inventory.setItem(18, MenuManager.createItemstack(Material.CHEST_MINECART, 1, ChatColor.GOLD + "Inventory", null, false, true));

    }

    @Override
    public void handleAction(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        ItemStack item = event.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        switch (event.getSlot()) {
            case 13:
                tycoonBlock.setActive(!tycoonBlock.isActive());
                inventory.setItem(13, menuManager.createTycoonItem(tycoonBlock));
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1f, 1f);
                break;
            case 18:
                new TycoonInventory(tycoonBlock, plugin).open(player);
                break;
            case 22:
                tycoonBlock.setAutoMinerEnabled(!tycoonBlock.isAutoMinerEnabled());
//                open(player);
                refresh(player, inventory);
                break;
            case 24:
                //tycoonBlock.withdrawBalance(player);
                tycoonBlock.sellInventory(tycoonBlock.getInventory(), player);
                refresh(player, inventory);
                //tycoonBlock.sellInventory(tycoonBlock.getInventory(), player);
                //refresh(player, tycoonBlock.getInventory());
                break;
            case 26:
                int itemsPerPage = 14;
                int page;
                if (tycoonBlock.getIndex() >= 0){
                    page = tycoonBlock.getIndex() / itemsPerPage;
                }else{
                    page = 0;
                }
                new OverviewMenu(plugin, page).open(player);
                break;
            default:
                break;
        }

    }
    public TycoonBlock getTycoonBlock() {
        return tycoonBlock;
    }
}
