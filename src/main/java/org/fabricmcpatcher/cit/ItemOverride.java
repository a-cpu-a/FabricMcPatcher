package org.fabricmcpatcher.cit;


import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.fabricmcpatcher.resource.PropertiesFile;
import org.fabricmcpatcher.resource.TileLoader;

import java.util.HashMap;
import java.util.Map;

final class ItemOverride extends OverrideBase {
    private Sprite icon;
    private final Map<Identifier, Sprite> iconMap;

    ItemOverride(PropertiesFile properties) {
        super(properties);

        if (items == null) {
            properties.error("no matching items specified");
        }

        iconMap = alternateTextures == null ? null : new HashMap<Identifier, Sprite>();
    }

    @Override
    String getType() {
        return "item";
    }

    Sprite getReplacementIcon(Identifier origIcon) {
        if (iconMap != null) {
            Sprite newIcon = iconMap.get(origIcon);
            if (newIcon != null) {
                return newIcon;
            }
        }
        return icon;
    }

    void preload(TileLoader tileLoader) {
        String special = null;
        if (items != null) {
            if (items.contains(CITUtils.itemCompass)) {
                special = "compass";
            } else if (items.contains(CITUtils.itemClock)) {
                special = "clock";
            }
        }/*
        if (textureName != null) {
            tileLoader.preloadTile(textureName, false, special);
        }
        if (alternateTextures != null) {
            for (Map.Entry<String, Identifier> entry : alternateTextures.entrySet()) {
                tileLoader.preloadTile(entry.getValue(), false, special);
            }
        }*/ //TODO
    }

    void registerIcon(TileLoader tileLoader) {
        /*if (textureName != null) {
            icon = tileLoader.getIcon(textureName);
        }
        if (alternateTextures != null) {
            for (Map.Entry<String, Identifier> entry : alternateTextures.entrySet()) {
                Sprite from = tileLoader.getIcon(entry.getKey());
                Sprite to = tileLoader.getIcon(entry.getValue());
                if (from != null && to != null) {
                    iconMap.put(from, to);
                }
            }
        }*/ //TODO
    }

    @Override
    String preprocessAltTextureKey(String name) {
        if (name.startsWith("textures/items/")) {
            name = name.substring(15);
            if (name.endsWith(".png")) {
                name = name.substring(0, name.length() - 4);
            }
        }
        return ItemAPI.expandTileName(name);
    }
}