package me.steeric.manhunt.game.managing;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import me.steeric.manhunt.Manhunt;
import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.data.WorldSet;

public class WorldManager {
	
	public static boolean worldsExist(String baseName) {
		
		World overworld = Bukkit.getWorld(baseName);
		if (overworld == null) return false;
		
		World nether = Bukkit.getWorld(baseName + "_nether");
		if (nether == null) return false;
		
		World end = Bukkit.getWorld(baseName + "_the_end");
		return end != null;
	}
	
	private static void deleteFolder(File folder) {
		
		if (folder.isFile()) return;
		
		// delete files
		File[] files = folder.listFiles();
		if (files == null) return;

		for (File file : files) {
			if (file.isDirectory()) {
				deleteFolder(file);
			}
		}
	}
	
	public static vec2d getEndLocation() {
		
		double angle = new Random().nextDouble() * 2 * Math.PI;
		
		return new vec2d(Math.cos(angle) * 100, Math.sin(angle) * 100);
		
	}
	
	public static class vec2d {
		public double x;
		public double z;
		
		public vec2d(double x, double z) {
			this.x = x;
			this.z = z;
		}
	}
	
	public static void deleteWorld(String name) {
		
		File folder = Bukkit.getServer().getWorldContainer();
		if (folder.isFile()) return;
		
		int index = -1;
		File[] files = folder.listFiles();
		if (files == null) return;
		
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().equals(name) && files[i].isDirectory()) {
				index = i;
				break;
			}
		}
		
		if (index >= 0) {
			deleteFolder(files[index]);
		}
	}
	
	public static World[] getWorlds(String baseName) {
		
		World[] worlds = new World[3];
		
		worlds[0] = Bukkit.getWorld(baseName);
		worlds[1] = Bukkit.getWorld(baseName + "_nether");
		worlds[2] = Bukkit.getWorld(baseName + "_the_end");
		
		return worlds;
		
	}
		
	public static boolean applicableWorld(World world) {
		
		String namePatternMode = Manhunt.config.getString("name-pattern-mode");
		String namePattern = Manhunt.config.getString("name-pattern");
		if (namePattern == null || namePatternMode == null) return false;

		String worldName = world.getName();
		
		if (namePatternMode.equals("regex")) {
			return worldName.matches(namePattern);
		} else if (namePatternMode.equals("prefix")) {
			return worldName.indexOf(namePattern) == 0;
		}
		
		return false;
	}

	public static String findWorld() {
	
		List<World> worlds = Bukkit.getWorlds();
		worlds = worlds.stream()
			.filter(world -> world.getEnvironment().equals(Environment.NORMAL) && applicableWorld(world))
			.collect(Collectors.toList());
		
		String baseName = null;
		
		outer: for (World world : worlds) {

			for (int j = 0; j < GameManager.games.size(); j++) {
				if (GameManager.games.get(j).getWorlds().toString().equals(world.getName())) continue outer;
			}

			if (Bukkit.getWorld(world.getName() + "_nether") != null && Bukkit.getWorld(world.getName() + "_the_end") != null) {
				baseName = world.getName();
			}

			break;
		}
		
		return baseName;
	}

	public static World createWorld(String name, Environment environment) {
		
		WorldCreator wc = new WorldCreator(name);
		
		wc.environment(environment);
		wc.type(WorldType.NORMAL);
		
		return Bukkit.getServer().createWorld(wc);
	}

	public static World[] createGameWorlds(String baseName) {

		String nether = baseName + "_nether";
		String end = baseName + "_the_end";
		
		World[] worlds = new World[3];
		
		if (Bukkit.getWorld(baseName) == null)
			worlds[0] = createWorld(baseName, Environment.NORMAL);
		else 
			worlds[0] = Bukkit.getWorld(baseName);
		
		
		if (Bukkit.getWorld(nether) == null) 
			worlds[1] = createWorld(nether, Environment.NETHER);
		else
			worlds[1] = Bukkit.getWorld(nether);
		
		if (Bukkit.getWorld(end) == null) 
			worlds[2] = createWorld(end, Environment.THE_END);
		else
			worlds[2] = Bukkit.getWorld(end);
		
		return worlds;
		
	}

	public static void deleteGameWorlds(Game game) {
		
		WorldSet worldSet = game.getWorlds();
		World[] worlds = worldSet.worlds;

		for (World world : worlds) {

			for (Player p : world.getPlayers()) {

				Location loc = p.getBedSpawnLocation();

				if (loc != null) {
					p.teleport(loc);
				} else {
					p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
				}
			}

			Bukkit.getServer().unloadWorld(world, false);
			deleteWorld(world.getName());
		}
	}
}
