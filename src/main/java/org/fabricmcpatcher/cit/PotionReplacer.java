package org.fabricmcpatcher.cit;


import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.fabricmcpatcher.resource.PropertiesFile;
import org.fabricmcpatcher.resource.TexturePackAPI;
import org.fabricmcpatcher.utils.MCLogger;
import org.fabricmcpatcher.utils.MCPatcherUtils;
import org.fabricmcpatcher.utils.id.PotionIdUtils;

import java.util.*;

class PotionReplacer {
    private static final MCLogger logger = MCLogger.getLogger(MCPatcherUtils.CUSTOM_ITEM_TEXTURES, "CIT");

    private static final String ITEM_ID_POTION = "minecraft:potion";
    private static final String ITEM_ID_GLASS_BOTTLE = "minecraft:glass_bottle";

    private static final String LAYER_POTION_CONTENTS = TexturePackAPI.select("potion_contents", "potion_overlay");
    private static final String LAYER_POTION_DRINKABLE = TexturePackAPI.select("potion", "potion_bottle_drinkable");
    private static final String LAYER_POTION_SPLASH = TexturePackAPI.select("potion_splash", "potion_bottle_splash");
    private static final String LAYER_EMPTY_BOTTLE = TexturePackAPI.select("potion", "potion_bottle_empty");

    private static final int SPLASH_BIT = 0x4000;
    private static final int EFFECT_BITS = 0x400f;
    private static final int MUNDANE_BITS = 0x403f;
    private static final int WATER_BITS = 0xffff;

    private static final int[] POTION_EFFECTS = new int[]{
        -1, // 0:  none
        2,  // 1:  moveSpeed
        10, // 2:  moveSlowdown
        -1, // 3:  digSpeed
        -1, // 4:  digSlowDown
        9,  // 5:  damageBoost
        5,  // 6:  heal
        12, // 7:  harm
        -1, // 8:  jump
        -1, // 9:  confusion
        1,  // 10: regeneration
        -1, // 11: resistance
        3,  // 12: fireResistance
        -1, // 13: waterBreathing
        14, // 14: invisibility
        -1, // 15: blindness
        6,  // 16: nightVision
        -1, // 17: hunger
        8,  // 18: weakness
        4,  // 19: poison
        -1, // 20: wither
    };

    private static final Map<String, Integer> mundanePotionMap = new HashMap<String, Integer>();
    private static int weight = -2;

    final List<ItemOverride> overrides = new ArrayList<ItemOverride>();

    static {
        try {
            //https://minecraft.wiki/w/Potion?oldid=859543#Unused_potions, minus 0 & 16 & 32 which are water & akward & thick
            for (int i : new int[]{0, 7, 11, 13, 15, 16, 23, 27, 29, 31, 32, 39, 43, 45, 47, 48, 55, 59, 61, 63}) {
                String name = PotionIdUtils.getMundaneName(i).replaceFirst("^potion\\.prefix\\.", "");
                mundanePotionMap.put(name, i);
                logger.fine("%s potion -> damage value %d", name, i);
            }

            int i = 0;
            for(Potion potion : Registries.POTION.stream().toList()) {
                PotionIdUtils.PotionInfo info = PotionIdUtils.potionType2Info.get(Registries.POTION.getEntry(potion));
                if(info==null)continue;
                logger.fine("%s/%s potion -> effect %d", info.name,info.newName==null?"":info.newName, i++); //potion.getName().replaceFirst("^potion\\.", "")
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    PotionReplacer() {
        Identifier path = getPotionPath("water", false);
        if (TexturePackAPI.hasResource(path)) {
            weight++;
            registerVanillaPotion(path, 0, WATER_BITS, false);
            weight--;
        }
        path = getPotionPath("empty", false);
        if (TexturePackAPI.hasResource(path)) {
            registerEmptyBottle(path);
        }
        registerPotionsByEffect(false);
        registerPotionsByEffect(true);
        registerMundanePotions(false);
        registerMundanePotions(true);
        registerOtherPotions(false);
        registerOtherPotions(true);
    }

    private static Identifier getPotionPath(String name, boolean splash) {
        String path = "cit/potion/" + (splash ? "splash/" : "normal/") + name + ".png";
        return TexturePackAPI.newMCPatcherIdentifier(path);
    }

    private static Properties newProperties(Identifier path, String itemID, String layer) {
        Properties properties = new Properties();
        properties.setProperty("type", "item");
        properties.setProperty("items", itemID);
        properties.setProperty("texture." + layer, path.toString());
        properties.setProperty("texture." + LAYER_POTION_CONTENTS, "blank");
        properties.setProperty("weight", String.valueOf(weight));
        return properties;
    }

    private static Properties newProperties(Identifier path, String itemID, boolean splash) {
        String layer = splash ? LAYER_POTION_SPLASH : LAYER_POTION_DRINKABLE;
        return newProperties(path, itemID, layer);
    }

    private void registerPotionsByEffect(boolean splash) {

        for (String name : PotionIdUtils.potionName2Info.keySet()) {
            PotionIdUtils.PotionInfo info = PotionIdUtils.potionName2Info.get(name);
        /*for (int effect = 0; effect < Potion.potionTypes.length; effect++) {
            if (Potion.potionTypes[effect] == null) {
                continue;
            }*/

            if(info.effect==null)continue;

            Identifier path = getPotionPath(name, splash);
            if (TexturePackAPI.hasResource(path)) {
                if (info.hasItemForm) {//effect < POTION_EFFECTS.length && POTION_EFFECTS[effect] >= 0
                    for (RegistryEntry<Potion> pot : info.type2Damage.keySet()) {

                        int damage = PotionIdUtils.getPotionDamage(pot,splash);//POTION_EFFECTS[effect];

                        //remove drinkable bit
                        if((damage&8192)!=0)
                            damage-=8192;
                    /*if (splash) {
                        damage |= SPLASH_BIT;
                    }*/
                        registerVanillaPotion(path, damage, EFFECT_BITS, splash);
                    }
                }
                if (!splash) {
                    registerCustomPotion(path, info.effect, splash);
                }
            }
        }
    }

    private void registerMundanePotions(boolean splash) {
        for (Map.Entry<String, Integer> entry : mundanePotionMap.entrySet()) {
            int damage = entry.getValue();
            if (splash) {
                damage |= SPLASH_BIT;
            }
            registerMundanePotion(entry.getKey(), damage, splash);
        }
    }

    private void registerMundanePotion(String name, int damage, boolean splash) {
        Identifier path = getPotionPath(name, splash);
        if (TexturePackAPI.hasResource(path)) {
            registerVanillaPotion(path, damage, MUNDANE_BITS, splash);
        }
    }

    private void registerOtherPotions(boolean splash) {
        Identifier path = getPotionPath("other", splash);
        if (TexturePackAPI.hasResource(path)) {
            Properties properties = newProperties(path, ITEM_ID_POTION, splash);
            StringBuilder sb = new StringBuilder();
            for (int i : mundanePotionMap.values()) {
                if (splash) {
                    i |= SPLASH_BIT;
                }
                sb.append(' ').append(i);
            }
            properties.setProperty("damage", sb.toString().trim());
            properties.setProperty("damageMask", String.valueOf(MUNDANE_BITS));
            addOverride(path, properties);
        }
    }

    private void registerVanillaPotion(Identifier path, int damage, int mask, boolean splash) {
        Properties properties = newProperties(path, ITEM_ID_POTION, splash);
        properties.setProperty("damage", String.valueOf(damage));
        properties.setProperty("damageMask", String.valueOf(mask));
        addOverride(path, properties);
    }

    private void registerCustomPotion(Identifier path, RegistryEntry<StatusEffect> effect, boolean splash) {
        Properties properties = newProperties(path, ITEM_ID_POTION, splash);
        properties.setProperty("nbt.CustomPotionEffects.0.Id", String.valueOf(PotionIdUtils.getEffectId(effect)));
        addOverride(path, properties);
    }

    private void registerEmptyBottle(Identifier path) {
        Properties properties = newProperties(path, ITEM_ID_GLASS_BOTTLE, LAYER_EMPTY_BOTTLE);
        addOverride(path, properties);
    }

    private void addOverride(Identifier path, Properties properties) {
        Identifier propertiesName = TexturePackAPI.transformIdentifier(path, ".png", ".properties");
        ItemOverride override = new ItemOverride(new PropertiesFile(logger, propertiesName, properties));
        if (override.properties.valid()) {
            overrides.add(override);
        }
    }
}