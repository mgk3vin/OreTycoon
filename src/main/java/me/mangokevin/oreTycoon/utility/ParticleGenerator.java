package me.mangokevin.oreTycoon.utility;

import me.mangokevin.oreTycoon.OreTycoon;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleGenerator {
    private final OreTycoon plugin = OreTycoon.getInstance();

    public void spawnBeaconBeam(Location spawn, boolean enabled) {
        if (!enabled) return;

        new BukkitRunnable() {
            int height = 0;
            @Override
            public void run() {
                if (height > 255) { // maximale Höhe
                    this.cancel();
                    return;
                }
                spawn.getWorld().spawnParticle(
                        Particle.END_ROD,                  // sieht wie Lichtstrahl aus
                        spawn.clone().add(0, height, 0), // Position
                        1, 0, 0, 0, 0                    // Einzelpartikel, keine Spread
                );
                height++;
            }
        }.runTaskTimer(plugin, 0, 1); // 1 Tick pro Partikel
    }
}
