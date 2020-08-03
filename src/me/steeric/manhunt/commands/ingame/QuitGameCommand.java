package me.steeric.manhunt.commands.ingame;

import static net.md_5.bungee.api.ChatColor.AQUA;

import java.util.List;

import org.bukkit.entity.Player;

import me.steeric.manhunt.commands.GameCommand;
import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.Manhunter;
import me.steeric.manhunt.game.data.PreJoin;
import me.steeric.manhunt.managing.GameManager;

public class QuitGameCommand implements GameCommand {

	@Override
	public boolean execute(Player player) {
		
		Game game = GameManager.inGame(player);
		
		if (game == null) {
		
			Game preJoinedGame = GameManager.hasPreJoined(player);
			
			if (preJoinedGame == null) return true;
			
			List<PreJoin> prejoins = preJoinedGame.getPreJoins();
			int index = -1;
			for (int i = 0; i < prejoins.size(); i++) {
				
				PreJoin prejoin = prejoins.get(i);
				if (prejoin.getPlayer().equals(player.getUniqueId())) index = i;
				
			}
			
			if (index >= 0) { 
				prejoins.remove(index); 
				player.sendMessage(AQUA + "Joining cancelled!");
			}
			
			return true;
		}
		
		Manhunter p = game.findPlayer(player);
		game.removePlayer(p);
		p.restoreData();
		
		player.sendMessage(AQUA + "Teleporting you to your location before game start!");
		return true;
	}
	
	

}
