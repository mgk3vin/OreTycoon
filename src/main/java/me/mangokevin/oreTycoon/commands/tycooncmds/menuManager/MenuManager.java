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

import javax.swing.text.StyledEditorKit;
import java.util.ArrayList;
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
    public void openTycoonMenu(Player p, int page) {
        List<TycoonBlock> tycoonBlockList = blockManager.getTycoonBlocksFromPlayer(p.getUniqueId());
        Inventory inventory = Bukkit.createInventory(new TycoonHolder(tycoonBlockList, page), 54, "Tycoon Menu");

        List<Integer> usableSlots = getUsableSlots();
        int itemsPerPage = usableSlots.size(); // Das sind 14
        int startIndex = page * itemsPerPage;

        // 1. Zuerst den Hintergrund füllen
        addFiller(inventory, Material.GRAY_STAINED_GLASS_PANE);

        // 2. NUR EINE Schleife nutzen, um die 14 Slots der aktuellen Seite zu füllen
        for (int i = 0; i < itemsPerPage; i++) {
            int tycoonIndex = startIndex + i;
            int slot = usableSlots.get(i); // Hol den passenden Slot (10-16 oder 28-34)

            if (tycoonIndex < tycoonBlockList.size()) {
                // Tycoon vorhanden -> Item setzen
                TycoonBlock block = tycoonBlockList.get(tycoonIndex);
                //inventory.setItem(slot, createTycoonItem(block));
                inventory.setItem(slot, createTycoonItem(block));
            } else if (tycoonIndex < blockManager.getMaxBlocksPerPlayer()) {
                // Slot ist leer, aber Tycoon-Platz ist theoretisch verfügbar
                inventory.setItem(slot, createItemstack(Material.BLACK_STAINED_GLASS_PANE, 1,
                        ChatColor.GRAY + "Freier Slot", null, false));
            }
        }

        // Vorherige Seite (Slot 45)
        if (page > 0) {
            ItemStack prev = createItemstack(Material.ARROW, 1, "§e<- Seite " + page, null, false);
            ItemMeta meta = prev.getItemMeta();
            meta.getPersistentDataContainer().set(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING, "page_prev");
            prev.setItemMeta(meta);
            inventory.setItem(45, prev);
        }

        // Nächste Seite (Slot 53)
        //if (startIndex + itemsPerPage < tycoonBlockList.size()) {
        if (startIndex + itemsPerPage < blockManager.getMaxBlocksPerPlayer()) {
            ItemStack next = createItemstack(Material.ARROW, 1, "§eSeite " + (page + 2) + " ->", null, false);
            ItemMeta meta = next.getItemMeta();
            meta.getPersistentDataContainer().set(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING, "page_next");
            next.setItemMeta(meta);
            inventory.setItem(53, next);
        }

        // 4. Toggle All Button
        inventory.setItem(49, createItemstack(Material.LIME_CONCRETE, 1, ChatColor.GREEN + "Turn all Tycoons on!", null, false));

        openMenu(p, inventory);
    }

    private List<Integer> getUsableSlots() {
        List<Integer> slots = new ArrayList<>();
        // Reihe 2 (Slots 10 bis 16)
        for (int i = 10; i <= 16; i++) {
            slots.add(i);
        }
        // Reihe 4 (Slots 28 bis 34)
        for (int i = 28; i <= 34; i++) {
            slots.add(i);
        }
        return slots;
    }
    public void refreshTycoonMenu(Inventory inventory,Player p, Boolean isActive){
        List<TycoonBlock> tycoonBlockList = blockManager.getTycoonBlocksFromPlayer(p.getUniqueId());

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
            statsmeta.getPersistentDataContainer().set(TycoonData.TYCOON_MENU_ITEM_UID_KEY, PersistentDataType.STRING, block.getBlockUID());
            stats.setItemMeta(statsmeta);

            inventory.setItem(19 + i, stats);
        }

        ItemStack toggleAllTycoons;
        if (isActive) {
            toggleAllTycoons = createItemstack(Material.RED_CONCRETE, 1, ChatColor.RED + "Turn all Tycoons off!", null, false);
        }else{
            toggleAllTycoons = createItemstack(Material.LIME_CONCRETE, 1, ChatColor.GREEN + "Turn all Tycoons on!", null, false);
        }

        inventory.setItem(28, toggleAllTycoons);
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
                block.getTycoonType().getName(),
                lore,
                glint);
        ItemMeta statsmeta = stats.getItemMeta();
        if (statsmeta == null) return null;
        statsmeta.getPersistentDataContainer().set(TycoonData.TYCOON_MENU_ITEM_KEY, PersistentDataType.STRING, "tycoon_menu_item");
        //statsmeta.getPersistentDataContainer().set(TycoonData.TYCOON_MENU_ITEM_INDEX_KEY, PersistentDataType.INTEGER, i + 1);
        statsmeta.getPersistentDataContainer().set(TycoonData.TYCOON_MENU_ITEM_UID_KEY, PersistentDataType.STRING, block.getBlockUID());
        stats.setItemMeta(statsmeta);
        return stats;
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
