package me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement;

import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonType;
import me.mangokevin.oreTycoon.worth.PriceUtility;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class TycoonBlockFactory {
    //Creates Tycoon Objects



    public TycoonBlockFactory() {}



//    public List<TycoonBlock> createTycoonsFromDB(String blockName) {
//        return databaseManager.loadTycoons();
//    }

    //Creates a new Tycoon Block itemStack
    public ItemStack createTycoonBlock(Player player, TycoonType type) {

        ItemStack item = new ItemStack(type.getMaterial(), 1);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        // Mark as tycoon
        meta.getPersistentDataContainer().set(TycoonData.TYCOON_BLOCK_KEY, PersistentDataType.BYTE, (byte) 1);
        // Save Enum type
        meta.getPersistentDataContainer().set(TycoonData.TYPE_KEY, PersistentDataType.STRING, type.name());

        // ItemStack
        meta.setDisplayName(type.getName() + " §7Tycoon");
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        List<String> lore = new ArrayList<>();
        lore.add("§8§m-----------------------");
        lore.add("§7Typ: " + type.getName());
        lore.add("§7Level: §e1");
        lore.add("§8§m-----------------------");
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }
    public ItemStack createSmartTycoonBlock(TycoonBlock smartTycoonBlock) {
        TycoonType tycoonType = smartTycoonBlock.getTycoonType();
        ItemStack tycoonItem = new ItemStack(tycoonType.getMaterial(), 1);

        TycoonData.writeToItem(tycoonItem,
                smartTycoonBlock.getLevel(),
                smartTycoonBlock.getLevelXp(),
                smartTycoonBlock.getLocation(),
                smartTycoonBlock.getMaterial(),
                smartTycoonBlock.getSpawnRate(),
                smartTycoonBlock.getCreationTime(),
                smartTycoonBlock.getTycoonType().name(),
                smartTycoonBlock.getInventory(),
                smartTycoonBlock.getTycoonUpgrades());

        ItemMeta tycoonMeta = tycoonItem.getItemMeta();

        if (tycoonMeta == null) return null;
        tycoonMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
        tycoonMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);


        List<String> lore = new ArrayList<>();
        lore.add("§8§m-----------------------");
        lore.add("§7Level: §e" + smartTycoonBlock.getLevel());
        lore.add("§7XP: §f" + smartTycoonBlock.getLevelXp());
        lore.add("§7Progress: §f" + smartTycoonBlock.getProgressBar(20));
        lore.add("§7Spawnrate: §f" + smartTycoonBlock.getSpawnRateFormatted() + "s");
        lore.add("§8§m-------§r§8Inventory§m--------");
        lore.add("§7Size: " + smartTycoonBlock.getStorageStatisticFormatted() + ChatColor.WHITE + " | " + ChatColor.GREEN + PriceUtility.calculateWorthFormatted(smartTycoonBlock.getInventory()));
        lore.add("§8§m-----------------------");
        tycoonMeta.setLore(lore);
        tycoonMeta.setDisplayName(smartTycoonBlock.getTycoonType().getName());


        tycoonItem.setItemMeta(tycoonMeta);

        return tycoonItem;
    }
}
