package org.fabricmcpatcher.sky;

import com.mojang.blaze3d.systems.RenderSystem;
import org.fabricmcpatcher.resource.BlendMethod;
import org.fabricmcpatcher.resource.PropertiesFile;
import org.fabricmcpatcher.utils.Config;
import org.fabricmcpatcher.utils.MCLogger;
import org.fabricmcpatcher.utils.MCPatcherUtils;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;

public class FireworksHelper {

    //TODO: replace layer of the vanilla particles
    public static final ParticleTextureSheet PARTICLE_SHEET_FIREWORKS = new ParticleTextureSheet() {
        @Override
        public BufferBuilder begin(Tessellator tessellator, TextureManager textureManager) {
            RenderSystem.depthMask(true);
            RenderSystem.setShader(ShaderProgramKeys.PARTICLE);
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
            RenderSystem.enableBlend();
            setParticleBlendMethod(this, true);
            return tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        }

        public String toString() {
            return "PARTICLE_SHEET_FIREWORKS";
        }
    };


    private static final ParticleTextureSheet LIT_LAYER = ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;//3;
    private static final ParticleTextureSheet EXTRA_LAYER = PARTICLE_SHEET_FIREWORKS;//LIT_LAYER + 1;
    private static final String PARTICLE_PROPERTIES = "particle.properties";

    private static final MCLogger logger = MCLogger.getLogger(MCPatcherUtils.BETTER_SKIES);
    private static final boolean enable = Config.getBoolean(MCPatcherUtils.BETTER_SKIES, "brightenFireworks", true);
    private static BlendMethod blendMethod=null;

    /*
    public static ParticleTextureSheet getFXLayer(Particle entity) {
        if (enable && (entity instanceof FireworksSparkParticle.Explosion || entity instanceof FireworksSparkParticle.Flash)) {
            return EXTRA_LAYER;
        } else {
            return entity.getType();
        }
    }
    */

    public static boolean skipThisLayer(boolean skip, ParticleTextureSheet layer) {
        return skip || layer == LIT_LAYER || (!enable && layer ==EXTRA_LAYER);
    }
    public static ParticleTextureSheet getUsedParticleSheet() {
        if( blendMethod==null)
            return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
        if(!blendMethod.isBlendEnabled())
            return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
        if(blendMethod.isDefaultBlendMode())
            return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;

        return PARTICLE_SHEET_FIREWORKS;
    }

    private static void setParticleBlendMethod(ParticleTextureSheet layer, boolean setDefault) {
        if (enable && layer == EXTRA_LAYER && blendMethod != null) {
            blendMethod.applyBlending();
        } else if (setDefault) {
            RenderSystem.defaultBlendFunc();
        }
    }

    static void reload() {
        PropertiesFile properties = PropertiesFile.getNonNull(logger, PARTICLE_PROPERTIES);
        String blend = properties.getString("blend.4", "add");
        blendMethod = BlendMethod.parse(blend);
        if (blendMethod == null) {
            properties.error("%s: unknown blend method %s", PARTICLE_PROPERTIES, blend);
        } else if (enable) {
            properties.config("using %s blending for fireworks particles", blendMethod);
        } else {
            properties.config("using default blending for fireworks particles");
        }
    }
}