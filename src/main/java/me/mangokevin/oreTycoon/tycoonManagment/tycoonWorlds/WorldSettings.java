package me.mangokevin.oreTycoon.tycoonManagment.tycoonWorlds;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WorldSettings {

    private final UUID ownerUUID;
    private boolean spawnBeacon;
    private Material worldItem;
    private boolean isPrivate;
    private List<UUID> trustedPlayer;

    public WorldSettings(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
        this.spawnBeacon = false;
        this.worldItem = Material.GRASS_BLOCK;
        this.isPrivate = true;
        this.trustedPlayer = new ArrayList<>();
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public boolean isSpawnBeaconActive() {
        return spawnBeacon;
    }
    public Material getWorldItem() {
        return worldItem;
    }
    public boolean isPrivate() {
        return isPrivate;
    }
    public List<UUID> getTrustedPlayer() {
        return trustedPlayer;
    }


    public void setSpawnBeacon(boolean spawnBeacon) {
        this.spawnBeacon = spawnBeacon;
    }

    public void setWorldItem(Material worldItem) {
        this.worldItem = worldItem;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public void setTrustedPlayer(List<UUID> trustedPlayer) {
        this.trustedPlayer = trustedPlayer;
    }
}
