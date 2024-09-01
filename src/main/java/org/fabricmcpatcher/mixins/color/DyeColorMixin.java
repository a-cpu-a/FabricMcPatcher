package org.fabricmcpatcher.mixins.color;

import net.minecraft.util.DyeColor;
import org.fabricmcpatcher.color.ColorizeEntity;
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

    @Shadow @Final private int id;

    @Inject(method = "getSignColor",at=@At(value = "RETURN"), cancellable = true)
    void getSignColorReturn(CallbackInfoReturnable<Integer> cir) {
        if(signColor==0)
            cir.setReturnValue(ColorizeWorld.colorizeSignText());
    }
    @Inject(method = "getEntityColor",at=@At(value = "RETURN"), cancellable = true)
    void getEntityColorReturn(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ColorizeEntity.getDyeColor(cir.getReturnValue(),id));
    }
}
