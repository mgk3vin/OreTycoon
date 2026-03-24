package me.mangokevin.oreTycoon;

import me.mangokevin.oreTycoon.commands.tycooncmds.TycoonCmd;
import me.mangokevin.oreTycoon.commands.tycooncmds.TycoonTabCompleter;
import me.mangokevin.oreTycoon.listener.tycoonListener.*;
import me.mangokevin.oreTycoon.menuManager.InventoryClickListener;
import me.mangokevin.oreTycoon.menuManager.MenuManager;
import me.mangokevin.oreTycoon.levelManagment.LevelManager;
import me.mangokevin.oreTycoon.listener.*;
import me.mangokevin.oreTycoon.papiExpansion.PlaceholderExpansion;
import me.mangokevin.oreTycoon.scoreboard.ScoreBoardManager;
import me.mangokevin.oreTycoon.sqlite.DatabaseManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonManager;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonBlockFactory;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonRegistry;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonWorlds.TycoonWorldManager;
import me.mangokevin.oreTycoon.utility.Console;
import me.mangokevin.oreTycoon.utility.ParticleGenerator;
import me.mangokevin.oreTycoon.utility.ParticleManager;
import me.mangokevin.oreTycoon.worth.WorthManager;
import net.ess3.api.IEssentials;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.mvplugins.multiverse.core.MultiverseCoreApi;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class OreTycoon extends JavaPlugin {

    private TycoonManager tycoonManager;
    private TycoonRegistry tycoonRegistry;
    private TycoonBlockFactory tycoonFactory;
    private MenuManager menuManager;
    private LevelManager levelManager;
    private WorthManager worthManager;
    private MultiverseCoreApi multiverseCoreApi;
    private TycoonWorldManager tycoonWorldManager;
    private ParticleGenerator particleGenerator;
    private ParticleManager  particleManager;
    private ScoreBoardManager scoreboardManager;
    private DatabaseManager databaseManager;
    private static Economy econ = null;
    private static IEssentials essentials;

    private TycoonCmd tycoonCmd;

    private static OreTycoon instance;

    public OreTycoon() {}



    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        saveDefaultConfig();
        this.tycoonRegistry = new TycoonRegistry(this);

        this.tycoonFactory = new TycoonBlockFactory();
        this.tycoonManager = new TycoonManager(this);

        //========= DatabaseManager setup =========
        databaseManager = new DatabaseManager(this);
        //========= DatabaseManager setup =========
        //========= ScoreBoard setup =========
        scoreboardManager = new ScoreBoardManager();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    scoreboardManager.updateScoreboard(player);
                }
            }
        }.runTaskTimer(this, 0, 20L);
        //========= ScoreBoard setup =========

        //========= WorthManager setup =========
        this.worthManager = new WorthManager(this);

//        new BukkitRunnable() {
//
//            @Override
//            public void run() {
//                worthManager.updateStockMarket();
//                Bukkit.broadcastMessage(ChatColor.GOLD + "📊 Stock Market updated!");
//            }
//        }.runTaskTimer(this, 0, 20L * 60 * 10);
        //========= WorthManager setup =========

        this.levelManager = new LevelManager();
        this.menuManager = new MenuManager(this);
        TycoonData.init(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderExpansion(this).register();
            getLogger().info("PlaceholderAPI Expansion enabled!");
        }
        //-----------------------   Vault setup    -----------------------
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //-----------------------   Vault setup    -----------------------
        //-----------------------   Essentials setup    -----------------------
        Plugin essPlugin = Bukkit.getPluginManager().getPlugin("Essentials");

        if (essPlugin != null && essPlugin.isEnabled()) {
            essentials = (IEssentials) essPlugin;
            getLogger().info("EssentialsX erfolgreich verknüpft.");
        } else {
            getLogger().severe("EssentialsX wurde nicht gefunden! Preise können nicht berechnet werden.");
        }
        //-----------------------   Essentials setup    -----------------------
        this.particleGenerator = new ParticleGenerator();
        this.particleManager = new ParticleManager();
        //-----------------------   MultiverseCore setup    -----------------------
        multiverseCoreApi = MultiverseCoreApi.get();
        if (multiverseCoreApi == null){
            Console.error(getClass(), "MultiverseCoreAPI not found!");
        }
        Console.log(getClass(), "MultiverseCore loaded!");
        this.tycoonWorldManager = new TycoonWorldManager(this);
        //-----------------------   MultiverseCore setup    -----------------------

        //-----------------------   Listeners & Commands    -----------------------
        getServer().getPluginManager().registerEvents(new BlockPlacedListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new TycoonManipulationListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(),this);
        getServer().getPluginManager().registerEvents(new TycoonAutoMineListener(), this);
        getServer().getPluginManager().registerEvents(new StockMarketUpdatedListener(this), this);
        getServer().getPluginManager().registerEvents(new TycoonUpdateListener(this), this);
        getServer().getPluginManager().registerEvents(new TycoonBoosterTickedListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new TycoonSpawnedBlockMinedListener(), this);
        tycoonCmd = new TycoonCmd(this);
        Objects.requireNonNull(getCommand("tycoon")).setExecutor(tycoonCmd);
        Objects.requireNonNull(getCommand("tycoon")).setTabCompleter(new TycoonTabCompleter(this));
        //-----------------------   Listeners & Commands    -----------------------

        //blockManager.loadTycoons();
        databaseManager.loadTycoons();

        tycoonWorldManager.loadPlayerWorlds();

        databaseManager.startAutoSaveTimer();
    }



    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    public static Economy getEconomy() {
        return econ;
    }
    public static IEssentials getEssentials() {
        return essentials;
    }


    @Override
    public void onDisable() {
        for (TycoonBlock tycoonBlock : tycoonRegistry.getAllTycoons()){
            Set<Block> snapshot = new HashSet<>(tycoonBlock.getActiveBlocks());
            databaseManager.saveTycoon(tycoonBlock, snapshot);
        }
//        if (blockManager != null) {
//            blockManager.saveTycoons();
//            Console.log(getClass(), "BlockManager saved!");
//            if (databaseManager != null) {
//                for (TycoonBlock tycoonBlock : blockManager.getTycoonBlocks().values()) {
//                    databaseManager.saveTycoon(tycoonBlock);
//                }
//            }
//        }
        if (tycoonWorldManager != null) {
            tycoonWorldManager.savePlayerWorlds();
            Console.log(getClass(), "TycoonWorldManager saved!");
        }

        // Plugin shutdown logic
    }

    // Getter für andere Klassen
    public MenuManager getMenuManager() {
        return menuManager;
    }
    public LevelManager getLevelManager() {
        return levelManager;
    }
    public ParticleManager getParticleManager() {
        return particleManager;
    }
    public WorthManager getWorthManager() {
        return worthManager;
    }
    public MultiverseCoreApi getMultiverseCoreApi() {return multiverseCoreApi;}
    public static OreTycoon getInstance() {
        return instance;
    }
    public TycoonWorldManager getTycoonWorldManager() {
        return tycoonWorldManager;
    }
    public ParticleGenerator getParticleGenerator() {
        return particleGenerator;
    }
    public ScoreBoardManager getScoreboardManager() {
        return scoreboardManager;
    }
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    public TycoonRegistry getTycoonRegistry() {
        return tycoonRegistry;
    }
    public TycoonBlockFactory getTycoonFactory() {
        return tycoonFactory;
    }
    public TycoonManager getTycoonManager() {
        return tycoonManager;
    }
    public TycoonCmd getTycoonCmd() {
        return tycoonCmd;
    }
}
