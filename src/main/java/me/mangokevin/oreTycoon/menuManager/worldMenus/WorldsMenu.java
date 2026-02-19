package me.mangokevin.oreTycoon.menuManager.worldMenus;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.menuManager.MenuInterface;
import me.mangokevin.oreTycoon.menuManager.MenuManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonWorlds.TycoonWorldManager;
import me.mangokevin.oreTycoon.utility.Console;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldsMenu implements MenuInterface {
    private final OreTycoon plugin;
    private final TycoonWorldManager tycoonWorldManager;

    public WorldsMenu(OreTycoon plugin) {
        this.plugin = plugin;
        this.tycoonWorldManager = plugin.getTycoonWorldManager();
    }

    @Override
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(new TycoonHolder(this), 45, ChatColor.GREEN + "Tycoon worlds");
        refresh(player, inventory);
        player.openInventory(inventory);
    }

    @Override
    public void refresh(Player player, Inventory inventory) {
        MenuManager.addFiller(inventory, Material.GRAY_STAINED_GLASS_PANE);

        Map<UUID, List<String>> playerWorlds = tycoonWorldManager.getPlayerWorlds();

        List<String> worldNames = playerWorlds.get(player.getUniqueId());

        int startSlot = 19;
        for (int i = 0; i < tycoonWorldManager.getMaxWorldsPerPlayer(); i++) {
            if (worldNames == null || worldNames.isEmpty() || i >= worldNames.size()) {
                List<String> lore = Arrays.asList("§8§m-----------------------",
                        ChatColor.YELLOW + "[ Click to Create ]",
                        "§8§m-----------------------");

                ItemStack item = MenuManager.createItemstack(
                        Material.BLACK_STAINED_GLASS_PANE,
                        1,
                        ChatColor.GRAY + "No world yet...",
                        lore,
                        false,
                        true,
                        true,
                        "locked"
                );
                inventory.setItem(startSlot + i, item);
            } else {
                ItemStack worldIcon = MenuManager.createWorldItem(worldNames.get(i), tycoonWorldManager);
                inventory.setItem(startSlot + i, worldIcon);
            }
        }
    }

    @Override
    public void handleAction(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {return;}
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        String action = pdc.get(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING);

        switch (action) {
            case "world_item" -> {
                String worldName = pdc.getOrDefault(TycoonData.WORLD_UID_KEY, PersistentDataType.STRING, "");
                if (worldName.isEmpty()) {
                    Console.error(getClass(), "Invalid world name!");
                    return;
                }
                new WorldSettingsMenu(worldName).open(player);
            }
            case "locked" -> {
                tycoonWorldManager.createTycoonWorld(player);
            }
            case null, default -> {}
        }

    }
}
