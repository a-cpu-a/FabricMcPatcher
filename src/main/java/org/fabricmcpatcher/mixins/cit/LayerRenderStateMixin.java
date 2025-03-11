package org.fabricmcpatcher.mixins.cit;

import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.ItemStack;
import org.fabricmcpatcher.accessors.IStackHolder;
import org.fabricmcpatcher.client.FabricMcPatcherClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderState.LayerRenderState.class)
public class LayerRenderStateMixin implements IStackHolder {

    @Unique
    private ItemStack stack=null;

    @Unique
    public void fabricMcPatcher$setStack(ItemStack stack) {
        this.stack = stack;
    }
    @Unique
    public ItemStack fabricMcPatcher$getStack() {
        return this.stack;
    }

    @Inject(method = "clear",at = @At(value = "HEAD"))
    void clearHead(CallbackInfo ci) {
        stack=null;
    }
    @Inject(method = "render",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"))
    void renderPreRender(CallbackInfo ci) {
        FabricMcPatcherClient.itemRendererCurrentStack = stack;
    }
}
