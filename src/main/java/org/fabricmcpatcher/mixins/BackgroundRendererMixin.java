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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    @Unique
    private static boolean drewFogCol = false;

    @ModifyVariable(method = "getFogColor",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getBottomY()I"),ordinal = 2)
    private static float getFogColorGetBottomYRed(float value,Camera camera) {
        if(camera.getSubmersionType()== CameraSubmersionType.LAVA) {
            if(ColorizeWorld.computeUnderlavaColor()) {
                drewFogCol=true;
                return Colorizer.setColor[0];
                //green=Colorizer.setColor[1];
                //blue=Colorizer.setColor[2];
            }
        }
        drewFogCol=false;
        return value;
    }
    @ModifyVariable(method = "getFogColor",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getBottomY()I"),ordinal = 3)
    private static float getFogColorGetBottomYGreen(float value) {
        if(drewFogCol) {
            return Colorizer.setColor[1];
        }
        return value;
    }
    @ModifyVariable(method = "getFogColor",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getBottomY()I"),ordinal = 4)
    private static float getFogColorGetBottomYBlue(float value) {
        if(drewFogCol) {
            return Colorizer.setColor[2];
        }
        return value;
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
