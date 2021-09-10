package org.moon.enchantmenttweaker.lib;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import org.moon.enchantmenttweaker.EnchantmentModifications;

public interface ArmorEnchantmentModification extends EnchantmentModification {

    /**
     * Returns how much damage should be reduced due to this enchantment, on the given ItemStack.
     * @param stack
     * @param level
     * @return reductionAmount
     */
    default int getDamageReduction(ItemStack stack, DamageSource source, int level) { return 0; }

    static int getProtectionAmount(Enchantment enchantment, ItemStack stack, DamageSource source, int level) {
        int amount = 0;
        for (EnchantmentModification modification : EnchantmentModifications.get(enchantment)) {
            if (modification instanceof ArmorEnchantmentModification armorModification) {
                amount += armorModification.getDamageReduction(stack, source, level);
            }
        }
        return amount;
    }
}
