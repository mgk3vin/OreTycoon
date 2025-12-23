package me.mangokevin.oreTycoon.tycoonManagment;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.levelManagment.LevelManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static me.mangokevin.oreTycoon.tycoonManagment.TycoonData.TYPE_KEY;

public class TycoonBlockManager {

    private final OreTycoon plugin;
    private final LevelManager  levelManager;

    private BukkitTask generatorTask;

    // ---------     Tycoon Key      ---------
    public static final NamespacedKey TYCOON_BLOCK_KEY = new NamespacedKey(
            JavaPlugin.getPlugin(OreTycoon.class), // Der Namespace (dein Plugin)
            "IS_TYCOON_BLOCK"                      // Der eigentliche Schlüsselname
    );
    // ---------     Tycoon Key      ---------
    //--------  NamespacedKeys  --------
    //--------  NamespacedKeys  --------

    //private static final HashMap<Location, UUID> tycoonBlocks = new HashMap<>();
    // ! Replaced with @tycoonBlocks

    private final Map<Location, TycoonBlock> tycoonBlocks;
    private final Map<String, TycoonBlock> tycoonBlocksUID;
    private final int maxBlocksPerPlayer;

    private static final List<Material> TYCOON_RESOURCE_MATERIALS = Arrays.asList(
            Material.COAL_ORE,
            Material.IRON_ORE,
            Material.GOLD_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE
            // Fügen Sie hier weitere Materialien hinzu
    );

    public TycoonBlockManager(@NotNull OreTycoon plugin, LevelManager levelManager) {
        this.plugin = plugin;
        this.levelManager = levelManager;
        this.tycoonBlocks = new HashMap<>();
        this.tycoonBlocksUID = new HashMap<>();
        maxBlocksPerPlayer = plugin.getConfig().getInt("maxBlocksPerPlayer"); //Config hinzufügen✅


        new BukkitRunnable() {
            @Override
            public void run() {
                for (TycoonBlock tycoon : tycoonBlocks.values()) {
                    tycoon.incrementAndCheck(); // Jeder Block zählt für sich selbst hoch
                }
            }
        }.runTaskTimer(plugin, 0, 20L); // Läuft jede Sekunde

    }


    public void openTycoonSpecificMenu(Player player, TycoonBlock tycoonBlock) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dm open tycoon_gui " + player.getName());
        player.setMetadata("viewing_tycoon", new FixedMetadataValue(plugin, tycoonBlock.getBlockUID()));
    }
    public void openTycoonMenu(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dm open tycoon_menu " + player.getName());
        player.setMetadata("viewing_tycoon_menu", new FixedMetadataValue(plugin, tycoonBlocks));
    }

    public List<TycoonBlock> getTycoonBlocksFromPlayer(UUID playerUUID) {
        List<TycoonBlock> tycoonBlocksList = new ArrayList<>();
        for (TycoonBlock tycoonBlock : tycoonBlocks.values()) {
            if (tycoonBlock.getOwnerUuid().equals(playerUUID)) {
                tycoonBlocksList.add(tycoonBlock);
            }
        }

        tycoonBlocksList.sort(new Comparator<TycoonBlock>() {
            @Override
            public int compare(TycoonBlock t1, TycoonBlock t2) {
                return Long.compare(t1.getCreationTime(), t2.getCreationTime());    //letzte Tycoons zuerst
            }
        });
        System.out.println("[BlockManager] returning " + tycoonBlocksList);
        return tycoonBlocksList;
    }
    public TycoonBlock getTycoonBlockFromIndex(Player player, int index) {
        List<TycoonBlock> tycoonBlocksList = getTycoonBlocksFromPlayer(player.getUniqueId());
        if (index < 1 || index > tycoonBlocksList.size()) {
            return null;
        }
        return tycoonBlocksList.get(index - 1);//1 basiert
    }

    @Deprecated
    public void checkTycoonProgress(TycoonBlock block){
        int currentLevel = block.getLevel();
        int nextLevel = block.getLevel() + 1;
        double progress = block.getProgress();

        if (progress >= 1.0){
            block.setLevel(nextLevel);
        }
    }

    public void playXpBlockHologram(TycoonBlock tycoonBlock, Block block, int xp) {
        tycoonBlock.displayXpHologram(block,xp);
        System.out.println("[BlockManager] displayXpHologram");
        new BukkitRunnable() {
            @Override
            public void run() {
                tycoonBlock.removeXpHologram(block);
                System.out.println("[BlockManager] removeXpHologram");
            }
        }.runTaskLater(plugin, 20L * 2);
    }
    // ---------------- Filesave working ----------------
    public void saveTycoons(){
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs(); // Erstellt den Ordner, falls er fehlt
        }

        File file = new File(plugin.getDataFolder(), "tycoons.yml");
        // Vorherige Daten löschen, um Duplikate zu vermeiden
        YamlConfiguration data = new  YamlConfiguration();

        for (TycoonBlock tycoon : tycoonBlocks.values()) {
            String UID = tycoon.getBlockUID();
            String path = "data." + UID + ".";

            // Wir speichern KEINE Location-Objekte, nur primitive Daten
            data.set(path + "world", tycoon.getLocation().getWorld().getName());
            data.set(path + "x", tycoon.getLocation().getBlockX());
            data.set(path + "y", tycoon.getLocation().getBlockY());
            data.set(path + "z", tycoon.getLocation().getBlockZ());

            // UUID als sauberer String
            data.set(path + "ownerUUID", tycoon.getOwnerUuid().toString());
            data.set(path + "ownerName", tycoon.getOwnerName());

            data.set(path + "type", tycoon.getTycoonType().name());

            data.set(path + "material", tycoon.getTycoonType().getMaterial().toString());
            data.set(path + "level", tycoon.getLevel());
            data.set(path + "xp", tycoon.getLevelXp());
            data.set(path + "isActive", tycoon.isActive());
            data.set(path + "creationDate", tycoon.getCreationTime());
            data.set(path + "index", tycoon.getIndex());

            if (tycoon.getLastSpawnedBlock() != null) {
                data.set(path + "lastSpawnedBlock", tycoon.getLastSpawnedBlock().name());
            }
            List<String> blockLocs = new ArrayList<>();
            for (Block block : tycoon.getActiveBlocks()){
                blockLocs.add(block.getX() + "," + block.getY() + "," + block.getZ());
            }
            data.set(path + "spawnedBlocks", blockLocs);

            data.set(path + "spawnInterval", tycoon.getSpawnInterval());
            data.set(path + "hologramUID", tycoon.getHologramUID());
        }
        try {
            data.save(file);
            plugin.getLogger().info("Alle Tycoons wurden erfolgreich in tycoons.yml gespeichert!");
        } catch (IOException e) {
            plugin.getLogger().severe("Fehler beim Speichern der Tycoons: " + e.getMessage());
        };
    }
    public void loadTycoons(){
        File file = new File(plugin.getDataFolder(), "tycoons.yml");
        if (!file.exists()) return;

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yaml.getConfigurationSection("data");

        if (section == null) return;


        for (String key : section.getKeys(false)) {
            String path = key + ".";
            try {
                // 1. Location aus Einzelteilen aufbauen
                String worldName = section.getString(path + "world");
                int x = section.getInt(path + "x");
                int y = section.getInt(path + "y");
                int z = section.getInt(path + "z");

                assert worldName != null;
                World world = Bukkit.getWorld(worldName);
                if (world == null) continue;
                Location loc = new Location(world, x, y, z);

                // 2. UUID als reinen String laden
                String uuidStr = section.getString(path + "ownerUUID");
                if (uuidStr == null) continue;
                UUID ownerUUID = UUID.fromString(uuidStr);
                String typeStr = section.getString(path + "type");
                TycoonType tycoonType = TycoonType.valueOf(typeStr);

                // 3. Restliche Daten
                int level = section.getInt(path + "level");
                int xp = section.getInt(path + "xp");
                boolean active = section.getBoolean(path + "isActive");
                long  creationTime = section.getLong(path + "creationDate");
                int index = section.getInt(path + "index");

                String tycoonMaterialString = section.getString(path + "material");
                assert tycoonMaterialString != null;
                Material tycoonMaterial = Material.getMaterial(tycoonMaterialString);

                int spawnInterval = section.getInt(path + "spawnInterval");
                String matName = section.getString(path + "lastSpawnedBlock");
                Material type = (matName != null) ? Material.getMaterial(matName) : Material.STONE;

                // 4. Objekt erstellen
                // Wichtig: Nutze deinen Konstruktor.
                // Falls er einen Spielernamen braucht, nimm Bukkit.getOfflinePlayer(ownerUUID).getName()
                TycoonBlock block = new TycoonBlock(tycoonType ,loc, ownerUUID, active, plugin, this,levelManager);
                block.setLevel(level);
                block.setLevelXp(xp);
                block.setCreationTime(creationTime);
                block.setIndex(index);
                System.out.println("[BlockManager] Loading Tycoon " + block.getLevel() + "|" + block.getLevelXp() + "|" + block.getIndex() + "|" + block.getMaterial().toString());

                if (type != null) {
                    block.setLastSpawnedBlock(type);
                }
                tycoonBlocks.put(loc, block);

                List<String> blockLocs = section.getStringList(path + "spawnedBlocks");
                for (String s : blockLocs) {
                    String[] parts = s.split(",");
                    int bx = Integer.parseInt(parts[0]);
                    int by = Integer.parseInt(parts[1]);
                    int bz = Integer.parseInt(parts[2]);
                    Block b = world.getBlockAt(bx, by, bz);

                    if (b.getType() != Material.AIR) {
                        System.out.println("[BlockManager] Reloading Block: " + b.getType() + "|" + bx + "," + by + "," + bz);
                        block.addActiveBlocks(b); // Methode in TycoonBlock, die den Block in die interne Liste packt
                    }
                }

                //block.createHologram(); // Hologramm neu starten

            } catch (Exception e) {
                plugin.getLogger().severe("Fehler beim Laden von Tycoon " + key + ": " + e.getMessage());
            }
        }
        for (TycoonBlock block : tycoonBlocks.values()) {
            block.createHologram();
            System.out.println("[BlockManager] Loading Hologram " + block.getLevel() + "|" + block.getLevelXp() + "|" + block.getIndex() + "|" + block.getMaterial());

        }
    }
    // ---------------- Filesave working ----------------

    @Deprecated
    private void loadDropsFromConfig(FileConfiguration config) {
        // Holen Sie die Liste der Sektionen unter 'tycoon-generator.drops'
        List<Map<?, ?>> dropsList = config.getMapList("tycoon-generator.drops");

        for (Map<?, ?> dropMap : dropsList) {
            String materialName = (String) dropMap.get("material");
            Double chance = (Double) dropMap.get("chance");

            // --- ⚠️ WICHTIG: Die sichere Konvertierung ---
            Material material = Material.getMaterial(materialName);

            if (material == null) {
                // Das Material existiert nicht (z.B. Tippfehler oder alte Version)
                plugin.getLogger().warning("Ungültiges Material in der Konfig: " + materialName + ". Wird ignoriert.");
                continue; // Springe zum nächsten Eintrag
            }

            if (chance == null) {
                plugin.getLogger().warning("Fehlende Chance für Material " + materialName + " in der Konfig. Wird ignoriert.");
                continue;
            }

            // Füge den validierten Drop zur aktiven Liste hinzu
            //possibleDrops.add(new ResourceDrop(material, chance));
        }
    }

    public boolean isTycoonBlock(@NotNull ItemStack placedItem) {

        if (placedItem.getItemMeta().getPersistentDataContainer().has(TYCOON_BLOCK_KEY, PersistentDataType.BYTE)) {

            // JA! Es ist unser Tycoon Block

            return true;
        }

        return false;
    }
    public boolean isTycoonBlock(@NotNull Block brokenBlock) {

        if (tycoonBlocks.containsKey(brokenBlock.getLocation())) {

            // JA! Es ist unser Tycoon Block
            return true;

        }

        return false;
    }
    public boolean isTycoonBlock(@NotNull Location brokenLocation) {

        if (tycoonBlocks.containsKey(brokenLocation)) {

            // JA! Es ist unser Tycoon Block
            return true;

        }

        return false;
    }

    public int getTycoonCount(UUID playerUUID) {
        int count = 0;
        for (TycoonBlock block : tycoonBlocks.values()) {
            if (block.getOwnerUuid().equals(playerUUID)) {
                count++;
            }
        }
        return count;
    }


    @Deprecated
    public boolean isObstructed(TycoonBlock tycoonBlock, Player player) {

        Location location = tycoonBlock.getLocation();
        World world = location.getWorld();
        int centerX = location.getBlockX();
        int centerZ = location.getBlockZ();
        int centerY = location.getBlockY();

        for (int x = centerX -2; x <=  centerX + 2; x++) {
            for (int z = centerZ -2; z <=  centerZ + 2; z++) {
                //for (int z = centerZ -2; z <=  centerZ + 2; z++) {}   optional für 3d scan mit y

                Location checkLocation = new Location(world, x, centerY, z);
                if (isTycoonBlock(checkLocation)){
                    player.sendMessage(ChatColor.RED + "Tycoon blocks must be placed atleast 5 blocks away from eachother!");
                    return true;
                }else {
                    return false;
                }
            }
        }
        return false;
    }

    @Deprecated
    public void startGenerator(TycoonBlock tycoonBlock, Player player) {
        if (this.generatorTask != null) {
            stopGenerator(tycoonBlock);
        }
        tycoonBlock.setActive(true);
        tycoonBlock.updateHologramPreset(tycoonBlock.getLocation(), "STATUS");
        this.generatorTask = new BukkitRunnable() {
            @Override
            public void run() {
                tycoonBlock.trySpawnRessource();
            }
        }.runTaskTimer(plugin, 0, 20L * 5);


    }
    @Deprecated
    public void stopGenerator(TycoonBlock tycoonBlock) {
        if (this.generatorTask != null) {
            this.generatorTask.cancel();
            tycoonBlock.setActive(false);
            tycoonBlock.updateHologramPreset(tycoonBlock.getLocation(), "STATUS");
        }

    }

    public boolean isObstructed(Location location, Player player) {
        World world = location.getWorld();
        int centerX = location.getBlockX();
        int centerZ = location.getBlockZ();
        int centerY = location.getBlockY();

        for (int x = centerX - 4; x <=  centerX + 4; x++) {
            for (int z = centerZ - 4; z <=  centerZ + 4; z++) {
                //for (int z = centerZ -2; z <=  centerZ + 2; z++) {}   optional für 3d scan mit y

                Location checkLocation = new Location(world, x, centerY, z);
                //System.out.println("[OreTycoon] checkLocation: " + centerX + "|" + centerY + "|" + centerZ);
                if (isTycoonBlock(checkLocation)){
                    player.sendMessage(ChatColor.RED + "Tycoon blocks must be placed atleast 5 blocks away from eachother!");
                    return true;
                }
            }
        }
        return false;
    }

    @Deprecated //moved to TycoonBlock class
    public void trySpawnRessource(TycoonBlock tycoonBlock, Player player) {
        Location center = tycoonBlock.getLocation();
        World world = center.getWorld();
        Random rand = new Random();

        // 1. Definiere das 5x5 Areal (vom Zentrum aus -2 bis +2)
        int minX = center.getBlockX() - 2;
        int maxX = center.getBlockX() + 2;
        int minZ = center.getBlockZ() - 2;
        int maxZ = center.getBlockZ() + 2;
        int fixedY = center.getBlockY(); // Wir spawnen nur auf der Y-Ebene des Tycoon-Block

        int randomX = rand.nextInt(maxX - minX + 1) + minX;
        int randomZ = rand.nextInt(maxZ - minZ + 1) + minZ;

        Location randomLocation = new Location(center.getWorld(), randomX, fixedY, randomZ);
        Block spawnBlock = randomLocation.getBlock();

        if (spawnBlock.getType().equals(Material.AIR)) {
            //Valid Spawn point
            Material material = randomMaterial();
            spawnBlock.setType(material);

            //tycoonBlock.manipulateHologram(tycoonBlock.getLocation(), material.name());
            tycoonBlock.setLastSpawnedBlock(material);
            tycoonBlock.updateHologramPreset(tycoonBlock.getLocation(), "BLOCK");
            player.playSound(randomLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.5f);
            player.spawnParticle(Particle.EXPLOSION, randomLocation, 1);
        }
    }

    @Deprecated //moved to TycoonBlock class
    private Material randomMaterial() {
        Random rand = new Random();
        int randint = rand.nextInt(0, TYCOON_RESOURCE_MATERIALS.size());

        return TYCOON_RESOURCE_MATERIALS.get(randint);
    }


    public void pickupTycoonBlock(Block block, Player player, TycoonBlock blockData) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() == Material.AIR) {
            giveSmartTycoonBlock(blockData, player);
        }else{
            player.sendMessage(ChatColor.RED + "Your hand needs to be empty!");
            return;
        }
        blockData.removeHologram(block.getLocation());
        block.setType(Material.AIR);
        player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 1.5F);
        removeTycoonBlock(block);
        //stopGenerator(blockData);
        for (TycoonBlock tycoonBlock : tycoonBlocks.values()) {
            tycoonBlock.updateHologramPreset(tycoonBlock.getLocation(), "ORDER");
        }
    }

    public void addTycoonBlock(Block placedBlock, UUID playerUuid, TycoonType tycoonType) {

        TycoonBlock tycoonBlock = new TycoonBlock(tycoonType ,placedBlock.getLocation(), playerUuid,false, plugin, this,levelManager);
        tycoonBlocks.put(placedBlock.getLocation(), tycoonBlock);
        tycoonBlocksUID.put(tycoonBlock.getBlockUID(), tycoonBlock);
        System.out.println("[OreTycoon] Added Tycoon Block " + playerUuid + " index" + tycoonBlock.getIndex());
    }
    public void addTycoonBlock(TycoonBlock block){
        tycoonBlocks.put(block.getLocation(), block);
        tycoonBlocksUID.put(block.getBlockUID(), block);
        System.out.println("[OreTycoon] Added Tycoon Block " + block.getOwnerName() + " index" + block.getIndex());
    }

    public void removeTycoonBlock(Block placedBlock) {
        //TycoonBlock removedTycoonBlock = tycoonBlocks.remove(placedBlock.getLocation());
        tycoonBlocks.remove(placedBlock.getLocation());
        //tycoonBlocksUID.remove(getTycoonBlock(placedBlock).getBlockUID());


    }

    public TycoonBlock getTycoonContainsBlock(Block block) {
        for (TycoonBlock tycoonBlock : tycoonBlocks.values()) {
            if (tycoonBlock.containsBlock(block)){
                return tycoonBlock;
            }
        }
        return null;
    }

    public TycoonBlock getTycoonBlock(Block placedBlock) {
        return tycoonBlocks.get(placedBlock.getLocation());
    }
    @Deprecated
    public TycoonBlock getTycoonBlockByUID(String blockUID) {
        return tycoonBlocksUID.get(blockUID);
    }


    public TycoonBlock getTycoonBlock(String blockUID){
        for (TycoonBlock tycoonBlock : tycoonBlocks.values()) {
            if (blockUID.equals(tycoonBlock.getBlockUID())){
                return tycoonBlock;
            }
        }
        return null;
    }

    @Deprecated
    public void giveTycoonBlock(Player p, Material type) {
        // 1. Das Item erstellen (ItemStack)
        ItemStack tycoonBlock = new ItemStack(type, 1);

        ItemMeta meta = tycoonBlock.getItemMeta();
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);


        // 3. Den Tag setzen
        // Wir speichern den Wert 1 (als Byte) unter dem Schlüssel.
        // Das ist wie: is_tycoon_block = 1
        //assert meta != null;
        meta.getPersistentDataContainer().set(TYCOON_BLOCK_KEY, PersistentDataType.BYTE, (byte) 1);

        // 4. Meta speichern und Item geben
        meta.setDisplayName("§bTycoon Block");
        tycoonBlock.setItemMeta(meta);
        p.getInventory().addItem(tycoonBlock);
    }
    public void giveTycoonBlock(Player p, TycoonType type) {
        // Material aus dem Enum holen
        ItemStack item = new ItemStack(type.getMaterial(), 1);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        // 1. Markierung als Tycoon UND Speichern des Typs
        meta.getPersistentDataContainer().set(TYCOON_BLOCK_KEY, PersistentDataType.BYTE, (byte) 1);
        // WICHTIG: Hier speichern wir, welcher Enum-Typ es ist (z.B. "COAL")
        meta.getPersistentDataContainer().set(TYPE_KEY, PersistentDataType.STRING, type.name());

        // 2. Optik
        meta.setDisplayName(type.getName() + " §7Tycoon");
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        List<String> lore = new ArrayList<>();
        lore.add("§8§m-----------------------");
        lore.add("§7Typ: " + type.getName());
        lore.add("§7Level: §e1");
        lore.add("§8§m-----------------------");
        meta.setLore(lore);

        item.setItemMeta(meta);
        p.getInventory().addItem(item);
    }
    public void giveSmartTycoonBlock(TycoonBlock tycoonBlock, Player player) {


        ItemStack item = new ItemStack(tycoonBlock.getTycoonType().getMaterial(), 1);

        TycoonData.writeToItem(item, tycoonBlock.getLevel(), tycoonBlock.getLevelXp(), tycoonBlock.getCreationTime(), tycoonBlock.getMaterial(), tycoonBlock.getSpawnInterval(), tycoonBlock.getCreationTime(), tycoonBlock.getTycoonType().toString());
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return;
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);


        List<String> lore = new ArrayList<>();
        lore.add("§8§m-----------------------");
        lore.add("§7Level: §e" + tycoonBlock.getLevel());
        lore.add("§7XP: §f" + tycoonBlock.getLevelXp());
        lore.add("§7Progress: §f" + tycoonBlock.getProgressBar(20));
        lore.add("§7Spawnrate: §f" + tycoonBlock.getSpawnInterval() + "s");
        lore.add("§8§m-----------------------");
        meta.setLore(lore);
        meta.setDisplayName(tycoonBlock.getTycoonType().getName());

        item.setItemMeta(meta);

        player.getInventory().setItemInMainHand(item);

    }

    public int getMaxBlocksPerPlayer() {
        return this.maxBlocksPerPlayer;
    }
    public LevelManager getLevelManager() {
        return this.levelManager;
    }
    // ---------     Getter      ---------
    public NamespacedKey getTycoonBlockKey() {
        return TYCOON_BLOCK_KEY;
    }
    // ---------     Getter      ---------


}


