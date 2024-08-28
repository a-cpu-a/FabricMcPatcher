package org.fabricmcpatcher.color.biome;


import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;

import java.util.Collection;

public interface IColorMap {
    boolean isHeightDependent();

    int getColorMultiplier();

    int getColorMultiplier(ClientWorld blockAccess, int i, int j, int k);

    float[] getColorMultiplierF(ClientWorld blockAccess, int i, int j, int k);

    void claimResources(Collection<Identifier> resources);

    IColorMap copy();
}