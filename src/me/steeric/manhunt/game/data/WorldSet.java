package me.steeric.manhunt.game.data;

import org.bukkit.GameRule;
import org.bukkit.World;

import me.steeric.manhunt.managing.WorldManager;

public class WorldSet {
	
	public World[] worlds = new World[3];
	public String baseName;
	
	public WorldSet(String baseName, boolean createWorlds) {
		
		if (!createWorlds) {
			this.worlds = WorldManager.getWorlds(baseName);
		} else {
			this.worlds = WorldManager.createGameWorlds(baseName);
		}
		
		for (World world : this.worlds) {
			
			GameRule<Boolean> rule = GameRule.ANNOUNCE_ADVANCEMENTS;
			world.setGameRule(rule, false);
		}
		
		this.baseName = baseName;
		
	}
	
	@Override
	public String toString() {
		return this.baseName;
	}

}
