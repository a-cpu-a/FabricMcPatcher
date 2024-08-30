package org.fabricmcpatcher.resource;


import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.atlas.AtlasSprite;
import net.minecraft.util.Identifier;
import org.fabricmcpatcher.utils.Config;
import org.fabricmcpatcher.utils.MCLogger;
import org.fabricmcpatcher.utils.MCPatcherUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;


/**
 *
 * Allows old code to add textures to the atlas
 *
 * */

//TODO: inject into atlas texture gatherer, and merge into its lists
public class TileLoader {
    private static final MCLogger logger = MCLogger.getLogger("Tilesheet");

    private static final List<TileLoader> loaders = new ArrayList<TileLoader>();

    private static final boolean debugTextures = Config.getBoolean(MCPatcherUtils.CONNECTED_TEXTURES, "debugTextures", false);
    private static final Map<String, String> specialTextures = new HashMap<String, String>();

    private static final TexturePackChangeHandler changeHandler;
    private static boolean changeHandlerCalled;
    private static boolean useFullPath;

    //private static final long MAX_TILESHEET_SIZE;

    protected final String mapName;
    protected final MCLogger subLogger;

    private SpriteAtlasTexture baseTextureMap;
    private final Map<String, SpriteContents> baseTexturesByName = new HashMap<String, SpriteContents>();
    private final Set<Identifier> tilesToRegister = new HashSet<Identifier>();
    private final Map<Identifier, BufferedImage> tileImages = new HashMap<Identifier, BufferedImage>();
    private final Map<String, Sprite> iconMap = new HashMap<String, Sprite>();

    static {
        long maxSize = 4096L;
        try {
            maxSize = RenderSystem.maxSupportedTextureSize();
        } catch (NoSuchMethodError e) {
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //MAX_TILESHEET_SIZE = (maxSize * maxSize * 4) * 7 / 8;
        //logger.config("max texture size is %dx%d (%.1fMB)", maxSize, maxSize, MAX_TILESHEET_SIZE / 1048576.0f);

        changeHandler = new TexturePackChangeHandler("Tilesheet API", 2) {
            @Override
            public void initialize() {
            }

            @Override
            public void beforeChange() {
                changeHandlerCalled = true;
                loaders.clear();
                specialTextures.clear();
                /*try {
                    FaceInfo.clear();
                } catch (NoClassDefFoundError e) {
                    // nothing
                } catch (Throwable e) {
                    e.printStackTrace();
                }*/ //TODO
            }

            @Override
            public void afterChange() {
                for (TileLoader loader : loaders) {
                    if (!loader.tilesToRegister.isEmpty()) {
                        loader.subLogger.warning("could not load all %s tiles (%d remaining)", loader.mapName, loader.tilesToRegister.size());
                        loader.tilesToRegister.clear();
                    }
                }
                changeHandlerCalled = false;
            }

            @Override
            public void afterChange2() {
                for (TileLoader loader : loaders) {
                    loader.finish();
                }
            }
        };
        TexturePackChangeHandler.register(changeHandler);
    }

    /*public static void registerIcons(SpriteAtlasTexture textureMap, String mapName, Map<String, SpriteContents> map) {
        mapName = mapName.replaceFirst("/$", "");
        logger.fine("before registerIcons(%s) %d icons", mapName, map.size());
        if (!changeHandlerCalled) {
            logger.severe("beforeChange was not called, invoking directly");
            changeHandler.beforeChange();
        }
        for (TileLoader loader : loaders) {
            if (loader.isForThisMap(mapName)) {
                if (loader.baseTextureMap == null) {
                    loader.baseTextureMap = textureMap;
                    loader.baseTexturesByName.putAll(map);
                }
                if (!loader.tilesToRegister.isEmpty()) {
                    loader.subLogger.fine("adding icons to %s (%d remaining)", mapName, loader.tilesToRegister.size(), mapName);
                    while (!loader.tilesToRegister.isEmpty() && loader.registerOneIcon(textureMap, mapName, map)) {
                        // nothing
                    }
                    loader.subLogger.fine("done adding icons to %s (%d remaining)", mapName, loader.tilesToRegister.size(), mapName);
                }
            }
        }
        logger.fine("after registerIcons(%s) %d icons", mapName, map.size());
    }

    public static String getOverridePath(String prefix, String basePath, String name, String ext) {
        String path;
        if (name.endsWith(".png")) {
            path = name.replaceFirst("^/", "").replaceFirst("\\.[^.]+$", "") + ext;
            useFullPath = true;
        } else {
            path = basePath;
            if (!basePath.endsWith("/")) {
                path += "/";
            }
            path += name;
            path += ext;
            useFullPath = false;
        }
        path = prefix + path;
        logger.finer("getOverridePath(%s, %s, %s, %s) -> %s", prefix, basePath, name, ext, path);
        return path;
    }

    public static String getOverrideBasename(Object o, String path) {
        if (useFullPath) {
            useFullPath = false;
            return "/" + path;
        } else {
            File file = new File(path);
            return file.getName().substring(0, file.getName().lastIndexOf('.'));
        }
    }

    public static boolean isSpecialTexture(SpriteAtlasTexture map, String texture, String special) {
        return special.equals(texture) || special.equals(specialTextures.get(texture));
    }

    public static BufferedImage generateDebugTexture(String text, int width, int height, boolean alternate) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(alternate ? new Color(0, 255, 255, 128) : Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(alternate ? Color.RED : Color.BLACK);
        int ypos = 10;
        if (alternate) {
            ypos += height / 2;
        }
        int charsPerRow = width / 8;
        if (charsPerRow <= 0) {
            return image;
        }
        while (text.length() % charsPerRow != 0) {
            text += " ";
        }
        while (ypos < height && !text.equals("")) {
            graphics.drawString(text.substring(0, charsPerRow), 1, ypos);
            ypos += graphics.getFont().getSize();
            text = text.substring(charsPerRow);
        }
        return image;
    }
*/

    static void init() {
    }

    public static Identifier getBlocksAtlas() {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    public static Identifier getItemsAtlas() {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
    public TileLoader(String mapName, MCLogger logger) {
        this.mapName = mapName;
        subLogger = logger;
        loaders.add(this);
    }
/*
    private static long getTextureSize(SpriteContents texture) {
        return texture == null ? 0 : 4 * IconAPI.getIconWidth(texture) * IconAPI.getIconHeight(texture);
    }

    private static long getTextureSize(Collection<SpriteContents> textures) {
        long size = 0;
        for (SpriteContents texture : textures) {
            size += getTextureSize(texture);
        }
        return size;
    }
*/
    /** Used to get default image for CIT files */
    public static Identifier getDefaultAddress(Identifier propertiesAddress) {
        return TexturePackAPI.transformIdentifier(propertiesAddress, ".properties", ".png");
    }

    public static Identifier parseTileAddress(Identifier propertiesAddress, String value) {
        return parseTileAddress(propertiesAddress, value, BlendMethod.ALPHA.getBlankResource());
    }

    public static Identifier parseTileAddress(Identifier propertiesAddress, String value, Identifier blankResource) {
        if (value == null) {
            return null;
        }
        if (value.equals("blank")) {
            return blankResource;
        }
        if (value.equals("null") || value.equals("none") || value.equals("default") || value.isEmpty()) {
            return null;
        }
        if (!value.endsWith(".png")) {
            value += ".png";
        }
        return TexturePackAPI.parseIdentifier(propertiesAddress, value);
    }
    /*
        public boolean preloadTile(Identifier resource, boolean alternate, String special) {
            if (tileImages.containsKey(resource)) {
                return true;
            }
            BufferedImage image = null;
            if (!debugTextures) {
                image = TexturePackAPI.getImage(resource);
                if (image == null) {
                    subLogger.warning("missing %s", resource);
                }
            }
            if (image == null) {
                image = generateDebugTexture(resource.getPath(), 64, 64, alternate);
            }
            tilesToRegister.add(resource);
            tileImages.put(resource, image);
            if (special != null) {
                specialTextures.put(resource.toString(), special);
            }
            return true;
        }

        public boolean preloadTile(Identifier resource, boolean alternate) {
            return preloadTile(resource, alternate, null);
        }

        protected boolean isForThisMap(String mapName) {
            return mapName.equals("textures") || mapName.startsWith(this.mapName);
        }

        private boolean registerDefaultIcon(String name) {
            if (name.startsWith(mapName) && name.endsWith(".png") && baseTextureMap != null) {
                String defaultName = name.substring(mapName.length()).replaceFirst("\\.png$", "");
                Sprite texture = iconMap.get(defaultName);
                if (texture != null) {
                    subLogger.finer("%s -> existing icon %s", name, defaultName);
                    iconMap.put(name, texture);
                    return true;
                }
            }
            return false;
        }

        private boolean registerOneIcon(SpriteAtlasTexture textureMap, String mapName, Map<String, SpriteContents> map) {
            Identifier resource = tilesToRegister.iterator().next();
            String name = resource.toString();
            if (registerDefaultIcon(name)) {
                tilesToRegister.remove(resource);
                return true;
            }
            BufferedImage image = tileImages.get(resource);
            if (image == null) {
                subLogger.error("tile for %s unexpectedly missing", resource);
                tilesToRegister.remove(resource);
                return true;
            }
            int width = image.getWidth();
            int height = image.getHeight();
            long currentSize = getTextureSize(map.values());
            long newSize = 4 * width * width;
            if (newSize + currentSize > MAX_TILESHEET_SIZE) {
                float sizeMB = (float) currentSize / 1048576.0f;
                if (currentSize <= 0) {
                    subLogger.error("%s too big for any tilesheet (%.1fMB), dropping", name, sizeMB);
                    tilesToRegister.remove(resource);
                    return true;
                } else {
                    subLogger.warning("%s nearly full (%.1fMB), will start a new tilesheet", mapName, sizeMB);
                    return false;
                }
            }
            Sprite icon;
            if (mapName.equals("textures")) { // 1.8
                icon = map.get(name);
                if (icon == null) {
                    icon = SpriteContents.createSprite(resource);
                }
            } else {
                icon = textureMap.registerSprite(name);
            }
            map.put(name, (SpriteContents) icon);
            iconMap.put(name, icon);
            String extra = (width == height ? "" : ", " + (height / width) + " frames");
            subLogger.finer("%s -> %s icon %dx%d%s", name, mapName, width, width, extra);
            tilesToRegister.remove(resource);
            return true;
        }
    */
    public void finish() {
        tilesToRegister.clear();
        tileImages.clear();
    }
/*
    public Sprite getIcon(String name) {
        if (MCPatcherUtils.isNullOrEmpty(name)) {
            return null;
        }
        Sprite icon = iconMap.get(name);
        if (icon == null) {
            icon = baseTexturesByName.get(name);
        }
        return icon;
    }

    public Sprite getIcon(Identifier resource) {
        return resource == null ? null : getIcon(resource.toString());
    }*/
}