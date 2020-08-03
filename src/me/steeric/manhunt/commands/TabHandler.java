package me.steeric.manhunt.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.steeric.manhunt.managing.GameManager;

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
				
				List<String> result = this.manhuntCommands.stream()
						.filter(str -> str.startsWith(args[0]))
						.collect(Collectors.toList());
				
				return result;
			}

			if (args.length == 2) {
				
				if (args[0].equals("create") || args[0].equals("list")) return new ArrayList<>();
				
				List<String> games = GameManager.games.stream()
						.map(game -> game.getName())
						.filter(name -> name.startsWith(args[1]))
						.collect(Collectors.toList());
				
				return games;
			}
		}
		
		return new ArrayList<>();
	}

}
