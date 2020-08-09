package me.steeric.manhunt.game.players;

import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.managing.ChatManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class Spectator extends AbstractPlayer {

    public Spectator(Player player, Game game) {
        super(player, game);
    }

    @Override
    public void preparePlayer() {

        super.preparePlayer();

        Player playerHandle = this.getPlayerHandle();
        if (playerHandle == null)
            return;

        Location gameSpawnLocation = this.getGame().getWorlds().worlds[0].getSpawnLocation();
        playerHandle.teleport(gameSpawnLocation);
        playerHandle.setGameMode(GameMode.SPECTATOR);
    }

    @Override
    public ChatManager.ChatMode getChatMode() {
        return ChatManager.ChatMode.TO_ALL;
    }

    @Override
    public void setChatMode(ChatManager.ChatMode mode) {

    }

    @Override
    public String getPlayerListName() {
        Player playerHandle = this.getPlayerHandle();
        if (playerHandle != null)
            return "[H] " + playerHandle.getName();
        else
            return "[H]";
    }

    @Override
    public String toString() {
        Player playerHandle = this.getPlayerHandle();
        if (playerHandle != null)
            return playerHandle.getName() + " [H]";
        else
            return "[H]";
    }
}
