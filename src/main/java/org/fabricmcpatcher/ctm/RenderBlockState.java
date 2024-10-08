package org.fabricmcpatcher.ctm;

import net.minecraft.block.Block;
import net.minecraft.client.texture.Sprite;
import net.minecraft.world.BlockRenderView;
import org.fabricmcpatcher.utils.block.BlockStateMatcher;

abstract public class RenderBlockState {
    public static final int BOTTOM_FACE = 0; // 0, -1, 0
    public static final int TOP_FACE = 1; // 0, 1, 0
    public static final int NORTH_FACE = 2; // 0, 0, -1
    public static final int SOUTH_FACE = 3; // 0, 0, 1
    public static final int WEST_FACE = 4; // -1, 0, 0
    public static final int EAST_FACE = 5; // 1, 0, 0

    public static final int[] GO_DOWN = new int[]{0, -1, 0};
    public static final int[] GO_UP = new int[]{0, 1, 0};
    public static final int[] GO_NORTH = new int[]{0, 0, -1};
    public static final int[] GO_SOUTH = new int[]{0, 0, 1};
    public static final int[] GO_WEST = new int[]{-1, 0, 0};
    public static final int[] GO_EAST = new int[]{1, 0, 0};

    public static final int[][] NORMALS = new int[][]{
        GO_DOWN,
        GO_UP,
        GO_NORTH,
        GO_SOUTH,
        GO_WEST,
        GO_EAST,
    };

    public static final int REL_L = 0;
    public static final int REL_DL = 1;
    public static final int REL_D = 2;
    public static final int REL_DR = 3;
    public static final int REL_R = 4;
    public static final int REL_UR = 5;
    public static final int REL_U = 6;
    public static final int REL_UL = 7;

    public static final int CONNECT_BY_BLOCK = 0;
    public static final int CONNECT_BY_TILE = 1;
    public static final int CONNECT_BY_MATERIAL = 2;

    protected static int[] add(int[] a, int[] b) {
        if (a.length != b.length) {
            throw new RuntimeException("arrays to add are not same length");
        }
        int[] c = new int[a.length];
        for (int i = 0; i < c.length; i++) {
            c[i] = a[i] + b[i];
        }
        return c;
    }

    protected static int[][] makeNeighborOffset(int left, int down, int right, int up) {
        int[] l = NORMALS[left];
        int[] d = NORMALS[down];
        int[] r = NORMALS[right];
        int[] u = NORMALS[up];
        return new int[][]{
            l,
            add(l, d),
            d,
            add(d, r),
            r,
            add(r, u),
            u,
            add(u, l),
        };
    }

    protected BlockRenderView blockAccess;
    protected Block block;
    protected boolean useAO;
    protected boolean inWorld;
    protected BlockStateMatcher matcher;

    protected boolean offsetsComputed;
    protected boolean haveOffsets;
    protected int di;
    protected int dj;
    protected int dk;

    final public BlockRenderView getBlockAccess() {
        return blockAccess;
    }

    final public Block getBlock() {
        return block;
    }

    final public boolean useAO() {
        return useAO;
    }

    final public boolean isInWorld() {
        return inWorld;
    }

    final public void setFilter(BlockStateMatcher matcher) {
        this.matcher = matcher;
    }

    final public BlockStateMatcher getFilter() {
        return matcher;
    }

    public void clear() {
        blockAccess = null;
        block = null;
        useAO = false;
        inWorld = false;
        offsetsComputed = false;
        haveOffsets = false;
        di = dj = dk = 0;
        setFilter(null);
    }

    abstract public int getI();

    abstract public int getJ();

    abstract public int getK();

    abstract public int getBlockFace();

    abstract public int getTextureFace();

    abstract public int getTextureFaceOrig();

    abstract public String getTextureFaceName();

    abstract public int getFaceForHV();

    abstract public boolean match(BlockStateMatcher matcher);

    abstract public int[] getOffset(int blockFace, int relativeDirection);

    abstract public boolean setCoordOffsetsForRenderType();

    final public int getDI() {
        return di;
    }

    final public int getDJ() {
        return dj;
    }

    final public int getDK() {
        return dk;
    }

    abstract public boolean shouldConnectByBlock(Block neighbor, int neighborI, int neighborJ, int neighborK);

    abstract public boolean shouldConnectByTile(Block neighbor, Sprite origIcon, int neighborI, int neighborJ, int neighborK);
}