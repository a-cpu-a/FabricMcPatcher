package org.fabricmcpatcher.cit;

import net.minecraft.util.Identifier;
import org.fabricmcpatcher.resource.PropertiesFile;
import org.fabricmcpatcher.resource.TexturePackAPI;

import java.util.HashMap;
import java.util.Map;

final class ArmorOverride extends OverrideBase {
    private final Map<Identifier, Identifier> armorMap;

    ArmorOverride(PropertiesFile properties) {
        super(properties);

        if (items == null) {
            properties.error("no matching items specified");
        }
        if (textureName == null && alternateTextures == null) {
            properties.error("no replacement textures specified");
        }

        if (alternateTextures == null) {
            armorMap = null;
        } else {
            armorMap = new HashMap<Identifier, Identifier>();
            for (Map.Entry<String, Identifier> entry : alternateTextures.entrySet()) {
                String key = entry.getKey();
                Identifier value = entry.getValue();
                armorMap.put(TexturePackAPI.parseIdentifier(CITUtils.FIXED_ARMOR_RESOURCE, key), value);
            }
        }
    }

    @Override
    String getType() {
        return "armor";
    }

    Identifier getReplacementTexture(Identifier origResource) {
        if (armorMap != null) {
            Identifier newResource = armorMap.get(origResource);
            if (newResource != null) {
                return newResource;
            }
        }
        return textureName;
    }

    @Override
    String preprocessAltTextureKey(String name) {
        if (!name.endsWith(".png")) {
            name += ".png";
        }
        if (!name.contains("/")) {
            name = "./" + name;
        }
        return TexturePackAPI.parseIdentifier(CITUtils.FIXED_ARMOR_RESOURCE, name).toString();
    }
}