package me.steeric.manhunt.game.managing;

import java.util.ArrayList;

import me.steeric.manhunt.game.players.Hunter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.steeric.manhunt.Manhunt;
import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.PlayerTracking;
import net.md_5.bungee.api.ChatColor;

public class PlayerManager {

	public static void dropItems(Player player, Location location, int xp) {
		
		ArrayList<ItemStack> inventory = new ArrayList<>();
	
		for (ItemStack stack : player.getInventory().getContents()) {
			
			if (stack != null) inventory.add(stack);
		}
		
		player.getInventory().clear();
				
		if (location == null) return;
		
		for (ItemStack stack : inventory) {
						
			if (PlayerTracking.isTracker(stack)) continue;
			player.getWorld().dropItemNaturally(location, stack);
			
		}
		
		if (xp > 0) (player.getWorld().spawn(location, ExperienceOrb.class)).setExperience(xp);
	}

	public static void dropItems(Player player) {
		
		dropItems(player, player.getLocation(), (Math.min(player.getLevel() * 7, 100)));
	}
	
	public static void respawnHunter(Hunter hunter, Game game) {
		
		Player playerHandle = hunter.getPlayerHandle();
		if (playerHandle == null)
			return;

		playerHandle.setGameMode(GameMode.SPECTATOR);
		playerHandle.setPlayerListName(ChatColor.RED + hunter.getPlayerListName());

		int xp = playerHandle.getLevel() * 7;
		if (xp > 100) xp = 100;
		
		Location spawnLocation = playerHandle.getBedSpawnLocation();
		Location location = playerHandle.getLocation();
							
		playerHandle.sendMessage(ChatColor.AQUA + "You died!");
		playerHandle.sendMessage(ChatColor.GOLD + "Respawning...");
		
		dropItems(playerHandle, location, xp);
		playerHandle.getInventory().addItem(PlayerTracking.getTracker());
		
		if (spawnLocation != null) {
			Location loc = new Location(spawnLocation.getWorld(), spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());
			loc.setDirection(loc.getDirection().setX(0).setY(1).setZ(0));
			playerHandle.teleport(loc);
		} else {
			Location gameSpawn = game.getWorlds().worlds[0].getSpawnLocation();
			Location loc = new Location(gameSpawn.getWorld(), gameSpawn.getX(), gameSpawn.getY(), gameSpawn.getZ());
			loc.setDirection(loc.getDirection().setX(0).setY(1).setZ(0));
			playerHandle.teleport(loc);
		}
		
		playerHandle.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 255, false, false, true));

		Bukkit.getScheduler().scheduleSyncDelayedTask(Manhunt.manhuntPlugin, () -> {
			playerHandle.setHealth(20.0);
			playerHandle.setFireTicks(0);
			playerHandle.setFoodLevel(20);
			playerHandle.setSaturation(20);
			playerHandle.setLevel(0);
			playerHandle.setExp(0.0f);
			playerHandle.setRemainingAir(playerHandle.getMaximumAir());
		}, 10);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Manhunt.manhuntPlugin, () -> {
			playerHandle.setGameMode(GameMode.SURVIVAL);
			hunter.setDead(false);
			playerHandle.setPlayerListName(ChatColor.GREEN + hunter.getPlayerListName());
		}, 20);
	}
	
}
