package me.mangokevin.oreTycoon.commands.tycooncmds.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlockManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.C;

import javax.swing.text.StyledEditorKit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuManager {
    private final OreTycoon plugin;

    public MenuManager(OreTycoon plugin) {
        this.plugin = plugin;
    }

    public void openTycoonStats(TycoonBlock block, Player player) {
        new StatsMenu(block, plugin).open(player);
    }
    public void openTycoonOverview(Player player, int page) {
        new OverviewMenu(plugin, page).open(player);
    }
    public ItemStack createTycoonItem(TycoonBlock block){
        Boolean glint = block.isActive();
        List<String> lore = Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "Status: " + ChatColor.RESET + block.isActiveFormatted(),
                ChatColor.GRAY + "Level: " + block.getLevel(),
                block.getProgressBar(20) + " " + block.getProgressPercentage() + "%",
                ChatColor.GRAY + "Spawn rate: " + block.getSpawnInterval(),
                "§8§m-----------------------");

        ItemStack stats = createItemstack(
                block.getMaterial(),
                1,
                block.getTycoonType().getName() + " #" + block.getIndex(),
                lore,
                glint,
                true);
        ItemMeta statsmeta = stats.getItemMeta();
        if (statsmeta == null) return null;
        statsmeta.getPersistentDataContainer().set(TycoonData.TYCOON_MENU_ITEM_KEY, PersistentDataType.STRING, "tycoon_menu_item");
        statsmeta.getPersistentDataContainer().set(TycoonData.TYCOON_MENU_ITEM_UID_KEY, PersistentDataType.STRING, block.getBlockUID());
        stats.setItemMeta(statsmeta);
        return stats;
    }
    public static ItemStack createItemstack(Material material, int amount, String name, List<String> lore, Boolean glint, Boolean hideAttributes){
        ItemStack itemstack = new ItemStack(material, amount);
        ItemMeta meta = itemstack.getItemMeta();
        if(meta != null){
            meta.setDisplayName(name);
            meta.setLore(lore);
            meta.setEnchantmentGlintOverride(glint);
            if (hideAttributes){
                meta.addItemFlags(ItemFlag.values());
            }
            itemstack.setItemMeta(meta);
        }
        return itemstack;
    }

    public static void addFiller(Inventory inventory, Material material){
        ItemStack filler = createFiller(material);
        int size = inventory.getSize();

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, filler);
        }
        for (int i = size - 9; i < size; i++) {
            inventory.setItem(i, filler);
        }
        for (int i = 0; i < size; i+=9) {
            inventory.setItem(i, filler);
            inventory.setItem(i+8, filler);
        }

    }
    public static ItemStack  createFiller(Material material){
        return createItemstack(material, 1, " ", null, false, true);
    }
}
