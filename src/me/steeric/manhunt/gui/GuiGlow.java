package me.steeric.manhunt.gui;

import me.steeric.manhunt.Manhunt;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class GuiGlow extends Enchantment {

    public static final GuiGlow GUI_GLOW = new GuiGlow(new NamespacedKey(Manhunt.manhuntPlugin, "gui_glow"));

    private GuiGlow(NamespacedKey key) {
        super(key);
    }

    @Override
    public String getName() {
        return "gui_glow";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return null;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return itemStack.getType() == Material.COMPASS || itemStack.getType() == Material.ENDER_EYE || itemStack.getType() == Material.FEATHER;
    }
}
