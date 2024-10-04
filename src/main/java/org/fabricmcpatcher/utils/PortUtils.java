package org.fabricmcpatcher.utils;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.block.enums.RailShape;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
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
        //TODO: dont load biome stuff before joining world
        Registry<Biome> bioReg = MinecraftClient.getInstance().world.getRegistryManager().getOrThrow(RegistryKeys.BIOME);
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

    public static int getRenderType(Block block) {
        return 3;//not really used in 1.8+
    }

    public static BlockState getStateFromMetadata(Block block, int i) {

        int iA12 = i&12;

        Direction.Axis axis = switch (iA12) {
            case 4 -> Direction.Axis.X;
            default -> Direction.Axis.Y;
            case 8 -> Direction.Axis.Z;
        };

        if(block== Blocks.HAY_BLOCK) {
            return block.getDefaultState().with(HayBlock.AXIS,axis);
        }
        else if(block instanceof WallBannerBlock) {
            return block.getDefaultState().with(WallBannerBlock.FACING,EnumFacingGetFront(i));
        }
        else if(block instanceof WallSignBlock) {
            return block.getDefaultState().with(WallSignBlock.FACING,EnumFacingGetFront(i));
        }
        else if(block instanceof BannerBlock) {

            return block.getDefaultState().with(BannerBlock.ROTATION,i);
        }
        else if(block instanceof PoweredRailBlock) {
            return getRailState(block, i&7).with(PoweredRailBlock.POWERED,(i & 8) > 0);
        }
        else if(block instanceof RailBlock) {
            return getRailState(block, i);
        }
        else if(block==Blocks.SAND && i==1) {
            return Blocks.RED_SAND.getDefaultState();//Changes Id!
        }
        else if(block ==Blocks.WHITE_WOOL || block==Blocks.WHITE_STAINED_GLASS
                || block==Blocks.WHITE_STAINED_GLASS_PANE || block==Blocks.WHITE_CARPET
                || block==Blocks.WHITE_TERRACOTTA || block==Blocks.WHITE_CONCRETE
                || block instanceof ConcretePowderBlock) {// || block==Blocks.WHITE_GLAZED_TERRACOTTA
            return block.getDefaultState();//Changes Id! TODO: implement lol
        }
        else if (block==Blocks.COMPARATOR) {
            return block.getDefaultState()
                    .with(ComparatorBlock.FACING, getHorizontal(i))
                    .with(ComparatorBlock.POWERED, (i & 8) > 0)
                    .with(ComparatorBlock.MODE, (i & 4) > 0 ? ComparatorMode.SUBTRACT : ComparatorMode.COMPARE);
        }
        else if(block==Blocks.SPONGE && (i&1)==1) {
            return Blocks.WET_SPONGE.getDefaultState();//Changes Id!
        }
        else if(block instanceof AnvilBlock) {
            switch ((i&15)>>2) {
                default:
                case 0: block =Blocks.ANVIL;break;
                case 1: block =Blocks.CHIPPED_ANVIL;break;
                case 2: block =Blocks.DAMAGED_ANVIL;break;
                case 3: block =Blocks.ANVIL;break;//special Error state :) (buggy, crashy)
            }
            return block.getDefaultState().with(AnvilBlock.FACING,getHorizontal(i&3));//Changes Id!
        }
        else if(block instanceof JukeboxBlock) {
            return block.getDefaultState().with(JukeboxBlock.HAS_RECORD,i>0);
        }
        else if(block instanceof BrewingStandBlock) {
            BlockState iblockstate = block.getDefaultState();
            for (int j = 0; j < 3; ++j)
            {
                iblockstate = iblockstate.with(BrewingStandBlock.BOTTLE_PROPERTIES[i], (i & 1 << i) > 0);
            }

            return iblockstate;
        }
        else if(block instanceof CarvedPumpkinBlock) {
            return block.getDefaultState().with(CarvedPumpkinBlock.FACING,getHorizontal(i));
        }
        else if(block instanceof TrapdoorBlock) {
            return block.getDefaultState()
                    .with(TrapdoorBlock.FACING, getFacingTrapdoor(i))
                    .with(TrapdoorBlock.OPEN, (i & 4) != 0)
                    .with(TrapdoorBlock.HALF, (i & 8) == 0 ? BlockHalf.BOTTOM : BlockHalf.TOP);
        }
        else if(block ==Blocks.SHORT_GRASS && i!=1) {
            //Changes Id!
            if(i==2)return Blocks.FERN.getDefaultState();
            return Blocks.DEAD_BUSH.getDefaultState();
        }
        else if(block ==Blocks.FARMLAND) {
            return block.getDefaultState().with(FarmlandBlock.MOISTURE,i & FarmlandBlock.MAX_MOISTURE);
        }
        else if(block ==Blocks.CAKE) {
            return block.getDefaultState().with(CakeBlock.BITES,i);//i>6 -> ??? what happens when a server sends that in 1.8 or... 1.12?
        }
        else if(block ==Blocks.STONE) {
            //Changes Id!
            switch (i) {
                case 1:block=Blocks.GRANITE;break;
                case 2:block=Blocks.POLISHED_GRANITE;break;
                case 3:block=Blocks.DIORITE;break;
                case 4:block=Blocks.POLISHED_DIORITE;break;
                case 5:block=Blocks.ANDESITE;break;
                case 6:block=Blocks.POLISHED_ANDESITE;break;
                default:break;
            }
        }
        //https://github.com/search?q=repo%3AZeroedInOnTech%2F1.8.8+getStateFromMeta&type=code&p=2 TODO: finish this
        //TODO: 1.12 blocks


        return block.getDefaultState();
    }

    private static BlockState getRailState(Block block, int i) {
        return block.getDefaultState().with(RailBlock.SHAPE,
                i >= RailShape.values().length
                        ? RailShape.NORTH_SOUTH
                        : RailShape.values()[i]);
    }

    private static Direction EnumFacingGetFront(int i) {
        i = i %6;

        Direction dir=Direction.NORTH;
        if(i >=2) dir=Direction.fromHorizontal(i -2);
        return dir;
    }

    private static Direction getHorizontal(int i) {
        return switch (i % 4) {
            case 0 -> Direction.SOUTH;
            case 1 -> Direction.WEST;
            default -> Direction.NORTH;
            case 3 -> Direction.EAST;
        };
    }
    private static Direction getFacingTrapdoor(int i) {
        return switch (i&3) {
            case 0 -> Direction.NORTH;
            case 1 -> Direction.SOUTH;
            default -> Direction.WEST;
            case 3 -> Direction.EAST;
        };
    }
}
