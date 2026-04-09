package me.mangokevin.oreTycoon.worth;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.StoredItemKey;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.SpawnBlock;
import net.ess3.api.IEssentials;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class PriceUtility {

    public static double calculateWorth(Map<StoredItemKey, Integer> storedItems){
        WorthManager worthManager = OreTycoon.getInstance().getWorthManager();
        return worthManager.getWorth(storedItems);
    }
    public static double calculateWorth(ItemStack item){
        WorthManager worthManager = OreTycoon.getInstance().getWorthManager();

        if (item == null || item.getType() == Material.AIR) {
            return 0.0;
        }

        double singleWorth = worthManager.getWorth(item.getType());

        return singleWorth * item.getAmount();
    }
    public static double calculateWorth(SpawnBlock spawnBlock){
        WorthManager worthManager = OreTycoon.getInstance().getWorthManager();

        if (spawnBlock == null || spawnBlock.getMaterial() == Material.AIR) {
            return 0.0;
        }

        return worthManager.getWorth(spawnBlock);
    }
    public static double calculateWorthFromMaterials(Map<Material, Integer> items){
        double worth = 0;
        for(Map.Entry<Material, Integer> entry : items.entrySet()){
            worth += calculateWorth(new ItemStack(entry.getKey(), entry.getValue()));
        }
        return worth;
    }
    public static double calculateWorth(Inventory inventory){
        double totalWorth = 0.0;

        for (ItemStack item : inventory.getContents()){
            if (item != null)
            {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    if (meta.getPersistentDataContainer().has(
                            TycoonData.MENU_ITEM_KEY,
                            PersistentDataType.STRING
                    )){
                        continue;
                    }
                }

                totalWorth += calculateWorth(item);
            }
        }

        return totalWorth;
    }
    public static double calculateWorthEssentials(ItemStack item){
        IEssentials ess = OreTycoon.getEssentials();
        if(ess==null)return 0;

        double worth = 0;
        if(item==null || item.getType() == Material.AIR)return 0.0;

        BigDecimal price = ess.getWorth().getPrice(ess, item);

        if(price!=null){
            worth += price.doubleValue() * item.getAmount();
        }

        return worth;
    }
    public static String calculateWorthPerHour(double speed, double averageReward){
        return formatMoney((3600/speed) * averageReward);
    }

    public static String calculateWorthFormatted(ItemStack item){
        return formatMoney(calculateWorth(item));
    }
    public static String calculateWorthFormatted(Inventory inventory){
        return formatMoney(calculateWorth(inventory));
    }
    private static final NumberFormat fmt = NumberFormat.getCompactNumberInstance(
            Locale.US, NumberFormat.Style.SHORT);

    public static String formatMoney(double value) {
        fmt.setMaximumFractionDigits(2); // Max 2 Nachkommastellen (z.B. 1.25k)
        return "$" + fmt.format(value);
    }
    @Deprecated
    public static String formatWorth(double value){
        if (value < 1000) return String.format("%.2f", value);

        // Definition der Suffixe
        String[] suffixes = new String[]{"", "k", "M", "B", "T", "Q"};
        int index = 0;

        while (value >= 1000 && index < suffixes.length - 1) {
            value /= 1000;
            index++;
        }

        // Gibt z.B. 1.25k oder 5.0M zurück
        return String.format("%.2f%s", value, suffixes[index]);
    }
}
