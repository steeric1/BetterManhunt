package me.steeric.manhunt.commands.pregame;

import static net.md_5.bungee.api.ChatColor.RED;

import me.steeric.manhunt.game.players.Hunter;
import me.steeric.manhunt.game.players.Runner;
import org.bukkit.entity.Player;

import me.steeric.manhunt.commands.GameCommand;
import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.Game.GameState;
import me.steeric.manhunt.game.managing.GameManager;

public class TeamCommand {
	
	public static class TeamHuntersCommand implements GameCommand {

		@Override
		public boolean execute(Player playerHandle) {

			Game game = GameManager.inGame(playerHandle);
			if (game == null)
				return true;
			
			/*if (game == null) {
				
				PreJoin preJoin = GameManager.findPreJoin(player.getUniqueId());
				
				if (preJoin == null) {
					player.sendMessage(RED + "You are not in a game!");
					return true;
				}
				
				GameManager.joinGame(player, PlayerType.HUNTER, preJoin.getGame().getName());
				
				player.sendMessage(AQUA + "Successfully joined game!\nYou are now a hunter!");
				return true;
				
			} else*/
			if (game.getState() == GameState.RUNNING || game.getState() == GameState.GAME_OVER) {
				playerHandle.sendMessage(RED + "You can't change teams now! The game has already started!");
				return true;
			}
			
			String message = GameManager.changeTeams(playerHandle, game, Hunter.class);
			playerHandle.sendMessage(message);
			
			return true;
		}
	}
	
	public static class TeamRunnersCommand implements GameCommand {

		@Override
		public boolean execute(Player playerHandle) {

			Game game = GameManager.inGame(playerHandle);
			if (game == null)
				return true;
			
			/*if (game == null) {
				
				PreJoin preJoin = GameManager.findPreJoin(player.getUniqueId());
				
				if (preJoin == null) {
					player.sendMessage(RED + "You are not in a game!");
					return true;
				}
				
				GameManager.joinGame(player, PlayerType.RUNNER, preJoin.getGame().getName());
				
				player.sendMessage(AQUA + "Successfully joined game!\nYou are now a runner!");
				return true;
				
			} else*/
			if (game.getState() == GameState.RUNNING || game.getState() == GameState.GAME_OVER) {
				playerHandle.sendMessage(RED + "You can't change teams now! The game has already started!");
				return true;
			}
			
			String message = GameManager.changeTeams(playerHandle, game, Runner.class);
			playerHandle.sendMessage(message);
			
			return true;
		}
	}
}
