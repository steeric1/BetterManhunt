package me.steeric.manhunt.managing;

import java.util.ArrayList;

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
import me.steeric.manhunt.game.Manhunter;
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
		
		if (xp > 0) ((ExperienceOrb) player.getWorld().spawn(location, ExperienceOrb.class)).setExperience(xp);	
	}

	public static void dropItems(Player player) {
		
		dropItems(player, player.getLocation(), (player.getLevel() * 7 > 100 ? 100 : player.getLevel() * 7));
	}
	
	public static void respawnHunter(Manhunter mh, Game game) {
		
		Player player = Bukkit.getPlayer(mh.getPlayer());
		player.setGameMode(GameMode.SPECTATOR);
		player.setPlayerListName(ChatColor.RED + " [" + mh.getType().toString().substring(0, 1).toUpperCase() + "] " + player.getName() + " ");

		int xp = player.getLevel() * 7;
		if (xp > 100) xp = 100;
		
		Location spawnLocation = player.getBedSpawnLocation();
		Location location = player.getLocation();
							
		player.sendMessage(ChatColor.AQUA + "You died!");
		player.sendMessage(ChatColor.GOLD + "Respawning...");
		
		dropItems(player, location, xp);
		player.getInventory().addItem(PlayerTracking.getTracker());
		
		if (spawnLocation != null) {
			Location loc = new Location(spawnLocation.getWorld(), spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());
			loc.setDirection(loc.getDirection().setX(0).setY(1).setZ(0));
			player.teleport(loc);
		} else {
			Location gameSpawn = game.getWorlds().worlds[0].getSpawnLocation();
			if (gameSpawn != null) {
				Location loc = new Location(gameSpawn.getWorld(), gameSpawn.getX(), gameSpawn.getY(), gameSpawn.getZ());
				loc.setDirection(loc.getDirection().setX(0).setY(1).setZ(0));
				player.teleport(loc);
			}
		}
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 255, false, false, true));

		Bukkit.getScheduler().scheduleSyncDelayedTask(Manhunt.manhuntPlugin, new Runnable() {
			
			@Override
			public void run() {

				player.setHealth(20.0);
				player.setFireTicks(0);
				player.setFoodLevel(20);
				player.setSaturation(20);
				player.setLevel(0);
				player.setExp(0.0f);
				player.setRemainingAir(player.getMaximumAir());
			}
		}, 10);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Manhunt.manhuntPlugin, new Runnable() {
			
			@Override
			public void run() {
				player.setGameMode(GameMode.SURVIVAL);	
				mh.setAlive(true);
				player.setPlayerListName(ChatColor.GREEN + " [" + mh.getType().toString().substring(0, 1).toUpperCase() + "] " + player.getName() + " ");
			}
		}, 20);
	}
	
}
