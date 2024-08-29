package org.fabricmcpatcher.mixins;

import net.minecraft.client.font.TextRenderer;
import org.fabricmcpatcher.color.ColorizeWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextRenderer.class)
public class TextRendererMixin {
    @Inject(method = "tweakTransparency",at=@At(value = "RETURN"),cancellable = true)
    private static void tweakTransparencyRet(int argb, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ColorizeWorld.colorizeText(argb));
    }
}
