package me.steeric.manhunt;

import java.util.ArrayList;

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
import me.steeric.manhunt.game.Manhunter;
import me.steeric.manhunt.game.Manhunter.PlayerType;
import me.steeric.manhunt.managing.GameManager;
import me.steeric.manhunt.managing.PlayerManager;
import me.steeric.manhunt.managing.WorldManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class EventListeners implements Listener {
	
	private final boolean attackTeam;
	
	public EventListeners() {
		attackTeam = Manhunt.config.getBoolean("attack-own-team");
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		
		if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
			
			Player player = event.getEntity().getKiller();
			Game game = GameManager.inGame(player);
			
			if (game == null) return;
			
			if (game.findPlayer(player).getType() == PlayerType.RUNNER && game.getState() == GameState.RUNNING) {
				game.gameOver(PlayerType.RUNNER);
			}	
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		
		if (event.getEntity() instanceof Player) {
			
			Player player = (Player) event.getEntity();
			Game game = GameManager.inGame(player);
			
			if (game == null) return;
			
			Manhunter p = game.findPlayer(player);			
			double health = player.getHealth() - event.getFinalDamage();
			
			if (health <= 0) {
				
				event.setCancelled(true);
				
				if (p.getType() == PlayerType.RUNNER && game.getState() == GameState.RUNNING) {
					
					player.setGameMode(GameMode.SPECTATOR);
					player.setPlayerListName(ChatColor.RED + " [" + p.getType().toString().substring(0, 1).toUpperCase() + "] " + player.getName() + " ");
					PlayerManager.dropItems(player);
					
					TextComponent[] message = new TextComponent[3];

					message[0] = new TextComponent("You died! Press 'QUIT' to quit the game. [ ");
					message[0].setColor(ChatColor.AQUA);
					
					message[1] = new TextComponent("QUIT");
					message[1].setColor(ChatColor.WHITE);
					message[1].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quitgame"));
					
					message[2] = new TextComponent(" ]");
					message[2].setColor(ChatColor.AQUA);
					
					player.spigot().sendMessage(message);
					
					game.decrementRunnersLeft();
					p.setAlive(false);

			
				} else if (p.getType() == PlayerType.HUNTER && game.getState() == GameState.RUNNING) {
					
					p.setAlive(false);
					PlayerManager.respawnHunter(p, game);
					
				}
				
				if (event instanceof EntityDamageByEntityEvent) {
					
					EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
					Entity killerEntity = e.getDamager();
					
					if (killerEntity instanceof Player) {
						
						Player killer = (Player) killerEntity;
						Game g = GameManager.inGame(killer);
						
						if (!game.equals(g)) return;
						
						Manhunter mh = game.findPlayer(killer);
						
						game.deathMessage(p.toString() + " was killed by " + mh.toString(), p);
					
					} else {
						game.deathMessage(p.toString() + " died", p);
					}
					
				} else {
					game.deathMessage(p.toString() + " died", p);
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
		
		Manhunter mhdamager = damagerGame.findPlayer(damager);
		Manhunter mhtarget = damagerGame.findPlayer(target);
		
		if (mhdamager.getType() == mhtarget.getType()) event.setCancelled(true);
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		
		Player player = event.getPlayer();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;
		
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) {
			
			if (game.getState() == GameState.GAME_OVER && !event.getTo().getWorld().equals(game.getWorlds().worlds[0])
					&& !event.getTo().getWorld().equals(game.getWorlds().worlds[1]) && !event.getTo().getWorld().equals(game.getWorlds().worlds[2])) {
				
				Manhunter p = game.findPlayer(player);
				
				if (!p.isDataRestored()) {
					
					event.setCancelled(true);
					Location location = event.getTo();
					p.restoreData();
					player.teleport(location);
					
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
		
		Player player = event.getPlayer();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;
		
		Manhunter p = game.findPlayer(player);
		
		if (game.getState() == GameState.RUNNING && p.getType() == PlayerType.HUNTER) {
			p.setCompassTargetAtQuit(player.getCompassTarget());
		}
		
		if (game.getState() == GameState.GAME_OVER) {
			p.restoreData();
		}
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		
		// update player's compass target
		// based on corresponding MHPlayer object
		
		Player player = event.getPlayer();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;
		
		Manhunter p = game.findPlayer(player);
		
		if (p.isAlive()) 
			player.setPlayerListName(ChatColor.GREEN + " [" + p.getType().toString().substring(0, 1).toUpperCase() + "] " + player.getName() + " ");
		else
			player.setPlayerListName(ChatColor.RED + " [" + p.getType().toString().substring(0, 1).toUpperCase() + "] " + player.getName() + " ");

		if (p.getType() == PlayerType.HUNTER && game.getState() == GameState.RUNNING) {
			player.setCompassTarget(p.getCompassTargetAtQuit());
			p.updateTracker();
		}
	}
	
	@EventHandler
	public void onPortalUse(PlayerPortalEvent event) {
		
		// MC servers do not automatically link worlds together
		// it needs to get done manually with PlayerPortalEvent
		
		Game game = GameManager.inGame(event.getPlayer());
		
		if (game == null) return;
		
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
			
			if (event.getFrom().getWorld().getEnvironment() == Environment.NORMAL) {
				event.setTo(new Location(game.getWorlds().worlds[1], event.getTo().getX(), event.getTo().getY(), event.getTo().getZ()));
			} else if (event.getFrom().getWorld().getEnvironment() == Environment.NETHER) {
				event.setTo(new Location(game.getWorlds().worlds[0], event.getTo().getX(), event.getTo().getY(), event.getTo().getZ()));
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
				endLocation = new Location(game.getWorlds().worlds[2], loc.x, event.getTo().getY(), loc.z);
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
		
		Player player = event.getPlayer();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;
		
		Manhunter mhplayer = game.findPlayer(player);
		
		// if the player that moved is a runner, get a list of all
		// hunters in the game
		if (mhplayer.getType() == PlayerType.RUNNER && game.getState() == GameState.RUNNING) {
			
			ArrayList<Manhunter> hunters = game.getHunters();
			
			for (int i = 0; i < hunters.size(); i++) {
				
				hunters.get(i).updateTracker();
			}
		} else if (mhplayer.getType() == PlayerType.HUNTER && (!game.isHeadStartOver() || !mhplayer.isAlive()) && game.getState() == GameState.RUNNING) {
			event.setCancelled(true);
		}
	}
}