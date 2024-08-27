package org.fabricmcpatcher.sky;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.fabricmcpatcher.resource.BlendMethod;
import org.fabricmcpatcher.resource.PropertiesFile;
import org.fabricmcpatcher.resource.TexturePackAPI;
import org.fabricmcpatcher.resource.TexturePackChangeHandler;
import org.fabricmcpatcher.utils.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class SkyRenderer {
    private static final MCLogger logger = MCLogger.getLogger(MCPatcherUtils.BETTER_SKIES);

    private static final boolean enable = Config.getBoolean(MCPatcherUtils.BETTER_SKIES, "skybox", true);
    private static final boolean unloadTextures = Config.getBoolean(MCPatcherUtils.BETTER_SKIES, "unloadTextures", true);
    public static final double horizonHeight = Config.getInt(MCPatcherUtils.BETTER_SKIES, "horizon", 16);

    private static double worldTime;
    private static float celestialAngle;
    private static float rainStrength;

    private static final HashMap<Integer, WorldEntry> worldSkies = new HashMap<Integer, WorldEntry>();
    private static WorldEntry currentWorld;

    public static boolean active;
    public static Identifier sunTex=null;
    public static Identifier moonTex=null;

    static {

        TexturePackChangeHandler.register(new TexturePackChangeHandler(MCPatcherUtils.BETTER_SKIES, 2) {
            @Override
            public void beforeChange() {
                worldSkies.clear();
            }

            @Override
            public void afterChange() {
                if (enable) {
                    ClientWorld world = MinecraftClient.getInstance().world;
                    if (world != null) {
                        getWorldEntry(PortUtils.getWorldId(world));
                    }
                }
                FireworksHelper.reload();
            }
        });
    }

    public static void setup(ClientWorld world, float partialTick, float celestialAngle) {
        int worldType = PortUtils.getWorldId(world);
        WorldEntry newEntry = getWorldEntry(worldType);
        if (newEntry != currentWorld && currentWorld != null) {
            currentWorld.unloadTextures();
        }
        currentWorld = newEntry;
        active = currentWorld.active();
        if (active) {
            worldTime = world.getTimeOfDay() + partialTick;
            rainStrength = 1.0f - world.getRainGradient(partialTick);
            SkyRenderer.celestialAngle = celestialAngle;
        }
    }

    public static boolean renderAll(float partialTick) {
        if (active) {
            ClientWorld world = MinecraftClient.getInstance().world;
            float rot = world.getSkyAngle(partialTick);

            setup(world, partialTick,rot);
            currentWorld.renderAll(Tessellator.getInstance());

            int phase = world.getMoonPhase();
            float alpha = 1.0f - world.getRainGradient(partialTick);
            float starBrightness = world.getStarBrightness(partialTick)*alpha;

            MatrixStack matrices = new MatrixStack();

            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rot * 360.0F));

            SkyRendering sky = MinecraftClient.getInstance().worldRenderer.skyRendering;

            SkyRenderer.sunTex = setupCelestialObject(Identifier.ofVanilla("textures/environment/sun.png"));
            sky.renderSun(alpha,Tessellator.getInstance(),matrices);
            SkyRenderer.moonTex = setupCelestialObject(Identifier.ofVanilla("textures/environment/moon_phases.png"));
            sky.renderMoon(phase,alpha,Tessellator.getInstance(),matrices);

            if (starBrightness > 0.0F) {
                sky.renderStars(RenderSystem.getShaderFog(), starBrightness, matrices);
            }

            matrices.pop();

        } else {
            sunTex=null;
            moonTex=null;
        }
        return active;
    }

    public static Identifier setupCelestialObject(Identifier defaultTexture) {
        if (active) {
            //Layer.clearBlendingMethod();
            //RenderSystem.setShaderColor(1.0f,1.0f,1.0f,rainStrength);
            //TODO: color alpha with rainStrength
            Layer layer = currentWorld.getCelestialObject(defaultTexture);
            if (layer != null) {
                layer.setBlendingMethod(rainStrength);
                return layer.texture;
            }
        }
        return null;
    }

    private static WorldEntry getWorldEntry(int worldType) {
        WorldEntry entry = worldSkies.get(worldType);
        if (entry == null) {
            entry = new WorldEntry(worldType);
            worldSkies.put(worldType, entry);
        }
        return entry;
    }

    private static class WorldEntry {
        private final int worldType;
        private final List<Layer> skies = new ArrayList<Layer>();
        private final Map<Identifier, Layer> objects = new HashMap<Identifier, Layer>();
        private final Set<Identifier> textures = new HashSet<Identifier>();

        WorldEntry(int worldType) {
            this.worldType = worldType;
            loadSkies();
            loadCelestialObject("sun");
            loadCelestialObject("moon_phases");
        }

        private void loadSkies() {
            for (int i = -1; ; i++) {
                String v1Path = "/environment/sky" + worldType + "/sky" + (i < 0 ? "" : String.valueOf(i)) + ".properties";
                String v2Path = "sky/world" + worldType + "/sky" + (i < 0 ? "" : String.valueOf(i)) + ".properties";
                Identifier resource = TexturePackAPI.newMCPatcherIdentifier(v1Path, v2Path);
                Layer layer = Layer.create(resource);
                if (layer == null) {
                    if (i > 0) {
                        break;
                    }
                } else if (layer.properties.valid()) {
                    logger.fine("loaded %s", resource);
                    skies.add(layer);
                    textures.add(layer.texture);
                }
            }
        }

        private void loadCelestialObject(String objName) {
            Identifier textureName = Identifier.ofVanilla("textures/environment/" + objName + ".png");
            String v1Path = "/environment/sky" + worldType + "/" + objName + ".properties";
            String v2Path = "sky/world" + worldType + "/" + objName + ".properties";
            Identifier resource = TexturePackAPI.newMCPatcherIdentifier(v1Path,v2Path);
            PropertiesFile properties = PropertiesFile.get(logger, resource);
            if (properties != null) {
                properties.setProperty("fade", "false");
                properties.setProperty("rotate", "true");
                Layer layer = new Layer(properties);
                if (properties.valid()) {
                    logger.fine("using %s (%s) for the %s", resource, layer.texture, objName);
                    objects.put(textureName, layer);
                }
            }
        }

        boolean active() {
            return !skies.isEmpty() || !objects.isEmpty();
        }

        void renderAll(Tessellator tessellator) {
            if (unloadTextures) {
                Set<Identifier> texturesNeeded = new HashSet<Identifier>();
                for (Layer layer : skies) {
                    if (layer.prepare()) {
                        texturesNeeded.add(layer.texture);
                    }
                }
                Set<Identifier> texturesToUnload = new HashSet<Identifier>();
                texturesToUnload.addAll(textures);
                texturesToUnload.removeAll(texturesNeeded);
                for (Identifier resource : texturesToUnload) {
                    TexturePackAPI.unloadTexture(resource);
                }
            }
            RenderSystem.depthMask(false);
            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0F);
            for (Layer layer : skies) {
                if (!unloadTextures) {
                    layer.prepare();
                }
                if (layer.brightness > 0.0f) {
                    layer.render(tessellator);
                    Layer.clearBlendingMethod();
                    //TODO: color alpha with rainStrength
                }
            }
            RenderSystem.depthMask(true);
        }

        Layer getCelestialObject(Identifier defaultTexture) {
            return objects.get(defaultTexture);
        }

        void unloadTextures() {
            for (Layer layer : skies) {
                TexturePackAPI.unloadTexture(layer.texture);
            }
        }
    }


    private static class Layer {
        private static final int SECS_PER_DAY = 24 * 60 * 60;
        private static final int TICKS_PER_DAY = 24000;
        private static final double TOD_OFFSET = -0.25;
        private static VertexBuffer skyBuffer=null;

        private static final float SKY_DISTANCE = 100.0f;

        private final PropertiesFile properties;
        private Identifier texture;
        private boolean fade;
        private boolean rotate;
        private float[] axis;
        private float speed;
        private BlendMethod blendMethod;

        private double a;
        private double b;
        private double c;

        float brightness;

        static Layer create(Identifier resource) {
            PropertiesFile properties = PropertiesFile.get(logger, resource);
            if (properties == null) {
                return null;
            } else {
                return new Layer(properties);
            }
        }

        Layer(PropertiesFile properties) {
            this.properties = properties;
            boolean valid = (readTexture() && readRotation() & readBlendingMethod() && readFadeTimers());
            if(skyBuffer==null) {
                skyBuffer = createSkyBuffer();
            }
        }

        private boolean readTexture() {
            texture = properties.getIdentifier("source", properties.toString().replaceFirst("\\.properties$", ".png"));
            if (TexturePackAPI.hasResource(texture)) {
                return true;
            } else {
                return properties.error("source texture %s not found", texture);
            }
        }

        private boolean readRotation() {
            rotate = properties.getBoolean("rotate", true);
            if (rotate) {
                speed = properties.getFloat("speed", 1.0f);

                String value = properties.getString("axis", "0.0 0.0 1.0");
                String[] tokens = value.split("\\s+");
                if (tokens.length == 3) {
                    float x;
                    float y;
                    float z;
                    try {
                        x = Float.parseFloat(tokens[0]);
                        y = Float.parseFloat(tokens[1]);
                        z = Float.parseFloat(tokens[2]);
                    } catch (NumberFormatException e) {
                        return properties.error("invalid rotation axis");
                    }
                    if (x * x + y * y + z * z == 0.0f) {
                        return properties.error("rotation axis cannot be 0");
                    }
                    axis = new float[]{z, y, -x};
                } else {
                    return properties.error("invalid rotate value %s", value);
                }
            }
            return true;
        }

        private boolean readBlendingMethod() {
            String value = properties.getString("blend", "add");
            blendMethod = BlendMethod.parse(value);
            if (blendMethod == null) {
                return properties.error("unknown blend method %s", value);
            }
            return true;
        }

        private boolean readFadeTimers() {
            fade = properties.getBoolean("fade", true);
            if (!fade) {
                return true;
            }
            int startFadeIn = parseTime(properties, "startFadeIn");
            int endFadeIn = parseTime(properties, "endFadeIn");
            int endFadeOut = parseTime(properties, "endFadeOut");
            if (!properties.valid()) {
                return false;
            }
            while (endFadeIn <= startFadeIn) {
                endFadeIn += SECS_PER_DAY;
            }
            while (endFadeOut <= endFadeIn) {
                endFadeOut += SECS_PER_DAY;
            }
            if (endFadeOut - startFadeIn >= SECS_PER_DAY) {
                return properties.error("fade times must fall within a 24 hour period");
            }
            int startFadeOut = startFadeIn + endFadeOut - endFadeIn;

            // f(x) = a cos x + b sin x + c
            // f(s0) = 0
            // f(s1) = 1
            // f(e1) = 0
            // Solve for a, b, c using Cramer's rule.
            double s0 = normalize(startFadeIn, SECS_PER_DAY, TOD_OFFSET);
            double s1 = normalize(endFadeIn, SECS_PER_DAY, TOD_OFFSET);
            double e0 = normalize(startFadeOut, SECS_PER_DAY, TOD_OFFSET);
            double e1 = normalize(endFadeOut, SECS_PER_DAY, TOD_OFFSET);
            double det = Math.cos(s0) * Math.sin(s1) + Math.cos(e1) * Math.sin(s0) + Math.cos(s1) * Math.sin(e1) -
                Math.cos(s0) * Math.sin(e1) - Math.cos(s1) * Math.sin(s0) - Math.cos(e1) * Math.sin(s1);
            if (det == 0.0) {
                return properties.error("determinant is 0");
            }
            a = (Math.sin(e1) - Math.sin(s0)) / det;
            b = (Math.cos(s0) - Math.cos(e1)) / det;
            c = (Math.cos(e1) * Math.sin(s0) - Math.cos(s0) * Math.sin(e1)) / det;

            logger.finer("%s: y = %f cos x + %f sin x + %f", properties, a, b, c);
            logger.finer("  at %f: %f", s0, f(s0));
            logger.finer("  at %f: %f", s1, f(s1));
            logger.finer("  at %f: %f", e0, f(e0));
            logger.finer("  at %f: %f", e1, f(e1));
            return true;
        }

        private int parseTime(PropertiesFile properties, String key) {
            String s = properties.getString(key, "");
            if ("".equals(s)) {
                properties.error("missing value for %s", key);
                return -1;
            }
            String[] t = s.split(":");
            if (t.length >= 2) {
                try {
                    int hh = Integer.parseInt(t[0].trim());
                    int mm = Integer.parseInt(t[1].trim());
                    int ss;
                    if (t.length >= 3) {
                        ss = Integer.parseInt(t[2].trim());
                    } else {
                        ss = 0;
                    }
                    return (60 * 60 * hh + 60 * mm + ss) % SECS_PER_DAY;
                } catch (NumberFormatException e) {
                }
            }
            properties.error("invalid %s time %s", key, s);
            return -1;
        }

        private static double normalize(double time, int period, double offset) {
            return 2.0 * Math.PI * (time / period + offset);
        }

        private double f(double x) {
            return a * Math.cos(x) + b * Math.sin(x) + c;
        }

        boolean prepare() {
            brightness = rainStrength;
            if (fade) {
                double x = normalize(worldTime, TICKS_PER_DAY, 0.0);
                brightness *= (float) f(x);
            }

            if (brightness <= 0.0f) {
                return false;
            }
            if (brightness > 1.0f) {
                brightness = 1.0f;
            }
            return true;
        }

        boolean render(Tessellator tessellator) {

            /*

		RenderSystem.depthMask(false);
		RenderSystem.setShader(ShaderProgramKeys.POSITION);
		RenderSystem.setShaderColor(red, green, blue, 1.0F);
		this.skyBuffer.bind();
		this.skyBuffer.draw(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
		VertexBuffer.unbind();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.depthMask(true);


            */

            RenderSystem.setShaderTexture(0,texture);
            //TexturePackAPI.bindTexture(texture);
            setBlendingMethod(brightness);


            Matrix4f modelViewMat = RenderSystem.getModelViewMatrix();
            if (rotate) {
                modelViewMat = new Matrix4f(modelViewMat);
                modelViewMat.rotate((float) (celestialAngle * Math.TAU * speed), axis[0], axis[1], axis[2]);
            }

            skyBuffer.bind();
            skyBuffer.draw(modelViewMat, RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
            VertexBuffer.unbind();

            return true;
        }


        private static BuiltBuffer tessellateSky(Tessellator tesselator) {
            BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

            Matrix4fStack stack = new Matrix4fStack(3);

            // north
            stack.rotate(90.0f, 1.0f, 0.0f, 0.0f);
            stack.rotate(-90.0f, 0.0f, 0.0f, 1.0f);
            drawTile(stack,bufferBuilder, 4);

            // top
            stack.pushMatrix();
            stack.rotate(90.0f, 1.0f, 0.0f, 0.0f);
            drawTile(stack,bufferBuilder, 1);
            GL11.glPopMatrix();

            // bottom
            stack.pushMatrix();
            stack.rotate(-90.0f, 1.0f, 0.0f, 0.0f);
            drawTile(stack,bufferBuilder, 0);
            stack.popMatrix();

            // west
            stack.rotate(90.0f, 0.0f, 0.0f, 1.0f);
            drawTile(stack,bufferBuilder, 5);

            // south
            stack.rotate(90.0f, 0.0f, 0.0f, 1.0f);
            drawTile(stack,bufferBuilder, 2);

            // east
            stack.rotate(90.0f, 0.0f, 0.0f, 1.0f);
            drawTile(stack,bufferBuilder, 3);

            return bufferBuilder.end();
        }
        private static VertexBuffer createSkyBuffer() {
            VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            vertexBuffer.bind();
            vertexBuffer.upload(tessellateSky(Tessellator.getInstance()));
            VertexBuffer.unbind();
            return vertexBuffer;
        }

        private static void drawTile(Matrix4f mat, BufferBuilder tessellator, int tile) {
            float tileX = (tile % 3) / 3.0f;
            float tileY = (int)(tile / 3) / 2.0f;
            tessellator.vertex(mat,-SKY_DISTANCE, -SKY_DISTANCE, -SKY_DISTANCE).texture(tileX, tileY);
            tessellator.vertex(mat,-SKY_DISTANCE, -SKY_DISTANCE, SKY_DISTANCE).texture(tileX, tileY + 0.5f);
            tessellator.vertex(mat,SKY_DISTANCE, -SKY_DISTANCE, SKY_DISTANCE).texture(tileX + 1.0f / 3.0f, tileY + 0.5f);
            tessellator.vertex(mat,SKY_DISTANCE, -SKY_DISTANCE, -SKY_DISTANCE).texture(tileX + 1.0f / 3.0f, tileY);
        }

        void setBlendingMethod(float brightness) {
            blendMethod.applyFade(brightness);
            blendMethod.applyAlphaTest();
            blendMethod.applyBlending();
            //GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        static void clearBlendingMethod() {
            RenderSystem.defaultBlendFunc();
            /*GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GLAPI.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GLAPI.glColor4f(1.0f, 1.0f, 1.0f, rainStrength);*/
            RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
        }
    }
}