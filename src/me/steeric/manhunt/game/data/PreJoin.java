package me.steeric.manhunt.game.data;

import java.util.UUID;

import me.steeric.manhunt.game.Game;

public class PreJoin {
	
	private final UUID player;
	private final Game game;
	
	public PreJoin(UUID player, Game game) {
		this.player = player;
		this.game = game;
	}
	
	public UUID getPlayer() {
		return this.player;
	}
	
	public Game getGame() {
		return this.game;
	}
	
	@Override
	public boolean equals(Object other) {
		
		if (other == null) return false;
		if (!(other instanceof PreJoin)) return false;
		
		PreJoin otherJoin = (PreJoin) other;
		
		if (otherJoin.getPlayer().equals(this.player) && otherJoin.getGame().equals(this.game)) return true;
		
		return false;
	}

}
