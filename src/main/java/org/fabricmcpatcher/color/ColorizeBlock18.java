package org.fabricmcpatcher.color;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.fabricmcpatcher.color.biome.ColorUtils;
import org.fabricmcpatcher.color.biome.IColorMap;
import org.fabricmcpatcher.ctm.CTMUtils18;
import org.fabricmcpatcher.resource.PropertiesFile;
import org.fabricmcpatcher.resource.TexturePackAPI;
import org.fabricmcpatcher.resource.TexturePackChangeHandler;
import org.fabricmcpatcher.utils.MCLogger;
import org.fabricmcpatcher.utils.MCPatcherUtils;
import org.fabricmcpatcher.utils.block.BlockAPI;
import org.fabricmcpatcher.utils.block.BlockStateMatcher;
import org.fabricmcpatcher.utils.block.RenderPassAPI;

import java.util.List;

public class ColorizeBlock18 {

    //https://github.com/NicosMinecraftArchive/mcpatcher-bitbucket/blob/1aa45839b2cd029143809edfa60ec59e5ef75f80/stubs/src/net/minecraft/src/ModelFace.java#L4

    //https://github.com/NicosMinecraftArchive/mcpatcher-bitbucket/blob/master/mods/src/com/prupe/mcpatcher/mod/cc/CC_Block.java
    //https://github.com/NicosMinecraftArchive/mcpatcher-bitbucket/blob/master/mods/src/com/prupe/mcpatcher/mod/cc/CustomColors.java

    private static final MCLogger logger = MCLogger.getLogger(MCPatcherUtils.CUSTOM_COLORS);

    private static final String COLOR_PROPERTIES = "color.properties";

    private static Block grassBlock;
    private static Block mycelBlock;

    private final CTMUtils18 ctm;
    private boolean useCM;
    private IColorMap colorMap;
    private boolean isSmooth;
    private final float[][] vertexColors = new float[4][3];
    public final float[] vertexColor = new float[3];

    private static final int[][][] FACE_VERTICES = new int[][][]{
        // bottom face (y=0)
        {
            {0, 0, 1}, // top left
            {0, 0, 0}, // bottom left
            {1, 0, 0}, // bottom right
            {1, 0, 1}, // top right
        },
        // top face (y=1)
        {
            {0, 1, 0},
            {0, 1, 1},
            {1, 1, 1},
            {1, 1, 0},
        },
        // north face (z=0)
        {
            {1, 1, 0},
            {1, 0, 0},
            {0, 0, 0},
            {0, 1, 0},
        },
        // south face (z=1)
        {
            {0, 1, 1},
            {0, 0, 1},
            {1, 0, 1},
            {1, 1, 1},
        },
        // west face (x=0)
        {
            {0, 1, 0},
            {0, 0, 0},
            {0, 0, 1},
            {0, 1, 1},
        },
        // east face (x=1)
        {
            {1, 1, 1},
            {1, 0, 1},
            {1, 0, 0},
            {1, 1, 0},
        }
    };

    private static final int[][][] FACE_VERTICES_WATER = new int[][][]{
        // bottom face (y=0)
        {
            {0, 0, 1}, // top left
            {0, 0, 0}, // bottom left
            {1, 0, 0}, // bottom right
            {1, 0, 1}, // top right
        },
        // top face (y=1)
        {
            {0, 1, 0},
            {0, 1, 1},
            {1, 1, 1},
            {1, 1, 0},
        },
        // north face (z=0)
        {
            {0, 1, 0},
            {1, 1, 0},
            {1, 0, 0},
            {0, 0, 0},
        },
        // south face (z=1)
        {
            {1, 1, 1},
            {0, 1, 1},
            {0, 0, 1},
            {1, 0, 1},
        },
        // west face (x=0)
        {
            {0, 1, 1},
            {0, 1, 0},
            {0, 0, 0},
            {0, 0, 1},
        },
        // east face (x=1)
        {
            {1, 1, 0},
            {1, 1, 1},
            {1, 0, 1},
            {1, 0, 0},
        }
    };

    static {
        TexturePackChangeHandler.register(new TexturePackChangeHandler(MCPatcherUtils.CUSTOM_COLORS, 2) {
            @Override
            public void beforeChange() {
                reset();
            }

            @Override
            public void afterChange() {
                PropertiesFile properties = PropertiesFile.getNonNull(logger, COLOR_PROPERTIES);
                ColorizeBlock.reloadAll(properties);
            }
        });
    }

    public static void reset() {
        try {
            grassBlock = BlockAPI.getFixedBlock("minecraft:grass_block");//minecraft:grass
            mycelBlock = BlockAPI.getFixedBlock("minecraft:mycelium");
            ColorizeBlock.reset();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public ColorizeBlock18(CTMUtils18 ctm) {
        this.ctm = ctm;
    }

    public void preRender(BlockRenderView blockAccess, BakedModel model, BlockState blockState, BlockPos position, Block block, boolean useAO) {
        colorMap = null;
        useCM = RenderPassAPI.instance.useColorMultiplierThisPass(block);
        if (useCM) {
            List<BlockStateMatcher> maps = ColorizeBlock.findColorMaps(block);
            if (maps != null) {
                for (BlockStateMatcher matcher : maps) {
                    if (matcher.matchBlockState(blockState)) {
                        colorMap = ColorizeBlock.getThreadLocal(matcher);
                        break;
                    }
                }
            }
        }
        isSmooth = false;
    }

    public void preRenderHeld(BakedModel model, BlockState blockState, Block block) {
        colorMap = null;
        isSmooth = false;
        List<BlockStateMatcher> maps = ColorizeBlock.findColorMaps(block);
        if (maps != null) {
            for (BlockStateMatcher matcher : maps) {
                if (matcher.matchBlockState(blockState)) {
                    colorMap = ColorizeBlock.getThreadLocal(matcher);
                    break;
                }
            }
        }
    }

    public void clear() {
        colorMap = null;
        isSmooth = false;
    }

    public void setDirection(Direction direction) {
        setDirection(direction, FACE_VERTICES);
    }

    public void setDirectionWater(Direction direction) {
        setDirection(direction, FACE_VERTICES_WATER);
    }

    private void setDirection(Direction direction, int[][][] faceVertices) {
        if (ColorizeBlock.enableSmoothBiomes && direction != null && colorMap != null && ctm.isInWorld()) {
            isSmooth = true;
            int[][] offsets = faceVertices[direction.ordinal()];
            computeVertexColor(offsets[0], vertexColors[0]);
            computeVertexColor(offsets[1], vertexColors[1]);
            computeVertexColor(offsets[2], vertexColors[2]);
            computeVertexColor(offsets[3], vertexColors[3]);
        } else {
            isSmooth = false;
        }
    }

    private void computeVertexColor(int[] offsets, float[] color) {
        int i = ctm.getI() + offsets[0];
        int j = ctm.getJ() + offsets[1];
        int k = ctm.getK() + offsets[2];
        if (ColorizeBlock.enableTestColorSmoothing) {
            int rgb = 0;
            if (i % 2 == 0) {
                rgb |= 0xff0000;
            }
            if (j % 2 == 0) {
                rgb |= 0x00ff00;
            }
            if (k % 2 == 0) {
                rgb |= 0x0000ff;
            }
            ColorUtils.intToFloat3(rgb, color);
        } else {
            float[] tmp = colorMap.getColorMultiplierF(ctm.getBlockAccess(), i, j, k);
            color[0] = tmp[0];
            color[1] = tmp[1];
            color[2] = tmp[2];
        }
    }

    public boolean useColormap(BakedQuad face) {
        return useCM && (face.hasTint() || (colorMap != null && ctm.getBlock() != grassBlock && ctm.getBlock() != mycelBlock));
    }

    public int colorMultiplier(int color) {
        if (colorMap == null) {
            return color;
        } else if (ctm.isInWorld()) {
            return colorMap.getColorMultiplier(ctm.getBlockAccess(), ctm.getI(), ctm.getJ(), ctm.getK());
        } else {
            return colorMap.getColorMultiplier();
        }
    }

    public float getVertexColor(float color, int vertex, int channel) {
        if (isSmooth) {
            return vertexColors[vertex][channel];
        } else {
            return color;
        }
    }

    public void applyVertexColor(Tessellator tessellator, float base, int vertex) {
        if (isSmooth) {
            float[] rgb = vertexColors[vertex];
            //TessellatorAPI.setColorOpaque_F(tessellator, base * rgb[0], base * rgb[1], base * rgb[2]); //TODO
        }
    }

    public float applyVertexColor(float base, int vertex, float r, float g, float b) {
        if (isSmooth) {
            float[] rgb = vertexColors[vertex];
            vertexColor[0] = base * rgb[0];
            vertexColor[1] = base * rgb[1];
            vertexColor[2] = base * rgb[2];
        } else {
            vertexColor[0] = r;
            vertexColor[1] = g;
            vertexColor[2] = b;
        }
        return vertexColor[0];
    }

    public float getVertexColor(int index) {
        return vertexColor[index];
    }

    public int getParticleColor(BlockRenderView blockAccess, BlockState blockState, BlockPos position, int defaultColor) {
        return getColorMultiplier(blockAccess, blockState, position, defaultColor);
    }

    // public static methods requested by MamiyaOtaru for VoxelMap
    public static int getColorMultiplier(BlockRenderView blockAccess, BlockState blockState, BlockPos position, int defaultColor) {
        List<BlockStateMatcher> maps = ColorizeBlock.findColorMaps(blockState.getBlock());
        if (maps != null) {
            for (BlockStateMatcher matcher : maps) {
                if (matcher.matchBlockState(blockState)) {
                    IColorMap colorMap = ColorizeBlock.getThreadLocal(matcher);
                    return colorMap.getColorMultiplier(blockAccess, position.getX(), position.getY(), position.getZ());
                }
            }
        }
        return defaultColor;
    }

    public static int getColorMultiplier(BlockRenderView blockAccess, BlockState blockState, BlockPos position) {
        return getColorMultiplier(blockAccess, blockState, position, 0xffffff);
    }
}