package me.mangokevin.oreTycoon.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.PriceUtility;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
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
import org.mvplugins.multiverse.external.vavr.collection.List;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TycoonSpawnBlocksMenu implements MenuInterface{

    private final TycoonBlock tycoonBlock;
    private final OreTycoon plugin;

    public TycoonSpawnBlocksMenu(TycoonBlock tycoonBlock, OreTycoon plugin) {
        this.tycoonBlock = tycoonBlock;
        this.plugin = plugin;
    }

    @Override
    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(new TycoonHolder(this), 54, ChatColor.GOLD + "Spawn Blocks Menu");
        refresh(player, inv);
        player.openInventory(inv);
    }

    @Override
    public void refresh(Player player, Inventory inventory) {
        MenuManager.addFiller(inventory, Material.GRAY_STAINED_GLASS_PANE);

        Map<Material, Boolean> activeRessourceMaterialsMap = tycoonBlock.getActiveRessourceMaterialsMap();

        Map<Material, Integer> spawnBlocksList = tycoonBlock.getTycoonType().getResources();
        int startIndex = 10;
        int itemPerRow = 1;

        int totalWeight = tycoonBlock.getTotalActiveWeight();

        for (Map.Entry<Material, Integer> entry : spawnBlocksList.entrySet()) {
            //for every Material
            Material material = entry.getKey();
            Integer chance = entry.getValue();

            boolean isActive = activeRessourceMaterialsMap.get(material);

            //effective Chance
            double effectiveChance = 0.0;
            if(isActive && totalWeight > 0) {
                effectiveChance = Math.round(((double) chance / totalWeight) * 100.0);
            }
            ItemStack spawnBlock = MenuManager.createItemstack(material,
                    1,
                    material.toString(),
                    Arrays.asList("§8§m-----------------------",
                            ChatColor.GRAY + "Spawn Chance: " + ChatColor.YELLOW + effectiveChance + "%",
                            ChatColor.GRAY + "Worth: " + ChatColor.GREEN + PriceUtility.calculateWorthFormatted(new ItemStack(material)),
                            "§8§m-----------------------"),
                    isActive,
                    true,
                    true,
                    "spawn_block");

            if (itemPerRow >= 8){
                //if row is full
                Console.debug("[TycoonSpawnBlocksMenu] Row full: " + itemPerRow + " resetting, -> starting: " + startIndex);
                startIndex += 2;    //skip to next row
                itemPerRow = 1;     //reset row counter
            }
            inventory.setItem(startIndex, spawnBlock);   //set to start index slot and get Material
            Console.debug("[TycoonSpawnBlocksMenu] setting inventory item: " + startIndex + " Item -> " + itemPerRow + "/7");
            itemPerRow++;   //next item counter
            startIndex++;   //next slot counter
        }

        ItemStack returnItem = MenuManager.createItemstack(Material.BARRIER,
                1,
                ChatColor.RED + "<- Back to Menu",
                null,
                false,
                true,
                true,
                "return");
        inventory.setItem(53, returnItem);
    }

    @Override
    public void handleAction(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        String action = pdc.get(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING);

        switch (action) {
            case "spawn_block":
                Map<Material, Boolean> activeResources = tycoonBlock.getActiveRessourceMaterialsMap();
                Material clickedMaterial = item.getType();

                boolean isActive = activeResources.get(clickedMaterial);
                activeResources.put(clickedMaterial, !isActive);

                Console.debug("[TycoonSpawnBlocksMenu] Setting inventory item: " + !isActive + " Item -> " + item.getType().name());
                tycoonBlock.setActiveRessourceMaterialsMap(activeResources);

                refresh(player, inventory);
                break;
            case "return":
                new StatsMenu(tycoonBlock, plugin).open(player);
                break;
            case null, default:
                break;
        }

    }
}
