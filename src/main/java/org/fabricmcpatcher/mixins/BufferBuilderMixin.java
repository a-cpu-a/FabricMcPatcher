package org.fabricmcpatcher.mixins;

import net.minecraft.client.render.BufferBuilder;
import org.fabricmcpatcher.accessors.IGetBuilding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BufferBuilder.class)
public class BufferBuilderMixin implements IGetBuilding {

    @Shadow private boolean building;

    @Unique
    public boolean mcPatcher$getBuilding() {
        return building;
    }
}
