package org.fabricmcpatcher.color;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import org.fabricmcpatcher.color.biome.ColorUtils;
import org.fabricmcpatcher.resource.TexturePackAPI;
import org.fabricmcpatcher.utils.Config;
import org.fabricmcpatcher.utils.MCLogger;
import org.fabricmcpatcher.utils.MCPatcherUtils;
import org.fabricmcpatcher.utils.PortUtils;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public final class Lightmap {

    public static final ShaderProgramKey CUSTOM_LIGHTMAP = new ShaderProgramKey(Identifier.of("mcpatcher:core/custom_lightmap"), VertexFormats.BLIT_SCREEN,Defines.EMPTY);

    static {
        ShaderProgramKeys.getAll().add(CUSTOM_LIGHTMAP);
    }

    private static final MCLogger logger = MCLogger.getLogger(MCPatcherUtils.CUSTOM_COLORS);

    private static final String LIGHTMAP_FORMAT1 = "/environment/lightmap%d.png";
    private static final String LIGHTMAP_FORMAT2 = "lightmap/world%d.png";
    private static final int LIGHTMAP_SIZE = 16;
    private static final int HEIGHT_WITHOUT_NIGHTVISION = 2 * LIGHTMAP_SIZE;
    private static final int HEIGHT_WITH_NIGHTVISION = 4 * LIGHTMAP_SIZE;

    private static final boolean useLightmaps = Config.getBoolean(MCPatcherUtils.CUSTOM_COLORS, "lightmaps", true);

    private static final HashMap<Integer, Lightmap> lightmaps = new HashMap<Integer, Lightmap>();

    //private final int width;
    private final boolean customNightvision;
    //private final int[] origMap;
    private final boolean valid;
    private final Identifier tex;
    /*
    private final float[] sunrgb = new float[3 * LIGHTMAP_SIZE];
    private final float[] torchrgb = new float[3 * LIGHTMAP_SIZE];
    private final float[] sunrgbnv = new float[3 * LIGHTMAP_SIZE];
    private final float[] torchrgbnv = new float[3 * LIGHTMAP_SIZE];
    private final float[] rgb = new float[3];
    */
    static void reset() {
        lightmaps.clear();
    }

    public static float sun;
    public static float torch;
    public static float nightVisionStrength;
    public static float gamma;
    public static int usesCustomNightvision;
    public static float heightR;
    public static Identifier origMap;

    public static boolean computeLightmap(GameRenderer renderer, ClientWorld world, float partialTick) {//, int[] mapRGB
        if (world == null || !useLightmaps) {
            return false;
        }
        Lightmap lightmap = null;
        int worldType = PortUtils.getWorldId(world);
        if (lightmaps.containsKey(worldType)) {
            lightmap = lightmaps.get(worldType);
        } else {
            Identifier resource = TexturePackAPI.newMCPatcherIdentifier(
                String.format(LIGHTMAP_FORMAT1, worldType),
                String.format(LIGHTMAP_FORMAT2, worldType)
            );
            BufferedImage image = TexturePackAPI.getImage(resource);
            if (image != null) {
                lightmap = new Lightmap(resource, image);
                if (!lightmap.valid) {
                    lightmap = null;
                }
            }
            lightmaps.put(worldType, lightmap);
        }
        return lightmap != null && lightmap.compute(renderer, world, partialTick);//, mapRGB
    }

    private Lightmap(Identifier resource, BufferedImage image) {
        //width = image.getWidth();
        tex=resource;
        int height = image.getHeight();
        customNightvision = (height == HEIGHT_WITH_NIGHTVISION);
        //origMap = new int[width * height];
        //image.getRGB(0, 0, width, height, origMap, 0, width);
        valid = (height == HEIGHT_WITHOUT_NIGHTVISION || height == HEIGHT_WITH_NIGHTVISION);
        if (!valid) {
            logger.error("%s must be exactly %d or %d pixels high", resource, HEIGHT_WITHOUT_NIGHTVISION, HEIGHT_WITH_NIGHTVISION);
        }
    }

    private boolean compute(GameRenderer renderer, ClientWorld world, float partialTick) {//int[] mapRGB,
        if (MinecraftClient.getInstance().player == null)return false;

        sun = ColorUtils.clamp(world.getLightningTicksLeft() > 0 ? 1.0f : 7.0f / 6.0f * (world.getSkyAngle(partialTick) - 0.2f));// * (width - 1);
        torch = ColorUtils.clamp(renderer.getLightmapTextureManager().flickerIntensity + 0.5f);// * (width - 1);

        if (MinecraftClient.getInstance().player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            nightVisionStrength = GameRenderer.getNightVisionStrength(MinecraftClient.getInstance().player, partialTick);
        }
        else
            nightVisionStrength=0.0f;

        gamma = (float) ColorUtils.clamp(MinecraftClient.getInstance().options.getGamma().getValue());

        usesCustomNightvision = customNightvision?1:0;

        heightR = customNightvision?0.25f:0.5f;

        origMap=tex;

        /*
        for (int i = 0; i < LIGHTMAP_SIZE; i++) {
            interpolate(origMap, i * width, sun, sunrgb, 3 * i);
            interpolate(origMap, (i + LIGHTMAP_SIZE) * width, torch, torchrgb, 3 * i);
            if (customNightvision && nightVisionStrength > 0.0f) {
                interpolate(origMap, (i + 2 * LIGHTMAP_SIZE) * width, sun, sunrgbnv, 3 * i);
                interpolate(origMap, (i + 3 * LIGHTMAP_SIZE) * width, torch, torchrgbnv, 3 * i);
            }
        }
        for (int s = 0; s < LIGHTMAP_SIZE; s++) {
            for (int t = 0; t < LIGHTMAP_SIZE; t++) {
                for (int k = 0; k < 3; k++) {
                    rgb[k] = ColorUtils.clamp(sunrgb[3 * s + k] + torchrgb[3 * t + k]);
                }
                if (nightVisionStrength > 0.0f) {
                    if (customNightvision) {
                        for (int k = 0; k < 3; k++) {
                            rgb[k] = ColorUtils.clamp((1.0f - nightVisionStrength) * rgb[k] + nightVisionStrength * (sunrgbnv[3 * s + k] + torchrgbnv[3 * t + k]));
                        }
                    } else {
                        float nightVisionMultiplier = Math.max(Math.max(rgb[0], rgb[1]), rgb[2]);
                        if (nightVisionMultiplier > 0.0f) {
                            nightVisionMultiplier = (1.0f - nightVisionStrength) + nightVisionStrength / nightVisionMultiplier;
                            for (int k = 0; k < 3; k++) {
                                rgb[k] = ColorUtils.clamp(rgb[k] * nightVisionMultiplier);
                            }
                        }
                    }
                }
                if (gamma != 0.0f) {
                    for (int k = 0; k < 3; k++) {
                        float tmp = 1.0f - rgb[k];
                        tmp = 1.0f - tmp * tmp * tmp * tmp;
                        rgb[k] = gamma * tmp + (1.0f - gamma) * rgb[k];
                    }
                }
                mapRGB[s * LIGHTMAP_SIZE + t] = 0xff000000 | ColorUtils.float3ToInt(rgb);
            }
        }*/
        return true;
    }

    //interpolate, but ONLY horizontally
    private static void interpolate(int[] map, int offset1, float x, float[] rgb, int offset2) {
        int x0 = (int) Math.floor(x);
        int x1 = (int) Math.ceil(x);
        if (x0 == x1) {
            ColorUtils.intToFloat3(map[offset1 + x0], rgb, offset2);
        } else {
            float xf = x - x0;//same as fract
            float xg = 1.0f - xf;//antifract
            float[] rgb0 = new float[3];
            float[] rgb1 = new float[3];
            ColorUtils.intToFloat3(map[offset1 + x0], rgb0);
            ColorUtils.intToFloat3(map[offset1 + x1], rgb1);
            for (int i = 0; i < 3; i++) {//loop over R,G,B
                rgb[offset2 + i] = xg * rgb0[i] + xf * rgb1[i];
            }
        }
    }
}