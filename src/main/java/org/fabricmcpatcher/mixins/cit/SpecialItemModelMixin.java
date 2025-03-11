package org.fabricmcpatcher.mixins.cit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.SpecialItemModel;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;
import org.fabricmcpatcher.accessors.IStackHolder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpecialItemModel.class)
public class SpecialItemModelMixin<T> {

    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderState$LayerRenderState;setSpecialModel(Lnet/minecraft/client/render/item/model/special/SpecialModelRenderer;Ljava/lang/Object;Lnet/minecraft/client/render/model/BakedModel;)V"))
    void updateReturn(ItemRenderState.LayerRenderState instance, SpecialModelRenderer<T> specialModelType, @Nullable T data, BakedModel model, Operation<Void> original, @Local(argsOnly = true) ItemStack stack)
    {
        original.call(instance, specialModelType, data, model);
        ((IStackHolder) instance).fabricMcPatcher$setStack(stack);
    }
}
