package org.fabricmcpatcher.utils;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import org.fabricmcpatcher.FabricMcPatcher;
import org.fabricmcpatcher.utils.id.BiomeIdUtils;

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

    public static int getBiomeId(Identifier biome) {
        BiomeIdUtils.BiomeInfo info = BiomeIdUtils.newId2Info.get(biome.getPath());
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

    public static NbtCompound getTagCompound(ItemStack itemStack) {
        return null;//TODO: implement, by converting components & adding to custom data
    }

    public static int[] setVertexDataSprite(int[] vtx, Sprite newSprite, Direction face) {

        for (int j = 0; j < 4; j++) {
            int i = 7 * j;
            float f = Float.intBitsToFloat(vtx[i]);
            float f1 = Float.intBitsToFloat(vtx[i + 1]);
            float f2 = Float.intBitsToFloat(vtx[i + 2]);
            float f3 = 0.0F;
            float f4 = 0.0F;

            switch (face)
            {
                case DOWN:
                    f3 = f * 16.0F;
                    f4 = (1.0F - f2) * 16.0F;
                    break;

                case UP:
                    f3 = f * 16.0F;
                    f4 = f2 * 16.0F;
                    break;

                case NORTH:
                    f3 = (1.0F - f) * 16.0F;
                    f4 = (1.0F - f1) * 16.0F;
                    break;

                case SOUTH:
                    f3 = f * 16.0F;
                    f4 = (1.0F - f1) * 16.0F;
                    break;

                case WEST:
                    f3 = f2 * 16.0F;
                    f4 = (1.0F - f1) * 16.0F;
                    break;

                case EAST:
                    f3 = (1.0F - f2) * 16.0F;
                    f4 = (1.0F - f1) * 16.0F;
            }

            vtx[i + 4] = Float.floatToRawIntBits(newSprite.getFrameFromU(f3));
            vtx[i + 4 + 1] = Float.floatToRawIntBits(newSprite.getFrameFromV(f4));
        }
        return vtx;
    }

    public static BakedQuad ModelFaceSprite(BakedQuad face, Sprite altSprite) {
        Direction dir = BakedQuadFactory.decodeDirection(face.getVertexData());
        return new BakedQuad(setVertexDataSprite(face.getVertexData().clone(),altSprite,dir),face.getColorIndex(), dir, altSprite,face.hasShade(),face.getLightEmission() );
    }
}
