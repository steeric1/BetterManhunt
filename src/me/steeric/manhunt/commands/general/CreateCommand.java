package me.steeric.manhunt.commands.general;

import static net.md_5.bungee.api.ChatColor.AQUA;
import static net.md_5.bungee.api.ChatColor.RED;
import static net.md_5.bungee.api.ChatColor.WHITE;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.steeric.manhunt.Manhunt;
import me.steeric.manhunt.commands.GeneralCommand;
import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.data.PreGame;
import me.steeric.manhunt.game.managing.GameManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CreateCommand implements GeneralCommand {
	
	public boolean execute(Player playerHandle, String name) {
		
		Game game = GameManager.findGame(name);
		
		if (game != null) { // a game by the same name already exists
			playerHandle.sendMessage(RED + "A game by that name already exists! Try another name!");
			return true;
		}
		
		GameManager.preGames.add(new PreGame(playerHandle.getUniqueId(), name));

		if (playerHandle.hasPermission("bettermanhunt.createnewworld") && Manhunt.config.getBoolean("create-new-world")) {
			
			TextComponent[] creationMessage = getCreationMessage(name);
			playerHandle.spigot().sendMessage(creationMessage);
		
		} else {
			
			playerHandle.sendMessage(AQUA + "Creating game " + WHITE + name + ".");
			Bukkit.dispatchCommand(playerHandle, "/findworld");
			return true;
			
		}
		
		return true;
	}
	
	private static TextComponent[] getCreationMessage(String name) {
		
		TextComponent[] creationMessage = new TextComponent[10];
		
		creationMessage[0] = new TextComponent("Creating game ");
		creationMessage[0].setColor(AQUA);
		
		creationMessage[1] = new TextComponent(name);
		creationMessage[1].setColor(WHITE);
		
		creationMessage[2] = new TextComponent(". Choose a world creation option: \n  [");
		creationMessage[2].setColor(AQUA);
		
		creationMessage[3] = new TextComponent("FIND A WORLD");
		creationMessage[3].setColor(WHITE);
		creationMessage[3].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/findworld"));
		
		creationMessage[4] = new TextComponent("]\n  [");
		creationMessage[4].setColor(AQUA);
		
		creationMessage[5] = new TextComponent("THIS WORLD");
		creationMessage[5].setColor(WHITE);
		creationMessage[5].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/thisworld"));
		
		creationMessage[6] = new TextComponent("]\n  [");
		creationMessage[6].setColor(AQUA);
		
		creationMessage[7] = new TextComponent("NEW WORLD");
		creationMessage[7].setColor(WHITE);
		creationMessage[7].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/newworld"));
		creationMessage[7].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
				new ComponentBuilder("WARNING: Creating worlds on the fly can crash servers that are not powerful. "
						+ "Perform this action at your own risk!").color(RED).create()));
		
		creationMessage[8] = new TextComponent(" (risky)");
		creationMessage[8].setColor(RED);
		creationMessage[8].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
				new ComponentBuilder("WARNING: Creating worlds on the fly can crash servers that are not powerful. "
						+ "Perform this action at your own risk!").color(RED).create()));
		
		creationMessage[9] = new TextComponent("]");
		creationMessage[9].setColor(AQUA);
		
		return creationMessage;
	}

}
