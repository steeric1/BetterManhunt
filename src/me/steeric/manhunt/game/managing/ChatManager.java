package me.steeric.manhunt.game.managing;

import me.steeric.manhunt.game.players.AbstractManhuntPlayer;
import me.steeric.manhunt.game.players.Hunter;
import me.steeric.manhunt.game.players.Runner;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.steeric.manhunt.game.Game;

public class ChatManager implements Listener {
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
				
		Player playerHandle = event.getPlayer();
		Game game = GameManager.inGame(playerHandle);
		
		if (game == null) return;

		AbstractManhuntPlayer player = game.findPlayer(playerHandle);
		String originalMessage = event.getMessage();
		String message;
		event.setCancelled(true);

		message = player.getChatMode() + " : " + playerHandle.getName() + ChatColor.GREEN + " [" + player.getClass().getSimpleName().substring(0, 1).toUpperCase() + "] " + ChatColor.WHITE + "> " + originalMessage;
		game.notifyTeam(message, player.getClass());
		
		if (player.getChatMode() == ChatMode.TO_ALL) {
			message = player.getChatMode() + " : " + playerHandle.getName() + ChatColor.RED + " [" + player.getClass().getSimpleName().substring(0, 1).toUpperCase() + "] " + ChatColor.WHITE + "> " + originalMessage;
			
			if (player instanceof Hunter)
				game.notifyTeam(message, Runner.class);
			else if (player instanceof Runner)
				game.notifyTeam(message, Hunter.class);
			
		}
	}
	
	public enum ChatMode {
		TO_ALL {
			@Override
			public String toString() {
				return ChatColor.AQUA + "[ALL]" + ChatColor.RESET;
			}
		},
		
		TO_TEAM {
			@Override
			public String toString() {
				return ChatColor.GREEN + "[TEAM]" + ChatColor.RESET;
			}
		}
	}

}
