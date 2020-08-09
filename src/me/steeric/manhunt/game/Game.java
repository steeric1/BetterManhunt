package me.steeric.manhunt.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import me.steeric.manhunt.game.players.*;
import me.steeric.manhunt.gui.TeamSelectionGui;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.steeric.manhunt.Manhunt;
import me.steeric.manhunt.game.data.PreJoin;
import me.steeric.manhunt.game.data.WorldSet;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_16_R1.PacketPlayOutTitle.EnumTitleAction;

/**
 *  Game object class
 */

public class Game {

	private final UUID admin;
	private final String name;
	private final List<AbstractManhuntPlayer> manhuntPlayers;
	private final List<Hunter> hunters;
	private final List<Runner> runners;
	private final List<Spectator> spectators;
	private GameState state;
	private final WorldSet worlds;
	private final boolean createdWorld;
	private boolean headStartOver;
	private int runnersLeft;
	private int headStartTime;
	private final List<PreJoin> preJoins;
	private Location endLocation;
	private final TeamSelectionGui teamSelectionGui;

	public Game(Player admin, String name, WorldSet worlds, boolean createdWorld) {
		
		this.admin = admin.getUniqueId();
		this.name = name;
		this.state = GameState.NOT_STARTED;
		this.worlds = worlds;
		this.createdWorld = createdWorld;
		this.headStartOver = false;
		this.runnersLeft = 0;
		this.headStartTime = 30;
		this.teamSelectionGui = new TeamSelectionGui(this);
		
		this.manhuntPlayers = new ArrayList<>();
		this.hunters = new ArrayList<>();
		this.runners = new ArrayList<>();
		this.spectators = new ArrayList<>();

		this.preJoins = new ArrayList<>();
	}
	
	public void start() {

		// set time to day
		this.worlds.worlds[0].setFullTime(1000);

		for (AbstractManhuntPlayer player : this.manhuntPlayers)
			player.preparePlayer();

		for (Spectator spectator : this.spectators)
			spectator.preparePlayer();

		this.giveStartingEffects();

		Runnable task = new HunterTimeoutTask(this);
		Bukkit.getServer().getScheduler()
			.scheduleSyncDelayedTask(Manhunt.manhuntPlugin, 
					task, this.headStartTime * 20);

		this.state = GameState.RUNNING;
	}
	
	public AbstractManhuntPlayer findPlayer(Player playerHandle) {
		
		UUID uuid = playerHandle.getUniqueId();
		
		for (AbstractManhuntPlayer player : this.manhuntPlayers) {
			if (player.getPlayerId().equals(uuid)) {
				return player;
			}
		}

		return null;
	}
	
	public <T extends AbstractManhuntPlayer> void gameOver(Class<T> winnerTeam) {
		
		this.state = GameState.GAME_OVER;

		for (AbstractManhuntPlayer manhuntPlayer : this.manhuntPlayers) {

			Player playerHandle = manhuntPlayer.getPlayerHandle();

			if (playerHandle != null) {
				playerHandle.sendMessage(ChatColor.GOLD + "Game over!\n" +
						winnerTeam.getSimpleName() + "s win!");
			}
		}
		
		Player adminPlayer = Bukkit.getPlayer(this.admin);
		if (adminPlayer == null) return;
		
		TextComponent[] message = new TextComponent[3];
		
		message[0] = new TextComponent("Game over. [ ");
		message[0].setColor(ChatColor.AQUA);
		
		message[1] = new TextComponent("DELETE GAME");
		message[1].setColor(ChatColor.WHITE);
		message[1].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/manhunt delete " + this.getName()));
		
		message[2] = new TextComponent(" ]");
		message[2].setColor(ChatColor.AQUA);
		
		adminPlayer.spigot().sendMessage(message);
		
	}
	
	public void decrementRunnersLeft(Runner runner) {

		if (!runner.isDead())
			return;

		this.runnersLeft--;
		
		if (this.runnersLeft < 1 && this.state == GameState.RUNNING)
			this.gameOver(Hunter.class);
	}

	public void setHeadStartTime(int time) {
		this.headStartTime = time;
	}

	public void addPlayer(AbstractPlayer player) { // returns boolean value indicating success/failure

		if (player instanceof Spectator) {
			this.spectators.add((Spectator) player);
		} else {
			this.manhuntPlayers.add((AbstractManhuntPlayer) player);

			if (player instanceof Hunter) {
				this.hunters.add((Hunter) player);
			} else if (player instanceof Runner) {
				this.runners.add((Runner) player);
				this.runnersLeft++;
			}
		}
	}

	public void showTeamSelectionGui(Player player) {
		player.openInventory(this.teamSelectionGui.getInventory());
	}
	
	public void removePlayer(AbstractManhuntPlayer player) {
		
		int index = 0;
		this.manhuntPlayers.remove(player);
		
		if (player instanceof Runner) {

			Runner runner = (Runner) player;
			this.runners.remove(runner);

			if (player.isDead())
				this.decrementRunnersLeft(runner);

		} else if (player instanceof Hunter) {
			this.hunters.remove(player);
		}
	}

	private void giveStartingEffects() {

		for (AbstractManhuntPlayer player : this.manhuntPlayers) {

			Player playerHandle = player.getPlayerHandle();
			if (playerHandle == null)
				return;

			if (player instanceof Hunter) {
				playerHandle.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, this.headStartTime * 20, 255, false, false, true));
				playerHandle.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, this.headStartTime * 20, 255, false, false, true));
			} else if (player instanceof Runner) {
				playerHandle.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 5 * 20, 255, false, false, true));

				PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE,
						ChatSerializer.a("{\"text\":\"§cRUN!\"}"), 20, 40, 20);
				PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE,
						ChatSerializer.a("{\"text\":\"§cYou are being hunted!\"}"), 40, 20, 20);

				((CraftPlayer) playerHandle).getHandle().playerConnection.sendPacket(title);
				((CraftPlayer) playerHandle).getHandle().playerConnection.sendPacket(subtitle);
			}
		}
	}
	
	public void deathMessage(String message, AbstractManhuntPlayer player) {
		
		for (AbstractManhuntPlayer toPlayer : this.manhuntPlayers) {
			
			if (toPlayer.equals(player)) continue;

			Player playerHandle = toPlayer.getPlayerHandle();
			if (playerHandle == null)
				continue;

			if (toPlayer.getClass() == player.getClass()) {
				playerHandle.sendMessage(ChatColor.RED + message);
			} else {
				playerHandle.sendMessage(ChatColor.GREEN + message);
			}
		}
	}
	
	public <T extends AbstractManhuntPlayer> void notifyTeam(String message, Class<T> team) {

		if (team == Hunter.class) {

			for (AbstractManhuntPlayer player : this.hunters) {
				Player playerHandle = player.getPlayerHandle();
				if (playerHandle != null)
					playerHandle.sendMessage(message);
			}

		} else if (team == Runner.class) {

			for (AbstractManhuntPlayer player : this.runners) {
				Player playerHandle = player.getPlayerHandle();
				if (playerHandle != null)
					playerHandle.sendMessage(message);
			}
		}
	}
	
	public boolean headStartNotOver() {
		return !this.headStartOver;
	}
	
	public void setHeadStartOver(boolean headStartOver) {
		this.headStartOver = headStartOver;
	}
	
	public boolean hasCreatedWorlds() {
		return this.createdWorld;
	}
	
	public WorldSet getWorlds() {
		return this.worlds;
	}
	
	@Override
	public String toString() {
		return this.name + " (" + this.state + ")"; 
	}
	
	public GameState getState() {
		return this.state;
	}
	
	public UUID getAdmin() {
		return this.admin;
	}

	public String getName()	{
		return this.name;
	}

	public List<AbstractManhuntPlayer> getManhuntPlayers() {
		return this.manhuntPlayers;
	}

	public List<Hunter> getHunters() {
		return this.hunters;
	}

	public List<Runner> getRunners() {
		return this.runners;
	}

	public List<Spectator> getSpectators() {
		return this.spectators;
	}

	public List<PreJoin> getPreJoins() {
		return this.preJoins;
	}

	public void setEndLocation(Location location) { this.endLocation = location; }

	public Location getEndLocation() { return this.endLocation; }

	@Override
	public boolean equals(Object other) {

		if (other == null) return false;
		if (!(other instanceof Game)) return false;

		Game otherGame = (Game) other;

		return otherGame.getName().equals(this.name);
	}


	public static class HunterTimeoutTask implements Runnable {

		private final Game game;

		public HunterTimeoutTask(Game game) {
			this.game = game;
		}

		@Override
		public void run() {

			this.game.setHeadStartOver(true);

			for (AbstractManhuntPlayer player : this.game.getHunters()) {

				Player playerHandle = player.getPlayerHandle();

				if (playerHandle != null) {
					playerHandle.getInventory().addItem(PlayerTracking.getTracker());
					playerHandle.teleport(playerHandle.getLocation().setDirection(playerHandle.getLocation().getDirection().setY(0).setX(1)));
				}
			}

		}
	}
	
	public enum GameState {
		
		NOT_STARTED {
			@Override
			public String toString() {
				return "NOT STARTED";
			}
		},
		
		RUNNING {
			@Override
			public String toString() {
				return "RUNNING";
			}
		},
		
		GAME_OVER {
			@Override
			public String toString() {
				return "GAME OVER";
			}
		}
	}

}
