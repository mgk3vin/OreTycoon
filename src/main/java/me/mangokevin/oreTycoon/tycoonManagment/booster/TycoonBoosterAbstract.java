package me.mangokevin.oreTycoon.tycoonManagment.booster;

import me.mangokevin.oreTycoon.menuManager.MenuManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.utility.Console;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public abstract class TycoonBoosterAbstract {
    protected final double boostValue;
    protected final long duration;

    public TycoonBoosterAbstract(double boostValue, long duration) {
        this.boostValue = boostValue;
        this.duration = duration;
    }

    protected String getRemainingTimeFormatted(long duration) {
        long totalSeconds = duration / 20; // Ticks in Sekunden umwandeln
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        Console.log(getClass(), "Duration: " + duration + " ticks | total Seconds: " + totalSeconds + "s | minutes: " + minutes + "m | seconds: " + seconds);

        // Gibt es im Format "2m 30s" zurück
        String returnString = minutes + "m " + seconds + "s";
        Console.log(getClass() ," Time Remaining: " + returnString);
        return returnString;
    }
    public ItemStack getItem() {
        ItemStack boosterItem = MenuManager.createItemstack(
          getMaterial(),
          1,
          getDisplayName(),
          getLore(),
          true,
          true,
          false,
          "tycoon_booster_item"
        );
        ItemMeta meta = boosterItem.getItemMeta();
        if (meta != null) {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(TycoonData.BOOSTER_ID_KEY, PersistentDataType.STRING, getUID());
            pdc.set(TycoonData.BOOSTER_VALUE_KEY, PersistentDataType.DOUBLE, boostValue);
            pdc.set(TycoonData.BOOSTER_DURATION_KEY, PersistentDataType.LONG, duration);
            boosterItem.setItemMeta(meta);
        }
        return boosterItem;
    }

    public abstract Material getMaterial();
    public abstract String getDisplayName();
    public abstract List<String> getLore();
    public abstract String getUID();
    public abstract double getBoostValue();
    public abstract long getDuration();
    public abstract void setDuration(long duration);
    public abstract void onApply(TycoonBlock tycoonBlock);
}
