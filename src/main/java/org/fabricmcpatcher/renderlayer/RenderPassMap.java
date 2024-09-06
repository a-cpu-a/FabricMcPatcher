package org.fabricmcpatcher.renderlayer;


import net.minecraft.block.Block;
import net.minecraft.block.IceBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import org.fabricmcpatcher.utils.MAL;
import org.fabricmcpatcher.utils.block.RenderPassAPI;

public class RenderPassMap {
    static final RenderPassMap instance = new RenderPassMap();

    public static int map18To17(int pass) {
        return pass > 1 ? instance.MCPatcherToVanilla(pass) : pass;
    }

    public static int map17To18(int pass) {
        return pass <= 1 ? instance.vanillaToMCPatcher(pass) : pass;
    }

    protected int vanillaToMCPatcher(int pass) {
        return pass;
    }

    protected int MCPatcherToVanilla(int pass) {
        return pass;
    }

    protected int getDefaultRenderPass(Block block) {
        return lay2Num(RenderLayers.getBlockLayer(block.getDefaultState()));
    }

    protected int getCutoutRenderPass() {
        return RenderPassAPI.CUTOUT_RENDER_PASS;
    }

    private static int lay2Num(RenderLayer lay) {
        if(lay==RenderLayer.getSolid())
            return 0;

        if(lay==RenderLayer.getCutoutMipped())
            return 1;

        if(lay==RenderLayer.getCutout())
            return 2;

        //if(lay==RenderLayer.getTranslucent() || tripwire)
        return 3;
    }
}