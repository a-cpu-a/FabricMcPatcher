package org.fabricmcpatcher.mixins.color.particle;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.particle.WaterSuspendParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import org.fabricmcpatcher.color.ColorizeEntity;
import org.fabricmcpatcher.color.Colorizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WaterSuspendParticle.class)
public class WaterSuspendParticleMixin {
    @Mixin(WaterSuspendParticle.UnderwaterFactory.class)
    static class UnderwaterFactory {

        @WrapOperation(at= @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/WaterSuspendParticle;setColor(FFF)V"),method = "createParticle(Lnet/minecraft/particle/SimpleParticleType;Lnet/minecraft/client/world/ClientWorld;DDDDDD)Lnet/minecraft/client/particle/Particle;")
        void createParticleSetColor(WaterSuspendParticle instance, float r, float g, float b, Operation<Void> original, SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f) {

            if(ColorizeEntity.computeSuspendColor((int) d, (int) e, (int) f)) {
                original.call(instance, Colorizer.setColor[0], Colorizer.setColor[1], Colorizer.setColor[2]);
            }

            original.call(instance,r,g,b);
        }
    }
}
