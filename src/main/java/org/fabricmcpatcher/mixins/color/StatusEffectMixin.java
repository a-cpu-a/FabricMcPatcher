package org.fabricmcpatcher.mixins.color;

import net.minecraft.entity.effect.StatusEffect;
import org.fabricmcpatcher.accessors.IOverrideColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffect.class)
public class StatusEffectMixin implements IOverrideColor {

    @Unique
    Integer mcPatcher$overrideCol = null;

    @Override
    @Unique
    public void mcPatcher$overrideColor(Integer col) {
        mcPatcher$overrideCol=col;
    }

    @Inject(method = "getColor",at=@At(value = "HEAD"),cancellable = true)
    void getColorHead(CallbackInfoReturnable<Integer> cir) {
        if(mcPatcher$overrideCol!=null)
            cir.setReturnValue(mcPatcher$overrideCol);
    }
}
