package me.mangokevin.oreTycoon.utility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Console {
    public static void log(String msg) {
        Bukkit.getConsoleSender().sendMessage("§8[§6OreTycoon§8] §f" + msg);
    }
    public static void log(Class<?> clazz, String msg) {
        Bukkit.getConsoleSender().sendMessage("§8[§6OreTycoon§8] §f§2 [" + clazz.getSimpleName() + "] §f"  + msg);
    }

    public static void debug(String msg) {
            Bukkit.getConsoleSender().sendMessage("§8[§bOreTycoon-Debug§8] §7" + msg);
    }
    public static void debug(Class<?> clazz, String msg) {
        Bukkit.getConsoleSender().sendMessage("§8[§6OreTycoon-Debug§8] §f§2 [" + clazz.getSimpleName() + "] §7"  + msg);
    }
    public static void warn(Class<?> clazz, String msg) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "§8[" + ChatColor.GOLD +"OreTycoon-WARN§8]" + ChatColor.GOLD + clazz.getSimpleName() + ": " + ChatColor.GOLD + msg);
    }
    public static void error(String msg) {
        Bukkit.getConsoleSender().sendMessage("§8[§4OreTycoon-ERROR§8] §c" + msg);
    }
    public static void error(Class<?> clazz, String msg) {
        Bukkit.getConsoleSender().sendMessage("§8[§6OreTycoon-ERROR§8] §f§2 [" + clazz.getSimpleName() + "] §c"  + msg);
    }
}
