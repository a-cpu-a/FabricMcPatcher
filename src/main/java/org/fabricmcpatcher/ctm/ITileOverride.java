package org.fabricmcpatcher.ctm;


import net.minecraft.client.texture.Sprite;
import org.fabricmcpatcher.utils.block.BlockStateMatcher;

import java.util.List;
import java.util.Set;

interface ITileOverride extends Comparable<ITileOverride> {
    boolean isDisabled();

    void registerIcons();

    List<BlockStateMatcher> getMatchingBlocks();

    Set<String> getMatchingTiles();

    int getRenderPass();

    int getWeight();

    Sprite getTileWorld(RenderBlockState renderBlockState, Sprite origIcon);

    Sprite getTileHeld(RenderBlockState renderBlockState, Sprite origIcon);
}