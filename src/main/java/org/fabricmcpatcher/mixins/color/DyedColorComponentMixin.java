package org.fabricmcpatcher.mixins.color;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import org.fabricmcpatcher.color.ColorizeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DyedColorComponent.class)
public class DyedColorComponentMixin {

    @WrapOperation(method = "setColor",at= @At(value = "INVOKE", target = "Lnet/minecraft/util/DyeColor;getEntityColor()I"))
    private static int setColorGetEntityColor(DyeColor instance, Operation<Integer> original) {
        Integer newCol = ColorizeEntity.getArmorDyeColor(instance.getId());

        if(newCol==null)
            return original.call(instance);

        return newCol;
    }

    @Inject(method = "getColor",at=@At(value = "RETURN"), cancellable = true)
    private static void getColorReturn(ItemStack stack, int defaultColor, CallbackInfoReturnable<Integer> cir) {
        if(cir.getReturnValue()==defaultColor && defaultColor==0xFFa06540) {//is default leather color
            cir.setReturnValue(ColorizeEntity.undyedLeatherColor|0xFF000000);//replace it, and add correct alpha
        }
    }

}
