package me.steeric.manhunt;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.steeric.manhunt.commands.CommandHandler;
import me.steeric.manhunt.commands.TabHandler;
import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.MilestoneTracker;
import me.steeric.manhunt.game.PlayerTracking;
import me.steeric.manhunt.managing.ChatManager;
import me.steeric.manhunt.managing.GameManager;

public class Manhunt extends JavaPlugin {
	
	// server-related objects for referencing in other classes
	public static FileConfiguration config; 
	public static Plugin manhuntPlugin;

	@Override
	public void onEnable() {
				
		// executors for plugin commands
		CommandHandler handler = new CommandHandler();
		TabHandler completer = new TabHandler();
		String[] commands = {
				"manhunt", 
				"newworld", "findworld", "thisworld", "teamrunners", "teamhunters", "headstart",
				"toall", "toteam", "quitgame",
			};
		
		for (String cmd : commands) {
			PluginCommand command = this.getCommand(cmd);
			if (command != null) {
				command.setExecutor(handler);
				command.setTabCompleter(completer);
			}
		}
		
		// load config.yml
		this.saveDefaultConfig();
		config = this.getConfig();
		
		// register events
		this.getServer().getPluginManager().registerEvents(new EventListeners(), this);
		this.getServer().getPluginManager().registerEvents(new MilestoneTracker(), this);
		this.getServer().getPluginManager().registerEvents(new ChatManager(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerTracking(), this);


		
		// initialize main Plugin object
		manhuntPlugin = this;

	}
	
	@Override
	public void onDisable() {

		// delete all games (saving games over server shutdown not implemented yet!)
		for (Game game : GameManager.games) {
			GameManager.deleteGame(game, null);
		}
		
	}
	
}
