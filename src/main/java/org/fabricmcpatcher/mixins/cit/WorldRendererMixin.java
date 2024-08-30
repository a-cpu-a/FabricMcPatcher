package org.fabricmcpatcher.mixins.cit;

import net.minecraft.client.render.*;
import net.minecraft.client.util.Handle;
import net.minecraft.util.profiler.Profiler;
import org.fabricmcpatcher.cit.CITUtils;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow @Final private BufferBuilderStorage bufferBuilders;

    @Inject(method = "method_62214",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    void injectGetArmorEntityGlint(Fog fog, RenderTickCounter renderTickCounter, Camera camera, Profiler profiler, Matrix4f matrix4f, Matrix4f matrix4f2, Handle handle, Handle handle2, Handle handle3, Handle handle4, boolean bl, Frustum frustum, Handle handle5, CallbackInfo ci) {

        //VertexConsumerProvider.Immediate immediate = this.bufferBuilders.getEntityVertexConsumers();
        //immediate.draw(CITUtils.ARMOR_ENTITY_GLINT_CUSTOMIZED);
    }
}
