package org.fabricmcpatcher.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method="<init>",at= @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;instance:Lnet/minecraft/client/MinecraftClient;",ordinal = 0))
    private void initHead(RunArgs args, CallbackInfo ci) {
        try{

            System.load("C:/Program Files/RenderDoc/renderdoc.dll");
        }
        catch (UnsatisfiedLinkError ignored){}
    }
}
