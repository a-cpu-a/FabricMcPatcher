package org.fabricmcpatcher.mixins.color.block;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.BlockView;
import net.minecraft.world.biome.Biome;
import org.fabricmcpatcher.utils.block.ExtendedBlockView;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockView.class)
public interface BlockViewMixin extends ExtendedBlockView {
    @Override
    public default int mcPatcher$getSeaLevel() {
        return 63;//default value
    }

    @Override
    public default Registry<Biome> mcPatcher$getBiomeRegistry() {
        return MinecraftClient.getInstance().world.getRegistryManager().get(RegistryKeys.BIOME);
    }
}
