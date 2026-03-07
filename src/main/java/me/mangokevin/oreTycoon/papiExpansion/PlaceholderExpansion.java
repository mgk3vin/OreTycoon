package me.mangokevin.oreTycoon.papiExpansion;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.levelManagment.LevelManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonManager;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonRegistry;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {

    private final TycoonManager tycoonManager;
    private final TycoonRegistry tycoonRegistry;
    private final LevelManager levelManager;
    public PlaceholderExpansion(OreTycoon plugin) {
        tycoonManager = plugin.getTycoonManager();
        tycoonRegistry = plugin.getTycoonRegistry();
        levelManager = plugin.getLevelManager();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "oretycoon";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Mangokevin";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }
    @Override
    public boolean persist() {
        return true; // Wichtig, damit die Expansion nach Reloads aktiv bleibt
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (!player.isOnline()) return "";
        Player onlinePlayer = player.getPlayer();

        String paramsLower = params.toLowerCase();

        assert onlinePlayer != null;
        // --- LOGIK A: LISTEN-MENÜ (slot_1_level, slot_1_status, etc.) ---
        if (paramsLower.startsWith("slot_")) {
            try {

                String[] parts = paramsLower.split("_");
                int index = Integer.parseInt(parts[1]) - 1;
                String type = parts[2];

                List<TycoonBlock> tycoons = tycoonRegistry.getAllTycoonsFromPlayer(onlinePlayer.getUniqueId());
                System.out.println("[PlaceholderExpansion] " + tycoons);
                if (index < 0 || index >= tycoons.size()) {
                    if (type.equals("material")) return "BARRIER";
                    return "false";
                }
                TycoonBlock t = tycoons.get(index);

                return switch (type){
                    case "level" -> String.valueOf(t.getLevel());
                    case "status" -> t.isActive() ? "true" : "false";
                    case "location" -> t.getLocation().getBlockX() + ", " + t.getLocation().getBlockZ();
                    case "material" -> t.getMaterial().name();
                    case "spawninterval" -> String.valueOf(t.getSpawnRate());
                    case "spawnrate" -> String.valueOf(t.getSpawnRate());
                    case "exists" -> "true";
                    default -> "default";
                };

            }catch (Exception ex){
                return "ERROR";
            }
        }

        if (paramsLower.equalsIgnoreCase("limit")) {
            return String.valueOf(tycoonManager.getMaxTycoonsPerPlayer());
        }

        if (!onlinePlayer.hasMetadata("viewing_tycoon")) return "N/A";

        String tycoonUID = onlinePlayer.getMetadata("viewing_tycoon").getFirst().asString();
        TycoonBlock tycoonBlock = tycoonRegistry.getTycoonBlock(tycoonUID);
        if (tycoonBlock == null) return "[Error] No tycoon found!";

        return switch (params.toLowerCase()) {
            case "level" -> String.valueOf(tycoonBlock.getLevel());
            case "xp_current" -> String.valueOf(tycoonBlock.getLevelXp());
            case "xp_needed" ->
                    String.valueOf(levelManager.getXpNeededForLevel(tycoonBlock.getLevel() + 1));
            case "progress_percent" ->
                    String.valueOf(levelManager.getProgressPercentage(tycoonBlock.getLevelXp(), tycoonBlock.getLevel() + 1));
            case "progress_bar" -> String.valueOf(tycoonBlock.getProgressBar(20));
            case "status" -> String.valueOf(tycoonBlock.isActive());
            case "owner" -> String.valueOf(tycoonBlock.getOfflineOwner().getName());
            case "material" -> tycoonBlock.getMaterial().name();
            case "index" -> String.valueOf(tycoonBlock.getIndex());
            default -> null;
        };

    }
}
