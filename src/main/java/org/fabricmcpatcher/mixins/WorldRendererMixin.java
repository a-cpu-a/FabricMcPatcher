package org.fabricmcpatcher.mixins;

import net.minecraft.client.render.*;
import org.fabricmcpatcher.sky.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "method_62215",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/render/SkyRendering;renderSky(FFF)V",shift = At.Shift.BEFORE),cancellable = true)
    void renderSkyRenderSky(Fog fog, DimensionEffects.SkyType skyType, float f, DimensionEffects dimensionEffects, CallbackInfo ci) {
        if(SkyRenderer.renderAll(f))
            ci.cancel();
    }
}
