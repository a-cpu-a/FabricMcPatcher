package org.fabricmcpatcher.color;

import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.util.Identifier;
import net.minecraft.world.BlockRenderView;
import org.fabricmcpatcher.color.biome.*;
import org.fabricmcpatcher.resource.PropertiesFile;
import org.fabricmcpatcher.resource.ResourceList;
import org.fabricmcpatcher.resource.TexturePackAPI;
import org.fabricmcpatcher.utils.Config;
import org.fabricmcpatcher.utils.MCLogger;
import org.fabricmcpatcher.utils.MCPatcherUtils;
import org.fabricmcpatcher.utils.block.BlockAPI;
import org.fabricmcpatcher.utils.block.BlockStateMatcher;
import org.fabricmcpatcher.utils.block.RenderBlocksUtils;

import java.util.*;

public class ColorizeBlock {
    private static final MCLogger logger = MCLogger.getLogger(MCPatcherUtils.CUSTOM_COLORS);

    private static final boolean useWaterColors = Config.getBoolean(MCPatcherUtils.CUSTOM_COLORS, "water", true);
    private static final boolean useTreeColors = Config.getBoolean(MCPatcherUtils.CUSTOM_COLORS, "tree", true);
    private static final boolean useRedstoneColors = Config.getBoolean(MCPatcherUtils.CUSTOM_COLORS, "redstone", true);
    private static final boolean useStemColors = Config.getBoolean(MCPatcherUtils.CUSTOM_COLORS, "stem", true);
    private static final boolean useBlockColors = Config.getBoolean(MCPatcherUtils.CUSTOM_COLORS, "otherBlocks", true);

    static final boolean enableSmoothBiomes = Config.getBoolean(MCPatcherUtils.CUSTOM_COLORS, "smoothBiomes", true);
    static final boolean enableTestColorSmoothing = Config.getBoolean(MCPatcherUtils.CUSTOM_COLORS, "testColorSmoothing", false);//debug thing

    private static final String REDSTONE_COLORS = "colormap/redstone.png";
    private static final String STEM_COLORS = "colormap/stem.png";
    private static final String PUMPKIN_STEM_COLORS = "colormap/pumpkinstem.png";
    private static final String MELON_STEM_COLORS = "colormap/melonstem.png";
    private static final String SWAMPGRASSCOLOR = "colormap/swampgrass.png";
    private static final String SWAMPFOLIAGECOLOR = "colormap/swampfoliage.png";
    private static final Identifier DEFAULT_GRASSCOLOR = Identifier.of(TexturePackAPI.select("/misc/grasscolor.png", "minecraft:textures/colormap/grass.png"));
    private static final Identifier DEFAULT_FOLIAGECOLOR = Identifier.of(TexturePackAPI.select("/misc/foliagecolor.png", "minecraft:textures/colormap/foliage.png"));
    private static final String PINECOLOR = "colormap/pine.png";
    private static final String BIRCHCOLOR = "colormap/birch.png";
    private static final String WATERCOLOR = "colormap/water.png";

    private static final String PALETTE_BLOCK_KEY = "palette.block.";

    private static Block waterBlock;
    private static Block staticWaterBlock;

    private static final Map<Block, List<BlockStateMatcher>> blockColorMaps = new IdentityHashMap<>(); // bitmaps from palette.block.*
    private static IColorMap waterColorMap;
    private static float[][] redstoneColor; // colormap/redstone.png

    private static final int blockBlendRadius = Config.getInt(MCPatcherUtils.CUSTOM_COLORS, "blockBlendRadius2", 4);

    public static int blockColor;
    public static boolean isSmooth;

    public static float colorRedTopLeft;
    public static float colorRedBottomLeft;
    public static float colorRedBottomRight;
    public static float colorRedTopRight;
    public static float colorGreenTopLeft;
    public static float colorGreenBottomLeft;
    public static float colorGreenBottomRight;
    public static float colorGreenTopRight;
    public static float colorBlueTopLeft;
    public static float colorBlueBottomLeft;
    public static float colorBlueBottomRight;
    public static float colorBlueTopRight;

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
            {1, 1, 1},
            {1, 1, 0},
            {0, 1, 0},
            {0, 1, 1},
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
            {0, 1, 1},
            {0, 0, 1},
            {1, 0, 1},
            {1, 1, 1},
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
            {1, 0, 1},
            {1, 0, 0},
            {1, 1, 0},
            {1, 1, 1},
        },

        // bottom face, water (y=0)
        {
            {0, 0, 1}, // top left
            {0, 0, 0}, // bottom left
            {1, 0, 0}, // bottom right
            {1, 0, 1}, // top right
        },
        // top face, water (y=1) cycle by 2
        {
            {0, 1, 0},
            {0, 1, 1},
            {1, 1, 1},
            {1, 1, 0},
        },
        // north face, water (z=0)
        {
            {0, 1, 0},
            {1, 1, 0},
            {1, 0, 0},
            {0, 0, 0},
        },
        // south face, water (z=1) cycle by 1
        {
            {1, 1, 1},
            {0, 1, 1},
            {0, 0, 1},
            {1, 0, 1},
        },
        // west face, water (x=0)
        {
            {0, 1, 1},
            {0, 1, 0},
            {0, 0, 0},
            {0, 0, 1},
        },
        // east face, water (x=1) cycle by 2
        {
            {1, 1, 0},
            {1, 1, 1},
            {1, 0, 1},
            {1, 0, 0},
        },
    };

    static {
        try {
            reset();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    static void reset() {
        waterBlock = BlockAPI.getFixedBlock("minecraft:water");//flowing_water
        staticWaterBlock = BlockAPI.getFixedBlock("minecraft:water");

        blockColorMaps.clear();
        waterColorMap = null;
        resetVertexColors();
        redstoneColor = null;
    }

    static void reloadAll(PropertiesFile properties) {
        if (useBlockColors) {
            reloadBlockColors(properties);
        }
        if (useTreeColors) {
            reloadFoliageColors(properties);
        }
        if (useWaterColors) {
            reloadWaterColors(properties);
        }
        if (ColorMap.useSwampColors) {
            reloadSwampColors(properties);
        }
        if (useRedstoneColors) {
            reloadRedstoneColors(properties);
        }
        if (useStemColors) {
            reloadStemColors(properties);
        }
    }

    private static void reloadFoliageColors(PropertiesFile properties) {
        IColorMap colorMap = ColorMap.loadVanillaColorMap(DEFAULT_GRASSCOLOR, TexturePackAPI.newMCPatcherIdentifier(SWAMPGRASSCOLOR));
        registerColorMap(colorMap, DEFAULT_GRASSCOLOR,
                "minecraft:grass_block:snowy=false minecraft:short_grass minecraft:fern minecraft:tall_grass minecraft:large_fern"); //minecraft:grass:snowy=false minecraft:tallgrass:1,2:type=tall_grass,fern minecraft:double_plant:2,3:variant=double_grass,double_fern
        colorMap = ColorMap.loadVanillaColorMap(DEFAULT_FOLIAGECOLOR, TexturePackAPI.newMCPatcherIdentifier(SWAMPFOLIAGECOLOR));
        registerColorMap(colorMap, DEFAULT_FOLIAGECOLOR, "minecraft:oak_leaves minecraft:vine"); //minecraft:leaves:0,4,8,12:variant=oak minecraft:vine
        registerColorMap(TexturePackAPI.newMCPatcherIdentifier(PINECOLOR), "minecraft:spruce_leaves"); //minecraft:leaves:1,5,9,13:variant=spruce
        registerColorMap(TexturePackAPI.newMCPatcherIdentifier(BIRCHCOLOR), "minecraft:birch-leaves"); //minecraft:leaves:2,6,10,14:variant=birch
    }

    private static IColorMap wrapBlockMap(IColorMap map) {
        if (map == null) {
            return null;
        } else {
            if (blockBlendRadius > 0) {
                map = new ColorMapBase.Blended(map, blockBlendRadius);
            }
            map = new ColorMapBase.Chunked(map);
            map = new ColorMapBase.Outer(map);
            return map;
        }
    }

    private static void reloadWaterColors(PropertiesFile properties) {
        waterColorMap = registerColorMap(TexturePackAPI.newMCPatcherIdentifier(WATERCOLOR), "minecraft:water"); //minecraft:flowing_water
        if (waterColorMap == null) {
            waterColorMap = new ColorMap.Water();
            registerColorMap(waterColorMap, null, "minecraft:water");//minecraft:flowing_water
        }
    }

    private static void reloadSwampColors(PropertiesFile properties) {
        int[] lilypadColor = new int[]{0x020830};
        if (Colorizer.loadIntColor("lilypad", lilypadColor, 0)) {
            IColorMap colorMap = new ColorMap.Fixed(lilypadColor[0]);
            registerColorMap(colorMap, TexturePackAPI.newMCPatcherIdentifier(Colorizer.COLOR_PROPERTIES), "minecraft:lily_pad"); //minecraft:waterlily
        }
    }

    private static void reloadBlockColors(PropertiesFile properties) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!key.startsWith(PALETTE_BLOCK_KEY)) {
                continue;
            }
            key = key.substring(PALETTE_BLOCK_KEY.length()).trim();
            Identifier resource = TexturePackAPI.parseIdentifier(TexturePackAPI.newMCPatcherIdentifier(Colorizer.COLOR_PROPERTIES), key);
            if (resource == null) {
                continue;
            }
            registerColorMap(resource, value);
        }

        for (Identifier resource : ResourceList.getInstance().listResources(ColorMap.BLOCK_COLORMAP_DIR, ".properties", false)) {
            Properties properties1 = TexturePackAPI.getProperties(resource);
            IColorMap colorMap = ColorMap.loadColorMap(true, resource, properties1);
            registerColorMap(colorMap, resource, MCPatcherUtils.getStringProperty(properties1, "blocks", getDefaultBlockName(resource)));
        }
        List<Identifier> unusedPNGs = new ArrayList<Identifier>(ColorMap.unusedPNGs);
        for (Identifier resource : unusedPNGs) {
            Properties properties1 = new Properties();
            IColorMap colorMap = ColorMap.loadColorMap(true, resource, properties1);
            registerColorMap(colorMap, resource, getDefaultBlockName(resource));
        }
    }

    private static String getDefaultBlockName(Identifier resource) {
        return resource.getNamespace() + ":" + resource.getPath().replaceFirst(".*/", "").replaceFirst("\\.[^.]*$", "");
    }

    private static IColorMap registerColorMap(Identifier resource, String idList) {
        IColorMap colorMap = ColorMap.loadColorMap(true, resource, null);
        return registerColorMap(colorMap, resource, idList);
    }

    private static IColorMap registerColorMap(IColorMap colorMap, Identifier resource, String idList) {
        if (colorMap == null) {
            return null;
        }
        colorMap = wrapBlockMap(colorMap);
        for (String idString : idList.split("\\s+")) {
            BlockStateMatcher blockMatcher = BlockAPI.createMatcher(new PropertiesFile(logger, resource), idString);
            if (blockMatcher != null) {
                List<BlockStateMatcher> maps = blockColorMaps.get(blockMatcher.getBlock());
                if (maps == null) {
                    maps = new ArrayList<BlockStateMatcher>();
                    blockColorMaps.put(blockMatcher.getBlock(), maps);
                }
                blockMatcher.setData(colorMap);
                maps.add(blockMatcher);
                if (resource != null) {
                    logger.fine("using %s for block %s, default color %06x",
                        colorMap, blockMatcher, colorMap.getColorMultiplier()
                    );
                }
            }
        }
        return colorMap;
    }

    private static void reloadRedstoneColors(PropertiesFile properties) {
        int[] rgb = MCPatcherUtils.getImageRGB(TexturePackAPI.getImage(TexturePackAPI.newMCPatcherIdentifier(REDSTONE_COLORS)));
        if (rgb != null && rgb.length >= 16) {
            redstoneColor = new float[16][];
            for (int i = 0; i < 16; i++) {
                float[] f = new float[3];
                ColorUtils.intToFloat3(rgb[i], f);
                redstoneColor[i] = f;
            }
        }
    }

    private static void reloadStemColors(PropertiesFile properties) {

        Identifier stemColors = TexturePackAPI.newMCPatcherIdentifier(STEM_COLORS);
        Identifier pumpkinColors = TexturePackAPI.newMCPatcherIdentifier(PUMPKIN_STEM_COLORS);
        Identifier melonColors = TexturePackAPI.newMCPatcherIdentifier(MELON_STEM_COLORS);

        Identifier resource = TexturePackAPI.hasResource(pumpkinColors) ? pumpkinColors : stemColors;
        registerMetadataRGB("minecraft:pumpkin_stem", resource, "age", 8);
        resource = TexturePackAPI.hasResource(melonColors) ? melonColors : stemColors;
        registerMetadataRGB("minecraft:melon_stem", resource, "age", 8);
    }

    private static void registerMetadataRGB(String blockName, Identifier resource, String property, int length) {
        int[] rgb = MCPatcherUtils.getImageRGB(TexturePackAPI.getImage(resource));
        if (rgb == null || rgb.length < length) {
            return;
        }
        for (int i = 0; i < length; i++) {
            IColorMap colorMap = new ColorMap.Fixed(rgb[i] & 0xffffff);
            String idList = String.format("%s:%d,%d:%s=%d", blockName, i, (i + length) & 0xf, property, i);
            registerColorMap(colorMap, resource, idList);
        }
    }

    static List<BlockStateMatcher> findColorMaps(Block block) {
        return blockColorMaps.get(block);
    }

    static IColorMap getThreadLocal(BlockStateMatcher matcher) {
        IColorMap newMap = (IColorMap) matcher.getThreadData();
        if (newMap == null) {
            IColorMap oldMap = (IColorMap) matcher.getData();
            newMap = oldMap.copy();
            matcher.setThreadData(newMap);
        }
        return newMap;
    }

    private static IColorMap findColorMap(Block block, int metadata) {
        List<BlockStateMatcher> maps = findColorMaps(block);
        if (maps != null) {
            for (BlockStateMatcher matcher : maps) {
                if (matcher.match(block, metadata)) {
                    return getThreadLocal(matcher);
                }
            }
        }
        return null;
    }

    private static IColorMap findColorMap(Block block, BlockRenderView blockAccess, int i, int j, int k) {
        List<BlockStateMatcher> maps = findColorMaps(block);
        if (maps != null) {
            for (BlockStateMatcher matcher : maps) {
                if (matcher.match(blockAccess, i, j, k)) {
                    return getThreadLocal(matcher);
                }
            }
        }
        return null;
    }

    public static boolean colorizeBlock(Block block) {
        return colorizeBlock(block, 16);
    }

    public static boolean colorizeBlock(Block block, int metadata) {
        IColorMap colorMap = findColorMap(block, metadata);
        if (colorMap == null) {
            RenderBlocksUtils.setupColorMultiplier(block, metadata, false);
            return false;
        } else {
            RenderBlocksUtils.setupColorMultiplier(block, metadata, true);
            blockColor = colorMap.getColorMultiplier();
            return true;
        }
    }

    public static boolean colorizeBlock(Block block, BlockRenderView blockAccess, int i, int j, int k) {
        IColorMap colorMap = findColorMap(block, blockAccess, i, j, k);
        return colorizeBlock(block, blockAccess, colorMap, i, j, k);
    }

    private static boolean colorizeBlock(Block block, BlockRenderView blockAccess, IColorMap colorMap, int i, int j, int k) {
        if (colorMap == null) {
            return false;
        } else {
            blockColor = colorMap.getColorMultiplier(blockAccess, i, j, k);
            return true;
        }
    }

    public static void computeWaterColor() {
        if (waterColorMap != null) {
            Colorizer.setColorF(waterColorMap.getColorMultiplier());
        }
    }

    public static boolean computeWaterColor(boolean includeBaseColor, int i, int j, int k) {
        if (waterColorMap == null) {
            return false;
        } else {
            Colorizer.setColorF(waterColorMap.getColorMultiplierF(BiomeAPI.getWorld(), i, j, k));
            if (includeBaseColor) {
                Colorizer.setColor[0] *= ColorizeEntity.waterBaseColor[0];
                Colorizer.setColor[1] *= ColorizeEntity.waterBaseColor[1];
                Colorizer.setColor[2] *= ColorizeEntity.waterBaseColor[2];
            }
            return true;
        }
    }


    public static void colorizeWaterBlockGL(Block block) {
        if (block == waterBlock) {// || block == staticWaterBlock
            float[] waterColor;
            if (waterColorMap == null) {
                waterColor = ColorizeEntity.waterBaseColor;
            } else {
                waterColor = new float[3];
                ColorUtils.intToFloat3(waterColorMap.getColorMultiplier(), waterColor);
            }
            //GLAPI.glColor4f(waterColor[0], waterColor[1], waterColor[2], 1.0f); //TODO
        }
    }

    public static boolean computeRedstoneWireColor(int current) {
        if (redstoneColor == null) {
            return false;
        } else {
            System.arraycopy(redstoneColor[current & 0xf], 0, Colorizer.setColor, 0, 3);
            return true;
        }
    }

    public static int colorizeRedstoneWire(int current) {
        if (redstoneColor == null) {
            return -1;
        } else {
            return ColorUtils.float3ToInt(redstoneColor[current]);
        }
    }

    private static float[] getVertexColor(BlockRenderView blockAccess, IColorMap colorMap, int i, int j, int k, int[] offsets) {
        if (enableTestColorSmoothing) {
            int rgb = 0;
            if ((i + offsets[0]) % 2 == 0) {
                rgb |= 0xff0000;
            }
            if ((j + offsets[1]) % 2 == 0) {
                rgb |= 0x00ff00;
            }
            if ((k + offsets[2]) % 2 == 0) {
                rgb |= 0x0000ff;
            }
            ColorUtils.intToFloat3(rgb, Colorizer.setColor);
            return Colorizer.setColor;
        } else {
            return colorMap.getColorMultiplierF(blockAccess, i + offsets[0], j + offsets[1], k + offsets[2]);
        }
    }
/*
    public static boolean setupBlockSmoothing(RenderBlocks renderBlocks, Block block, BlockRenderView blockAccess,
                                              int i, int j, int k, int face,
                                              float topLeft, float bottomLeft, float bottomRight, float topRight) {
        return RenderBlocksUtils.useColorMultiplier(face) &&
            setupBiomeSmoothing(renderBlocks, block, blockAccess, i, j, k, face, true, topLeft, bottomLeft, bottomRight, topRight);
    }*/

    /*
    // TODO: remove
    @Deprecated
    public static boolean setupBlockSmoothingGrassSide(RenderBlocks renderBlocks, Block block, BlockRenderView blockAccess,
                                                       int i, int j, int k, int face,
                                                       float topLeft, float bottomLeft, float bottomRight, float topRight) {
        return checkBiomeSmoothing(block, face) &&
            setupBiomeSmoothing(renderBlocks, block, blockAccess, i, j, k, face, true, topLeft, bottomLeft, bottomRight, topRight);
    }


    public static boolean setupBlockSmoothing(RenderBlocks renderBlocks, Block block, BlockRenderView blockAccess,
                                              int i, int j, int k, int face) {
        return checkBiomeSmoothing(block, face) &&
            setupBiomeSmoothing(renderBlocks, block, blockAccess, i, j, k, face, true, 1.0f, 1.0f, 1.0f, 1.0f);
    }
*/
    private static boolean checkBiomeSmoothing(Block block, int face) {
        return enableSmoothBiomes && face >= 0 && RenderBlocksUtils.isAmbientOcclusionEnabled() && BlockAPI.getBlockLightValue(block) == 0;
    }
/*
    private static boolean setupBiomeSmoothing(RenderBlocks renderBlocks, Block block, BlockRenderView blockAccess,
                                               int i, int j, int k, int face,
                                               boolean useAO, float topLeft, float bottomLeft, float bottomRight, float topRight) {
        if (!setupBlockSmoothing(block, blockAccess, i, j, k, face)) {
            return false;
        }

        if (useAO) {
            float aoBase = RenderBlocksUtils.AO_BASE[face % 6];
            topLeft *= aoBase;
            bottomLeft *= aoBase;
            bottomRight *= aoBase;
            topRight *= aoBase;
        }

        renderBlocks.colorRedTopLeft = topLeft * colorRedTopLeft;
        renderBlocks.colorGreenTopLeft = topLeft * colorGreenTopLeft;
        renderBlocks.colorBlueTopLeft = topLeft * colorBlueTopLeft;

        renderBlocks.colorRedBottomLeft = bottomLeft * colorRedBottomLeft;
        renderBlocks.colorGreenBottomLeft = bottomLeft * colorGreenBottomLeft;
        renderBlocks.colorBlueBottomLeft = bottomLeft * colorBlueBottomLeft;

        renderBlocks.colorRedBottomRight = bottomRight * colorRedBottomRight;
        renderBlocks.colorGreenBottomRight = bottomRight * colorGreenBottomRight;
        renderBlocks.colorBlueBottomRight = bottomRight * colorBlueBottomRight;

        renderBlocks.colorRedTopRight = topRight * colorRedTopRight;
        renderBlocks.colorGreenTopRight = topRight * colorGreenTopRight;
        renderBlocks.colorBlueTopRight = topRight * colorBlueTopRight;

        return true;
    }
*/
    public static void setupBlockSmoothing(Block block, BlockRenderView blockAccess, int i, int j, int k, int face,
                                           float r, float g, float b) {
        if (!setupBlockSmoothing(block, blockAccess, i, j, k, face)) {
            setVertexColors(r, g, b);
        }
    }

    private static boolean setupBlockSmoothing(Block block, BlockRenderView blockAccess, int i, int j, int k, int face) {
        if (!checkBiomeSmoothing(block, face)) {
            return false;
        }
        IColorMap colorMap = findColorMap(block, blockAccess, i, j, k);
        if (colorMap == null) {
            return false;
        }

        int[][] offsets = FACE_VERTICES[face];
        float[] color;

        color = getVertexColor(blockAccess, colorMap, i, j, k, offsets[0]);
        colorRedTopLeft = color[0];
        colorGreenTopLeft = color[1];
        colorBlueTopLeft = color[2];

        color = getVertexColor(blockAccess, colorMap, i, j, k, offsets[1]);
        colorRedBottomLeft = color[0];
        colorGreenBottomLeft = color[1];
        colorBlueBottomLeft = color[2];

        color = getVertexColor(blockAccess, colorMap, i, j, k, offsets[2]);
        colorRedBottomRight = color[0];
        colorGreenBottomRight = color[1];
        colorBlueBottomRight = color[2];

        color = getVertexColor(blockAccess, colorMap, i, j, k, offsets[3]);
        colorRedTopRight = color[0];
        colorGreenTopRight = color[1];
        colorBlueTopRight = color[2];

        return true;
    }

    private static void resetVertexColors() {
        setVertexColors(1.0f, 1.0f, 1.0f);
    }

    private static void setVertexColors(float r, float g, float b) {
        colorRedTopLeft = colorRedBottomLeft = colorRedBottomRight = colorRedTopRight = r;
        colorGreenTopLeft = colorGreenBottomLeft = colorGreenBottomRight = colorGreenTopRight = g;
        colorBlueTopLeft = colorBlueBottomLeft = colorBlueBottomRight = colorBlueTopRight = b;
    }
}