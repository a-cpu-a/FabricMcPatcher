package org.fabricmcpatcher.mixins.cit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import org.fabricmcpatcher.accessors.IStackHolder;
import org.fabricmcpatcher.cit.CITUtils;
import org.fabricmcpatcher.client.FabricMcPatcherClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements IStackHolder {




    @WrapOperation(method = "renderItem(Lnet/minecraft/item/ModelTransformationMode;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II[ILnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/item/ItemRenderState$Glint;)V",
            at= @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;getItemGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;ZZ)Lnet/minecraft/client/render/VertexConsumer;"))
    private static VertexConsumer renderItemQuadsHasGlint(VertexConsumerProvider vertexConsumers, RenderLayer layer, boolean solid, boolean glint, Operation<VertexConsumer> original) {

        if(CITUtils.setupArmorEnchantments(FabricMcPatcherClient.itemRendererCurrentStack)) {
            glint=false;
        }
        return original.call(vertexConsumers,layer,solid,glint);
    }
    @WrapOperation(method = "renderItem(Lnet/minecraft/item/ModelTransformationMode;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II[ILnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/item/ItemRenderState$Glint;)V",
            at= @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;renderBakedItemModel(Lnet/minecraft/client/render/model/BakedModel;[IIILnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;)V"
            ))
    private static void renderItemRenderBakedItemModel(BakedModel model, int[] tints, int light, int overlay, MatrixStack matrices, VertexConsumer vertexConsumer, Operation<Void> original, @Local(argsOnly = true)VertexConsumerProvider vertexConsumers) {



        original.call(model,tints,light,overlay,matrices,vertexConsumer);

        if (!CITUtils.isArmorEnchantmentActive())
            return;

        if(vertexConsumers instanceof VertexConsumerProvider.Immediate)
            ((VertexConsumerProvider.Immediate) vertexConsumers).draw(TexturedRenderLayers.getItemEntityTranslucentCull());

        while (CITUtils.preRenderArmorEnchantment()) {

            //every layer can have a different consumer
            VertexConsumer vertexConsumer2 = CITUtils.getVertexConsumer(vertexConsumers,
                    RenderLayers.getItemLayer(FabricMcPatcherClient.itemRendererCurrentStack), true,
                    ColorHelper.fromFloats(
                    CITUtils.boundFade.x, CITUtils.boundFade.y,
                    CITUtils.boundFade.z, CITUtils.boundFade.w
            ));
            //draw something
            original.call(model,tints,light,overlay,matrices,vertexConsumer2);

            CITUtils.postRenderArmorEnchantment();
        }

        /*

        List<VertexConsumer> consumers=new ArrayList<>();


        if (CITUtils.isArmorEnchantmentActive()) {
            while (CITUtils.preRenderArmorEnchantment()) {

                //every layer can have a different consumer
                VertexConsumer vertexConsumer2 = CITUtils.getVertexConsumer(vertexConsumers,
                        RenderLayers.getItemLayer(itemStack), true,
                        ColorHelper.fromFloats(
                                CITUtils.boundFade.x, CITUtils.boundFade.y,
                                CITUtils.boundFade.z, CITUtils.boundFade.w
                        ));
                consumers.add(vertexConsumer2);

                //draw something
                //renderBakedItemModel(model,stack,light,overlay,matrices,vertexConsumer2);

                CITUtils.postRenderArmorEnchantment();
            }
        }
        if(!consumers.isEmpty()) {
            consumers.addFirst(vertices);
            vertices = VertexConsumers.union( consumers.toArray(new VertexConsumer[0]));
        }


        original.call(instance,model,stack,light,overlay,matrices,vertices);

        */
    }
}
