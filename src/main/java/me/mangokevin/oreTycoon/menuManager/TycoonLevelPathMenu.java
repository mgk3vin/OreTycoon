package me.mangokevin.oreTycoon.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import me.mangokevin.oreTycoon.tycoonManagment.levelManagement.LevelReward;
import me.mangokevin.oreTycoon.tycoonManagment.levelManagement.LevelRewardRegistry;
import me.mangokevin.oreTycoon.utility.Console;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TycoonLevelPathMenu implements MenuInterface{

    private final List<Integer> usableSlots = Arrays.asList(9, 10, 19, 28, 37, 38, 39, 30, 21, 12, 13, 14, 23, 32, 41, 42, 43, 34, 25, 16, 17);
    private final TycoonBlock tycoonBlock;
    private final int page;
    private final OreTycoon plugin;

    public TycoonLevelPathMenu(TycoonBlock tycoonBlock, int page, OreTycoon plugin) {
        this.tycoonBlock = tycoonBlock;
        this.page = page;
        this.plugin = plugin;
    }

    @Override
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(new TycoonHolder(this), 54, ChatColor.GRAY+ "Level path");
        refresh(player, inventory);
        player.openInventory(inventory);
    }

    @Override
    public void refresh(Player player, Inventory inventory) {
        //========== Filler ==========
        MenuManager.addFiller(inventory, Material.CYAN_STAINED_GLASS_PANE);
        //========== Filler ==========

        //========== Level items ==========
        int startIndex = page * usableSlots.size();
        for (int i = 0; i < usableSlots.size(); i++) {
            int slot = usableSlots.get(i);
            int level = i + 1 + startIndex;
            int index = i + startIndex;
            int currentTycoonLevel = tycoonBlock.getLevel();
            boolean isClaimed = tycoonBlock.getTycoonUpgrades().hasClaimedLevel(level);


            ItemStack levelItem;
            if(isClaimed){
                 levelItem = MenuManager.createItemstack(Material.MINECART,
                        1,
                        ChatColor.AQUA + "Level " + level,
                         Arrays.asList("§8§m-----------------------",
                                 ChatColor.GRAY + "Reward already claimed",
                                 "§8§m-----------------------"),
                        false,
                        true,
                         true,
                        "level_item_claimed");
            } else if (currentTycoonLevel >= level){
                levelItem = Objects.requireNonNull(LevelRewardRegistry.getLevelReward(level, tycoonBlock)).getDisplayItem();
            } else {
                levelItem = MenuManager.createItemstack(Material.RED_STAINED_GLASS_PANE,
                        1,
                        ChatColor.RED + "Level " + level,
                        Arrays.asList("§8§m-----------------------",
                                ChatColor.GRAY + "Reward locked",
                                "§8§m-----------------------"),
                        true,
                        true,
                        true,
                        "level_item_locked");
            }

            if (level > LevelRewardRegistry.getSize()){
                levelItem = new ItemStack(Material.AIR);
            }

            ItemMeta levelItemMeta = levelItem.getItemMeta();
            if(levelItemMeta != null) {
                levelItemMeta.getPersistentDataContainer().set(TycoonData.MENU_ACTION_TYCOON_LEVEL_KEY, PersistentDataType.INTEGER, level);
                levelItem.setItemMeta(levelItemMeta);
            }
            inventory.setItem(slot, levelItem);

        }
        //========== Level items ==========

        if (page > 0) {
            ItemStack prev_page = MenuManager.createItemstack(Material.ARROW,
                    1,
                    "<- Previous Page",
                    null,
                    false,
                    true,
                    true,
                    "prev_page");
            inventory.setItem(45, prev_page);
        }
        if (page < (LevelRewardRegistry.getSize() / usableSlots.size())) {
            Console.debug(getClass(), "TycoonLevelPathMenu page " + page + " < " + LevelRewardRegistry.LEVEL_REWARDS.size() + "/" + usableSlots.size());
            ItemStack next_page = MenuManager.createItemstack(Material.ARROW,
                    1,
                    "Next Page ->",
                    null,
                    false,
                    true,
                    true,
                    "next_page");
            inventory.setItem(53, next_page);
        }

        //Return button
        ItemStack returnItem = MenuManager.createItemstack(Material.BARRIER,
                1,
                ChatColor.RED + "<- Back to Stats Menu",
                null,
                false,
                true,
                true,
                "return");
        inventory.setItem(49, returnItem);

    }

    @Override
    public void handleAction(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            String action = pdc.get(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING);
            int level = pdc.getOrDefault(TycoonData.LEVEL_REWARD_ID_KEY, PersistentDataType.INTEGER, 0);


            switch (action) {
                case "level_reward_item":
                    if (level == 0 || level > LevelRewardRegistry.getSize()){
                        Console.error(getClass(), "Invalid level reward!");
                        break;
                    }
                    LevelReward levelReward = LevelRewardRegistry.getLevelReward(level, tycoonBlock);
                    levelReward.apply(player);
                    tycoonBlock.getTycoonUpgrades().claimLevel(level);
                    Console.log(getClass(), "Level reward " + level + " has been claimed!");
                    tycoonBlock.updateHologram();
                    refresh(player, inventory);
                    break;
                case "level_item_locked":
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.2f);
                    break;
                case "level_item_claimed":
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_HIT, 1.0f, 1.2f);
                    break;
                case "return":
                    new StatsMenu(tycoonBlock, plugin).open(player);
                    break;
                case "next_page":
                    new TycoonLevelPathMenu(tycoonBlock, page + 1, plugin).open(player);
                    break;
                case "prev_page":
                    new TycoonLevelPathMenu(tycoonBlock, page - 1, plugin).open(player);
                case null, default:
                    break;
            }
        }
    }

 }
