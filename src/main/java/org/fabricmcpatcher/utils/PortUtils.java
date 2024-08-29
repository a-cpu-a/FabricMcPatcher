package org.fabricmcpatcher.utils;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.fabricmcpatcher.FabricMcPatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PortUtils {
    public static int getWorldId(ClientWorld world) {
        return world.getDimensionEffects().getSkyType().ordinal()-1;
    }

    //in mcpatcher or optifine folder
    public static Identifier findResource(String resource) {
        for (String folder : FabricMcPatcher.CHECK_FOLDERS) {
            Identifier testId = Identifier.ofVanilla(folder+resource);
            Optional<Resource> testRes =  MinecraftClient.getInstance().getResourceManager().getResource(testId);
            if(testRes.isEmpty())
                continue;

            return testId;
        }
        return null;
    }

    public static final Map<String,Identifier> biomeName2Id;

    static {
        Map<String,Identifier> map = new HashMap<>();

        map.put("Ocean",Identifier.ofVanilla("ocean"));

        biomeName2Id = ImmutableMap.copyOf(map);
    }

    public static String getBiomeName(Identifier id) {
        BiomeIdUtils.BiomeInfo info = BiomeIdUtils.newId2Info.get(id.getPath());
        if(info==null)
            return null;

        return info.propName;
    }

    public static int getBiomeId(RegistryEntry<Biome> biome) {
        BiomeIdUtils.BiomeInfo info = BiomeIdUtils.newId2Info.get(biome.getKey().get().getValue().getPath());
        if(info==null)return -1;

        return info.id;
    }
    public static int getBiomeId(String propName) {
        BiomeIdUtils.BiomeInfo info = BiomeIdUtils.propName2Info.get(propName);
        if(info==null)return -1;

        return info.id;
    }

    public static Biome findBiomeByName(String name) {
        if (name == null) {
            return null;
        }
        name = name.replace(" ", "");
        if (name.isEmpty()) {
            return null;
        }

        BiomeIdUtils.BiomeInfo info = BiomeIdUtils.propName2Info.get(name);

        if(info==null)
            return null;

        Registry<Biome> bioReg = MinecraftClient.getInstance().world.getRegistryManager().get(RegistryKeys.BIOME);
        return bioReg.get(Identifier.of(info.newId));
    }
}
