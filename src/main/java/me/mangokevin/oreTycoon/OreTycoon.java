package me.mangokevin.oreTycoon;

import me.mangokevin.oreTycoon.commands.TycoonCmd;
import me.mangokevin.oreTycoon.levelManagment.LevelManager;
import me.mangokevin.oreTycoon.listener.BlockBreakListener;
import me.mangokevin.oreTycoon.listener.BlockInteractListener;
import me.mangokevin.oreTycoon.listener.BlockPlacedListener;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlockManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mvplugins.multiverse.core.MultiverseCoreApi;

public final class OreTycoon extends JavaPlugin {

    private TycoonBlockManager blockManager;
    private LevelManager  levelManager;

    public OreTycoon() {

    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        this.blockManager = new TycoonBlockManager(this, levelManager);
        this.levelManager = new LevelManager();

        blockManager.loadTycoons();

        RegisteredServiceProvider<MultiverseCoreApi> provider = Bukkit.getServicesManager().getRegistration(MultiverseCoreApi.class);
        if (provider != null) {
            MultiverseCoreApi coreApi = provider.getProvider();
        }

        //-----------------------   Listeners & Commands    -----------------------
        getServer().getPluginManager().registerEvents(new BlockPlacedListener(this, blockManager), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this, blockManager), this);
        getServer().getPluginManager().registerEvents(new BlockInteractListener(this, blockManager), this);
        getCommand("tycoon").setExecutor(new TycoonCmd(this, blockManager));
        //-----------------------   Listeners & Commands    -----------------------
    }

    @Override
    public void onDisable() {
        if (blockManager != null) {
            blockManager.saveTycoons();
        }
        // Plugin shutdown logic
    }
}
