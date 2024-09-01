package org.fabricmcpatcher.mixins.color.entity;

import net.minecraft.block.MapColor;
import org.fabricmcpatcher.accessors.IOriginalColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapColor.class)
public class MapColorMixin implements IOriginalColor {
    @Unique
    private int mcPatcher$originalColor;

    @Inject(method = "<init>",at=@At(value = "RETURN"))
    void initReturn(int id, int color, CallbackInfo ci){
        mcPatcher$originalColor = color;
    }

    @Override
    @Unique
    public int mcPatcher$GetOriginalColor() {
        return mcPatcher$originalColor;
    }
}
