package org.fabricmcpatcher.cit;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.util.Identifier;
import org.fabricmcpatcher.resource.BlendMethod;
import org.fabricmcpatcher.resource.IconAPI;
import org.fabricmcpatcher.resource.PropertiesFile;
import org.fabricmcpatcher.resource.TexturePackAPI;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;

final class Enchantment extends OverrideBase {
    private static final float ITEM_2D_THICKNESS = 0.0625f;

    static float baseArmorWidth;
    static float baseArmorHeight;

    final int layer;
    final BlendMethod blendMethod;
    private final float rotation;
    private final double speed;
    final float duration;

    private boolean armorScaleSet;
    private float armorScaleX;
    private float armorScaleY;

    private CITUtils.GlintTextureInfo armorGlintInfo;
    private CITUtils.GlintTextureInfo guiGlintInfo;

    static void beginOuter2D() {
        RenderSystem.enableBlend();
        RenderSystem.depthFunc(GL11.GL_EQUAL);
        RenderSystem.depthMask(false);
    }

    static void endOuter2D() {
        RenderSystem.disableBlend();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.depthMask(true);
        //GLAPI.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    static void beginOuter3D() {
        RenderSystem.enableBlend();
        RenderSystem.depthFunc(GL11.GL_EQUAL);
        //GLAPI.glAlphaFunc(GL11.GL_GREATER, 0.01f);
    }

    static void endOuter3D() {
        //GLAPI.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
    }

    Enchantment(PropertiesFile properties) {
        super(properties);

        if (properties.valid() && textureName == null && alternateTextures == null) {
            properties.error("no source texture specified");
        }

        layer = properties.getInt("layer", 0);
        String value = properties.getString("blend", "add");
        blendMethod = BlendMethod.parse(value);
        if (blendMethod == null) {
            properties.error("unknown blend type %s", value);
        }
        rotation = properties.getFloat("rotation", 0.0f);
        speed = properties.getDouble("speed", 0.0);
        duration = properties.getFloat("duration", 1.0f);

        String valueX = properties.getString("armorScaleX", "");
        String valueY = properties.getString("armorScaleY", "");
        if (!valueX.isEmpty() && !valueY.isEmpty()) {
            try {
                armorScaleX = Float.parseFloat(valueX);
                armorScaleY = Float.parseFloat(valueY);
                armorScaleSet = true;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (!armorScaleSet) {
            setArmorScale();
        }

        this.armorGlintInfo = new CITUtils.GlintTextureInfo(speed,rotation,armorScaleX*0.16f,armorScaleY*0.16f);
        this.guiGlintInfo = new CITUtils.GlintTextureInfo(speed,rotation,8.0f,8.0f);
    }

    @Override
    String getType() {
        return "enchantment";
    }

    void render2D(Tessellator tessellator, float intensity, float x0, float y0, float x1, float y1, float z) {
        /*if (intensity <= 0.0f) {
            return;
        }
        if (intensity > 1.0f) {
            intensity = 1.0f;
        }
        if (!bindTexture(CITUtils.lastOrigIcon)) {
            return;
        }
        begin(intensity);
        TessellatorAPI.startDrawingQuads(tessellator);
        TessellatorAPI.addVertexWithUV(tessellator, x0, y0, z, 0.0f, 0.0f);
        TessellatorAPI.addVertexWithUV(tessellator, x0, y1, z, 0.0f, 1.0f);
        TessellatorAPI.addVertexWithUV(tessellator, x1, y1, z, 1.0f, 1.0f);
        TessellatorAPI.addVertexWithUV(tessellator, x1, y0, z, 1.0f, 0.0f);
        TessellatorAPI.draw(tessellator);
        end();*/
    }

    void render3D(DrawContext context, float intensity, int width, int height) {
        /*if (intensity <= 0.0f) {
            return;
        }
        if (intensity > 1.0f) {
            intensity = 1.0f;
        }
        if (!bindTexture(CITUtils.lastOrigIcon)) {
            return;
        }
        begin(intensity);
        ItemRenderer.renderItemIn2D(tessellator, 1.0f, 0.0f, 0.0f, 1.0f, width, height, ITEM_2D_THICKNESS);
        end();*/
    }

    boolean bindTexture(SpriteContents icon) {
        Identifier texture;
        if (alternateTextures != null && icon != null) {
            texture = alternateTextures.get(IconAPI.getIconName(icon));
            if (texture == null) {
                texture = textureName;
            }
        } else {
            texture = textureName;
        }
        if (texture == null) {
            return false;
        } else {
            CITUtils.boundTex = texture;
            return true;
        }
    }

    void beginArmor(float intensity) {
        /*GL11.glEnable(GL11.GL_BLEND);
        GLAPI.glDepthFunc(GL11.GL_EQUAL);
        GLAPI.glDepthMask(false);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glMatrixMode(GL11.GL_TEXTURE);*/
        begin(intensity);
        //GL11.glScalef(scaleX, scaleY, 1.0f);
        //GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    void endArmor() {
        /*GLAPI.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(GL11.GL_BLEND);
        GLAPI.glDepthFunc(GL11.GL_LEQUAL);
        GLAPI.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        end();
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);*/
    }

    void begin(float intensity) {
        CITUtils.boundBlending = blendMethod.TRANSPARENCY_TYPE;
        //blendMethod.applyBlending();
        //blendMethod.applyDepthFunc();
        CITUtils.boundFade =  blendMethod.getFade(intensity);
        CITUtils.boundGlintInfo = this.armorGlintInfo;
        CITUtils.boundGlintInfoGui = this.guiGlintInfo;
        /*GL11.glPushMatrix();
        if (speed != 0.0) {
            double offset = ((double) System.currentTimeMillis() * speed) / 3000.0;
            offset -= Math.floor(offset);
            GL11.glTranslatef((float) offset * 8.0f, 0.0f, 0.0f);
        }
        GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f);*/
    }

    void end() {
        //GL11.glPopMatrix();
    }

    private void setArmorScale() {
        armorScaleSet = true;
        armorScaleX = 1.0f;
        armorScaleY = 0.5f;
        BufferedImage overlayImage = TexturePackAPI.getImage(textureName);
        if (overlayImage != null) {
            if (overlayImage.getWidth() < baseArmorWidth) {
                armorScaleX *= baseArmorWidth / (float) overlayImage.getWidth();
            }
            if (overlayImage.getHeight() < baseArmorHeight) {
                armorScaleY *= baseArmorHeight / (float) overlayImage.getHeight();
            }
        }
        logger.finer("%s: scaling by %.3fx%.3f for armor model", this, armorScaleX, armorScaleY);
    }
}