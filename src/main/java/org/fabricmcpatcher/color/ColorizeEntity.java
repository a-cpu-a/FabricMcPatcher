package org.fabricmcpatcher.color;

import net.minecraft.client.particle.LavaEmberParticle;
import net.minecraft.recipe.ArmorDyeRecipe;
import net.minecraft.util.Identifier;
import org.fabricmcpatcher.color.biome.BiomeAPI;
import org.fabricmcpatcher.color.biome.ColorUtils;
import org.fabricmcpatcher.resource.PropertiesFile;
import org.fabricmcpatcher.resource.TexturePackAPI;
import org.fabricmcpatcher.utils.MCPatcherUtils;

import java.util.Arrays;
import java.util.Random;

public class ColorizeEntity {
    private static Identifier LAVA_DROP_COLORS;
    private static Identifier MYCELIUM_COLORS;
    private static Identifier XPORB_COLORS;

    public static float[] waterBaseColor; // particle.water
    private static float[] lavaDropColors; // misc/lavadropcolor.png

    public static float[] portalColor= new float[]{1.0f, 0.3f, 0.9f};// particle.portal

    public static int undyedLeatherColor=0xa06540; // armor.default

    private static final Random random = new Random();
    private static int[] myceliumColors;

    private static int[] xpOrbColors;
    public static int xpOrbRed;
    public static int xpOrbGreen;
    public static int xpOrbBlue;

    private static final String[] colorNames = new String[]{
        "white",
        "orange",
        "magenta",
        "lightBlue",
        "yellow",
        "lime",
        "pink",
        "gray",
        "silver",
        "cyan",
        "purple",
        "blue",
        "brown",
        "green",
        "red",
        "black",
    };
    private static final String[] colorNames2 = new String[]{
            null,
            null,
            null,
            "light_blue",
            null,
            null,
            null,
            null,
            "light_gray"
    };

    private static final Integer[] dyeColors = new Integer[colorNames.length]; // dye.*
    private static final Integer[] fleeceColors = new Integer[colorNames.length]; // sheep.*
    private static final Integer[] collarColors = new Integer[colorNames.length]; // collar.*
    private static final Integer[] armorColors = new Integer[colorNames.length]; // armor.*

    static {
        try {
            reset();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    static void reset() {
        waterBaseColor = new float[]{0.2f, 0.3f, 1.0f};
        portalColor = new float[]{1.0f, 0.3f, 0.9f};
        lavaDropColors = null;
        Arrays.fill(dyeColors, null);
        Arrays.fill(fleeceColors, null);
        Arrays.fill(collarColors, null);
        Arrays.fill(armorColors, null);
        undyedLeatherColor = 0xa06540;
        myceliumColors = null;
        xpOrbColors = null;
    }

    static void reloadParticleColors(PropertiesFile properties) {

        LAVA_DROP_COLORS = TexturePackAPI.newMCPatcherIdentifier("colormap/lavadrop.png");
        MYCELIUM_COLORS = TexturePackAPI.newMCPatcherIdentifier( "colormap/myceliumparticle.png");

        Colorizer.loadFloatColor("drop.water", waterBaseColor);
        Colorizer.loadFloatColor("particle.water", waterBaseColor);
        Colorizer.loadFloatColor("particle.portal", portalColor);
        int[] rgb = MCPatcherUtils.getImageRGB(TexturePackAPI.getImage(LAVA_DROP_COLORS));
        if (rgb != null) {
            lavaDropColors = new float[3 * rgb.length];
            for (int i = 0; i < rgb.length; i++) {
                ColorUtils.intToFloat3(rgb[i], lavaDropColors, 3 * i);
            }
        }
        myceliumColors = MCPatcherUtils.getImageRGB(TexturePackAPI.getImage(MYCELIUM_COLORS));
    }

    static void reloadDyeColors(PropertiesFile properties) {
        for (int i = 0; i < colorNames.length; i++) {
            dyeColors[i] = Colorizer.loadIntegerColor("dye." + Colorizer.getStringKey(colorNames, i));
            if(dyeColors[i]==null && i<colorNames2.length && colorNames2[i]!=null)
                dyeColors[i] = Colorizer.loadIntegerColor("dye." + Colorizer.getStringKey(colorNames2, i));
        }
        for (int i = 0; i < colorNames.length; i++) {
            String key = Colorizer.getStringKey(colorNames, i);
            String key2 = (i<colorNames2.length && colorNames2[i]!=null)?Colorizer.getStringKey(colorNames2, i):null;
            fleeceColors[i] = Colorizer.loadIntegerColor("sheep." + key);
            collarColors[i] = Colorizer.loadIntegerColor("collar." + key);
            armorColors[i] = Colorizer.loadIntegerColor("armor." + key);

            if(key2!=null) {
                if(fleeceColors[i]==null)
                    fleeceColors[i] = Colorizer.loadIntegerColor("sheep." + key2);
                if(collarColors[i]==null)
                    collarColors[i] = Colorizer.loadIntegerColor("collar." + key2);
                if(armorColors[i]==null)
                    armorColors[i] = Colorizer.loadIntegerColor("armor." + key2);
            }
        }
        undyedLeatherColor = Colorizer.loadIntColor("armor.default", undyedLeatherColor);
    }

    static void reloadXPOrbColors(PropertiesFile properties) {
        XPORB_COLORS = TexturePackAPI.newMCPatcherIdentifier("colormap/xporb.png");
        xpOrbColors = MCPatcherUtils.getImageRGB(TexturePackAPI.getImage(XPORB_COLORS));
    }

    public static int colorizeXPOrb(int origColor, float timer) {
        if (xpOrbColors == null || xpOrbColors.length == 0) {
            return origColor;
        } else {
            return xpOrbColors[(int) ((Math.sin(timer / 4.0) + 1.0) * (xpOrbColors.length - 1) / 2.0)];
        }
    }

    public static void colorizeXPOrb(int origRed, int origBlue, float timer) {
        int color = colorizeXPOrb((origRed << 16) | (255<<8) | origBlue, timer);
        xpOrbRed = (color >> 16) & 0xff;
        xpOrbGreen = (color >> 8) & 0xff;
        xpOrbBlue = color & 0xff;
    }

    public static boolean computeLavaDropColor(int age) {
        if (lavaDropColors == null) {
            return false;
        } else {
            int offset = 3 * Math.max(Math.min(lavaDropColors.length / 3 - 1, age - 20), 0);
            System.arraycopy(lavaDropColors, offset, Colorizer.setColor, 0, 3);
            return true;
        }
    }

    public static boolean computeMyceliumParticleColor() {
        if (myceliumColors == null) {
            return false;
        } else {
            Colorizer.setColorF(myceliumColors[random.nextInt(myceliumColors.length)]);
            return true;
        }
    }
/*
    public static int getPotionEffectColor(int defaultColor, LivingEntity entity) {
        return defaultColor == 0 ? defaultColor : entity.overridePotionColor;
    }*/

    public static void computeSuspendColor(int defaultColor, int i, int j, int k) {
        if (ColorizeWorld.underwaterColor != null) {
            defaultColor = ColorizeWorld.underwaterColor.getColorMultiplier(BiomeAPI.getWorld(), i, j, k);
        }
        Colorizer.setColorF(defaultColor);
    }

    public static int getDyeColor(int rgb, int index) {
        Integer newRGB = dyeColors[index];
        return newRGB == null ? rgb : (newRGB|0xFF000000);
    }

    public static Integer getFleeceColor(int index) {
        return getArrayColor(fleeceColors, index);
    }

    public static Integer getWolfCollarColor(int index) {
        return getArrayColor(collarColors, index);
    }

    public static Integer getArmorDyeColor(int index) {
        return getArrayColor(armorColors, index);
    }

    private static Integer getArrayColor(Integer[] array, int index) {
        Integer newRGB = array[index];
        return newRGB;
    }
}