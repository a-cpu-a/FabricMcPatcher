package org.fabricmcpatcher.mixins.cit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper;
import org.fabricmcpatcher.cit.CITUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow protected abstract void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices);

    @WrapOperation(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;Z)V",
            at= @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasGlint()Z",ordinal = 1))
    boolean renderItemQuadsHasGlint(ItemStack instance, Operation<Boolean> original) {

        if(CITUtils.setupArmorEnchantments(instance)) {
            return false;//disable original glint
        }
        return original.call(instance);
    }
    @WrapOperation(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;Z)V",
            at= @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;renderBakedItemModel(Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;)V"
            ))
    void renderItemRenderBakedItemModel(ItemRenderer instance, BakedModel model, ItemStack stack, int light,
                                        int overlay, MatrixStack matrices, VertexConsumer vertices, Operation<Void> original,
                                        ItemStack itemStack,
                                        ModelTransformationMode modelTransformationMode,
                                        MatrixStack matrixStack,
                                        VertexConsumerProvider vertexConsumers) {



        original.call(instance,model,stack,light,overlay,matrices,vertices);

        if (!CITUtils.isArmorEnchantmentActive())
            return;

        if(vertexConsumers instanceof VertexConsumerProvider.Immediate)
            ((VertexConsumerProvider.Immediate) vertexConsumers).draw(TexturedRenderLayers.getItemEntityTranslucentCull());

        while (CITUtils.preRenderArmorEnchantment()) {

            //every layer can have a different consumer
            VertexConsumer vertexConsumer2 = CITUtils.getVertexConsumer(vertexConsumers,
                    RenderLayers.getItemLayer(itemStack), true,
                    ColorHelper.fromFloats(
                    CITUtils.boundFade.x, CITUtils.boundFade.y,
                    CITUtils.boundFade.z, CITUtils.boundFade.w
            ));
            //draw something
            renderBakedItemModel(model,stack,light,overlay,matrices,vertexConsumer2);

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
