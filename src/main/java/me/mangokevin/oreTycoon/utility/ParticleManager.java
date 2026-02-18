package me.mangokevin.oreTycoon.utility;

import me.mangokevin.oreTycoon.OreTycoon;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleManager {
    private final OreTycoon plugin = OreTycoon.getInstance();
    private final ParticleGenerator particleGenerator = plugin.getParticleGenerator();
    private boolean isBeaconActive;


    public void spawnBeaconBeam(Location spawn) {
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
    public void setBeaconActive(boolean beaconActive) {
        isBeaconActive = beaconActive;
    }
    public boolean isBeaconActive() {
        return isBeaconActive;
    }
}
