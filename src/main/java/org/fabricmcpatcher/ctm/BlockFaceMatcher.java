package org.fabricmcpatcher.ctm;


import org.fabricmcpatcher.utils.MAL;
import org.fabricmcpatcher.utils.MCPatcherUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.fabricmcpatcher.ctm.RenderBlockState.*;

public class BlockFaceMatcher {

    public static BlockFaceMatcher create(String propertyValue) {
        if (!MCPatcherUtils.isNullOrEmpty(propertyValue)) {
            String[] values = propertyValue.toLowerCase().split("\\s+");
            try {
                BlockFaceMatcher matcher = new BlockFaceMatcher(values);
                if (!matcher.isAll()) {
                    return matcher;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private final Set<String> faces = new HashSet<String>();
    private boolean all;

    protected BlockFaceMatcher(String[] values) {
        for (String face : values) {
            if (MCPatcherUtils.isNullOrEmpty(face)) {
                continue;
            }
            faces.add(face);
            if (face.equals("bottom")) {
                faces.add("down");
            } else if (face.equals("top")) {
                faces.add("up");
            } else if (face.equals("side") || face.equals("sides")) {
                faces.add("north");
                faces.add("south");
                faces.add("west");
                faces.add("east");
            } else if (face.equals("all")) {
                all = true;
                break;
            }
        }
        if (faces.contains("down") && faces.contains("up")) {
            faces.add("end");
            faces.add("ends");
        }
        if (faces.contains("north") && faces.contains("south") && faces.contains("west") && faces.contains("east")) {
            faces.add("side");
            faces.add("sides");
        }
        Collections.addAll(faces, values);
    }

    public boolean match(RenderBlockState renderBlockState) {
        return faces.contains(renderBlockState.getTextureFaceName());
    }

    protected boolean isAll() {
        return all;
    }
/*
    final public static class V1 extends BlockFaceMatcher {
        private final int faces;

        protected V1(String[] values) {
            int flags = 0;
            for (String face : values) {
                if (face.equals("bottom") || face.equals("down")) {
                    flags |= (1 << BOTTOM_FACE);
                } else if (face.equals("top") || face.equals("up")) {
                    flags |= (1 << TOP_FACE);
                } else if (face.equals("north")) {
                    flags |= (1 << NORTH_FACE);
                } else if (face.equals("south")) {
                    flags |= (1 << SOUTH_FACE);
                } else if (face.equals("east")) {
                    flags |= (1 << EAST_FACE);
                } else if (face.equals("west")) {
                    flags |= (1 << WEST_FACE);
                } else if (face.equals("side") || face.equals("sides")) {
                    flags |= (1 << NORTH_FACE) | (1 << SOUTH_FACE) | (1 << EAST_FACE) | (1 << WEST_FACE);
                } else if (face.equals("all")) {
                    flags = -1;
                }
            }
            faces = flags;
        }

        @Override
        public boolean match(RenderBlockState renderBlockState) {
            int face = renderBlockState.getTextureFace();
            return face >= 0 && (faces & (1 << face)) != 0;
        }

        @Override
        protected boolean isAll() {
            return faces == -1;
        }
    }*/
}