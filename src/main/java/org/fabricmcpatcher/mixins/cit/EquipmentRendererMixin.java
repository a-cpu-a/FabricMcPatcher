package org.fabricmcpatcher.mixins.cit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.fabricmcpatcher.cit.CITUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;


@Mixin(EquipmentRenderer.class)
public abstract class EquipmentRendererMixin {


    @Unique
    private boolean drawGlint=false;

    @WrapOperation(method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
            at= @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasGlint()Z"))
    boolean renderArmorHasGlint(ItemStack instance, Operation<Boolean> original) {

        drawGlint=false;

        if(CITUtils.setupArmorEnchantments(instance)) {
            drawGlint=true;

            return false;//disable original glint
        }

        return original.call(instance);
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
    at=@At(value = "RETURN"))
    void renderReturn(EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> assetKey, Model model, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, @Nullable Identifier texture, CallbackInfo ci) {
        if(drawGlint) {
            drawGlint=false;
            while (CITUtils.preRenderArmorEnchantment()) {
                //draw something
                model.render(matrices, vertexConsumers.getBuffer(CITUtils.ARMOR_ENTITY_GLINT_CUSTOMIZED.apply(
                        CITUtils.boundTex, CITUtils.boundBlending,CITUtils.boundGlintInfo,
                        ColorHelper.fromFloats(
                                CITUtils.boundFade.x,CITUtils.boundFade.y,
                                CITUtils.boundFade.z,CITUtils.boundFade.w
                        ))), light, OverlayTexture.DEFAULT_UV);

                CITUtils.postRenderArmorEnchantment();
            }
        }
    }
}
