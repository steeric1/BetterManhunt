package me.steeric.manhunt.commands.pregame;

import static net.md_5.bungee.api.ChatColor.AQUA;
import static net.md_5.bungee.api.ChatColor.RED;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import me.steeric.manhunt.commands.GameCommand;
import me.steeric.manhunt.game.data.PreGame;
import me.steeric.manhunt.managing.GameManager;
import me.steeric.manhunt.managing.WorldManager;

public class WorldCommand {
	
	public static class FindWorldCommand implements GameCommand {

		@Override
		public boolean execute(Player player) {
			
			if (!player.hasPermission("bettermanhunt.createnewworld")) {
				player.sendMessage(RED + "You don't have the permission to do that!");
				return true;
			}
			
			PreGame preGame = GameManager.findPreGame(player.getUniqueId());
			if (preGame == null) {
				player.sendMessage("You are not creating a game!");
				return true;
			}
			
			player.sendMessage(AQUA + "Finding a world...");
			String baseName = WorldManager.findWorld();
			
			if (baseName == null) {
				player.sendMessage(RED + "Could not find an applicable world. Try again later!");
				GameManager.preGames.remove(preGame);
				return true;
			}
			
			GameManager.createGame(player, preGame.getName(), baseName, false);
			
			return true;
		}
	}
	
	public static class NewWorldCommand implements GameCommand {

		@Override
		public boolean execute(Player player) {

			PreGame preGame = GameManager.findPreGame(player.getUniqueId());
			if (preGame == null) {
				player.sendMessage("You are not creating a game!");
				return true;
			}
			
			String baseName = "auto_MHWORLD_" + GameManager.games.size();
			player.sendMessage(RED + "Starting to create new worlds. You might experience (heavy) lag during the operation.");
			
			GameManager.createGame(player, preGame.getName(), baseName, true);
			
			return true;
		}
	}
	
	public static class ThisWorldCommand implements GameCommand {

		@Override
		public boolean execute(Player player) {
			
			PreGame preGame = GameManager.findPreGame(player.getUniqueId());
			if (preGame == null) {
				player.sendMessage(RED + "You are not creating a game!");
				return true;
			}
			
			World world = player.getWorld();
			if (world.getEnvironment() != Environment.NORMAL) {
				player.sendMessage(RED + "Games can be only started in the overworld!");
				return true;
			}
			
			if (!WorldManager.worldsExist(world.getName())) {
				player.sendMessage(RED + "Can't find a nether or an end to link with this world!");
				return true;
			}
			
			String baseName = world.getName();
			player.sendMessage(AQUA + "Creating a game in this world.");

			GameManager.createGame(player, preGame.getName(), baseName, false);
			
			return false;
		}
		
	}
}
