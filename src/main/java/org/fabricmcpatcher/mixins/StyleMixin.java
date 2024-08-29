package org.fabricmcpatcher.mixins;

import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.fabricmcpatcher.color.ColorizeWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Style.class)
public abstract class StyleMixin {
    @Shadow public abstract Style withColor(@Nullable TextColor color);

    @Inject(method = "withColor(I)Lnet/minecraft/text/Style;",at=@At(value = "HEAD"),cancellable = true)
    void withColorHead(int rgbColor, CallbackInfoReturnable<Style> cir) {
        int col = ColorizeWorld.colorizeText(rgbColor);
        if(col==-1)return;//not changed

        cir.setReturnValue(this.withColor(TextColor.fromRgb(col)));
        cir.cancel();
    }
}
