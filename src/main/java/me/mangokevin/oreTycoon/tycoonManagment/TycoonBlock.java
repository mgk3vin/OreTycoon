package me.mangokevin.oreTycoon.tycoonManagment;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.menuManager.TycoonInventory;
import me.mangokevin.oreTycoon.events.tycoonEvents.TycoonAutoMinedEvent;
import me.mangokevin.oreTycoon.events.tycoonEvents.TycoonChangedAttributesEvent;
import me.mangokevin.oreTycoon.tycoonManagment.booster.AutoMinerSpeedBooster;
import me.mangokevin.oreTycoon.tycoonManagment.booster.SellMultiplyBooster;
import me.mangokevin.oreTycoon.tycoonManagment.booster.SpawnSpeedBooster;
import me.mangokevin.oreTycoon.tycoonManagment.booster.TycoonBoosterAbstract;
import me.mangokevin.oreTycoon.utility.Console;
import me.mangokevin.oreTycoon.levelManagment.LevelManager;
import me.mangokevin.oreTycoon.worth.PriceUtility;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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

    private final Location location;
    private final OfflinePlayer owner;
    private final UUID ownerUuid;
    private final Material material;
    private long creationTime;
    private int index;
    private final String tycoonDisplayName;
    private final String blockUID;
    //</editor-fold>
    private int level;
    private int levelXp;

    private double storedBalance;

    private boolean isActive;
    private Material lastSpawnedMaterial;


    private int tickCounter = 0;
    private int miningTickCounter = 0;

    private final Inventory inventory;
    private final TycoonInventory tycoonInventory;
    private boolean autoMinerEnabled;

    private final TycoonType type;

    private String hologramUID;
    Block block;
    public static final Map<Location, String> hologramMap = new HashMap<>();

    private final Set<Block> activeBlocks = new HashSet<>();

    HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

    private final double basePrice;
    //<editor-fold desc="⚙️ Upgrade Variables">
    //⬇️========== Upgrade Attributes ==========⬇️
    private int spawnRate;
    private final int minSpawnRate = 10;
    private int spawnRateLevel;

    private int miningRate;
    private static final int MIN_MINING_RATE = 10;
    private int miningRateLevel;

    private double sellMultiplier = 1;
    private int sellMultiplierLevel;
    private double sellMultiplierBuff;
    private double maxSellMultiplier = 5.0;

    private double doubleDropsChance = 0.0;
    private int doubleDropsChanceLevel;
    private double maxDoubleDropsChance = 100.0;

    private double fortuneChance = 1.0;
    private int fortuneChanceLevel;
    private double maxFortuneChance = 100.0;

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
    private TycoonBoosterManager tycoonBooster;
    private SellMultiplyBooster sellMultiplyBooster;
    private AutoMinerSpeedBooster autoMinerSpeedBooster;
    private SpawnSpeedBooster spawnSpeedBooster;
    //========== Booster Attributes ==========


    private final OreTycoon plugin;
    private final TycoonBlockManager blockManager;
    private final LevelManager levelManager;

    private final Map<Material, Integer> ressourceMaterialsMap;
    private Map<Material, Boolean> activeRessourceMaterialsMap = new HashMap<>();

    private final Random random = new Random();


    public TycoonBlock(TycoonType type, Location location, UUID ownerUuid, boolean isActive, OreTycoon plugin, TycoonUpgrades upgrades) {
        this.location = location;
        this.block = location.getBlock();
        this.ownerUuid = ownerUuid;
        this.owner = Bukkit.getOfflinePlayer(ownerUuid);
        this.isActive = isActive;

        this.plugin = plugin;
        this.blockManager = plugin.getBlockManager();
        this.levelManager = plugin.getLevelManager();

        this.creationTime = System.currentTimeMillis();
        level = 1;
        levelXp = 0;
        storedBalance = 0;

        this.autoMinerEnabled = false;

        this.type = type;
        this.material = type.getMaterial();
        this.basePrice = type.getBasePrice();
        this.spawnRate = type.getSpawnInterval();
        this.miningRate = type.getMiningInterval();
        this.ressourceMaterialsMap = type.getResources();
        this.tycoonDisplayName = type.getName();
        this.inventoryStorage = type.getDefaultMaxInventoryStorage();
        this.buffMaterials = type.getBuffMaterials();

        //Set all ressources to inactive
        for (Map.Entry<Material, Integer> entry : ressourceMaterialsMap.entrySet()) {
            this.activeRessourceMaterialsMap.put(entry.getKey(), true);
        }

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






        this.blockUID = Objects.requireNonNull(this.location.getWorld()).getName() + "_" +
                this.location.getBlockX() + "_" +
                this.location.getBlockY() + "_" +
                this.location.getBlockZ();


        this.tycoonInventory = new TycoonInventory(this, plugin);
        this.inventory = Bukkit.createInventory(new TycoonHolder(this.tycoonInventory), 36, tycoonDisplayName);
        checkIfBuffed();
    }

    //<editor-fold desc="🔎 Increment and Check">
    public void incrementAndCheck() {
        tickCounter++;

        if (tickCounter >= spawnRate) {
            tickCounter = 0;
            if (isActive) {

                if (random.nextDouble() * 100.0 < doubleDropsChance) {
                    Console.log(getClass(), "A double Drop has been spawned!");
                    trySpawnMultiplyResources(2);
                }else {
                    trySpawnMultiplyResources(1);
                }
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
                Block target = activeBlocks.stream()
                        .skip(random.nextInt(activeBlocks.size()))
                        .findFirst().orElse(null);

                if (target != null && target.getType() != Material.AIR) {
                    tryAutoMining(this, target.getLocation());
                }
            }
        }
    }
    //</editor-fold>

    public void handleReward(Block block) {
        levelManager.handleXpGain(this, 50);
        //blockManager.playXpBlockHologram(this, block, 50);
        removeBlock(block);

    }

    //<editor-fold desc="📦 Inventory Methods">
    public void sellInventory(Inventory inventory, Player player) {
        Economy econ = OreTycoon.getEconomy();
        double worth = PriceUtility.calculateWorth(inventory) * sellMultiplier;
        System.out.println("TycoonBlock Calculate Worth: " + PriceUtility.formatMoney(worth));
        if (worth <= 0) return;
        econ.depositPlayer(player, worth);
        player.sendMessage(ChatColor.GREEN + "Sold items worth: " + PriceUtility.formatMoney(worth) + " with " + sellMultiplier + "x Sell Multiplier");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.3f, 1);
        cleanInventory(inventory);
        updateHologramPreset(location, "WORTH");
    }

    public void cleanInventory(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);

            if (item == null || item.getType() == Material.AIR) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                inventory.setItem(i, null);
                continue;
            }

            if (!meta.getPersistentDataContainer().has(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING)) {
                inventory.setItem(i, null);
            }
        }
    }
    //</editor-fold>

    @Deprecated
    public void trySpawnResource() {

        Location center = getLocation();
        World world = center.getWorld();


        // 1. Definiere das 5x5 Areal (vom Zentrum aus -2 bis +2)
        int minX = center.getBlockX() - 2;
        int maxX = center.getBlockX() + 2;
        int minZ = center.getBlockZ() - 2;
        int maxZ = center.getBlockZ() + 2;
        int fixedY = center.getBlockY(); // Wir spawnen nur auf der Y-Ebene des Tycoon-Block

        int randomX = random.nextInt(maxX - minX + 1) + minX;
        int randomZ = random.nextInt(maxZ - minZ + 1) + minZ;

        Location randomLocation = new Location(center.getWorld(), randomX, fixedY, randomZ);
        Block spawnBlock = randomLocation.getBlock();

        if (spawnBlock.getType().equals(Material.AIR)) {
            //Valid Spawn point
            Material material = getRandomMaterial(ressourceMaterialsMap);
            if (material == null) return;
            spawnBlock.setType(material);
            //Gespawnten Block merken!
            //spawnedBlocksMap.put(spawnBlock.getLocation(), spawnBlock);
            activeBlocks.add(spawnBlock);

            spawnBlock.setMetadata("tycoon_id", new FixedMetadataValue(plugin, blockUID));

            //tycoonBlock.manipulateHologram(tycoonBlock.getLocation(), material.name());
            setLastSpawnedMaterial(material);
            updateHologramPreset(getLocation(), "BLOCK");
            assert world != null;
            world.playSound(randomLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.5f);
            world.spawnParticle(Particle.EXPLOSION, randomLocation, 1);
        }
    }
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
            Block spawnBlock = randomLocation.getBlock();

            //Check Spawn Location validity
            if (spawnBlock.getType().equals(Material.AIR)) {
                //Valid Spawn point
                Material material = getRandomMaterial(ressourceMaterialsMap);
                if (material == null) return;
                spawnBlock.setType(material);
                //Gespawnten Block merken!
                //spawnedBlocksMap.put(spawnBlock.getLocation(), spawnBlock);
                activeBlocks.add(spawnBlock);

                spawnBlock.setMetadata("tycoon_id", new FixedMetadataValue(plugin, blockUID));

                //tycoonBlock.manipulateHologram(tycoonBlock.getLocation(), material.name());
                setLastSpawnedMaterial(material);
                updateHologramPreset(getLocation(), "BLOCK");
                assert world != null;
                world.playSound(randomLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.5f);
                world.spawnParticle(Particle.EXPLOSION, randomLocation, 1);
            }
        }


    }

    public void updateAttributes() {
        sellMultiplierLevel = upgrades.getSellMultiplierLevel();
        sellMultiplierBuff = upgrades.getSellMultiplierBuff();

        spawnRateLevel = upgrades.getSpawnRateLevel();
        spawnRate = TycoonUpgrades.calculateNewSpawnRate(spawnRateLevel, type.getSpawnInterval());
        if (spawnSpeedBooster != null){
            if (spawnRate >= minSpawnRate + spawnSpeedBooster.getBoostValue()){
                spawnRate -= (int) spawnSpeedBooster.getBoostValue();
            } else {
                spawnRate = minSpawnRate;
            }
        }

        miningRateLevel = upgrades.getMiningRateLevel();
        miningRate = TycoonUpgrades.calculateNewMiningRate(miningRateLevel, type.getMiningInterval());
        if (autoMinerSpeedBooster != null) {
            Console.log(getClass(), "Miningrate before boost: " + miningRate);
            miningRate -= (int) autoMinerSpeedBooster.getBoostValue();
            Console.log(getClass(), "Auto Miner Speed with booster enabled: " + miningRate + " Booster Value: " + autoMinerSpeedBooster.getBoostValue());
        }


        sellMultiplier = TycoonUpgrades.calculateNewSellMultiplier(sellMultiplierLevel, type.getSellMultiplier());
        if (sellMultiplyBooster != null) {
            sellMultiplier += sellMultiplyBooster.getBoostValue();
        }
        sellMultiplier *= sellMultiplierBuff;

        doubleDropsChanceLevel = upgrades.getDoubleDropsLevel();
        doubleDropsChance = TycoonUpgrades.calculateNewDoubleDropChance(doubleDropsChanceLevel, 0.0);

        fortuneChanceLevel = upgrades.getFortuneLevel();
        fortuneChance = TycoonUpgrades.calculateNewFortuneChance(fortuneChanceLevel, 1.0);

        inventoryStorageLevel = upgrades.getInventoryStorageLevel();
        inventoryStorage = TycoonUpgrades.getMaxInventoryStorage(inventoryStorageLevel, type.getDefaultMaxInventoryStorage());

        isAutoMinerUnlocked = upgrades.isAutoMinerUnlocked();

        updateHologramPreset(getLocation(), "ALL");
        TycoonChangedAttributesEvent event = new TycoonChangedAttributesEvent(this);
        Bukkit.getPluginManager().callEvent(event);
    }
    //========== Upgrade Methods ==========
    //<editor-fold desc="🔧 Upgrade Methods">
    public void upgradeSpawnRate(Player player) {
        if (spawnRate <= minSpawnRate) {
            player.sendMessage(ChatColor.RED + "Max Level Reached!");
            return;
        }
        double cost = TycoonUpgrades.getSpawnRateUpgradeCost(this,spawnRateLevel + 1);
        Economy economy = OreTycoon.getEconomy();
        if (economy.has(player, cost)) {
            economy.withdrawPlayer(player, cost);
            int nextLevel = spawnRateLevel + 1;
            upgrades.setSpawnRateLevel(nextLevel);
            updateAttributes();
            Console.debug("Upgrade spawn rate level to: " + nextLevel + " New spawn rate: " + spawnRate + " At cost: " + economy.format(cost));
            player.sendMessage(ChatColor.GREEN + "You upgraded the Spawn rate to " + getSpawnRateFormatted() + "s for: " + PriceUtility.formatMoney(cost));
            Bukkit.getPluginManager().callEvent(new TycoonChangedAttributesEvent(this));
        }else {
            player.sendMessage(ChatColor.RED + "Not enough money!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }

    }

    public void upgradeMiningRate(Player player) {
        if (miningRate <= MIN_MINING_RATE) {
            player.sendMessage(ChatColor.RED + "Max Level Reached!");
            return;
        }
        double cost = TycoonUpgrades.getMiningRateUpgradeCost(this,miningRateLevel + 1);
        Economy economy = OreTycoon.getEconomy();
        if (miningRate == spawnRate) {
            player.sendMessage(ChatColor.RED + "Mining rate level " + miningRateLevel + " can't be higher than spawn rate level: " + spawnRateLevel);
            return;
        }
        if (economy.has(player, cost)) {
            economy.withdrawPlayer(player, cost);
            int nextLevel = miningRateLevel + 1;
            upgrades.setMiningRateLevel(nextLevel);
            updateAttributes();
            Console.debug("Upgrade mining rate level to " + nextLevel + " New mining rate: " + miningRate + " At cost: " + economy.format(cost));
            player.sendMessage(ChatColor.GREEN + "You upgraded the Mining rate to " + getMiningRateFormatted() + "s for: " + PriceUtility.formatMoney(cost));

            Bukkit.getPluginManager().callEvent(new TycoonChangedAttributesEvent(this));
        }else {
            player.sendMessage(ChatColor.RED + "Not enough money!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
    }
    public void upgradeMaxInventoryStorage(Player player) {
        upgrades.setInventoryStorageLevel(inventoryStorageLevel + 1);
        updateAttributes();
        player.sendMessage(ChatColor.GREEN + "Upgraded Storage to " + getStorageStatisticFormatted());
    }

    public void upgradeSellMultiplier(Player player) {
        if (sellMultiplier >= maxSellMultiplier) {
            player.sendMessage(ChatColor.RED + "Max Level Reached!");
            return;
        }
        double cost = TycoonUpgrades.getSellMultiplierUpgradeCost(this,sellMultiplierLevel + 1);
        Economy economy = OreTycoon.getEconomy();
        if (economy.has(player, cost)) {
            economy.withdrawPlayer(player, cost);
            int nextLevel = sellMultiplierLevel + 1;
            upgrades.setSellMultiplierLevel(nextLevel);
            updateAttributes();
            player.sendMessage(ChatColor.GREEN + "You upgraded the Sell Multiplier to " + getSellMultiplier() + "x for: " + PriceUtility.formatMoney(cost));
        }else {
            player.sendMessage(ChatColor.RED + "Not enough money!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
    }
    public void upgradeDoubleDropsChance(Player player) {
        if (doubleDropsChance >= maxDoubleDropsChance) {
            player.sendMessage(ChatColor.RED + "Max Level Reached!");
            return;
        }
        double cost = TycoonUpgrades.getDoubleDropChanceUpgradeCost(this,doubleDropsChanceLevel + 1);
        Economy economy = OreTycoon.getEconomy();
        if (economy.has(player, cost)) {
            economy.withdrawPlayer(player, cost);
            int nextLevel = doubleDropsChanceLevel + 1;
            upgrades.setDoubleDropsLevel(nextLevel);
            updateAttributes();
            player.sendMessage(ChatColor.GREEN + "You upgrade Double Drops to " + getDoubleDropsChanceFormatted() + " for: " + PriceUtility.formatMoney(cost));
        }else {
            player.sendMessage(ChatColor.RED + "Not enough money!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
    }
    public void upgradeFortuneChance(Player player) {
        if (fortuneChance >= maxFortuneChance) {
            player.sendMessage(ChatColor.RED + "Max Level Reached!");
            return;
        }
        double cost = TycoonUpgrades.getFortuneUpgradeCost(this,fortuneChanceLevel + 1);
        Economy economy = OreTycoon.getEconomy();
        if (economy.has(player, cost)) {
            economy.withdrawPlayer(player, cost);
            int nextLevel = fortuneChanceLevel + 1;
            upgrades.setFortuneLevel(nextLevel);
            updateAttributes();
            player.sendMessage(ChatColor.GREEN + "You upgraded Fortune to " + getFortuneChanceFormatted() + " for: " + PriceUtility.formatMoney(cost));
        }else {
            player.sendMessage(ChatColor.RED + "Not enough money!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
    }
    //</editor-fold>
    //========== Upgrade Methods ==========

    //========= Buff methods =========
    //<editor-fold desc="🔥 Buff Methods">
    public boolean checkIfBuffed() {
        Location checkLocation = location.clone();
        checkLocation.add(0, -1, 0);
        if (buffMaterials.contains(checkLocation.getBlock().getType())) {
            isBuffed = true;
            activateSellMultiplierBuff();
            updateHologramPreset(location, "BUFF");
            return true;
        }else  {
            isBuffed = false;
            deactivateSellMultiplierBuff();
            updateHologramPreset(location, "BUFF");
            return false;
        }
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



    //---------- AutoMiner ----------
    public boolean tryAutoMining(TycoonBlock tycoonBlock, Location blockLocation) {
        if (!isAutoMinerUnlocked){
            return false;
        }
        ItemStack item = new ItemStack(blockLocation.getBlock().getType());
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return false;
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        pdc.set(TycoonData.BLOCK_IS_AUTOMINED_KEY, PersistentDataType.STRING, "block_is_automined");
        if (!tycoonBlock.canFitItem(tycoonBlock.getInventory(), item)) {
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
                    ItemStack item = new ItemStack(blockLocation.getBlock().getType());

                    //Fortune Multiplier
                    if (random.nextDouble() * 100.0 < fortuneChance) {
                        item.setAmount(2);
                        Console.log(getClass(), "Fortune has doubled the drop!");
                    }
                    //Fortune Multiplier

                    TycoonAutoMinedEvent event = new TycoonAutoMinedEvent(tycoonBlock, item);
                    Bukkit.getPluginManager().callEvent(event);

                    tycoonBlock.handleReward(blockLocation.getBlock());

                    blockLocation.getBlock().setType(Material.AIR);
                    pdc.remove(TycoonData.BLOCK_IS_AUTOMINED_KEY);
                    this.cancel();
                }

            }
        }.runTaskTimer(plugin, 0, 1L);
        return true;
    }
    //---------- AutoMiner ----------

    private Material getRandomMaterial(Map<Material, Integer> map) {
        //Only from active Materials
        int totalActiveWeight = 0;
        for (Map.Entry<Material, Integer> entry : map.entrySet()) {
            //Only when the Material is active it gets added to the total Weight
            if (activeRessourceMaterialsMap.get(entry.getKey())) {
                totalActiveWeight += entry.getValue();
            }
        }

        //Safety Check if every Item is disabled
        if (totalActiveWeight <= 0) {
            return null;
        }

        int randomValue = random.nextInt(totalActiveWeight);
        int currentSum = 0;

        for (Map.Entry<Material, Integer> entry : map.entrySet()) {
            //Only check for active Ressources
            if (activeRessourceMaterialsMap.get(entry.getKey())) {
                currentSum += entry.getValue();
                if (randomValue < currentSum) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public double getAverageWorth() {
        Map<Material, Integer> resources = type.getResources();

        if (resources == null || resources.isEmpty()) return 0.0;

        double totalWeight = 0;
        for (int weight : resources.values()) {
            totalWeight += weight;
        }
        double averageWorth = 0;
        for (Map.Entry<Material, Integer> entry : resources.entrySet()) {
            Material mat = entry.getKey();
            int weight = entry.getValue();

            // Hier holst du den Preis pro Stück aus deiner Preis-Liste
            ItemStack item = new ItemStack(mat);
            double price = PriceUtility.calculateWorth(item);

            // Anteil am Gesamtwert berechnen
            averageWorth += price * (weight / totalWeight);
        }
        return averageWorth;
    }

    public int getTotalActiveWeight(){
        Map<Material, Integer> OriginalResources = type.getResources();

        int totalWeight = 0;
        for (Map.Entry<Material, Integer> entry : OriginalResources.entrySet()) {
            if (activeRessourceMaterialsMap.get(entry.getKey())) {
                totalWeight += entry.getValue();
            }
        }
        return totalWeight;
    }

    public boolean containsBlock(Block block) {
        return activeBlocks.contains(block);
    }
    public void removeBlock(Block block) {
        activeBlocks.remove(block);
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

        List<TycoonBlock> tycoonBlockList = blockManager.getTycoonBlocksFromPlayer(ownerUuid);

        index = -1;
        for (int i = 0; i < tycoonBlockList.size(); i++) {
            if (tycoonBlockList.get(i).getBlockUID().equals(blockUID)) {
                index = i + 1;
                break;
            }
        }

        List<String> name = new LinkedList<>();
        name.add("[ " + getOwnerName() + "'s Tycoon #" + index + " ]");
        hologramData.setText(name);

        hologramData.addLine(tycoonDisplayName + ChatColor.RESET);

        hologramData.addLine("Status: " + isActiveFormatted() + ChatColor.WHITE + " | " + isBuffedFormatted() + ChatColor.RESET);

        hologramData.addLine("Level: " + level);
        hologramData.addLine("xp: " + levelXp + "/" + levelManager.getXpNeededForLevel(level + 1) + " | " + (int) levelManager.getProgressPercentage(levelXp, level + 1) + "%");
        hologramData.addLine(ChatColor.DARK_GRAY + "[" +getProgressBar(20) + ChatColor.DARK_GRAY + "]");
        double currentWorth = PriceUtility.calculateWorth(inventory);
        hologramData.addLine(ChatColor.RESET + "Inventory: "+ ChatColor.GREEN + PriceUtility.formatMoney(currentWorth) + ChatColor.WHITE + " | " + getStorageStatisticFormatted());
        hologramData.setBackground(Color.fromARGB(60, 80, 80, 80));
        hologramData.setPersistent(false);
        hologramData.setTextShadow(true);
        hologramData.setSeeThrough(false);
        Hologram hologram = manager.create(hologramData);
        manager.addHologram(hologram);


        hologram.queueUpdate();
        hologramMap.put(location, hologramUID);
    }
    public void removeHologram(Location location) {
            if (getHologram(location) != null) {
                manager.removeHologram(getHologram(location));
                hologramMap.remove(location);
            }
    }
    public void updateHologram(Location location) {
        Hologram hologram = getHologram(location);
        if (hologram != null) {
            hologram.queueUpdate();
        }
    }
    public void updateHologramPreset(Location location, String preset) {
        Hologram hologram = getHologram(location);
        if (hologram == null) {
            Console.error(getClass() ,"No Hologram found at Location " + location);
            return;
        }
        HologramData data = hologram.getData();
        //TextHologramData textHologramData = (TextHologramData) data;
        List<String> hologramLines = ((TextHologramData) data).getText();

        double currentWorth;
        List<TycoonBlock> tycoonBlockList = blockManager.getTycoonBlocksFromPlayer(ownerUuid);
        switch (preset) {
            case "BLOCKNAME":
                hologramLines.set(1, tycoonDisplayName + ChatColor.RESET);
                break;
            case "STATUS", "BUFF":
                hologramLines.set(2, "Status: " + isActiveFormatted() + ChatColor.WHITE + " | " + isBuffedFormatted() + ChatColor.RESET);
                break;
            case "LEVEL":
                hologramLines.set(3, "Level: " + level);
                break;
            case "XP":
                hologramLines.set(4, "xp: " + levelXp + "/" + levelManager.getXpNeededForLevel(level + 1) + " | " + (int) levelManager.getProgressPercentage(levelXp, level + 1) + "%");
                break;
            case "PROGRESS":
                hologramLines.set(5, ChatColor.DARK_GRAY + "[" +getProgressBar(20) + ChatColor.DARK_GRAY + "]");
                break;
            case "WORTH", "BALANCE", "STORAGE":
                currentWorth = PriceUtility.calculateWorth(inventory);
                hologramLines.set(6, ChatColor.RESET + "Inventory: "+ ChatColor.GREEN + PriceUtility.formatMoney(currentWorth) + ChatColor.WHITE + " | " + getStorageStatisticFormatted());
                break;
            case "ORDER":

                index = -1;
                for (int i = 0; i < tycoonBlockList.size(); i++) {
                    if (tycoonBlockList.get(i).getBlockUID().equals(blockUID)) {
                        index = i + 1;
                        break;
                    }
                }
                hologramLines.set(0, "[ " + getOwnerName() + "'s Tycoon #" + index + " ]");
                break;
            case "ALL":
                hologramLines.set(1, tycoonDisplayName + ChatColor.RESET);
                hologramLines.set(2, "Status: " + isActiveFormatted() + ChatColor.WHITE + " | " + isBuffedFormatted() + ChatColor.RESET);
                hologramLines.set(3, "Level: " + level);
                hologramLines.set(4, "xp: " + levelXp + "/" + levelManager.getXpNeededForLevel(level + 1) + " | " + (int) levelManager.getProgressPercentage(levelXp, level + 1) + "%");
                hologramLines.set(5, ChatColor.DARK_GRAY + "[" +getProgressBar(20) + ChatColor.DARK_GRAY + "]");
                currentWorth = PriceUtility.calculateWorth(inventory);
                hologramLines.set(6, ChatColor.RESET + "Inventory: "+ ChatColor.GREEN + PriceUtility.formatMoney(currentWorth) + ChatColor.WHITE + " | " + getStorageStatisticFormatted());

                index = -1;
                for (int i = 0; i < tycoonBlockList.size(); i++) {
                    if (tycoonBlockList.get(i).getBlockUID().equals(blockUID)) {
                        index = i + 1;
                        break;
                    }
                }
                hologramLines.set(0, "[ " + getOwnerName() + "'s Tycoon #" + index + " ]");

                return;
            default:
                break;
        }
        updateHologram(location);
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

    // ---------     Adder      ---------
    public void addActiveBlocks(Block block) {
        activeBlocks.add(block);
    }
    public String getStorageStatisticFormatted(){
        int storedItems = getStoredItemsCount();
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
    public int getStoredItemsCount(){
        int storedItemsCount = 0;
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta != null) {
                    if (itemMeta.getPersistentDataContainer().has(TycoonData.MENU_ITEM_KEY, PersistentDataType.STRING)) {
                        continue;
                    }
                }
                storedItemsCount += itemStack.getAmount();
            }
        }
        return storedItemsCount;
    }
    public boolean canFitItem(Inventory inv, ItemStack item) {

        if (getStoredItemsCount() + item.getAmount() > inventoryStorage) {
            Console.debug("[Tycoon] Storage Full: " + getStoredItemsCount() + "/" + inventoryStorage);
            return false;
        }
        // 1. Gibt es überhaupt einen komplett leeren Slot?
        if (inv.firstEmpty() != -1) return true;

        // 2. Wenn kein leerer Slot da ist, prüfe, ob ein existierender Stack
        // des gleichen Typs noch Platz für weitere Items hat.

        for (ItemStack content : inv.getContents()) {
            if (content != null && content.isSimilar(item)) {
                if (content.getAmount() < content.getMaxStackSize()) {

                    return true; // Es ist noch Platz in diesem Stack
                }
            }
        }

        return false; // Absolut kein Platz mehr
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
        return location;
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
    public String isActiveFormatted(){
        if (isActive) {
            return ChatColor.GREEN + "spawning..." + ChatColor.RESET;
        }else {
            return ChatColor.RED + "offline..." + ChatColor.RESET;
        }
    }
    public Material getLastSpawnedMaterial() {
        return lastSpawnedMaterial;
    }
    public int getSpawnRate() {
        return spawnRate;
    }
    public String getHologramUID(){
        return hologramUID;
    }
    public Set<Block> getActiveBlocks(){
        return activeBlocks;
    }
    public Material getMaterial() {
        return material;
    }
    public long getCreationTime() {
        return creationTime;
    }
    public boolean isAutoMinerEnabled() {
        return autoMinerEnabled;
    }
    public Inventory getInventory() {
        return inventory;
    }
    public double getStoredBalance(){
        return storedBalance;
    }
    public TycoonInventory getTycoonInventory() {
        return tycoonInventory;
    }
    public int getSpawnRateLevel() {
        return spawnRateLevel;
    }
    public int getMiningRate() {
        return miningRate;
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
    public TycoonUpgrades getTycoonUpgrades() {
        return upgrades;
    }
    public double getSpawnRateFormatted(){
        return (double) spawnRate /20;
    }
    public double getMiningRateFormatted(){
        return (double) miningRate /20;
    }
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
        updateHologramPreset(location, "STATUS");
    }
    public void setLastSpawnedMaterial(Material lastSpawnedMaterial) {
        this.lastSpawnedMaterial = lastSpawnedMaterial;
    }
    public void setSpawnRate(int spawnRate) {
        this.spawnRate = spawnRate;
    }
    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
    public void setAutoMinerEnabled(boolean autoMinerEnabled) {
        if (isAutoMinerUnlocked){
            this.autoMinerEnabled = autoMinerEnabled;
        }
    }
    public void setActiveRessourceMaterialsMap(Map<Material, Boolean> activeRessourceMaterialsMap) {
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
    // ---------     Setter      ---------
}
