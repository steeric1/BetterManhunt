package me.steeric.manhunt.commands.general;

import static net.md_5.bungee.api.ChatColor.AQUA;
import static net.md_5.bungee.api.ChatColor.WHITE;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import me.steeric.manhunt.commands.GameCommand;
import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.Game.GameState;
import me.steeric.manhunt.game.managing.GameManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ListCommand implements GameCommand {
	
	@Override
	public boolean execute(Player playerHandle) {
		
		List<Game> games = GameManager.games;
		List<TextComponent> list = new ArrayList<>();
		
		TextComponent header = new TextComponent("List of existing games: ");
		header.setColor(AQUA);
		list.add(header);
		
		if (games.size() == 0) { // if no games
					
			TextComponent noGames = new TextComponent("\n  (no games)");
			noGames.setColor(AQUA);
			list.add(noGames);
			
		} else {
			
			for (Game g : games) { // otherwise loop through all games
				// if game is not started, offer the option for joining
				
				TextComponent li = new TextComponent("\n  - ");
				li.setColor(AQUA);
				list.add(li);
				
				TextComponent gameName = new TextComponent(g.getName());
				
				if (g.getState() == GameState.NOT_STARTED) {
					
					gameName.setColor(WHITE);
					gameName.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/manhunt join " + g.getName()));
					gameName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
							new ComponentBuilder("Join game").color(WHITE).create()));
					
				} else {
					gameName.setColor(AQUA);
				}
				
				// add game state in square brackets after game name
				TextComponent gameState = new TextComponent(" [" + g.getState() + "]");
				
				list.add(gameName);
				list.add(gameState);
				
			}
		}
		
		TextComponent footer = new TextComponent("\n\nCreate a new game with /manhunt create <game>");
		footer.setColor(AQUA);
		list.add(footer);
		
		// arraylist -> array (.spigot().sendMessage() requires an array)
		TextComponent[] listArr = new TextComponent[list.size()];
		for (int i = 0; i < list.size(); i++) {
			listArr[i] = list.get(i);
		}
		
		playerHandle.spigot().sendMessage(listArr);
		
		return true;
	}
	
	
	
}
