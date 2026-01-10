package me.mangokevin.oreTycoon.tycoonManagment;

import me.mangokevin.oreTycoon.commands.tycooncmds.utility.StorageUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.units.qual.N;

import java.util.stream.Collectors;

public class TycoonData {
    private static NamespacedKey TYCOON_BLOCK;
    private static NamespacedKey LEVEL;
    public static NamespacedKey XP;
    private static NamespacedKey CREATION;
    private static NamespacedKey MATERIAL;
    private static NamespacedKey SPAWN_INTERVAL;
    private static NamespacedKey CREATION_TIME;
    public static NamespacedKey TYPE_KEY;
    public static NamespacedKey INVENTORY_KEY;
    public static NamespacedKey MENU_ACTION_KEY;
    public static NamespacedKey TYCOON_MENU_ITEM_KEY;
    public static NamespacedKey MENU_ITEM_KEY;
    public static NamespacedKey TYCOON_MENU_ITEM_INDEX_KEY;
    public static NamespacedKey TYCOON_MENU_ITEM_UID_KEY;
    public static NamespacedKey BLOCK_IS_AUTOMINED_KEY;

    public static NamespacedKey MENU_ACTION_TYCOON_LEVEL_KEY;
    //========== Upgrade Keys ==========
    public static NamespacedKey TYCOON_SPAWN_RATE_LEVEL_KEY;
    public static NamespacedKey TYCOON_MINING_RATE_LEVEL_KEY;
    public static NamespacedKey TYCOON_SELL_MULTIPLIER_LEVEL_KEY;
    public static NamespacedKey TYCOON_MAX_INVENTORY_STORAGE_KEY;
    public static NamespacedKey TYCOON_CLAIMED_LEVELS_KEY;
    //========== Upgrade Keys ==========


    // Wird einmal in der onEnable deiner Main aufgerufen: TycoonData.init(this);
    public static void init(Plugin plugin) {
        TYCOON_BLOCK = new NamespacedKey(plugin, "IS_TYCOON_BLOCK");
        LEVEL = new NamespacedKey(plugin, "level");
        XP = new NamespacedKey(plugin, "xp");
        CREATION = new NamespacedKey(plugin, "creation_date");
        MATERIAL = new NamespacedKey(plugin, "material");
        SPAWN_INTERVAL = new NamespacedKey(plugin, "spawn_interval");
        CREATION_TIME = new NamespacedKey(plugin, "creation_time");
        TYPE_KEY = new NamespacedKey(plugin, "type");
        INVENTORY_KEY = new NamespacedKey(plugin, "inventory");
        MENU_ACTION_KEY = new NamespacedKey(plugin, "menu_action");
        TYCOON_MENU_ITEM_KEY = new NamespacedKey(plugin, "tycoon_menu_item");
        TYCOON_MENU_ITEM_INDEX_KEY = new NamespacedKey(plugin, "tycoon_menu_item_index");
        TYCOON_MENU_ITEM_UID_KEY = new  NamespacedKey(plugin, "tycoon_menu_item_uid");
        BLOCK_IS_AUTOMINED_KEY = new NamespacedKey(plugin, "block_is_automined");
        MENU_ITEM_KEY = new NamespacedKey(plugin, "menu_item");
        MENU_ACTION_TYCOON_LEVEL_KEY = new NamespacedKey(plugin, "menu_action_tycoon_level");

        //========== Upgrade Keys ==========
        TYCOON_SPAWN_RATE_LEVEL_KEY = new NamespacedKey(plugin, "tycoon_spawn_rate_level");
        TYCOON_MINING_RATE_LEVEL_KEY = new NamespacedKey(plugin, "tycoon_mining_rate_level");
        TYCOON_SELL_MULTIPLIER_LEVEL_KEY = new NamespacedKey(plugin, "tycoon_sell_multiplier_level");
        TYCOON_MAX_INVENTORY_STORAGE_KEY = new NamespacedKey(plugin, "tycoon_max_inventory_storage");
        TYCOON_CLAIMED_LEVELS_KEY = new NamespacedKey(plugin, "tycoon_claimed_levels");
        //========== Upgrade Keys ==========
    }
    // Speichert die Daten eines Tycoons auf ein Item
    public static void writeToItem(ItemStack item, int level, int xp, long creation, Material material, int spawnInterval, long creationTime, String type, Inventory inventory, TycoonUpgrades upgrades) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(TYCOON_BLOCK, PersistentDataType.BYTE,  (byte) 1);
        pdc.set(TYPE_KEY, PersistentDataType.STRING, type);
        pdc.set(LEVEL, PersistentDataType.INTEGER, level);
        pdc.set(XP, PersistentDataType.INTEGER, xp);
        pdc.set(CREATION, PersistentDataType.LONG, creation);
        pdc.set(MATERIAL, PersistentDataType.STRING, material.toString());
        pdc.set(SPAWN_INTERVAL, PersistentDataType.INTEGER, spawnInterval);
        pdc.set(CREATION_TIME, PersistentDataType.LONG, creationTime);

        byte[] byteArray = StorageUtils.toByteArray(inventory);

        pdc.set(INVENTORY_KEY, PersistentDataType.BYTE_ARRAY, byteArray);

        //========== Upgrade Keys ==========
        pdc.set(TYCOON_SPAWN_RATE_LEVEL_KEY, PersistentDataType.INTEGER, upgrades.getSpawnRateLevel());
        pdc.set(TYCOON_MINING_RATE_LEVEL_KEY, PersistentDataType.INTEGER, upgrades.getMiningRateLevel());
        pdc.set(TYCOON_SELL_MULTIPLIER_LEVEL_KEY, PersistentDataType.INTEGER, upgrades.getSellMultiplierLevel());
        pdc.set(TYCOON_MAX_INVENTORY_STORAGE_KEY, PersistentDataType.INTEGER, upgrades.getInventoryStorageLevel());
        //Save ClaimedLevels
        String claimedLevels = upgrades.getClaimedLevels().stream().map(String::valueOf).collect(Collectors.joining(","));
        pdc.set(TYCOON_CLAIMED_LEVELS_KEY, PersistentDataType.STRING, claimedLevels);
        //========== Upgrade Keys ==========
        item.setItemMeta(meta); // DAS speichert es wirklich auf das Item!
    }

    public NamespacedKey getTYCOON_BLOCK_KEY() {
        return TYCOON_BLOCK;
    }
    public NamespacedKey getLEVEL_KEY() {
        return LEVEL;
    }
    public NamespacedKey getXP_KEY() {
        return XP;
    }
    public NamespacedKey getCREATION_TIME_KEY() {
        return CREATION_TIME;
    }
    public NamespacedKey getMATERIAL_KEY() {
        return MATERIAL;
    }
    public NamespacedKey getSPAWN_INTERVAL_KEY() {
        return SPAWN_INTERVAL;
    }
    public NamespacedKey getTYPE_KEY() {
        return TYPE_KEY;
    }
    public NamespacedKey getTycoon_MENU_ITEM_KEY() {
        return TYCOON_MENU_ITEM_KEY;
    }
}
