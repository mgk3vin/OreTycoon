package me.mangokevin.oreTycoon.commands.tycooncmds.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlockManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
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
        Inventory inventory = Bukkit.createInventory(new TycoonHolder(this), 27, ChatColor.GREEN + "Tycoon Stats");

        MenuManager.addFiller(inventory, Material.GRAY_STAINED_GLASS_PANE);

        inventory.setItem(13, menuManager.createTycoonItem(tycoonBlock));
        inventory.setItem(26, MenuManager.createItemstack(Material.BARRIER, 1, ChatColor.RED + "⬅️Back to Overview", null, false));
        player.openInventory(inventory);
    }

    @Override
    public void handleAction(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if (event.getSlot() == 26) {
            menuManager.openTycoonOverview(player, 0);
        }
        if (event.getSlot() == 13) {
            tycoonBlock.setActive(!tycoonBlock.isActive());
            event.getInventory().setItem(13, menuManager.createTycoonItem(tycoonBlock));
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1f, 1f);
        }
    }
}
