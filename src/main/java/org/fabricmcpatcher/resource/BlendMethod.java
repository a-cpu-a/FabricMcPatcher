package org.fabricmcpatcher.resource;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

public class BlendMethod {
    private static final Set<Identifier> blankResources = new HashSet<Identifier>();

    public static final BlendMethod ALPHA = new BlendMethod("alpha", GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, true, false, true, 0);
    public static final BlendMethod ADD = new BlendMethod("add", GL11.GL_SRC_ALPHA, GL11.GL_ONE, true, false, true, 0);
    public static final BlendMethod SUBTRACT = new BlendMethod("subtract", GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO, true, true, false, 0);
    public static final BlendMethod MULTIPLY = new BlendMethod("multiply", GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA, true, true, true, 0xffffffff);
    public static final BlendMethod DODGE = new BlendMethod("dodge", GL11.GL_ONE, GL11.GL_ONE, true, true, false, 0);
    public static final BlendMethod BURN = new BlendMethod("burn", GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_COLOR, true, true, false, null);
    public static final BlendMethod SCREEN = new BlendMethod("screen", GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_COLOR, true, true, false, 0xffffffff);
    public static final BlendMethod OVERLAY = new BlendMethod("overlay", GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR, true, true, false, 0x80808080);
    public static final BlendMethod REPLACE = new BlendMethod("replace", 0, 0, false, false, true, null);

    private final int srcBlend;
    private final int dstBlend;
    private final String name;
    private final boolean blend;
    private final boolean fadeRGB;
    private final boolean fadeAlpha;
    private final Identifier blankResource;

    public static BlendMethod parse(String text) {
        text = text.toLowerCase().trim();
        if (text.equals("alpha")) {
            return ALPHA;
        } else if (text.equals("add")) {
            return ADD;
        } else if (text.equals("subtract")) {
            return SUBTRACT;
        } else if (text.equals("multiply")) {
            return MULTIPLY;
        } else if (text.equals("dodge")) {
            return DODGE;
        } else if (text.equals("burn")) {
            return BURN;
        } else if (text.equals("screen")) {
            return SCREEN;
        } else if (text.equals("overlay") || text.equals("color")) {
            return OVERLAY;
        } else if (text.equals("replace") || text.equals("none")) {
            return REPLACE;
        } else {
            String[] tokens = text.split("\\s+");
            if (tokens.length >= 2) {
                try {
                    int srcBlend = Integer.parseInt(tokens[0]);
                    int dstBlend = Integer.parseInt(tokens[1]);
                    return new BlendMethod("custom(" + srcBlend + "," + dstBlend + ")", srcBlend, dstBlend, true, true, false, 0);
                } catch (NumberFormatException e) {
                }
            }
        }
        return null;
    }

    public static Set<Identifier> getAllBlankResources() {
        return blankResources;
    }

    private BlendMethod(String name, int srcBlend, int dstBlend, boolean blend, boolean fadeRGB, boolean fadeAlpha, Integer neutralRGB) {
        this.name = name;
        this.srcBlend = srcBlend;
        this.dstBlend = dstBlend;
        this.blend = blend;
        this.fadeRGB = fadeRGB;
        this.fadeAlpha = fadeAlpha;
        if (neutralRGB == null) {
            blankResource = null;
        } else {
            //TODO: create a mipmap sized png, with the neutral color, or maybe just put 32x32 textures inside resources
            blankResource=null;
            //String filename = String.format(MCPatcherUtils.BLANK_PNG_FORMAT, neutralRGB);
            //blankResource = TexturePackAPI.newMCPatcherIdentifier(filename);
        }
        if (blankResource != null) {
            blankResources.add(blankResource);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public void applyFade(float fade) {
        //TODO: change vertex colors instead?
        if (fadeRGB && fadeAlpha) {
            RenderSystem.setShaderColor(fade, fade,fade,fade);
            //GLAPI.glColor4f(fade, fade, fade, fade);
        } else if (fadeRGB) {
            RenderSystem.setShaderColor(fade, fade,fade, 1.0F);
            //GLAPI.glColor4f(fade, fade, fade, 1.0f);
        } else if (fadeAlpha) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, fade);
            //GLAPI.glColor4f(1.0f, 1.0f, 1.0f, fade);
        } else {
            //no fading
            RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
        }
    }

    public void applyAlphaTest() {
        if (blend) {
            RenderSystem.disableBlend();
        } else {
            RenderSystem.enableBlend();
            //GLAPI.glAlphaFunc(GL11.GL_GREATER, 0.01f); //TODO: should we do anything about this? 0.01 is less than modern mc, which uses 0.1
        }
    }

    public void applyDepthFunc() {
        if (blend) {
            RenderSystem.depthFunc(GL11.GL_EQUAL);
        } else {
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            RenderSystem.depthMask(true);
        }
    }

    public void applyBlending() {
        if (blend) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(srcBlend, dstBlend, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        } else {
            RenderSystem.disableBlend();
        }
    }

    public boolean isColorBased() {
        return fadeRGB;
    }

    public boolean canFade() {
        return blend && (fadeAlpha || fadeRGB);
    }

    public Identifier getBlankResource() {
        return blankResource;
    }

    public boolean isBlendEnabled() {
        return blend;
    }
    public boolean isDefaultBlendMode() {
        return srcBlend==GL11.GL_SRC_ALPHA && dstBlend==GL11.GL_ONE_MINUS_SRC_ALPHA;
    }
}