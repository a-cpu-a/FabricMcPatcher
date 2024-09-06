package org.fabricmcpatcher.utils.block;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.fabricmcpatcher.resource.PropertiesFile;
import org.fabricmcpatcher.resource.TexturePackAPI;
import org.fabricmcpatcher.utils.MAL;
import org.fabricmcpatcher.utils.MCPatcherUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class BlockAPI {
    private static final HashMap<String, Integer> canonicalIdByName = new HashMap<String, Integer>();
    private static final HashMap<Integer,String> nameByCanonicalId = new HashMap<>();

    private static void addBlock(String id,int oldId) {
        canonicalIdByName.put(id,oldId);
        nameByCanonicalId.put(oldId,id);
    }

    static {
        addBlock("minecraft:air", 0);
        addBlock("minecraft:stone", 1);
        addBlock("minecraft:grass", 2);
        addBlock("minecraft:dirt", 3);
        addBlock("minecraft:cobblestone", 4);
        addBlock("minecraft:planks", 5);
        addBlock("minecraft:sapling", 6);
        addBlock("minecraft:bedrock", 7);
        addBlock("minecraft:flowing_water", 8);
        addBlock("minecraft:water", 9);
        addBlock("minecraft:flowing_lava", 10);
        addBlock("minecraft:lava", 11);
        addBlock("minecraft:sand", 12);
        addBlock("minecraft:gravel", 13);
        addBlock("minecraft:gold_ore", 14);
        addBlock("minecraft:iron_ore", 15);
        addBlock("minecraft:coal_ore", 16);
        addBlock("minecraft:log", 17);
        addBlock("minecraft:leaves", 18);
        addBlock("minecraft:sponge", 19);
        addBlock("minecraft:glass", 20);
        addBlock("minecraft:lapis_ore", 21);
        addBlock("minecraft:lapis_block", 22);
        addBlock("minecraft:dispenser", 23);
        addBlock("minecraft:sandstone", 24);
        addBlock("minecraft:noteblock", 25);
        addBlock("minecraft:bed", 26);
        addBlock("minecraft:golden_rail", 27);
        addBlock("minecraft:detector_rail", 28);
        addBlock("minecraft:sticky_piston", 29);
        addBlock("minecraft:web", 30);
        addBlock("minecraft:tallgrass", 31);
        addBlock("minecraft:deadbush", 32);
        addBlock("minecraft:piston", 33);
        addBlock("minecraft:piston_head", 34);
        addBlock("minecraft:wool", 35);
        addBlock("minecraft:piston_extension", 36);
        addBlock("minecraft:yellow_flower", 37);
        addBlock("minecraft:red_flower", 38);
        addBlock("minecraft:brown_mushroom", 39);
        addBlock("minecraft:red_mushroom", 40);
        addBlock("minecraft:gold_block", 41);
        addBlock("minecraft:iron_block", 42);
        addBlock("minecraft:double_stone_slab", 43);
        addBlock("minecraft:stone_slab", 44);
        addBlock("minecraft:brick_block", 45);
        addBlock("minecraft:tnt", 46);
        addBlock("minecraft:bookshelf", 47);
        addBlock("minecraft:mossy_cobblestone", 48);
        addBlock("minecraft:obsidian", 49);
        addBlock("minecraft:torch", 50);
        addBlock("minecraft:fire", 51);
        addBlock("minecraft:mob_spawner", 52);
        addBlock("minecraft:oak_stairs", 53);
        addBlock("minecraft:chest", 54);
        addBlock("minecraft:redstone_wire", 55);
        addBlock("minecraft:diamond_ore", 56);
        addBlock("minecraft:diamond_block", 57);
        addBlock("minecraft:crafting_table", 58);
        addBlock("minecraft:wheat", 59);
        addBlock("minecraft:farmland", 60);
        addBlock("minecraft:furnace", 61);
        addBlock("minecraft:lit_furnace", 62);
        addBlock("minecraft:standing_sign", 63);
        addBlock("minecraft:wooden_door", 64);
        addBlock("minecraft:ladder", 65);
        addBlock("minecraft:rail", 66);
        addBlock("minecraft:stone_stairs", 67);
        addBlock("minecraft:wall_sign", 68);
        addBlock("minecraft:lever", 69);
        addBlock("minecraft:stone_pressure_plate", 70);
        addBlock("minecraft:iron_door", 71);
        addBlock("minecraft:wooden_pressure_plate", 72);
        addBlock("minecraft:redstone_ore", 73);
        addBlock("minecraft:lit_redstone_ore", 74);
        addBlock("minecraft:unlit_redstone_torch", 75);
        addBlock("minecraft:redstone_torch", 76);
        addBlock("minecraft:stone_button", 77);
        addBlock("minecraft:snow_layer", 78);
        addBlock("minecraft:ice", 79);
        addBlock("minecraft:snow", 80);
        addBlock("minecraft:cactus", 81);
        addBlock("minecraft:clay", 82);
        addBlock("minecraft:reeds", 83);
        addBlock("minecraft:jukebox", 84);
        addBlock("minecraft:fence", 85);
        addBlock("minecraft:pumpkin", 86);
        addBlock("minecraft:netherrack", 87);
        addBlock("minecraft:soul_sand", 88);
        addBlock("minecraft:glowstone", 89);
        addBlock("minecraft:portal", 90);
        addBlock("minecraft:lit_pumpkin", 91);
        addBlock("minecraft:cake", 92);
        addBlock("minecraft:unpowered_repeater", 93);
        addBlock("minecraft:powered_repeater", 94);
        addBlock("minecraft:chest_locked_aprilfools_super_old_legacy_we_should_not_even_have_this", 95);
        addBlock("minecraft:trapdoor", 96);
        addBlock("minecraft:monster_egg", 97);
        addBlock("minecraft:stonebrick", 98);
        addBlock("minecraft:brown_mushroom_block", 99);
        addBlock("minecraft:red_mushroom_block", 100);
        addBlock("minecraft:iron_bars", 101);
        addBlock("minecraft:glass_pane", 102);
        addBlock("minecraft:melon_block", 103);
        addBlock("minecraft:pumpkin_stem", 104);
        addBlock("minecraft:melon_stem", 105);
        addBlock("minecraft:vine", 106);
        addBlock("minecraft:fence_gate", 107);
        addBlock("minecraft:brick_stairs", 108);
        addBlock("minecraft:stone_brick_stairs", 109);
        addBlock("minecraft:mycelium", 110);
        addBlock("minecraft:waterlily", 111);
        addBlock("minecraft:nether_brick", 112);
        addBlock("minecraft:nether_brick_fence", 113);
        addBlock("minecraft:nether_brick_stairs", 114);
        addBlock("minecraft:nether_wart", 115);
        addBlock("minecraft:enchanting_table", 116);
        addBlock("minecraft:brewing_stand", 117);
        addBlock("minecraft:cauldron", 118);
        addBlock("minecraft:end_portal", 119);
        addBlock("minecraft:end_portal_frame", 120);
        addBlock("minecraft:end_stone", 121);
        addBlock("minecraft:dragon_egg", 122);
        addBlock("minecraft:redstone_lamp", 123);
        addBlock("minecraft:lit_redstone_lamp", 124);
        addBlock("minecraft:double_wooden_slab", 125);
        addBlock("minecraft:wooden_slab", 126);
        addBlock("minecraft:cocoa", 127);
        addBlock("minecraft:sandstone_stairs", 128);
        addBlock("minecraft:emerald_ore", 129);
        addBlock("minecraft:ender_chest", 130);
        addBlock("minecraft:tripwire_hook", 131);
        addBlock("minecraft:tripwire", 132);
        addBlock("minecraft:emerald_block", 133);
        addBlock("minecraft:spruce_stairs", 134);
        addBlock("minecraft:birch_stairs", 135);
        addBlock("minecraft:jungle_stairs", 136);
        addBlock("minecraft:command_block", 137);
        addBlock("minecraft:beacon", 138);
        addBlock("minecraft:cobblestone_wall", 139);
        addBlock("minecraft:flower_pot", 140);
        addBlock("minecraft:carrots", 141);
        addBlock("minecraft:potatoes", 142);
        addBlock("minecraft:wooden_button", 143);
        addBlock("minecraft:skull", 144);
        addBlock("minecraft:anvil", 145);
        addBlock("minecraft:trapped_chest", 146);
        addBlock("minecraft:light_weighted_pressure_plate", 147);
        addBlock("minecraft:heavy_weighted_pressure_plate", 148);
        addBlock("minecraft:unpowered_comparator", 149);
        addBlock("minecraft:powered_comparator", 150);
        addBlock("minecraft:daylight_detector", 151);
        addBlock("minecraft:redstone_block", 152);
        addBlock("minecraft:quartz_ore", 153);
        addBlock("minecraft:hopper", 154);
        addBlock("minecraft:quartz_block", 155);
        addBlock("minecraft:quartz_stairs", 156);
        addBlock("minecraft:activator_rail", 157);
        addBlock("minecraft:dropper", 158);
        addBlock("minecraft:stained_hardened_clay", 159);
        addBlock("minecraft:hay_block", 170);
        addBlock("minecraft:carpet", 171);
        addBlock("minecraft:hardened_clay", 172);
        addBlock("minecraft:coal_block", 173);
        addBlock("minecraft:packed_ice", 174);
        addBlock("minecraft:double_plant", 175);
        //TODO: add 1.12 stuff, fix some block mappings with 1.13 ids
    }

    private static final BlockAPI instance = new BlockAPI();

    public static Block getFixedBlock(String name) {
        Block block = parseBlockName(name);
        if (block == null) {
            new IllegalArgumentException("unknown block " + name).printStackTrace();
        }
        return block;
    }

    public static Block parseBlockName(String name) {
        if (MCPatcherUtils.isNullOrEmpty(name)) {
            return null;
        }
        if (name.matches("(minecraft:)?\\d+")) {
            int id = Integer.parseInt(name.replace("minecraft:", ""));
            return instance.getBlockById_Impl(id);
        }
        name = getFullName(name);
        return instance.getBlockByName_Impl(name);
    }

    public static String getBlockName(Block block) {
        return block == null ? "(null)" : instance.getBlockName_Impl(block);
    }

    public static List<Block> getAllBlocks() {
        List<Block> blocks = new ArrayList<Block>();
        for (Iterator<Block> i = instance.iterator_Impl(); i.hasNext(); ) {
            Block block = i.next();
            if (block != null && !blocks.contains(block)) {
                blocks.add(block);
            }
        }
        return blocks;
    }

    public static Block getBlockAt(BlockRenderView blockAccess, int i, int j, int k) {
        return instance.getBlockAt_Impl(blockAccess, i, j, k);
    }

    public static int getMetadataAt(BlockRenderView blockAccess, int i, int j, int k) {
        return instance.getMetadataAt_Impl(blockAccess, i, j, k);
    }

    public static Sprite getBlockIcon(Block block, BlockRenderView blockAccess, int i, int j, int k, int face) {
        return instance.getBlockIcon_Impl(block, blockAccess, i, j, k, face);
    }

    public static boolean shouldSideBeRendered(Block block, BlockRenderView blockAccess, int i, int j, int k, int face) {
        return instance.shouldSideBeRendered_Impl(block, blockAccess, i, j, k, face);
    }
    public static boolean shouldSideBeRendered(Block block, BlockRenderView blockAccess, BlockPos p, Direction face) {
        return instance.shouldSideBeRendered_Impl(block, blockAccess, p, face);
    }

    // used by custom colors ItemRenderer patch in 1.6 only
    public static Block getBlockById(int id) {
        return instance.getBlockById_Impl(id);
    }

    public static String getFullName(String name) {
        return name == null ? null : name.indexOf(':') >= 0 ? name : "minecraft:" + name;
    }

    public static int getBlockLightValue(Block block) {
        return instance.getBlockLightValue_Impl(block);
    }

    public static BlockStateMatcher createMatcher(PropertiesFile source, String matchString) {
        Map<String, String> propertyMap = new HashMap<>();
        String namespace = null;
        String blockName = null;
        StringBuilder metadata = new StringBuilder();
        StringBuilder metaString = new StringBuilder();
        for (String s : matchString.split("\\s*:\\s*")) {
            if (s.equals("")) {
                continue;
            }
            boolean appendThis = false;
            String[] tokens = s.split("\\s*=\\s*", 2);
            if (blockName == null) {
                blockName = s;
            } else if (tokens.length == 2) {
                propertyMap.put(tokens[0], tokens[1]);
                appendThis = true;
            } else if (namespace == null && !s.matches("\\d[-, 0-9]*")) {
                namespace = blockName;
                blockName = s;
            } else if (s.matches("\\d[-, 0-9]*")) {
                metadata.append(' ').append(s);
                appendThis = true;
            } else {
                source.warning("invalid token '%s' in %s", source, s, matchString);
                return null;
            }
            if (appendThis) {
                metaString.append(':');
                metaString.append(s);
            }
        }

        if (MCPatcherUtils.isNullOrEmpty(namespace)) {
            namespace = source.getResource().getNamespace();
        }
        if (MCPatcherUtils.isNullOrEmpty(blockName)) {
            source.warning("cannot parse namespace/block name from %s", matchString);
            return null;
        }
        Block block = parseBlockName(namespace + ':' + blockName);
        if (block == null) {
            source.warning("unknown block %s:%s", namespace, blockName);
            return null;
        }

        try {
            return instance.getBlockStateMatcherClass_Impl().getDeclaredConstructor(
                PropertiesFile.class, String.class, Block.class, String.class, Map.class
            ).newInstance(
                source, metaString.toString(), block, metadata.toString().trim(), propertyMap
            );
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String expandTileName(String tileName) {
        return instance.expandTileName_Impl(tileName);
    }

    BlockAPI() {
    }
    private final Block airBlock= Blocks.AIR;
    /*
    V2(Registry<Block> registry) {
        this.registry = registry;
        File outputFile = new File("blocks17.txt");
        if (outputFile.isFile()) {
            PrintStream ps = null;
            try {
                ps = new PrintStream(outputFile);
                String[] nameList = new String[4096];
                for (String name17 : registry.getKeys()) {
                    Block block = registry.getValue(name17);
                    if (block != null) {
                        int id = registry.getId(block);
                        if (id >= 0 && id < nameList.length) {
                            nameList[id] = name17;
                        }
                    }
                }
                for (int id = 0; id < nameList.length; id++) {
                    if (nameList[id] != null) {
                        ps.printf("addBlock(\"%s\", %d);\n", nameList[id], id);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                MCPatcherUtils.close(ps);
            }
        }
    }

    V2() {
        this(Block.blockRegistry);
    }*/
    /*
    protected Block getBlockAt_Impl(BlockRenderView blockAccess, int i, int j, int k) {
        return blockAccess.getBlock(i, j, k);
    }

    protected int getMetadataAt_Impl(BlockRenderView blockAccess, int i, int j, int k) {
        return blockAccess.getBlockMetadata(i, j, k);
    }

    protected Icon getBlockIcon_Impl(Block block, BlockRenderView blockAccess, int i, int j, int k, int face) {
        return block.getBlockIcon(blockAccess, i, j, k, face);
    }

    protected boolean shouldSideBeRendered_Impl(Block block, BlockRenderView blockAccess, int i, int j, int k, int face) {
        return block.shouldSideBeRendered(blockAccess, i, j, k, face);
    }*/

    protected Iterator<Block> iterator_Impl() {
        return Registries.BLOCK.iterator();
    }

    protected Block getBlockById_Impl(int id) {
        return Registries.BLOCK.get(Identifier.of(nameByCanonicalId.get(id)));
    }
    /*
    protected Block getBlockByName_Impl(String name) {
        return registry.getValue(name);
    }

    protected String getBlockName_Impl(Block block) {
        String name = registry.getKey(block);
        return name == null ? String.valueOf(registry.getId(block)) : name;
    }*/

    protected int getBlockLightValue_Impl(Block block) {
        return block.getDefaultState().getLuminance();
    }
    /*
    protected Class<? extends BlockStateMatcher> getBlockStateMatcherClass_Impl() {
        return BlockStateMatcher.V1.class;
    }

    protected String expandTileName_Impl(String tileName) {
        return tileName;
    }*/

    /*
    V3() {
        super(Block.blockRegistry1);
        airBlock = registry.getValueObject(null);
    }*/

    protected Block getBlockAt_Impl(BlockRenderView blockAccess, int i, int j, int k) {
        return blockAccess.getBlockState(new BlockPos(i, j, k)).getBlock();
    }

    protected int getMetadataAt_Impl(BlockRenderView blockAccess, int i, int j, int k) {
        return 0; // TODO
    }

    protected Sprite getBlockIcon_Impl(Block block, BlockRenderView blockAccess, int i, int j, int k, int face) {
        return MinecraftClient.getInstance()
                .getBlockRenderManager()
                .getModel(blockAccess
                        .getBlockState(new BlockPos(i,j,k))).getParticleSprite();//null;
    }

    protected boolean shouldSideBeRendered_Impl(Block block, BlockRenderView blockAccess, int i, int j, int k, int face) {
        BlockPos pos = new BlockPos(i, j, k);

        BlockState state = blockAccess.getBlockState(pos);
        Direction dir = Direction.values()[face];
        BlockState state2 = blockAccess.getBlockState(pos.add(dir.getVector()));

        return Block.shouldDrawSide(state, state2, dir);
    }
    protected boolean shouldSideBeRendered_Impl(Block block, BlockRenderView blockAccess, BlockPos pos, Direction dir) {
        BlockState state = blockAccess.getBlockState(pos);

        BlockState state2 = blockAccess.getBlockState(pos.add(dir.getVector()));

        return Block.shouldDrawSide(state, state2, dir);
    }

    protected Block getBlockByName_Impl(String name) {
        Block block = Registries.BLOCK.get(TexturePackAPI.parseIdentifier(name));
        if (block == airBlock && !name.equals("minecraft:air")) {
            return null;
        } else {
            return block;
        }
    }

    protected String getBlockName_Impl(Block block) {
        Identifier name = Registries.BLOCK.getId(block);
        return name.toString();
    }

    protected Class<? extends BlockStateMatcher> getBlockStateMatcherClass_Impl() {
        return BlockStateMatcher.class;
    }

    protected String expandTileName_Impl(String tileName) {
        if (!tileName.contains(":")) {
            tileName = "minecraft:blocks/" + tileName;
        }
        return tileName;
    }
}