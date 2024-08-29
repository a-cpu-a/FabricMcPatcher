package org.fabricmcpatcher.mixins;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.biome.Biome;
import org.fabricmcpatcher.color.ColorizeWorld;
import org.fabricmcpatcher.color.Colorizer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    @Inject(method = "getFogColor",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getBottomY()I"),locals = LocalCapture.CAPTURE_FAILHARD)
    private static void getFogColorGetBottomY(Camera camera, float tickDelta, ClientWorld world, int clampedViewDistance, float skyDarkness, CallbackInfoReturnable<Vector4f> cir, CameraSubmersionType  cameraSubmersionType, Entity entity, float  red, float  green, float blue) {
        if(camera.getSubmersionType()== CameraSubmersionType.LAVA) {
            if(ColorizeWorld.computeUnderlavaColor()) {
                red=Colorizer.setColor[0];
                green=Colorizer.setColor[1];
                blue=Colorizer.setColor[2];
            }
        }
    }
    @Inject(method = "getFogColor",at= @At(value = "HEAD"))
    private static void getFogColorHead(Camera camera, float tickDelta, ClientWorld world, int clampedViewDistance, float skyDarkness, CallbackInfoReturnable<Vector4f> cir) {
        ColorizeWorld.setupForFog(camera.getFocusedEntity());
    }
    @Redirect(method = "getFogColor",at= @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getWaterFogColor()I"))
    private static int getFogColorGetWaterFogColor(Biome instance) {
        if(ColorizeWorld.computeUnderwaterColor()) {
            return Colorizer.getColorInt();
        }
        return instance.getFogColor();
    }
}
