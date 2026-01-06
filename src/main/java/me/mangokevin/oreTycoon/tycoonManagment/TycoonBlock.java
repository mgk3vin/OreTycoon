package me.mangokevin.oreTycoon.tycoonManagment;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.commands.tycooncmds.menuManager.MenuManager;
import me.mangokevin.oreTycoon.commands.tycooncmds.menuManager.TycoonInventory;
import me.mangokevin.oreTycoon.levelManagment.LevelManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class TycoonBlock {

    private final Location location;
    private final OfflinePlayer owner;
    private final UUID ownerUuid;
    private final Material material;
    private long creationTime;
    private int index;
    private final String tycoonDisplayName;
    private final String blockUID;

    private int level;
    private int levelXp;

    private double storedBalance;

    private boolean isActive;
    private Material lastSpawnedMaterial;

    private int tickCounter = 0;
    private int spawnInterval;
    private int miningTickCounter = 0;
    private final int miningInterval;


    private final Inventory inventory;
    private final TycoonInventory tycoonInventory;
    private boolean autoMinerEnabled;

    private final TycoonType type;

    private String hologramUID;
    Block block;
    public static final Map<Location, String> hologramMap = new HashMap<>();

    private final Set<Block> activeBlocks = new HashSet<>();
    private List<Location> spawnedBlockLocations = new ArrayList<>();

    HologramManager manager = FancyHologramsPlugin.get().getHologramManager();



    private final OreTycoon plugin;
    private final TycoonBlockManager blockManager;
    private final LevelManager levelManager;

    private final Map<Material, Integer> ressourceMaterialsMap;


    public TycoonBlock(TycoonType type,Location location, UUID ownerUuid, boolean isActive, OreTycoon plugin, TycoonBlockManager blockManager, LevelManager levelManager) {
        this.location = location;
        this.block = location.getBlock();
        this.ownerUuid = ownerUuid;
        this.owner = Bukkit.getOfflinePlayer(ownerUuid);
        this.isActive = isActive;
        this.plugin = plugin;
        this.blockManager = blockManager;
        this.levelManager = levelManager;
        this.creationTime = System.currentTimeMillis();
        level = 1;
        levelXp = 0;
        storedBalance = 0;

        this.autoMinerEnabled = false;

        this.type = type;
        this.material = type.getMaterial();
        this.spawnInterval = type.getSpawnInterval();
        this.miningInterval = type.getMiningInterval();
        this.ressourceMaterialsMap = type.getResources();
        this.tycoonDisplayName = type.getName();

        this.spawnedBlockLocations = new ArrayList<>();


        this.blockUID = Objects.requireNonNull(this.location.getWorld()).getName() + "_" +
                this.location.getBlockX() + "_" +
                this.location.getBlockY() + "_" +
                this.location.getBlockZ();


        this.tycoonInventory = new TycoonInventory(this, plugin);
        this.inventory = Bukkit.createInventory(new TycoonHolder(this.tycoonInventory), 36, tycoonDisplayName);
    }


    public void incrementAndCheck(){
        tickCounter++;
        if (tickCounter >= spawnInterval) {
            tickCounter = 0;
            if (isActive) {
                trySpawnRessource();
            }
        }
        // 2. Der Auto-Miner Timer (Erz abbauen)
        // Wir prüfen: Ist der Modus an UND ist der Tycoon aktiv?
        if (autoMinerEnabled) {
            miningTickCounter++;

            if (miningTickCounter >= miningInterval) {
                miningTickCounter = 0;
                // Wichtig: Nur versuchen abzubauen, wenn dort auch wirklich ein Block steht!
                Random random = new Random();
                for (Block block : activeBlocks) {
                    spawnedBlockLocations.add(block.getLocation());
                }
                if (spawnedBlockLocations.isEmpty()) {return;}
                Location spawnedBlockLoc;
                if (spawnedBlockLocations.size() == 1) {
                    spawnedBlockLoc = spawnedBlockLocations.getFirst();
                }else {
                    spawnedBlockLoc = spawnedBlockLocations.get(random.nextInt(0, spawnedBlockLocations.size() - 1));
                }

                if (spawnedBlockLoc.getBlock().getType() != Material.AIR) {
                    setAutoMinerEnabled(blockManager.tryAutoMining(this, spawnedBlockLoc));
                }
            }
        } else {
            // Falls der Miner aus ist, setzen wir den Counter zurück,
            // damit er nicht "vorlädt" für den Moment des Einschaltens.
            miningTickCounter = 0;
        }
    }
    public void handleReward(Block block) {
        levelManager.handleXpGain(this, 50);
        blockManager.playXpBlockHologram(this, block, 50);
        removeBlock(block);

    }
    public void sellInventory(Inventory inventory, Player player) {
        Economy econ = OreTycoon.getEconomy();
        double worth = PriceUtility.calculateWorth(inventory);
        System.out.println("TycoonBlock Calculate Worth: " + "$" + PriceUtility.formatMoney(worth));
        if (worth <= 0) return;
        econ.depositPlayer(player, worth);
        player.sendMessage(ChatColor.GREEN + "Sold items worth: " + "$" + PriceUtility.formatMoney(worth));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.3f, 1);
        cleanInventory(inventory);
    }
    public void cleanInventory(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);

            if(item==null || item.getType() == Material.AIR)continue;

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
    @Deprecated
    public void withdrawBalance(Player player){
        Economy economy = OreTycoon.getEconomy();

        if (storedBalance > 0) {
            economy.depositPlayer(player, storedBalance);
            player.sendMessage(ChatColor.GREEN + "You collected " + economy.format(storedBalance) + " from your tycoon!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.5f, 1);
            storedBalance = 0;
        }
    }
    public void trySpawnRessource() {
        Location center = getLocation();
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
    private Material getRandomMaterial(Map<Material, Integer> map) {
        int totalWeight = 0;
        for (int weight : map.values()) {
            totalWeight += weight;
        }
        int randomValue = new Random().nextInt(totalWeight);
        int currentSum = 0;
        for (Map.Entry<Material, Integer> entry : map.entrySet()) {
            currentSum += entry.getValue();
            if (randomValue < currentSum) {
                return entry.getKey();
            }
        }
        return null;
    }

    public boolean containsBlock(Block block) {
        return activeBlocks.contains(block);
    }
    public void removeBlock(Block block) {
        activeBlocks.remove(block);
    }

    // ---------     SpawnedBlockHologram      ---------
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

        hologramData.addLine("Status: " + isActiveFormatted());

        hologramData.addLine("Level: " + level);
        hologramData.addLine("xp: " + levelXp + "/" + levelManager.getXpNeededForLevel(level + 1) + " | " + (int) levelManager.getProgressPercentage(levelXp, level + 1) + "%");
        hologramData.addLine(ChatColor.DARK_GRAY + "[" +getProgressBar(20) + ChatColor.DARK_GRAY + "]");
        hologramData.setBackground(Color.fromARGB(0));
        hologramData.setPersistent(false);
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
            return;
        }
        HologramData data = hologram.getData();
        //TextHologramData textHologramData = (TextHologramData) data;
        List<String> hologramLines = ((TextHologramData) data).getText();


        switch (preset) {
            case "BLOCKNAME":
                hologramLines.set(1, tycoonDisplayName + ChatColor.RESET);
                break;
            case "STATUS":
                hologramLines.set(2, "Status: " + isActiveFormatted());
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
            case "ORDER":
                List<TycoonBlock> tycoonBlockList = blockManager.getTycoonBlocksFromPlayer(ownerUuid);

                index = -1;
                for (int i = 0; i < tycoonBlockList.size(); i++) {
                    if (tycoonBlockList.get(i).getBlockUID().equals(blockUID)) {
                        index = i + 1;
                        break;
                    }
                }
                hologramLines.set(0, "[ " + getOwnerName() + "'s Tycoon #" + index + " ]");
            default:
                break;
        }
        updateHologram(location);
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


    // ---------     Adder      ---------
    public void addActiveBlocks(Block block) {
        activeBlocks.add(block);
    }
    public void tryAddBlocksToInventory(Block block) {
        ItemStack item = new ItemStack(block.getType());

        if (canFitItem(inventory, item)) {
            inventory.addItem(item);
        }
    }
    public boolean canFitItem(Inventory inv, ItemStack item) {
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
    public int getSpawnInterval() {
        return spawnInterval;
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
    public void setSpawnInterval(int spawnInterval) {
        this.spawnInterval = spawnInterval;
    }
    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
    public void setAutoMinerEnabled(boolean autoMinerEnabled) {
        this.autoMinerEnabled = autoMinerEnabled;
    }
    // ---------     Setter      ---------
}
