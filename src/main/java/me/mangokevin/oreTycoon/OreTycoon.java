package me.mangokevin.oreTycoon;

import me.mangokevin.oreTycoon.commands.tycooncmds.TycoonCmd;
import me.mangokevin.oreTycoon.commands.tycooncmds.TycoonTabCompleter;
import me.mangokevin.oreTycoon.menuManager.InventoryClickListener;
import me.mangokevin.oreTycoon.menuManager.MenuManager;
import me.mangokevin.oreTycoon.tycoonListener.TycoonAutoMineListener;
import me.mangokevin.oreTycoon.levelManagment.LevelManager;
import me.mangokevin.oreTycoon.listener.*;
import me.mangokevin.oreTycoon.papiExpansion.PlaceholderExpansion;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlockManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import net.ess3.api.IEssentials;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mvplugins.multiverse.core.MultiverseCoreApi;

import java.util.Objects;

public final class OreTycoon extends JavaPlugin {

    private TycoonBlockManager blockManager;
    private TycoonData tdData;
    private MenuManager menuManager;
    private LevelManager levelManager;
    private static Economy econ = null;
    private static IEssentials essentials;

    public OreTycoon() {}



    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        this.levelManager = new LevelManager();
        this.blockManager = new TycoonBlockManager(this, levelManager);
        this.tdData = new TycoonData();
        this.menuManager = new MenuManager(this);
        TycoonData.init(this);


        // TODO: Generate Tycoon Islands with Preset
        RegisteredServiceProvider<MultiverseCoreApi> provider = Bukkit.getServicesManager().getRegistration(MultiverseCoreApi.class);
        if (provider != null) {
            @SuppressWarnings("unused")
            MultiverseCoreApi coreApi = provider.getProvider();
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderExpansion(blockManager).register();
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

        //-----------------------   Listeners & Commands    -----------------------
        getServer().getPluginManager().registerEvents(new BlockPlacedListener(this, blockManager, tdData), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this, blockManager, levelManager), this);
        getServer().getPluginManager().registerEvents(new BlockInteractListener(this, blockManager), this);
        getServer().getPluginManager().registerEvents(new TycoonManipulationListener(blockManager), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(),this);
        getServer().getPluginManager().registerEvents(new TycoonAutoMineListener(), this);
        Objects.requireNonNull(getCommand("tycoon")).setExecutor(new TycoonCmd(this, blockManager));
        Objects.requireNonNull(getCommand("tycoon")).setTabCompleter(new TycoonTabCompleter());
        //-----------------------   Listeners & Commands    -----------------------

        blockManager.loadTycoons();
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }
    public TycoonBlockManager getBlockManager() {
        return blockManager;
    }
    public LevelManager getLevelManager() {
        return levelManager;
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
        if (blockManager != null) {
            blockManager.saveTycoons();
        }
        // Plugin shutdown logic
    }


}
