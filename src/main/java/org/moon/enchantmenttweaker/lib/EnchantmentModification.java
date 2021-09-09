package org.moon.enchantmenttweaker.lib;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.moon.enchantmenttweaker.EnchantmentModifications;

import java.util.Optional;

public interface EnchantmentModification {

    /**
     * Overrides the minimum level for this enchantment, returning -1 uses its original value.
     * @param stack
     * @return minLevel
     */
    default int getMinLevel(ItemStack stack) { return -1; }

    /**
     * Overrides the maximum level for this enchantment, returning -1 uses its original value.
     * @param stack
     * @return maxLevel
     */
    default int getMaxLevel(ItemStack stack) { return -1; }

    /**
     * Adds a new condition to test if this enchantment is applicable to a ItemStack or not.
     * @param stack
     * @param level The level of the item being enchanted. Optional, since this check is run once before enchanting (without the level yet), then again after the level has been calculated.
     * @return isApplicable
     */
    default boolean isApplicableTo(ItemStack stack, Optional<Integer> level) { return false; }

    /**
     * Adds a condition to test whether an item is NOT applicable to a ItemStack
     * @param stack
       @param level The level of the item being enchanted. Optional, since this check is run once before enchanting (without the level yet), then again after the level has been calculated.
     * @return isNotApplicable
     */
    default boolean isNotApplicableTo(ItemStack stack, Optional<Integer> level) { return false; }


    static boolean isApplicableTo(Enchantment enchantment, ItemStack stack, int level) {
        return isApplicableTo(enchantment, stack, Optional.of(level));
    }
    static boolean isApplicableTo(Enchantment enchantment, ItemStack stack) {
        return isApplicableTo(enchantment, stack, Optional.empty());
    }
    private static boolean isApplicableTo(Enchantment enchantment, ItemStack stack, Optional<Integer> level) {
        for (EnchantmentModification modification : EnchantmentModifications.get(enchantment)) {
            if (modification.isApplicableTo(stack, level)) {
                return true;
            }
        }
        return false;
    }

    static boolean isNotApplicableTo(Enchantment enchantment, ItemStack stack, int level) {
        return isNotApplicableTo(enchantment, stack, Optional.of(level));
    }
    static boolean isNotApplicableTo(Enchantment enchantment, ItemStack stack) {
        return isNotApplicableTo(enchantment, stack, Optional.empty());
    }
    private static boolean isNotApplicableTo(Enchantment enchantment, ItemStack stack, Optional<Integer> level) {
        for (EnchantmentModification modification : EnchantmentModifications.get(enchantment)) {
            if (modification.isNotApplicableTo(stack, level)) {
                return true;
            }
        }
        return false;
    }

    static int getMinLevel(Enchantment enchantment, ItemStack stack) {
        for (EnchantmentModification modification : EnchantmentModifications.get(enchantment)) {
            int cur = modification.getMinLevel(stack);
            if (cur != -1) return cur;
        }
        return enchantment.getMinLevel();
    }
    static int getMaxLevel(Enchantment enchantment, ItemStack stack) {
        for (EnchantmentModification modification : EnchantmentModifications.get(enchantment)) {
            int cur = modification.getMaxLevel(stack);
            if (cur != -1) return cur;
        }
        return enchantment.getMinLevel();
    }
}
