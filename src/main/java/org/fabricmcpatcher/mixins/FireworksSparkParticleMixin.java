package org.fabricmcpatcher.mixins;

import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import org.fabricmcpatcher.sky.FireworksHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworksSparkParticle.class)
public class FireworksSparkParticleMixin {

    @Mixin(targets = "net.minecraft.client.particle.FireworksSparkParticle$Explosion")
    static class Explosion extends Particle {

        protected Explosion(ClientWorld world, double x, double y, double z) {
            super(world, x, y, z);
        }

        @Shadow
        public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        }


        @Override
        public ParticleTextureSheet getType() {
            return FireworksHelper.getUsedParticleSheet();
        }

    }
    @Mixin(FireworksSparkParticle.Flash.class)
    static class Flash {
        @Inject(method = "getType",at=@At(value = "HEAD"),cancellable = true)
        public void getTypeHead(CallbackInfoReturnable<ParticleTextureSheet> cir) {
            ParticleTextureSheet sheet = FireworksHelper.getUsedParticleSheet();
            if(sheet==null)return;
            cir.setReturnValue(sheet);
            cir.cancel();
        }

    }
}
