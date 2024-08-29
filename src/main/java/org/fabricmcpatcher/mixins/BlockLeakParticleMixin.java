package org.fabricmcpatcher.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.particle.BlockLeakParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleEffect;
import org.fabricmcpatcher.color.ColorizeEntity;
import org.fabricmcpatcher.color.Colorizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockLeakParticle.class)
public class BlockLeakParticleMixin {


    @WrapOperation(method = {"createFallingLava","createLandingLava"},at= @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/BlockLeakParticle;setColor(FFF)V"))
    private static void initReturn(BlockLeakParticle instance, float r, float g, float b, Operation<Void> original) {
        if(ColorizeEntity.computeLavaDropColor(40))
        {
            r= Colorizer.setColor[0];
            g= Colorizer.setColor[1];
            b= Colorizer.setColor[2];
        }
        original.call(instance,r,g,b);
    }

    @Mixin(targets = "net.minecraft.client.particle.BlockLeakParticle$DrippingLava")
    static abstract class DrippingLava extends Particle {

        protected DrippingLava(ClientWorld world, double x, double y, double z) {
            super(world, x, y, z);
        }

        @Inject(method = "<init>",at=@At(value = "RETURN"))
        void initReturn(ClientWorld clientWorld, double d, double e, double f, Fluid fluid, ParticleEffect particleEffect, CallbackInfo ci) {
            if(ColorizeEntity.computeLavaDropColor(40 - this.maxAge))
            {
                this.red= Colorizer.setColor[0];
                this.green= Colorizer.setColor[1];
                this.blue= Colorizer.setColor[2];
            }
        }

        @Inject(method = "updateAge",at= @At(value = "FIELD", target = "Lnet/minecraft/client/particle/BlockLeakParticle$DrippingLava;blue:F",shift = At.Shift.AFTER))
        void updateAgeAfterBlue(CallbackInfo ci) {
            if(ColorizeEntity.computeLavaDropColor(40 - this.maxAge))
            {
                this.red= Colorizer.setColor[0];
                this.green= Colorizer.setColor[1];
                this.blue= Colorizer.setColor[2];
            }
        }
    }
}
