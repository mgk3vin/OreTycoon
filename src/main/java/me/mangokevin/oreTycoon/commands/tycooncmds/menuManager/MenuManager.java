package me.mangokevin.oreTycoon.commands.tycooncmds.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlockManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

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
        ItemStack backToMenu = createItemstack(Material.BARRIER, 1, ChatColor.RED + "Return to the main menu", null, false);
        inventory.setItem(26, backToMenu);
        openMenu(p, inventory);

    }
    public void openTycoonMenu(Player p) {
        Inventory inventory = Bukkit.createInventory(p , 45, "Tycoon Menu");
        List<TycoonBlock> tycoonBlockList = blockManager.getTycoonBlocksFromPlayer(p.getUniqueId());

        addFiller(inventory, Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < tycoonBlockList.size(); i++) {
            TycoonBlock block = tycoonBlockList.get(i);

            List<String> lore = Arrays.asList("§8§m-----------------------",
                    ChatColor.GRAY + "Status: " + ChatColor.RESET + block.isActiveFormatted(),
                    ChatColor.GRAY + "Level: " + block.getLevel(),
                    block.getProgressBar(20) + " " + block.getProgress() + "%",
                    ChatColor.GRAY + "Spawn rate: " + block.getSpawnInterval(),
                    "§8§m-----------------------");
            ItemStack stats = createItemstack(block.getMaterial(), 1, block.getTycoonType().getName(), lore, block.isActive());
            ItemMeta statsmeta = stats.getItemMeta();
            if (statsmeta == null)return;
            statsmeta.getPersistentDataContainer().set(TycoonData.TYCOON_MENU_ITEM_KEY, PersistentDataType.STRING, "tycoon_menu_item");
            statsmeta.getPersistentDataContainer().set(TycoonData.TYCOON_MENU_ITEM_INDEX_KEY, PersistentDataType.INTEGER, i + 1);
            stats.setItemMeta(statsmeta);

            inventory.setItem(19 + i, stats);
        }
        if (tycoonBlockList.size() < blockManager.getMaxBlocksPerPlayer()) {
            for (int i = tycoonBlockList.size(); i < blockManager.getMaxBlocksPerPlayer(); i++) {
                ItemStack emptySlot = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemMeta meta = emptySlot.getItemMeta();
                if (meta == null) return;
                meta.setDisplayName(ChatColor.GRAY + "" + ChatColor.ITALIC + "Empty Slot");
                meta.setLore(Arrays.asList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Place down more tycoons to manage them here"));

                emptySlot.setItemMeta(meta);
                inventory.setItem(19 + i, emptySlot);
            }
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
