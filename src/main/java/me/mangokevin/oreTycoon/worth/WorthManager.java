package me.mangokevin.oreTycoon.worth;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.events.tycoonEvents.StockMarketUpdatedEvent;
import me.mangokevin.oreTycoon.utility.Console;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WorthManager {
    private final OreTycoon plugin;
    private final File worthFile;
    private final File stockMarketFile;

    // Konfigurationen (geladen aus YAML)
    private YamlConfiguration worthConfig;
    private YamlConfiguration stockConfig;

    private final Map<Material, Double> baseWorthCache = new HashMap<>();
    private final Map<Material, Double> multiplierCache = new HashMap<>();

    private final double maxPriceChange = 0.2;

    Random rand = new Random();

    // ========== NEUE VARIABLEN FÜR BOSSBAR ==========
    private BossBar updateTimerBar;  // Die BossBar
    private long lastUpdateTime = 0;  // Wann war der letzte Update?
    private final long UPDATE_INTERVAL = 1000 * 60 * 2; //1000ms = 1s * 60 = 1m
    // ========== NEUE VARIABLEN FÜR BOSSBAR ==========


    public WorthManager(OreTycoon plugin) {
        this.plugin = plugin;
        this.worthFile = new File(plugin.getDataFolder(), "worth.yml");
        this.stockMarketFile = new File(plugin.getDataFolder(), "stock-market.yml");

        createDefaultFiles();
        loadWorthConfig();
        loadStockMarketConfig();

        createBossBar();
        startUpdateTimer();
    }

    private void createBossBar() {
        updateTimerBar = Bukkit.createBossBar(
                ChatColor.GREEN + "📊 Stock Market Update: --:--:--",
                BarColor.GREEN,
                BarStyle.SOLID
        );

        updateTimerBar.setVisible(true);

        for (Player player : Bukkit.getOnlinePlayers()) {
            updateTimerBar.addPlayer(player);
        }

        Console.log(getClass(), "BossBar has been created.");
    }

    private void startUpdateTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long timeSinceLastUpdate = currentTime - lastUpdateTime;
                long timeUntilNextUpdate = UPDATE_INTERVAL - timeSinceLastUpdate;

                if (timeUntilNextUpdate < 0) {
                    updateStockMarket();
                    lastUpdateTime = System.currentTimeMillis();
                    timeUntilNextUpdate = UPDATE_INTERVAL;
                }

                double progress = (double) (UPDATE_INTERVAL - timeUntilNextUpdate) / UPDATE_INTERVAL;
                updateTimerBar.setProgress(progress);

                String timeString = formatTimeRemaining(timeUntilNextUpdate);

                updateTimerBar.setTitle("📊 Stock Market Update in: " + timeString);
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private String formatTimeRemaining(long miliseconds) {
        long seconds = miliseconds / 1000;

        // Berechne Stunden, Minuten, Sekunden
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        // Formatiere als HH:MM:SS
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
        // %02d = Mit Nullen auffüllen (z.B. "05" statt "5")
    }

    public void addPlayerToTimerBar(Player player) {
        if (updateTimerBar != null) {
            updateTimerBar.addPlayer(player);
        }
    }

    public void removePlayerFromTimerBar(Player player) {
        if (updateTimerBar != null) {
            updateTimerBar.removePlayer(player);
        }
    }

    public double getWorth(Material material) {
        if (!baseWorthCache.containsKey(material)) {
            return 0.0;
        }

        double baseWorth = baseWorthCache.get(material);

        double multiplier = multiplierCache.get(material);

        double result = baseWorth * multiplier;

        return Math.round(result * 100.0) / 100.0;
    }

    public void updateStockMarket() {
        Console.log(getClass(), "Updating prices...");

        for (Material material : baseWorthCache.keySet()) {

            double change = rand.nextDouble(-maxPriceChange, maxPriceChange);
            double currentMultiplier = multiplierCache.getOrDefault(material, 1.0);

            double newMultiplier = change + currentMultiplier;

            newMultiplier = Math.max(0.2, Math.min(2.5, newMultiplier));

            multiplierCache.put(material, newMultiplier);

//            Console.debug(getClass(), material.name() + ": " +
//                    String.format("%.2f", currentMultiplier) + "x → " +
//                    String.format("%.2f", newMultiplier) + "x");
        }

        Bukkit.getPluginManager().callEvent(new StockMarketUpdatedEvent());
        saveStockMarketConfig();
        Console.log(getClass() ,"Prices have been updated!");

        updateTimerBar.setTitle("📊 Stock Market UPDATE! New prices!");
        updateTimerBar.setProgress(1.0);

        Bukkit.broadcastMessage(
                ChatColor.GREEN +
                        "[>] Stock Market updated! View new prices /tycoon stockmarket"
        );
    }

    private void saveStockMarketConfig() {
        try {
            stockConfig.set("last_update", System.currentTimeMillis());

            for (Material material : multiplierCache.keySet()) {
                stockConfig.set("market_multipliers." + material.name(),
                        Math.round(multiplierCache.get(material) * 100.0) / 100.0);
            }

            for (Material material : multiplierCache.keySet()) {
                stockConfig.set("trends." + material.name(), getTrendEmoji(material));
            }

            stockConfig.save(stockMarketFile);
        } catch (IOException e) {
            Console.error(getClass(),"Could not save stock market config!");
            throw new RuntimeException(e);
        }
    }

    private void createDefaultFiles(){
        if(!worthFile.exists()){
            plugin.saveResource("worth.yml", false);
        }

        if(!stockMarketFile.exists()){
            try{
                stockMarketFile.createNewFile();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadWorthConfig(){
        worthConfig = YamlConfiguration.loadConfiguration(worthFile);

        baseWorthCache.clear();

        if(worthConfig.contains("materials")){
            for(String key : worthConfig.getConfigurationSection("materials").getKeys(false)) {

                try {

                    Material material = Material.valueOf(key.toUpperCase());

                    double worth = worthConfig.getDouble("materials." + key + ".base_worth");

                    baseWorthCache.put(material, worth);

                    Console.debug(getClass() ,"Loaded: " + key + " = $" + worth);

                } catch (IllegalArgumentException e) {
                    Console.error(getClass() ,"Invalid material key: " + key);
                }

                Console.log(getClass() ,"Config loaded! (" + baseWorthCache.size() + " Materials)");
            }
        }
    }

    private void loadStockMarketConfig(){
        stockConfig = YamlConfiguration.loadConfiguration(stockMarketFile);
        multiplierCache.clear();

        if(stockConfig.contains("market_multipliers")){
            for(String key : stockConfig.getConfigurationSection("market_multipliers").getKeys(false)) {

                try {
                    Material material = Material.valueOf(key.toUpperCase());

                    double multiplier = stockConfig.getDouble("market_multipliers." + key);

                    multiplierCache.put(material, multiplier);
                }catch (IllegalArgumentException e) {
                    Console.error(getClass() ,"Invalid multiplier key: " + key);
                }
            }
        }
        for (Material material : baseWorthCache.keySet()) {
            multiplierCache.putIfAbsent(material, 1.0);
        }
        Console.log(getClass() ,"Config loaded! (" + multiplierCache.size() + " Materials)");
    }

    private String getTrendEmoji(Material material){
        double multiplier = multiplierCache.get(material);

        if(multiplier >= 1.5) {
            return "📈 Trending!";
        } else if (multiplier >= 1.1) {
            return "📈 Rising";
        } else if (multiplier >= 0.9) {
            return "➡️ Stable";
        } else if (multiplier >= 0.7 ) {
            return "📉 Sinking";
        } else if (multiplier < 0.7) {
            return "📉 CRASH!";
        }

        return "➡️ Stable";
    }
    public String getTrend(Material material){
        double multiplier = multiplierCache.get(material);

        if(multiplier >= 1.5) {
            return "Trending";
        } else if (multiplier >= 1.1) {
            return "Rising";
        } else if (multiplier >= 0.9) {
            return "Stable";
        } else if (multiplier >= 0.7 ) {
            return "Sinking";
        } else if (multiplier < 0.7) {
            return "Crashing";
        }

        return "Stable";
    }
    //========== Getter ==========
    public double getBaseWorth(Material material){
        return baseWorthCache.getOrDefault(material, 0.0);
    }
    public double getMultiplier(Material material){
        return multiplierCache.getOrDefault(material, 0.0);
    }
    public Map<Material, Double> getAllWorths() {
        return new  HashMap<>(baseWorthCache);
    }
    //========== Getter ==========
}
