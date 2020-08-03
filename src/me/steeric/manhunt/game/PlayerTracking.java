package me.steeric.manhunt.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.steeric.manhunt.Manhunt;
import me.steeric.manhunt.game.Manhunter.PlayerType;
import me.steeric.manhunt.managing.GameManager;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_16_R1.PacketPlayOutTitle.EnumTitleAction;

public class PlayerTracking implements Listener {
	
	private static final String TRACKER_NAME = ChatColor.GOLD + "Player Tracker";
	private static final String TRACKER_LORE = "Tracking to closest runner...";
	private static final boolean showPlayer = Manhunt.config.getBoolean("trackers-show-player");
	private static final boolean showDistance = Manhunt.config.getBoolean("trackers-show-distance");
	
	public static Location findClosestRunner(Location location, Game game) {
		
		List<Manhunter> runners = game.getRunners();
		List<Manhunter> runnersInSameWorld = new ArrayList<>();

		for (Manhunter runner : runners) {

			Player player = Bukkit.getPlayer(runner.getPlayer());
			if (player != null) {

				if (player.getGameMode() == GameMode.SPECTATOR) continue;

				if (player.getWorld().equals(location.getWorld())) {
					runnersInSameWorld.add(runner);
				}
			}
		}
		
		if (runnersInSameWorld.size() == 0) return null;

		Player closestPlayer = null;
		int index = 0;
		while (closestPlayer == null)
			closestPlayer = Bukkit.getPlayer(runnersInSameWorld.get(index++).getPlayer());

		Location closest = closestPlayer.getLocation();
		double distance;
		for (int i = index + 1; i < runnersInSameWorld.size(); i++) {
			
			Player player = Bukkit.getPlayer(runnersInSameWorld.get(i).getPlayer());
			if (player != null) {
				Location playerLocation = player.getLocation();
				distance = location.distanceSquared(playerLocation);

				if (distance < location.distanceSquared(closest)) {
					closest = playerLocation;
				}
			}
		}
		
		return closest;
	}
	
	public static ItemStack getTracker() {
		
		ItemStack item = new ItemStack(Material.COMPASS);
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return null;
		
		meta.setDisplayName(TRACKER_NAME);
		
		List<String> lore = new ArrayList<>();
		lore.add(TRACKER_LORE);
		
		meta.setLore(lore);
		
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		item.setItemMeta(meta);
		return item;
	}
	
	public static boolean isTracker(ItemStack item) {

		ItemMeta meta = item.getItemMeta();
		if (meta == null) return false;

		List<String> lore = meta.getLore();
		if (lore == null) return false;

		return item.getType() == Material.COMPASS && meta.getDisplayName().equals(TRACKER_NAME) && lore.get(0).equals(TRACKER_LORE);
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		
		if (!showDistance && !showPlayer) return;
		
		Player player = event.getPlayer();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;
		
		Manhunter mh = game.findPlayer(player);
		if (mh.getType() == PlayerType.RUNNER) return;
		
		Action action = event.getAction();
		
		if (action == Action.RIGHT_CLICK_AIR) {
			
			ItemStack item = event.getItem();
			if (item == null || !isTracker(item)) return;
			
			Material itemType = item.getType();

			if (itemType == Material.COMPASS) {
				
				TrackingTarget target = getClosestTarget(player.getLocation(), game);
				StringBuilder sb = new StringBuilder();
				
				if (target == null) {
					sb.append("�cNo runners found!");
				} else {
					if (target.player != null) sb.append("�r�6Tracking to: �o").append(target.player.getName());
					if (target.distance >= 0) sb.append("�r�6 | Distance: �o").append(target.distance).append(" blocks");
				}
				
				String text = sb.toString();
				
				PacketPlayOutTitle message = new PacketPlayOutTitle(EnumTitleAction.ACTIONBAR,
						ChatSerializer.a("{\"text\":\"" + text + "\"}"), 20, 100, 20);
				
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(message);
			}
		}
	}
	
	private static TrackingTarget getClosestTarget(Location location, Game game) {
		
		List<Manhunter> runners = game.getRunners();
		List<Manhunter> runnersInSameWorld = new ArrayList<>();

		for (Manhunter runner : runners) {

			Player playerHandle = Bukkit.getPlayer(runner.getPlayer());
			if (playerHandle != null) {
				if (playerHandle.getGameMode() == GameMode.SPECTATOR) continue;

				if (playerHandle.getWorld().equals(location.getWorld())) {
					runnersInSameWorld.add(runner);
				}
			}
		}
		
		if (runnersInSameWorld.size() == 0) return null;
		
		Player closest = null;
		int index = 0;
		while (closest == null)
			closest = Bukkit.getPlayer(runnersInSameWorld.get(index++).getPlayer());

		double distance;
		for (int i = index + 1; i < runnersInSameWorld.size(); i++) {
			
			Player playerHandle = Bukkit.getPlayer(runnersInSameWorld.get(i).getPlayer());
			if (playerHandle != null) {
				Location playerLocation = playerHandle.getLocation();
				distance = location.distanceSquared(playerLocation);

				if (distance < location.distanceSquared(closest.getLocation())) {
					closest = playerHandle;
				}
			}
		}
		
		double finalDistance = -1;
		if (showDistance) finalDistance = location.distance(closest.getLocation());
		if (!showPlayer) closest = null;
		
		return new TrackingTarget(closest, finalDistance);
	}
	
	
	// "struct" of data for tracking
	public static final class TrackingTarget {
		
		public Player player;
		public double distance;
		
		public TrackingTarget(Player player, double distance) {
			this.player = player;
			this.distance = distance;
		}
	}
}
