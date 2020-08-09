package me.steeric.manhunt.commands.general;

import static net.md_5.bungee.api.ChatColor.RED;

import org.bukkit.entity.Player;

import me.steeric.manhunt.commands.GeneralCommand;
import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.Game.GameState;
import me.steeric.manhunt.game.managing.GameManager;

public class DeleteCommand implements GeneralCommand {

	@Override
	public boolean execute(Player playerHandle, String name) {

		Game game = GameManager.findGame(name);
		
		if (game == null) { // if game was not found
			playerHandle.sendMessage(RED + "That game doesn't exist!");
			return true;
		} 
		
		// check if player invoking command is the admin of the game
		if (!playerHandle.getUniqueId().equals(game.getAdmin()) && !playerHandle.hasPermission("bettermanhunt.admin")) {
			playerHandle.sendMessage(RED + "You don't have the permission to do that!");
			return true;
		}
		
		if (game.headStartNotOver() && game.getState() == GameState.RUNNING) {
			playerHandle.sendMessage(RED + "You can't delete this game right now!");
			return true;
		}
		
		GameManager.deleteGame(game, playerHandle);
		return true;
	}

}
