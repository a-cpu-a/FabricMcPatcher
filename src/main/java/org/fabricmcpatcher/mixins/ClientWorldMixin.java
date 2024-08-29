package org.fabricmcpatcher.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.math.Vec3d;
import org.fabricmcpatcher.color.ColorizeWorld;
import org.fabricmcpatcher.color.Colorizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @WrapOperation(method = "getSkyColor",at= @At(value = "INVOKE", target = "Lnet/minecraft/util/CubicSampler;sampleColor(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/CubicSampler$RgbFetcher;)Lnet/minecraft/util/math/Vec3d;"))
    Vec3d getSkyColorSampleColor(Vec3d pos, CubicSampler.RgbFetcher rgbFetcher, Operation<Vec3d> original,Vec3d cameraPos, float tickDelta) {
        if(ColorizeWorld.computeSkyColor((ClientWorld)(Object) this,tickDelta))
            return new Vec3d(Colorizer.setColor[0],Colorizer.setColor[1],Colorizer.setColor[2]);
        return original.call(pos,rgbFetcher);
    }
}
