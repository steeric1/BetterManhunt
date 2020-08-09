package me.steeric.manhunt;

import java.util.ArrayList;
import java.util.List;

import me.steeric.manhunt.game.players.AbstractManhuntPlayer;
import me.steeric.manhunt.game.players.Hunter;
import me.steeric.manhunt.game.players.Runner;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.Game.GameState;
import me.steeric.manhunt.game.managing.GameManager;
import me.steeric.manhunt.game.managing.PlayerManager;
import me.steeric.manhunt.game.managing.WorldManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class EventListeners implements Listener {
	
	private final boolean attackTeam;
	
	public EventListeners() {
		this.attackTeam = Manhunt.config.getBoolean("attack-own-team");
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		
		if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
			
			Player playerHandle = event.getEntity().getKiller();
			Game game = GameManager.inGame(playerHandle);
			
			if (game == null || playerHandle == null) return;
			
			if (game.findPlayer(playerHandle) instanceof Runner && game.getState() == GameState.RUNNING) {
				game.gameOver(Runner.class);
			}	
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		
		if (event.getEntity() instanceof Player) {
			
			Player playerHandle = (Player) event.getEntity();
			Game game = GameManager.inGame(playerHandle);
			
			if (game == null) return;
			
			AbstractManhuntPlayer manhuntPlayer = game.findPlayer(playerHandle);
			double health = playerHandle.getHealth() - event.getFinalDamage();
			
			if (health <= 0.0) {
				
				event.setCancelled(true);
				
				if (manhuntPlayer instanceof Runner && game.getState() == GameState.RUNNING) {
					
					playerHandle.setGameMode(GameMode.SPECTATOR);
					playerHandle.setPlayerListName(ChatColor.RED + manhuntPlayer.getPlayerListName());
					PlayerManager.dropItems(playerHandle);
					
					TextComponent[] message = new TextComponent[3];

					message[0] = new TextComponent("You died! Press 'QUIT' to quit the game. [ ");
					message[0].setColor(ChatColor.AQUA);
					
					message[1] = new TextComponent("QUIT");
					message[1].setColor(ChatColor.WHITE);
					message[1].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quitgame"));
					
					message[2] = new TextComponent(" ]");
					message[2].setColor(ChatColor.AQUA);
					
					playerHandle.spigot().sendMessage(message);
					
					game.decrementRunnersLeft((Runner) manhuntPlayer);
					manhuntPlayer.setDead(true);

				} else if (manhuntPlayer instanceof Hunter && game.getState() == GameState.RUNNING) {
					
					manhuntPlayer.setDead(true);
					PlayerManager.respawnHunter((Hunter) manhuntPlayer, game);
				}
				
				if (event instanceof EntityDamageByEntityEvent) {
					
					EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
					Entity killerEntity = e.getDamager();
					
					if (killerEntity instanceof Player) {
						
						Player killer = (Player) killerEntity;
						Game g = GameManager.inGame(killer);
						
						if (!game.equals(g)) return;
						
						AbstractManhuntPlayer killerManhuntPlayer = game.findPlayer(killer);
						
						game.deathMessage(manhuntPlayer.toString() + " was killed by " + killerManhuntPlayer.toString(), manhuntPlayer);
					
					} else {
						game.deathMessage(manhuntPlayer.toString() + " died", manhuntPlayer);
					}
					
				} else {
					game.deathMessage(manhuntPlayer.toString() + " died", manhuntPlayer);
				}
			} 
		}
	}
	
	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		
		if (this.attackTeam) return;
		if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;
		
		Player damager = (Player) event.getDamager();
		Player target = (Player) event.getEntity();
		
		Game damagerGame = GameManager.inGame(damager);
		Game targetGame = GameManager.inGame(target);
		
		if (damagerGame == null || targetGame == null) return;
		if (!damagerGame.equals(targetGame)) return;
		
		AbstractManhuntPlayer damagerManhuntPlayer = damagerGame.findPlayer(damager);
		AbstractManhuntPlayer targetManhuntPlayer = damagerGame.findPlayer(target);
		
		if (damagerManhuntPlayer.getClass() == targetManhuntPlayer.getClass())
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		
		Player playerHandle = event.getPlayer();
		Game game = GameManager.inGame(playerHandle);
		
		if (game == null) return;
		
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) {

			Location to = event.getTo();
			if (to == null || to.getWorld() == null) return;
			
			if (game.getState() == GameState.GAME_OVER && !to.getWorld().equals(game.getWorlds().worlds[0])
					&& !to.getWorld().equals(game.getWorlds().worlds[1]) && !to.getWorld().equals(game.getWorlds().worlds[2])) {
				
				AbstractManhuntPlayer player = game.findPlayer(playerHandle);
				
				if (!player.isPlayerDataRestored()) {
					
					event.setCancelled(true);
					Location location = event.getTo();
					player.restoreData();
					playerHandle.teleport(location);
				}
			}
		}
		
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		
		// a new Player object gets created for every login on server
		// we need to save tracker target for MHPlayer objects
		// MHPlayer objects do not get created again (UUID based)
		// this way, if we save tracker targets, on player login
		// players' tracker targets will not be reset to world spawn point
		
		Player playerHandle = event.getPlayer();
		Game game = GameManager.inGame(playerHandle);
		
		if (game == null) return;
		
		AbstractManhuntPlayer player = game.findPlayer(playerHandle);
		
		if (game.getState() == GameState.RUNNING && player instanceof Hunter)
			((Hunter) player).setCompassTargetAtQuit(playerHandle.getCompassTarget());
		
		if (game.getState() == GameState.GAME_OVER)
			player.restoreData();
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		
		// update player's compass target
		// based on corresponding MHPlayer object
		
		Player playerHandle = event.getPlayer();
		Game game = GameManager.inGame(playerHandle);
		
		if (game == null) return;
		
		AbstractManhuntPlayer player = game.findPlayer(playerHandle);
		
		if (!player.isDead())
			playerHandle.setPlayerListName(ChatColor.GREEN + player.getPlayerListName());
		else
			playerHandle.setPlayerListName(ChatColor.RED + player.getPlayerListName());

		if (player instanceof Hunter && game.getState() == GameState.RUNNING) {
			Hunter hunter = (Hunter) player;
			playerHandle.setCompassTarget(hunter.getCompassTargetAtQuit());
			hunter.updateTracker();
		}
	}
	
	@EventHandler
	public void onPortalUse(PlayerPortalEvent event) {
		
		// MC servers do not automatically link worlds together
		// it needs to get done manually with PlayerPortalEvent
		
		Game game = GameManager.inGame(event.getPlayer());
		
		if (game == null) return;

		Location from = event.getFrom();
		Location to = event.getTo();
		if (to == null || to.getWorld() == null || from.getWorld() == null) return;
		
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {

			if (from.getWorld().getEnvironment() == Environment.NORMAL) {
				event.setTo(new Location(game.getWorlds().worlds[1], to.getX(), to.getY(), to.getZ()));
			} else if (from.getWorld().getEnvironment() == Environment.NETHER) {
				event.setTo(new Location(game.getWorlds().worlds[0], to.getX(), to.getY(), to.getZ()));
			}
			
		} else if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {

			// just changing world is not enough in the case of the end
			// end main islands tend to generate with the approximated size
			// of 100 x 100 blocks

			// teleporting players to a random location inside of a circle
			// with the radius of 100 blocks

			Location endLocation;
			if (game.getEndLocation() == null) {
				WorldManager.vec2d loc = WorldManager.getEndLocation();
				endLocation = new Location(game.getWorlds().worlds[2], loc.x, to.getY(), loc.z);
				game.setEndLocation(endLocation);
			} else {
				endLocation = game.getEndLocation();
			}

			event.setTo(endLocation);
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		
		// when runners move, all hunters in the specific game
		// need to get their trackers updated
		
		Player playerHandle = event.getPlayer();
		Game game = GameManager.inGame(playerHandle);
		
		if (game == null) return;
		
		AbstractManhuntPlayer player = game.findPlayer(playerHandle);
		
		// if the player that moved is a runner, get a list of all
		// hunters in the game
		if (player instanceof Runner && game.getState() == GameState.RUNNING) {
			
			List<Hunter> hunters = game.getHunters();

			for (Hunter hunter : hunters) {
				hunter.updateTracker();
			}
		} else if (player instanceof Hunter && (game.headStartNotOver() || player.isDead()) && game.getState() == GameState.RUNNING) {
			event.setCancelled(true);
		}
	}
}