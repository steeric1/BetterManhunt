package me.steeric.manhunt.game;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.steeric.manhunt.Manhunt;
import me.steeric.manhunt.game.Manhunter.PlayerType;
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
	private final ArrayList<Manhunter> players;
	private final ArrayList<Manhunter> hunters;
	private final ArrayList<Manhunter> runners;
	private ArrayList<UUID> deadRunners;
	private GameState state;
	private final WorldSet worlds;
	private final boolean createdWorld;
	private boolean headStartOver;
	private int runnersLeft;
	private int headStartTime;
	private final ArrayList<PreJoin> preJoins;
	private Location endLocation;

	
	public Game(Player admin, String name, WorldSet worlds, boolean createdWorld) {
		
		this.admin = admin.getUniqueId();
		this.name = name;
		this.state = GameState.NOT_STARTED;
		this.worlds = worlds;
		this.createdWorld = createdWorld;
		this.headStartOver = false;
		this.runnersLeft = 0;
		this.headStartTime = 30;
		
		this.players = new ArrayList<>();
		this.hunters = new ArrayList<>();
		this.runners = new ArrayList<>();
		this.deadRunners = new ArrayList<>();
		
		this.preJoins = new ArrayList<>();
	}
	
	public void start() {
		
		for (Manhunter p : this.players) {
					
			Player player = Bukkit.getServer().getPlayer(p.getPlayer());
			p.saveData();
			player.setPlayerListName(ChatColor.GREEN + " [" + p.getType().toString().substring(0, 1).toUpperCase() + "] " + player.getName() + " ");
			Location spawnLoc = this.worlds.worlds[0].getSpawnLocation();
			Location loc = new Location(spawnLoc.getWorld(), spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ());
			if (p.getType() == PlayerType.HUNTER) 
				loc.setDirection(loc.getDirection().setX(0).setY(1).setZ(0));
			
			player.teleport(loc);
			player.getInventory().clear();
			
			player.getEnderChest().clear();
			player.setBedSpawnLocation(spawnLoc, true);
			
			player.setGameMode(GameMode.SURVIVAL);
			
			for (PotionEffect effect : player.getActivePotionEffects()) {
				player.removePotionEffect(effect.getType());
				
			}
			
			player.setHealthScaled(false);
			player.setHealth(20);
			player.setExp(0);
			player.setLevel(0);
			player.setSaturation(20);
			player.setFoodLevel(20);
			
			player.sendMessage(ChatColor.AQUA + "Teleporting you to the game!");
		}
		
		Runnable task = new HunterTimeoutTask(this);
		
		for (Manhunter p : this.players) {
			
				Player player = Bukkit.getPlayer(p.getPlayer());
			
			if (p.getType() == PlayerType.HUNTER) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, this.headStartTime * 20, 255, false, false, true));
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, this.headStartTime * 20, 255, false, false, true));
			} else if (p.getType() == PlayerType.RUNNER) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 5 * 20, 255, false, false, true));
				
				PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, 
						ChatSerializer.a("{\"text\":\"§cRUN!\"}"), 20, 40, 20);
				PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, 
						ChatSerializer.a("{\"text\":\"§cYou are being hunted!\"}"), 40, 20, 20);
				
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle);
			}
		}
		
		Bukkit.getServer().getScheduler()
			.scheduleSyncDelayedTask(Manhunt.manhuntPlugin, 
					task, this.headStartTime * 20);
		
		this.state = GameState.RUNNING;
	}
	
	public static class HunterTimeoutTask implements Runnable {
		
		private Game game;
		
		public HunterTimeoutTask(Game game) {
			this.game = game;
		}
		
		@Override
		public void run() {
			
			this.game.setHeadStartOver(true);
			
			for (Manhunter p : this.game.getHunters()) {
				
				Player player = Bukkit.getPlayer(p.getPlayer());
				
				player.getInventory().addItem(PlayerTracking.getTracker());
				player.teleport(player.getLocation().setDirection(player.getLocation().getDirection().setY(0).setX(1)));
				
			}
			
		}
		// this is a comment btw.
	}
	
	public Manhunter findPlayer(Player player) {
		
		UUID uuid = player.getUniqueId();
		
		for (Manhunter p : this.players) {
			if (p.getPlayer().equals(uuid)) {
				return p;
			}
		}
		
		
		return null;
	}
	
	public void gameOver(PlayerType winner) {
		
		this.state = GameState.GAME_OVER;
		
		for (int i = 0; i < this.players.size(); i++) {
			
			Player player = Bukkit.getPlayer(this.players.get(i).getPlayer());
			
			player.sendMessage(ChatColor.GOLD + "Game over!\n" + 
			winner.toString().substring(0, 1).toUpperCase() + 
			winner.toString().substring(1) + "s win!");
		}
		
		Player adminPlayer = Bukkit.getPlayer(this.admin);
		
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
	
	public void decrementRunnersLeft(UUID player) {

		for (UUID id : this.deadRunners) {
			if (id.equals(player)) return;
		}

		this.deadRunners.add(player);
		this.runnersLeft--;
		
		if (this.runnersLeft < 1 && this.state == GameState.RUNNING) {
			this.gameOver(PlayerType.HUNTER);
		}
	}

	public void setHeadStartTime(int time) {
		this.headStartTime = time;
	}
	
	public boolean addPlayer(Player player, PlayerType type) { // returns boolean value indicating success/failure
		
		// check if player already has joined the game
		for (Manhunter p : this.players) {
			
			if (p.getPlayer().equals(player.getUniqueId())) 
				return false;
		}
		
		Manhunter _player = new Manhunter(player, type, this);
		
		this.players.add(_player);
		
		if (type == PlayerType.HUNTER) this.hunters.add(_player);
		else {
			this.runners.add(_player);
			this.runnersLeft++;
		}
		
		return true;
		
	}
	
	public void removePlayer(Manhunter p) {
		
		int index = 0;
		
		for (int i = 0; i < this.players.size(); i++) {
			if (this.players.get(i).getPlayer().equals(p.getPlayer())) {
				index = i;
				break;
			}
		}
		
		this.players.remove(index);
		
		if (p.getType() == PlayerType.RUNNER) {
			
			for (int i = 0; i < this.runners.size(); i++) {
				if (this.runners.get(i).getPlayer().equals(p.getPlayer())) {
					index = i;
					break;
				}
			}
			
			this.runners.remove(index);
			
			if (p.isAlive()) this.decrementRunnersLeft(p.getPlayer());
			
		} else if (p.getType() == PlayerType.HUNTER) {
		
			for (int i = 0; i < this.hunters.size(); i++) {
				if (this.hunters.get(i).getPlayer().equals(p.getPlayer())) {
					index = i;
					break;
				}
			}
			
			this.hunters.remove(index);
			
		}
		
	}
	
	public void deathMessage(String message, Manhunter player) {
		
		for (Manhunter p : this.players) {
			
			if (p.equals(player)) continue;
			
			if (p.getType() == player.getType()) {
				Bukkit.getPlayer(p.getPlayer()).sendMessage(ChatColor.RED + message);
			} else {
				Bukkit.getPlayer(p.getPlayer()).sendMessage(ChatColor.GREEN + message);
			}
		}
	}
	
	public void notifyTeam(String message, PlayerType team) {
		
		if (team == PlayerType.HUNTER) {
			
			for (Manhunter p : this.hunters) {
				Bukkit.getPlayer(p.getPlayer()).sendMessage(message);
			}
		
		} else if (team == PlayerType.RUNNER) {
			
			for (Manhunter p : this.runners) {
				Bukkit.getPlayer(p.getPlayer()).sendMessage(message);
			}
		}
	}
	
	public void notifyPlayers(String message) {
		
		for (Manhunter p : this.players) {
			Bukkit.getPlayer(p.getPlayer()).sendMessage(message);
		}
		
	}
	
	public boolean isHeadStartOver() {
		return this.headStartOver;
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
	
	public ArrayList<Manhunter> getPlayers() {
		return this.players;
	}
	
	public ArrayList<Manhunter> getHunters() {
		return this.hunters;
	}
	
	public ArrayList<Manhunter> getRunners() {
		return this.runners;
	}
	
	public ArrayList<PreJoin> getPreJoins() {
		return this.preJoins;
	}

	public void setEndLocation(Location location) { this.endLocation = location; }

	public Location getEndLocation() { return this.endLocation; }
	
	@Override
	public boolean equals(Object other) {
		
		if (other == null) return false;
		if (!(other instanceof Game)) return false;
		
		Game otherGame = (Game) other;
		
		if (otherGame.getName().equals(this.name)) return true;
		
		return false;
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
