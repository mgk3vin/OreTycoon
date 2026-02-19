package me.mangokevin.oreTycoon.tycoonManagment.tycoonWorlds;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.utility.Console;
import me.mangokevin.oreTycoon.utility.ParticleGenerator;
import me.mangokevin.oreTycoon.utility.ParticleManager;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.mvplugins.multiverse.core.utils.result.Attempt;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.options.CloneWorldOptions;
import org.mvplugins.multiverse.core.world.options.DeleteWorldOptions;
import org.mvplugins.multiverse.core.world.reasons.CloneFailureReason;
import org.mvplugins.multiverse.external.vavr.control.Option;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TycoonWorldManager {
    private final OreTycoon plugin;
    private final MultiverseCoreApi multiverseCoreApi;

    private final WorldManager worldManager;
    private final ParticleGenerator particleGenerator;
    private final ParticleManager particleManager;

    private final int maxWorldsPerPlayer = 5;

    private final Map<UUID, List<String>> playerWorlds = new HashMap<>();
    //NEW
    private final Map<String, WorldSettings> worldSettings = new HashMap<>();

    public TycoonWorldManager(OreTycoon plugin) {
        this.plugin = plugin;
        multiverseCoreApi = plugin.getMultiverseCoreApi();
        this.worldManager = multiverseCoreApi.getWorldManager();
        this.particleGenerator = plugin.getParticleGenerator();
        this.particleManager = plugin.getParticleManager();
    }

    public void savePlayerWorlds() {
        File file = new File(plugin.getDataFolder(), "playerWorlds.yml");
        // Vorherige Daten löschen, um Duplikate zu vermeiden
        YamlConfiguration data = new YamlConfiguration();

//        for (UUID uuid : playerWorlds.keySet()) {
//            String path = "worlds." + uuid.toString();
//            data.set(path, new ArrayList<>(playerWorlds.get(uuid)));
//            for (String worldName : playerWorlds.get(uuid)) {
//                data.set(path + "." + worldName, "test");
//            }
//        }
        //NEW
        for (String worldName : worldSettings.keySet()) {
            WorldSettings worldSetting = worldSettings.get(worldName);
            String path = "worlds." + worldName + ".";

            data.set(path + "owner", worldSetting.getOwnerUUID().toString());
            data.set(path + "isSpawnBeaconActive", worldSetting.isSpawnBeaconActive());
            data.set(path + "worldItem", worldSetting.getWorldItem().name());
            data.set(path + "isPrivate", worldSetting.isPrivate());

            List<String> trustedPlayers = worldSetting.getTrustedPlayer()
                    .stream()
                    .map(UUID::toString)
                    .toList();
            data.set(path + "trustedPlayers", trustedPlayers);
        }

        try {
            data.save(file);
            Console.log(getClass(), "Saved player worlds to " + file.getName() + " successfully");
        } catch (IOException e) {
            Console.error(getClass(), "Error occurred while saving: " + e.getMessage());
        }
    }
    public void loadPlayerWorlds() {
        File file = new File(plugin.getDataFolder(), "playerWorlds.yml");
        if (!file.exists()) return;

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yaml.getConfigurationSection("worlds");

        if (section == null) return;

        playerWorlds.clear();
        //NEW
        worldSettings.clear();

//        for (String key : section.getKeys(false)) {
//            try {
//                UUID uuid = UUID.fromString(key);
//                List<String> worlds = section.getStringList(key);
//                playerWorlds.put(uuid, new ArrayList<>(worlds));
//
//                Console.debug(getClass(), "Loaded worlds for " + uuid + ": " + worlds.size());
//
//            } catch (Exception e) {
//                Console.error(getClass(), "Error occurred while loading playerWorlds: " + key + ": " + e.getMessage());
//            }
//        }
        //NEW
        for (String worldName : section.getKeys(false)) {
            Console.log(getClass(), "Found world entry: " + worldName);
            String path = worldName + ".";

            String ownerUUIDString = section.getString(path + "owner");
            if (ownerUUIDString == null) {
                Console.error(getClass(), "Error occurred while loading player worlds: " + worldName + " Owner not found!");
                continue;
            };
            UUID ownerUUID = UUID.fromString(section.getString(path + "owner"));

            WorldSettings worldSetting = new WorldSettings(ownerUUID);

            worldSetting.setSpawnBeacon(section.getBoolean(path + "isSpawnBeaconActive"));

            String materialString = section.getString(path + "worldItem");
            if (materialString == null) {
                Console.error(getClass(), "Error occurred while loading player worlds: " + worldName + " World Item Material not found!");
                continue;
            }
            worldSetting.setWorldItem(Material.matchMaterial(materialString));
            worldSetting.setPrivate(section.getBoolean(path + "isPrivate"));
            List<String> trustedPlayers = section.getStringList(path + "trustedPlayers");

            List<UUID> trustedPlayerUUIDs = trustedPlayers
                    .stream()
                    .map(UUID::fromString)
                    .toList();
            worldSetting.setTrustedPlayer(trustedPlayerUUIDs);

            playerWorlds
                    .computeIfAbsent(ownerUUID, k -> new ArrayList<>())
                    .add(worldName);
            worldSettings.put(worldName, worldSetting);

            if (worldSetting.isSpawnBeaconActive()){
                worldManager.getWorld(worldName).peek(world -> {
                    particleManager.startBeacon(worldName, world.getSpawnLocation());
                }).onEmpty(() -> {
                    Console.error(getClass(), "No world found for " + worldName);
                });
            }
        }
    }

    public void createTycoonWorld(Player player) {
        WorldManager worldManager = multiverseCoreApi.getWorldManager();
        int worldNumber = getNextFreeWorldNumber(player);
        if (worldNumber == -1) {
            player.sendMessage(ChatColor.RED + "No free world slot found!");
            return;
        }
        String newWorldName = generateWorldName(player, worldNumber);

        if (!canCreateWorld(player)) {
            player.sendMessage(ChatColor.RED + "Failed to create world! Max Worlds: " + maxWorldsPerPlayer);
            return;
        }

        if (Bukkit.getWorld(newWorldName) != null) {
            Console.error(getClass(), "World " + newWorldName + " already exists!");
            return;
        }
        if (Bukkit.getWorld("Tycoon_World_Template_1_21_8") == null) {
            Console.error(getClass(), "Tycoon_World_Template_1_21_8 world doesn't exist!");
            return;
        }

        Option<LoadedMultiverseWorld> optionalTemplate =
                worldManager.getLoadedWorld("Tycoon_World_Template_1_21_8");
        if (optionalTemplate.isEmpty()) {
            Console.error(getClass(), "Template not Found!");
            return;
        }
        LoadedMultiverseWorld loadedMultiverseWorld = optionalTemplate.get();

        Attempt<LoadedMultiverseWorld, CloneFailureReason> attempt = multiverseCoreApi.getWorldManager().cloneWorld(CloneWorldOptions.fromTo(loadedMultiverseWorld, newWorldName));

        attempt.onFailure(reason -> {
            Console.error(getClass(), "Clone failed: " + reason.toString());
            player.sendMessage(ChatColor.RED + "Failed to clone world!");
        });

        attempt.onSuccess(newWorld -> {
            //✅World creation succeeded

            // ========== Create World Settings Default ==========
            WorldSettings worldSetting = createWorldSettings(newWorldName, player.getUniqueId());

            worldSetting.setSpawnBeacon(true);

            addPlayerWorld(player, newWorldName);
            worldSettings.put(newWorldName, worldSetting);
            // ========== Create World Settings Default ==========

            worldManager.getWorld(newWorldName).peek(world -> {
                particleManager.startBeacon(newWorldName ,world.getSpawnLocation());
            });

            player.sendMessage(ChatColor.GREEN + "Created new world!");
            listTycoonWorlds(player);

            //Teleport Player
            multiverseCoreApi.getDestinationsProvider()
                    .parseDestination("e:" + newWorldName + ":8,33,8:0:0")
                    .peek(destination -> {
                        multiverseCoreApi.getSafetyTeleporter().to(destination)
                                .checkSafety(true)
                                .teleport(List.of(player));
                    })
                    .onFailure(reason -> {
                        player.sendMessage(ChatColor.RED + "Failed to teleport to destination!");
                    });
        });
    }

    public void teleportToWorld(Player player, String worldName) {
        multiverseCoreApi.getDestinationsProvider()
                .parseDestination("w:" + worldName)
                .peek(destination -> {
                    var result = multiverseCoreApi.getSafetyTeleporter()
                            .to(destination)
                            .checkSafety(true)
                            .teleport(List.of(player));

                    result.onFailure(reason -> {
                        player.sendMessage(ChatColor.RED + "Failed to teleport to destination!");
                        Console.error(getClass(), "Failed to teleport to destination! Reason: " + reason.toString());
                    });
                    result.onSuccess(world -> {
                        player.sendMessage(ChatColor.GREEN + "Teleported to destination!");
                    });
                })
                .onFailure(reason -> {
                    player.sendMessage(ChatColor.RED + "Failed to teleport to destination!");
                    Console.error(getClass(), "Failed to teleport to destination! Reason: " + reason.toString());
                })
                .onSuccess(destination -> {
                    player.sendMessage(ChatColor.GREEN + "Destination found!");
                });
    }

    public boolean canCreateWorld(Player player) {
        UUID uuid = player.getUniqueId();
        if (!(playerWorlds.containsKey(uuid))) {return true;}

        return playerWorlds.get(uuid).size() < maxWorldsPerPlayer;
    }

    private void addPlayerWorld(Player player, String worldName) {
        playerWorlds.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(worldName);
    }
    private void removePlayerWorld(Player player, String worldName) {
        UUID uuid = player.getUniqueId();
        if (!playerWorlds.containsKey(uuid)) {return;}
        playerWorlds.get(uuid).remove(worldName);

        if (playerWorlds.get(uuid).isEmpty()) {
            playerWorlds.remove(uuid);
        }
    }

    public void setWorldSpawn(Player player, String worldName) {
        worldManager.getWorld(worldName)
                .peek(world -> {

                    Location targetLocation = player.getLocation();

                    var result = multiverseCoreApi.getSafetyTeleporter()
                            .to(targetLocation)
                            .checkSafety(true)
                            .teleport(List.of(player));

                    result.onFailure(reason -> {
                        Console.error(getClass(), "Failed to teleport to destination! Reason: " + reason.toString());
                        player.sendMessage(ChatColor.RED + "Failed to set teleport destination!");
                    });
                    result.onSuccess(success -> {
                        particleManager.stopBeacon(worldName);

                        world.setSpawnLocation(targetLocation);
                        worldManager.saveWorldsConfig();

                        particleManager.startBeacon(worldName, world.getSpawnLocation());

                        player.sendMessage(ChatColor.GREEN + "Teleported destination set to your location!");
                    });
                })
                .onEmpty(() -> {
                    Console.error(getClass(), "World not found! " + worldName);
                });
    }

    public void deleteTycoonWorld(Player player, String worldName) {
        multiverseCoreApi.getWorldManager()
                .getWorld(worldName)
                .peek(world -> {
                    multiverseCoreApi.getWorldManager()
                            .deleteWorld(DeleteWorldOptions.world(world))
                            .onFailure(reason -> {
                                // send error message
                                Console.error(getClass(), "Failed to delete world! Reason: " + reason.toString());
                                player.sendMessage(ChatColor.RED + "Failed to delete world! Can't delete world when players are still loaded!");
                            })
                            .onSuccess(name -> {
                                // send success message
                                Console.debug(getClass(), "Deleted Tycoon world!");
                                player.sendMessage(ChatColor.GREEN + "Deleted Tycoon world: " + worldName);

                                particleManager.spawnBeaconBeam(world.getSpawnLocation(), false);

                                worldSettings.remove(worldName);

                                removePlayerWorld(player, worldName);

                                listTycoonWorlds(player);
                            });
                })
                .onEmpty(() -> {
                    // world not found
                    Console.debug(getClass(), "Failed to delete world! World not found! " + worldName);
                });
    }
    public void deleteTycoonWorld(Player player,int worldNumber) {
        String worldName = generateWorldName(player, worldNumber);
        multiverseCoreApi.getWorldManager()
                .getWorld(worldName)
                .peek(world -> {
                    multiverseCoreApi.getWorldManager()
                            .deleteWorld(DeleteWorldOptions.world(world))
                            .onFailure(reason -> {
                                // send error message
                                Console.error(getClass(), "Failed to delete world! Reason: " + reason.toString());
                            })
                            .onSuccess(name -> {
                                // send success message
                                Console.debug(getClass(), "Deleted Tycoon world!");
                                player.sendMessage(ChatColor.GREEN + "Deleted Tycoon world: " + worldName);
                                removePlayerWorld(player, worldName);
                                listTycoonWorlds(player);
                            });
                })
                .onEmpty(() -> {
                    // world not found
                    Console.debug(getClass(), "Failed to delete world! World not found! " + worldName);
                });
    }

    public void listTycoonWorlds(Player player) {
        List<String> worldNames = playerWorlds.getOrDefault(player.getUniqueId(), new ArrayList<>());
        player.sendMessage(ChatColor.GREEN + "========== Tycoon worlds ==========");
        if (worldNames.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "You don't have any Tycoon worlds! -> /tycoon create");
        }
        for (String worldName : worldNames) {
            player.sendMessage(ChatColor.GREEN + worldName);
        }
        player.sendMessage(ChatColor.GREEN + "===================================");
    }

    public WorldSettings createWorldSettings(String worldName, UUID owner) {
        return worldSettings.computeIfAbsent(worldName,
                k -> new WorldSettings(owner));
    }

    private int getNextFreeWorldNumber(Player player) {
        List<String> worlds = playerWorlds.getOrDefault(player.getUniqueId(), new ArrayList<>());

        for (int i = 1; i <= maxWorldsPerPlayer; i++) {
            String name = generateWorldName(player, i);
            if (!worlds.contains(name) && Bukkit.getWorld(name) == null) {
                return i;
            }
        }
        return -1;
    }

    private String generateWorldName(Player player, int worldNumber) {
        return "tycoon_" + player.getName() + "_" + worldNumber;
    }

    public WorldSettings getWorldSettings(String worldName) {
        return worldSettings.get(worldName);
    }

    public Map<UUID, List<String>> getPlayerWorlds() {
        return playerWorlds;
    }
    public int getMaxWorldsPerPlayer(){return maxWorldsPerPlayer;}
}
