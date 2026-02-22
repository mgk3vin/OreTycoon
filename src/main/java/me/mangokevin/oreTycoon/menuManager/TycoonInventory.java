package me.mangokevin.oreTycoon.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.worth.PriceUtility;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.worth.WorthManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class TycoonInventory implements MenuInterface {

    private final TycoonBlock tycoonBlock;
    private final OreTycoon plugin;
    private final WorthManager worthManager;

    private static final Map<UUID, InventoryMode> playerInventoryMode = new HashMap<>();

    public TycoonInventory(TycoonBlock tycoonBlock, OreTycoon plugin) {
        this.tycoonBlock = tycoonBlock;
        this.plugin = plugin;
        this.worthManager = plugin.getWorthManager();
    }

    @Override
    public void open(Player player) {
        Inventory inventory = tycoonBlock.getInventory();

        playerInventoryMode.putIfAbsent(player.getUniqueId(), InventoryMode.SELL_MODE);

        refresh(player, inventory);
        player.openInventory(inventory);
    }

    @Override
    public void refresh(Player player, Inventory inventory) {
        //Filler items for bottom row only
        for (int i = 27; i < 36; i++) {
            ItemStack item = MenuManager.createItemstack(
                    Material.GRAY_STAINED_GLASS_PANE,
                    1,
                    " ",
                    null,
                    false,
                    true,
                    true,
                    "filler_item"
            );
            inventory.setItem(i, item);
        }
        //Inventory Mode Button 31
        InventoryMode mode = playerInventoryMode.getOrDefault(player.getUniqueId(), InventoryMode.SELL_MODE);
        ItemStack invModeItem = getModeItem(mode, inventory);
        inventory.setItem(31, invModeItem);

        ItemStack backToMenuItem = MenuManager.createItemstack(
                Material.BARRIER,
                1,
                ChatColor.RED + "<- Back",
                null,
                false,
                true,
                true,
                "return_item"
        );
        inventory.setItem(35, backToMenuItem);

    }


    public boolean addItem(ItemStack item){

        Inventory inv = tycoonBlock.getInventory();
        if (!(tycoonBlock.canFitItem(inv, item))) {return false;}

        // --- Formatt the item Worth ---
        setInventoryItemMeta(item);
        // --- Formatt the item Worth ---

        for (int i = 0; i < 27; i++){
            ItemStack slotItem = inv.getItem(i);

            // 1. Slot ist leer
            if (slotItem == null || slotItem.getType() == Material.AIR) {
                inv.setItem(i, item);
                return true;
            }

            // 2. Slot hat das gleiche Item und noch Platz
            if (slotItem.getType().equals(item.getType())) {
                int canAdd = slotItem.getMaxStackSize() - slotItem.getAmount();
                if (canAdd >= item.getAmount()) {
                    // Es ist noch platz im stack
                    slotItem.setAmount(slotItem.getAmount() + item.getAmount());

                    // --- Formatt the item Worth and set pdc ---
                    setInventoryItemMeta(item);
                    // --- Formatt the item Worth and set pdc ---

                    return true;
                } else if (canAdd > 0) {
                    // Teilweise füllen und Rest weitersuchen (optional)
                    slotItem.setAmount(slotItem.getMaxStackSize());
                    item.setAmount(item.getAmount() - canAdd);
                }
            }
        }
        return false;
    }

    @Override
    public void handleAction(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        ItemStack item = event.getCurrentItem();

        ClickType clickType = event.getClick();
        InventoryMode mode = playerInventoryMode.get(player.getUniqueId());

        if (item == null|| item.getType() == Material.AIR) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String action = pdc.get(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING);

        final var shiftClick = clickType.equals(ClickType.SHIFT_RIGHT) || clickType.equals(ClickType.SHIFT_LEFT);
        switch (action) {
            case "inventory_item" -> {
                if (mode.equals(InventoryMode.DROP_MODE)) {
                    if (shiftClick) {
                        tycoonBlock.dropItem(item, player);
                        item.setAmount(0);
                    } else {
                        ItemStack droppedItem = new ItemStack(item.getType(), 1);
                        tycoonBlock.dropItem(droppedItem, player);
                        item.setAmount(item.getAmount() - 1);
                    }
                }
                refresh(player, inventory);
            }
            case "drop_mode" -> {
                if (shiftClick) {
                    InventoryMode nextMode = mode.nextMode();
                    playerInventoryMode.put(player.getUniqueId(), nextMode);
                    player.sendMessage(ChatColor.GRAY + "Changing inventory mode to " + nextMode.getDisplayName());
                }
                refresh(player, inventory);
            }
            case "sell_item"-> {
                if (shiftClick) {
                    InventoryMode nextMode = mode.nextMode();
                    playerInventoryMode.put(player.getUniqueId(), nextMode);
                    player.sendMessage(ChatColor.GRAY + "Changing inventory mode to " + nextMode.getDisplayName());
                } else {
                    tycoonBlock.sellInventory(inventory, player);
                }
                refresh(player, inventory);
            }
            case "return_item"-> {
                new StatsMenu(tycoonBlock, plugin).open(player);
            }
            case null, default-> {}

        }
    }
    public TycoonBlock getTycoonBlock() {
        return tycoonBlock;
    }
    private void setInventoryItemMeta(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING, "inventory_item");
            pdc.set(TycoonData.INVENTORY_ITEM_KEY, PersistentDataType.STRING, item.getType().name());
            meta.setLore(Arrays.asList(
                    "§8§m-----------------------",
                    ChatColor.GRAY + "Worth: " + PriceUtility.formatMoney(PriceUtility.calculateWorth(item)) + worthManager.getWorthMultiplierFormatted(item.getType()),
                    "§8§m-----------------------"
            ));
            item.setItemMeta(meta);
        } else plugin.getLogger().warning("Item meta is null");
    }
    private ItemStack getModeItem(InventoryMode mode, Inventory inventory) {
        return switch (mode) {
            case SELL_MODE -> {
                double currentWorth = PriceUtility.calculateWorth(inventory);
                yield MenuManager.createItemstack(
                        Material.GREEN_STAINED_GLASS_PANE,
                        1,
                        mode.getDisplayName(),
                        Arrays.asList(
                                "§8§m-----------------------",
                                ChatColor.GREEN + "Sell all: " + PriceUtility.formatMoney(currentWorth),
                                "§8§m-----------------------",
                                ChatColor.YELLOW + "[ Shift + Click to change Mode ]"
                        ),
                        false,
                        true,
                        true,
                        "sell_item"
                );
            }
            case DROP_MODE -> {
                List<String> dropModeLore = Arrays.asList(
                        "§8§m-----------------------",
                        ChatColor.RED + "Dropped items can't be sold and can't",
                        ChatColor.RED + "be put back into the Tycoons inventory",
                        ChatColor.RED + "[ Click on item to drop one ]",
                        ChatColor.RED + "[ Shift + Click on item to drop stack ]",
                        "§8§m-----------------------",
                        ChatColor.YELLOW + "[ Shift + Click to change Mode ]");
                yield MenuManager.createItemstack(
                        Material.DROPPER,
                        1,
                        mode.getDisplayName(),
                        dropModeLore,
                        false,
                        true,
                        true,
                        "drop_mode"
                );
            }
        };
    }
    public enum InventoryMode {

        SELL_MODE(ChatColor.GREEN + "" + ChatColor.BOLD + "Sell Mode"),
        DROP_MODE(ChatColor.GOLD + "" + ChatColor.BOLD + "Drop Mode");

        private final String displayName;

        InventoryMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
        public InventoryMode nextMode() {
            InventoryMode[] modes = InventoryMode.values();
            return modes[(this.ordinal() + 1) % modes.length];
        }
    }
}
