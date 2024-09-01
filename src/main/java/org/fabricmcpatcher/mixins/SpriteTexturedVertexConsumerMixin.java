package org.fabricmcpatcher.mixins;

import net.minecraft.client.render.SpriteTexturedVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import org.fabricmcpatcher.accessors.IGetBuilding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SpriteTexturedVertexConsumer.class)
public class SpriteTexturedVertexConsumerMixin implements IGetBuilding {

    @Shadow @Final private VertexConsumer delegate;

    @Unique
    public boolean mcPatcher$getBuilding() {
        if(delegate instanceof IGetBuilding)
            return ((IGetBuilding)delegate).mcPatcher$getBuilding();
        return false;
    }
}
