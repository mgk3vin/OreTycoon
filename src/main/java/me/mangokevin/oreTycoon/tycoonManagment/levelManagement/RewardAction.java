package me.mangokevin.oreTycoon.tycoonManagment.levelManagement;


import org.bukkit.entity.Player;

@FunctionalInterface
public interface RewardAction {
    void apply(Player player);
}
