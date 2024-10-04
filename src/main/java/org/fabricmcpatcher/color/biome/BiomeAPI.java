package org.fabricmcpatcher.color.biome;


import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import org.fabricmcpatcher.resource.PropertiesFile;
import org.fabricmcpatcher.utils.MCLogger;
import org.fabricmcpatcher.utils.MCPatcherUtils;
import org.fabricmcpatcher.utils.PortUtils;
import org.fabricmcpatcher.utils.block.ExtendedBlockView;
import org.fabricmcpatcher.utils.id.BiomeIdUtils;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.Set;

public class BiomeAPI {
    private static final MCLogger logger = MCLogger.getLogger(MCPatcherUtils.CUSTOM_COLORS);
    private static final BiomeAPI instance = new BiomeAPI();

    public static final int WORLD_MAX_HEIGHT = 255;
    public static final boolean isColorHeightDependent = instance.isColorHeightDependent();

    private static boolean biomesLogged;

    private static Identifier lastBiomeId;
    private static Biome lastBiome;
    private static int lastI;
    private static int lastK;


    public static void parseBiomeList(String list, BitSet bits) {
        logBiomes();
        if (MCPatcherUtils.isNullOrEmpty(list)) {
            return;
        }
        for (String s : list.split(list.contains(",") ? "\\s*,\\s*" : "\\s+")) {
            Biome biome = findBiomeByName(s);
            if (biome != null) {
                bits.set(MinecraftClient.getInstance().world.getRegistryManager().getOrThrow(RegistryKeys.BIOME).getRawId(biome));
            }
        }
    }

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
        /*
        for (Biome biome : Biome.biomeList) {
            if (biome == null || biome.biomeName == null) {
                continue;
            }
            if (name.equalsIgnoreCase(biome.biomeName) || name.equalsIgnoreCase(biome.biomeName.replace(" ", ""))) {
                if (biome.biomeID >= 0 && biome.biomeID < Biome.biomeList.length) {
                    return biome;
                }
            }
        }*/
        return PortUtils.findBiomeByName(name);
    }

    public static ClientWorld getWorld() {
        return MinecraftClient.getInstance().world;
    }

    public static int getBiomeIDAt(BlockView blockAccess, int i, int j, int k) {
        Identifier biome = getBiomeRegGenAt(blockAccess, i, j, k);
        return biome==null?0xFF : PortUtils.getBiomeId(biome);//biome == null ? Biome.biomeList.length : biome.biomeID;
    }
    public static int getNewBiomeIdAt(BlockView blockAccess, int i, int j, int k) {
        Biome biome = getBiomeGenAt(blockAccess, i, j, k);
        return biome==null?-1 : MinecraftClient.getInstance().world.getRegistryManager().getOrThrow(RegistryKeys.BIOME).getRawId(biome);//biome == null ? Biome.biomeList.length : biome.biomeID;
    }

    public static Identifier getBiomeRegGenAt(BlockView blockAccess, int i, int j, int k) {
        if (lastBiomeId == null || i != lastI || k != lastK) {
            if(i != lastI || k != lastK)
                lastBiome=null;
            lastI = i;
            lastK = k;
            lastBiomeId =instance.getBiomeGenAt_Impl(blockAccess, i, j, k);
        }
        return lastBiomeId;
    }
    public static Biome getBiomeGenAt(BlockView blockAccess, int i, int j, int k) {
        if (lastBiome == null || i != lastI || k != lastK) {
            if(i != lastI || k != lastK)
                lastBiomeId=null;
            lastI = i;
            lastK = k;
            lastBiome =instance.getBiomeGenAt_Impl_(blockAccess, i, j, k);
        }
        return lastBiome;
    }

    public static float getTemperature(Biome biome, int i, int j, int k,int seaLevel) {
        return instance.getTemperaturef_Impl(biome, i, j, k,seaLevel);
    }

    public static float getTemperature(BlockView blockAccess, int i, int j, int k,int seaLevel) {
        return getTemperature(getBiomeGenAt(blockAccess, i, j, k), i, j, k,seaLevel);
    }

    public static float getRainfall(Biome biome, int i, int j, int k) {
        return biome.weather.downfall();
    }

    public static float getRainfall(BlockView blockAccess, int i, int j, int k) {
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
        if(MinecraftClient.getInstance().world==null)return;
        if (!biomesLogged) {
            biomesLogged = true;
            Registry<Biome> bioReg = MinecraftClient.getInstance().world.getRegistryManager().getOrThrow(RegistryKeys.BIOME);
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

    protected Identifier getBiomeGenAt_Impl(BlockView blockAccess, int i, int j, int k) {
        @UnknownNullability RegistryEntry<Biome> biome = blockAccess.getBiomeFabric(new BlockPos(i, j, k));
        if(biome==null)
            return Identifier.ofVanilla("plains");
        return biome.getKey().get().getValue();
    }
    protected Biome getBiomeGenAt_Impl_(BlockView blockAccess, int i, int j, int k) {
        @UnknownNullability RegistryEntry<Biome> biome = blockAccess.getBiomeFabric(new BlockPos(i, j, k));
        if(biome==null) {
            return ((ExtendedBlockView)blockAccess).mcPatcher$getBiomeRegistry().get(Identifier.ofVanilla("plains"));
        }
        return ((ExtendedBlockView)blockAccess).mcPatcher$getBiomeRegistry().get(biome.getKey().get());
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