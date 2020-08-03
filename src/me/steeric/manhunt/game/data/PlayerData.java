package me.steeric.manhunt.game.data;

import java.util.Collection;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerData {
	
	private Location location;
	private ItemStack[] inventory;
	private ItemStack[] enderchest;
	private Location spawnLocation;
//	private HashMap<Advancement, Collection<String>> advancementCriteria;
	private GameMode gamemode;
	private Collection<PotionEffect> effects;
	private double healthScale;
	private boolean isHealthScaled;
	private double health;
	private float exp;
	private int expLevel;
	private float saturation;
	private int foodLevel;
	private Location compassTarget;
	
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
