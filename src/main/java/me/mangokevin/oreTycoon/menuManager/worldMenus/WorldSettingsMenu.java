package me.mangokevin.oreTycoon.menuManager.worldMenus;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.menuManager.MenuInterface;
import me.mangokevin.oreTycoon.menuManager.MenuManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonWorlds.TycoonWorldManager;
import me.mangokevin.oreTycoon.utility.ParticleGenerator;
import me.mangokevin.oreTycoon.utility.ParticleManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.mvplugins.multiverse.core.world.WorldManager;

public class WorldSettingsMenu implements MenuInterface {
    private final OreTycoon plugin = OreTycoon.getInstance();
    private final TycoonWorldManager tycoonWorldManager = plugin.getTycoonWorldManager();
    private final WorldManager worldManager;
    private final ParticleGenerator particleGenerator = plugin.getParticleGenerator();
    private final ParticleManager particleManager = plugin.getParticleManager();

    private final String worldName;

    public WorldSettingsMenu(String worldName) {
        this.worldName = worldName;
        this.worldManager = plugin.getMultiverseCoreApi().getWorldManager();
    }

    @Override
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(new TycoonHolder(this), 54, "World Settings");
        refresh(player, inventory);
        player.openInventory(inventory);
    }

    @Override
    public void refresh(Player player, Inventory inventory) {
        MenuManager.addFiller(inventory, Material.GRAY_STAINED_GLASS_PANE);

        //22 World Item
        ItemStack worldItem = MenuManager.createWorldItem(worldName);
        inventory.setItem(22, worldItem);
        //37 Spawn Beacon Item
        ItemStack toggleSpawnBeaconItem = MenuManager.createItemstack(
                Material.BEACON,
                1,
                ChatColor.AQUA + "Show World Spawn",
                null,
                (particleManager.isBeaconActive()),
                true,
                true,
                "toggle_spawn_beacon"
        );
        inventory.setItem(37, toggleSpawnBeaconItem);
        //38 Delete Item
        ItemStack deleteItem = MenuManager.createItemstack(
                Material.BARRIER,
                1,
                ChatColor.RED + "DELETE WORLD!",
                null,
                false,
                true,
                true,
                "delete_world"
        );
        inventory.setItem(38, deleteItem);
        //40 Teleport Item
        ItemStack teleportItem = MenuManager.createItemstack(
                Material.ENDER_PEARL,
                1,
                ChatColor.DARK_PURPLE + "Teleport to island!",
                null,
                false,
                true,
                true,
                "teleport_player"
        );
        inventory.setItem(40, teleportItem);

        //42 SetWorldSpawn Item
        ItemStack setWorldSpawnItem = MenuManager.createItemstack(
                Material.RED_BED,
                1,
                ChatColor.RED + "Set World Spawn",
                null,
                false,
                true,
                true,
                "set_world_spawn"
        );
        inventory.setItem(42, setWorldSpawnItem);
    }

    @Override
    public void handleAction(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {return;}
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        String action = pdc.get(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING);

        switch (action) {
            case "teleport_player" -> {
                tycoonWorldManager.teleportToWorld(player, worldName);
            }
            case "delete_world" -> {
                tycoonWorldManager.deleteTycoonWorld(player ,worldName);
                new WorldsMenu(plugin).open(player);
            }
            case "set_world_spawn" -> {
                tycoonWorldManager.setWorldSpawn(player, worldName);
            }
            case "toggle_spawn_beacon" -> {
                //TODO: make it toggleable and last as long as it turned on
                particleManager.setBeaconActive(!particleManager.isBeaconActive());

                worldManager.getWorld(worldName)
                                .peek(world -> {
                                    particleManager.spawnBeaconBeam(world.getSpawnLocation());
                                });
                refresh(player, inventory);
            }
            case null, default -> {}
        }

    }
}
