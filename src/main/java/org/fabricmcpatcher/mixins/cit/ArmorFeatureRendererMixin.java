package org.fabricmcpatcher.mixins.cit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper;
import org.fabricmcpatcher.cit.CITUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<S extends BipedEntityRenderState, M extends BipedEntityModel<S>, A extends BipedEntityModel<S>> extends FeatureRenderer<S, M> {

    public ArmorFeatureRendererMixin(FeatureRendererContext<S, M> context) {
        super(context);
    }

    @WrapOperation(method = "renderArmor",at= @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasGlint()Z"))
    boolean renderArmorHasGlint(ItemStack instance, Operation<Boolean> original, MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, EquipmentSlot armorSlot, int light, A model) {

        if(CITUtils.setupArmorEnchantments(instance)) {

            while (CITUtils.preRenderArmorEnchantment()) {
                //draw something
                model.render(matrices, vertexConsumers.getBuffer(CITUtils.ARMOR_ENTITY_GLINT_CUSTOMIZED.apply(
                        CITUtils.boundTex, CITUtils.boundBlending,CITUtils.boundGlintInfo)), light, OverlayTexture.DEFAULT_UV,
                        ColorHelper.fromFloats(
                                CITUtils.boundFade.x,CITUtils.boundFade.y,
                                CITUtils.boundFade.z,CITUtils.boundFade.w
                        ));

                CITUtils.postRenderArmorEnchantment();
            }

            return false;//disable original glint
        }

        return original.call(instance);
    }
}
