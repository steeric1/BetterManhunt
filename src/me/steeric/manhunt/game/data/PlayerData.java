package me.steeric.manhunt.game.data;

import java.util.Collection;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerData {
	
	private final Location location;
	private final ItemStack[] inventory;
	private final ItemStack[] enderchest;
	private final Location spawnLocation;
	private final GameMode gamemode;
	private final Collection<PotionEffect> effects;
	private double healthScale;
	private final boolean isHealthScaled;
	private final double health;
	private final float exp;
	private final int expLevel;
	private final float saturation;
	private final int foodLevel;
	private final Location compassTarget;
	
	public PlayerData(Player player) {
		
		this.location = player.getLocation();
		this.inventory = player.getInventory().getContents();
		
		this.enderchest = player.getEnderChest().getContents();
		
		if (player.getBedSpawnLocation() == null) {
			this.spawnLocation = player.getWorld().getSpawnLocation();
		} else {
			this.spawnLocation = player.getBedSpawnLocation();
	}
	
//		Iterator<Advancement> iterator = Bukkit.advancementIterator();
//		HashMap<Advancement, Collection<String>> advancementCriteria = new HashMap<>();
//		
//		while (iterator.hasNext()) {
//			Advancement advancement = iterator.next();
//			advancementCriteria.put(advancement, player.getAdvancementProgress(advancement).getAwardedCriteria());
//		}
//	
//		this.advancementCriteria = advancementCriteria;
		
		this.gamemode = player.getGameMode();
		this.effects = player.getActivePotionEffects();
		
		this.isHealthScaled = player.isHealthScaled();
		
		if (player.isHealthScaled()) {
			this.healthScale = player.getHealthScale();
		}
		
		this.health = player.getHealth();
		
		this.exp = player.getExp();
		this.expLevel = player.getLevel();
		this.saturation = player.getSaturation();
		this.foodLevel = player.getFoodLevel();
		this.compassTarget = player.getCompassTarget();

	}
	
	public void setDataToPlayer(Player player) {
		
		player.teleport(this.location);
		
		player.getInventory().setContents(this.inventory);
		player.getEnderChest().setContents(this.enderchest);
		
		player.setBedSpawnLocation(this.spawnLocation, true);
		
//		Iterator<Advancement> iterator = Bukkit.advancementIterator();
//		
//		while (iterator.hasNext()) {
//			
//			Advancement advancement = iterator.next();
//			
//			for (String crit : this.advancementCriteria.get(advancement)) {
//				player.getAdvancementProgress(advancement).awardCriteria(crit);
//			}
//			
//			
//		}
		
		player.setGameMode(this.gamemode);
		player.addPotionEffects(this.effects);
		
		player.setHealth(this.health);
		player.setHealthScaled(this.isHealthScaled);
		
		if (this.isHealthScaled) {
			player.setHealthScale(this.healthScale);
		}
		
		player.setExp(this.exp);
		player.setLevel(this.expLevel);
		player.setSaturation(this.saturation);
		player.setFoodLevel(this.foodLevel);
		player.setCompassTarget(this.compassTarget);
		
	}

}
