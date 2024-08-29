package org.fabricmcpatcher.mixins;

import net.minecraft.util.Formatting;
import org.fabricmcpatcher.color.ColorizeWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Formatting.class)
public class FormattingMixin {

    @Shadow @Final private @Nullable Integer colorValue;

    @Shadow @Final private int colorIndex;

    //TODO: add special shadow color override to TextColor, cuz the upper colorIndexes are shadow colors
    @Inject(method = "getColorValue", at=@At(value = "HEAD"),cancellable = true)
    void getColorValueHead(CallbackInfoReturnable<Integer> cir) {
        if(colorValue==null)return;

        cir.setReturnValue(ColorizeWorld.colorizeText(colorValue,colorIndex));
        cir.cancel();
    }
}
