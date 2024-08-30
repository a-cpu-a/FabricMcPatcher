package org.fabricmcpatcher.mixins.cit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import org.fabricmcpatcher.cit.CitMixinUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BannerBlockEntityRenderer.class)
public class BannerBlockEntityRendererMixin {
    @WrapOperation(at= @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"), method = "renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLnet/minecraft/util/DyeColor;Lnet/minecraft/component/type/BannerPatternsComponent;ZZ)V")
    private static void renderCanvasRender(ModelPart instance, MatrixStack matrices, VertexConsumer vertices, int light, int overlay,
                                           Operation<Void> original, MatrixStack matricesUpper,
                                           VertexConsumerProvider vertexConsumers,
                                           int lightUpper,
                                           int overlayUpper,
                                           ModelPart canvas,
                                           SpriteIdentifier baseSprite) {

        //original.call(instance,matrices,vertices,light,overlay);

        CitMixinUtils.renderGlintLayers(instance, instance, matrices, vertices, light, overlay,
                original, vertexConsumers, baseSprite.getRenderLayer(RenderLayer::getEntitySolid));

    }
}
