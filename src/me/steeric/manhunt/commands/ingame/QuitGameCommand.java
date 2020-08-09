package me.steeric.manhunt.commands.ingame;

import static net.md_5.bungee.api.ChatColor.AQUA;

import java.util.List;

import me.steeric.manhunt.game.players.AbstractManhuntPlayer;
import org.bukkit.entity.Player;

import me.steeric.manhunt.commands.GameCommand;
import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.data.PreJoin;
import me.steeric.manhunt.game.managing.GameManager;

public class QuitGameCommand implements GameCommand {

	@Override
	public boolean execute(Player playerHandle) {
		
		Game game = GameManager.inGame(playerHandle);
		
		if (game == null) {
		
			Game preJoinedGame = GameManager.hasPreJoined(playerHandle);
			
			if (preJoinedGame == null) return true;
			
			List<PreJoin> prejoins = preJoinedGame.getPreJoins();
			int index = -1;
			for (int i = 0; i < prejoins.size(); i++) {
				
				PreJoin prejoin = prejoins.get(i);
				if (prejoin.getPlayer().equals(playerHandle.getUniqueId())) index = i;
				
			}
			
			if (index >= 0) { 
				prejoins.remove(index); 
				playerHandle.sendMessage(AQUA + "Joining cancelled!");
			}
			
			return true;
		}
		
		AbstractManhuntPlayer player = game.findPlayer(playerHandle);
		game.removePlayer(player);
		player.restoreData();
		
		playerHandle.sendMessage(AQUA + "Teleporting you to your location before game start!");
		return true;
	}
	
	

}
