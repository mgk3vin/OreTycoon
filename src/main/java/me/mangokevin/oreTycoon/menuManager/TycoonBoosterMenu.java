package me.mangokevin.oreTycoon.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBooster;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import me.mangokevin.oreTycoon.utility.Console;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class TycoonBoosterMenu implements MenuInterface{
    private final TycoonBlock tycoonBlock;
    private final OreTycoon plugin;

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
        MenuManager.addFiller(inventory, Material.GRAY_STAINED_GLASS_PANE);

        if (tycoonBlock.getTycoonBooster().isAutoMinerBoosterActive()) {
            ItemStack autoMinerBooster = new TycoonBooster().createAutoMinerBooster(1);
            inventory.setItem(22, autoMinerBooster);
        } else if (tycoonBlock.getTycoonBooster().isSellMultiplierBoosterActive()) {
            ItemStack sellMultiplierBooster = new TycoonBooster().createSellMultiplierBooster(1);
            inventory.setItem(22, sellMultiplierBooster);
        } else {
            ItemStack autoMinerBooster = MenuManager.createItemstack(Material.SCULK_VEIN,
                    1,
                    ChatColor.DARK_PURPLE + "No active Booster...",
                    null,
                    false,
                    true,
                    "empty_booster_slot");
            inventory.setItem(22, autoMinerBooster);
        }

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
            case "tycoon_booster_item":
                if (tycoonBlock.getTycoonBooster().isAutoMinerBoosterActive() || tycoonBlock.getTycoonBooster().isSellMultiplierBoosterActive()) {
                    player.sendMessage(ChatColor.DARK_PURPLE + "Booster is already active.");
                    return;
                }
                ItemStack menuBoosterItem = item.clone();
                item.setAmount(item.getAmount() - 1);

                //Activate Booster on Tycoon
                tycoonBlock.getTycoonBooster().setSellMultiplierBoosterActive(true);
                tycoonBlock.updateAttributes();
                Console.debug("Booster item has been updated" + "Booster: " + tycoonBlock.getTycoonBooster().getSellMultiplierBoost() + " Tycoon Boost: " + tycoonBlock.getSellMultiplier());

                ItemMeta boosterSlotMeta = menuBoosterItem.getItemMeta();
                if (boosterSlotMeta != null) {
                    boosterSlotMeta.setLore(Arrays.asList(
                            ChatColor.GRAY + "Booster: " + ChatColor.GREEN + "ACTIVE",
                            ChatColor.GRAY + "Boost: ???",
                            ChatColor.GRAY + "Duration: ???"
                    ));
                }
                menuBoosterItem.setItemMeta(boosterSlotMeta);
                menuBoosterItem.setAmount(1);
                inventory.setItem(22, menuBoosterItem);
                player.updateInventory();
                break;
            case "empty_booster_slot":
                ItemMeta holdingItemMeta = holdingItem.getItemMeta();
                if (holdingItemMeta != null) {
                    PersistentDataContainer boosterPdc = holdingItemMeta.getPersistentDataContainer();
                    String boosterAction = boosterPdc.get(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING);
                    switch (boosterAction) {
                        case "tycoon_booster_item":
                            if (holdingItem.getAmount() > 1) {
                                Console.debug("[TycoonBoosterMenu] Setting booster amount from " + holdingItem.getAmount() + " to " + (holdingItem.getAmount() - 1));
                                holdingItem.setAmount(holdingItem.getAmount() - 1);
                            } else {
                                event.getWhoClicked().setItemOnCursor(null); // Cursor leeren
                                Console.debug("[TycoonBoosterMenu] Setting booster amount to 0");
                            }
                            break;
                        case null, default:
                            break;
                    }
                }
                break;
            case null, default:
                break;
        }
    }
    private boolean isBooster(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String action = pdc.get(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING);
        if (action == null) return false;
        return action.equals("tycoon_booster_item");
    }
}
