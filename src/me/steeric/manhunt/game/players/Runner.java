package me.steeric.manhunt.game.players;

import com.sun.deploy.security.SelectableSecurityManager;
import me.steeric.manhunt.game.Game;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Runner extends AbstractManhuntPlayer {

    public Runner(Player player, Game game) {
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
    }

    @Override
    public String getPlayerListName() {
        Player playerHandle = this.getPlayerHandle();
        if (playerHandle != null)
            return "[R] " + playerHandle.getName();
        else
            return "[R]";
    }

    @Override
    public String toString() {
        Player playerHandle = this.getPlayerHandle();
        if (playerHandle != null)
            return playerHandle.getName() + " [R]";
        else
            return "[R]";
    }
}
