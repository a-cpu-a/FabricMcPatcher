package org.fabricmcpatcher.color;


import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.fabricmcpatcher.color.biome.*;
import org.fabricmcpatcher.accessors.IMutableColor;
import org.fabricmcpatcher.resource.PropertiesFile;
import org.fabricmcpatcher.resource.TexturePackAPI;
import org.fabricmcpatcher.utils.Config;
import org.fabricmcpatcher.utils.MCPatcherUtils;
import org.fabricmcpatcher.utils.PortUtils;

import java.util.HashMap;
import java.util.Map;

import static org.fabricmcpatcher.color.Colorizer.*;

public class ColorizeWorld {
    private static final int fogBlendRadius = Config.getInt(MCPatcherUtils.CUSTOM_COLORS, "fogBlendRadius", 7);

    private static Identifier UNDERWATERCOLOR = null;
    private static Identifier UNDERLAVACOLOR = null;
    private static Identifier FOGCOLOR0 = null;
    private static Identifier SKYCOLOR0 = null;

    private static final String TEXT_KEY = "text.";
    private static final String TEXT_CODE_KEY = TEXT_KEY + "code.";

    private static final int CLOUDS_DEFAULT = -1;
    private static final int CLOUDS_NONE = 0;
    private static final int CLOUDS_FAST = 1;
    private static final int CLOUDS_FANCY = 2;
    private static int cloudType = CLOUDS_DEFAULT;

    private static Entity fogCamera;

    private static final Map<Integer, Integer> textColorMap = new HashMap<Integer, Integer>(); // text.*
    private static final int[] textCodeColors = new int[32]; // text.code.*
    private static final boolean[] textCodeColorSet = new boolean[32];
    private static int signTextColor; // text.sign
    private static int bossBarTextColor=-1; // text.sign

    static IColorMap underwaterColor;
    private static IColorMap underlavaColor;
    private static IColorMap fogColorMap;
    private static IColorMap skyColorMap;

    public static float[] netherFogColor;
    public static float[] endFogColor;
    public static int endSkyColor;

    static {
        try {
            reset();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    static void reset() {
        underwaterColor = null;
        underlavaColor = null;
        fogColorMap = null;
        skyColorMap = null;

        netherFogColor = new float[]{0.2f, 0.03f, 0.03f};
        endFogColor = new float[]{0.075f, 0.075f, 0.094f};
        endSkyColor = 0x181818;

        cloudType = CLOUDS_DEFAULT;

        textColorMap.clear();
        for (int i = 0; i < textCodeColorSet.length; i++) {
            textCodeColorSet[i] = false;
        }
        for (Formatting formatting : Formatting.values()) {
            if(!formatting.isColor())continue;

            ((IMutableColor)(Object)TextColor.fromFormatting(formatting)).mcpatcher$setRgb(formatting.getColorValue());
        }
        signTextColor = 0;
    }

    static void reloadFogColors(PropertiesFile properties) {

        UNDERWATERCOLOR = TexturePackAPI.newMCPatcherIdentifier("/misc/underwatercolor.png", "colormap/underwater.png");
        UNDERLAVACOLOR = TexturePackAPI.newMCPatcherIdentifier("/misc/underlavacolor.png", "colormap/underlava.png");
        FOGCOLOR0 = TexturePackAPI.newMCPatcherIdentifier("/misc/fogcolor0.png", "colormap/fog0.png");
        SKYCOLOR0 = TexturePackAPI.newMCPatcherIdentifier("/misc/skycolor0.png", "colormap/sky0.png");

        underwaterColor = wrapFogMap(ColorMap.loadFixedColorMap(Colorizer.useFogColors, UNDERWATERCOLOR));
        underlavaColor = wrapFogMap(ColorMap.loadFixedColorMap(Colorizer.useFogColors, UNDERLAVACOLOR));
        fogColorMap = wrapFogMap(ColorMap.loadFixedColorMap(Colorizer.useFogColors, FOGCOLOR0));
        skyColorMap = wrapFogMap(ColorMap.loadFixedColorMap(Colorizer.useFogColors, SKYCOLOR0));

        loadFloatColor("fog.nether", netherFogColor);
        loadFloatColor("fog.end", endFogColor);
        endSkyColor = loadIntColor("sky.end", endSkyColor);
    }

    static IColorMap wrapFogMap(IColorMap map) {
        if (map == null) {
            return null;
        } else {
            if (fogBlendRadius > 0) {
                map = new ColorMapBase.Blended(map, fogBlendRadius);
            }
            map = new ColorMapBase.Cached(map);
            map = new ColorMapBase.Smoothed(map, 3000.0f);
            map = new ColorMapBase.Outer(map);
            return map;
        }
    }

    static void reloadCloudType(PropertiesFile properties) {
        String value = properties.getString("clouds", "").toLowerCase();
        if (value.equals("fast")) {
            cloudType = CLOUDS_FAST;
        } else if (value.equals("fancy")) {
            cloudType = CLOUDS_FANCY;
        } else if (value.equals("none")) {
            cloudType = CLOUDS_NONE;
        }
    }

    static void reloadTextColors(PropertiesFile properties) {
        for (int i = 0; i < textCodeColors.length; i++) {
            textCodeColorSet[i] = loadIntColor(TEXT_CODE_KEY + i, textCodeColors, i);
            if (textCodeColorSet[i] && i + 16 < textCodeColors.length) {//shadow colors
                textCodeColors[i + 16] = (textCodeColors[i] & 0xfcfcfc) >> 2;
                textCodeColorSet[i + 16] = true;
            }
        }
        for (Formatting formatting : Formatting.values()) {
            if(!formatting.isColor())continue;
            ((IMutableColor)(Object)TextColor.fromFormatting(formatting)).mcpatcher$setRgb(formatting.getColorValue());
        }
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!key.startsWith(TEXT_KEY) || key.startsWith(TEXT_CODE_KEY)) {
                continue;
            }
            key = key.substring(TEXT_KEY.length()).trim();
            try {
                int newColor;
                int oldColor;
                if (key.equals("xpbar")) {
                    oldColor = 0x80ff20;
                } else {
                    oldColor = Integer.parseInt(key, 16);
                }
                newColor = Integer.parseInt(value, 16);
                textColorMap.put(oldColor, newColor);
            } catch (NumberFormatException ignored) {
            }
        }
        signTextColor = loadIntColor("text.sign", 0);
        bossBarTextColor = loadIntColor("text.boss",  0xffffff);//was once 0xff00ff
    }

    public static void setupForFog(Entity entity) {
        fogCamera = entity;
    }

    private static boolean computeFogColor(ClientWorld blockAccess, IColorMap colorMap) {
        if (colorMap == null || fogCamera == null) {
            return false;
        } else {
            int i = (int) fogCamera.getX();
            int j = (int) fogCamera.getY();
            int k = (int) fogCamera.getZ();
            Colorizer.setColorF(colorMap.getColorMultiplierF(blockAccess, i, j, k));
            return true;
        }
    }

    public static boolean computeFogColor(ClientWorld worldProvider, float f) {
        return PortUtils.getWorldId(worldProvider) == 0 && computeFogColor(worldProvider, fogColorMap);
    }

    public static boolean computeSkyColor(ClientWorld world, float f) {
        if (PortUtils.getWorldId(world) == 0 && computeFogColor(world, skyColorMap)) {
            computeLightningFlash(world, f);
            return true;
        } else {
            return false;
        }
    }

    public static boolean computeUnderwaterColor() {
        return computeFogColor(BiomeAPI.getWorld(), underwaterColor);
    }

    public static boolean computeUnderlavaColor() {
        return computeFogColor(BiomeAPI.getWorld(), underlavaColor);
    }

    private static void computeLightningFlash(ClientWorld world, float f) {
        if (world.getLightningTicksLeft() > 0) {
            f = 0.45f * ColorUtils.clamp(world.getLightningTicksLeft() - f);
            setColor[0] = setColor[0] * (1.0f - f) + 0.8f * f;
            setColor[1] = setColor[1] * (1.0f - f) + 0.8f * f;
            setColor[2] = setColor[2] * (1.0f - f) + 0.8f * f;
        }
    }

    public static boolean drawFancyClouds(boolean fancyGraphics) {
        switch (cloudType) {
            case CLOUDS_NONE:
            case CLOUDS_FAST:
                return false;

            case CLOUDS_FANCY:
                return true;

            default:
                return fancyGraphics;
        }
    }

    public static CloudRenderMode drawFancyClouds(CloudRenderMode fancyGraphics) {
        switch (cloudType) {
            case CLOUDS_NONE:
                return CloudRenderMode.OFF;
            case CLOUDS_FAST:
                return CloudRenderMode.FAST;
            case CLOUDS_FANCY:
                return CloudRenderMode.FANCY;

            default:
                return fancyGraphics;
        }
    }

    public static Integer colorizeText(int defaultColor) {
        int high = defaultColor & 0xff000000;
        defaultColor &= 0xffffff;
        Integer newColor = textColorMap.get(defaultColor);
        if (newColor == null) {
            return null;
        } else {
            return high | newColor;
        }
    }

    public static int colorizeText(int defaultColor, int index) {
        if (index < 0 || index >= textCodeColors.length || !textCodeColorSet[index]) {
            return defaultColor;
        } else {
            return (defaultColor & 0xff000000) | textCodeColors[index];
        }
    }
    public static boolean shouldColorizeText(int index) {
        return index >= 0 && index < textCodeColors.length && textCodeColorSet[index];
    }

    public static int colorizeSignText() {
        return signTextColor;
    }
    public static int colorizeBossBarText() {
        return bossBarTextColor;
    }
}