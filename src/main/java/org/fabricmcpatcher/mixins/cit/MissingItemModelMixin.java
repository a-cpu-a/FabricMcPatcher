package org.fabricmcpatcher.mixins.cit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.MissingItemModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.fabricmcpatcher.accessors.IStackHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MissingItemModel.class)
public class MissingItemModelMixin {

    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderState$LayerRenderState;setModel(Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/client/render/RenderLayer;)V"))
    void updateReturn(ItemRenderState.LayerRenderState instance, BakedModel model, RenderLayer renderLayer, Operation<Void> original, @Local(argsOnly = true) ItemStack stack)
    {
        original.call(instance,model,renderLayer);
        ((IStackHolder) instance).fabricMcPatcher$setStack(stack);
    }
}
