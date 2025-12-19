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
    private Player testowner;
    private OfflinePlayer owner;
    private final UUID ownerUuid;

    private int level;
    private int totalxp;
    private int levelxp;
    private double progress;

    private boolean isActive;
    private Material lastSpawnedBlock;
    private int tickCounter = 0;
    private int spawnInterval;
    private final String blockUID;

    Hologram statsHologram;
    private String hologramUID;
    Block block;
    public static final Map<Location, String> hologramMap = new HashMap<>();
    private Map<Location, Block> spawnedBlocksMap = new HashMap<>();

    private final Set<Block> activeOres = new HashSet<>();

    HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

    private final OreTycoon plugin;
    private final LevelManager levelManager;


    private final List<Material> TYCOON_RESOURCE_MATERIALS = Arrays.asList(
            Material.COAL_ORE,
            Material.IRON_ORE,
            Material.GOLD_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE
            // Fügen Sie hier weitere Materialien hinzu
    );

    public TycoonBlock(Location location, UUID ownerUuid, boolean isActive, int spawnInterval, OreTycoon plugin, LevelManager levelManager) {
        this.location = location;
        this.block = location.getBlock();
        this.ownerUuid = ownerUuid;
        this.owner = Bukkit.getOfflinePlayer(ownerUuid);
        this.isActive = isActive;
        this.plugin = plugin;
        this.levelManager = levelManager;
        level = 1;
        totalxp = 0;
        levelxp = 0;
        progress = 0;

        this.spawnInterval = spawnInterval;

        this.blockUID = this.location.getWorld().getName() + "_" +
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
    public void trySpawnRessource() {
        Player player = owner.getPlayer();
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
            Material material = getRandomMaterial();
            spawnBlock.setType(material);
            //Gespawnten Block merken!
            //spawnedBlocksMap.put(spawnBlock.getLocation(), spawnBlock);
            activeOres.add(spawnBlock);

            spawnBlock.setMetadata("tycoon_id", new FixedMetadataValue(plugin, blockUID));

            //tycoonBlock.manipulateHologram(tycoonBlock.getLocation(), material.name());
            setLastSpawnedBlock(material);
            updateHologramPreset(getLocation(), "BLOCK");
            world.playSound(randomLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.5f);
            world.spawnParticle(Particle.EXPLOSION, randomLocation, 1);
        }
    }
    private Material getRandomMaterial() {
        Random rand = new Random();
        int randint = rand.nextInt(0, TYCOON_RESOURCE_MATERIALS.size());

        return TYCOON_RESOURCE_MATERIALS.get(randint);
    }

    public boolean containsBlock(Block block) {
        return activeOres.contains(block);
    }
    public void removeBlock(Block block) {
        activeOres.remove(block);
    }

    // ---------     BlockHologram      ---------
    public void createHologram() {
        Location l = new Location(block.getWorld(), block.getX()+0.5, block.getY()+1.5, block.getZ()+0.5);

        hologramUID = "TycoonBlockHolo_" + l.getBlockX() + "_" + l.getBlockY() + "_" + l.getBlockZ() + "_" + getOwnerName();

        TextHologramData hologramData = new TextHologramData(hologramUID, l);

        List<String> name = new LinkedList<>();
        name.add("---  " + getOwnerName() + "'s Tycoon ---");
        hologramData.setText(name);

        hologramData.addLine("Block: " + lastSpawnedBlock);
        hologramData.addLine("Status: " + isActive);
        hologramData.addLine("Level: " + level);
        hologramData.addLine("xp: " + levelxp + "/" + levelManager.getXpNeededForLevel(level + 1) + " | " + levelManager.getProgressPercentage(levelxp, level + 1));
//        hologramData.addLine("X: " + block.getX());
//        hologramData.addLine("Y: " + block.getY());
//        hologramData.addLine("Z: " + block.getZ());

        hologramData.setBackground(Color.fromARGB(0));
        hologramData.setPersistent(false);
        de.oliver.fancyholograms.api.hologram.Hologram hologram = manager.create(hologramData);
        manager.addHologram(hologram);


        hologram.queueUpdate();
        hologramMap.put(location, hologramUID);
    }
    public void removeHologram(Location location) {
//            String uniqueId = hologramMap.get(location);
//
//
//            Hologram hologram = manager.getHologram(uniqueId).orElse(null);
//            if (hologram != null) {
//
//            }else{
//                System.out.println("Hologram with id " + uniqueId + " not found");
//            }
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

        HologramData data = hologram.getData();
        //TextHologramData textHologramData = (TextHologramData) data;
        List<String> hologramLines = ((TextHologramData) data).getText();

        if (hologram == null) {
            return;
        }
        switch (preset) {
            case "BLOCK":
                hologramLines.set(1, "Block: " + lastSpawnedBlock);
                break;
            case "STATUS":
                if (isActive) {
                    hologramLines.set(2, "Status: " + ChatColor.GREEN + isActive + ChatColor.RESET);
                }else {
                    hologramLines.set(2, "Status: " + ChatColor.RED + isActive + ChatColor.RESET);
                }
                break;
            case "LEVEL":
                hologramLines.set(3, "Level: " + level);
                break;
            case "XP":
                hologramLines.set(4, "xp: " + levelxp + "/" + levelManager.getXpNeededForLevel(level + 1) + " | " + levelManager.getProgressPercentage(levelxp, level + 1));
                break;
            default:
                break;
        }
        updateHologram(location);
    }
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
    // ---------     BlockHologram      ---------


    // ---------     Adder      ---------
    public void addTotalXp(int amount) {
        this.totalxp += amount;
        updateHologramPreset(location, "XP");
    }
    public void addLevelxp(int amount) {
        this.levelxp += amount;
    }
    @Deprecated
    public void levelUp(int leftoverXp){
        this.level++;
        this.levelxp = leftoverXp;
        updateHologramPreset(location, "LEVEL");
    }
    // ---------     Adder      ---------
    // ---------     Getter      ---------

    public int getLevelxp(){
        return levelxp;
    }
    public String getBlockUID(){
        return blockUID;
    }
    public int getLevel() {
        return level;
    }
    public int getTotalxp() {
        return totalxp;
    }
    public double getProgress() {
        return progress;
    }
    public Location getLocation() {
        return location;
    }
    public OfflinePlayer getOfflineOwner() {
        return owner;
    }
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
    // ---------     Getter      ---------

    // ---------     Setter      ---------
    public void setLevel(int level) {
        this.level = level;
    }
    public void setLevelxp(int levelxp) {
        this.levelxp = levelxp;
    }
    public void setTotalxp(int totalxp) {
        this.totalxp = totalxp;
    }
    public void setProgress(double progress) {
        this.progress = progress;
    }
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
    // ---------     Setter      ---------
}
