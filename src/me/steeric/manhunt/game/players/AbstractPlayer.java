package me.steeric.manhunt.game.players;

import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.data.PlayerData;
import me.steeric.manhunt.game.managing.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class AbstractPlayer {

    private final UUID playerId;
    protected PlayerData playerData;
    protected boolean playerDataRestored;
    private final Game game;

    public AbstractPlayer(Player player, Game game) {
        this.playerId = player.getUniqueId();
        this.playerData = null;
        this.playerDataRestored = false;
        this.game = game;
    }

    public void saveData() {

        Player playerHandle = this.getPlayerHandle();
        if (playerHandle != null)
            this.playerData = new PlayerData(playerHandle);
    }

    public void restoreData() {

        if (this.playerData == null || this.playerDataRestored)
            return;

        Player playerHandle = this.getPlayerHandle();
        if (playerHandle == null)
            return;

        this.playerData.setDataToPlayer(playerHandle);
        this.playerDataRestored = true;
    }

    public void preparePlayer() {

        this.saveData();

        Player playerHandle = this.getPlayerHandle();
        if (playerHandle == null)
            return;

        Location gameSpawnLocation = this.game.getWorlds().worlds[0].getSpawnLocation();

        playerHandle.setPlayerListName(this.getPlayerListName());
        playerHandle.getInventory().clear();
        playerHandle.getEnderChest().clear();
        playerHandle.setBedSpawnLocation(gameSpawnLocation, true);
        playerHandle.getActivePotionEffects()
                .forEach(effect -> playerHandle.removePotionEffect(effect.getType()));
        playerHandle.setHealthScaled(false);
        playerHandle.setHealth(20);
        playerHandle.setExp(0);
        playerHandle.setLevel(0);
        playerHandle.setSaturation(20);
        playerHandle.setFoodLevel(20);
    }

    public abstract String getPlayerListName();

    @Override
    public abstract String toString();

    @Override
    public boolean equals(Object other) {

        if (!(other instanceof AbstractPlayer))
            return false;

        return ((AbstractPlayer) other).getPlayerId().equals(this.playerId);
    }


    public Player getPlayerHandle() { return Bukkit.getPlayer(this.playerId); }
    public UUID getPlayerId() { return this.playerId; }
    public Game getGame() { return this.game; }
    public boolean isPlayerDataRestored() { return this.playerDataRestored; }

    public abstract ChatManager.ChatMode getChatMode();
    public abstract void setChatMode(ChatManager.ChatMode toAll);
}
