package me.mangokevin.oreTycoon.tycoonManagment;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.levelManagment.LevelManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class TycoonBlock {

    private final Location location;
    private OfflinePlayer owner;
    private final UUID ownerUuid;
    private final Material material;
    private long creationTime;
    private int index;
    private String tycoonDisplayName;


    private int level;
    private int totalXp;
    private int levelXp;
    private double progress;

    private boolean isActive;
    private Material lastSpawnedBlock;
    private int tickCounter = 0;
    private int spawnInterval;
    private final String blockUID;

    private final TycoonType type;

    private String hologramUID;
    Block block;
    public static final Map<Location, String> hologramMap = new HashMap<>();

    private final Set<Block> activeBlocks = new HashSet<>();

    HologramManager manager = FancyHologramsPlugin.get().getHologramManager();



    private final OreTycoon plugin;
    private final TycoonBlockManager blockManager;
    private final LevelManager levelManager;

    private HashMap<Material, Integer> ressourceMaterials = new HashMap<>();
    private Map<Material, Integer> ressourceMaterialsMap;

    private final List<Material> TYCOON_RESOURCE_MATERIALS = Arrays.asList(
            Material.COAL_ORE,
            Material.IRON_ORE,
            Material.GOLD_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE
            // Fügen Sie hier weitere Materialien hinzu
    );

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
        totalXp = 0;
        levelXp = 0;
        progress = 0;

        this.type = type;
        this.material = type.getMaterial();
        this.spawnInterval = type.getSpawnInterval();
        this.ressourceMaterialsMap = type.getResources();
        this.tycoonDisplayName = type.getName();


        fillRessources();

        this.blockUID = Objects.requireNonNull(this.location.getWorld()).getName() + "_" +
                this.location.getBlockX() + "_" +
                this.location.getBlockY() + "_" +
                this.location.getBlockZ();


    }

    public void incrementAndCheck(){
        tickCounter++;
        if (tickCounter >= spawnInterval) {
            tickCounter = 0;
            if (isActive) {
                trySpawnRessource();
            }
        }
    }


    public void fillRessources(){
        //Material : weight in procent
        ressourceMaterials.put(Material.STONE, 20);
        ressourceMaterials.put(Material.DIRT, 5);
        ressourceMaterials.put(Material.GRANITE, 10);
        ressourceMaterials.put(Material.DIORITE, 10);
        ressourceMaterials.put(Material.ANDESITE, 15);
        ressourceMaterials.put(Material.GRAVEL, 10);
        ressourceMaterials.put(Material.COAL_ORE, 10);
        ressourceMaterials.put(Material.IRON_ORE, 15);
        ressourceMaterials.put(Material.COPPER_ORE, 5);
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
            setLastSpawnedBlock(material);
            updateHologramPreset(getLocation(), "BLOCK");
            assert world != null;
            world.playSound(randomLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.5f);
            world.spawnParticle(Particle.EXPLOSION, randomLocation, 1);
        }

    }
    @Deprecated
    private Material getRandomMaterial() {
        Random rand = new Random();
        int randint = rand.nextInt(0, TYCOON_RESOURCE_MATERIALS.size());

        return TYCOON_RESOURCE_MATERIALS.get(randint);
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

        if (isActive){
            hologramData.addLine("Status: " + ChatColor.GREEN+ true + ChatColor.RESET);
        }else{
            hologramData.addLine("Status: " + ChatColor.RED + false + ChatColor.RESET);
        }

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
                if (isActive) {
                    hologramLines.set(2, "Status: " + ChatColor.GREEN + true + ChatColor.RESET);
                }else {
                    hologramLines.set(2, "Status: " + ChatColor.RED + false + ChatColor.RESET);
                }
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

    @Deprecated
    public void editHologram(Location location, int line, String text) {
        if (getHologram(location) != null) {
            Hologram hologram = getHologram(location);
            HologramData data = hologram.getData();
            //TextHologramData textHologramData = (TextHologramData) data;
            List<String> hologramLines = ((TextHologramData) data).getText();

            hologramLines.set(line, text);

            System.out.println("[TycoonBlockHolo] Editing hologram - " + hologram.getName());

            hologram.queueUpdate();
        }
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
    @Deprecated
    public void addTotalXp(int amount) {
        this.totalXp += amount;
        updateHologramPreset(location, "XP");
    }
    @Deprecated
    public void addLevelXp(int amount) {
        this.levelXp += amount;
    }
    @Deprecated
    public void levelUp(int leftoverXp){
        this.level++;
        this.levelXp = leftoverXp;
        updateHologramPreset(location, "LEVEL");
    }
    public void addActiveBlocks(Block block) {
        activeBlocks.add(block);
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
    @Deprecated
    public int getTotalXp() {
        return totalXp;
    }
    public double getProgress() {
        return progress;
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
    @Deprecated
    public Player getOwner() {
        if (owner.getPlayer() != null) return owner.getPlayer();
        else return null;
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
    public Material getLastSpawnedBlock() {
        return lastSpawnedBlock;
    }
    public int getTickCounter() {
        return tickCounter;
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
    @Deprecated
    public void setTotalXp(int totalXp) {
        this.totalXp = totalXp;
    }
    @Deprecated
    public void setProgress(double progress) {
        this.progress = progress;
    }
    @Deprecated
    public void setOwner(Player owner) {
        this.owner = owner;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
        updateHologramPreset(location, "STATUS");
    }
    public void setLastSpawnedBlock(Material lastSpawnedBlock) {
        this.lastSpawnedBlock = lastSpawnedBlock;
    }
    public void setTickCounter(int tickCounter) {
        this.tickCounter = tickCounter;
    }
    public void setSpawnInterval(int spawnInterval) {
        this.spawnInterval = spawnInterval;
    }
    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
    // ---------     Setter      ---------
}
