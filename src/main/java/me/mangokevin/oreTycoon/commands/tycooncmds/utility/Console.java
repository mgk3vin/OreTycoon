package me.mangokevin.oreTycoon.commands.tycooncmds.utility;

import me.mangokevin.oreTycoon.OreTycoon;
import org.bukkit.Bukkit;

public class Console {
    public static void log(String msg) {
        Bukkit.getConsoleSender().sendMessage("§8[§6OreTycoon§8] §f" + msg);
    }

    public static void debug(String msg) {
            Bukkit.getConsoleSender().sendMessage("§8[§bOreTycoon-Debug§8] §7" + msg);
    }

    public static void error(String msg) {
        Bukkit.getConsoleSender().sendMessage("§8[§4OreTycoon-ERROR§8] §c" + msg);
    }
}
