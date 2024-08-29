package org.fabricmcpatcher.mixins;

import net.minecraft.util.DyeColor;
import org.fabricmcpatcher.color.ColorizeWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DyeColor.class)
public class DyeColorMixin {
    @Shadow @Final private int signColor;

    @Inject(method = "getSignColor",at=@At(value = "RETURN"), cancellable = true)
    void getSignColorReturn(CallbackInfoReturnable<Integer> cir) {
        if(signColor==0)
            cir.setReturnValue(ColorizeWorld.colorizeSignText());
    }
}
