package me.mangokevin.oreTycoon.menuManager.worldMenus;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.menuManager.MenuInterface;
import me.mangokevin.oreTycoon.menuManager.MenuManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonWorlds.TycoonWorldManager;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonWorlds.WorldSettings;
import me.mangokevin.oreTycoon.utility.ParticleGenerator;
import me.mangokevin.oreTycoon.utility.ParticleManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.mvplugins.multiverse.core.world.WorldManager;

import java.util.Arrays;
import java.util.List;
import java.util.logging.LogRecord;

public class WorldSettingsMenu implements MenuInterface {
    private final OreTycoon plugin = OreTycoon.getInstance();
    private final TycoonWorldManager tycoonWorldManager = plugin.getTycoonWorldManager();
    private final WorldManager worldManager;
    private final ParticleGenerator particleGenerator = plugin.getParticleGenerator();
    private final ParticleManager particleManager = plugin.getParticleManager();

    private WorldSettings worldSettings;

    private final String worldName;

    public WorldSettingsMenu(String worldName) {
        this.worldName = worldName;
        this.worldManager = plugin.getMultiverseCoreApi().getWorldManager();
        this.worldSettings = tycoonWorldManager.getWorldSettings(worldName);
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
        ItemStack worldItem = MenuManager.createWorldItem(worldName, tycoonWorldManager);
        inventory.setItem(22, worldItem);
        //37 Spawn Beacon Item
        ItemStack toggleSpawnBeaconItem = MenuManager.createItemstack(
                Material.BEACON,
                1,
                (worldSettings.isSpawnBeaconActive() ? ChatColor.RED + "Hide World Spawn" : ChatColor.GREEN + "Show World Spawn"),
                null,
                worldSettings.isSpawnBeaconActive(),
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

        //43 WorldIcon Item
        List<String> worldIconLore = Arrays.asList(
                "§8§m-----------------------",
                ChatColor.GRAY + "Click to set the icon",
                ChatColor.GRAY + "to the item in your hand!",
                "§8§m-----------------------"
        );
        ItemStack worldIconItem = MenuManager.createItemstack(
                Material.CHEST,
                1,
                ChatColor.GOLD + "Click to Change Icon",
                worldIconLore,
                false,
                true,
                true,
                "change_icon"
        );
        inventory.setItem(43, worldIconItem);
        //53 Back to main menu Item

        ItemStack returnItem = MenuManager.createItemstack(
                Material.OAK_DOOR,
                1,
                ChatColor.RED + "<- Back to main Menu",
                null,
                false,
                true,
                true,
                "return"
        );
        inventory.setItem(53, returnItem);
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
            case "change_icon" -> {
                ItemStack icon = player.getInventory().getItemInMainHand();
                if (icon.getType().equals(Material.AIR)) {
                    player.sendMessage(ChatColor.RED + "Invalid item!");
                    return;
                }

                worldSettings.setWorldItem(icon.getType());
                refresh(player, inventory);
                player.sendMessage(ChatColor.GREEN+ "Changed World Icon to " + icon.getType().name());
            }
            case "teleport_player" -> {
                tycoonWorldManager.teleportToWorld(player, worldName);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_TELEPORT, 1, 1);
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
                boolean newState = !worldSettings.isSpawnBeaconActive();
                worldSettings.setSpawnBeacon(newState);

                if (newState) {
                    worldManager.getWorld(worldName)
                            .peek(world -> {
                                particleManager.startBeacon(worldName ,world.getSpawnLocation());
                            });
                } else {
                    particleManager.stopBeacon(worldName);
                }
                refresh(player, inventory);
            }
            case "return" -> {
                new WorldsMenu(plugin).open(player);
            }
            case null, default -> {}
        }

    }
}
