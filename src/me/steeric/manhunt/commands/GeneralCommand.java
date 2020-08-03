package me.steeric.manhunt.commands;

import org.bukkit.entity.Player;

public interface GeneralCommand {
	
	public abstract boolean execute(Player player, String name);
	
}
