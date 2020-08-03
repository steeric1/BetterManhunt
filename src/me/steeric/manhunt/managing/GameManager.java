package me.steeric.manhunt.managing;

import static net.md_5.bungee.api.ChatColor.AQUA;
import static net.md_5.bungee.api.ChatColor.RED;
import static net.md_5.bungee.api.ChatColor.WHITE;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.Manhunter;
import me.steeric.manhunt.game.Manhunter.PlayerType;
import me.steeric.manhunt.game.data.PreGame;
import me.steeric.manhunt.game.data.PreJoin;
import me.steeric.manhunt.game.data.WorldSet;
import net.md_5.bungee.api.ChatColor;

/**
 * Utility class for game management
 */

public class GameManager {
	
	public static ArrayList<Game> games = new ArrayList<>(); // static list of all existing games
	public static ArrayList<PreGame> preGames = new ArrayList<>(); // static list of all existing games
	
	public static void createGame(Player admin, String name, String baseName, boolean createWorlds) {
		
		WorldSet worldSet = new WorldSet(baseName, createWorlds);
		
		// create new game
		Game game = new Game(admin, name, worldSet, createWorlds);
		games.add(game);
		
		PreGame preGame = new PreGame(admin.getUniqueId(), name);
		preGames.remove(preGame);
		
		admin.sendMessage(AQUA + "A game has been successfully created with the name of " + WHITE + name + AQUA + 
				". The admin of the game is " + WHITE + admin.getName() + AQUA +  ".");
		
	}
	
	public static void deleteGame(Game game, Player player) {
				
		for (Manhunter mh : game.getPlayers()) {
			mh.restoreData();
			Bukkit.getPlayer(mh.getPlayer()).sendMessage(AQUA + "Teleporting you to your location before the game started!");
		}
		
		if (game.hasCreatedWorlds()) WorldManager.deleteGameWorlds(game);
		
		games.remove(game);
		if (player != null) player.sendMessage(AQUA + "Game deleted successfully!");
	}
	
	public static PreGame findPreGame(UUID creator) {
		
		for (PreGame s : preGames) {
			if (s.getCreator().equals(creator)) return s;
		}
		
		return null;
	}
	
	public static PreGame findPreGame(String name) {
		
		for (PreGame s : preGames) {
			if (s.getName().equals(name)) return s;
		}
		
		return null;
	}
	
	public static PreJoin findPreJoin(UUID player) {
		
		for (Game game : games) {
			for (PreJoin p : game.getPreJoins()) {
				if (p.getPlayer().equals(player)) return p;
			}
		}
		
		return null;
	}
	
	public static String startGame(Game game, Player player) {
		
		game.start();
		
		return ChatColor.AQUA + "Game started!";
		
	}
	
	public static String getGamePostFix(Game game) {
		
		for (int i = 0; i < games.size(); i++) {
			if (games.get(i).getName().equals(game.getName())) return String.valueOf(i);
		}
		
		return null;
		
	}
	
	public static String joinGame(Player player, PlayerType type, String name) {
		
		Game game = findGame(name);
				
		// used whether player tries to join a game they have previously joined
		// by the click event in the message
		// ManhuntCommand.onCommand handles joins by command /manhunt join
		
		if (game == null) return null;
		
		if (!game.addPlayer(player, type)) return ChatColor.RED + "You have already joined that game!";
		
		game.getPreJoins().remove(new PreJoin(player.getUniqueId(), game));
		
		if (!player.equals(Bukkit.getPlayer(game.getAdmin())))
			Bukkit.getPlayer(game.getAdmin()).sendMessage(ChatColor.WHITE + player.getName() + ChatColor.AQUA + " [" + type.toString().substring(0, 1).toUpperCase() + "] joined your game!");
		
		return ChatColor.AQUA + "Succesfully joined game!\nYou are now a " + type + "!";
	}
	
	public static boolean hasJoined(Player player, Game game) { // used by ManhuntCommand.onCommand to check if player has already joined a game
		
		for (Manhunter p : game.getPlayers()) {
			if (p.getPlayer().equals(player.getUniqueId())) return true;
		}
		
		return false;
	}
	
	public static Game hasPreJoined(Player player) {
		
		for (Game game : games) {
			
			for (PreJoin prejoin : game.getPreJoins()) 
				if (prejoin.getPlayer().equals(player.getUniqueId())) 
					return game;
		
		}
		
		return null;
	}
	
	
	public static Game findGame(String name) {
		
		for (Game game : games) {
			if (game.getName().equals(name)) return game;
		}
		
		return null;
	}
	
	public static String changeTeams(Player player, Game game, PlayerType type) {
			
		ArrayList<Manhunter> runners = game.getRunners();
		ArrayList<Manhunter> hunters = game.getHunters();
		ArrayList<Manhunter> players = game.getPlayers();
		
		if (type == PlayerType.RUNNER) {
			
			for (int i = 0; i < runners.size(); i++) {
				if (runners.get(i).getPlayer().equals(player.getUniqueId())) {
					return RED + "You already are a runner!";
				}
			}
			
			Manhunter newPlayer = new Manhunter(player, Manhunter.PlayerType.RUNNER, game);
			runners.add(newPlayer);
			
			int index = 0;
			
			for (int i = 0; i < hunters.size(); i++) {
				if (hunters.get(i).getPlayer().equals(player.getUniqueId())) {
					index = i;
					break;
				}
			}
			
			hunters.remove(index);
			
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).getPlayer().equals(player.getUniqueId())) {
					index = i;
					break;
				}
			}
			
			players.remove(index);
			players.add(newPlayer);
			
			
		} else {
			
			for (int i = 0; i < hunters.size(); i++) {
				if (hunters.get(i).getPlayer().equals(player.getUniqueId())) {
					return RED + "You already are a hunter!";
				}
			}
			
			Manhunter newPlayer = new Manhunter(player, Manhunter.PlayerType.HUNTER, game);
			hunters.add(newPlayer);
			
			int index = 0;
			
			for (int i = 0; i < runners.size(); i++) {
				if (runners.get(i).getPlayer().equals(player.getUniqueId())) {
					index = i;
					break;
				}
			}
			
			runners.remove(index);
			
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).getPlayer().equals(player.getUniqueId())) {
					index = i;
					break;
				}
			}
			
			players.remove(index);
			players.add(newPlayer);
			
		}
		
		return AQUA + "You are now a " + type + "!";
	}

	public static Game inGame(Player player) {
	
		for (Game game : games) {
			for (Manhunter p : game.getPlayers()) {
				if (p.getPlayer().equals(player.getUniqueId())) return game;
			}
		}
		
		return null;
		
	}
}
