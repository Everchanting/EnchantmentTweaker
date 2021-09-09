package org.moon.enchantmenttweaker.impl.mixin;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableInt;
import org.moon.enchantmenttweaker.lib.ArmorEnchantmentModification;
import org.moon.enchantmenttweaker.lib.EnchantmentModification;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Users beware: This mod isn't just a simple mixin, oh no.
// Minecraft's enchantment system is so BUSTED, so basically I had to make it really hacky to work.
// *Hopefully* shouldn't cause many mod compatibility issues... just use this library :) /s
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Unique private static int enchantmentPower;
    @Unique private static ArrayList possibleEntries;
    @Unique private static Enchantment enchantment;
    @Unique private static ItemStack itemStack;

    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;"))
    private static <E> ArrayList<E> stealLocalVariable() {
        possibleEntries = Lists.newArrayList();
        return possibleEntries;
    }

    // Steals the enchantment iterators return value
    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private static <E>E stealLocalVariable(Iterator<Enchantment> iterator) {
        Enchantment next = iterator.next();
        enchantment = next;
        return (E) next;
    }

    // Steals the ItemStack and power arguments
    @Inject(method = "getPossibleEntries", at = @At(value = "HEAD"))
    private static void getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        itemStack = stack;
        enchantmentPower = power;
    }

    // "Hacks" into isAcceptableItem, and makes it run *per enchantment* instead of *per enchantment TYPE*. Also injects my condition into it~
    // @TODO Turn this into it's own api for other mods
    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"))
    private static boolean isAcceptableItemFix(EnchantmentTarget enchantmentTarget, Item item) {


        if (EnchantmentModification.isNotApplicableTo(enchantment, itemStack)) {
            return false;
        }
        if (EnchantmentModification.isApplicableTo(enchantment, itemStack)) {
            int maxLevel = EnchantmentModification.getMaxLevel(enchantment, itemStack);
            int minLevel = EnchantmentModification.getMinLevel(enchantment, itemStack);
            for(int i = maxLevel; i > minLevel - 1; --i) {
                if (enchantmentPower >= enchantment.getMinPower(i) && enchantmentPower <= enchantment.getMaxPower(i)) {
                    if (EnchantmentModification.isNotApplicableTo(enchantment, itemStack, i)) {
                        return false;
                    }
                    if (EnchantmentModification.isApplicableTo(enchantment, itemStack, i)) {
                        possibleEntries.add(new EnchantmentLevelEntry(enchantment, i));
                        return true;
                    }
                }
            }
        }

        return enchantment.isAcceptableItem(itemStack);
    }


    @Unique private static Iterable<ItemStack> allEquipment;
    @Unique private static DamageSource damageSource;

    @Inject(method = "getProtectionAmount", at = @At("HEAD"))
    private static void getProtectionAmount(Iterable<ItemStack> equipment, DamageSource source, CallbackInfoReturnable<Integer> cir) {
        allEquipment = equipment;
        damageSource = source;
    }

    @Redirect(method = "getProtectionAmount",
            at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/mutable/MutableInt;intValue()I"))
    private static int redirectProtectionAmountValue(MutableInt mutableInt) {

        allEquipment.forEach((equipmentItem) -> {
            EnchantmentHelper.get(equipmentItem).forEach((enchantment, level) -> {
                mutableInt.add(ArmorEnchantmentModification.getProtectionAmount(enchantment, equipmentItem, damageSource, level));
            });
        });

        return mutableInt.intValue();
    }
}
