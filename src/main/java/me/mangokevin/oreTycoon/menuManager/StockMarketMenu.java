package me.mangokevin.oreTycoon.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import me.mangokevin.oreTycoon.utility.Console;
import me.mangokevin.oreTycoon.worth.PriceUtility;
import me.mangokevin.oreTycoon.worth.WorthManager;
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

import java.util.*;
import java.util.List;

public class StockMarketMenu implements MenuInterface {

    private WorthManager worthManager = OreTycoon.getInstance().getWorthManager();
    private Map<Material, Double> allWorths = worthManager.getAllWorths();

    private final int page;

    private static final Map<String, SortMode> playerSortMode = new HashMap<>();

    public StockMarketMenu(int page) {
        this.page = page;
    }

    @Override
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(new TycoonHolder(this), 54, "Stock Market Menu");

        playerSortMode.putIfAbsent(player.getName(), SortMode.MULTIPLIER_DESCENDING);

        refresh(player, inventory);
        player.openInventory(inventory);
    }

    @Override
    public void refresh(Player player, Inventory inventory) {
        MenuManager.addFiller(inventory, Material.GRAY_STAINED_GLASS_PANE);

        allWorths = worthManager.getAllWorths();

        SortMode currentSortMode = playerSortMode.getOrDefault(player.getName(), SortMode.MULTIPLIER_DESCENDING);

        List<Material> sortedMaterials = sortMaterials(allWorths, currentSortMode);

        //========= Stock items UPDATED=========
        List<Integer> usableSlots = getUsableSlots();
        int itemsPerPage = usableSlots.size();
        int startIndex = page * itemsPerPage;


        // Prüfe ob die Seite valid ist
        if (startIndex >= sortedMaterials.size()) {
            Console.error(getClass(), "Page out of bounds!");
            return;
        }


        for (int i = 0; i < itemsPerPage; i++) {
            Console.debug(getClass(), "------------------------------------------");
            int worthIndex = startIndex + i;

            if (worthIndex >= sortedMaterials.size()) {
                break;
            }

            int slot = usableSlots.get(i);

            ItemStack worthItem = createWorthItem(sortedMaterials.get(worthIndex));
            Console.debug(getClass(), "Getting item from List at Index: " + worthIndex);
            inventory.setItem(slot, worthItem);
            Console.debug(getClass(), "Setting item at Slot: " + slot + " as " + worthItem.getType());
        }
        //========= Stock items UPDATED=========

        //========= Navigation items =========
        if (page > 0){
            ItemStack pagePrev = MenuManager.createItemstack(
                    Material.ARROW,
                    1,
                    ChatColor.GOLD + "<- Page " + (page - 1),
                    null,
                    false,
                    true,
                    true,
                    "page_prev"
            );
            inventory.setItem(45, pagePrev);
        }
        if (startIndex + itemsPerPage < sortedMaterials.size()) {
            ItemStack pageNext = MenuManager.createItemstack(
                    Material.ARROW,
                    1,
                    ChatColor.GOLD + "Page " + (page + 1) + " ->",
                    null,
                    false,
                    true,
                    true,
                    "page_next"
            );
            inventory.setItem(53, pageNext);
        }
        //========= Navigation items =========

        //========= Filter item =========
        List<String> filterLore = Arrays.asList(
                "§8§m-----------------------",
                ChatColor.GRAY + "Sort Mode: " + ChatColor.GOLD + currentSortMode.getDisplayName(),
                ChatColor.GRAY + "Click to change",
                "§8§m-----------------------");
        ItemStack filterItem = MenuManager.createItemstack(
                Material.HOPPER,
                1,
                ChatColor.GOLD + "Change Sort Mode",
                filterLore,
                false,
                true,
                true,
                "filter_item"
        );
        inventory.setItem(49, filterItem);
        //========= Filter item ==========

    }

    @Override
    public void handleAction(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        Inventory inventory = event.getInventory();
        PersistentDataContainer pdc;
        if(item != null){
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                pdc = meta.getPersistentDataContainer();
                String action = pdc.get(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING);

                switch (action) {
                    case "filter_item" -> {
                        SortMode currentSortMode = playerSortMode.getOrDefault(player.getName(), SortMode.MULTIPLIER_DESCENDING);

                        SortMode nextSortMode = currentSortMode.next();
                        playerSortMode.put(player.getName(), nextSortMode);

                        refresh(player, inventory);
                    }
                    case "page_prev" -> {
                        new StockMarketMenu(page - 1).open(player);
                    }
                    case "page_next" -> {
                        new StockMarketMenu(page + 1).open(player);
                    }
                    case null -> {}
                    default -> throw new IllegalStateException("Unexpected value: " + action);
                }

            }
        }

    }


    private List<Material> sortMaterials(Map<Material, Double> allWorths, SortMode sortMode) {
        List<Material> materials = new ArrayList<>(allWorths.keySet());

        switch(sortMode) {
            case MULTIPLIER_DESCENDING -> {
                materials.sort((m1, m2) ->
                        Double.compare(
                                worthManager.getMultiplier(m2),
                                worthManager.getMultiplier(m1)
                        )
                );
            }
            case MULTIPLIER_ASCENDING -> {
                materials.sort((m1, m2) ->
                        Double.compare(
                                worthManager.getMultiplier(m1),
                                worthManager.getMultiplier(m2)
                        )
                );
            }
            case WORTH_DESCENDING -> {
                materials.sort((m1, m2) ->
                        Double.compare(
                                worthManager.getWorth(m2),
                                worthManager.getWorth(m1)
                        )
                );
            }
            case WORTH_ASCENDING -> {
                materials.sort((m1, m2) ->
                        Double.compare(
                                worthManager.getWorth(m1),
                                worthManager.getWorth(m2)
                        )
                );
            }
            case NAME_DESCENDING -> {
                materials.sort((m1, m2) ->
                        m2.name().compareTo(m1.name())
                );
            }
            case NAME_ASCENDING ->  {
                materials.sort((m1, m2) ->
                        m1.name().compareTo(m2.name())
                );
            }
            case null -> {}
        }
        return materials;
    }

    public enum SortMode {
        MULTIPLIER_DESCENDING("Multiplier ↓"),
        MULTIPLIER_ASCENDING("Multiplier ↑"),
        WORTH_DESCENDING("Worth ↓"),
        WORTH_ASCENDING("Worth ↑"),
        NAME_DESCENDING("Name Z→A"),
        NAME_ASCENDING("Name A→Z");

        private final String displayName;

        SortMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
        public SortMode next() {
            SortMode[] modes = SortMode.values();
            return modes[(this.ordinal() + 1) % modes.length];
        }
    }

    private List<Integer> getUsableSlots(){
        List<Integer> slots = new ArrayList<>();
        int start = 10;
        for (int i = 0; i < 4; i++) {
            for (int n = 0; n < 7; n++) {
                slots.add(start);
                start++;
            }
            start += 2;
        }

        return slots;
    }

    private ItemStack createWorthItem(Material material) {
        double worthMultiplier = worthManager.getMultiplier(material);
        worthMultiplier = Math.round(worthMultiplier*100.0)/100.0;
        double worthMultiplierFormatted = Math.round(worthMultiplier * 100.0 - 100.0);
        String worthFormatted = PriceUtility.formatMoney(worthManager.getWorth(material));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = Arrays.asList(
                "§8§m-----------------------",
                ChatColor.GRAY + "Worth: " + worthFormatted + (worthMultiplier >= 1.0 ? ChatColor.GREEN + " ( +" + worthMultiplierFormatted + "% )" : ChatColor.RED + " ( " + worthMultiplierFormatted + "% )"),
                ChatColor.GRAY + "Trend: " + (worthManager.getMultiplier(material) >= 1.0 ? ChatColor.GREEN + worthManager.getTrend(material) : ChatColor.RED + worthManager.getTrend(material)),
                "§8§m-----------------------");
        assert meta != null;
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }
}
