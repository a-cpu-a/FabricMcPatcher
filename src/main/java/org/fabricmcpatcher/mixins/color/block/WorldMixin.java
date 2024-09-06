package org.fabricmcpatcher.mixins.color.block;

import net.fabricmc.fabric.api.blockview.v2.FabricBlockView;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(World.class)
public abstract class WorldMixin implements FabricBlockView {

    @Shadow public abstract BiomeAccess getBiomeAccess();

    public RegistryEntry<Biome> getBiomeFabric(BlockPos pos) {
        return getBiomeAccess().getBiome(pos);
    }
}
