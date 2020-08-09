package me.steeric.manhunt.gui;

import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.players.*;
import me.steeric.manhunt.utils.ItemUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class TeamSelectionGui implements Listener {

    private final Inventory inventory;
    private final Game game;

    private final ItemStack hunterItem;
    private final ItemStack runnerItem;
    private final ItemStack spectatorItem;

    public TeamSelectionGui(Game game) {

        this.inventory = Bukkit.createInventory(null, 27, "Join game as a...");
        this.game = game;

        this.hunterItem = ItemUtils.createItem("...hunter", Material.COMPASS, 1, Collections.singletonList("Hunters currently joined: 0"), ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        this.runnerItem = ItemUtils.createItem("...runner", Material.ENDER_EYE, 1, Collections.singletonList("Runners currently joined: 0"), ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        this.spectatorItem = ItemUtils.createItem("...spectator", Material.FEATHER, 1, Collections.singletonList("Spectators currently joined: 0"), ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
    }

    private void setItems() {

        int loc;
        for (int i = 0; i < 27; i++) {
            loc = ((int) (i / 3.0)) % 3;
            if (loc == 0) {
                this.inventory.setItem(i, ItemUtils.createItem("", Material.GRAY_STAINED_GLASS_PANE, 1, Collections.emptyList()));
            } else if (loc == 1) {
                this.inventory.setItem(i, ItemUtils.createItem("", Material.WHITE_STAINED_GLASS_PANE, 1, Collections.emptyList()));
            } else if (loc == 2) {
                this.inventory.setItem(i, ItemUtils.createItem("", Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1, Collections.emptyList()));
            }
        }

        // set special items
        this.inventory.setItem(10, this.hunterItem);
        this.inventory.setItem(13, this.spectatorItem);
        this.inventory.setItem(16, this.runnerItem);
    }

    @EventHandler
    public void onGuiClick(InventoryClickEvent event) {

        if (event.getClickedInventory() == this.inventory) {

            // prevent the player from taking items out of the inventory
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !(event.getWhoClicked() instanceof Player))
                return;

            Player player = (Player) event.getWhoClicked();
            Material clickedMaterial = clickedItem.getType();
            if (clickedMaterial == Material.COMPASS)
                this.addHunter(player);
            else if (clickedMaterial == Material.ENDER_EYE)
                this.addRunner(player);
            else if (clickedMaterial == Material.FEATHER)
                this.addSpectator(player);
        }
    }

    private void addPlayer(AbstractPlayer player) {

        this.game.addPlayer(player);
        Player playerHandle = player.getPlayerHandle();
        if (playerHandle == null)
            return;

        // send join message to player
        playerHandle.sendMessage(ChatColor.AQUA + "Successfully joined game!\nYou are now a " + player.getClass().getSimpleName().toLowerCase());

        // send join message to game admin
        Player admin = Bukkit.getPlayer(this.game.getAdmin());
        if (admin != null && !admin.equals(playerHandle))
            admin.sendMessage(ChatColor.WHITE + playerHandle.getName() + ChatColor.AQUA + " [" + player.getClass().getSimpleName().substring(0, 1).toUpperCase() + "] joined your game!");
    }

    private void addRunner(Player playerHandle) {

        // if first runner
        if (this.game.getRunners().size() == 0) {
            ItemMeta meta = this.runnerItem.getItemMeta();
            if (meta != null) {
                meta.addEnchant(GuiGlow.GUI_GLOW, 1, false);
                this.runnerItem.setItemMeta(meta);
            }
        } else // otherwise, increase item stack amount
            this.runnerItem.setAmount(this.runnerItem.getAmount() + 1);

        // update lore
        ItemMeta meta = this.runnerItem.getItemMeta();
        if (meta != null) {
            meta.setLore(Collections.singletonList("Runners currently joined: " + (this.game.getRunners().size() + 1)));
            this.runnerItem.setItemMeta(meta);
        }

        // the item must be added to the inventory every time it is updated
        // because Inventory#setItem takes a copy of the item passed as a parameter
        this.inventory.setItem(16, this.runnerItem);

        // actually add the player to the game
        // and send join messages
        this.addPlayer(new Runner(playerHandle, this.game));
    }

    private void addHunter(Player playerHandle) {

        // if first hunter
        if (this.game.getHunters().size() == 0) {
            ItemMeta meta = this.hunterItem.getItemMeta();
            if (meta != null) {
                meta.addEnchant(GuiGlow.GUI_GLOW, 1, false);
                this.hunterItem.setItemMeta(meta);
            }
        } else { // otherwise, increase item stack amount
            this.hunterItem.setAmount(this.hunterItem.getAmount() + 1);
        }

        // update lore
        ItemMeta meta = this.hunterItem.getItemMeta();
        if (meta != null) {
            meta.setLore(Collections.singletonList("Hunters currently joined: " + (this.game.getHunters().size() + 1)));
            this.hunterItem.setItemMeta(meta);
        }

        // the item must be added to the inventory every time it is updated
        // because Inventory#setItem takes a copy of the item passed as a parameter
        this.inventory.setItem(10, this.hunterItem);

        this.addPlayer(new Hunter(playerHandle, this.game));
    }

    private void addSpectator(Player playerHandle) {

        if (this.game.getSpectators().size() == 0) {
            ItemMeta meta = this.spectatorItem.getItemMeta();
            if (meta != null) {
                meta.addEnchant(GuiGlow.GUI_GLOW, 1, false);
                this.spectatorItem.setItemMeta(meta);
            }
        } else {
            this.spectatorItem.setAmount(this.spectatorItem.getAmount() + 1);
        }

        ItemMeta meta = this.spectatorItem.getItemMeta();
        if (meta != null) {
            meta.setLore(Collections.singletonList("Spectators currently joined: " + (this.game.getSpectators().size() + 1)));
        }

        this.inventory.setItem(13, this.spectatorItem);

        this.addPlayer(new Spectator(playerHandle, this.game));
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}
