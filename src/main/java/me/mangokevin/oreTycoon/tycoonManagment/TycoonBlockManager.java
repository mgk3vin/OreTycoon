package me.mangokevin.oreTycoon.tycoonManagment;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonEvents.TycoonAutoMinedEvent;
import me.mangokevin.oreTycoon.utility.Console;
import me.mangokevin.oreTycoon.levelManagment.LevelManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
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
        }.runTaskTimer(plugin, 0, 1L); // Läuft jeden Tick (20 pro sekunde)

    }
    @Deprecated //Moved to Tycoonblock class
    public boolean tryAutoMining(TycoonBlock tycoonBlock, Location blockLocation) {
        ItemStack item = new ItemStack(blockLocation.getBlock().getType());
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return false;
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        pdc.set(TycoonData.BLOCK_IS_AUTOMINED_KEY, PersistentDataType.STRING, "block_is_automined");
        if (!tycoonBlock.canFitItem(tycoonBlock.getInventory(), item)){
            return false;
        }
        new BukkitRunnable() {

            float progress = 0.0f;
            @Override
            public void run() {
                // 1. Punkte zentrieren (damit sie aus der Mitte der Blöcke kommen)
                Location start = tycoonBlock.getLocation().clone().add(0.5, 0.5, 0.5);
                Location target = blockLocation.clone().add(0.5, 0.5, 0.5);

                // 2. Vektor vom Start zum Ziel berechnen
                Vector direction = target.toVector().subtract(start.toVector());
                double distance = start.distance(target); // Gesamtlänge der Strecke

                // 3. Den Vektor normalisieren (auf die Länge 1 bringen) und skalieren
                // Wir wollen alle 0.2 Blöcke einen Partikel
                double spacing = 0.2;
                direction.normalize().multiply(spacing);

                // 4. Die Linie entlanglaufen und Partikel spawnen
                Location current = start.clone();
                for (double i = 0; i < distance; i += spacing) {
                    // Partikel spawnen (z.B. Dust für farbige Laser oder End_Rod für Magie)
                    start.getWorld().spawnParticle(Particle.DUST, current, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 0.5f));

                    // Den Punkt ein Stück weiter in Richtung Ziel schieben
                    current.add(direction);
                }
                // 2. Vibrations-Sound (leise)
                if (progress % 0.2 < 0.05) {
                    start.getWorld().playSound(start, Sound.BLOCK_NOTE_BLOCK_HAT, 0.2f, 0.5f + progress);
                }
                progress += 0.1f;

                if (progress >= 1.0f) {

                    blockLocation.getWorld().playSound(blockLocation, Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
                    blockLocation.getWorld().spawnParticle(Particle.WHITE_SMOKE, blockLocation, 3);


                    //tycoonBlock.getTycoonInventory().addItem(item);
                    ItemStack item =  new ItemStack(blockLocation.getBlock().getType());
                    TycoonAutoMinedEvent event = new TycoonAutoMinedEvent(tycoonBlock, item);
                    Bukkit.getPluginManager().callEvent(event);

                    tycoonBlock.handleReward(blockLocation.getBlock());

                    blockLocation.getBlock().setType(Material.AIR);
                    pdc.remove(TycoonData.BLOCK_IS_AUTOMINED_KEY);
                    this.cancel();
                }

            }
        }.runTaskTimer(plugin, 0, 3L);
        return true;
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
        return tycoonBlocksList;
    }
    public TycoonBlock getTycoonBlockFromIndex(Player player, int index) {
        List<TycoonBlock> tycoonBlocksList = getTycoonBlocksFromPlayer(player.getUniqueId());
        if (index < 1 || index > tycoonBlocksList.size()) {
            return null;
        }
        return tycoonBlocksList.get(index - 1);//1 basiert
    }

//    public void playXpBlockHologram(TycoonBlock tycoonBlock, Block block, int xp) {
//        tycoonBlock.displayXpHologram(block,xp);
//        System.out.println("[BlockManager] displayXpHologram");
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                tycoonBlock.removeXpHologram(block);
//                System.out.println("[BlockManager] removeXpHologram");
//            }
//        }.runTaskLater(plugin, 20L * 1);
//    }
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

            //========== Save Upgrade Attributes ==========
            data.set(path + "spawnRate", tycoon.getSpawnRate());
            data.set(path + "spawnRateLevel", tycoon.getSpawnRateLevel());
            data.set(path + "miningRate", tycoon.getMiningRate());
            data.set(path + "miningRateLevel", tycoon.getMiningRateLevel());
            data.set(path + "sellMultiplierLevel", tycoon.getSellMultiplierLevel());

            data.set(path + "inventoryStorageLevel", tycoon.getTycoonUpgrades().getInventoryStorageLevel());

            //Claimed Levels
            List<Integer> claimedLevels = tycoon.getTycoonUpgrades().getClaimedLevels();
            data.set(path + "claimedLevels", claimedLevels);
            //========== Save Upgrade Attributes ==========

            data.set(path + "material", tycoon.getTycoonType().getMaterial().toString());
            data.set(path + "level", tycoon.getLevel());
            data.set(path + "xp", tycoon.getLevelXp());
            data.set(path + "isActive", tycoon.isActive());
            data.set(path + "isAutoMinerEnabled", tycoon.isAutoMinerEnabled());
            data.set(path + "creationDate", tycoon.getCreationTime());
            data.set(path + "index", tycoon.getIndex());

            //---------- Not used ----------
            if (tycoon.getLastSpawnedMaterial() != null) {
                data.set(path + "lastSpawnedBlock", tycoon.getLastSpawnedMaterial().name());
            }
            //---------- Not used ----------



            //---------- Inventory save ----------
            List<String> inventoryItems = new ArrayList<>();
            for (ItemStack item : tycoon.getInventory()) {
                if (item == null) continue;

                ItemMeta meta = item.getItemMeta();
                if (meta == null) continue;

                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                if (pdc.has(TycoonData.MENU_ITEM_KEY) || pdc.has(TycoonData.MENU_ACTION_KEY)) continue;
                for (int i = 0; i < item.getAmount(); i++) {
                    inventoryItems.add(item.getType().name());
                }

                Console.log("[BlockManager] saving inventory item: " + item.getAmount() + "x " + item.getType().name());
            }
            data.set(path + "inventoryItems", inventoryItems);
            //---------- Inventory save ----------

            List<String> blockLocs = new ArrayList<>();
            for (Block block : tycoon.getActiveBlocks()){
                blockLocs.add(block.getX() + "," + block.getY() + "," + block.getZ());
            }
            data.set(path + "spawnedBlocks", blockLocs);

            data.set(path + "spawnInterval", tycoon.getSpawnRate());
            data.set(path + "hologramUID", tycoon.getHologramUID());
        }
        try {
            data.save(file);
            plugin.getLogger().info("Alle Tycoons wurden erfolgreich in tycoons.yml gespeichert!");
        } catch (IOException e) {
            plugin.getLogger().severe("Fehler beim Speichern der Tycoons: " + e.getMessage());
        }
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
                boolean autoMinerEnabled = section.getBoolean(path + "isAutoMinerEnabled");
                long  creationTime = section.getLong(path + "creationDate");
                int index = section.getInt(path + "index");

                //========== Load Upgrade Attributes ==========
                int spawnRateLevel = section.getInt(path + "spawnRateLevel");
                int miningRateLevel = section.getInt(path + "miningRateLevel");
                int sellMultiplierLevel = section.getInt(path + "sellMultiplierLevel");
                int inventoryStorageLevel = section.getInt(path + "inventoryStorageLevel");
                List<Integer> claimedLevels = section.getIntegerList(path + "claimedLevels");

                TycoonUpgrades tycoonUpgrades = new  TycoonUpgrades();
                if(tycoonUpgrades != null){
                    tycoonUpgrades.setSpawnRateLevel(spawnRateLevel);
                    tycoonUpgrades.setMiningRateLevel(miningRateLevel);
                    tycoonUpgrades.setSellMultiplierLevel(sellMultiplierLevel);
                    tycoonUpgrades.setInventoryStorageLevel(inventoryStorageLevel);
                    tycoonUpgrades.setClaimedLevels(claimedLevels);
                }
                //========== Load Upgrade Attributes ==========


                String tycoonMaterialString = section.getString(path + "material");
                assert tycoonMaterialString != null;

                //int spawnInterval = section.getInt(path + "spawnInterval");
                String matName = section.getString(path + "lastSpawnedBlock");
                Material type = (matName != null) ? Material.getMaterial(matName) : Material.STONE;

                // 4. Objekt erstellen
                // Wichtig: Nutze deinen Konstruktor.
                // Falls er einen Spielernamen braucht, nimm Bukkit.getOfflinePlayer(ownerUUID).getName()
                TycoonBlock block = new TycoonBlock(tycoonType ,loc, ownerUUID, active, plugin, tycoonUpgrades);
                block.setAutoMinerEnabled(autoMinerEnabled);
                block.setLevel(level);
                block.setLevelXp(xp);
                block.setCreationTime(creationTime);
                block.setIndex(index);
                System.out.println("[BlockManager] Loading Tycoon " + block.getLevel() + "|" + block.getLevelXp() + "|" + block.getIndex() + "|" + block.getMaterial().toString());

                if (type != null) {
                    block.setLastSpawnedMaterial(type);
                }
                tycoonBlocks.put(loc, block);

                //---------- Inventory Load ----------
                List<String> inventoryItems = section.getStringList(path + "inventoryItems");
                for (String inventoryItem : inventoryItems) {
                    Material item = Material.getMaterial(inventoryItem);
                    if (item != null) {
                        ItemStack itemStack = new ItemStack(item);
                        block.getTycoonInventory().addItem(itemStack);
                        Console.log("added " + inventoryItem + " to inventory");
                    }
                }
                //---------- Inventory Load ----------

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


            } catch (Exception e) {
                plugin.getLogger().severe("Fehler beim Laden von Tycoon " + key + ": " + e.getMessage());
            }
        }
        for (TycoonBlock block : tycoonBlocks.values()) {
            block.createHologram();
            Console.log("[BlockManager] Creating Hologram: " + PriceUtility.calculateWorth(block.getInventory()));
        }
    }
    // ---------------- Filesave working ----------------

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

    public boolean isObstructed(Location location, Player player) {
        World world = location.getWorld();
        int centerX = location.getBlockX();
        int centerZ = location.getBlockZ();
        int centerY = location.getBlockY();

        for (int x = centerX - 4; x <=  centerX + 4; x++) {
            for (int z = centerZ - 4; z <=  centerZ + 4; z++) {
                for (int y = centerY -2; y <=  centerY + 2; y++) {
                    Location checkLocation = new Location(world, x, y, z);
                    if (isTycoonBlock(checkLocation)){
                        player.sendMessage(ChatColor.RED + "Tycoon blocks must be placed atleast 5 blocks away from eachother!");
                        return true;
                    }
                }
            }
        }
        return false;
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
        // Destroying all active blocks from the tycoon
        for (Block activeBlock : blockData.getActiveBlocks()) {
            if (activeBlock.getType() != Material.AIR) {
                activeBlock.setType(Material.AIR);
                activeBlock.getWorld().playEffect(activeBlock.getLocation(), Effect.SPONGE_DRY,0);
            }
        }
        for (TycoonBlock tycoonBlock : tycoonBlocks.values()) {
            tycoonBlock.updateHologramPreset(tycoonBlock.getLocation(), "ORDER");
        }
    }

    public void addTycoonBlock(Block placedBlock, UUID playerUuid, TycoonType tycoonType) {

        TycoonBlock tycoonBlock = new TycoonBlock(tycoonType ,placedBlock.getLocation(), playerUuid,false, plugin, new TycoonUpgrades());
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

        TycoonData.writeToItem(item, tycoonBlock.getLevel(), tycoonBlock.getLevelXp(), tycoonBlock.getCreationTime(), tycoonBlock.getLocation(),tycoonBlock.getMaterial(), tycoonBlock.getSpawnRate(), tycoonBlock.getCreationTime(), tycoonBlock.getTycoonType().toString(), tycoonBlock.getInventory(), tycoonBlock.getTycoonUpgrades());
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return;
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);


        List<String> lore = new ArrayList<>();
        lore.add("§8§m-----------------------");
        lore.add("§7Level: §e" + tycoonBlock.getLevel());
        lore.add("§7XP: §f" + tycoonBlock.getLevelXp());
        lore.add("§7Progress: §f" + tycoonBlock.getProgressBar(20));
        lore.add("§7Spawnrate: §f" + tycoonBlock.getSpawnRate() + "s");
        lore.add("§8§m-------§r§8Inventory§m--------");
        lore.addAll(inventoryItemsToLore(tycoonBlock.getInventory()));
        lore.add("§8§m-----------------------");
        meta.setLore(lore);
        meta.setDisplayName(tycoonBlock.getTycoonType().getName());


        item.setItemMeta(meta);

        player.getInventory().setItemInMainHand(item);

    }
    private List<String> inventoryItemsToLore(Inventory inventory) {
        List<String> itemList = new ArrayList<>();
        for (ItemStack item : inventory.getContents()) {
            if (item == null|| item.getType() == Material.AIR) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;
            if (meta.getPersistentDataContainer().has(TycoonData.MENU_ITEM_KEY, PersistentDataType.STRING)) {continue;}

            itemList.add(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + item.getAmount() + "x " + item.getType().name().toLowerCase());
        }
        return itemList;
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


