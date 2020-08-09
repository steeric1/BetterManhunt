package me.steeric.manhunt.commands.general;

import static net.md_5.bungee.api.ChatColor.AQUA;
import static net.md_5.bungee.api.ChatColor.RED;
import static net.md_5.bungee.api.ChatColor.WHITE;

import org.bukkit.entity.Player;

import me.steeric.manhunt.commands.GeneralCommand;
import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.Game.GameState;
import me.steeric.manhunt.game.data.PreJoin;
import me.steeric.manhunt.game.managing.GameManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class JoinCommand implements GeneralCommand {

	@Override
	public boolean execute(Player playerHandle, String name) {

		Game game = GameManager.findGame(name);
		
		if (game == null) { // if no game was found
			playerHandle.sendMessage(RED + "That game doesn't exist!");
			return true;
		} else if (game.getState() == GameState.RUNNING || game.getState() == GameState.GAME_OVER) {
			playerHandle.sendMessage(RED + "You can't join now! The game is already running!");
			return true;
		} else if (GameManager.hasJoined(playerHandle, game)) { // if player already joined that game
			playerHandle.sendMessage(RED + "You have already joined that game!");
			return true;
		} else if (GameManager.inGame(playerHandle) != null) {
			playerHandle.sendMessage(RED + "You cannot join two different games!");
			return true;
		}/* else {
			
			Game preJoinedGame = GameManager.hasPreJoined(player);
			
			if (preJoinedGame != null) {
				
				if (preJoinedGame.equals(game)) {
					player.sendMessage("You are already joining that game!");
					return true;
				}
			
				player.sendMessage(RED + "You are already joining another game!\nUse /quitgame to join your desired game!");
				return true;
			}
		} */

		game.showTeamSelectionGui(playerHandle);
		return true;
	}
	
	private static TextComponent[] getJoinMessage(Game game) {
		
		TextComponent[] joinMessage = new TextComponent[7];
		
		joinMessage[0] = new TextComponent("Joining game ");
		joinMessage[0].setColor(AQUA);
		
		joinMessage[1] = new TextComponent(game.getName());
		joinMessage[1].setColor(WHITE);
		
		joinMessage[2] = new TextComponent(". Join as [ ");
		joinMessage[2].setColor(AQUA);
		
		joinMessage[3] = new TextComponent("RUNNER");
		joinMessage[3].setColor(WHITE);
		joinMessage[3].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teamrunners"));
		
		joinMessage[4] = new TextComponent(" (" + game.getRunners().size() + ") ] / [ ");
		joinMessage[4].setColor(AQUA);
		
		joinMessage[5] = new TextComponent("HUNTER");
		joinMessage[5].setColor(WHITE);
		joinMessage[5].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teamhunters"));
		     
		joinMessage[6] = new TextComponent(" (" + game.getHunters().size() + ") ]");
		joinMessage[6].setColor(AQUA);
		
		return joinMessage;
	}
}
