package me.mangokevin.oreTycoon.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import me.mangokevin.oreTycoon.utility.CooldownManager;
import me.mangokevin.oreTycoon.worth.PriceUtility;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.worth.WorthManager;
import org.bukkit.Bukkit;
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

    private final int page;

    private static final CooldownManager sellCooldown = new CooldownManager(1000 * 3);
    private static final CooldownManager changeModeCooldown = new CooldownManager(1000);

    private static final Map<UUID, InventoryMode> playerInventoryMode = new HashMap<>();

    public TycoonInventory(OreTycoon plugin, TycoonBlock tycoonBlock, int page) {
        this.tycoonBlock = tycoonBlock;
        this.plugin = plugin;
        this.worthManager = plugin.getWorthManager();
        this.page = page;
    }

    @Override
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(new TycoonHolder(this), 36, ChatColor.GOLD + "Inventory - Page " + (page + 1));

        playerInventoryMode.putIfAbsent(player.getUniqueId(), InventoryMode.SELL_MODE);

        refresh(player, inventory);
        player.openInventory(inventory);
    }

    @Override
    public void refresh(Player player, Inventory inventory) {
        //Clear inventory to refill it correctly
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, null);
        }
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

        List<ItemStack> allInventoryStacks = getItemsAsStack();
        int startIndex = page * 27;
        int endIndex = Math.min(startIndex + 27, allInventoryStacks.size());

        for (int i = startIndex; i < endIndex; i++) {
            int slot = i - startIndex;
            ItemStack item = allInventoryStacks.get(i);
            setInventoryItemMeta(item);
            inventory.setItem(slot, item);
        }

        if (page > 0) {
            //previous page button
            ItemStack prevPageIcon = MenuManager.createItemstack(
                    Material.ARROW,
                    1,
                    ChatColor.GOLD + "<- Previous Page",
                    null,
                    false,
                    true,
                    true,
                    "previous_page"
            );
            inventory.setItem(27, prevPageIcon);
        }
        ItemStack nextPageIcon = MenuManager.createItemstack(
                Material.ARROW,
                1,
                ChatColor.GOLD + "Next Page ->",
                null,
                false,
                true,
                true,
                "next_page"
        );
        inventory.setItem(35, nextPageIcon);


        //Inventory Mode Button 30
        InventoryMode mode = playerInventoryMode.getOrDefault(player.getUniqueId(), InventoryMode.SELL_MODE);
        ItemStack invModeItem = getModeItem(mode);
        inventory.setItem(30, invModeItem);

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
        inventory.setItem(32, backToMenuItem);

    }

    private List<ItemStack> getItemsAsStack(){
        List<ItemStack> stacks = new ArrayList<>();
        for (Map.Entry<Material, Integer> entry : tycoonBlock.getStoredItems().entrySet()) {
            Material material = entry.getKey();
            int remaining = entry.getValue();

            while (remaining > 0) {
                int stackSize = Math.min(remaining, 64);
                stacks.add(new ItemStack(material, stackSize));
                remaining -= stackSize;
            }
        }
        return stacks;
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
                    } else {
                        ItemStack droppedItem = new ItemStack(item.getType(), 1);
                        tycoonBlock.dropItem(droppedItem, player);
                    }
                }
                refresh(player, inventory);
            }
            case "drop_mode" -> {
                if (shiftClick) {
                    if (changeModeCooldown.isOnCooldown(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "You have to wait "
                                + changeModeCooldown.getRemainingCooldownSeconds(player.getUniqueId())
                                + "s before you can change the mode again!");
                    } else {
                        InventoryMode nextMode = mode.nextMode();
                        playerInventoryMode.put(player.getUniqueId(), nextMode);
                        player.sendMessage(ChatColor.GRAY + "Changing inventory mode to " + nextMode.getDisplayName());
                        changeModeCooldown.setCooldown(player.getUniqueId());
                    }

                }
                refresh(player, inventory);
            }
            case "sell_item"-> {
                if (shiftClick) {
                    if (changeModeCooldown.isOnCooldown(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "You have to wait "
                                + changeModeCooldown.getRemainingCooldownSeconds(player.getUniqueId())
                                + "s before you can change the mode again!");
                    } else {
                        InventoryMode nextMode = mode.nextMode();
                        playerInventoryMode.put(player.getUniqueId(), nextMode);
                        player.sendMessage(ChatColor.GRAY + "Changing inventory mode to " + nextMode.getDisplayName());
                        changeModeCooldown.setCooldown(player.getUniqueId());
                    }
                } else {
                    if (!sellCooldown.isOnCooldown(player.getUniqueId())) {
                        //tycoonBlock.sellInventory(inventory, player);
                        tycoonBlock.sellTycoonInventory(player);
                        sellCooldown.setCooldown(player.getUniqueId());
                    } else {
                        player.sendMessage(ChatColor.RED + "You have to wait "
                                + sellCooldown.getRemainingCooldownSeconds(player.getUniqueId())
                                + "s before you can sell again!");
                    }
                }
                refresh(player, inventory);
            }
            case "next_page" -> {
                new TycoonInventory(plugin, tycoonBlock, page + 1).open(player);
            }
            case "previous_page" -> {
                new TycoonInventory(plugin, tycoonBlock, page - 1).open(player);
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
    private ItemStack getModeItem(InventoryMode mode) {
        return switch (mode) {
            case SELL_MODE -> {
                String currentWorthFormatted = PriceUtility.formatMoney(PriceUtility.calculateWorth(tycoonBlock.getStoredItems()));
                yield MenuManager.createItemstack(
                        Material.GREEN_STAINED_GLASS_PANE,
                        1,
                        mode.getDisplayName(),
                        Arrays.asList(
                                "§8§m-----------------------",
                                ChatColor.GREEN + "Sell all: " + currentWorthFormatted,
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
