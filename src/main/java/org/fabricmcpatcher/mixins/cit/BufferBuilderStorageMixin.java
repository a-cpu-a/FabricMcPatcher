package org.fabricmcpatcher.mixins.cit;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.BufferAllocator;
import org.fabricmcpatcher.cit.CITUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BufferBuilderStorage.class)
public abstract class BufferBuilderStorageMixin {

    @Shadow
    private static void assignBufferBuilder(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> builderStorage, RenderLayer layer) {
    }

    @Inject(method = "method_54639",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    void injectAtGetArmorEntityGlint(Object2ObjectLinkedOpenHashMap map, CallbackInfo ci) {

        //assignBufferBuilder(map, CITUtils.ARMOR_ENTITY_GLINT_CUSTOMIZED);
    }
}
