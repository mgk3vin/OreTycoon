package me.mangokevin.oreTycoon.tycoonManagment;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.utility.StorageUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class TycoonData {
    public static NamespacedKey TYCOON_BLOCK_KEY;
    private static NamespacedKey TYCOON_BLOCK;
    private static NamespacedKey LEVEL;
    public static NamespacedKey XP;
    private static NamespacedKey MATERIAL;
    private static NamespacedKey SPAWN_INTERVAL;
    private static NamespacedKey CREATION_TIME;
    public static NamespacedKey TYPE_KEY;
    public static NamespacedKey LOCATION_KEY;
    public static NamespacedKey INVENTORY_KEY;
    public static NamespacedKey MENU_ACTION_KEY;
    public static NamespacedKey TYCOON_MENU_ITEM_KEY;
    public static NamespacedKey MENU_ITEM_KEY;
    public static NamespacedKey TYCOON_MENU_ITEM_INDEX_KEY;
    public static NamespacedKey TYCOON_MENU_ITEM_UID_KEY;
    public static NamespacedKey BLOCK_IS_AUTOMINED_KEY;

    public static NamespacedKey MENU_ACTION_TYCOON_LEVEL_KEY;
    //========== Upgrade Keys ==========
    public static NamespacedKey TYCOON_IS_AUTO_MINER_UNLOCKED_KEY;
    public static NamespacedKey TYCOON_SPAWN_RATE_LEVEL_KEY;
    public static NamespacedKey TYCOON_MINING_RATE_LEVEL_KEY;
    public static NamespacedKey TYCOON_SELL_MULTIPLIER_LEVEL_KEY;
    public static NamespacedKey TYCOON_MAX_INVENTORY_STORAGE_KEY;
    public static NamespacedKey TYCOON_CLAIMED_LEVELS_KEY;
    public static NamespacedKey TYCOON_DOUBLE_DROPS_LEVEL_KEY;
    public static NamespacedKey TYCOON_FORTUNE_LEVEL_KEY;
    //========== Upgrade Keys ==========

    //========== Booster Keys ==========
    public static NamespacedKey BOOSTER_ID_KEY;
    public static NamespacedKey BOOSTER_VALUE_KEY;
    public static NamespacedKey BOOSTER_DURATION_KEY;
    //========== Booster Keys ==========

    //========== Worlds Keys ==========
    public static NamespacedKey WORLD_UID_KEY;
    //========== Worlds Keys ==========

    public static NamespacedKey INVENTORY_ITEM_KEY;


    // Wird einmal in der onEnable deiner Main aufgerufen: TycoonData.init(this);
    public static void init(Plugin plugin) {
        TYCOON_BLOCK_KEY = new NamespacedKey(plugin, "Tycoon_block");
        TYCOON_BLOCK = new NamespacedKey(plugin, "IS_TYCOON_BLOCK");
        LEVEL = new NamespacedKey(plugin, "level");
        XP = new NamespacedKey(plugin, "xp");
        MATERIAL = new NamespacedKey(plugin, "material");
        SPAWN_INTERVAL = new NamespacedKey(plugin, "spawn_interval");
        CREATION_TIME = new NamespacedKey(plugin, "creation_time");
        TYPE_KEY = new NamespacedKey(plugin, "type");
        LOCATION_KEY = new NamespacedKey(plugin, "location");
        INVENTORY_KEY = new NamespacedKey(plugin, "inventory");
        MENU_ACTION_KEY = new NamespacedKey(plugin, "menu_action");
        TYCOON_MENU_ITEM_KEY = new NamespacedKey(plugin, "tycoon_menu_item");
        TYCOON_MENU_ITEM_INDEX_KEY = new NamespacedKey(plugin, "tycoon_menu_item_index");
        TYCOON_MENU_ITEM_UID_KEY = new  NamespacedKey(plugin, "tycoon_menu_item_uid");
        BLOCK_IS_AUTOMINED_KEY = new NamespacedKey(plugin, "block_is_automined");
        MENU_ITEM_KEY = new NamespacedKey(plugin, "menu_item");
        MENU_ACTION_TYCOON_LEVEL_KEY = new NamespacedKey(plugin, "menu_action_tycoon_level");


        //========== Upgrade Keys ==========
        TYCOON_IS_AUTO_MINER_UNLOCKED_KEY = new NamespacedKey(plugin, "tycoon_is_auto_miner_unlocked");
        TYCOON_SPAWN_RATE_LEVEL_KEY = new NamespacedKey(plugin, "tycoon_spawn_rate_level");
        TYCOON_MINING_RATE_LEVEL_KEY = new NamespacedKey(plugin, "tycoon_mining_rate_level");
        TYCOON_SELL_MULTIPLIER_LEVEL_KEY = new NamespacedKey(plugin, "tycoon_sell_multiplier_level");
        TYCOON_MAX_INVENTORY_STORAGE_KEY = new NamespacedKey(plugin, "tycoon_max_inventory_storage");
        TYCOON_CLAIMED_LEVELS_KEY = new NamespacedKey(plugin, "tycoon_claimed_levels");
        TYCOON_DOUBLE_DROPS_LEVEL_KEY = new NamespacedKey(plugin, "tycoon_double_drops_level");
        TYCOON_FORTUNE_LEVEL_KEY = new NamespacedKey(plugin, "tycoon_fortune_level");
        //========== Upgrade Keys ==========


        //========== Booster Keys ==========
        BOOSTER_ID_KEY = new  NamespacedKey(plugin, "booster_id");
        BOOSTER_VALUE_KEY = new   NamespacedKey(plugin, "booster_value");
        BOOSTER_DURATION_KEY = new   NamespacedKey(plugin, "booster_duration");
        //========== Booster Keys ==========

        //========== Worlds Keys ==========
        WORLD_UID_KEY = new  NamespacedKey(plugin, "world_uid");
        //========== Worlds Keys ==========

        INVENTORY_ITEM_KEY = new  NamespacedKey(plugin, "inventory_item");
    }
    // Speichert die Daten eines Tycoons auf ein Item
    public static void writeToItem(ItemStack item, int level, int xp, Location loc, Material material, int spawnInterval, long creationTime, String type, Inventory inventory, TycoonUpgrades upgrades) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(TYCOON_BLOCK, PersistentDataType.BYTE,  (byte) 1);
        pdc.set(TYCOON_BLOCK_KEY, PersistentDataType.BYTE, (byte) 1);
        pdc.set(TYPE_KEY, PersistentDataType.STRING, type);
        pdc.set(LEVEL, PersistentDataType.INTEGER, level);
        pdc.set(XP, PersistentDataType.INTEGER, xp);

        // Wir bauen einen String: "world;x;y;z;yaw;pitch"
        String locString = String.format(Locale.US, "%s;%.2f;%.2f;%.2f;%.2f;%.2f",
                loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        pdc.set(LOCATION_KEY, PersistentDataType.STRING, locString);

        pdc.set(MATERIAL, PersistentDataType.STRING, material.toString());
        pdc.set(SPAWN_INTERVAL, PersistentDataType.INTEGER, spawnInterval);
        pdc.set(CREATION_TIME, PersistentDataType.LONG, creationTime);

        byte[] byteArray = StorageUtils.toByteArray(inventory);

        pdc.set(INVENTORY_KEY, PersistentDataType.BYTE_ARRAY, byteArray);

        //========== Upgrade Keys ==========
        pdc.set(TYCOON_IS_AUTO_MINER_UNLOCKED_KEY, PersistentDataType.BOOLEAN, upgrades.isAutoMinerUnlocked());
        pdc.set(TYCOON_SPAWN_RATE_LEVEL_KEY, PersistentDataType.INTEGER, upgrades.getSpawnRateLevel());
        pdc.set(TYCOON_MINING_RATE_LEVEL_KEY, PersistentDataType.INTEGER, upgrades.getMiningRateLevel());
        pdc.set(TYCOON_SELL_MULTIPLIER_LEVEL_KEY, PersistentDataType.INTEGER, upgrades.getSellMultiplierLevel());
        pdc.set(TYCOON_DOUBLE_DROPS_LEVEL_KEY, PersistentDataType.INTEGER, upgrades.getDoubleDropsLevel());
        pdc.set(TYCOON_FORTUNE_LEVEL_KEY, PersistentDataType.INTEGER, upgrades.getFortuneLevel());
        pdc.set(TYCOON_MAX_INVENTORY_STORAGE_KEY, PersistentDataType.INTEGER, upgrades.getInventoryStorageLevel());
        //Save ClaimedLevels
        String claimedLevels = upgrades.getClaimedLevels().stream().map(String::valueOf).collect(Collectors.joining(","));
        pdc.set(TYCOON_CLAIMED_LEVELS_KEY, PersistentDataType.STRING, claimedLevels);
        //========== Upgrade Keys ==========
        item.setItemMeta(meta); // DAS speichert es wirklich auf das Item!
    }
    public static TycoonBlock readFromItem(PersistentDataContainer pdc, Player player, Block placedBlock, OreTycoon plugin) {

        int level = pdc.getOrDefault(TycoonData.LEVEL ,PersistentDataType.INTEGER, 1);
        int xp = pdc.getOrDefault(TycoonData.XP, PersistentDataType.INTEGER, 0);
        long creationTime = System.currentTimeMillis();

        String tycoonName = pdc.getOrDefault(TycoonData.TYPE_KEY, PersistentDataType.STRING, "ERROR");
        TycoonType tycoonType = TycoonType.valueOf(tycoonName);

        Location location = placedBlock.getLocation();
        UUID uuid = player.getUniqueId();

        //========== Load Upgrades ==========
        TycoonUpgrades upgrades = new TycoonUpgrades();
        upgrades.setAutoMinerUnlocked(pdc.getOrDefault(TYCOON_IS_AUTO_MINER_UNLOCKED_KEY, PersistentDataType.BOOLEAN, false));
        upgrades.setSpawnRateLevel(pdc.getOrDefault(TYCOON_SPAWN_RATE_LEVEL_KEY, PersistentDataType.INTEGER, 0));
        upgrades.setMiningRateLevel(pdc.getOrDefault(TYCOON_MINING_RATE_LEVEL_KEY, PersistentDataType.INTEGER, 0));
        upgrades.setSellMultiplierLevel(pdc.getOrDefault(TYCOON_SELL_MULTIPLIER_LEVEL_KEY, PersistentDataType.INTEGER, 0));
        upgrades.setDoubleDropsLevel(pdc.getOrDefault(TYCOON_DOUBLE_DROPS_LEVEL_KEY, PersistentDataType.INTEGER, 0));
        upgrades.setInventoryStorageLevel(pdc.getOrDefault(TYCOON_MAX_INVENTORY_STORAGE_KEY, PersistentDataType.INTEGER, 0));
        upgrades.setFortuneLevel(pdc.getOrDefault(TYCOON_FORTUNE_LEVEL_KEY, PersistentDataType.INTEGER, 0));

        //Load claimed Levels from String
        String claimedLevelsData = pdc.get(TycoonData.TYCOON_CLAIMED_LEVELS_KEY, PersistentDataType.STRING);
        if (claimedLevelsData != null && !claimedLevelsData.trim().isEmpty()) {
            String[] claimedLevels = pdc.get(TycoonData.TYCOON_CLAIMED_LEVELS_KEY, PersistentDataType.STRING).split(",");
            List<Integer> claimedLevelsList = new ArrayList<>();
            for (String claimedLevel : claimedLevels) {
                claimedLevelsList.add(Integer.parseInt(claimedLevel));
            }
            upgrades.setClaimedLevels(claimedLevelsList);
        }

        //========== Load Upgrades ==========


        TycoonBlock tycoonBlock = new TycoonBlock(tycoonType, location, uuid, false, plugin, upgrades);
        tycoonBlock.setLevel(level);
        tycoonBlock.setLevelXp(xp);
        tycoonBlock.setCreationTime(creationTime);


        if (pdc.has(TycoonData.INVENTORY_KEY, PersistentDataType.BYTE_ARRAY)){
            byte[] byteArray =  pdc.get(TycoonData.INVENTORY_KEY, PersistentDataType.BYTE_ARRAY);

            StorageUtils.fromByteArray(byteArray, tycoonBlock.getInventory());
        }

        tycoonBlock.setLoaded(true);
        return tycoonBlock;
    }
}
