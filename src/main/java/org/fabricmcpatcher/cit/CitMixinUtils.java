package org.fabricmcpatcher.cit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;

public class CitMixinUtils {
    public CitMixinUtils() {
    }

    public static void renderGlintLayers(Object realInst, ModelPart instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, Operation<Void> original, VertexConsumerProvider vertexConsumers, RenderLayer baseLayer) {
        original.call(realInst, matrixStack, vertexConsumer, light, overlay);

        if (!CITUtils.isArmorEnchantmentActive())
            return;


        while (CITUtils.preRenderArmorEnchantment()) {

            //every layer can have a different consumer
            VertexConsumer vertexConsumer2 = CITUtils.getVertexConsumer(vertexConsumers, baseLayer, false,-1);
            //draw something
            instance.render(matrixStack, vertexConsumer2, light, overlay, ColorHelper.fromFloats(
                    CITUtils.boundFade.x, CITUtils.boundFade.y,
                    CITUtils.boundFade.z, CITUtils.boundFade.w
            ));

            CITUtils.postRenderArmorEnchantment();
        }
    }
}