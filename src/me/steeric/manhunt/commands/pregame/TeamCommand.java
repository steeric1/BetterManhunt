package me.steeric.manhunt.commands.pregame;

import static net.md_5.bungee.api.ChatColor.AQUA;
import static net.md_5.bungee.api.ChatColor.RED;

import org.bukkit.entity.Player;

import me.steeric.manhunt.commands.GameCommand;
import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.Game.GameState;
import me.steeric.manhunt.game.Manhunter.PlayerType;
import me.steeric.manhunt.game.data.PreJoin;
import me.steeric.manhunt.managing.GameManager;

public class TeamCommand {
	
	public static class TeamHuntersCommand implements GameCommand {

		@Override
		public boolean execute(Player player) {

			Game game = GameManager.inGame(player);
			
			if (game == null) {
				
				PreJoin preJoin = GameManager.findPreJoin(player.getUniqueId());
				
				if (preJoin == null) {
					player.sendMessage(RED + "You are not in a game!");
					return true;
				}
				
				GameManager.joinGame(player, PlayerType.HUNTER, preJoin.getGame().getName());
				
				player.sendMessage(AQUA + "Successfully joined game!\nYou are now a hunter!");
				return true;
				
			} else if (game.getState() == GameState.RUNNING || game.getState() == GameState.GAME_OVER) {
				
				player.sendMessage(RED + "You can't change teams now! The game has already started!");
				return true;
			}
			
			String message = GameManager.changeTeams(player, game, PlayerType.HUNTER);
			player.sendMessage(message);
			
			return true;
		}
	}
	
	public static class TeamRunnersCommand implements GameCommand {

		@Override
		public boolean execute(Player player) {

			Game game = GameManager.inGame(player);
			
			if (game == null) {
				
				PreJoin preJoin = GameManager.findPreJoin(player.getUniqueId());
				
				if (preJoin == null) {
					player.sendMessage(RED + "You are not in a game!");
					return true;
				}
				
				GameManager.joinGame(player, PlayerType.RUNNER, preJoin.getGame().getName());
				
				player.sendMessage(AQUA + "Successfully joined game!\nYou are now a runner!");
				return true;
				
			} else if (game.getState() == GameState.RUNNING || game.getState() == GameState.GAME_OVER) {
				
				player.sendMessage(RED + "You can't change teams now! The game has already started!");
				return true;
			}
			
			String message = GameManager.changeTeams(player, game, PlayerType.RUNNER);
			player.sendMessage(message);
			
			return true;
		}
	}
}
