package org.fabricmcpatcher.mixins.color;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.particle.PortalParticle;
import org.fabricmcpatcher.color.ColorizeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PortalParticle.class)
public class PortalParticleMixin {
    @WrapOperation(method = "<init>",at= @At(value = "FIELD", target = "Lnet/minecraft/client/particle/PortalParticle;red:F"))
    void initSetRed(PortalParticle instance, float value, Operation<Void> original) {
        if(ColorizeEntity.portalColor!=null) {
            original.call(instance,value/0.9f*ColorizeEntity.portalColor[0]);
            return;
        }
        original.call(instance,value);
    }
    @WrapOperation(method = "<init>",at= @At(value = "FIELD", target = "Lnet/minecraft/client/particle/PortalParticle;green:F"))
    void initSetGreen(PortalParticle instance, float value, Operation<Void> original) {
        if(ColorizeEntity.portalColor!=null) {
            original.call(instance,value/0.3f*ColorizeEntity.portalColor[1]);
            return;
        }
        original.call(instance,value);
    }
    @WrapOperation(method = "<init>",at= @At(value = "FIELD", target = "Lnet/minecraft/client/particle/PortalParticle;blue:F"))
    void initSetBlue(PortalParticle instance, float value, Operation<Void> original) {
        if(ColorizeEntity.portalColor!=null) {
            original.call(instance,value*ColorizeEntity.portalColor[2]);
            return;
        }
        original.call(instance,value);
    }
}
