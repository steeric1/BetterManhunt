package me.steeric.manhunt.game.managing;

import static net.md_5.bungee.api.ChatColor.AQUA;
import static net.md_5.bungee.api.ChatColor.RED;
import static net.md_5.bungee.api.ChatColor.WHITE;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.steeric.manhunt.game.players.AbstractManhuntPlayer;
import me.steeric.manhunt.game.players.Hunter;
import me.steeric.manhunt.game.players.Runner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.steeric.manhunt.game.Game;
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
	
	public static void deleteGame(Game game, Player playerHandle) {
				
		for (AbstractManhuntPlayer player : game.getManhuntPlayers()) {
			player.restoreData();

			Player gamePlayerHandle = player.getPlayerHandle();
			if (playerHandle != null)
				playerHandle.sendMessage(AQUA + "Teleporting you to your location before the game started!");

		}
		
		if (game.hasCreatedWorlds()) WorldManager.deleteGameWorlds(game);
		
		games.remove(game);
		if (playerHandle != null) playerHandle.sendMessage(AQUA + "Game deleted successfully!");
	}
	
	public static PreGame findPreGame(UUID creator) {
		
		for (PreGame s : preGames) {
			if (s.getCreator().equals(creator)) return s;
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
	
	public static String startGame(Game game) {
		game.start();
		return ChatColor.AQUA + "Game started!";
	}
	
	/* public static void joinGame(Player player, PlayerType type, String name) {
		
		Game game = findGame(name);
				
		// used whether player tries to join a game they have previously joined
		// by the click event in the message
		// ManhuntCommand.onCommand handles joins by command /manhunt join

		if (game == null) return;
		if (!game.addPlayer(player, type)) return;
		
		game.getPreJoins().remove(new PreJoin(player.getUniqueId(), game));

		Player adminHandle = Bukkit.getPlayer(game.getAdmin());
		if (adminHandle != null && !player.equals(adminHandle))
			adminHandle.sendMessage(ChatColor.WHITE + player.getName() + ChatColor.AQUA + " [" + type.toString().substring(0, 1).toUpperCase() + "] joined your game!");
	} Ä/*/
	
	public static boolean hasJoined(Player playerHandle, Game game) { // used by ManhuntCommand.onCommand to check if player has already joined a game

		UUID playerId;
		for (AbstractManhuntPlayer player : game.getManhuntPlayers()) {

			playerId = player.getPlayerId();

			if (playerId.equals(playerHandle.getUniqueId()))
				return true;
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
	
	public static <T extends AbstractManhuntPlayer> String changeTeams(Player playerHandle, Game game, Class<T> team) {
			
		List<Runner> runners = game.getRunners();
		List<Hunter> hunters = game.getHunters();
		List<AbstractManhuntPlayer> players = game.getManhuntPlayers();
		
		if (team == Runner.class) {

			UUID playerId;
			for (Runner runner : runners) {

				playerId = runner.getPlayerId();
				if (playerId == null)
					continue;

				if (playerId.equals(playerHandle.getUniqueId())) {
					return RED + "You already are a runner!";
				}
			}
			
			Runner runner = new Runner(playerHandle, game);
			runners.add(runner);
			
			int index = 0;

			for (int i = 0; i < hunters.size(); i++) {
				if (hunters.get(i).getPlayerId().equals(playerHandle.getUniqueId())) {
					index = i;
					break;
				}
			}

			hunters.remove(index);
			
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).getPlayerId().equals(playerHandle.getUniqueId())) {
					index = i;
					break;
				}
			}
			
			players.remove(index);
			players.add(runner);

		} else if (team == Hunter.class) {

			for (Hunter hunter : hunters) {
				if (hunter.getPlayerId().equals(playerHandle.getUniqueId())) {
					return RED + "You already are a hunter!";
				}
			}
			
			Hunter hunter = new Hunter(playerHandle, game);
			hunters.add(hunter);
			
			int index = 0;
			
			for (int i = 0; i < runners.size(); i++) {
				if (runners.get(i).getPlayerId().equals(playerHandle.getUniqueId())) {
					index = i;
					break;
				}
			}
			
			runners.remove(index);
			
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).getPlayerId().equals(playerHandle.getUniqueId())) {
					index = i;
					break;
				}
			}
			
			players.remove(index);
			players.add(hunter);
			
		}
		
		return AQUA + "You are now a " + team.getSimpleName().toLowerCase() + "!";
	}

	public static Game inGame(Player playerHandle) {
	
		for (Game game : games) {
			for (AbstractManhuntPlayer player : game.getManhuntPlayers()) {
				if (player.getPlayerId().equals(playerHandle.getUniqueId())) return game;
			}
		}
		
		return null;
	}
}
