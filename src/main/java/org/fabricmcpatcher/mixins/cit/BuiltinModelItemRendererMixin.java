package org.fabricmcpatcher.mixins.cit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper;
import org.fabricmcpatcher.cit.CITUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltinModelItemRenderer.class)
public class BuiltinModelItemRendererMixin {

    @Shadow private TridentEntityModel modelTrident;

    @WrapOperation(method = "render",at= @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasGlint()Z"))
    boolean renderHasGlint(ItemStack instance, Operation<Boolean> original) {

        if(CITUtils.setupArmorEnchantments(instance)) {
            return false;//disable original glint
        }
        return original.call(instance);
    }
    @Inject(method = "render",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/TridentEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",shift = At.Shift.AFTER))
    void renderTridentRender(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {

        if(!CITUtils.isArmorEnchantmentActive())
            return;

        while (CITUtils.preRenderArmorEnchantment()) {

            //every layer can have a different consumer
            VertexConsumer vertexConsumer2 = CITUtils.getVertexConsumer(vertexConsumers, this.modelTrident.getLayer(TridentEntityModel.TEXTURE), false);
            //draw something
            this.modelTrident.render(matrices, vertexConsumer2, light, overlay,ColorHelper.fromFloats(
                    CITUtils.boundFade.x,CITUtils.boundFade.y,
                    CITUtils.boundFade.z,CITUtils.boundFade.w
            ));

            CITUtils.postRenderArmorEnchantment();
        }
    }
}
