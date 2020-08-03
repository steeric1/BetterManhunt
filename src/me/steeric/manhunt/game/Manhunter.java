package me.steeric.manhunt.game;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.steeric.manhunt.game.data.MilestoneProgress;
import me.steeric.manhunt.game.data.PlayerData;
import me.steeric.manhunt.managing.ChatManager.ChatMode;

public class Manhunter {
	
	private PlayerType type;
	private UUID player;
	private PlayerData prevData;
	private Location compassTargetAtQuit;
	private Game game;
	private MilestoneProgress progress;
	private ChatMode chatMode;
	private boolean dataRestored;
	private boolean alive;
	
	public Manhunter(Player player, PlayerType type, Game game) {
		this.player = player.getUniqueId();
		this.type = type;
		this.game = game;
		this.progress = new MilestoneProgress();
		this.chatMode = ChatMode.TO_TEAM;
		this.dataRestored = false;
		this.alive = true;
	}
	
	public Game getGame() {
		return this.game;
	}
	
	public Location getCompassTargetAtQuit() {
		
		if (this.compassTargetAtQuit != null) return this.compassTargetAtQuit;
		
		return null;
	}
	
	public void setCompassTargetAtQuit(Location location) {
		this.compassTargetAtQuit = location;
	}
	
	@Override
	public String toString() {
		return Bukkit.getPlayer(this.player).getName() + " [" + this.type.toString().substring(0,1).toUpperCase() + "]";
	}
	
	public void updateTracker() {
		
		Player p = Bukkit.getPlayer(this.player);
		
		if (p == null) return;
		
		Location closest = PlayerTracking.findClosestRunner(p.getLocation(), this.game);
		
		if (closest != null) 
			p.setCompassTarget(closest);	
	}
	
	public void restoreData() {
		
		if (this.prevData == null) return;
		if (this.dataRestored) return;
		
		this.prevData.setDataToPlayer(Bukkit.getServer().getPlayer(this.player));
		this.dataRestored = true;
	}
	
	public boolean isDataRestored() {
		return this.dataRestored;
	}

	public void saveData() {
		this.prevData = new PlayerData(Bukkit.getServer().getPlayer(this.player));
	}
	
	public UUID getPlayer() {
		return this.player;
	}
	
	public PlayerType getType() {
		return this.type;
	}

	public MilestoneProgress getMilestoneProgress() {
		return progress;
	}

	public void setMilestoneProgress(MilestoneProgress progress) {
		this.progress = progress;
	}
	
	public ChatMode getChatMode() {
		return this.chatMode;
	}
	
	public void setChatMode(ChatMode mode) {
		this.chatMode = mode;
	}
	
	public boolean isAlive() {
		return this.alive;
	}
	
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	@Override
	public boolean equals(Object other) {
		
		if (other == null) return false;
		if (!(other instanceof Manhunter)) return false;
		
		Manhunter otherPlayer = (Manhunter) other;
		
		if (otherPlayer.getPlayer().equals(this.player)) return true;
		
		return false;
	}

	public static enum PlayerType {
		
		HUNTER {
			@Override
			public String toString() {
				return "hunter";
			}
		}, 
		
		RUNNER {
			@Override
			public String toString() {
				return "runner";
			}			
		}
	}
}
