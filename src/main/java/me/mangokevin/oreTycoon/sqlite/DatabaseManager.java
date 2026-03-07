package me.mangokevin.oreTycoon.sqlite;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.menuManager.TycoonInventory;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonType;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonUpgrades;
import me.mangokevin.oreTycoon.tycoonManagment.booster.BoosterRegistry;
import me.mangokevin.oreTycoon.tycoonManagment.booster.TycoonBoosterAbstract;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonRegistry;
import me.mangokevin.oreTycoon.utility.Console;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private final OreTycoon plugin;
    private final TycoonRegistry tycoonRegistry;
    private Connection connection;

    public DatabaseManager(OreTycoon plugin) {
        this.plugin = plugin;
        this.tycoonRegistry = plugin.getTycoonRegistry();

        File dbFile = new File(plugin.getDataFolder(), "tycoons.db");
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            connection.createStatement().executeUpdate("PRAGMA journal_mode=WAL;");
        } catch (SQLException e) {
            Console.error(getClass(), "Database connection failed! Reason: " + e.getMessage());
        }
        createTables();
    }

    public void createTables() {
        String tycoonTableCMD = "CREATE TABLE IF NOT EXISTS tycoons " +
                "(uid TEXT PRIMARY KEY," +
                " owner_uuid STRING," +
                " level INT," +
                " xp INT," +
                " world STRING," +
                " x DOUBLE," +
                " y DOUBLE," +
                " z DOUBLE," +
                " isActive BOOLEAN," +
                " is_auto_miner_active BOOLEAN," +
                " tycoon_type STRING," +
                " creation_date LONG," +
                " tycoon_index INT," +
                " hologram_uid TEXT)";
        String tycoonUpgradesCMD = "CREATE TABLE IF NOT EXISTS tycoon_upgrades" +
                "(tycoon_uid TEXT PRIMARY KEY," +
                "spawn_rate_level INT," +
                "mining_rate_level INT," +
                "sell_multiplier_level INT," +
                "inventory_storage_level INT," +
                "double_drops_level INT," +
                "fortune_level INT," +
                "is_autominer_unlocked BOOLEAN)";
        String tycoonInventoriesCMD = "CREATE TABLE IF NOT EXISTS tycoon_inventories" +
                "(tycoon_uid TEXT," +
                "material TEXT," +
                "amount INT," +
                "slot INT)";
        String tycoonClaimedLevels = "CREATE TABLE IF NOT EXISTS tycoon_claimed_levels" +
                "(tycoon_uid TEXT," +
                "claimed_level INT)";
        String tycoonActiveMaterials = "CREATE TABLE IF NOT EXISTS tycoon_active_materials" +
                "(tycoon_uid TEXT," +
                "material TEXT," +
                "is_active BOOLEAN)";
        String tycoonActiveBoosters = "CREATE TABLE IF NOT EXISTS tycoon_active_boosters" +
                "(tycoon_uid TEXT PRIMARY KEY," +
                "is_booster_active BOOLEAN," +
                "booster_type TEXT," +
                "boost_value DOUBLE," +
                "boost_duration LONG)";
        String tycoonSpawnedBlockLocations = "CREATE TABLE IF NOT EXISTS tycoon_spawned_block_locations" +
                "(tycoon_uid TEXT," +
                "world TEXT," +
                "x DOUBLE," +
                "y DOUBLE," +
                "z DOUBLE)";
        try {
            connection.createStatement().execute(tycoonTableCMD);
            Console.log(getClass(), "Table tycoons created!");
            connection.createStatement().execute(tycoonUpgradesCMD);
            Console.log(getClass(), "Table tycoon_upgrades created!");
            connection.createStatement().execute(tycoonClaimedLevels);
            Console.log(getClass(), "Table tycoon_claimed_levels created!");
            connection.createStatement().execute(tycoonInventoriesCMD);
            Console.log(getClass(), "Table tycoon_inventories created!");
            connection.createStatement().execute(tycoonActiveMaterials);
            Console.log(getClass(), "Table tycoon_active_materials created!");
            connection.createStatement().execute(tycoonActiveBoosters);
            Console.log(getClass(), "Table tycoon_active_boosters created!");
            connection.createStatement().execute(tycoonSpawnedBlockLocations);
            Console.log(getClass(), "Table tycoon_spawned_block_locations created!");
        } catch (SQLException e) {
            Console.error(getClass(), "Database command execution failed! Reason: " + e.getMessage());
        }
    }
    public void startAutoSaveTimer(){
        new BukkitRunnable() {
            @Override
            public void run() {
                for (TycoonBlock tycoonBlock : tycoonRegistry.getAllTycoons()){
                    saveTycoonAsync(tycoonBlock);
                }
            }
        }.runTaskTimer(plugin, 20L * 60 * 5, 20L * 60 * 5);//Auto save every 5 Minutes
    }

    public void deleteTycoon(String tycoonUID){
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM tycoons WHERE uid = ?"
            );
            statement.setString(1, tycoonUID);
            statement.executeUpdate();

            String[] tables = {
                    "tycoon_upgrades",
                    "tycoon_claimed_levels",
                    "tycoon_inventories",
                    "tycoon_active_materials",
                    "tycoon_active_boosters",
                    "tycoon_spawned_block_locations"
            };
            for (String table : tables) {
                PreparedStatement tableDeletion = connection.prepareStatement("DELETE FROM " + table + " WHERE tycoon_uid = ?");
                tableDeletion.setString(1, tycoonUID);
                tableDeletion.executeUpdate();
            }
            Console.log(getClass(), "Deleted tycoon " + tycoonUID + " from database!");
        } catch (SQLException e) {
            Console.error(getClass(), "Database Tycoon deletion failed! Reason: " + e.getMessage());
        }
    }
    public void deleteTycoonAsync(String tycoonUID){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            deleteTycoon(tycoonUID);
        });
    }

    public void saveTycoon(TycoonBlock tycoonBlock) {
        try{
            PreparedStatement statement = connection.prepareStatement(
                    //Insert or Replace overwrites entry for uid or creates new one
                    "INSERT OR REPLACE INTO tycoons " +
                            "(uid," +
                            " owner_uuid," +
                            " level," +
                            " xp," +
                            " world," +
                            " x, y, z," +
                            " isActive," +
                            " is_auto_miner_active," +
                            " tycoon_type," +
                            " creation_date," +
                            " tycoon_index," +
                            " hologram_uid)" +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            Location tycoonLocation = tycoonBlock.getLocation();
            World world = tycoonBlock.getLocation().getWorld();
            if (world == null) {
                Console.error(getClass(), "Tycoon world is null! Save failed!");
                return;
            };
            statement.setString(1, tycoonBlock.getBlockUID());              //1 = first ?
            statement.setString(2, tycoonBlock.getOwnerUuid().toString());  //2 = second ?
            statement.setInt(3, tycoonBlock.getLevel());
            statement.setInt(4, tycoonBlock.getLevelXp());
            statement.setString(5, world.getName());
            statement.setDouble(6, tycoonLocation.getX());
            statement.setDouble(7, tycoonLocation.getY());
            statement.setDouble(8, tycoonLocation.getZ());
            statement.setBoolean(9, tycoonBlock.isActive());
            statement.setBoolean(10, tycoonBlock.isAutoMinerEnabled());
            statement.setString(11, tycoonBlock.getTycoonType().name());
            statement.setLong(12, tycoonBlock.getCreationTime());
            statement.setInt(13, tycoonBlock.getIndex());
            statement.setString(14, tycoonBlock.getHologramUID());

            statement.executeUpdate();  //run

            saveTycoonUpgrades(tycoonBlock);
            saveTycoonClaimedLevels(tycoonBlock);
            saveTycoonInventory(tycoonBlock);
            saveTycoonActiveMaterials(tycoonBlock);
            saveTycoonActiveBoosters(tycoonBlock);
            saveTycoonSpawnedBlockLocations(tycoonBlock);
            Console.debug(getClass(), "Tycoon " + tycoonBlock.getBlockUID() + "saved!");
        } catch (SQLException e) {
            Console.error(getClass(), "Database save failed! Reason: " + e.getMessage());
        }
    }
    public void saveTycoonAsync(TycoonBlock tycoonBlock) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            saveTycoon(tycoonBlock);
        });
    }
    private void saveTycoonUpgrades(TycoonBlock tycoonBlock) {
        TycoonUpgrades upgrades = tycoonBlock.getTycoonUpgrades();
        try{
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT OR REPLACE INTO tycoon_upgrades (tycoon_uid, spawn_rate_level, mining_rate_level, sell_multiplier_level, inventory_storage_level, double_drops_level, fortune_level, is_autominer_unlocked) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, tycoonBlock.getBlockUID());
            statement.setInt(2, upgrades.getSpawnRateLevel());
            statement.setInt(3, upgrades.getMiningRateLevel());
            statement.setInt(4, upgrades.getSellMultiplierLevel());
            statement.setInt(5, upgrades.getInventoryStorageLevel());
            statement.setInt(6, upgrades.getDoubleDropsLevel());
            statement.setInt(7, upgrades.getFortuneLevel());
            statement.setBoolean(8, upgrades.isAutoMinerUnlocked());

            statement.executeUpdate();
        } catch (SQLException e) {
            Console.error(getClass(), "Database save failed while saving tycoon upgrades! Reason: " + e.getMessage());
        }
    }
    private void saveTycoonClaimedLevels(TycoonBlock tycoonBlock) {
        try{
            PreparedStatement delStatement = connection.prepareStatement("DELETE FROM tycoon_claimed_levels WHERE tycoon_uid = ?");
            delStatement.setString(1, tycoonBlock.getBlockUID());
            delStatement.executeUpdate();

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT OR REPLACE INTO tycoon_claimed_levels" +
                            "(tycoon_uid," +
                            "claimed_level)" +
                            "VALUES (?, ?)"
            );
            statement.setString(1, tycoonBlock.getBlockUID());
            TycoonUpgrades upgrades = tycoonBlock.getTycoonUpgrades();
            for (int claimedLevel : upgrades.getClaimedLevels()) {
                statement.setInt(2, claimedLevel);
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            Console.error(getClass(), "Database claimed levels save failed! Reason: " + e.getMessage());
        }
    }
    private void saveTycoonInventory(TycoonBlock tycoonBlock) {
        Inventory inventory = tycoonBlock.getInventory();
        try {
            //Delete previous entries
            PreparedStatement delStatement = connection.prepareStatement("DELETE FROM tycoon_inventories WHERE tycoon_uid = ?");
            delStatement.setString(1, tycoonBlock.getBlockUID());
            delStatement.executeUpdate();

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT OR REPLACE INTO tycoon_inventories " +
                            "(tycoon_uid," +
                            "material," +
                            "amount," +
                            "slot)" +
                            "VALUES (?, ?, ?, ?)"
            );

            statement.setString(1, tycoonBlock.getBlockUID());
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack itemStack = inventory.getItem(i);
                if (itemStack == null || itemStack.getType() == Material.AIR) {continue;}

                //Check for inventory item pdc tag
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta != null) {
                    PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
                    if (pdc.has(TycoonData.INVENTORY_ITEM_KEY, PersistentDataType.STRING)) {
                        statement.setString(2, itemStack.getType().name());
                        statement.setInt(3, itemStack.getAmount());
                        statement.setInt(4, i);
                        statement.executeUpdate();
                    }
                }

            }

        } catch (SQLException e) {
            Console.error(getClass(), "Database inventory save failed! Reason: " + e.getMessage());
        }
    }
    private void saveTycoonActiveMaterials(TycoonBlock tycoonBlock) {
        Map<Material, Boolean> activeMaterials = tycoonBlock.getActiveRessourceMaterialsMap();
        try {
            //Delete previous entries
            PreparedStatement delStatement = connection.prepareStatement("DELETE FROM tycoon_active_materials WHERE tycoon_uid = ?");
            delStatement.setString(1, tycoonBlock.getBlockUID());
            delStatement.executeUpdate();

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT OR REPLACE INTO tycoon_active_materials " +
                            "(tycoon_uid," +
                            "material," +
                            "is_active)" +
                            "VALUES (?, ?, ?)"
            );
            statement.setString(1, tycoonBlock.getBlockUID());
            for (Map.Entry<Material, Boolean> entry : activeMaterials.entrySet()) {
                statement.setString(2, entry.getKey().name());
                statement.setBoolean(3, entry.getValue());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            Console.error(getClass(), "Database active materials save failed! Reason: " + e.getMessage());
        }
    }
    private void saveTycoonActiveBoosters(TycoonBlock tycoonBlock) {
        TycoonBoosterAbstract tycoonBooster = tycoonBlock.isAnyBoosterActive();
        if (tycoonBooster == null) return;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT OR REPLACE INTO tycoon_active_boosters " +
                            "(tycoon_uid," +
                            "is_booster_active," +
                            "booster_type," +
                            "boost_value," +
                            "boost_duration)" +
                            "VALUES (?, ?, ?, ?, ?)"
            );
            statement.setString(1, tycoonBlock.getBlockUID());
            statement.setBoolean(2, true);
            statement.setString(3, tycoonBooster.getUID());
            statement.setDouble(4, tycoonBooster.getBoostValue());
            statement.setLong(5, tycoonBooster.getDuration());

            statement.executeUpdate();
        } catch (SQLException e) {
            Console.error(getClass(), "Database active boosters save failed! Reason: " + e.getMessage());
        }
    }
    private void saveTycoonSpawnedBlockLocations(TycoonBlock tycoonBlock) {
        try{
            PreparedStatement delStatement = connection.prepareStatement(
                    "DELETE FROM tycoon_spawned_block_locations WHERE tycoon_uid = ?"
            );
            delStatement.setString(1, tycoonBlock.getBlockUID());
            delStatement.executeUpdate();
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT OR REPLACE INTO tycoon_spawned_block_locations " +
                            "(tycoon_uid," +
                            "world," +
                            "x, y, z)" +
                            "VALUES (?, ?, ?, ?, ?)"
            );
            statement.setString(1, tycoonBlock.getBlockUID());
            for (Block block : tycoonBlock.getActiveBlocks()){
                Location location = block.getLocation();
                statement.setString(2, block.getWorld().getName());
                statement.setDouble(3, location.getX());
                statement.setDouble(4, location.getY());
                statement.setDouble(5, location.getZ());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            Console.error(getClass(), "Database spawned blocks save failed! Reason: " + e.getMessage());
        }
    }

    public void loadTycoons() {
        //List<TycoonBlock> tycoonBlocks = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM tycoons");
            ResultSet result = statement.executeQuery();    //Reads the db data

            while (result.next()) { //jumps to next result until false
                //Building 1 tycoon block
                String uid = result.getString("uid");
                String ownerUuidString = result.getString("owner_uuid");
                UUID ownerUuid = UUID.fromString(ownerUuidString);

                int level = result.getInt("level");
                int xp = result.getInt("xp");

                boolean isAutoMinerActive = result.getBoolean("is_auto_miner_active");
                long creationTime = result.getLong("creation_date");
                int tycoonIndex = result.getInt("tycoon_index");

                String hologramUID = result.getString("hologram_uid");
                //========== Load Upgrades ==========
                TycoonUpgrades upgrades = loadTycoonUpgrades(uid);
                loadTycoonClaimedLevels(upgrades, uid);
                //========== Load Upgrades ==========

                //========== Load Location ==========
                String world = result.getString("world");
                double x = result.getDouble("x");
                double y = result.getDouble("y");
                double z = result.getDouble("z");

                World worldObj = Bukkit.getWorld(world);
                if (worldObj == null) {
                    Console.error(getClass(), "World " + world + " not found! Reason: " + world);
                    continue;
                }
                Location tycoonLocation = new Location(worldObj, x, y, z);
                //========== Load Location ==========

                String tycoonTypeString = result.getString("tycoon_type");
                TycoonType tycoonType = TycoonType.valueOf(tycoonTypeString);

                boolean isActive = result.getBoolean("isActive");
                TycoonBlock tycoonBlock = new TycoonBlock(tycoonType, tycoonLocation, ownerUuid, isActive, plugin, upgrades);

                tycoonBlock.setLevel(level);
                tycoonBlock.setLevelXp(xp);
                tycoonBlock.setAutoMinerEnabled(isAutoMinerActive);
                tycoonBlock.setCreationTime(creationTime);
                tycoonBlock.setIndex(tycoonIndex);

                loadTycoonInventory(tycoonBlock);
                loadTycoonActiveMaterials(tycoonBlock);
                loadTycoonActiveBoosters(tycoonBlock);
                loadTycoonSpawnedLocations(tycoonBlock);

                tycoonBlock.setLoaded(true);

                Console.debug(getClass(), "Tycoon " + uid + " index: " + tycoonBlock.getIndex() + " Before registering");
                tycoonRegistry.addTycoon(tycoonBlock);
                Console.debug(getClass(), "Tycoon " + uid + " index: " + tycoonBlock.getIndex() + " After registering");
                //tycoonBlocks.add(tycoonBlock);
            }
            for (TycoonBlock block : tycoonRegistry.getAllTycoons()) {
                Console.debug(getClass(), "Tycoon " + block.getBlockUID() + " index: " + block.getIndex() + " Before creating hologram and updating attributes");
                block.createHologram();
                block.updateAttributes();
                Console.debug(getClass(), "Tycoon " + block.getBlockUID() + " index: " + block.getIndex() + " After creating hologram and updating attributes");

            }
        } catch (SQLException e) {
            Console.error(getClass(), "Database load failed! Reason: " + e.getMessage());
        }
        //return tycoonBlocks;
    }
    private TycoonUpgrades loadTycoonUpgrades(String tycoonUID) {
        TycoonUpgrades upgrades = new TycoonUpgrades();

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM tycoon_upgrades WHERE tycoon_uid = ?"
            );
            statement.setString(1, tycoonUID);
            ResultSet result = statement.executeQuery();
            if (result.next()){
                upgrades.setSpawnRateLevel(result.getInt("spawn_rate_level"));
                upgrades.setMiningRateLevel(result.getInt("mining_rate_level"));
                upgrades.setSellMultiplierLevel(result.getInt("sell_multiplier_level"));
                upgrades.setInventoryStorageLevel(result.getInt("inventory_storage_level"));
                upgrades.setDoubleDropsLevel(result.getInt("double_drops_level"));
                upgrades.setFortuneLevel(result.getInt("fortune_level"));
                upgrades.setAutoMinerUnlocked(result.getBoolean("is_autominer_unlocked"));
            }
        } catch (SQLException e) {
            Console.error(getClass(), "Database load for tycoon upgrades failed! Reason: " + e.getMessage());
        }

        return upgrades;
    }
    private void loadTycoonClaimedLevels(TycoonUpgrades upgrades, String blockUID) {
        List<Integer> claimedLevels = new ArrayList<>();
        try{
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM tycoon_claimed_levels WHERE tycoon_uid = ?"
            );
            statement.setString(1, blockUID);
            ResultSet result = statement.executeQuery();
            while (result.next()){
                int claimedLevel = result.getInt("claimed_level");
                claimedLevels.add(claimedLevel);
            }
            upgrades.setClaimedLevels(claimedLevels);
        } catch (SQLException e) {
            Console.error(getClass(), "Database load for tycoon upgrades claimed levels failed! Reason: " + e.getMessage());
        }

    }
    private void loadTycoonInventory(TycoonBlock tycoonBlock) {
        TycoonInventory inventory = tycoonBlock.getTycoonInventory();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM tycoon_inventories WHERE tycoon_uid = ?");
            statement.setString(1, tycoonBlock.getBlockUID());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                String materialName = result.getString("material");
                Material material = Material.valueOf(materialName);
                int amount = result.getInt("amount");

                ItemStack itemStack = new  ItemStack(material, amount);

                inventory.addItem(itemStack);
            }
        } catch (SQLException e) {
            Console.error(getClass(), "Database inventory load failed! Reason: " + e.getMessage());
        }
    }
    private void loadTycoonActiveMaterials(TycoonBlock tycoonBlock) {
        Map<Material, Boolean> activeMaterials = new HashMap<>();

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM tycoon_active_materials WHERE tycoon_uid = ?"
            );
            statement.setString(1, tycoonBlock.getBlockUID());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                String materialName = result.getString("material");
                Material material = Material.valueOf(materialName);
                boolean isActive = result.getBoolean("is_active");

                activeMaterials.put(material, isActive);
            }
            //Fallback if map returnes empty
            if (activeMaterials.isEmpty()) {
                //Default ressource map
                for (Map.Entry<Material, Integer> entry : tycoonBlock.getTycoonType().getResources().entrySet()) {
                    activeMaterials.put(entry.getKey(), true);
                }
            }

            tycoonBlock.setActiveResourceMaterialsMap(activeMaterials);
        } catch (SQLException e) {
            Console.error(getClass(), "Database active materials load failed! Reason: " + e.getMessage());
        }
    }
    private void loadTycoonActiveBoosters(TycoonBlock tycoonBlock) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM tycoon_active_boosters WHERE tycoon_uid = ?"
            );
            statement.setString(1, tycoonBlock.getBlockUID());
            ResultSet result = statement.executeQuery();
            if (result.next()) {

                boolean isBoosterActive = result.getBoolean("is_booster_active");
                String boosterUID = result.getString("booster_type");
                double boostValue = result.getDouble("boost_value");
                long boostDuration = result.getLong("boost_duration");
                if (isBoosterActive) {
                    TycoonBoosterAbstract tycoonBooster = BoosterRegistry.createBooster(boosterUID, boostValue, boostDuration);
                    if (tycoonBooster != null){
                        tycoonBooster.onApply(tycoonBlock);
                    }
                }
            }
        } catch (SQLException e) {
            Console.error(getClass(), "Database active boosters load failed! Reason: " + e.getMessage());
        }
    }
    private void loadTycoonSpawnedLocations(TycoonBlock tycoonBlock) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM tycoon_spawned_block_locations WHERE tycoon_uid = ?"
            );
            statement.setString(1, tycoonBlock.getBlockUID());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                World world = Bukkit.getWorld(result.getString("world"));
                if (world == null) {
                    Console.error(getClass(), "Spawned block locations load failed! Reason: " + result.getString("world") + " is null!");
                    continue;
                }
                double x = result.getDouble("x");
                double y = result.getDouble("y");
                double z = result.getDouble("z");

                Location location = new Location(world, x, y, z);
                Block block = world.getBlockAt(location);
                tycoonBlock.getActiveBlocks().add(block);
            }
        } catch (SQLException e) {
            Console.error(getClass(), "Database spawned locations load failed! Reason: " + e.getMessage());
        }
    }
}
