package me.steeric.manhunt.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.steeric.manhunt.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.steeric.manhunt.game.managing.GameManager;

public class TabHandler implements TabCompleter {
	
	List<String> manhuntCommands;
	
	public TabHandler() {
		
		this.manhuntCommands = new ArrayList<>();
		
		this.manhuntCommands.add("create");
		this.manhuntCommands.add("delete");
		this.manhuntCommands.add("join");
		this.manhuntCommands.add("list");
		this.manhuntCommands.add("start");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (label.equals("manhunt")) {
			
			if (args.length == 0) return this.manhuntCommands;
			
			if (args.length == 1) {

				return this.manhuntCommands.stream()
						.filter(str -> str.startsWith(args[0]))
						.collect(Collectors.toList());
			}

			if (args.length == 2) {
				
				if (args[0].equals("create") || args[0].equals("list")) return new ArrayList<>();

				return GameManager.games.stream()
						.map(Game::getName)
						.filter(name -> name.startsWith(args[1]))
						.collect(Collectors.toList());
			}
		}
		
		return new ArrayList<>();
	}

}
