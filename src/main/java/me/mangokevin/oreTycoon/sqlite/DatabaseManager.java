package me.mangokevin.oreTycoon.sqlite;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.*;
import me.mangokevin.oreTycoon.tycoonManagment.booster.BoosterRegistry;
import me.mangokevin.oreTycoon.tycoonManagment.booster.TycoonBoosterAbstract;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.SpawnBlock;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.SpawnMaterial;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.SpawnMaterialRarity;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.StoredItemKey;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonRegistry;
import me.mangokevin.oreTycoon.tycoonManagment.upgrades.TycoonUpgrades;
import me.mangokevin.oreTycoon.utility.Console;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.Semaphore;

public class DatabaseManager {
    private final OreTycoon plugin;
    private final TycoonRegistry tycoonRegistry;
    private Connection connection;

    private final Semaphore semaphore = new Semaphore(1);

    public DatabaseManager(OreTycoon plugin) {
        this.plugin = plugin;
        this.tycoonRegistry = plugin.getTycoonRegistry();

        File dbFile = new File(plugin.getDataFolder(), "tycoons.db");
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("PRAGMA journal_mode=WAL;");
            }
        } catch (SQLException e) {
            Console.error(getClass(), "Database connection failed! Reason: " + e.getMessage());
        }
        createTables();
    }

    private void migrateTables(String table, String column, String type){
        try {
            connection.createStatement().executeUpdate(
                    "ALTER TABLE " + table + " ADD COLUMN " + column + " " + type
            );
            Console.log(getClass(), "Migration: Added column " + column + " to " + table);
        } catch (SQLException e) {
            Console.error(getClass(), "Database migration failed! (Column already exists) Reason: " + e.getMessage());
        }
    }

    private void createFreshTables(){
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
                "multi_miner_level INT," +
                "is_autominer_unlocked BOOLEAN)";
        String tycoonInventoriesCMD = "CREATE TABLE IF NOT EXISTS tycoon_inventories" +
                "(tycoon_uid TEXT," +
                "material TEXT," +
                "rarity TEXT," +
                "amount INT," +
                "slot INT)";
        String tycoonClaimedLevels = "CREATE TABLE IF NOT EXISTS tycoon_claimed_levels" +
                "(tycoon_uid TEXT," +
                "claimed_level INT)";
        String tycoonActiveMaterials = "CREATE TABLE IF NOT EXISTS tycoon_active_materials" +
                "(tycoon_uid TEXT," +
                "material TEXT," +
                "rarity TEXT," +
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
                "z DOUBLE," +
                "rarity TEXT)";
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
    private void runMigrations() {
        Console.log(getClass(), "Migration started!");
    }
    public void createTables() {
        createFreshTables();
    }
    public void startAutoSaveTimer(){
        new BukkitRunnable() {
            @Override
            public void run() {
                for (TycoonBlock tycoonBlock : tycoonRegistry.getAllTycoons()){
                    saveTycoonAsync(tycoonBlock);
                }
            }
        }.runTaskTimer(plugin, 20L * 60, 20L * 60 * 5);//Auto save every 5 Minutes
    }
    public void startBackupTimer(){
        new BukkitRunnable() {
            @Override
            public void run() {
                createBackup();
            }
        }.runTaskTimer(plugin, 20L * 60 * 5, 20L * 60 * 60 * 12);//5min delay 12h cycle
    }

    public void createBackup() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                semaphore.acquire();

                File backupFolder = new File(plugin.getDataFolder(), "tycoon_backups");
                if (!backupFolder.exists()) backupFolder.mkdirs();

                String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date());
                File backupFile = new File(backupFolder, "backup_" + timeStamp + ".db");

                // WICHTIG: Falls die Datei existiert, schlägt VACUUM INTO fehl.
                if (backupFile.exists()) {
                    backupFile.delete();
                }

                // Nutze VACUUM INTO für ein sauberes, konsistentes Backup einer laufenden DB
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate("VACUUM INTO '" + backupFile.getAbsolutePath() + "'");
                    plugin.getLogger().info("Backup created: " + backupFile.getName());

                    // Optional: Hier alte Backups löschen, die älter als 7 Tage sind
                    deleteOldBackups(backupFile);
                } catch (SQLException e) {
                    plugin.getLogger().severe("Backup failed: " + e.getMessage());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                plugin.getLogger().severe("Backup failed: " + e.getMessage());
            }finally {
                semaphore.release();
            }

        });
    }

    private void deleteOldBackups(File backupFile) {
        File[] backups = backupFile.listFiles((dir, name) -> name.startsWith("backup_") && name.endsWith(".db"));
        if (backups != null && backups.length > 5) {
            Arrays.sort(backups, (a, b) -> Long.compare(a.lastModified(), b.lastModified()));

            long cutoff = System.currentTimeMillis() - (5L * 24 * 60 * 60 * 1000L);
            for (int i = 5; i < backups.length; i++) {
                File backup = backups[i];
                if (backup.lastModified() < cutoff) {
                    if (backup.delete()) {
                        Console.log(getClass(), "Backup deleted: " + backup.getName());
                    } else  {
                        Console.error(getClass(), "Could not delete old backup: "  + backup.getName());
                    }
                }
            }
        }
    }

    public void deleteTycoon(String tycoonUID){
        try {
            semaphore.acquire();
            String sql = "DELETE FROM tycoons WHERE uid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Console.error(getClass(), "Database Tycoon deletion failed! " + e.getMessage());
        }finally {
            semaphore.release();
        }

    }
    public void deleteTycoonAsync(String tycoonUID){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            deleteTycoon(tycoonUID);
        });
    }

    public void saveTycoon(TycoonBlock tycoonBlock, List<SpawnBlock> activeBlocksSnapshot) {
        try {
            semaphore.acquire();

            String sql = "INSERT OR REPLACE INTO tycoons " +
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
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
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
                statement.setBoolean(9, tycoonBlock.isActiveByPlayer());
                statement.setBoolean(10, tycoonBlock.isAutoMinerEnabled());
                statement.setString(11, tycoonBlock.getTycoonType().name());
                statement.setLong(12, tycoonBlock.getCreationTime());
                statement.setInt(13, tycoonBlock.getIndex());
                statement.setString(14, tycoonBlock.getHologramUID());

                statement.executeUpdate();  //run

                saveTycoonUpgrades(tycoonBlock);
                saveTycoonClaimedLevels(tycoonBlock);
                saveTycoonInventory(tycoonBlock);
                //saveTycoonActiveMaterials(tycoonBlock);
                saveTycoonActiveBoosters(tycoonBlock);
                saveTycoonSpawnedBlockLocations(tycoonBlock, activeBlocksSnapshot);
            } catch (SQLException e) {
                Console.error(getClass(), "Database save failed! Reason: " + e.getMessage());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // GEÄNDERT: Interrupted Status setzen
            Console.error(getClass(), "Save process was interrupted!");
        } finally {
            semaphore.release();
        }

    }
    public void saveTycoonAsync(TycoonBlock tycoonBlock) {
        List<SpawnBlock> activeBlocksSnapshot = new ArrayList<>(tycoonBlock.getActiveBlocks());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Console.log(getClass(), "Tycoon " + tycoonBlock.getBlockUID() + " saved Async!");
            saveTycoon(tycoonBlock, activeBlocksSnapshot);
        });
    }
    private void saveTycoonUpgrades(TycoonBlock tycoonBlock) {
        TycoonUpgrades upgrades = tycoonBlock.getTycoonUpgrades();

        String sql = "INSERT OR REPLACE INTO tycoon_upgrades (" +
                "tycoon_uid," +
                " spawn_rate_level," +
                " mining_rate_level," +
                " sell_multiplier_level," +
                " inventory_storage_level," +
                " double_drops_level," +
                " fortune_level," +
                " is_autominer_unlocked," +
                "multi_miner_level" +
                ") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tycoonBlock.getBlockUID());
            statement.setInt(2, upgrades.getSpawnRateLevel());
            statement.setInt(3, upgrades.getMiningRateLevel());
            statement.setInt(4, upgrades.getSellMultiplierLevel());
            statement.setInt(5, upgrades.getInventoryStorageLevel());
            statement.setInt(6, upgrades.getDoubleDropsLevel());
            statement.setInt(7, upgrades.getFortuneLevel());
            statement.setBoolean(8, upgrades.isAutoMinerUnlocked());
            statement.setInt(9, upgrades.getMultipleMinerLevel());

            statement.executeUpdate();
        } catch (SQLException e) {
            Console.error(getClass(), "Database save failed while saving tycoon upgrades! Reason: " + e.getMessage());
        }
    }
    private void saveTycoonClaimedLevels(TycoonBlock tycoonBlock) {
        String delSql = "DELETE FROM tycoon_claimed_levels WHERE tycoon_uid = ?";

        try(PreparedStatement delStatement = connection.prepareStatement(delSql)) {
            delStatement.setString(1, tycoonBlock.getBlockUID());
            delStatement.executeUpdate();
        } catch (SQLException e) {
            Console.error(getClass(), "Database claimed levels save failed! Reason: " + e.getMessage());
        }

        String sql = "INSERT OR REPLACE INTO tycoon_claimed_levels" +
                "(tycoon_uid," +
                "claimed_level)" +
                "VALUES (?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
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
        //Delete previous entries
        String delSql = "DELETE FROM tycoon_inventories WHERE tycoon_uid = ?";
        try (PreparedStatement delStatement = connection.prepareStatement(delSql)){
            delStatement.setString(1, tycoonBlock.getBlockUID());
            delStatement.executeUpdate();
        }catch (SQLException e) {
            Console.error(getClass(), "Database inventory save failed! Reason: " + e.getMessage());
        }

        String sql = "INSERT OR REPLACE INTO tycoon_inventories " +
                "(tycoon_uid," +
                "material," +
                "rarity," +
                "amount," +
                "slot)" +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, tycoonBlock.getBlockUID());
            //New Map save
            for (Map.Entry<StoredItemKey, Integer> entry : tycoonBlock.getStoredItems().entrySet()) {
                statement.setString(2, entry.getKey().material().name());
                statement.setString(3, entry.getKey().rarity().name());
                statement.setInt(4, entry.getValue());
                statement.setInt(5, 0);
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            Console.error(getClass(), "Database inventory save failed! Reason: " + e.getMessage());
        }
    }
    @Deprecated
    private void saveTycoonActiveMaterials(TycoonBlock tycoonBlock) {
        //Map<Material, Boolean> activeMaterials = tycoonBlock.getActiveRessourceMaterialsMap();
        String delSql = "DELETE FROM tycoon_active_materials WHERE tycoon_uid = ?";
        //Delete previous entries
        try (PreparedStatement delStatement = connection.prepareStatement(delSql)){
            delStatement.setString(1, tycoonBlock.getBlockUID());
            delStatement.executeUpdate();
        }catch (SQLException e) {
            Console.error(getClass(), "Database active materials save failed! Reason: " + e.getMessage());
        }
        String sql = "INSERT OR REPLACE INTO tycoon_active_materials " +
                "(tycoon_uid," +
                "material," +
                "rarity," +
                "is_active)" +
                "VALUES (?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setString(1, tycoonBlock.getBlockUID());
            for (SpawnMaterial spawnMaterial : tycoonBlock.getSpawnMaterials()){
                statement.setString(2, spawnMaterial.getMaterial().name());
                statement.setString(3, spawnMaterial.getRarity().name());
                statement.setBoolean(4, spawnMaterial.isActive());
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            Console.error(getClass(), "Database active materials save failed! Reason: " + e.getMessage());
        }
    }

    private void saveTycoonActiveBoosters(TycoonBlock tycoonBlock) {
        TycoonBoosterAbstract tycoonBooster = tycoonBlock.isAnyBoosterActive();
        if (tycoonBooster == null) return;
        String sql = "INSERT OR REPLACE INTO tycoon_active_boosters " +
                "(tycoon_uid," +
                "is_booster_active," +
                "booster_type," +
                "boost_value," +
                "boost_duration)" +
                "VALUES (?, ?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
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
    private void saveTycoonSpawnedBlockLocations(TycoonBlock tycoonBlock, List<SpawnBlock> activeBlocksSnapshot) {
        String delSql = "DELETE FROM tycoon_spawned_block_locations WHERE tycoon_uid = ?";
        try(PreparedStatement delStatement = connection.prepareStatement(delSql)) {
            delStatement.setString(1, tycoonBlock.getBlockUID());
            delStatement.executeUpdate();
        }catch (SQLException e) {
            Console.error(getClass(), "Database spawned blocks save failed! Reason: " + e.getMessage());
        }
        String sql = "INSERT OR REPLACE INTO tycoon_spawned_block_locations " +
                "(tycoon_uid," +
                "world," +
                "x, y, z," +
                "rarity)" +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tycoonBlock.getBlockUID());
            for (SpawnBlock block : activeBlocksSnapshot) {
                Location location = block.getSpawnLocation();
                statement.setString(2, Objects.requireNonNull(block.getSpawnLocation().getWorld()).getName());
                statement.setDouble(3, location.getX());
                statement.setDouble(4, location.getY());
                statement.setDouble(5, location.getZ());
                statement.setString(6, block.getSpawnMaterialRarity().name());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            Console.error(getClass(), "Database spawned blocks save failed! Reason: " + e.getMessage());
        }
    }

    public void loadTycoons() {
        try {
            semaphore.acquire();

            String sql = "SELECT * FROM tycoons";
            try (PreparedStatement statement = connection.prepareStatement(sql)) { //Reads the db data
                try(ResultSet result = statement.executeQuery()) {
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
                        //loadTycoonActiveMaterials(tycoonBlock);
                        loadTycoonActiveBoosters(tycoonBlock);
                        loadTycoonSpawnedLocations(tycoonBlock);

                        tycoonBlock.setLoaded(true);

                        Console.debug(getClass(), "Tycoon " + uid + " index: " + tycoonBlock.getIndex() + " Before registering");
                        tycoonRegistry.addTycoon(tycoonBlock);
                        Console.debug(getClass(), "Tycoon " + uid + " index: " + tycoonBlock.getIndex() + " After registering");
                    }
                }
            } catch (SQLException e) {
                Console.error(getClass(), "Database load failed! Reason: " + e.getMessage());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Console.error(getClass(), "Load interrupted!");
        }finally {
            semaphore.release(); // GEÄNDERT: DB wieder für Backups/Saves freigeben
        }

        for (TycoonBlock block : tycoonRegistry.getAllTycoons()) {
            Console.debug(getClass(), "Tycoon " + block.getBlockUID() + " index: " + block.getIndex() + " Before creating hologram and updating attributes");
            block.createHologram();
            block.updateAttributes();
            Console.debug(getClass(), "Tycoon " + block.getBlockUID() + " index: " + block.getIndex() + " After creating hologram and updating attributes");
        }
    }
    private TycoonUpgrades loadTycoonUpgrades(String tycoonUID) {
        TycoonUpgrades upgrades = new TycoonUpgrades();

        String sql =  "SELECT * FROM tycoon_upgrades WHERE tycoon_uid = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tycoonUID);
            try(ResultSet result = statement.executeQuery()) {
                if (result.next()){
                    upgrades.setSpawnRateLevel(result.getInt("spawn_rate_level"));
                    upgrades.setMiningRateLevel(result.getInt("mining_rate_level"));
                    upgrades.setSellMultiplierLevel(result.getInt("sell_multiplier_level"));
                    upgrades.setInventoryStorageLevel(result.getInt("inventory_storage_level"));
                    upgrades.setDoubleDropsLevel(result.getInt("double_drops_level"));
                    upgrades.setFortuneLevel(result.getInt("fortune_level"));
                    upgrades.setAutoMinerUnlocked(result.getBoolean("is_autominer_unlocked"));
                    upgrades.setMultipleMinerLevel(result.getInt("multi_miner_level"));
                }
            }
        } catch (SQLException e) {
            Console.error(getClass(), "Database load for tycoon upgrades failed! Reason: " + e.getMessage());
        }

        return upgrades;
    }
    private void loadTycoonClaimedLevels(TycoonUpgrades upgrades, String blockUID) {
        List<Integer> claimedLevels = new ArrayList<>();
        String sql = "SELECT * FROM tycoon_claimed_levels WHERE tycoon_uid = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, blockUID);
            try(ResultSet result = statement.executeQuery()) {
                while (result.next()){
                    int claimedLevel = result.getInt("claimed_level");
                    claimedLevels.add(claimedLevel);
                }
                upgrades.setClaimedLevels(claimedLevels);
            }
        } catch (SQLException e) {
            Console.error(getClass(), "Database load for tycoon upgrades claimed levels failed! Reason: " + e.getMessage());
        }

    }
    private void loadTycoonInventory(TycoonBlock tycoonBlock) {
        String sql = "SELECT * FROM tycoon_inventories WHERE tycoon_uid = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tycoonBlock.getBlockUID());
            try(ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    String materialName = result.getString("material");
                    Material material = Material.valueOf(materialName);

                    String rarityName = result.getString("rarity");
                    SpawnMaterialRarity spawnMaterialRarity = SpawnMaterialRarity.valueOf(rarityName);

                    int amount = result.getInt("amount");

                    ItemStack itemStack = new  ItemStack(material, amount);


                    tycoonBlock.addItem(new StoredItemKey(material, spawnMaterialRarity), amount);
                }
            }
        } catch (SQLException e) {
            Console.error(getClass(), "Database inventory load failed! Reason: " + e.getMessage());
        }
    }
    @Deprecated
    private void loadTycoonActiveMaterials(TycoonBlock tycoonBlock) {
        String sql =  "SELECT * FROM tycoon_active_materials WHERE tycoon_uid = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tycoonBlock.getBlockUID());
            try (ResultSet result = statement.executeQuery()){
                while (result.next()) {
                    String materialName = result.getString("material");
                    String rarity = result.getString("rarity");
                    boolean isActive = result.getBoolean("is_active");

                    try {
                        Material material = Material.valueOf(materialName);
                        SpawnMaterialRarity spawnMaterialRarity = SpawnMaterialRarity.valueOf(rarity);

                        tycoonBlock.getSpawnMaterials().stream()
                                .filter(sm -> sm.getMaterial().equals(material)
                                        && sm.getRarity().equals(spawnMaterialRarity))
                                .findFirst()
                                .ifPresent(sm -> sm.setActive(isActive));
                    } catch (IllegalArgumentException e) {
                        Console.error(getClass(), "Unknown material or rarity in DB: "
                                + materialName + " / " + rarity + " — skipping");
                    }

                }
            }
        } catch (SQLException e) {
            Console.error(getClass(), "Database active materials load failed! Reason: " + e.getMessage());
        }
    }

    private void loadTycoonActiveBoosters(TycoonBlock tycoonBlock) {
        String sql = "SELECT * FROM tycoon_active_boosters WHERE tycoon_uid = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tycoonBlock.getBlockUID());
            try(ResultSet result = statement.executeQuery()) {
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
            }
        } catch (SQLException e) {
            Console.error(getClass(), "Database active boosters load failed! Reason: " + e.getMessage());
        }
    }
    private void loadTycoonSpawnedLocations(TycoonBlock tycoonBlock) {
        String sql = "SELECT * FROM tycoon_spawned_block_locations WHERE tycoon_uid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, tycoonBlock.getBlockUID());
            try (ResultSet result = statement.executeQuery()){
                while (result.next()) {
                    World world = Bukkit.getWorld(result.getString("world"));
                    if (world == null) {
                        Console.error(getClass(), "Spawned block locations load failed! Reason: " + result.getString("world") + " is null!");
                        continue;
                    }
                    double x = result.getDouble("x");
                    double y = result.getDouble("y");
                    double z = result.getDouble("z");

                    SpawnMaterialRarity spawnMaterialRarity = SpawnMaterialRarity.valueOf(result.getString("rarity"));

                    Location location = new Location(world, x, y, z);
                    Block block = world.getBlockAt(location);
                    block.setMetadata("tycoon_id", new FixedMetadataValue(plugin, tycoonBlock.getBlockUID()));

                    List<SpawnMaterial> spawnMaterials = tycoonBlock.getSpawnMaterials();
                    spawnMaterials.stream()
                            .filter(sm -> sm.getMaterial().equals(block.getType())
                                    && sm.getRarity().equals(spawnMaterialRarity))
                            .findFirst()
                            .ifPresent(sm -> tycoonBlock.getActiveBlocks().add(new SpawnBlock(sm, location)));
                }
            }
        } catch (SQLException e) {
            Console.error(getClass(), "Database spawned locations load failed! Reason: " + e.getMessage());
        }
    }
}
