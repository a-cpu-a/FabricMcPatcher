package org.fabricmcpatcher.color.biome;


import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import org.fabricmcpatcher.resource.PropertiesFile;
import org.fabricmcpatcher.utils.MCLogger;
import org.fabricmcpatcher.utils.MCPatcherUtils;
import org.fabricmcpatcher.utils.PortUtils;

import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.Set;

public class BiomeAPI {
    private static final MCLogger logger = MCLogger.getLogger(MCPatcherUtils.CUSTOM_COLORS);
    private static final BiomeAPI instance = new BiomeAPI();

    public static final int WORLD_MAX_HEIGHT = 255;
    public static final boolean isColorHeightDependent = instance.isColorHeightDependent();

    private static boolean biomesLogged;

    private static RegistryEntry<Biome> lastBiome;
    private static int lastI;
    private static int lastK;

    /*
    public static void parseBiomeList(String list, BitSet bits) {
        logBiomes();
        if (MCPatcherUtils.isNullOrEmpty(list)) {
            return;
        }
        for (String s : list.split(list.contains(",") ? "\\s*,\\s*" : "\\s+")) {
            Biome biome = findBiomeByName(s);
            if (biome != null) {
                bits.set(biome.biomeID);
            }
        }
    }*/

    public static BitSet getHeightListProperty(PropertiesFile properties, String suffix) {
        int minHeight = Math.max(properties.getInt("minHeight" + suffix, 0), 0);
        int maxHeight = Math.min(properties.getInt("maxHeight" + suffix, WORLD_MAX_HEIGHT), WORLD_MAX_HEIGHT);
        String heightStr = properties.getString("heights" + suffix, "");
        if (minHeight == 0 && maxHeight == WORLD_MAX_HEIGHT && heightStr.length() == 0) {
            return null;
        } else {
            BitSet heightBits = new BitSet(WORLD_MAX_HEIGHT + 1);
            if (heightStr.length() == 0) {
                heightStr = String.valueOf(minHeight) + "-" + String.valueOf(maxHeight);
            }
            for (int i : MCPatcherUtils.parseIntegerList(heightStr, 0, WORLD_MAX_HEIGHT)) {
                heightBits.set(i);
            }
            return heightBits;
        }
    }

    public static Biome findBiomeByName(String name) {
        logBiomes();
        //TODO: inject identifier -> name mapper, also add to logBiomes
        if (name == null) {
            return null;
        }
        name = name.replace(" ", "");
        if (name.isEmpty()) {
            return null;
        }
        for (Biome biome : Biome.biomeList) {
            if (biome == null || biome.biomeName == null) {
                continue;
            }
            if (name.equalsIgnoreCase(biome.biomeName) || name.equalsIgnoreCase(biome.biomeName.replace(" ", ""))) {
                if (biome.biomeID >= 0 && biome.biomeID < Biome.biomeList.length) {
                    return biome;
                }
            }
        }
        return null;
    }

    public static ClientWorld getWorld() {
        return MinecraftClient.getInstance().world;
    }

    public static int getBiomeIDAt(BiomeAccess blockAccess, int i, int j, int k) {
        RegistryEntry<Biome> biome = getBiomeRegGenAt(blockAccess, i, j, k);
        return biome==null?0xFF : PortUtils.getBiomeId(biome);//biome == null ? Biome.biomeList.length : biome.biomeID;
    }

    public static RegistryEntry<Biome> getBiomeRegGenAt(BiomeAccess blockAccess, int i, int j, int k) {
        if (lastBiome == null || i != lastI || k != lastK) {
            lastI = i;
            lastK = k;
            lastBiome = instance.getBiomeGenAt_Impl(blockAccess, i, j, k);
        }
        return lastBiome;
    }
    public static Biome getBiomeGenAt(BiomeAccess blockAccess, int i, int j, int k) {
        if (lastBiome == null || i != lastI || k != lastK) {
            lastI = i;
            lastK = k;
            lastBiome = instance.getBiomeGenAt_Impl(blockAccess, i, j, k);
        }
        return lastBiome.getKeyOrValue().right().get();
    }

    public static float getTemperature(Biome biome, int i, int j, int k,int seaLevel) {
        return instance.getTemperaturef_Impl(biome, i, j, k,seaLevel);
    }

    public static float getTemperature(BiomeAccess blockAccess, int i, int j, int k,int seaLevel) {
        return getTemperature(getBiomeGenAt(blockAccess, i, j, k), i, j, k,seaLevel);
    }

    public static float getRainfall(Biome biome, int i, int j, int k) {
        return biome.weather.downfall();
    }

    public static float getRainfall(BiomeAccess blockAccess, int i, int j, int k) {
        return getRainfall(getBiomeGenAt(blockAccess, i, j, k), i, j, k);
    }

    public static int getGrassColor(BlockRenderView biome, int i, int j, int k) {
        return instance.getGrassColor_Impl(biome, i, j, k);
    }

    public static int getFoliageColor(BlockRenderView biome, int i, int j, int k) {
        return instance.getFoliageColor_Impl(biome, i, j, k);
    }

    public static int getWaterColorMultiplier(Biome biome) {
        return biome == null ? 0xffffff : biome.getWaterColor();
    }

    private static void logBiomes() {
        if (!biomesLogged) {
            biomesLogged = true;
            Registry<Biome> bioReg = MinecraftClient.getInstance().world.getRegistryManager().get(RegistryKeys.BIOME);
            Set<Identifier> ids = bioReg.getIds();
            int i = 0;
            for (Identifier id : ids) {
                Biome biome = bioReg.get(id);
                if (biome != null) {
                    int x = (int) (255.0f * (1.0f - biome.getTemperature()));
                    int y = (int) (255.0f * (1.0f - biome.getTemperature() * biome.weather.downfall()));
                    logger.config("setupBiome #%d id=%s \"%s\" %06x (%d,%d)", i++, id.toString(), PortUtils.getBiomeName(id),biome.getWaterColor(),  x, y);
                }
            }
        }
    }


    protected boolean isColorHeightDependent() {
        return true;
    }

    protected RegistryEntry<Biome> getBiomeGenAt_Impl(BiomeAccess blockAccess, int i, int j, int k) {
        return blockAccess.getBiome(new BlockPos(i, j, k));
    }

    protected float getTemperaturef_Impl(Biome biome, int i, int j, int k,int seaLevel) {
        return biome.getTemperature(new BlockPos(i, j, k),seaLevel);
    }

    protected int getGrassColor_Impl(BlockRenderView view, int i, int j, int k) {
        return BiomeColors.getGrassColor(view,new BlockPos(i, j, k));
    }

    protected int getFoliageColor_Impl(BlockRenderView view, int i, int j, int k) {
        return BiomeColors.getFoliageColor(view,new BlockPos(i, j, k));
    }
    BiomeAPI() {
    }

}