package org.fabricmcpatcher.mixins.color;

import net.minecraft.text.TextColor;
import org.fabricmcpatcher.accessors.IMutableColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TextColor.class)
public abstract class TextColorMixin implements IMutableColor {

    @Shadow private int rgb;

    @Unique
    public void mcpatcher$setRgb(int v) {
        rgb=v;
    }
}
