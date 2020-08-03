package me.steeric.manhunt.game.data;

import java.util.UUID;

public class PreGame {
	
	private final UUID creator;
	private final String name;
	
	public PreGame(UUID creator, String name) {
		this.creator = creator;
		this.name = name;
	}
	
	public UUID getCreator() {
		return this.creator;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean equals(Object other) {
		
		if (other == null) return false;
		
		if (!(other instanceof PreGame)) return false;
		
		PreGame otherGame = (PreGame) other;
		
		if (otherGame.getCreator().equals(this.creator) && otherGame.getName().equals(this.name)) return true;
		
		return false;
	}

}
