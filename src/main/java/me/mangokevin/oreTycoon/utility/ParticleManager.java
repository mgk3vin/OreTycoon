package me.mangokevin.oreTycoon.utility;

import me.mangokevin.oreTycoon.OreTycoon;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class ParticleManager {
    private final OreTycoon plugin = OreTycoon.getInstance();
    private final ParticleGenerator particleGenerator = plugin.getParticleGenerator();

    private final Map<String, Integer> activeBeacons = new HashMap<>();

    public void startBeacon(String worldName, Location spawnLocation) {
        if (activeBeacons.containsKey(worldName)) {
            return;
        }
        int taskID = new BukkitRunnable() {
            @Override
            public void run() {
                particleGenerator.spawnBeaconBeam(spawnLocation, true);
            }
        }.runTaskTimer(plugin, 0L , 50L).getTaskId();

        activeBeacons.put(worldName, taskID);
    }
    public void resetBeacon(String worldName) {
        if (activeBeacons.containsKey(worldName)) {
            plugin.getMultiverseCoreApi().getWorldManager().getWorld(worldName)
                    .peek(world -> {
                        stopBeacon(worldName);
                        startBeacon(worldName, world.getSpawnLocation());
                    });
        }
    }

    public void stopBeacon(String worldName) {
        Integer taskID = activeBeacons.remove(worldName);
        if (taskID != null) {
            plugin.getServer().getScheduler().cancelTask(taskID);
        }
    }

    @Deprecated
    public void spawnBeaconBeam(Location spawn, boolean isBeaconActive) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isBeaconActive) {
                    this.cancel();
                }
                particleGenerator.spawnBeaconBeam(spawn, isBeaconActive);
            }
        }.runTaskTimer(plugin, 0L, 50L);
    }
}
