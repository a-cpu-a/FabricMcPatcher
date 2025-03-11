package org.fabricmcpatcher.mixins.cit;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.BasicItemModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.fabricmcpatcher.accessors.IStackHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BasicItemModel.class)
public class BasicItemModelMixin {

    @Inject(method = "update", at = @At(value = "RETURN"))
    void updateReturn(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ModelTransformationMode transformationMode, ClientWorld world, LivingEntity user, int seed, CallbackInfo ci, @Local ItemRenderState.LayerRenderState layerRenderState)
    {
        ((IStackHolder) layerRenderState).fabricMcPatcher$setStack(stack);
    }
}
