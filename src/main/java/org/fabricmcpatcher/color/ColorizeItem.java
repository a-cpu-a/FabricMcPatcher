package org.fabricmcpatcher.color;


import com.google.common.collect.ImmutableMap;
import net.minecraft.block.MapColor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import org.fabricmcpatcher.accessors.IOriginalColor;
import org.fabricmcpatcher.accessors.IOverrideColor;
import org.fabricmcpatcher.resource.PropertiesFile;
import org.fabricmcpatcher.utils.MCLogger;
import org.fabricmcpatcher.utils.MCPatcherUtils;
import org.fabricmcpatcher.utils.id.PotionIdUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fabricmcpatcher.utils.id.PotionIdUtils.effects;

public class ColorizeItem {
    private static final MCLogger logger = MCLogger.getLogger(MCPatcherUtils.CUSTOM_COLORS);

    private static final Map<EntityType<?>, List<String>> entityNamesByID = new HashMap<>();
    private static final Map<EntityType<?>, Integer> spawnerEggShellColors = new HashMap<>(); // egg.shell.*
    private static final Map<EntityType<?>, Integer> spawnerEggSpotColors = new HashMap<>(); // egg.spots.*

    private static int waterBottleColor; // potion.water
    private static final List<StatusEffect> potions = new ArrayList<>(); // potion.*

    private static boolean potionsInitialized;

    private static final String[] MAP_MATERIALS = new String[]{
            "air",
            "grass",
            "sand",
            "cloth",
            "tnt",
            "ice",
            "iron",
            "foliage",
            "snow",
            "clay",
            "dirt",
            "stone",
            "water",
            "wood",
            "quartz",
            "adobe",
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
            "gold",
            "diamond",
            "lapis",
            "emerald",
            "obsidian",//Removed! replaced with spruce stuff
            "netherrack",
            //from optifine:
            "white_terracotta",
            "orange_terracotta",
            "magenta_terracotta",
            "light_blue_terracotta",
            "yellow_terracotta",
            "lime_terracotta",
            "pink_terracotta",
            "gray_terracotta",
            "light_gray_terracotta",
            "cyan_terracotta",
            "purple_terracotta",
            "blue_terracotta",
            "brown_terracotta",
            "green_terracotta",
            "red_terracotta",
            "black_terracotta",
            "crimson_nylium",
            "crimson_stem",
            "crimson_hyphae",
            "warped_nylium",
            "warped_stem",
            "warped_hyphae",
            "warped_wart_block",
            "deepslate",
            "raw_iron",
            "glow_lichen"
    };


    private static final ImmutableMap<String, String> ALT_MAP_MATERIALS = ImmutableMap.<String,String>builder()
            .put("obsidian","podzol")
            .put("silver","light_gray")
            .put("lightBlue","light_blue")
            .put("adobe","orange")
            .put("snow","white")
            .build();

    static {
        try {
            reset();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        for (StatusEffect e:effects) {
            setupPotion(e);
        }
    }

    static void reset() {
        spawnerEggShellColors.clear();
        spawnerEggSpotColors.clear();

        // 1.5+btw: Calling PotionHelper on startup runs the static initializer which crashes because Potion class
        // hasn't finished initializing yet.
        if (potionsInitialized) {
/*
            if (PotionHelper.getPotionColorCache() != null) {
                PotionHelper.getPotionColorCache().clear();
            }*/
        }
        potionsInitialized = true;

        waterBottleColor = -13083194;
        /*for (StatusEffect potion : potions) {
            potion.color = potion.originalColor;
        }*/

        for (int i = 0; i < MapColor.COLORS.length;i++) {
            try {

                MapColor mapColor = MapColor.get(i);
                if(mapColor==null)
                    continue;
                mapColor.color = ((IOriginalColor)mapColor).mcPatcher$GetOriginalColor();
            }
            catch (IndexOutOfBoundsException ignored) {break;}
        }
    }

    static void reloadPotionColors(PropertiesFile properties) {
        for (StatusEffect potion : potions) {
            ((IOverrideColor)potion).mcPatcher$overrideColor(null);

            PotionIdUtils.PotionInfo info = PotionIdUtils.getInfo(potion);
            Colorizer.loadIntColor("potion."+info.name, potion);
            if(info.newName!=null)
                Colorizer.loadIntColor("potion."+info.newName, potion);
        }
        int[] temp = new int[]{waterBottleColor};
        Colorizer.loadIntColor("potion.water", temp, 0);
        waterBottleColor = temp[0];//|0xFF000000;//fix alpha
    }

    static void reloadMapColors(PropertiesFile properties) {
        int[] rgb = new int[]{-1};
        for (int i = 0; i < MapColor.COLORS.length;i++) {
            try {
                MapColor mapColor = MapColor.get(i);
                if(mapColor==null)
                    continue;

                String name = Colorizer.getStringKey(MAP_MATERIALS,i);

                rgb[0] = ((IOriginalColor)mapColor).mcPatcher$GetOriginalColor();//reset default color

                if(ALT_MAP_MATERIALS.containsKey(name)) {
                    //if there is a alt name, then try to load a color
                    Colorizer.loadIntColor("map." + ALT_MAP_MATERIALS.get(name), rgb, 0);
                }

                Colorizer.loadIntColor("map." + name, rgb, 0);
                mapColor.color = rgb[0];
            }
            catch (IndexOutOfBoundsException ignored) {break;}
        }
    }

    public static void setupSpawnerEgg(List<String> entityNames, EntityType<?> entityID, int defaultShellColor, int defaultSpotColor) {
        logger.config("egg.shell.%s=???", entityNames.getFirst());
        logger.config("egg.spots.%s=???", entityNames.getFirst());
        entityNamesByID.put(entityID, entityNames);
    }

    public static void setupPotion(StatusEffect potion) {
        ((IOverrideColor)potion).mcPatcher$overrideColor(null);
        potions.add(potion);
    }

    public static int colorizeSpawnerEgg(int defaultColor, EntityType<?> entityID, int spots) {
        if (!Colorizer.useEggColors) {
            return defaultColor;
        }
        Integer value = null;
        Map<EntityType<?>, Integer> eggMap = (spots == 0 ? spawnerEggShellColors : spawnerEggSpotColors);
        if (eggMap.containsKey(entityID)) {
            value = eggMap.get(entityID);
        } else if (entityNamesByID.containsKey(entityID)) {
            List<String> names = entityNamesByID.get(entityID);
            if (names != null) {
                int[] tmp = new int[]{defaultColor};
                for (String name : names) {
                    Colorizer.loadIntColor((spots == 0 ? "egg.shell." : "egg.spots.") + name, tmp, 0);
                }
                eggMap.put(entityID, tmp[0]);
                value = tmp[0];
            }
        }
        return value == null ? defaultColor : value;
    }

    public static int getWaterBottleColor() {
        return waterBottleColor;
    }
}