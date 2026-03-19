package me.mangokevin.oreTycoon.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import me.mangokevin.oreTycoon.tycoonManagment.booster.*;
import me.mangokevin.oreTycoon.utility.Console;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TycoonBoosterMenu implements MenuInterface{
    private final TycoonBlock tycoonBlock;
    private final OreTycoon plugin;

    private BukkitRunnable animationTask;

    public TycoonBoosterMenu(TycoonBlock tycoonBlock, OreTycoon plugin) {
        this.tycoonBlock = tycoonBlock;
        this.plugin = plugin;
    }

    @Override
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(new TycoonHolder(this), 45, "Booster");
        refresh(player, inventory);
        player.openInventory(inventory);
    }

    @Override
    public void refresh(Player player, Inventory inventory) {
        MenuManager.addFiller(inventory, Material.GRAY_STAINED_GLASS_PANE, false);
        TycoonBoosterAbstract tycoonBoosterAbstract = tycoonBlock.isAnyBoosterActive();
        switch (tycoonBoosterAbstract) {
            case AutoMinerSpeedBooster autoMinerSpeedBooster -> {
                //MenuManager.addFiller(inventory, Material.BLUE_STAINED_GLASS_PANE);
                startAnimation(Material.BLUE_STAINED_GLASS_PANE, Material.LIGHT_BLUE_STAINED_GLASS_PANE, inventory);
            }
            case SellMultiplyBooster sellMultiplyBooster -> {
                startAnimation(Material.GREEN_STAINED_GLASS_PANE, Material.LIME_STAINED_GLASS_PANE, inventory);
            }
            case SpawnSpeedBooster spawnSpeedBooster -> {
                startAnimation(Material.MAGENTA_STAINED_GLASS_PANE, Material.PINK_STAINED_GLASS_PANE, inventory);
            }
            case null, default -> {
                stopAnimation();
                MenuManager.addFiller(inventory, Material.GRAY_STAINED_GLASS_PANE);
            }
        }


        if (tycoonBlock.getTycoonBoosterManager().isAutoMinerBoosterActive()) {
            ItemStack autoMinerBooster = tycoonBlock.getAutoMinerSpeedBooster().getItem();
            inventory.setItem(22, autoMinerBooster);
        } else if (tycoonBlock.getTycoonBoosterManager().isSellMultiplierBoosterActive()) {
            ItemStack sellMultiplierBooster = tycoonBlock.getSellMultiplierBooster().getItem();
            inventory.setItem(22, sellMultiplierBooster);
        }else if (tycoonBlock.getTycoonBoosterManager().isSpawnSpeedBoosterActive()){
            ItemStack spawnSpeedBooster = tycoonBlock.getSpawnSpeedBooster().getItem();
            inventory.setItem(22, spawnSpeedBooster);
        }
        else {
            ItemStack autoMinerBooster = MenuManager.createItemstack(Material.SCULK_VEIN,
                    1,
                    ChatColor.DARK_PURPLE + "No active Booster...",
                    null,
                    false,
                    true,
                    true,
                    "empty_booster_slot");
            inventory.setItem(22, autoMinerBooster);
        }

        ItemStack returnButton = MenuManager.createItemstack(
                Material.BARRIER,
                1,
                ChatColor.RED + "Back to Stats Menu",
                null,
                false,
                true,
                true,
                "return"
        );
        inventory.setItem(40, returnButton);

    }

    @Override
    public void handleAction(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        ItemStack holdingItem = event.getCursor();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        String action = pdc.get(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING);


        switch (action) {
            //Clicked a tycoon Booster item
            case "tycoon_booster_item":
                if (tycoonBlock.isAnyBoosterActive() != null) {
                    player.sendMessage(ChatColor.DARK_PURPLE + "Booster is already active.");
                    return;
                }
                String uid = pdc.get(TycoonData.BOOSTER_ID_KEY, PersistentDataType.STRING);
                double value = pdc.getOrDefault(TycoonData.BOOSTER_VALUE_KEY, PersistentDataType.DOUBLE, 0.0);
                long duration = pdc.getOrDefault(TycoonData.BOOSTER_DURATION_KEY, PersistentDataType.LONG, 0L);

                TycoonBoosterAbstract tycoonBooster = BoosterRegistry.createBooster(uid, value, duration);
                Console.debug(getClass(), "Booster created: " + uid + " | " + value + " | " + duration);

                if (tycoonBooster != null) {
                    if (tycoonBooster.getUID().equals("auto_miner_booster") && !tycoonBlock.getTycoonUpgrades().isAutoMinerUnlocked()) {
                        //Auto Miner Locked
                        player.sendMessage(ChatColor.RED + "Can't activate " + tycoonBooster.getDisplayName() + ChatColor.RED + " when Auto Miner is still locked!");
                        return;
                    }
                    tycoonBooster.onApply(tycoonBlock);
                    Console.debug(getClass(), "Starting apply logic from tycoon Booster");
                } else {
                    Console.debug(getClass(), "Booster not created");
                }
                ItemStack menuBoosterItem = item.clone();
                item.setAmount(item.getAmount() - 1);

                menuBoosterItem.setAmount(1);
                inventory.setItem(22, menuBoosterItem);
                player.updateInventory();
                break;
            case "return":
                new StatsMenu(tycoonBlock, plugin).open(player);
                break;
            case null, default:
                break;

        }
    }
    @Override
    public void onClose(Player player) {
        stopAnimation();
    }
    public TycoonBlock getTycoonBlock() {
        return tycoonBlock;
    }
    private boolean isBooster(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String action = pdc.get(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING);
        if (action == null) return false;
        return action.equals("tycoon_booster_item");
    }

    private void startAnimation(Material fillerItem, Material glowItem, Inventory inventory) {
        if (animationTask != null && !animationTask.isCancelled()) return;
        int size = inventory.getSize();
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            slots.add(i);
        }
        for (int i = 8; i < size; i += 9) {
            slots.add(i);
        }
        for (int i = size - 1; i > size - 9; i--) {
            slots.add(i);
        }
        for (int i = size - 9; i > 0; i -= 9) {
            slots.add(i);
        }
        slots.remove(Integer.valueOf(40));

        animationTask = new BukkitRunnable() {
            int step = 0;

            @Override
            public void run() {
                // Reset previous slot
                int prevSlot = slots.get((step - 1 + slots.size()) % slots.size());
                inventory.setItem(prevSlot, new ItemStack(fillerItem));

                // Let current slot glow
                inventory.setItem(slots.get(step), new ItemStack(glowItem));

                step = (step + 1) % slots.size(); // returns 0 when finished
            }
        };
        animationTask.runTaskTimer(plugin, 0L, 2L);

    }
    private void stopAnimation() {
        if (animationTask != null && !animationTask.isCancelled()) {
            animationTask.cancel();
            animationTask = null;
        }
    }
}
