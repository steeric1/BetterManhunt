package me.steeric.manhunt.game;

import static org.bukkit.Material.COBBLESTONE;
import static org.bukkit.Material.IRON_BOOTS;
import static org.bukkit.Material.IRON_CHESTPLATE;
import static org.bukkit.Material.IRON_HELMET;
import static org.bukkit.Material.IRON_INGOT;
import static org.bukkit.Material.IRON_LEGGINGS;
import static org.bukkit.Material.STONE_AXE;
import static org.bukkit.Material.STONE_HOE;
import static org.bukkit.Material.STONE_PICKAXE;
import static org.bukkit.Material.STONE_SWORD;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.StructureType;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import me.steeric.manhunt.game.Manhunter.PlayerType;
import me.steeric.manhunt.game.data.MilestoneProgress.Milestone;
import me.steeric.manhunt.managing.GameManager;

public class MilestoneTracker implements Listener {
	
	private final Map<Material, Milestone> itemMilestones;
	
	public MilestoneTracker() {
		
		this.itemMilestones = this.initItemMilestones();
	}
	
	private EnumMap<Material, Milestone> initItemMilestones() {
		
		EnumMap<Material, Milestone> milestones = new EnumMap<>(Material.class);
		
		// get stone
		milestones.put(COBBLESTONE, Milestone.GET_STONE);
		
		// stone tools
		milestones.put(STONE_PICKAXE, Milestone.STONE_TOOLS);
		milestones.put(STONE_AXE, Milestone.STONE_TOOLS);
		milestones.put(STONE_SWORD, Milestone.STONE_TOOLS);
		milestones.put(STONE_HOE, Milestone.STONE_TOOLS);
		
		// get iron
		milestones.put(IRON_INGOT, Milestone.GET_IRON);
		
		// suit up
		milestones.put(IRON_BOOTS, Milestone.SUIT_UP);
		milestones.put(IRON_LEGGINGS, Milestone.SUIT_UP);
		milestones.put(IRON_CHESTPLATE, Milestone.SUIT_UP);
		milestones.put(IRON_HELMET, Milestone.SUIT_UP);
		
		// get lava
		milestones.put(Material.LAVA_BUCKET, Milestone.GET_LAVA);
		
		// get obsidian
		milestones.put(Material.OBSIDIAN, Milestone.GET_OBSIDIAN);
		
		// get diamonds
		milestones.put(Material.DIAMOND, Milestone.GET_DIAMONDS);
		
		return milestones;
	}
	
	public void trackItems(Material itemType, Manhunter player, Game game) {
		
		Milestone milestone = this.itemMilestones.get(itemType);
		
		if (milestone != null)
			awardMilestone(player, milestone, game);
	}
	
	@EventHandler
	public void onGetItem(EntityPickupItemEvent event) {
		
		if (!(event.getEntity() instanceof Player)) return;
		
		Player player = (Player) event.getEntity();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;
		
		Manhunter mhplayer = game.findPlayer(player);		
		trackItems(event.getItem().getItemStack().getType(), mhplayer, game);
	}
	
	@EventHandler
	public void onEntityRightClick(PlayerInteractEntityEvent event) {
		
		Player player = event.getPlayer();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;
		if (!(event.getRightClicked() instanceof Villager)) return;
		
		Villager villager = (Villager) event.getRightClicked();
		Manhunter mh = game.findPlayer(player);
		mh.getMilestoneProgress().setTrader(villager);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		
		if (!(event.getPlayer() instanceof Player)) return;
		
		Player player = (Player) event.getPlayer();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;
		
		Manhunter mh = game.findPlayer(player);
		
		if (mh.getMilestoneProgress().getTrader() != null) mh.getMilestoneProgress().setTrader(null);
	}
	
	@EventHandler
	public void onClickTrade(InventoryClickEvent event) {
		
		if (!(event.getWhoClicked() instanceof Player)) return;
		
		Player player = (Player) event.getWhoClicked();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;
		
		Manhunter mh = game.findPlayer(player);
		
		if (mh.getMilestoneProgress().hasMilestone(Milestone.TRAGE_WITH_VILLAGER)) return;
		if (mh.getMilestoneProgress().getTrader() == null) return;
		
		Villager villager = mh.getMilestoneProgress().getTrader();
		ItemStack item = event.getCurrentItem();
		
		if (item == null) return;
		
		for (MerchantRecipe r : villager.getRecipes()) {
			
			if (item.equals(r.getResult())) {
				awardMilestone(mh, Milestone.TRAGE_WITH_VILLAGER, game);
				return;
			}
		}
	}
	
	@EventHandler
	public void onTame(EntityTameEvent event) {
		
		if (!(event.getOwner() instanceof Player)) return;
		
		Player player = (Player) event.getOwner();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;
		
		Manhunter mh = game.findPlayer(player);
		awardMilestone(mh, Milestone.TAME_ANIMALS, game);
	}
	
	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event) {

		ItemStack item = event.getItemStack();
		if (item == null) return;
		
		if (item.getType() != Material.LAVA_BUCKET) return;
		
		Player player = event.getPlayer();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;
		
		Manhunter mh = game.findPlayer(player);
		awardMilestone(mh, Milestone.GET_LAVA, game);
	}
	
	@EventHandler
	public void onCraftItem(CraftItemEvent event) {
		
		if (!(event.getWhoClicked() instanceof Player)) return;
				
		Player player = (Player) event.getWhoClicked();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;
		
		Manhunter mhplayer = game.findPlayer(player);
		trackItems(event.getRecipe().getResult().getType(), mhplayer, game);
	}
	
	@EventHandler
	public void onClickItem(InventoryClickEvent event) {
		
		if (!(event.getWhoClicked() instanceof Player)) return;
		
		Player player = (Player) event.getWhoClicked();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;
		
		Manhunter mhplayer = game.findPlayer(player);
		ItemStack item = event.getCurrentItem();
		
		if (item == null) return;
		
		trackItems(item.getType(), mhplayer, game);
	}
	
	@EventHandler
	public void onEnchant(EnchantItemEvent event) {
		
		Player player = event.getEnchanter();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;
		
		Manhunter mhplayer = game.findPlayer(player);
		awardMilestone(mhplayer, Milestone.ENCHANTER, game);
	}
	
	@EventHandler
	public void onItemUse(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;
				
		Action action = event.getAction();
		
		if (action == Action.RIGHT_CLICK_AIR) {

			ItemStack item = event.getItem();
			if (item == null) return;
			
			Material itemType = item.getType();
			Manhunter mhplayer = game.findPlayer(player);
			
			if (itemType == Material.ENDER_EYE) awardMilestone(mhplayer, Milestone.THROW_ENDER_EYE, game);
		}
	}
	
	@EventHandler
	public void onDimEnter(PlayerPortalEvent event) {
		
		Player player = event.getPlayer();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;
		
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
			
			Manhunter mhplayer = game.findPlayer(player);
			awardMilestone(mhplayer, Milestone.ENTER_NETHER, game);
		
		} else if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
			
			Manhunter mhplayer = game.findPlayer(player);
			awardMilestone(mhplayer, Milestone.ENTER_THE_END, game);
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		
		if (event.getPlayer().getWorld().getEnvironment() != Environment.NETHER) return;
		
		Player player = event.getPlayer();
		Game game = GameManager.inGame(player);
		
		if (game == null) return;
		
		Manhunter mhplayer = game.findPlayer(player);
		if (mhplayer.getMilestoneProgress().hasMilestone(Milestone.FIND_FORTRESS)) return;
		
		World world = player.getWorld();
		Location loc = player.getLocation();
		Block block = world.getBlockAt(new Location(world, loc.getX(), loc.getY() - 1, loc.getZ()));
		
		if (block.getType() == Material.NETHER_BRICKS) {
					
			int air = 0;
			int bricks = 0;
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					for (int y = -1; y <= 1; y++) {
						
						Location location = new Location(world, loc.getX() + x, loc.getY() + y, loc.getZ() + z);
						Block b = world.getBlockAt(location);
						Material type = b.getType();
						
						if (type == Material.AIR)
							air++;
						else if (type == Material.NETHER_BRICKS)
							bricks++;
						else
							return;
					}
				}
			}

			if (1.0 * bricks / (air + bricks) >= 1.0/3.0 && isInFortress(loc))
				awardMilestone(mhplayer, Milestone.FIND_FORTRESS, game);
		}
	}
	
	private static boolean isInFortress(Location location) {

		World world = location.getWorld();
		if (world == null) return false;

		Location structureLoc = world.locateNearestStructure(location, StructureType.NETHER_FORTRESS, 100, false);
		return structureLoc != null && structureLoc.distanceSquared(location) <= Math.pow(120, 2);
	}
	
	private static void awardMilestone(Manhunter mhplayer, Milestone milestone, Game game) {
		
		if (!mhplayer.getMilestoneProgress().hasMilestone(milestone)) {
			
			mhplayer.getMilestoneProgress().awardMilestone(milestone);
			
			game.notifyTeam(ChatColor.GREEN + mhplayer.toString() + "" + milestone, mhplayer.getType());
			
			if (mhplayer.getType() == PlayerType.HUNTER)
				game.notifyTeam(ChatColor.RED + mhplayer.toString() + "" + milestone, PlayerType.RUNNER);
			else if (mhplayer.getType() == PlayerType.RUNNER)
				game.notifyTeam(ChatColor.RED + mhplayer.toString() + "" + milestone, PlayerType.HUNTER);
			
		}
	}
}
