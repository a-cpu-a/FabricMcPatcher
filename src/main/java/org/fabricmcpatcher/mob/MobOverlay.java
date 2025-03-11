package org.fabricmcpatcher.mob;


import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.fabricmcpatcher.resource.TexturePackAPI;

public class MobOverlay {
    private static final Identifier MOOSHROOM_OVERLAY = TexturePackAPI.newMCPatcherIdentifier("/mob/redcow_overlay.png", "mob/cow/mooshroom_overlay.png");
    private static final Identifier SNOWMAN_OVERLAY = TexturePackAPI.newMCPatcherIdentifier("/mob/snowman_overlay.png", "mob/snowman_overlay.png");

    private static final double MOO_X0 = -0.45;
    private static final double MOO_X1 = 0.45;
    private static final double MOO_Y0 = -0.5;
    private static final double MOO_Y1 = 0.5;
    private static final double MOO_Z0 = -0.45;
    private static final double MOO_Z1 = 0.45;

    private static final double SNOW_X0 = -0.5;
    private static final double SNOW_X1 = 0.5;
    private static final double SNOW_Y0 = -0.5;
    private static final double SNOW_Y1 = 0.5;
    private static final double SNOW_Z0 = -0.5;
    private static final double SNOW_Z1 = 0.5;

    private static boolean overlayActive;
    private static int overlayCounter;
    private static boolean haveMooshroom;
    private static boolean haveSnowman;

    static void reset() {
        haveMooshroom = TexturePackAPI.hasResource(MOOSHROOM_OVERLAY);
        haveSnowman = TexturePackAPI.hasResource(SNOWMAN_OVERLAY);
    }

    public static Identifier setupMooshroom(LivingEntity entity, Identifier defaultTexture) {
        overlayCounter = 0;
        if (haveMooshroom) {
            overlayActive = true;
            return MobRandomizer.randomTexture(entity, MOOSHROOM_OVERLAY);
        } else {
            overlayActive = false;
            return defaultTexture;
        }
    }

    public static boolean renderMooshroomOverlay(double offset) {
        if (overlayActive && overlayCounter < 3) {
            float tileX0 = overlayCounter / 3.0f;
            float tileX1 = ++overlayCounter / 3.0f;
/*
            Tessellator tessellator = TessellatorAPI.getTessellator();
            TessellatorAPI.startDrawingQuads(tessellator);
            TessellatorAPI.addVertexWithUV(tessellator, MOO_X0 + offset, MOO_Y1 + offset, MOO_Z0 - offset, tileX0, 0.0);
            TessellatorAPI.addVertexWithUV(tessellator, MOO_X0 + offset, MOO_Y0 + offset, MOO_Z0 - offset, tileX0, 1.0);
            TessellatorAPI.addVertexWithUV(tessellator, MOO_X1 + offset, MOO_Y0 + offset, MOO_Z1 - offset, tileX1, 1.0);
            TessellatorAPI.addVertexWithUV(tessellator, MOO_X1 + offset, MOO_Y1 + offset, MOO_Z1 - offset, tileX1, 0.0);
            TessellatorAPI.addVertexWithUV(tessellator, MOO_X1 + offset, MOO_Y1 + offset, MOO_Z1 - offset, tileX0, 0.0);
            TessellatorAPI.addVertexWithUV(tessellator, MOO_X1 + offset, MOO_Y0 + offset, MOO_Z1 - offset, tileX0, 1.0);
            TessellatorAPI.addVertexWithUV(tessellator, MOO_X0 + offset, MOO_Y0 + offset, MOO_Z0 - offset, tileX1, 1.0);
            TessellatorAPI.addVertexWithUV(tessellator, MOO_X0 + offset, MOO_Y1 + offset, MOO_Z0 - offset, tileX1, 0.0);
            TessellatorAPI.addVertexWithUV(tessellator, MOO_X0 + offset, MOO_Y1 + offset, MOO_Z1 - offset, tileX0, 0.0);
            TessellatorAPI.addVertexWithUV(tessellator, MOO_X0 + offset, MOO_Y0 + offset, MOO_Z1 - offset, tileX0, 1.0);
            TessellatorAPI.addVertexWithUV(tessellator, MOO_X1 + offset, MOO_Y0 + offset, MOO_Z0 - offset, tileX1, 1.0);
            TessellatorAPI.addVertexWithUV(tessellator, MOO_X1 + offset, MOO_Y1 + offset, MOO_Z0 - offset, tileX1, 0.0);
            TessellatorAPI.addVertexWithUV(tessellator, MOO_X1 + offset, MOO_Y1 + offset, MOO_Z0 - offset, tileX0, 0.0);
            TessellatorAPI.addVertexWithUV(tessellator, MOO_X1 + offset, MOO_Y0 + offset, MOO_Z0 - offset, tileX0, 1.0);
            TessellatorAPI.addVertexWithUV(tessellator, MOO_X0 + offset, MOO_Y0 + offset, MOO_Z1 - offset, tileX1, 1.0);
            TessellatorAPI.addVertexWithUV(tessellator, MOO_X0 + offset, MOO_Y1 + offset, MOO_Z1 - offset, tileX1, 0.0);
            TessellatorAPI.draw(tessellator);
            */ //TODO
        }
        return overlayActive;
    }

    public static void finishMooshroom() {
        overlayCounter = 0;
        overlayActive = false;
    }

    public static boolean renderSnowmanOverlay(LivingEntity entity) {
        if (!haveSnowman) {
            return false;
        }
        /*
        TexturePackAPI.bindTexture(MobRandomizer.randomTexture(entity, SNOWMAN_OVERLAY));
        Tessellator tessellator = TessellatorAPI.getTessellator();
        TessellatorAPI.startDrawingQuads(tessellator);

        double[] c = new double[4];

        // bottom = y-
        getTileCoordinates(0, c);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X1, SNOW_Y0, SNOW_Z0, c[0], c[2]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X1, SNOW_Y0, SNOW_Z1, c[1], c[2]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X0, SNOW_Y0, SNOW_Z1, c[1], c[3]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X0, SNOW_Y0, SNOW_Z0, c[0], c[3]);

        // top = y+
        getTileCoordinates(1, c);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X0, SNOW_Y1, SNOW_Z0, c[0], c[2]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X0, SNOW_Y1, SNOW_Z1, c[1], c[2]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X1, SNOW_Y1, SNOW_Z1, c[1], c[3]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X1, SNOW_Y1, SNOW_Z0, c[0], c[3]);

        // back = x-
        getTileCoordinates(2, c);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X0, SNOW_Y1, SNOW_Z1, c[0], c[2]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X0, SNOW_Y1, SNOW_Z0, c[1], c[2]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X0, SNOW_Y0, SNOW_Z0, c[1], c[3]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X0, SNOW_Y0, SNOW_Z1, c[0], c[3]);

        // right = z-
        getTileCoordinates(3, c);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X0, SNOW_Y1, SNOW_Z0, c[0], c[2]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X1, SNOW_Y1, SNOW_Z0, c[1], c[2]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X1, SNOW_Y0, SNOW_Z0, c[1], c[3]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X0, SNOW_Y0, SNOW_Z0, c[0], c[3]);

        // front = x+
        getTileCoordinates(4, c);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X1, SNOW_Y1, SNOW_Z0, c[0], c[2]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X1, SNOW_Y1, SNOW_Z1, c[1], c[2]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X1, SNOW_Y0, SNOW_Z1, c[1], c[3]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X1, SNOW_Y0, SNOW_Z0, c[0], c[3]);

        // left = z+
        getTileCoordinates(5, c);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X1, SNOW_Y1, SNOW_Z1, c[0], c[2]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X0, SNOW_Y1, SNOW_Z1, c[1], c[2]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X0, SNOW_Y0, SNOW_Z1, c[1], c[3]);
        TessellatorAPI.addVertexWithUV(tessellator, SNOW_X1, SNOW_Y0, SNOW_Z1, c[0], c[3]);

        TessellatorAPI.draw(tessellator);
            */ //TODO
        return true;
    }

    private static void getTileCoordinates(int tileNum, double[] c) {
        c[0] = (tileNum % 3) / 3.0;
        c[1] = c[0] + 1.0 / 3.0;
        c[2] = (tileNum / 3) / 2.0;
        c[3] = c[2] + 0.5;
    }
}