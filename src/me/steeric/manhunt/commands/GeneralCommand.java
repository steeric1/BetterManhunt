package me.steeric.manhunt.commands;

import org.bukkit.entity.Player;

public interface GeneralCommand {
	boolean execute(Player playerHandle, String name);
}
