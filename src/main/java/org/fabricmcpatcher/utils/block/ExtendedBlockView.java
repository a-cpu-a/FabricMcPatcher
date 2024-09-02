package org.fabricmcpatcher.utils.block;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registry;
import net.minecraft.world.biome.Biome;

public interface ExtendedBlockView {


    int mcPatcher$getSeaLevel();
    Registry<Biome> mcPatcher$getBiomeRegistry();
}
