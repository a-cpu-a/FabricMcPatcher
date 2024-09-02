package org.fabricmcpatcher.color.biome;


import net.minecraft.util.Identifier;
import net.minecraft.world.BlockView;

import java.util.Collection;

public interface IColorMap {
    boolean isHeightDependent();

    int getColorMultiplier();

    int getColorMultiplier(BlockView blockAccess, int i, int j, int k);

    float[] getColorMultiplierF(BlockView blockAccess, int i, int j, int k);

    void claimResources(Collection<Identifier> resources);

    IColorMap copy();
}