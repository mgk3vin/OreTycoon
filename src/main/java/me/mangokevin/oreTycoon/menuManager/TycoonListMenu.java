package me.mangokevin.oreTycoon.menuManager;

import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonType;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TycoonListMenu implements MenuInterface{
    @Override
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(new TycoonHolder(this), 54, "Tycoon List Menu");
        refresh(player, inventory);
        player.openInventory(inventory);
    }

    @Override
    public void refresh(Player player, Inventory inventory) {
        MenuManager.addFiller(inventory, Material.GRAY_STAINED_GLASS_PANE);
        List<TycoonType> tycoonTypes = Arrays.stream(TycoonType.values()).toList();

        int slot = 10;
        for (int i = 0; i < tycoonTypes.size(); i++) {
            if (i > 0 && i % 7 == 0){
                slot += 2;
            }
            TycoonType tycoonType = tycoonTypes.get(i);
            List<String> lore = Arrays.asList(
                    "§8§m-----------------------",
                    ChatColor.GRAY + "Spawn rate: " + tycoonType.getSpawnInterval()/20 + "s",
                    ChatColor.GRAY + "Mining rate " + tycoonType.getMiningInterval()/20 + "s",
                    ChatColor.GRAY + "Sell multiplier: " + tycoonType.getSellMultiplier() + "x",
                    ChatColor.GRAY + "Inventory: " + tycoonType.getDefaultMaxInventoryStorage() + " items",
                    "§8§m-----------------------"
            );
            ItemStack tycoonItem = MenuManager.createItemstack(
                    tycoonType.getMaterial(),
                    1,
                    tycoonType.getName(),
                    lore,
                    false,
                    true,
                    true,
                    "tycoon_display_item"
            );
            inventory.setItem(slot, tycoonItem);
            slot++;
        }
    }

    @Override
    public void handleAction(InventoryClickEvent event) {

    }
}
