package me.steeric.manhunt.commands.ingame;

import org.bukkit.entity.Player;

import me.steeric.manhunt.commands.GameCommand;
import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.managing.ChatManager.ChatMode;
import me.steeric.manhunt.game.managing.GameManager;

public class ChatModeCommand {
	
	public static class ToAllCommand implements GameCommand {

		@Override
		public boolean execute(Player playerHandle) {
			
			Game game = GameManager.inGame(playerHandle);
			
			if (game == null) return true;
			
			game.findPlayer(playerHandle).setChatMode(ChatMode.TO_ALL);
			
			return true;
		}
	}
	
	public static class ToTeamCommand implements GameCommand {

		@Override
		public boolean execute(Player playerHandle) {
			
			Game game = GameManager.inGame(playerHandle);
			
			if (game == null) return true;
			
			game.findPlayer(playerHandle).setChatMode(ChatMode.TO_TEAM);
			
			return true;
		}
	}
}
