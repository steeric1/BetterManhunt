package me.steeric.manhunt.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.steeric.manhunt.commands.general.CreateCommand;
import me.steeric.manhunt.commands.general.DeleteCommand;
import me.steeric.manhunt.commands.general.JoinCommand;
import me.steeric.manhunt.commands.general.ListCommand;
import me.steeric.manhunt.commands.general.StartCommand;
import me.steeric.manhunt.commands.ingame.ChatModeCommand;
import me.steeric.manhunt.commands.ingame.QuitGameCommand;
import me.steeric.manhunt.commands.pregame.HeadStartCommand;
import me.steeric.manhunt.commands.pregame.TeamCommand;
import me.steeric.manhunt.commands.pregame.WorldCommand;

public class CommandHandler implements CommandExecutor {
	
	/*
	 * PERMISSIONS for commands:
	 * 
	 * bettermanhunt.createnewworld
	 *    - if mapping create-new-world in config.yml is set to true, players with this permission can create new worlds upon game creation
	 * 
	 * bettermanhunt.admin
	 *    - this permission allows a player to be in complete control of all games
	 * 
	 */
	
	// main class for plugin command handling
	
	// general commands: create, delete, join, list, start
	Map<String, GeneralCommand> generalCommands;
	GameCommand list;
	// pre game commands: newworld, findworld, teamrunners, teamhunters, [headstart (needs a parameter, hence inherits GeneralCommand)]
	// in game commands: toall, toteam, quitgame
	GeneralCommand headstart;
	Map<String, GameCommand> gameCommands;
	
	public CommandHandler() {
		
		// init general commands
		this.generalCommands = new HashMap<>();
		this.gameCommands = new HashMap<>();
		
		this.generalCommands.put("create", new CreateCommand());
		this.generalCommands.put("delete", new DeleteCommand());
		this.generalCommands.put("join", new JoinCommand());
		this.generalCommands.put("start", new StartCommand());
		
		// headstart and list require their own objects
		this.list = new ListCommand();	
		this.headstart = new HeadStartCommand();
		
		// init pre game commands
		this.gameCommands.put("newworld", new WorldCommand.NewWorldCommand());
		this.gameCommands.put("findworld", new WorldCommand.FindWorldCommand());
		this.gameCommands.put("thisworld", new WorldCommand.ThisWorldCommand());
		this.gameCommands.put("teamrunners", new TeamCommand.TeamRunnersCommand());
		this.gameCommands.put("teamhunters", new TeamCommand.TeamHuntersCommand());
		
		// init in game commands
		this.gameCommands.put("toall", new ChatModeCommand.ToAllCommand());
		this.gameCommands.put("toteam", new ChatModeCommand.ToTeamCommand());
		this.gameCommands.put("quitgame", new QuitGameCommand());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			
		if (!(sender instanceof Player)) return true; // plugin doesn't provide an interface for console

		Player player = (Player) sender;
				
		if (label.equals("manhunt")) {
						
			if (args.length == 0) return false;
			if (args[0].equals("list")) return this.list.execute(player);
			if (args.length < 2) return false;
			
			String action = args[0];
			String name = args[1];
						
			return this.generalCommands.get(action).execute(player, name);
		
		} else {
			
			if (label.equals("headstart") && args.length > 0)
				return this.headstart.execute(player, args[0]);
			
			return this.gameCommands.get(label).execute(player);
		}
	}
}
