package me.steeric.manhunt.commands.pregame;

import static net.md_5.bungee.api.ChatColor.AQUA;
import static net.md_5.bungee.api.ChatColor.RED;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import me.steeric.manhunt.commands.GameCommand;
import me.steeric.manhunt.game.data.PreGame;
import me.steeric.manhunt.game.managing.GameManager;
import me.steeric.manhunt.game.managing.WorldManager;

public class WorldCommand {
	
	public static class FindWorldCommand implements GameCommand {

		@Override
		public boolean execute(Player playerHandle) {
			
			if (!playerHandle.hasPermission("bettermanhunt.createnewworld")) {
				playerHandle.sendMessage(RED + "You don't have the permission to do that!");
				return true;
			}
			
			PreGame preGame = GameManager.findPreGame(playerHandle.getUniqueId());
			if (preGame == null) {
				playerHandle.sendMessage("You are not creating a game!");
				return true;
			}
			
			playerHandle.sendMessage(AQUA + "Finding a world...");
			String baseName = WorldManager.findWorld();
			
			if (baseName == null) {
				playerHandle.sendMessage(RED + "Could not find an applicable world. Try again later!");
				GameManager.preGames.remove(preGame);
				return true;
			}
			
			GameManager.createGame(playerHandle, preGame.getName(), baseName, false);
			
			return true;
		}
	}
	
	public static class NewWorldCommand implements GameCommand {

		@Override
		public boolean execute(Player playerHandle) {

			PreGame preGame = GameManager.findPreGame(playerHandle.getUniqueId());
			if (preGame == null) {
				playerHandle.sendMessage("You are not creating a game!");
				return true;
			}
			
			String baseName = "auto_MHWORLD_" + GameManager.games.size();
			playerHandle.sendMessage(RED + "Starting to create new worlds. You might experience (heavy) lag during the operation.");
			
			GameManager.createGame(playerHandle, preGame.getName(), baseName, true);
			
			return true;
		}
	}
	
	public static class ThisWorldCommand implements GameCommand {

		@Override
		public boolean execute(Player playerHandle) {
			
			PreGame preGame = GameManager.findPreGame(playerHandle.getUniqueId());
			if (preGame == null) {
				playerHandle.sendMessage(RED + "You are not creating a game!");
				return true;
			}
			
			World world = playerHandle.getWorld();
			if (world.getEnvironment() != Environment.NORMAL) {
				playerHandle.sendMessage(RED + "Games can be only started in the overworld!");
				return true;
			}
			
			if (!WorldManager.worldsExist(world.getName())) {
				playerHandle.sendMessage(RED + "Can't find a nether or an end to link with this world!");
				return true;
			}
			
			String baseName = world.getName();
			playerHandle.sendMessage(AQUA + "Creating a game in this world.");

			GameManager.createGame(playerHandle, preGame.getName(), baseName, false);
			
			return false;
		}
	}
}