package org.fabricmcpatcher.mixins;

import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.*;
import org.fabricmcpatcher.color.ColorizeWorld;
import org.fabricmcpatcher.sky.SkyRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow @Final private BufferBuilderStorage bufferBuilders;

    @Inject(method = "method_62215",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getSkyColor(Lnet/minecraft/util/math/Vec3d;F)I"),cancellable = true)
    void renderSkyRenderSky(Fog fog, DimensionEffects.SkyType skyType, float f, DimensionEffects dimensionEffects, CallbackInfo ci) {

        if(SkyRenderer.renderAll(this.bufferBuilders.getEntityVertexConsumers(),f))
            ci.cancel();
    }


    @Redirect(method = "render",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getCloudRenderModeValue()Lnet/minecraft/client/option/CloudRenderMode;"))
    CloudRenderMode renderGetCloudRenderModeValue(GameOptions instance) {
        return ColorizeWorld.drawFancyClouds(instance.getCloudRenderModeValue());
    }
}
