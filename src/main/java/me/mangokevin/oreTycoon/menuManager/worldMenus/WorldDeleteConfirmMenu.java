package me.mangokevin.oreTycoon.menuManager.worldMenus;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.menuManager.MenuInterface;
import me.mangokevin.oreTycoon.menuManager.MenuManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonWorlds.TycoonWorldManager;
import me.mangokevin.oreTycoon.utility.CooldownManager;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class WorldDeleteConfirmMenu implements MenuInterface {
    private final OreTycoon plugin;
    private final TycoonWorldManager worldManager;
    private final CooldownManager deletionCooldown = new CooldownManager(1000 * 5);

    private BukkitRunnable runnable;

    private final String worldName;

    public WorldDeleteConfirmMenu(OreTycoon plugin, String worldName) {
        this.plugin = plugin;
        this.worldManager = plugin.getTycoonWorldManager();
        this.worldName = worldName;
    }


    @Override
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(new TycoonHolder(this), 27, ChatColor.RED + "World Delete Confirm");
        player.openInventory(inventory);
        deletionCooldown.setCooldown(player.getUniqueId());
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                refresh(player, inventory);
                if (!deletionCooldown.isOnCooldown(player.getUniqueId())) {
                    this.cancel();
                }
            }
        };
        runnable.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void refresh(Player player, Inventory inventory) {
        if (deletionCooldown.isOnCooldown(player.getUniqueId())) {
            //11 Confirm Pending
            List<String> confirmPendingLore = Arrays.asList(
                    "§8§m-----------------------",
                    ChatColor.RED + "You need to wait " + deletionCooldown.getRemainingCooldownSeconds(player.getUniqueId()) +"s before confirming!",
                    "§8§m-----------------------");
            ItemStack confirmItem = MenuManager.createItemstack(
                    Material.GRAY_STAINED_GLASS_PANE,
                    1,
                    ChatColor.RED + "Delete World (forever)",
                    confirmPendingLore,
                    false,
                    true,
                    true,
                    "delete_pending"
            );
            inventory.setItem(11, confirmItem);
        } else {
            //11 Confirm
            List<String> confirmLore = Arrays.asList(
                    "§8§m-----------------------",
                    ChatColor.RED + "Permanently delete " + worldName + "!",
                    ChatColor.RED + "You will loose everything in this world",
                    "§8§m-----------------------");
            ItemStack confirmItem = MenuManager.createItemstack(
                    Material.LIME_STAINED_GLASS_PANE,
                    1,
                    ChatColor.GREEN + "Delete World (forever)",
                    confirmLore,
                    false,
                    true,
                    true,
                    "delete_confirm"
            );
            inventory.setItem(11, confirmItem);
        }
        //15 Cancel
        ItemStack cancelItem = MenuManager.createItemstack(
                Material.RED_STAINED_GLASS_PANE,
                1,
                ChatColor.RED + "Cancel World Deletion",
                null,
                false,
                true,
                true,
                "delete_cancel"
        );
        inventory.setItem(15, cancelItem);
    }

    @Override
    public void handleAction(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String action = pdc.get(TycoonData.MENU_ACTION_KEY,  PersistentDataType.STRING);
        switch (action) {
            case "delete_confirm" -> {
                worldManager.deleteTycoonWorld(player, worldName);
                runnable.cancel();
                new WorldsMenu(plugin).open(player);
            }
            case "delete_cancel" -> {
                player.closeInventory();
            }
            case "delete_pending" -> {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }

            case null, default -> {}
        }
        refresh(player, inventory);
    }
    @Override
    public void onClose(Player player) {
        runnable.cancel();
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1.0f, 1.0f);
    }
}
