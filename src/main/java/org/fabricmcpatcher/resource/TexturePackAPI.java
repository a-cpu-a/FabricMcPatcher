package org.fabricmcpatcher.resource;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.fabricmcpatcher.FabricMcPatcher;
import org.fabricmcpatcher.utils.MAL;
import org.fabricmcpatcher.utils.MCLogger;
import org.fabricmcpatcher.utils.MCPatcherUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class TexturePackAPI {
    private static final MCLogger logger = MCLogger.getLogger("Texture Pack");

    public static final String DEFAULT_NAMESPACE = "minecraft";

    private static final TexturePackAPI instance = new TexturePackAPI();//MAL.newInstance(TexturePackAPI.class, "texturepack");

    //public static final String MCPATCHER_SUBDIR = TexturePackAPI.select("/", "mcpatcher/");
    //public static final Identifier ITEMS_PNG = new Identifier(TexturePackAPI.select("/gui/items.png", GLAPI.select("textures/atlas/items.png", "textures/atlas/blocks.png")));
    //public static final Identifier BLOCKS_PNG = new Identifier(TexturePackAPI.select("terrain.png", "textures/atlas/blocks.png"));

    public static boolean isInitialized() {
        return instance != null && instance.isInitialized_Impl();
    }

    public static void scheduleTexturePackRefresh() {
        MinecraftClient.getInstance().reloadResourcesConcurrently();
    }

    public static List<ResourcePack> getResourcePacks(String namespace) {
        List<ResourcePack> list = new ArrayList<ResourcePack>();
        instance.getResourcePacks_Impl(namespace, list);
        return list;
    }

    public static Set<String> getNamespaces() {
        Set<String> set = new HashSet<String>();
        set.add(DEFAULT_NAMESPACE);

        ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
        if(resourceManager==null)return set;

        set.addAll(resourceManager.getAllNamespaces());
        return set;
    }

    public static InputStream getInputStream(Identifier resource) {
        try {
            /*
            if (resource instanceof IdentifierWithSource) {
                try {
                    return instance.getInputStream_Impl(((IdentifierWithSource) resource).getSource(), resource);
                } catch (IOException e) {
                }
            }*/
            return resource == null ? null : instance.getInputStream_Impl(resource);
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean hasResource(Identifier resource) {
        if (resource == null) {
            return false;
        } else if (resource.getPath().endsWith(".png")) {
            return getImage(resource) != null;
        } else if (resource.getPath().endsWith(".properties")) {
            return getProperties(resource) != null;
        } else {
            InputStream is = getInputStream(resource);
            MCPatcherUtils.close(is);
            return is != null;
        }
    }

    public static boolean hasCustomResource(Identifier resource) {
        InputStream jar = null;
        InputStream pack = null;
        try {
            String path = instance.getFullPath_Impl(resource);
            pack = getInputStream(resource);
            if (pack == null) {
                return false;
            }
            jar = MinecraftClient.class.getResourceAsStream(path);
            if (jar == null) {
                return true;
            }
            byte[] buffer1 = new byte[4096];
            byte[] buffer2 = new byte[4096];
            int read1;
            int read2;
            while ((read1 = pack.read(buffer1)) > 0) {
                read2 = jar.read(buffer2);
                if (read1 != read2) {
                    return true;
                }
                for (int i = 0; i < read1; i++) {
                    if (buffer1[i] != buffer2[i]) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            MCPatcherUtils.close(jar);
            MCPatcherUtils.close(pack);
        }
        return false;
    }

    public static BufferedImage getImage(Identifier resource) {
        if (resource == null) {
            return null;
        }
        InputStream input = getInputStream(resource);
        BufferedImage image = null;
        if (input != null) {
            try {
                image = ImageIO.read(input);
            } catch (IOException e) {
                logger.error("could not read %s", resource);
                e.printStackTrace();
            } finally {
                MCPatcherUtils.close(input);
            }
        }
        return image;
    }

    public static Properties getProperties(Identifier resource) {
        Properties properties = new Properties();
        if (getProperties(resource, properties)) {
            return properties;
        } else {
            return null;
        }
    }

    public static boolean getProperties(Identifier resource, Properties properties) {
        if (properties != null) {
            InputStream input = getInputStream(resource);
            try {
                if (input != null) {
                    properties.load(input);
                    return true;
                }
            } catch (IOException e) {
                logger.error("could not read %s", resource);
                e.printStackTrace();
            } finally {
                MCPatcherUtils.close(input);
            }
        }
        return false;
    }

    public static Identifier transformIdentifier(Identifier resource, String oldExt, String newExt) {
        return Identifier.of(resource.getNamespace(), resource.getPath().replaceFirst(Pattern.quote(oldExt) + "$", newExt));
    }

    public static Identifier parsePath(String path) {
        return MCPatcherUtils.isNullOrEmpty(path) ? null : instance.parsePath_Impl(path.replace(File.separatorChar, '/'));
    }

    public static Identifier parseIdentifier(String path) {
        return parseIdentifier(Identifier.ofVanilla("a"), path);
    }

    public static Identifier parseIdentifier(Identifier baseResource, String path) {
        return MCPatcherUtils.isNullOrEmpty(path) ? null : instance.parseIdentifier_Impl(baseResource, path);
    }

    public static <T> T select(T v1, T v2) {
        return instance.select_Impl(v1, v2);
    }

    public static Identifier newMCPatcherIdentifierNS(String ns,String path) {
        path = path.replaceFirst("^/+", "");
        Identifier fallback = null;
        for (String folder : FabricMcPatcher.CHECK_FOLDERS) {
            try {
                fallback = Identifier.of(ns,folder+path);
            }
            catch (InvalidIdentifierException ignored) {
                fallback = Identifier.of(ns,folder+path.toLowerCase());//fallback, for some old assets (damageBoost, ...)
            }
            Optional<Resource> testRes =  MinecraftClient.getInstance().getResourceManager().getResource(fallback);
            if(testRes.isEmpty())
                continue;

            return fallback;
        }
        return fallback;
        //return new Identifier(MCPATCHER_SUBDIR + path.replaceFirst("^/+", ""));
    }
    public static Identifier newMCPatcherIdentifier(String path) {
        return newMCPatcherIdentifierNS("minecraft",path);
    }
    public static Identifier newMCPatcherIdentifier(String v1Path, String v2Path) {
        return newMCPatcherIdentifier(select(v1Path, v2Path));
    }


    public static int getTextureIfLoaded(Identifier resource) {
        return resource == null ? -1 : instance.getTextureIfLoaded_Impl(resource);
    }

    public static boolean isTextureLoaded(Identifier resource) {
        return getTextureIfLoaded(resource) >= 0;
    }
/*
    public static TextureObject getTextureObject(Identifier resource) {
        return MinecraftClient.getInstance().getTextureManager().getTexture(resource);
    }*/
/*
    public static void bindTexture(Identifier resource) {
        if (resource != null) {
            instance.bindTexture_Impl(resource);
        }
    }*/

    public static void unloadTexture(Identifier resource) {
        if (resource != null) {
            instance.unloadTexture_Impl(resource);
        }
    }

    public static void flushUnusedTextures() {
        instance.flushUnusedTextures_Impl();
    }

    private static final String ASSETS = "assets/";

    private ResourceManager getResourceManager() {
        return MinecraftClient.getInstance().getResourceManager();
    }

    protected boolean isInitialized_Impl() {
        return getResourceManager() != null;
    }

    protected void getResourcePacks_Impl(String namespace, List<ResourcePack> resourcePacks) {
        assert namespace==null;

        resourcePacks.addAll(MinecraftClient.getInstance().getResourceManager().streamResourcePacks().toList());

        /*ResourceManager resourceManager = getResourceManager();
        if (resourceManager instanceof LifecycledResourceManagerImpl) {
            for (Map.Entry<String, FallbackResourceManager> entry : ((LifecycledResourceManagerImpl) resourceManager).namespaceMap.entrySet()) {
                if (namespace == null || namespace.equals(entry.getKey())) {
                    List<ResourcePack> packs = entry.getValue().resourcePacks;
                    if (packs != null) {
                        resourcePacks.removeAll(packs);
                        resourcePacks.addAll(packs);
                    }
                }
            }
        }*/
    }


    protected InputStream getInputStream_Impl(Identifier resource) throws IOException {
        Optional<Resource> res = MinecraftClient.getInstance().getResourceManager().getResource(resource);
        if(res.isEmpty())
            throw new IOException();
        return res.get().getInputStream();
    }
/*
    protected InputStream getInputStream_Impl(ResourcePack resourcePack, Identifier resource) throws IOException {
        return resourcePack.getInputStream(resource);
    }*/

    protected String getFullPath_Impl(Identifier resource) {
        return ASSETS + resource.getNamespace() + "/" + resource.getPath();
    }

    protected Identifier parsePath_Impl(String path) {
        if (path.startsWith(ASSETS)) {
            path = path.substring(ASSETS.length());
            int slash = path.indexOf('/');
            if (slash > 0 && slash + 1 < path.length()) {
                return Identifier.of(path.substring(0, slash), path.substring(slash + 1));
            }
        }
        return null;
    }

    protected Identifier parseIdentifier_Impl(Identifier baseResource, String path) {
        boolean absolute = false;
        if (path.startsWith("%blur%")) {
            path = path.substring(6);
        }
        if (path.startsWith("%clamp%")) {
            path = path.substring(7);
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
            absolute = true;
        }
        if (path.startsWith("assets/minecraft/")) {
            path = path.substring(17);
            absolute = true;
        }
        // Absolute path, including namespace:
        // namespace:path/filename -> assets/namespace/path/filename
        int colon = path.indexOf(':');
        if (colon >= 0) {
            return Identifier.of(path.substring(0, colon), path.substring(colon + 1));
        }
        Identifier resource;
        if (path.startsWith("~/")) {
            // Relative to namespace mcpatcher dir:
            // ~/path -> assets/(namespace of base file)/mcpatcher/path
            String type = baseResource.getPath().substring(0,baseResource.getPath().indexOf('/')+1);//either "mcpatcher/" or "optifine/"
            resource = Identifier.of(baseResource.getNamespace(), type + path.substring(2));
        } else if (path.startsWith("./")) {
            // Relative to properties file:
            // ./path -> (dir of base file)/path
            resource = Identifier.of(baseResource.getNamespace(), baseResource.getPath().replaceFirst("[^/]+$", "") + path.substring(2));
        } else if (!absolute && !path.contains("/")) {
            // Relative to properties file:
            // filename -> (dir of base file)/filename
            resource = Identifier.of(baseResource.getNamespace(), baseResource.getPath().replaceFirst("[^/]+$", "") + path);
        } else {
            // Absolute path, w/o namespace:
            // path/filename -> assets/(namespace of base file)/path/filename
            resource = Identifier.of(baseResource.getNamespace(), path);
        }
        return resource;
    }

    protected <T> T select_Impl(T unused, T v2) {
        return v2;
    }

    protected int getTextureIfLoaded_Impl(Identifier resource) {
        AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().getTexture(resource);
        return texture==null?-1:texture.getGlId();
    }

/*
    protected void bindTexture_Impl(Identifier resource) {
        MinecraftClient.getInstance().getTextureManager().getTexture(resource).bindTexture();
    }*/

    protected void unloadTexture_Impl(Identifier resource) {
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        AbstractTexture texture = textureManager.getTexture(resource);
        if (texture != null && !(texture instanceof SpriteAtlasTexture) && !(texture instanceof NativeImageBackedTexture)) {
            //((AbstractTexture) texture).unloadGLTexture();
            logger.finer("unloading texture %s", resource);
            //textureManager.texturesByName.remove(resource);
            textureManager.destroyTexture(resource);
        }
    }

    protected void flushUnusedTextures_Impl() {
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        //TODO: is this required?
        /*if (textureManager != null) {
            Set<Identifier> texturesToUnload = new HashSet<Identifier>();
            for (Map.Entry<Identifier, TextureObject> entry : textureManager.texturesByName.entrySet()) {
                Identifier resource = entry.getKey();
                TextureObject texture = entry.getValue();
                if (texture instanceof SimpleTexture && !(texture instanceof ThreadDownloadImageData) && !TexturePackAPI.hasResource(resource)) {
                    texturesToUnload.add(resource);
                }
            }
            for (Identifier resource : texturesToUnload) {
                unloadTexture(resource);
            }
        }*/
    }
}