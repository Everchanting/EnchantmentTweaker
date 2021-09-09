package org.moon.enchantmenttweaker;

import com.google.common.collect.HashMultimap;
import net.minecraft.enchantment.Enchantment;
import org.moon.enchantmenttweaker.lib.EnchantmentModification;

import java.util.Set;

public class EnchantmentModifications {
    private static final HashMultimap<Enchantment, EnchantmentModification> enchantmentModifications = HashMultimap.create();

    public static void add(Enchantment enchantment, EnchantmentModification modification) {
        enchantmentModifications.put(enchantment, modification);
    }

    public static Set<EnchantmentModification> get(Enchantment enchantment) {
        return enchantmentModifications.containsKey(enchantment) ? enchantmentModifications.get(enchantment) : Set.of();
    }
}
