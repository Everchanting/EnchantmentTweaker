package org.moon.enchantmenttweaker.impl.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.moon.enchantmenttweaker.lib.EnchantmentModification;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    public void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment self = (Enchantment) (Object) this;

        if (EnchantmentModification.isNotApplicableTo(self, stack)) {
            cir.setReturnValue(false);
            cir.cancel();
        } else if (EnchantmentModification.isApplicableTo(self, stack)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}