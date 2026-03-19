package me.mangokevin.oreTycoon.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    private final long durationMs;

    public CooldownManager(long durationMs) {
        this.durationMs = durationMs;
    }

    public boolean isOnCooldown(UUID uuid) {
        if (!cooldowns.containsKey(uuid)) {return  false;}
        return System.currentTimeMillis() < cooldowns.get(uuid) + durationMs;
    }
    public void setCooldown(UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis());
    }
    public long getRemainingCooldownMs(UUID uuid) {
        if (!cooldowns.containsKey(uuid)) {return  0L;}
        return Math.max(0L, (cooldowns.get(uuid) + durationMs) - System.currentTimeMillis());
    }
}
