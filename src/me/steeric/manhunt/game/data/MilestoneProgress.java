package me.steeric.manhunt.game.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Villager;

public class MilestoneProgress {
	
	private final List<Milestone> milestones;
	private Villager trader;
	
	public MilestoneProgress() {
		this.milestones = new ArrayList<>();
		this.trader = null;
	}
	
	public void awardMilestone(Milestone milestone) {
		this.milestones.add(milestone);
	}
	
	public boolean hasMilestone(Milestone milestone) {
		return this.milestones.contains(milestone);
	}
	
	public Villager getTrader() {
		return trader;
	}

	public void setTrader(Villager trader) {
		this.trader = trader;
	}

	public enum Milestone {
		
		GET_STONE {
			@Override
			public String toString() {
				return " has proceeded to" + ChatColor.GOLD + " the Stone Age!";
			}
		}, 
		
		STONE_TOOLS {
			@Override
			public String toString() {
				return " now has" + ChatColor.GOLD + " stone tools!";
			}
		}, 
		
		GET_IRON {
			@Override
			public String toString() {
				return " has acquired their" + ChatColor.GOLD + " first iron!";
			}
		},
		
		SUIT_UP {
			@Override
			public String toString() {
				return " is now protected with" + ChatColor.GOLD + " armor!";
			}
		},
		
		GET_LAVA {
			@Override
			public String toString() {
				return " has their first" + ChatColor.GOLD + " bucket of lava!";
			}
		},
		
		GET_OBSIDIAN {
			@Override
			public String toString() {
				return " has obtained their" + ChatColor.GOLD + " first obsidian!";
			}
		},
		
		GET_DIAMONDS {
			@Override
			public String toString() {
				return " found" + ChatColor.GOLD + " diamonds!";
			}
		},
		
		ENTER_NETHER {
			@Override
			public String toString() {
				return " has entered" + ChatColor.GOLD + " The Nether!";
			}
		},
		
		FIND_FORTRESS {
			@Override
			public String toString() {
				return " is in a" + ChatColor.GOLD + " Nether fortress!";
			}
		},
		
		ENCHANTER {
			@Override
			public String toString() {
				return ChatColor.GOLD + " enchanted their first item!";
			}
		},
		
		THROW_ENDER_EYE {
			@Override
			public String toString() {
				return " has begun their" + ChatColor.GOLD + " search for a stronghold!";
			}
		},
		
		ENTER_THE_END {
			@Override
			public String toString() {
				return " is now fighting against" + ChatColor.GOLD + " the Ender Dragon!";
			}
		},
		
		TRAGE_WITH_VILLAGER {
			@Override
			public String toString() {
				return " struck" + ChatColor.GOLD + " a deal with a villager!";
			}
		},
		
		TAME_ANIMALS {
			@Override
			public String toString() {
				return " has now" + ChatColor.GOLD + " a pet!";
			}
		}
	}

}
