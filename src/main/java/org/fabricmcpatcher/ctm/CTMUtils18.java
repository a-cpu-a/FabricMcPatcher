package org.fabricmcpatcher.ctm;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.fabricmcpatcher.color.ColorizeBlock18;
import org.fabricmcpatcher.resource.FaceInfo;
import org.fabricmcpatcher.utils.MCLogger;
import org.fabricmcpatcher.utils.MCPatcherUtils;
import org.fabricmcpatcher.utils.block.BlockAPI;
import org.fabricmcpatcher.utils.block.BlockStateMatcher;
import org.fabricmcpatcher.utils.block.RenderBlocksUtils;

import java.util.*;

public class CTMUtils18 extends RenderBlockState {
    private static final MCLogger logger = MCLogger.getLogger(MCPatcherUtils.CONNECTED_TEXTURES);

    private static final int[][][] NEIGHBOR_OFFSET = new int[][][]{
        makeNeighborOffset(WEST_FACE, NORTH_FACE, EAST_FACE, SOUTH_FACE), // BOTTOM_FACE - flipped n-s in 1.8 custom models
        makeNeighborOffset(WEST_FACE, SOUTH_FACE, EAST_FACE, NORTH_FACE), // TOP_FACE
        makeNeighborOffset(EAST_FACE, BOTTOM_FACE, WEST_FACE, TOP_FACE), // NORTH_FACE
        makeNeighborOffset(WEST_FACE, BOTTOM_FACE, EAST_FACE, TOP_FACE), // SOUTH_FACE
        makeNeighborOffset(NORTH_FACE, BOTTOM_FACE, SOUTH_FACE, TOP_FACE), // WEST_FACE
        makeNeighborOffset(SOUTH_FACE, BOTTOM_FACE, NORTH_FACE, TOP_FACE), // EAST_FACE
    };

    private static final ThreadLocal<CTMUtils18> instances = new ThreadLocal<CTMUtils18>();

    private static final Set<Property<?>> ignoredProperties = new HashSet<>();
    private static final Map<Block, Property<?>> halfProperties = new IdentityHashMap<>();
    private static Block bedBlock;
    private static Property<?> bedHeadProperty;
    private static Property<?> bedFacingProperty;
    private static Block doublePlantBlock;

    private BakedModel model;
    private BlockState blockState;
    private BlockPos position;
    private Direction direction;
    private int effectiveFace;
    private int uvRotation;
    private int hvFace;
    private String textureFaceName;

    private final TileOverrideIterator.IJK ijkIterator = CTMUtils.newIJKIterator();
    private final TileOverrideIterator.Metadata metadataIterator = CTMUtils.newMetadataIterator();

    private final ColorizeBlock18 colorizeBlock;

    static void reset() {
        ignoredProperties.clear();
        halfProperties.clear();
        bedBlock = BlockAPI.getFixedBlock("minecraft:bed");
        bedHeadProperty = getPropertyByName(bedBlock, "part");
        bedFacingProperty = getPropertyByName(bedBlock, "facing");
        doublePlantBlock = BlockAPI.getFixedBlock("minecraft:double_plant");

        for (Block block : BlockAPI.getAllBlocks()) {
            logger.config("Block %s", BlockAPI.getBlockName(block));
            BlockState state = block.getDefaultState();
            for (Property<?> property : state.getProperties()) {
                String name = property.getName();
                if (name.equals("half") || name.equals("part")) {
                    ignoredProperties.add(property);
                    if (name.equals("half")) {
                        halfProperties.put(block, property);
                    }
                }
                logger.config("  %s(%s):%s", name, property.getName(), BlockStateMatcher.getPropertyValues(property));
            }
        }
    }

    private static Property<?> getPropertyByName(Block block, String propertyName) {
        if (block != null) {
            for (Property<?> property : block.getDefaultState().getProperties()) {
                if (propertyName.equals(property.getName())) {
                    return property;
                }
            }
        }
        return null;
    }

    private static boolean comparePropertyValues(Comparable a, Comparable b) {
        if (a == b) {
            return true;
        } else if (a == null) {
            return false;
        } else {
            return a.equals(b);
        }
    }

    public static CTMUtils18 getInstance() {
        CTMUtils18 instance = instances.get();
        if (instance == null) {
            instance = new CTMUtils18();
            instances.set(instance);
        }
        return instance;
    }

    public static void postRender() {
        getInstance().clear();
    }

    public static boolean postRender(boolean b) {
        postRender();
        return b;
    }

    private CTMUtils18() {
        logger.info("new CTMUtils18() for %s", Thread.currentThread());
        colorizeBlock = new ColorizeBlock18(this);
    }

    public boolean preRender(BlockRenderView blockAccess, BakedModel model, BlockState blockState, BlockPos position, Block block, boolean useAO) {
        //blockState = fixupState(blockState, blockAccess, block, position);
        this.blockAccess = blockAccess;
        this.model = model;
        this.blockState = blockState;
        this.position = position;
        this.block = block;
        this.useAO = useAO;
        direction = null;
        inWorld = true;
        offsetsComputed = false;
        haveOffsets = false;
        di = dj = dk = 0;

        colorizeBlock.preRender(blockAccess, model, blockState, position, block, useAO);

        return true;
    }

    public boolean preRenderHeld(BakedModel model, Block block, BlockState blockState) {
        return preRenderHeld(model, blockState, block);
    }

    public boolean preRenderHeld(BakedModel model, BlockState blockState, Block block) {
        blockAccess = null;
        this.model = model;
        this.blockState = blockState;
        position = null;
        this.block = block;
        useAO = false;
        direction = null;
        inWorld = false;
        offsetsComputed = false;
        haveOffsets = false;
        di = dj = dk = 0;

        colorizeBlock.preRenderHeld(model, blockState, block);

        return true;
    }
/*
    private static BlockState fixupState(BlockState blockState, BlockRenderView blockAccess, Block block, BlockPos position) {
        if (block == doublePlantBlock) {
            // for some reason, this is needed to fix the variant property on the bottom half of double grass
            blockState = block.getBlockStateInWorld(blockState, blockAccess, position);
        }
        return blockState;
    }*/

    public void setDirection(Direction direction) {
        this.direction = direction;
        colorizeBlock.setDirection(direction);
    }

    public void setDirectionWater(Direction direction) {
        this.direction = direction;
        colorizeBlock.setDirectionWater(direction);
    }

    public boolean useColormap(BakedQuad face) {
        return colorizeBlock.useColormap(face);
    }

    public int colorMultiplier(int color) {
        return colorizeBlock.colorMultiplier(color);
    }

    public float getVertexColor(float color, int vertex, int channel) {
        return colorizeBlock.getVertexColor(color, vertex, channel);
    }

    public void applyVertexColor(Tessellator tessellator, float base, int vertex) {
        colorizeBlock.applyVertexColor(tessellator, base, vertex);
    }

    public float applyVertexColor(float base, int vertex, float r, float g, float b) {
        return colorizeBlock.applyVertexColor(base, vertex, r, g, b);
    }

    public float getVertexColor(int index) {
        return colorizeBlock.getVertexColor(index);
    }

    public BakedQuad getModelFace(BakedQuad origFace) {
        FaceInfo faceInfo = FaceInfo.getFaceInfo(origFace);
        if (faceInfo == null) {
            textureFaceName = null;
            return origFace;
        }
        Sprite origIcon = faceInfo.getSprite();
        textureFaceName = faceInfo.getTextureName();
        setUVFace(faceInfo);
        TileOverrideIterator iterator = isInWorld() ? ijkIterator : metadataIterator;
        iterator.go(this, origIcon);
        Sprite newIcon = (Sprite) iterator.getIcon();
        return faceInfo.getAltFace(newIcon);
    }

    private void setUVFace(FaceInfo faceInfo) {
        effectiveFace = faceInfo.getEffectiveFace();
        uvRotation = faceInfo.getUVRotation();
        hvFace = effectiveFace >= 0 ? effectiveFace : RenderBlockState.NORTH_FACE;
    }

    @Override
    public void clear() {
        super.clear();
        model = null;
        blockState = null;
        position = null;
        colorizeBlock.clear();
    }

    @Override
    public int getI() {
        return position.getX();
    }

    @Override
    public int getJ() {
        return position.getY();
    }

    @Override
    public int getK() {
        return position.getZ();
    }

    @Override
    public int getBlockFace() {
        return effectiveFace;
    }

    @Override
    public int getTextureFace() {
        return direction == null ? -1 : direction.ordinal();
    }

    @Override
    public int getTextureFaceOrig() {
        return getTextureFace();
    }

    @Override
    public String getTextureFaceName() {
        return textureFaceName;
    }

    @Override
    public int getFaceForHV() {
        return hvFace;
    }

    @Override
    public boolean match(BlockStateMatcher matcher) {
        return matcher.matchBlockState(blockState);
    }

    @Override
    public int[] getOffset(int blockFace, int relativeDirection) {
        return NEIGHBOR_OFFSET[blockFace][(relativeDirection + uvRotation) & 7];
    }

    @Override
    public boolean setCoordOffsetsForRenderType() {
        if (!offsetsComputed) {
            offsetsComputed = true;
            if (block == bedBlock) {
                if (getEnumProperty(bedHeadProperty) > 0) { // part=foot
                    switch (getEnumProperty(bedFacingProperty)) {
                        case 2:
                            dk = -1; // head is one block north
                            break;

                        case 3:
                            dk = 1; // head is one block south
                            break;

                        case 4:
                            di = -1; // head is one block west
                            break;

                        case 5:
                            di = 1; // head is one block east
                            break;

                        default:
                            return false;
                    }
                    haveOffsets = true;
                }
            } else if (getEnumProperty(halfProperties.get(block)) > 0) { // half=lower
                dj = 1;
                haveOffsets = true;
            }
        }
        return haveOffsets;
    }

    private int getEnumProperty(Property<?> property) {
        if (property != null) {
            Comparable value = blockState.get(property);
            if (value instanceof Enum) {
                return ((Enum) value).ordinal();
            }
        }
        return -1;
    }

    @Override
    public boolean shouldConnectByBlock(Block neighbor, int neighborI, int neighborJ, int neighborK) {
        if (block != neighbor) {
            return false;
        }
        BlockState neighborState = blockAccess.getBlockState(new BlockPos(neighborI, neighborJ, neighborK));
        for (Property<?> property : blockState.getProperties()) {
            if (ignoredProperties.contains(property)) {
                continue;
            }
            if (!neighborState.getProperties().contains(property)) {
                return false;
            }
            Comparable value = blockState.get(property);
            Comparable neighborValue = neighborState.get(property);
            if (!comparePropertyValues(value, neighborValue)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean shouldConnectByTile(Block neighbor, Sprite origIcon, int neighborI, int neighborJ, int neighborK) {
        BlockPos neighborPosition = new BlockPos(neighborI, neighborJ, neighborK);
        BlockState neighborState = blockAccess.getBlockState(neighborPosition);
        BakedModel neighborModel = MinecraftClient.getInstance().getBlockRenderManager().getModel(neighborState);

        Random rng = Random.createLocal();
        rng.setSeed(neighborState.getRenderingSeed(neighborPosition));

        List<BakedQuad> neighborFaces = neighborModel.getQuads(neighborState, direction, rng);
        if (!neighborFaces.isEmpty()) {
            for (BakedQuad neighborFace : neighborFaces) {
                if (origIcon == FaceInfo.getFaceInfo(neighborFace).getSprite()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CTMUtils18{");
        if (block != null) {
            sb.append(BlockAPI.getBlockName(block)).append(" ");
        }
        if (position != null) {
            sb.append(" @").append(position.getX()).append(',').append(position.getY()).append(',').append(position.getZ());
        }
        if (direction != null) {
            sb.append(' ').append(direction.toString());
        }
        return sb.append('}').toString();
    }

    /*
    public static BlockState setBetterGrassProperty(BlockState state, Block block, BlockRenderView blockAccess, BlockPos position, Property<?> property, int direction) {
        boolean isBetterGrass = false;
        if (RenderBlocksUtils.enableBetterGrass) {
            int[] normal = NORMALS[direction];
            if (block == BlockAPI.getBlockAt(blockAccess, position.getX() + normal[0], position.getY() + normal[1] - 1, position.getZ() + normal[2])) {
                isBetterGrass = true;
            }
        }
        return state.setProperty(property, isBetterGrass);
    }*/ //TODO: it adds NWSE booleans to the grass block states....

    public int getParticleColor(BlockRenderView blockAccess, BlockState blockState, BlockPos position, int color) {
        //blockState = fixupState(blockState, blockAccess, blockState.getBlock(), position);
        return colorizeBlock.getParticleColor(blockAccess, blockState, position, color);
    }
}