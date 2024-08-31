package org.fabricmcpatcher.mixins.cit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import org.fabricmcpatcher.cit.CitMixinUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BedBlockEntityRenderer.class)
public class BedBlockEntityRendererMixin {//broken, low prio, cuz vanilla dont work either (enchant glint rendered before the item (???))
/*
    @WrapOperation(method = "renderPart",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/model/Model;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"))
    void renderPartRender(Model instance, MatrixStack matrices, VertexConsumer vertices, int light, int overlay,
                          Operation<Void> original, MatrixStack matricesUpper,
                          VertexConsumerProvider vertexConsumers,
                          Model model,
                          Direction direction,
                          SpriteIdentifier sprite) {
        if(CitMixinUtils.renderingBlockEntity)
            CitMixinUtils.renderGlintLayers(instance,instance.getPart(),matrices,vertices,light,overlay,original,vertexConsumers,sprite.getRenderLayer(RenderLayer::getEntitySolid));
        //else
        //    original.call(instance,matrices,vertices,light,overlay);
    }*/
}
