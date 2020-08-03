package me.steeric.manhunt.commands.pregame;

import static net.md_5.bungee.api.ChatColor.AQUA;
import static net.md_5.bungee.api.ChatColor.RED;

import org.bukkit.entity.Player;

import me.steeric.manhunt.commands.GeneralCommand;
import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.Game.GameState;
import me.steeric.manhunt.managing.GameManager;

public class HeadStartCommand implements GeneralCommand {

	@Override
	public boolean execute(Player player, String time) {
		
		Game game = GameManager.inGame(player);
		
		if (game == null) {
			player.sendMessage(RED + "You are not in a game!");
			return true;
		}
		
		if (!game.getAdmin().equals(game.findPlayer(player).getPlayer()) && !player.hasPermission("bettermanhunt.admin")) {
			player.sendMessage(RED + "You don't have the permissions to do that!");
			return true;
		}
		
		if (game.getState() == GameState.RUNNING || game.getState() == GameState.GAME_OVER) {
			player.sendMessage(RED + "You can't do that now! The game has already started!");
			return true;
		}
		
		int headstart = -1;
		
		try {
			headstart = Integer.valueOf(time);
		} catch (Exception e) {
			return false;
		}
		
		if (headstart > -1) {
			game.setHeadStartTime(headstart);
			player.sendMessage(AQUA + "Head start time updated!");
			return true;
		}
		
		return false;
	}

}
