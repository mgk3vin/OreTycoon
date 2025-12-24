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
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.C;

import java.util.Arrays;
import java.util.List;

public class MenuManager {

    private final OreTycoon plugin;
    private final TycoonBlockManager blockManager;



    public MenuManager(OreTycoon plugin, TycoonBlockManager blockManager) {
        this.plugin = plugin;
        this.blockManager = blockManager;
    }


    public void openTycoonGui(Player p, TycoonBlock block, Boolean glint) {
        Inventory inventory = Bukkit.createInventory(new TycoonHolder(block), 27, "Tycoon Stats");

        addFiller(inventory, Material.GRAY_STAINED_GLASS_PANE);

        List<String> lore = Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "Status: " + ChatColor.RESET + block.isActiveFormatted(),
                ChatColor.GRAY + "Level: " + block.getLevel(),
                block.getProgressBar(20) + " " + block.getProgress() + "%",
                ChatColor.GRAY + "Spawn rate: " + block.getSpawnInterval(),
                "§8§m-----------------------");

        ItemStack stats = createItemstack(
                block.getMaterial(),
                1,
                block.getTycoonType().getName(),
                lore,
                glint);
        ItemMeta statsmeta = stats.getItemMeta();
        if (statsmeta != null){
            statsmeta.getPersistentDataContainer().set(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING, "tycoon_toggle");
            inventory.setItem(13, stats);
        }

        openMenu(p, inventory);

    }
    public void refreshTycoonGui(Inventory inventory, TycoonBlock block, Boolean glint) {
        List<String> lore = Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "Status: " + ChatColor.RESET + block.isActiveFormatted(),
                ChatColor.GRAY + "Level: " + block.getLevel(),
                block.getProgressBar(20) + " " + block.getProgressPercentage() + "%",
                ChatColor.GRAY + "Spawn rate: " + block.getSpawnInterval(),
                "§8§m-----------------------");

        ItemStack stats = createItemstack(
                block.getMaterial(),
                1,
                block.getTycoonType().getName(),
                lore,
                glint);
        ItemMeta statsmeta = stats.getItemMeta();
        if (statsmeta != null){
            statsmeta.getPersistentDataContainer().set(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING, "tycoon_toggle");
            inventory.setItem(13, stats);
        }
    }

    public static ItemStack createItemstack(Material material, int amount, String s, List<String> l, Boolean b){
        ItemStack itemstack = new ItemStack(material, amount);
        ItemMeta meta = itemstack.getItemMeta();
        if(meta != null){
            meta.setDisplayName(s);
            meta.setLore(l);
            meta.setEnchantmentGlintOverride(b);
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
        return createItemstack(material, 1, " ", null, false);
    }
    public static void openMenu(Player player, Inventory inventory) {
        player.openInventory(inventory);
    }
    public static void closeMenu(Player player) {
        player.closeInventory();
    }
}
