package me.mangokevin.oreTycoon;

import me.mangokevin.oreTycoon.commands.tycooncmds.TycoonCmd;
import me.mangokevin.oreTycoon.commands.tycooncmds.TycoonTabCompleter;
import me.mangokevin.oreTycoon.commands.tycooncmds.menuManager.MenuListener;
import me.mangokevin.oreTycoon.commands.tycooncmds.menuManager.MenuManager;
import me.mangokevin.oreTycoon.commands.tycooncmds.toggle_selected;
import me.mangokevin.oreTycoon.levelManagment.LevelManager;
import me.mangokevin.oreTycoon.listener.*;
import me.mangokevin.oreTycoon.papiExpansion.PlaceholderExpansion;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlockManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mvplugins.multiverse.core.MultiverseCoreApi;

import java.util.Objects;

public final class OreTycoon extends JavaPlugin {

    private TycoonBlockManager blockManager;
    private TycoonData tdData;
    private MenuManager menuManager;

    public OreTycoon() {}



    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        LevelManager levelManager = new LevelManager();
        this.blockManager = new TycoonBlockManager(this, levelManager);
        this.tdData = new TycoonData();
        this.menuManager = new MenuManager(this ,blockManager);
        TycoonData.init(this);

        blockManager.loadTycoons();

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

        //-----------------------   Listeners & Commands    -----------------------
        getServer().getPluginManager().registerEvents(new BlockPlacedListener(this, blockManager, tdData), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this, blockManager, levelManager), this);
        getServer().getPluginManager().registerEvents(new BlockInteractListener(this, blockManager), this);
        getServer().getPluginManager().registerEvents(new TycoonManipulationListener(blockManager), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        Objects.requireNonNull(getCommand("tycoon")).setExecutor(new TycoonCmd(this, blockManager));
        Objects.requireNonNull(getCommand("tycoon")).setTabCompleter(new TycoonTabCompleter());
        //-----------------------   Listeners & Commands    -----------------------


    }

    public MenuManager getMenuManager() {
        return menuManager;
    }
    public TycoonBlockManager getBlockManager() {
        return blockManager;
    }

    @Override
    public void onDisable() {
        if (blockManager != null) {
            blockManager.saveTycoons();
        }
        // Plugin shutdown logic
    }


}
