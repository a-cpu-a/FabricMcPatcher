package org.fabricmcpatcher.mob;

import net.minecraft.util.Identifier;
import org.fabricmcpatcher.color.biome.BiomeAPI;
import org.fabricmcpatcher.resource.PropertiesFile;
import org.fabricmcpatcher.resource.TexturePackAPI;
import org.fabricmcpatcher.utils.MCLogger;
import org.fabricmcpatcher.utils.MCPatcherUtils;
import org.fabricmcpatcher.utils.WeightedIndex;

import java.util.*;

import static org.fabricmcpatcher.resource.TexturePackAPI.*;

class MobRuleList {
    private static final MCLogger logger = MCLogger.getLogger(MCPatcherUtils.RANDOM_MOBS);

    public static final String ALTERNATIVES_REGEX = "_(eyes|overlay|tame|angry|collar|fur|invulnerable|shooting)\\.properties$";

    private static final Map<Identifier, MobRuleList> allRules = new HashMap<Identifier, MobRuleList>();

    private final Identifier baseSkin;
    private final List<Identifier> allSkins;
    private final int skinCount;
    private final List<MobRuleEntry> entries;

    private MobRuleList(Identifier baseSkin) {
        this.baseSkin = baseSkin;

        TexturePackAPI.newMCPatcherIdentifier("mob/");

        String newPath = baseSkin.getPath().replaceFirst("^textures/entity/", "mob/");
        Identifier newSkin = TexturePackAPI.newMCPatcherIdentifierNS(baseSkin.getNamespace(),newPath);
        allSkins = new ArrayList<Identifier>();
        allSkins.add(baseSkin);
        for (int i = 2; ; i++) {
            Identifier skin = transformIdentifier(newSkin, ".png", String.valueOf(i) + ".png");
            if (!hasResource(skin)) {
                break;
            }
            allSkins.add(skin);
        }
        skinCount = allSkins.size();
        if (skinCount <= 1) {
            entries = null;
            return;
        }
        logger.fine("found %d variations for %s", skinCount, baseSkin);

        Identifier filename = transformIdentifier(newSkin, ".png", ".properties");
        Identifier altFilename = Identifier.of(newSkin.getNamespace(), filename.getPath().replaceFirst(ALTERNATIVES_REGEX, ".properties"));
        PropertiesFile properties = PropertiesFile.get(logger, filename);
        if (properties == null && !filename.equals(altFilename)) {
            properties = PropertiesFile.get(logger, altFilename);
            if (properties != null) {
                logger.fine("using %s for %s", altFilename, baseSkin);
            }
        }
        ArrayList<MobRuleEntry> tmpEntries = new ArrayList<MobRuleEntry>();
        if (properties != null) {
            for (int i = 0; ; i++) {
                MobRuleEntry entry = MobRuleEntry.load(properties, i, skinCount);
                if (entry == null) {
                    if (i > 0) {
                        break;
                    }
                } else {
                    logger.fine("  %s", entry.toString());
                    tmpEntries.add(entry);
                }
            }
        }
        entries = tmpEntries.isEmpty() ? null : tmpEntries;
    }

    Identifier getSkin(long key, int i, int j, int k, Integer biome) {
        if (entries == null) {
            int index = (int) (key % skinCount);
            if (index < 0) {
                index += skinCount;
            }
            return allSkins.get(index);
        } else {
            if (j < 0) {
                j = 0;
            }
            for (MobRuleEntry entry : entries) {
                if (entry.match(i, j, k, biome)) {
                    int index = entry.weightedIndex.choose(key);
                    return allSkins.get(entry.skins[index]);
                }
            }
        }
        return baseSkin;
    }

    static MobRuleList get(Identifier texture) {
        MobRuleList list = allRules.get(texture);
        if (list == null) {
            list = new MobRuleList(texture);
            allRules.put(texture, list);
        }
        return list;
    }

    static void clear() {
        allRules.clear();
    }

    private static class MobRuleEntry {
        final int[] skins;
        final WeightedIndex weightedIndex;
        private final BitSet biomes;
        private final BitSet height;

        static MobRuleEntry load(PropertiesFile properties, int index, int limit) {
            String skinList = properties.getString("skins." + index, "").toLowerCase();
            int[] skins;
            if (skinList.equals("*") || skinList.equals("all") || skinList.equals("any")) {
                skins = new int[limit];
                for (int i = 0; i < skins.length; i++) {
                    skins[i] = i;
                }
            } else {
                skins = MCPatcherUtils.parseIntegerList(skinList, 1, limit);
                if (skins.length <= 0) {
                    return null;
                }
                for (int i = 0; i < skins.length; i++) {
                    skins[i]--;
                }
            }

            WeightedIndex chooser = WeightedIndex.create(skins.length, properties.getString("weights." + index, ""));
            if (chooser == null) {
                return null;
            }

            BitSet biomes;
            String biomeList = properties.getString("biomes." + index, "");
            if (biomeList.isEmpty()) {
                biomes = null;
            } else {
                biomes = new BitSet();
                BiomeAPI.parseBiomeList(biomeList, biomes);
            }

            BitSet height = BiomeAPI.getHeightListProperty(properties, "." + index);

            return new MobRuleEntry(skins, chooser, biomes, height);
        }

        MobRuleEntry(int[] skins, WeightedIndex weightedIndex, BitSet biomes, BitSet height) {
            this.skins = skins;
            this.weightedIndex = weightedIndex;
            this.biomes = biomes;
            this.height = height;
        }

        boolean match(int i, int j, int k, Integer biome) {
            if (biomes != null) {
                if (biome == null || !biomes.get(biome)) {
                    return false;
                }
            }
            if (height != null && !height.get(j)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("skins:");
            for (int i : skins) {
                sb.append(' ').append(i + 1);
            }
            if (biomes != null) {
                sb.append(", biomes:");
                for (int i = biomes.nextSetBit(0); i >= 0; i = biomes.nextSetBit(i + 1)) {
                    sb.append(' ').append(i);
                }
            }
            if (height != null) {
                sb.append(", height:");
                for (int i = height.nextSetBit(0); i >= 0; i = height.nextSetBit(i + 1)) {
                    sb.append(' ').append(i);
                }
            }
            sb.append(", weights: ").append(weightedIndex.toString());
            return sb.toString();
        }
    }
}