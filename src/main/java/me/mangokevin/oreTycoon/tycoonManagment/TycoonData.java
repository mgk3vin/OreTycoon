package me.mangokevin.oreTycoon.tycoonManagment;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class TycoonData {
    private static NamespacedKey TYCOON_BLOCK;
    private static NamespacedKey LEVEL;
    private static NamespacedKey XP;
    private static NamespacedKey CREATION;
    private static NamespacedKey MATERIAL;
    private static NamespacedKey SPAWN_INTERVAL;
    private static NamespacedKey CREATION_TIME;
    public static NamespacedKey TYPE_KEY;
    public static NamespacedKey MENU_ACTION_KEY;
    public static NamespacedKey TYCOON_MENU_ITEM_KEY;
    public static NamespacedKey TYCOON_MENU_ITEM_INDEX_KEY;

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
        MENU_ACTION_KEY = new NamespacedKey(plugin, "menu_action");
        TYCOON_MENU_ITEM_KEY = new NamespacedKey(plugin, "tycoon_menu_item");
        TYCOON_MENU_ITEM_INDEX_KEY = new NamespacedKey(plugin, "tycoon_menu_item_index");
    }
    // Speichert die Daten eines Tycoons auf ein Item
    public static void writeToItem(ItemStack item, int level, int xp, long creation, Material material, int spawnInterval, long creationTime, String type) {
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
