package org.fabricmcpatcher.mixins.color.particle;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.ExperienceOrbEntityRenderer;
import net.minecraft.client.render.entity.state.ExperienceOrbEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.fabricmcpatcher.color.ColorizeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ExperienceOrbEntityRenderer.class)
public class ExperienceOrbEntityRendererMixin {

    @WrapOperation(method = "render(Lnet/minecraft/client/render/entity/state/ExperienceOrbEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/ExperienceOrbEntityRenderer;vertex(Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/util/math/MatrixStack$Entry;FFIIIFFI)V"))
    private void renderVertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, float x, float y, int red, int green, int blue, float u, float v, int light, Operation<Void> original, ExperienceOrbEntityRenderState experienceOrbEntityRenderState) {

        ColorizeEntity.colorizeXPOrb(red,blue,experienceOrbEntityRenderState.age);
        original.call(vertexConsumer,matrix,x,y,ColorizeEntity.xpOrbRed,ColorizeEntity.xpOrbGreen,ColorizeEntity.xpOrbBlue,u,v,light);
    }
}
