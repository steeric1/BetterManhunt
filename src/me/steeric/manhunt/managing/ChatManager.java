package me.steeric.manhunt.managing;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.Manhunter;
import me.steeric.manhunt.game.Manhunter.PlayerType;

public class ChatManager implements Listener {
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
				
		Player player = event.getPlayer();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;

		Manhunter p = game.findPlayer(player);
		String originalMessage = event.getMessage();
		String message;
		event.setCancelled(true);

		// [TEAM] : Steeric [R] > moi
		
		message = p.getChatMode() + " : " + player.getName() + ChatColor.GREEN + " [" + p.getType().toString().substring(0, 1).toUpperCase() + "] " + ChatColor.WHITE + "> " + originalMessage;
		game.notifyTeam(message, p.getType());
		
		if (p.getChatMode() == ChatMode.TO_ALL) {
			message = p.getChatMode() + " : " + player.getName() + ChatColor.RED + " [" + p.getType().toString().substring(0, 1).toUpperCase() + "] " + ChatColor.WHITE + "> " + originalMessage;
			
			if (p.getType() == PlayerType.HUNTER)
				game.notifyTeam(message, PlayerType.RUNNER);
			else if (p.getType() == PlayerType.RUNNER)
				game.notifyTeam(message, PlayerType.HUNTER);
			
		}
	}
	
	public static enum ChatMode {
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
