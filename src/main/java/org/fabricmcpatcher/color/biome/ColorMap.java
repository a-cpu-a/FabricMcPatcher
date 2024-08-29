package org.fabricmcpatcher.color.biome;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import org.fabricmcpatcher.resource.PropertiesFile;
import org.fabricmcpatcher.resource.ResourceList;
import org.fabricmcpatcher.resource.TexturePackAPI;
import org.fabricmcpatcher.utils.*;

import java.awt.image.BufferedImage;
import java.util.*;

abstract public class ColorMap implements IColorMap {
    private static final MCLogger logger = MCLogger.getLogger(MCPatcherUtils.CUSTOM_COLORS);

    public static final boolean useSwampColors = Config.getBoolean(MCPatcherUtils.CUSTOM_COLORS, "swamp", true);

    private static final int FIXED = 0;
    private static final int TEMPERATURE_HUMIDITY = 1;
    private static final int BIOME_HEIGHT = 2;

    private static final int COLORMAP_WIDTH = 256;
    private static final int COLORMAP_HEIGHT = 256;

    //private static Object TexturePackAPI;
    public static final String BLOCK_COLORMAP_DIR = "colormap/blocks"; // in TexturePackAPI.MCPATCHER_SUBDIR
    public static final List<Identifier> unusedPNGs = new ArrayList<Identifier>();

    private static final String VANILLA_TYPE = "_vanillaType";
    private static final String ALT_SOURCE = "_altSource";

    private static int defaultColorMapFormat;
    private static boolean defaultFlipY;
    private static float defaultYVariance;

    protected final Identifier resource;
    protected final int[] map;
    protected final int width;
    protected final int height;
    protected final float maxX;
    protected final float maxY;

    private final float[] xy = new float[2];
    private final float[] lastColor = new float[3];

    public static IColorMap loadVanillaColorMap(Identifier vanillaImage, Identifier swampImage) {
        Properties properties = new Properties();
        properties.setProperty("format", "1");
        properties.setProperty("source", vanillaImage.toString());
        if (!TexturePackAPI.hasCustomResource(vanillaImage)) {
            if (vanillaImage.getPath().contains("grass")) {
                properties.setProperty(VANILLA_TYPE, "grass");
            } else if (vanillaImage.getPath().contains("foliage")) {
                properties.setProperty(VANILLA_TYPE, "foliage");
            }
        }
        if (swampImage != null) {
            properties.setProperty(ALT_SOURCE, swampImage.toString());
        }
        return loadColorMap(true, vanillaImage, properties);
    }

    public static IColorMap loadFixedColorMap(boolean useCustom, Identifier resource) {
        return loadColorMap(useCustom, resource, null);
    }

    public static IColorMap loadColorMap(boolean useCustom, Identifier resource, Properties properties) {
        IColorMap map = loadColorMap1(useCustom, resource, properties);
        if (map != null) {
            map.claimResources(unusedPNGs);
        }
        return map;
    }

    private static IColorMap loadColorMap1(boolean useCustom, Identifier resource, Properties properties) {
        if (!useCustom || resource == null) {
            return null;
        }

        Identifier propertiesResource;
        Identifier imageResource;
        if (resource.toString().endsWith(".png")) {
            propertiesResource = TexturePackAPI.transformIdentifier(resource, ".png", ".properties");
            imageResource = resource;
        } else if (resource.toString().endsWith(".properties")) {
            propertiesResource = resource;
            imageResource = TexturePackAPI.transformIdentifier(resource, ".properties", ".png");
        } else {
            return null;
        }
        if (properties == null) {
            properties = TexturePackAPI.getProperties(propertiesResource);
            if (properties == null) {
                properties = new Properties();
            }
        }

        int format = parseFormat(MCPatcherUtils.getStringProperty(properties, "format", ""));
        if (format == FIXED) {
            int color = MCPatcherUtils.getHexProperty(properties, "color", 0xffffff);
            return new Fixed(color);
        }

        String path = MCPatcherUtils.getStringProperty(properties, "source", "");
        if (!MCPatcherUtils.isNullOrEmpty(path)) {
            imageResource = TexturePackAPI.parseIdentifier(resource, path);
        }
        BufferedImage image = TexturePackAPI.getImage(imageResource);
        if (image == null) {
            return null;
        }

        switch (format) {
            case TEMPERATURE_HUMIDITY:
                String vanillaSource = MCPatcherUtils.getStringProperty(properties, VANILLA_TYPE, "");
                IColorMap defaultMap;
                if ("grass".equals(vanillaSource)) {
                    defaultMap = new Grass(image);
                } else if ("foliage".equals(vanillaSource)) {
                    defaultMap = new Foliage(image);
                } else {
                    defaultMap = new TempHumidity(imageResource, properties, image);
                }
                path = MCPatcherUtils.getStringProperty(properties, ALT_SOURCE, "");
                if (useSwampColors && !MCPatcherUtils.isNullOrEmpty(path)) {
                    Identifier swampResource = TexturePackAPI.parseIdentifier(resource, path);
                    image = TexturePackAPI.getImage(swampResource);
                    if (image != null) {
                        IColorMap swampMap = new TempHumidity(swampResource, properties, image);
                        return new Swamp(defaultMap, swampMap);
                    }
                }
                return defaultMap;

            case BIOME_HEIGHT:
                Grid grid = new Grid(imageResource, properties, image);
                if (grid.isInteger()) {
                    return new IntegerGrid(grid);
                } else {
                    return grid;
                }

            default:
                logger.error("%s: unknown format %d", resource, format);
                return null;
        }
    }

    public static void reset() {
        unusedPNGs.clear();
        defaultColorMapFormat = TEMPERATURE_HUMIDITY;
        defaultFlipY = false;
        defaultYVariance = Config.getInt(MCPatcherUtils.CUSTOM_COLORS, "yVariance", 0);
    }

    public static void reloadColorMapSettings(PropertiesFile properties) {
        unusedPNGs.addAll(ResourceList.getInstance().listMcPatcherResources(BLOCK_COLORMAP_DIR, ".png", false));
        defaultColorMapFormat = parseFormat(properties.getString("palette.format", ""));
        defaultFlipY = properties.getBoolean("palette.flipY", false);
        defaultYVariance = properties.getFloat("palette.yVariance", 0.0f);
    }

    private static int parseFormat(String value) {
        if (MCPatcherUtils.isNullOrEmpty(value)) {
            return defaultColorMapFormat;
        }
        value = value.toLowerCase();
        if (value.matches("^\\d+$")) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (value.equals("fixed")) {
            return FIXED;
        } else if (value.equals("temperature+humidity") || value.equals("t+h") || value.equals("vanilla")) {
            return TEMPERATURE_HUMIDITY;
        } else if (value.equals("biome+height") || value.equals("b+h") || value.equals("grid")) {
            return BIOME_HEIGHT;
        }
        return defaultColorMapFormat;
    }

    ColorMap(Identifier resource, Properties properties, BufferedImage image) {
        this(resource, MCPatcherUtils.getImageRGB(image), image.getWidth(), image.getHeight());
    }

    ColorMap(Identifier resource, int[] map, int width, int height) {
        this.resource = resource;
        this.map = map;
        this.width = width;
        this.height = height;
        for (int i = 0; i < map.length; i++) {
            map[i] &= 0xffffff;
        }
        maxX = width - 1.0f;
        maxY = height - 1.0f;
    }

    abstract protected void computeXY(ClientWorld biome, int i, int j, int k, float[] f, int seaLevel);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + resource + "}";
    }

    @Override
    public final int getColorMultiplier(ClientWorld blockAccess, int i, int j, int k) {
        computeXY(blockAccess, i, j, k, xy, blockAccess.getSeaLevel());
        return getRGB(xy[0], xy[1]);
    }

    @Override
    public final float[] getColorMultiplierF(ClientWorld blockAccess, int i, int j, int k) {
        int rgb = getColorMultiplier(blockAccess, i, j, k);
        ColorUtils.intToFloat3(rgb, lastColor);
        return lastColor;
    }

    @Override
    public void claimResources(Collection<Identifier> resources) {
        resources.remove(resource);
    }

    protected int getRGB(float x, float y) {
        x = clamp(x, 0.0f, maxX);
        y = clamp(y, 0.0f, maxY);

        int x0 = (int) x;
        int dx = (int) (256.0f * (x - (float) x0));
        int x1 = x0 + 1;

        int y0 = (int) y;
        int dy = (int) (256.0f * (y - (float) y0));
        int y1 = y0 + 1;

        if (dx == 0 && dy == 0) {
            return getRGB(x0, y0);
        } else if (dx == 0) {
            return interpolate(x0, y0, x0, y1, dy);
        } else if (dy == 0) {
            return interpolate(x0, y0, x1, y0, dx);
        } else {
            return interpolate(
                interpolate(x0, y0, x1, y0, dx),
                interpolate(x0, y1, x1, y1, dx),
                dy
            );
        }
    }

    private int getRGB(int x, int y) {
        return map[x + width * y];
    }

    private int interpolate(int x1, int y1, int x2, int y2, int a2) {
        return interpolate(getRGB(x1, y1), getRGB(x2, y2), a2);
    }

    private static int interpolate(int rgb1, int rgb2, int a2) {
        int a1 = 256 - a2;

        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >> 8) & 0xff;
        int b1 = rgb1 & 0xff;

        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = rgb2 & 0xff;

        int r = (a1 * r1 + a2 * r2) >> 8;
        int g = (a1 * g1 + a2 * g2) >> 8;
        int b = (a1 * b1 + a2 * b2) >> 8;

        return (r << 16) | (g << 8) | b;
    }

    protected static float noise0to1(int i, int j, int k, int l) {
        int hash = (int) WeightedIndex.hash128To64(i, j, k, l) & Integer.MAX_VALUE;
        return (float) ((double) hash / (double) Integer.MAX_VALUE);
    }

    protected static float noiseMinus1to1(int i, int j, int k, int l) {
        int hash = (int) WeightedIndex.hash128To64(i, j, k, l);
        return (float) ((double) hash / (double) Integer.MIN_VALUE);
    }

    protected static float clamp(float i, float min, float max) {
        if (i < min) {
            return min;
        } else if (i > max) {
            return max;
        } else {
            return i;
        }
    }

    protected static int clamp(int i, int min, int max) {
        if (i < min) {
            return min;
        } else if (i > max) {
            return max;
        } else {
            return i;
        }
    }

    public static final class Fixed implements IColorMap {
        private final int colorI;
        private final float[] colorF = new float[3];

        public Fixed(int color) {
            colorI = color;
            ColorUtils.intToFloat3(colorI, colorF);
        }

        @Override
        public String toString() {
            return String.format("Fixed{%06x}", colorI);
        }

        @Override
        public boolean isHeightDependent() {
            return false;
        }

        @Override
        public int getColorMultiplier() {
            return colorI;
        }

        @Override
        public int getColorMultiplier(ClientWorld blockAccess, int i, int j, int k) {
            return colorI;
        }

        @Override
        public float[] getColorMultiplierF(ClientWorld blockAccess, int i, int j, int k) {
            return colorF;
        }

        @Override
        public void claimResources(Collection<Identifier> resources) {
        }

        @Override
        public IColorMap copy() {
            return this;
        }
    }

    public static final class Water implements IColorMap {
        private final float[] lastColor = new float[3];

        @Override
        public String toString() {
            return String.format("Water{%06x}", getColorMultiplier());
        }

        @Override
        public boolean isHeightDependent() {
            return false;
        }

        @Override
        public int getColorMultiplier() {
            return BiomeAPI.getWaterColorMultiplier(BiomeAPI.findBiomeByName("Ocean"));
        }

        @Override
        public int getColorMultiplier(ClientWorld blockAccess, int i, int j, int k) {
            return BiomeAPI.getWaterColorMultiplier(BiomeAPI.getBiomeGenAt(blockAccess, i, j, k));
        }

        @Override
        public float[] getColorMultiplierF(ClientWorld blockAccess, int i, int j, int k) {
            ColorUtils.intToFloat3(getColorMultiplier(blockAccess, i, j, k), lastColor);
            return lastColor;
        }

        @Override
        public void claimResources(Collection<Identifier> resources) {
        }

        @Override
        public IColorMap copy() {
            return new Water();
        }
    }

    public abstract static class Vanilla implements IColorMap {
        protected final int defaultColor;
        protected final float[] lastColor = new float[3];

        Vanilla(BufferedImage image) {
            this(image.getRGB(127, 127));
        }

        Vanilla(int defaultColor) {
            this.defaultColor = defaultColor & 0xffffff;
        }

        @Override
        public String toString() {
            return String.format("%s{%06x}", getClass().getSimpleName(), defaultColor);
        }

        @Override
        final public boolean isHeightDependent() {
            return BiomeAPI.isColorHeightDependent;
        }

        @Override
        final public int getColorMultiplier() {
            return defaultColor;
        }

        @Override
        final public int getColorMultiplier(ClientWorld blockAccess, int i, int j, int k) {
            return getColorMultiplier(blockAccess,i,j,k);
        }

        @Override
        final public float[] getColorMultiplierF(ClientWorld blockAccess, int i, int j, int k) {
            ColorUtils.intToFloat3(getColorMultiplier(blockAccess, i, j, k), lastColor);
            return lastColor;
        }

        @Override
        final public void claimResources(Collection<Identifier> resources) {
        }

        abstract int getColorMultiplier(BlockRenderView biome, int i, int j, int k);
    }

    public static final class Grass extends Vanilla {
        Grass(BufferedImage image) {
            super(image);
        }

        Grass(int defaultColor) {
            super(defaultColor);
        }

        @Override
        public IColorMap copy() {
            return new Grass(defaultColor);
        }

        @Override
        int getColorMultiplier(BlockRenderView biome, int i, int j, int k) {
            return BiomeAPI.getGrassColor(biome, i, j, k);
        }
    }

    public static final class Foliage extends Vanilla {
        Foliage(BufferedImage image) {
            super(image);
        }

        Foliage(int defaultColor) {
            super(defaultColor);
        }

        @Override
        public IColorMap copy() {
            return new Foliage(defaultColor);
        }

        @Override
        int getColorMultiplier(BlockRenderView biome, int i, int j, int k) {
            return BiomeAPI.getFoliageColor(biome, i, j, k);
        }
    }

    public static final class Swamp implements IColorMap {
        private final IColorMap defaultMap;
        private final IColorMap swampMap;
        private final Biome swampBiome;

        Swamp(IColorMap defaultMap, IColorMap swampMap) {
            this.defaultMap = defaultMap;
            this.swampMap = swampMap;
            swampBiome = BiomeAPI.findBiomeByName("Swampland");
        }

        @Override
        public String toString() {
            return defaultMap.toString();
        }

        @Override
        public boolean isHeightDependent() {
            return defaultMap.isHeightDependent() || swampMap.isHeightDependent();
        }

        @Override
        public int getColorMultiplier() {
            return defaultMap.getColorMultiplier();
        }

        @Override
        public int getColorMultiplier(ClientWorld blockAccess, int i, int j, int k) {
            IColorMap map = BiomeAPI.getBiomeGenAt(blockAccess, i, j, k) == swampBiome ? swampMap : defaultMap;
            return map.getColorMultiplier(blockAccess, i, j, k);
        }

        @Override
        public float[] getColorMultiplierF(ClientWorld blockAccess, int i, int j, int k) {
            IColorMap map = BiomeAPI.getBiomeGenAt(blockAccess, i, j, k) == swampBiome ? swampMap : defaultMap;
            return map.getColorMultiplierF(blockAccess, i, j, k);
        }

        @Override
        public void claimResources(Collection<Identifier> resources) {
            defaultMap.claimResources(resources);
            swampMap.claimResources(resources);
        }

        @Override
        public IColorMap copy() {
            return new Swamp(defaultMap.copy(), swampMap.copy());
        }
    }

    public static final class TempHumidity extends ColorMap {
        private final int defaultColor;

        private TempHumidity(Identifier resource, Properties properties, BufferedImage image) {
            super(resource, properties, image);
            defaultColor = MCPatcherUtils.getHexProperty(properties, "color", getRGB(maxX * 0.5f, maxY * 0.5f));
        }

        private TempHumidity(Identifier resource, int[] map, int width, int height, int defaultColor) {
            super(resource, map, width, height);
            this.defaultColor = defaultColor;
        }

        @Override
        public boolean isHeightDependent() {
            return BiomeAPI.isColorHeightDependent;
        }

        @Override
        public int getColorMultiplier() {
            return defaultColor;
        }

        @Override
        public IColorMap copy() {
            return new TempHumidity(resource, map, width, height, defaultColor);
        }

        @Override
        protected void computeXY(ClientWorld biome, int i, int j, int k, float[] f, int seaLevel) {
            float temperature = ColorUtils.clamp(BiomeAPI.getTemperature(biome, i, j, k,seaLevel));
            float rainfall = ColorUtils.clamp(BiomeAPI.getRainfall(biome, i, j, k));
            f[0] = maxX * (1.0f - temperature);
            f[1] = maxY * (1.0f - temperature * rainfall);
        }
    }

    public static final class Grid extends ColorMap {
        /** Maps biome id to X coord on image, not normalized */
        private final float[] biomeX = new float[4096];//4k biomes, maybe some mod used more...
        private final float yVariance;
        private final float yOffset;
        private final int defaultColor;

        private Grid(Identifier resource, Properties properties, BufferedImage image) {
            super(resource, properties, image);

            if (MCPatcherUtils.getBooleanProperty(properties, "flipY", defaultFlipY)) {
                int[] temp = new int[width];
                for (int i = 0; i < map.length / 2; i += width) {
                    int j = map.length - width - i;
                    System.arraycopy(map, i, temp, 0, width);
                    System.arraycopy(map, j, map, i, width);
                    System.arraycopy(temp, 0, map, j, width);
                }
            }

            yVariance = Math.max(MCPatcherUtils.getFloatProperty(properties, "yVariance", defaultYVariance), 0.0f);
            yOffset = MCPatcherUtils.getFloatProperty(properties, "yOffset", 0.0f);
            for (int i = 0; i < biomeX.length; i++) {
                biomeX[i] = i % width;
            }
            //undocumented, even optifine doesnt know about this
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (key.endsWith(".x") && !MCPatcherUtils.isNullOrEmpty(value)) {
                    key = key.substring(0, key.length() - 2);
                    int id = PortUtils.getBiomeId(key);
                    if (id!=-1) {
                        try {
                            biomeX[id] = Float.parseFloat(value);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            defaultColor = MCPatcherUtils.getHexProperty(properties, "color", getRGB(biomeX[1], getY(ColorMapBase.DEFAULT_HEIGHT)));
        }

        private Grid(Identifier resource, int[] map, int width, int height, float[] biomeX, float yVariance, float yOffset, int defaultColor) {
            super(resource, map, width, height);
            System.arraycopy(biomeX, 0, this.biomeX, 0, biomeX.length);
            this.yVariance = yVariance;
            this.yOffset = yOffset;
            this.defaultColor = defaultColor;
        }

        boolean isInteger() {
            if (yVariance != 0.0f || Math.floor(yOffset) != yOffset) {
                return false;
            }
            for (int i = 0; i < biomeX.length; i++) {
                if (biomeX[i] != i % width) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean isHeightDependent() {
            return true;
        }

        @Override
        public int getColorMultiplier() {
            return defaultColor;
        }

        @Override
        public IColorMap copy() {
            return new Grid(resource, map, width, height, biomeX, yVariance, yOffset, defaultColor);
        }

        @Override
        protected void computeXY(ClientWorld biome, int i, int j, int k, float[] f, int seaLevel) {
            f[0] = getX(BiomeAPI.getBiomeRegGenAt(biome.getBiomeAccess(), i, j, k), i, j, k);
            f[1] = getY(BiomeAPI.getBiomeRegGenAt(biome.getBiomeAccess(), i, j, k), i, j, k);
        }

        private float getX(Identifier biome, int i, int j, int k) {
            return biomeX[PortUtils.getBiomeId(biome)];
        }

        private float getY(int j) {
            return (float) j - yOffset;
        }

        private float getY(Identifier biome, int i, int j, int k) {
            float y = getY(j);
            if (yVariance != 0.0f) {
                y += yVariance * noiseMinus1to1(k, -j, i, ~PortUtils.getBiomeId(biome));//biome.biomeID
            }
            return y;
        }
    }

    public static final class IntegerGrid implements IColorMap {
        private final Identifier resource;
        private final int[] map;
        private final int width;
        private final int maxHeight;
        private final int yOffset;
        private final int defaultColor;
        private final float[] lastColor = new float[3];

        IntegerGrid(Grid grid) {
            this(grid.resource, grid.map, grid.width, grid.height - 1, (int) grid.yOffset, grid.defaultColor);
        }

        IntegerGrid(Identifier resource, int[] map, int width, int maxHeight, int yOffset, int defaultColor) {
            this.resource = resource;
            this.map = map;
            this.width = width;
            this.maxHeight = maxHeight;
            this.yOffset = yOffset;
            this.defaultColor = defaultColor;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" + resource + "}";
        }

        @Override
        public boolean isHeightDependent() {
            return true;
        }

        @Override
        public int getColorMultiplier() {
            return defaultColor;
        }

        @Override
        public int getColorMultiplier(ClientWorld blockAccess, int i, int j, int k) {
            return getRGB(BiomeAPI.getBiomeIDAt(blockAccess.getBiomeAccess(), i, j, k), j);
        }

        @Override
        public float[] getColorMultiplierF(ClientWorld blockAccess, int i, int j, int k) {
            int rgb = getColorMultiplier(blockAccess, i, j, k);
            ColorUtils.intToFloat3(rgb, lastColor);
            return lastColor;
        }

        @Override
        public void claimResources(Collection<Identifier> resources) {
            resources.remove(resource);
        }

        @Override
        public IColorMap copy() {
            return new IntegerGrid(resource, map, width, maxHeight, yOffset, defaultColor);
        }

        private int getRGB(int x, int y) {
            x = clamp(x, 0, COLORMAP_WIDTH - 1) % width;
            y = clamp(y - yOffset, 0, maxHeight);
            return map[y * width + x];
        }
    }
}