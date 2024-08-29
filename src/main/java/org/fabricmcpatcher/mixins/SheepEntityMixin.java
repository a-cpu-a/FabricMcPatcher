package org.fabricmcpatcher.mixins;

import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import org.fabricmcpatcher.color.ColorizeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SheepEntity.class)
public class SheepEntityMixin {
    @Inject(method = "getRgbColor",at=@At(value = "HEAD"),cancellable = true)
    private static void getRgbColorhead(DyeColor dyeColor, CallbackInfoReturnable<Integer> cir) {
        Integer col = ColorizeEntity.getFleeceColor(dyeColor.getId());
        if(col==null) return;

        cir.setReturnValue(col);
        cir.cancel();
    }
}
