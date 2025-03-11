package org.fabricmcpatcher.mixins.cit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Identifier;
import org.fabricmcpatcher.accessors.IGetBuilding;
import org.fabricmcpatcher.cit.CITUtils;
import org.fabricmcpatcher.cit.CitMixinUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Mixin(BuiltinModelItemRenderer.class)
public class BuiltinModelItemRendererMixin {
/*
    @Shadow private TridentEntityModel modelTrident;

    @Shadow private ShieldEntityModel modelShield;

    @Inject(method = "render",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;renderEntity(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)Z",shift = At.Shift.BEFORE))
    void renderRenderEntityBefore(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        CitMixinUtils.renderingBlockEntity= CITUtils.setupArmorEnchantments(stack);
    }
    @Inject(method = "render",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;renderEntity(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)Z",shift = At.Shift.AFTER))
    void renderRenderEntityAfter(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        CitMixinUtils.renderingBlockEntity=false;
    }

    @WrapOperation(method = "render",at= @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasGlint()Z"))
    boolean renderHasGlint(ItemStack instance, Operation<Boolean> original) {

        if(CITUtils.setupArmorEnchantments(instance)) {
            return false;//disable original glint
        }
        return original.call(instance);
    }
    @WrapOperation(method = "render",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/TridentEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"))
    void renderTridentRender(TridentEntityModel instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, Operation<Void> original, ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        mcPatcher$renderRender(instance,instance.getRootPart(),matrixStack,vertexConsumer,light,overlay,original,stack,mode,matrices,vertexConsumers);
    }
    @WrapOperation(method = "render",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"))
    void renderShieldRender(ModelPart instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, Operation<Void> original, ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        mcPatcher$renderRender(instance,instance,matrixStack,vertexConsumer,light,overlay,original,stack,mode,matrices,vertexConsumers);
    }



    @Unique
    private void mcPatcher$renderRender(Object realInst,ModelPart instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, Operation<Void> original, ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {

        boolean trident = realInst instanceof TridentEntityModel;


        if(!trident && (vertexConsumer instanceof BufferBuilder || vertexConsumer instanceof SpriteTexturedVertexConsumer) && !((IGetBuilding) vertexConsumer).mcPatcher$getBuilding()) {

            //we are drawing the banner / shield part

            CITUtils.setupArmorEnchantments(stack);

            vertexConsumer = ModelBaker.SHIELD_BASE.getSprite()
                    .getTextureSpecificVertexConsumer(
                            ItemRenderer.getItemGlintConsumer(vertexConsumers, this.modelShield.getLayer(ModelBaker.SHIELD_BASE.getAtlasId()), false, false)
                    );
        }
        Identifier tex = trident ?TridentEntityModel.TEXTURE:ModelBaker.SHIELD_BASE.getAtlasId();
        CitMixinUtils.renderGlintLayers(realInst, instance, matrixStack, vertexConsumer, light, overlay, original, vertexConsumers, trident ? modelTrident.getLayer(tex) : modelShield.getLayer(tex));
    }*/


}
