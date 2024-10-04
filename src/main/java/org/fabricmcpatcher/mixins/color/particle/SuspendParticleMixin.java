package org.fabricmcpatcher.mixins.color.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SuspendParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import org.fabricmcpatcher.color.ColorizeBlock;
import org.fabricmcpatcher.color.ColorizeEntity;
import org.fabricmcpatcher.color.Colorizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SuspendParticle.class)
public class SuspendParticleMixin {

    @Mixin(SuspendParticle.MyceliumFactory.class)
    static class MyceliumFactoryMixin {

        @Inject(method = "createParticle(Lnet/minecraft/particle/SimpleParticleType;Lnet/minecraft/client/world/ClientWorld;DDDDDD)Lnet/minecraft/client/particle/Particle;",at=@At(value = "RETURN"))
        void createParticleReturn(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, CallbackInfoReturnable<Particle> cir) {

            if(ColorizeEntity.computeMyceliumParticleColor())
                cir.getReturnValue().setColor(Colorizer.setColor[0],Colorizer.setColor[1],Colorizer.setColor[2]);
        }
    }
}
