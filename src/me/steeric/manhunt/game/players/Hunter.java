package me.steeric.manhunt.game.players;

import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.PlayerTracking;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Hunter extends AbstractManhuntPlayer {

    private Location compassTargetAtQuit;

    public Hunter(Player player, Game game) {
        super(player, game);
    }

    public void updateTracker() {

        Player playerHandle = this.getPlayerHandle();
        if (playerHandle == null)
            return;

        Location closest = PlayerTracking.findClosestRunner(playerHandle.getLocation(), this.getGame());
        if (closest != null)
            playerHandle.setCompassTarget(closest);
    }

    @Override
    public void preparePlayer() {

        super.preparePlayer();

        Player playerHandle = this.getPlayerHandle();
        if (playerHandle == null)
            return;

        Location gameSpawnLocation = this.getGame().getWorlds().worlds[0].getSpawnLocation();
        Location teleportLocation = new Location(gameSpawnLocation.getWorld(), gameSpawnLocation.getX(), gameSpawnLocation.getY(), gameSpawnLocation.getZ());
        teleportLocation.setDirection(teleportLocation.getDirection().setY(-1));
        playerHandle.teleport(teleportLocation);
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

    public Location getCompassTargetAtQuit() { return this.compassTargetAtQuit; }
    public void setCompassTargetAtQuit(Location target) { this.compassTargetAtQuit = target; }
}
