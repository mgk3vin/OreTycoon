package me.mangokevin.oreTycoon.tycoonManagment;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.events.tycoonEvents.TycoonUpdateEvent;
import me.mangokevin.oreTycoon.menuManager.TycoonInventory;
import me.mangokevin.oreTycoon.events.tycoonEvents.TycoonAutoMinedEvent;
import me.mangokevin.oreTycoon.events.tycoonEvents.TycoonChangedAttributesEvent;
import me.mangokevin.oreTycoon.tycoonManagment.booster.AutoMinerSpeedBooster;
import me.mangokevin.oreTycoon.tycoonManagment.booster.SellMultiplyBooster;
import me.mangokevin.oreTycoon.tycoonManagment.booster.SpawnSpeedBooster;
import me.mangokevin.oreTycoon.tycoonManagment.booster.TycoonBoosterAbstract;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.StoredItemKey;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.SpawnBlock;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.SpawnMaterial;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonRegistry;
import me.mangokevin.oreTycoon.utility.Console;
import me.mangokevin.oreTycoon.levelManagment.LevelManager;
import me.mangokevin.oreTycoon.worth.PriceUtility;
import me.mangokevin.oreTycoon.worth.WorthManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.*;
public class TycoonBlock {



    //<editor-fold desc="🪪Tycoon Variables">

    private final Location tycoonLocation;
    private final World tycoonWorld;
    private final OfflinePlayer owner;
    private final UUID ownerUuid;
    private final Material tycoonMaterial;
    private long creationTime;
    private int index;
    private final String tycoonDisplayName;
    private final String blockUID;
    //</editor-fold>
    private int level;
    private int levelXp;

    private boolean isActive;
    private boolean shouldBeActive;

    private int tickCounter = 0;
    private int miningTickCounter = 0;


    private final TycoonInventory tycoonInventory;
    private boolean autoMinerEnabled;

    private final TycoonType type;

    private String hologramUID;
    Block block;
    public static final Map<Location, String> hologramMap = new HashMap<>();


    //private final Set<Block> activeBlocks = new HashSet<>();
    private final List<SpawnBlock> activeBlocks = new ArrayList<>();

    HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

    //<editor-fold desc="⚙️ Upgrade Variables">
    //⬇️========== Upgrade Attributes ==========⬇️
    private int spawnRate;
    private final int minSpawnRate = 10;
    private int spawnRateLevel;

    private int miningRate;
    private static final int min_mining_rate = 10;
    private int miningRateLevel;

    private double sellMultiplier = 1;
    private int sellMultiplierLevel;
    private double sellMultiplierBuff;
    private double maxSellMultiplier = 5.0;


    private double doubleDropsChance = 0.0;
    private int doubleDropsChanceLevel;
    private int doubleDropsTier;
    private int doubleDropsAmount;
    private int baseDoubleDropsAmount = 0;
    private final double maxDoubleDropsChance = 100.0;

    private double fortuneChance = 1.0;
    private int fortuneChanceLevel;
    private int fortuneTier;
    private int fortuneMultiplier;
    private int baseFortuneMultiplier = 1;
    private final double maxFortuneChance = 100.0;

    private double multiMinerChance = 0;
    private int multiMinerChanceLevel;
    private int multiMinerTier;
    private int multiMinerAmount;
    private int baseMultiMinerAmount = 1;
    private final double maxMultiMinerChance = 100.0;


    private int inventoryStorage;
    private int inventoryStorageLevel;

    private final List<Material> buffMaterials;

    private boolean isAutoMinerUnlocked;

    private final TycoonUpgrades upgrades;
    //⬆️========== Upgrade Attributes ==========⬆️
    //</editor-fold>
    //========== Buff Attributes ==========
    private boolean isBuffed;
    //========== Buff Attributes ==========

    //========== Booster Attributes ==========
    private final TycoonBoosterManager tycoonBooster;
    private SellMultiplyBooster sellMultiplyBooster;
    private AutoMinerSpeedBooster autoMinerSpeedBooster;
    private SpawnSpeedBooster spawnSpeedBooster;
    //========== Booster Attributes ==========

    private boolean isLoaded;   //Set loaded when tycoonManager has succesfully loaded every tycoon
    private final OreTycoon plugin;

    private final LevelManager levelManager;
    private final TycoonRegistry tycoonRegistry;

    //private final Map<Material, Integer> storedItems = new HashMap<>();
    private final Map<StoredItemKey, Integer> storedItems = new HashMap<>();

    //private final Map<Material, Integer> ressourceMaterialsMap;
    private final List<SpawnMaterial> spawnMaterials;
    @Deprecated
    private Map<Material, Boolean> activeRessourceMaterialsMap = new HashMap<>();

    private final Random random = new Random();


    public TycoonBlock(TycoonType type, Location tycoonLocation, UUID ownerUuid, boolean isActive, OreTycoon plugin, TycoonUpgrades upgrades) {
        this.isLoaded = false;

        this.tycoonLocation = tycoonLocation;
        this.tycoonWorld = tycoonLocation.getWorld();
        this.block = tycoonLocation.getBlock();
        this.ownerUuid = ownerUuid;
        this.owner = Bukkit.getOfflinePlayer(ownerUuid);
        this.isActive = isActive;
        this.shouldBeActive = isActive;

        this.plugin = plugin;

        this.levelManager = plugin.getLevelManager();
        this.tycoonRegistry = plugin.getTycoonRegistry();

        this.creationTime = System.currentTimeMillis();
        level = 1;
        levelXp = 0;

        this.autoMinerEnabled = false;

        this.type = type;
        this.tycoonMaterial = type.getMaterial();
        this.spawnRate = type.getSpawnInterval();
        this.miningRate = type.getMiningInterval();
        //this.ressourceMaterialsMap = type.getResources();
        this.spawnMaterials = type.getSpawnMaterials().stream()
                .map(sm -> new SpawnMaterial(sm.getMaterial(), sm.getWeight(), sm.getRarity()))
                .toList();
        this.tycoonDisplayName = type.getName();
        this.inventoryStorage = type.getDefaultMaxInventoryStorage();
        this.buffMaterials = type.getBuffMaterials();


        //========== Get Booster  ==========
        this.tycoonBooster = new TycoonBoosterManager(plugin, this);
        //========== Get Booster  ==========

        //========== Get Upgrade Attributes ==========
        this.upgrades = upgrades;

        this.spawnRateLevel = upgrades.getSpawnRateLevel();
        this.miningRateLevel = upgrades.getMiningRateLevel();
        this.sellMultiplierLevel = upgrades.getSellMultiplierLevel();
        this.doubleDropsChanceLevel = upgrades.getDoubleDropsLevel();
        this.fortuneChanceLevel = upgrades.getFortuneLevel();
        this.inventoryStorageLevel = upgrades.getInventoryStorageLevel();
        this.isBuffed = upgrades.isBuffed();
        this.isAutoMinerUnlocked = upgrades.isAutoMinerUnlocked();
        //========== Get Upgrade Attributes ==========
        //========== Calculate rates matching Level ==========
        updateAttributes();
        //========== Calculate rates matching Level ==========






        this.blockUID = Objects.requireNonNull(this.tycoonLocation.getWorld()).getName() + "_" +
                this.tycoonLocation.getBlockX() + "_" +
                this.tycoonLocation.getBlockY() + "_" +
                this.tycoonLocation.getBlockZ();


        this.tycoonInventory = new TycoonInventory(plugin, this, 0);
        checkIfBuffed();
    }

    //<editor-fold desc="🔎 Increment and Check">
    public void incrementAndCheck() {

        tickCounter++;
        verifyStats();

        if (tycoonWorld.getPlayers().isEmpty()) {
            if (isActive) {
                setActive(false);
                Console.debug(getClass(), "TycoonBlock shut down!");
            }
        } else if (!isActive && shouldBeActive) {
            setActive(true);
        }

        if (tickCounter >= spawnRate) {
            tickCounter = 0;
            if (isActive) {
                trySpawnMultiplyResources(applyDoubleDropsChance());
            }
        }
        // 2. Der Auto-Miner Timer (Erz abbauen)
        // Wir prüfen: Ist der Modus an UND ist der Tycoon aktiv?
        if (autoMinerEnabled) {
            miningTickCounter++;

            if (miningTickCounter >= miningRate) {
                miningTickCounter = 0;
                // Wichtig: Nur versuchen abzubauen, wenn dort auch wirklich ein Block steht!

                if (activeBlocks.isEmpty()) return;

                // Einen zufälligen Block aus dem Set picken
//                SpawnBlock targetBlock = activeBlocks.get(random.nextInt(activeBlocks.size()));
//
//                if (targetBlock != null && targetBlock.getMaterial() != Material.AIR) {
//                    tryAutoMining(targetBlock.getSpawnLocation());
//                }
                tryAutoMiningMultiple(applyMultiMinerChance());
            }
        }
    }
    //</editor-fold>

    public void handleReward(SpawnBlock spawnBlock, int amount) {
        if (spawnBlock != null) {
            levelManager.handleXpGain(this, spawnBlock.getSpawnMaterialRarity().getXpAmount() * amount);
            activeBlocks.remove(spawnBlock);
        }
    }

    public void verifyStats() {
        if (spawnRate <= minSpawnRate) {
            spawnRate = minSpawnRate;
        }
        if (miningRate <= min_mining_rate) {
            miningRate = min_mining_rate;
        }
    }
    //<editor-fold desc="📦 Inventory Methods">
    public double sellTycoonInventory(Player player) {
        WorthManager worthManager = OreTycoon.getInstance().getWorthManager();
        Economy econ = OreTycoon.getEconomy();
        double worth = worthManager.getWorth(getStoredItems());

        System.out.println("TycoonBlock Calculate Worth: " + PriceUtility.formatMoney(worth));
        if (worth <= 0) return 0.0;
        econ.depositPlayer(player, worth);
        player.sendMessage(ChatColor.GREEN + "Sold items worth: " + PriceUtility.formatMoney(worth) + " with " + getSellMultiplierFormatted() + "x Sell Multiplier");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.3f, 1);
        storedItems.clear();
        updateHologram();
        return worth;
    }

    public void dropItem(StoredItemKey key, int amount, Player player) {
        //Remove item from inventory map
        int currentItems = storedItems.getOrDefault(key, 0);
        int newAmount = currentItems - amount;

        if (newAmount <= 0) {
            storedItems.remove(key);
        } else {
            storedItems.put(key, newAmount);
        }

        Location dropLocation = tycoonLocation.clone();
        dropLocation.setY(dropLocation.getY() + 1);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.3f, 1);
        ItemStack freshItem = new ItemStack(key.material(), amount);
        player.getWorld().dropItem(dropLocation, freshItem);
        updateHologram();
    }

    //</editor-fold>

    public void trySpawnMultiplyResources(int amount) {
        if (amount <= 0) return;
        Location center = getLocation();
        World world = center.getWorld();


        // 1. Definiere das 5x5 Areal (vom Zentrum aus -2 bis +2)
        int minX = center.getBlockX() - 2;
        int maxX = center.getBlockX() + 2;
        int minZ = center.getBlockZ() - 2;
        int maxZ = center.getBlockZ() + 2;
        int fixedY = center.getBlockY(); // Wir spawnen nur auf der Y-Ebene des Tycoon-Block

        for (int i = 0; i < amount; i++) {
            //Find random spawn coordinate
            int randomX = random.nextInt(maxX - minX + 1) + minX;
            int randomZ = random.nextInt(maxZ - minZ + 1) + minZ;

            //Save Spawn Location
            Location randomLocation = new Location(center.getWorld(), randomX, fixedY, randomZ);
            Block spawnLocationBlock = randomLocation.getBlock();

            //Check Spawn Location validity
            if (spawnLocationBlock.getType().equals(Material.AIR)) {
                //Valid Spawn point
                SpawnMaterial spawnMaterial = getRandomMaterial(spawnMaterials);
                if (spawnMaterial == null) return;

                //Create SpawnBlock object
                SpawnBlock spawnBlock = new SpawnBlock(spawnMaterial, randomLocation);

                spawnLocationBlock.setType(spawnMaterial.getMaterial());

                activeBlocks.add(spawnBlock);

                spawnLocationBlock.setMetadata("tycoon_id", new FixedMetadataValue(plugin, blockUID));

                updateHologram();
                assert world != null;
                world.playSound(randomLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.5f);
                world.spawnParticle(Particle.EXPLOSION, randomLocation, 1);
            }
        }


    }
    //---------- AutoMiner ----------
    private void tryAutoMining() {
        if (!isAutoMinerUnlocked){
            return;
        }
        SpawnBlock targetBlock = activeBlocks.get(random.nextInt(activeBlocks.size()));
        Location tagetLocation = targetBlock.getSpawnLocation();

        ItemStack item = new ItemStack(targetBlock.getBlock().getType());
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return;
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        pdc.set(TycoonData.BLOCK_IS_AUTOMINED_KEY, PersistentDataType.STRING, "block_is_automined");

        if (isStorageFull()){
            return;
        }

        TycoonBlock tycoon = this;

        new BukkitRunnable() {

            float progress = 0.0f;

            @Override
            public void run() {
                // 1. Punkte zentrieren (damit sie aus der Mitte der Blöcke kommen)
                Location start = getLocation().clone().add(0.5, 0.5, 0.5);
                Location target = tagetLocation.clone().add(0.5, 0.5, 0.5);

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
                    Objects.requireNonNull(start.getWorld()).playSound(start, Sound.BLOCK_NOTE_BLOCK_HAT, 0.2f, 0.5f + progress);
                }
                progress += 0.1f;

                if (progress >= 1.0f) {
//                    for (SpawnBlock spawnBlock : activeBlocks) {
//                        if (spawnBlock.getSpawnLocation().equals(blockLocation)) {
//                            targetSpawnBlock = spawnBlock;
//                            break;
//                        }
//                    }
                    Objects.requireNonNull(tagetLocation.getWorld()).playSound(tagetLocation, Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
                    tagetLocation.getWorld().spawnParticle(Particle.BLOCK, tagetLocation, 15, 0.2, 0.2, 0.2, block.getBlockData());

//                    if (targetBlock == null){
//                        Console.error(getClass(), "Target block is null!");
//                        return;
//                    }

                    StoredItemKey key = new StoredItemKey(targetBlock.getMaterial(), targetBlock.getSpawnMaterialRarity());

                    TycoonAutoMinedEvent event = new TycoonAutoMinedEvent(tycoon, targetBlock, key);
                    Bukkit.getPluginManager().callEvent(event);

                    block.removeMetadata("tycoon_id", plugin);

                    tagetLocation.getBlock().setType(Material.AIR);
                    pdc.remove(TycoonData.BLOCK_IS_AUTOMINED_KEY);

                    activeBlocks.remove(targetBlock);

                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1L);
    }
    private void tryAutoMiningMultiple(int amout) {
        for (int i = 0; i < amout; i++) {
            tryAutoMining();
        }
    }
    //---------- AutoMiner ----------
    public void updateAttributes() {
        sellMultiplierLevel = upgrades.getSellMultiplierLevel();
        sellMultiplierBuff = upgrades.getSellMultiplierBuff();

        spawnRateLevel = upgrades.getSpawnRateLevel();
        spawnRate = TycoonUpgrades.calculateNewSpawnRate(spawnRateLevel, type.getSpawnInterval());

        if (spawnSpeedBooster != null){
            spawnRate = Math.max(minSpawnRate, spawnRate  - (int) spawnSpeedBooster.getBoostValue());
        }

        miningRateLevel = upgrades.getMiningRateLevel();
        miningRate = TycoonUpgrades.calculateNewMiningRate(miningRateLevel, type.getMiningInterval());
        if (autoMinerSpeedBooster != null) {
            miningRate = Math.max(min_mining_rate ,miningRate - (int) autoMinerSpeedBooster.getBoostValue());
        }


        sellMultiplier = TycoonUpgrades.calculateNewSellMultiplier(sellMultiplierLevel, type.getSellMultiplier());
        if (sellMultiplyBooster != null) {
            sellMultiplier += sellMultiplyBooster.getBoostValue();
        }
        sellMultiplier *= sellMultiplierBuff;



        //---------- Double Drops update ----------
        doubleDropsChanceLevel = upgrades.getDoubleDropsLevel();
        double rawDoubleDropsChance = TycoonUpgrades.calculateNewDoubleDropChance(doubleDropsChanceLevel, 0.0);

        doubleDropsTier = (int) ((rawDoubleDropsChance - 1) / maxDoubleDropsChance);
        doubleDropsAmount = 2 + doubleDropsTier;
        baseDoubleDropsAmount = doubleDropsAmount - 1;

        //effective chance:
        doubleDropsChance = rawDoubleDropsChance % maxDoubleDropsChance;

        if (doubleDropsChance == 0 && rawDoubleDropsChance > 0) {
            doubleDropsChance = maxDoubleDropsChance;
        }
        //---------- Double Drops update ----------

        //---------- Fortune update ----------
        fortuneChanceLevel = upgrades.getFortuneLevel();
        double rawFortuneChance = TycoonUpgrades.calculateNewFortuneChance(fortuneChanceLevel, 0);

        fortuneTier = (int) ((rawFortuneChance - 1)/ maxFortuneChance);
        fortuneMultiplier = 2 + fortuneTier;
        baseFortuneMultiplier = fortuneMultiplier - 1;

        //effective chance:
        fortuneChance = rawFortuneChance % maxFortuneChance;

        if (fortuneChance == 0 && rawFortuneChance > 0) {
            fortuneChance = maxFortuneChance;
        }
        //---------- Fortune update ----------

        //---------- Multi Miner update ----------
        multiMinerChanceLevel = upgrades.getMultipleMinerLevel();
        double rawMultiMinerChance = TycoonUpgrades.calculateMultipleMinerChance(multiMinerChanceLevel, 0);

        multiMinerTier = (int) ((rawMultiMinerChance - 1) / maxMultiMinerChance);
        multiMinerAmount = 2 + multiMinerTier;
        baseMultiMinerAmount = multiMinerAmount - 1;

        multiMinerChance = rawMultiMinerChance % maxMultiMinerChance;

        if (multiMinerChance == 0 && rawMultiMinerChance > 0) {
            multiMinerChance = maxMultiMinerChance;
        }
        //---------- Multi Miner update ----------



        inventoryStorageLevel = upgrades.getInventoryStorageLevel();
        inventoryStorage = TycoonUpgrades.getMaxInventoryStorage(inventoryStorageLevel, type.getDefaultMaxInventoryStorage());

        isAutoMinerUnlocked = upgrades.isAutoMinerUnlocked();

        TycoonChangedAttributesEvent event = new TycoonChangedAttributesEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        callTycoonUpdateEvent();
    }
    //========== Upgrade Methods ==========
    //<editor-fold desc="🔧 Upgrade Methods">
    public void upgradeSpawnRate(Player player) {
        upgradeSpawnRate(player, false);
    }
    public void upgradeSpawnRate(Player player, boolean force) {
        if (spawnRate <= minSpawnRate) {
            giveMaxLevelMSG(player);
            return;
        }
        int nextLevel = spawnRateLevel + 1;
        if (force) {
            upgrades.setSpawnRateLevel(nextLevel);
            updateAttributes();
        } else {
            double cost = TycoonUpgrades.getSpawnRateUpgradeCost(this, nextLevel);
            handleUpgrade(player, cost, () -> {
                upgrades.setSpawnRateLevel(nextLevel);
            });
            player.sendMessage(ChatColor.GREEN + "You upgraded the Spawn rate to " + getSpawnRateFormatted() + "s for: " + PriceUtility.formatMoney(cost));
        }
    }
    public boolean isSpawnRateMaxed() {
        return spawnRate <= minSpawnRate;
    }

    public void upgradeMiningRate(Player player) {
        upgradeMiningRate(player, false);
    }
    public void upgradeMiningRate(Player player, boolean force) {
        int nextLevel = miningRateLevel + 1;

        if (miningRate <= min_mining_rate) {
            giveMaxLevelMSG(player);
            return;
        }
        if (miningRate <= spawnRate) {
            player.sendMessage(ChatColor.RED + "Mining rate level " + miningRateLevel + " can't be higher than spawn rate level: " + spawnRateLevel);
            return;
        }

        if (force) {
            upgrades.setMiningRateLevel(nextLevel);
            updateAttributes();
        } else {
            double cost = TycoonUpgrades.getMiningRateUpgradeCost(this,nextLevel);
            handleUpgrade(player, cost, () -> {
                upgrades.setMiningRateLevel(nextLevel);
            });
            player.sendMessage(ChatColor.GREEN + "You upgraded the Mining rate to " + getMiningRateFormatted() + "s for: " + PriceUtility.formatMoney(cost));
        }
    }
    public boolean isMiningRateMaxed(){
        return  miningRate <= spawnRate || miningRate <= min_mining_rate;
    }

    public void upgradeMaxInventoryStorage(Player player) {
        upgradeMaxInventoryStorage(player, false);
    }
    public void upgradeMaxInventoryStorage(Player player, boolean force) {
        int nextLevel = inventoryStorageLevel + 1;
        if (force) {
            upgrades.setInventoryStorageLevel(nextLevel);
            updateAttributes();
        } else {
            double cost = TycoonUpgrades.getInventoryStorageUpgradeCost(this, nextLevel);
            handleUpgrade(player, cost, () -> {
                upgrades.setInventoryStorageLevel(nextLevel);
            });
            player.sendMessage(ChatColor.GREEN + "You upgraded the Inventory Storage to " + getStorageStatisticFormatted() + ChatColor.GREEN + " for: " + PriceUtility.formatMoney(cost));

        }
    }

    public void upgradeSellMultiplier(Player player) {
        upgradeSellMultiplier(player, false);
    }
    public void upgradeSellMultiplier(Player player, boolean force) {
        if (sellMultiplier >= maxSellMultiplier) {
            giveMaxLevelMSG(player);
            return;
        }
        int nextLevel = upgrades.getSellMultiplierLevel() + 1;
        if (force) {
            upgrades.setSellMultiplierLevel(nextLevel);
            updateAttributes();
        } else {
            double cost = TycoonUpgrades.getSellMultiplierUpgradeCost(this, nextLevel);
            handleUpgrade(player, cost, () -> {
                upgrades.setSellMultiplierLevel(nextLevel);
            });
            player.sendMessage(ChatColor.GREEN + "You upgraded the Sell Multiplier to " + getSellMultiplier() + "x for: " + PriceUtility.formatMoney(cost));
        }
    }
    public boolean isSellMultiplierMaxed() {
        return sellMultiplier >= maxSellMultiplier;
    }

    public void upgradeDoubleDropsChance(Player player) {
        upgradeDoubleDropsChance(player, false);
    }
    public void upgradeDoubleDropsChance(Player player, boolean force) {
        double rawDoubleDropsChance = TycoonUpgrades.calculateNewDoubleDropChance(doubleDropsChanceLevel, 0);

        if (rawDoubleDropsChance >= 500) {
            player.sendMessage(ChatColor.RED + "Max Level Reached!");
            return;
        }
        int nextLevel = doubleDropsChanceLevel + 1;
        if (force) {
            upgrades.setDoubleDropsLevel(nextLevel);
            updateAttributes();
        } else {
            double cost = TycoonUpgrades.getDoubleDropChanceUpgradeCost(this,nextLevel);
            handleUpgrade(player, cost, () -> {
                upgrades.setDoubleDropsLevel(nextLevel);
            });
            player.sendMessage(ChatColor.GREEN + "You upgrade Double Drops Chance to " + getDoubleDropsChanceFormatted() + " for: " + PriceUtility.formatMoney(cost));
        }
    }
    public boolean isDoubleDropsChanceMaxed() {
        return doubleDropsChance >= maxDoubleDropsChance;
    }

    public void upgradeFortuneChance(Player player) {
        upgradeFortuneChance(player, false);
    }
    public void upgradeFortuneChance(Player player, boolean force) {
        double rawFortuneChance = TycoonUpgrades.calculateNewFortuneChance(fortuneChanceLevel, 0);

        if (rawFortuneChance >= 500.0) {
            giveMaxLevelMSG(player);
            return;
        }
        int nextLevel = upgrades.getFortuneLevel() + 1;
        if (force) {
            upgrades.setFortuneLevel(nextLevel);
            updateAttributes();
        } else {
            double cost = TycoonUpgrades.getFortuneUpgradeCost(this, nextLevel);
            handleUpgrade(player, cost, () -> {
                upgrades.setFortuneLevel(nextLevel);
            });
            player.sendMessage(ChatColor.GREEN + "You upgraded Fortune Chance to " + getFortuneChanceFormatted() + " for: " + PriceUtility.formatMoney(cost));
        }
    }

    public void upgradeMultiMinerChance(Player player, boolean force) {
        double rawMultiMinerChance = TycoonUpgrades.calculateMultipleMinerChance(multiMinerChanceLevel, 0);
        if (rawMultiMinerChance >= 500.0) {
            giveMaxLevelMSG(player);
            return;
        }

        int nextLevel = upgrades.getMultipleMinerLevel() + 1;
        if (force) {
            upgrades.setMultipleMinerLevel(nextLevel);
            updateAttributes();
        } else {
            double cost = TycoonUpgrades.getMultipleMinerUpgradeCost(this, nextLevel);
            handleUpgrade(player, cost, () -> {
                upgrades.setMultipleMinerLevel(nextLevel);
            });
            player.sendMessage(ChatColor.GREEN + "You upgraded Multi Miner Chance to " + getMultiMinerChanceFormatted() + " for: " + PriceUtility.formatMoney(cost));
        }
    }
    public int applyMultiMinerChance() {
        if (random.nextDouble() * 100.0 < multiMinerChance) {
            return multiMinerAmount;
        } else {
            return baseMultiMinerAmount;
        }
    }
    public int applyDoubleDropsChance() {
        if (random.nextDouble() * 100.0 < doubleDropsChance) {
            return doubleDropsAmount;
        } else {
            return baseDoubleDropsAmount;
        }
    }
    public int applyFortune(){
        if (random.nextDouble() * 100.0 < getFortuneChance()) {
            return fortuneMultiplier;
        } else {
            return baseFortuneMultiplier;
        }
    }
    public boolean isFortuneChanceMaxed() {
        return TycoonUpgrades.calculateNewFortuneChance(fortuneChanceLevel, 0) >= 500.0;
    }

    private void handleUpgrade(Player player, double cost, Runnable onSuccess) {
        Economy economy = OreTycoon.getEconomy();

        if (economy.has(player, cost)) {
            economy.withdrawPlayer(player, cost);

            onSuccess.run();

            updateAttributes();
        }else {
            player.sendMessage(ChatColor.RED + "Not enough money!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
    }
    private void giveMaxLevelMSG(Player player) {
        player.sendMessage(ChatColor.RED + "Max Level Reached!");
    }
    //</editor-fold>
    //========== Upgrade Methods ==========

    //========= Buff methods =========
    //<editor-fold desc="🔥 Buff Methods">
    public void checkIfBuffed() {
        Location checkLocation = tycoonLocation.clone();
        checkLocation.add(0, -1, 0);
        if (buffMaterials.contains(checkLocation.getBlock().getType())) {
            isBuffed = true;
            activateSellMultiplierBuff();
        }else  {
            isBuffed = false;
            deactivateSellMultiplierBuff();
        }
        updateHologram();
    }
    public void activateSellMultiplierBuff() {
        upgrades.setSellMultiplierBuff(1.5);
        isBuffed = true;
        updateAttributes();
    }
    public void deactivateSellMultiplierBuff() {
        upgrades.setSellMultiplierBuff(1.0);
        isBuffed = false;
        updateAttributes();
    }
    //</editor-fold>
    //========= Buff methods =========

//    public boolean isObstructed(Player player) {
//        World world = location.getWorld();
//        int centerX = location.getBlockX();
//        int centerZ = location.getBlockZ();
//        int centerY = location.getBlockY();
//
//        for (int x = centerX - 4; x <=  centerX + 4; x++) {
//            for (int z = centerZ - 4; z <=  centerZ + 4; z++) {
//                for (int y = centerY -2; y <=  centerY + 2; y++) {
//                    Location checkLocation = new Location(world, x, y, z);
//                    if (tycoonRegistry.isTycoonBlock(checkLocation)){
//                        player.sendMessage(ChatColor.RED + "Tycoon blocks must be placed at least 5 blocks away from each other!");
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
    private SpawnMaterial getRandomMaterial(List<SpawnMaterial> spawnMaterials) {
        //Only from active Materials
        int totalActiveWeight = 0;
        for (SpawnMaterial spawnMaterial : spawnMaterials) {
            //Only when the Material is active it gets added to the total Weight
            if (spawnMaterial.isActive()) {
                totalActiveWeight += spawnMaterial.getWeight();
            }
        }

        //Safety Check if every Item is disabled
        if (totalActiveWeight <= 0) {
            return null;
        }

        int randomValue = random.nextInt(totalActiveWeight);
        int currentSum = 0;

        for (SpawnMaterial spawnMaterial : spawnMaterials) {
            //Only check for active Ressources
            if (spawnMaterial.isActive()) {
                currentSum += spawnMaterial.getWeight();
                if (randomValue < currentSum) {
                    Material result = spawnMaterial.getMaterial();

                    if (!result.isBlock()) {
                        Console.error(getClass(), result.name() + " is not a block! Remove it from TycoonType resources.");
                        return null;
                    }
                    return spawnMaterial;
                }
            }
        }
        return null;
    }

    public double getAverageWorth() {
        //Map<Material, Integer> resources = type.getResources();
        List<SpawnMaterial> spawnMaterials = getSpawnMaterials();

        if (spawnMaterials == null || spawnMaterials.isEmpty()) return 0.0;

        double totalWeight = 0;
        for (SpawnMaterial spawnMaterial : spawnMaterials) {
            totalWeight += spawnMaterial.getWeight();
        }
        double averageWorth = 0;
        for (SpawnMaterial spawnMaterial : spawnMaterials) {
            Material mat = spawnMaterial.getMaterial();
            int weight = spawnMaterial.getWeight();

            // Hier holst du den Preis pro Stück aus deiner Preis-Liste
            ItemStack item = new ItemStack(mat);
            double price = PriceUtility.calculateWorth(item);

            // Anteil am Gesamtwert berechnen
            averageWorth += price * (weight / totalWeight);
        }
        return averageWorth;
    }

    public int getTotalActiveWeight(){
        //Map<Material, Integer> OriginalResources = type.getResources();
        List<SpawnMaterial> spawnMaterials = getSpawnMaterials();

        int totalWeight = 0;
        for (SpawnMaterial spawnMaterial : spawnMaterials) {
            if (spawnMaterial.isActive()) {
                totalWeight += spawnMaterial.getWeight();
            }
        }
        return totalWeight;
    }

    public boolean containsBlock(Block block) {
        return activeBlocks.stream()
                .anyMatch(sb -> sb.getSpawnLocation().getBlockX() == block.getX()
                        && sb.getSpawnLocation().getBlockY() == block.getY()
                        && sb.getSpawnLocation().getBlockZ() == block.getZ());
    }
    public void removeBlock(Block block) {
        activeBlocks.removeIf(sb -> sb.getSpawnLocation().getBlockX() == block.getX()
        && sb.getSpawnLocation().getBlockY() == block.getY()
        && sb.getSpawnLocation().getBlockZ() == block.getZ());
    }

    public void teleportPlayer(Player player) {
        Location teleportLoc = getLocation();
        boolean isSave = false;
        for (int i = 0; i < 2; i++) {
            isSave = getLocation().clone().add(0, i + 1, 0).getBlock().getType().equals(Material.AIR);
            if (!isSave) {
                player.sendMessage(ChatColor.RED + "Teleport destination is obstructed!");
                break;
            }
        }
        if (isSave) {
            player.teleport(teleportLoc.clone().add(0.5, 1, 0.5));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_TELEPORT, 1.0f, 1);
        }
    }

    // ---------     SpawnedBlockHologram      ---------
    @Deprecated
    public void displayXpHologram(Block brokenBlock, int xp) {
        Location l = new Location(brokenBlock.getWorld(), brokenBlock.getX()+0.5, brokenBlock.getY()+1.5, brokenBlock.getZ()+0.5);
        World world = brokenBlock.getWorld();

        hologramUID = "SpawnedBlockHolo_" + l.getBlockX() + "_" + l.getBlockY() + "_" + l.getBlockZ() + "_" + getOwnerName();

        TextHologramData hologramData = new TextHologramData(hologramUID, l);

        List<String> name = new LinkedList<>();
        name.add(ChatColor.GREEN + "+" + xp);
        hologramData.setText(name);
        hologramData.setBackground(Color.fromARGB(0));
        hologramData.setPersistent(false);
        Hologram hologram = manager.create(hologramData);
        manager.addHologram(hologram);

        hologramMap.put(brokenBlock.getLocation(), hologramUID);
        hologram.queueUpdate();

        world.playSound(l, Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
    }
    public void removeXpHologram(Block block) {
        Hologram hologram = getHologram(block.getLocation());

        if (hologram != null) {
            manager.removeHologram(hologram);
            hologram.queueUpdate();
        }
    }
    // ---------     SpawnedBlockHologram      ---------


    // ---------     TycoonHologram      ---------
    public void createHologram() {
        Location l = new Location(block.getWorld(), block.getX()+0.5, block.getY()+1.5, block.getZ()+0.5);

        hologramUID = "TycoonBlockHolo_" + l.getBlockX() + "_" + l.getBlockY() + "_" + l.getBlockZ() + "_" + getOwnerName();

        TextHologramData hologramData = new TextHologramData(hologramUID, l);

        List<TycoonBlock> tycoonBlockList = tycoonRegistry.getAllTycoonsFromPlayer(ownerUuid);

        //Calculate index/Order
        if (tycoonBlockList.contains(this)) {
            index = tycoonBlockList.indexOf(this) + 1;  // 1 based
        } else {
            index = -1;
            Console.error(getClass() ,"Error while loading tycoon index! Tycoon is not registered!");
        }


        List<String> name = new LinkedList<>();
        name.add("[ " + getOwnerName() + "'s Tycoon #" + index + " ]");
        hologramData.setText(name);

        hologramData.addLine(tycoonDisplayName + ChatColor.RESET);

        hologramData.addLine("Status: " + isActiveFormatted() + ChatColor.WHITE + " | " + isBuffedFormatted() + ChatColor.RESET);

        hologramData.addLine("Level: " + level);
        hologramData.addLine("xp: " + levelXp + "/" + levelManager.getXpNeededForLevel(level + 1) + " | " + (int) levelManager.getProgressPercentage(levelXp, level + 1) + "%");
        hologramData.addLine(ChatColor.DARK_GRAY + "[" +getProgressBar(20) + ChatColor.DARK_GRAY + "]");
        double currentWorth = PriceUtility.calculateWorth(getStoredItems());
        hologramData.addLine(ChatColor.RESET + "Inventory: "+ ChatColor.GREEN + PriceUtility.formatMoney(currentWorth) + ChatColor.WHITE + " | " + getStorageStatisticFormatted());
        hologramData.setBackground(Color.fromARGB(60, 80, 80, 80));
        hologramData.setPersistent(false);
        hologramData.setTextShadow(true);
        hologramData.setSeeThrough(false);
        Hologram hologram = manager.create(hologramData);
        manager.addHologram(hologram);


        hologram.queueUpdate();
        hologramMap.put(tycoonLocation, hologramUID);
    }
    public void removeHologram(Location location) {
            if (getHologram(location) != null) {
                manager.removeHologram(getHologram(location));
                hologramMap.remove(location);
            }
    }
    public void removeHologram() {
        if (getHologram(tycoonLocation) != null) {
            manager.removeHologram(getHologram(tycoonLocation));
            hologramMap.remove(tycoonLocation);
        }
    }
    public void queueHologramUpdate(Location location) {
        Hologram hologram = getHologram(location);
        if (hologram != null) {
            hologram.queueUpdate();
        }
    }
    public void queueHologramUpdate() {
        queueHologramUpdate(tycoonLocation);
    }
    public void updateHologram() {
        if (!isLoaded()) {
            Console.error(getClass() + " Cant update hologram, Tycoon is not loaded!");
            return;
        }
        Hologram hologram = getHologram(tycoonLocation);
        if (hologram == null) {
            Console.error(getClass() ,"No Hologram found at Location " + tycoonLocation);
            return;
        }
        HologramData data = hologram.getData();
        List<String> hologramLines = ((TextHologramData) data).getText();


        List<TycoonBlock> tycoonBlockList = plugin.getTycoonRegistry().getAllTycoons();

        hologramLines.set(1, tycoonDisplayName + ChatColor.RESET);
        hologramLines.set(2, "Status: " + isActiveFormatted() + ChatColor.WHITE + " | " + isBuffedFormatted() + ChatColor.RESET);
        hologramLines.set(3, "Level: " + level);
        hologramLines.set(4, "xp: " + levelXp + "/" + levelManager.getXpNeededForLevel(level + 1) + " | " + (int) levelManager.getProgressPercentage(levelXp, level + 1) + "%");
        hologramLines.set(5, ChatColor.DARK_GRAY + "[" +getProgressBar(20) + ChatColor.DARK_GRAY + "]");
        String currentWorthFormatted = PriceUtility.formatMoney(PriceUtility.calculateWorth(getStoredItems()));
        if (isInventoryFull()){
            hologramLines.set(6, ChatColor.RED + "" + ChatColor.BOLD + "Inventory FULL: "+ ChatColor.GREEN + currentWorthFormatted + ChatColor.WHITE + " | " + getStorageStatisticFormatted());
        } else {
            hologramLines.set(6, ChatColor.RESET + "Inventory: "+ ChatColor.GREEN + currentWorthFormatted + ChatColor.WHITE + " | " + getStorageStatisticFormatted());

        }

        //Calculate index/Order
        if (tycoonBlockList.contains(this)) {
            index = tycoonBlockList.indexOf(this) + 1;  // 1 based
        } else {
            index = -1;
            Console.error(getClass() ,"Error while loading tycoon index! Tycoon is not registered!");
        }

        hologramLines.set(0, "[ " + getOwnerName() + "'s Tycoon #" + index + " ]");

        queueHologramUpdate();
    }
    public String isBuffedFormatted(){
        if(isBuffed){
            return ChatColor.GREEN + "Buff active";
        }else {
            return ChatColor.RED + "Buff inactive";
        }
    }

    public String getProgressBar(int bars){
        double percent = (levelManager.getProgressPercentage(levelXp, level + 1)/100);
        int progressBars = (int) (bars * percent);
        int leftOverBars = (bars - progressBars);

        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GREEN);
        for (int i = 0; i < progressBars; i++) {
            sb.append("|");
        }
        sb.append(ChatColor.GRAY);
        for (int i = 0; i < leftOverBars; i++) {
            sb.append("|");
        }
        return sb.toString();
    }

    public Hologram getHologram(Location location) {
        if (hologramMap.containsKey(location)) {
            String uniqueId = hologramMap.get(location);

            Hologram hologram = manager.getHologram(uniqueId).orElse(null);
            if (hologram != null) {
                return hologram;
            }else{
                System.out.println("Hologram with id " + uniqueId + " not found");
                return null;
            }
        }
        return null;
    }
    // ---------     TycoonHologram      ---------

    public TycoonBoosterAbstract isAnyBoosterActive(){
        if(sellMultiplyBooster != null ){
            return sellMultiplyBooster;
        } else if (autoMinerSpeedBooster != null) {
            return autoMinerSpeedBooster;
        } else if (spawnSpeedBooster != null) {
            return spawnSpeedBooster;
        }
        return null;
    }
    public void callTycoonUpdateEvent(){
        Bukkit.getPluginManager().callEvent(new TycoonUpdateEvent(this));
    }


    // ---------     Adder      ---------
    public void addActiveBlocks(SpawnBlock spawnBlock) {
        activeBlocks.add(spawnBlock);
    }
    public String getStorageStatisticFormatted(){
        int storedItems = getStoredItemsAmount();
        double storagePercentage = (double)storedItems / (double)inventoryStorage;
        storagePercentage = Math.round(storagePercentage * 100.0);
        if (storagePercentage < 30.0) {
            return ChatColor.GRAY + "["  + ChatColor.GREEN + storedItems + ChatColor.GRAY + "/" + ChatColor.RED + inventoryStorage + ChatColor.GRAY + "]";
        } else if (storagePercentage < 50.0) {
            return ChatColor.GRAY + "["  + ChatColor.DARK_GREEN + storedItems + ChatColor.GRAY + "/" + ChatColor.RED + inventoryStorage + ChatColor.GRAY + "]";
        } else if (storagePercentage < 70) {
            return ChatColor.GRAY + "["  + ChatColor.YELLOW + storedItems + ChatColor.GRAY + "/" + ChatColor.RED + inventoryStorage + ChatColor.GRAY + "]";
        } else if (storagePercentage < 90.0) {
            return ChatColor.GRAY + "["  + ChatColor.GOLD + storedItems + ChatColor.GRAY + "/" + ChatColor.RED + inventoryStorage + ChatColor.GRAY + "]";
        } else if (storagePercentage <= 100.0) {
            return ChatColor.GRAY + "["  + ChatColor.RED + storedItems + ChatColor.GRAY + "/" + ChatColor.RED + inventoryStorage + ChatColor.GRAY + "]";
        }
        return storagePercentage + "%";
    }

    public int getStoredItemsAmount(){
        int storedItemsCount = 0;
        for (StoredItemKey storedItem : storedItems.keySet()){
            storedItemsCount += storedItems.get(storedItem);
        }
        return storedItemsCount;
    }
    public boolean addItem(StoredItemKey item, int amount){
        if (isStorageFull()) return false;
        storedItems.merge(item, amount, Integer::sum);
        return true;
    }

    // ---------     Adder      ---------
    // ---------     Getter      ---------
    public int getIndex() {
        return index;
    }
    public int getLevelXp(){
        return levelXp;
    }
    public String getBlockUID(){
        return blockUID;
    }
    public int getLevel() {
        return level;
    }
    public int getProgressPercentage() {
        return (int) levelManager.getProgressPercentage(levelXp, level + 1);
    }
    public Location getLocation() {
        return tycoonLocation;
    }
    public TycoonType getTycoonType() {
        return type;
    }
    public OfflinePlayer getOfflineOwner() {
        return owner;
    }
    public String getOwnerName() {
        return owner.getName();
    }
    public UUID getOwnerUuid() {
        return ownerUuid;
    }
    public  boolean isActive() {
        return isActive;
    }
    public boolean isActiveByPlayer(){
        return shouldBeActive;
    }
    public String isActiveFormatted(){
        if (isActive) {
            return ChatColor.GREEN + "spawning..." + ChatColor.RESET;
        }else {
            return ChatColor.RED + "offline..." + ChatColor.RESET;
        }
    }
    public int getSpawnRate() {
        return spawnRate;
    }
    public String getHologramUID(){
        return hologramUID;
    }
    public List<SpawnBlock> getActiveBlocks(){
        return activeBlocks;
    }
    public Material getTycoonMaterial() {
        return tycoonMaterial;
    }
    public long getCreationTime() {
        return creationTime;
    }
    public boolean isAutoMinerEnabled() {
        return autoMinerEnabled;
    }
    public TycoonInventory getTycoonInventory() {
        return tycoonInventory;
    }
    public int getSpawnRateLevel() {
        return spawnRateLevel;
    }
    public int getMiningRateLevel() {
        return miningRateLevel;
    }
    public double getSellMultiplier() {
        return sellMultiplier;
    }
    public String getSellMultiplierFormatted() {
        return String.format("%.2f", sellMultiplier);
    }
    public int getSellMultiplierLevel() {
        return sellMultiplierLevel;
    }
    public String getDoubleDropsChanceFormatted(){
        return String.format("%.2f", doubleDropsChance) + "%";
    }
    public String getFortuneChanceFormatted(){
        return String.format("%.2f", fortuneChance) + "%";
    }
    public double getFortuneChance() {
        return fortuneChance;
    }
    public String getMultiMinerChanceFormatted(){
        return String.format("%.2f", multiMinerChance) + "%";
    }
    public double getMultiMinerChance() {
        return multiMinerChance;
    }
    public TycoonUpgrades getTycoonUpgrades() {
        return upgrades;
    }
    public double getSpawnRateFormatted(){
        return (double) spawnRate /20;
    }
    public double getMiningRateFormatted(){
        return (double) miningRate /20;
    }
    @Deprecated
    public Map<Material, Boolean> getActiveRessourceMaterialsMap(){
        return activeRessourceMaterialsMap;
    }
    public int getInventoryStorage(){
        return inventoryStorage;
    }
    public TycoonBoosterManager getTycoonBoosterManager() {
        return tycoonBooster;
    }
    public SellMultiplyBooster getSellMultiplierBooster() {
        return sellMultiplyBooster;
    }
    public AutoMinerSpeedBooster getAutoMinerSpeedBooster() {return autoMinerSpeedBooster;}
    public SpawnSpeedBooster getSpawnSpeedBooster() {return spawnSpeedBooster;}
    public boolean isLoaded() {
        return isLoaded;
    }
    public boolean isInventoryFull(){
        return isStorageFull();
    }
    public Map<StoredItemKey, Integer> getStoredItems(){
        return storedItems;
    }
    public boolean isStorageFull(){
        return getStoredItemsAmount() >= inventoryStorage;
    }
    public List<SpawnMaterial> getSpawnMaterials() {
        return spawnMaterials;
    }
    public SpawnBlock getSpawnBlockFromBlock(Block block) {
        for (SpawnBlock spawnBlock : activeBlocks) {
            if (spawnBlock.getSpawnLocation().equals(block.getLocation())) return spawnBlock;
        }
        return null;
    }
    public int getFortuneTier() {
        return fortuneTier;
    }
    public int getFortuneMultiplier() {
        return fortuneMultiplier;
    }
    public int getBaseFortuneMultiplier() {
        return baseFortuneMultiplier;
    }
    public double getDoubleDropsChance() {
        return doubleDropsChance;
    }
    public int getDoubleDropsTier() {
        return doubleDropsTier;
    }
    public int getDoubleDropsAmount() {
        return doubleDropsAmount;
    }
    public int getMultiMinerTier() {
        return multiMinerTier;
    }
    public int getMultiMinerAmount() {
        return multiMinerAmount;
    }
    // ---------     Getter      ---------

    // ---------     Setter      ---------
    public void setIndex(int index) {
        this.index = index;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public void setLevelXp(int levelXp) {
        this.levelXp = levelXp;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
        updateHologram();
    }
    public void setActiveByPlayer(boolean activeByPlayer) {
        this.shouldBeActive = activeByPlayer;
        setActive(activeByPlayer);
    }
    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
    public void setAutoMinerEnabled(boolean autoMinerEnabled) {
        if (isAutoMinerUnlocked){
            this.autoMinerEnabled = autoMinerEnabled;
        }
    }
    @Deprecated
    public void setActiveResourceMaterialsMap(Map<Material, Boolean> activeRessourceMaterialsMap) {
        this.activeRessourceMaterialsMap = activeRessourceMaterialsMap;
    }
    public void setSellMultiplierBooster(SellMultiplyBooster sellMultiplierBooster) {
        this.sellMultiplyBooster = sellMultiplierBooster;
    }
    public void setAutoMinerSpeedBooster(AutoMinerSpeedBooster autoMinerSpeedBooster) {
        this.autoMinerSpeedBooster = autoMinerSpeedBooster;
    }
    public void setSpawnSpeedBooster(SpawnSpeedBooster spawnSpeedBooster) {
        this.spawnSpeedBooster = spawnSpeedBooster;
    }
    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }
    // ---------     Setter      ---------
}
