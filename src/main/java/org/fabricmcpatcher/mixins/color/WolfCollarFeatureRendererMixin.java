package org.fabricmcpatcher.mixins.color;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.util.DyeColor;
import org.fabricmcpatcher.color.ColorizeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WolfCollarFeatureRenderer.class)
public class WolfCollarFeatureRendererMixin {

    @WrapOperation(at= @At(value = "INVOKE", target = "Lnet/minecraft/util/DyeColor;getEntityColor()I"), method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/WolfEntityRenderState;FF)V")
    int renderGetEntityColor(DyeColor instance, Operation<Integer> original) {
        Integer col =  ColorizeEntity.getWolfCollarColor(instance.getId());
        if(col!=null)
            return col;
        return original.call(instance);
    }
}
