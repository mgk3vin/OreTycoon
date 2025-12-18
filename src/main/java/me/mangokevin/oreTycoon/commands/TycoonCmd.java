package me.mangokevin.oreTycoon.commands;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlockManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TycoonCmd implements CommandExecutor {

    private final OreTycoon oreTycoon;
    private final TycoonBlockManager blockManager;

    public TycoonCmd(OreTycoon oreTycoon, TycoonBlockManager blockManager) {
        this.oreTycoon = oreTycoon;
        this.blockManager = blockManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player p)) {
            return true;
        }

        if (args.length == 0) {
            blockManager.giveTycoonBlock(p);
        }

        return true;
    }


//    public void giveTycoonBlock(Player p){
//        // 1. Das Item erstellen (ItemStack)
//        ItemStack tycoonBlock = new ItemStack(Material.DIAMOND_BLOCK);
//        ItemMeta meta = tycoonBlock.getItemMeta();
//
//        // 2. Den Schlüssel definieren
//        // "this" bezieht sich auf deine Hauptklasse.
//        // Wenn du in einer anderen Klasse bist, nutze: JavaPlugin.getPlugin(DeinMainPlugin.class)
//        NamespacedKey key = new NamespacedKey(oreTycoon, "is_tycoon_block");
//
//        // 3. Den Tag setzen
//        // Wir speichern den Wert 1 (als Byte) unter dem Schlüssel.
//        // Das ist wie: is_tycoon_block = 1
//        //assert meta != null;
//        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
//
//        // 4. Meta speichern und Item geben
//        meta.setDisplayName("§bUnendlicher Tycoon Block");
//        tycoonBlock.setItemMeta(meta);
//        p.getInventory().addItem(tycoonBlock);
//    }
}





