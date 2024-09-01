package org.fabricmcpatcher.utils.id;

import com.ibm.icu.impl.ICUResourceBundle;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionIdUtils {

    //https://minecraft.wiki/w/Potion?oldid=859543#Unused_potions
    //https://minecraft.fandom.com/wiki/Potion/Before_1.9
    //https://minecraft.wiki/w/Potion

    public static final Map<String,PotionInfo> potionName2Info = new HashMap<>();
    public static final Map<RegistryEntry<Potion>,PotionInfo> potionType2Info = new HashMap<>();
    public static final Map<RegistryEntry<Potion>,Integer> potionType2Damage = new HashMap<>();
    public static Map<RegistryEntry<StatusEffect>,Integer> effect2Id = new HashMap<>();
    public static Map<StatusEffect,PotionInfo> effectRaw2Info = new HashMap<>();
    public static List<StatusEffect> effects = new ArrayList<>();

    public static final String[] MUNDANE_NAMES = new String[]{
            "potion.prefix.mundane",
            "potion.prefix.uninteresting",
            "potion.prefix.bland",
            "potion.prefix.clear",
            "potion.prefix.milky",
            "potion.prefix.diffuse",
            "potion.prefix.artless",
            "potion.prefix.thin",
            "potion.prefix.awkward",
            "potion.prefix.flat",
            "potion.prefix.bulky",
            "potion.prefix.bungling",
            "potion.prefix.buttered",
            "potion.prefix.smooth",
            "potion.prefix.suave",
            "potion.prefix.debonair",
            "potion.prefix.thick",
            "potion.prefix.elegant",
            "potion.prefix.fancy",
            "potion.prefix.charming",
            "potion.prefix.dashing",
            "potion.prefix.refined",
            "potion.prefix.cordial",
            "potion.prefix.sparkling",
            "potion.prefix.potent",
            "potion.prefix.foul",
            "potion.prefix.odorless",
            "potion.prefix.rank",
            "potion.prefix.harsh",
            "potion.prefix.acrid",
            "potion.prefix.gross",
            "potion.prefix.stinky"};

    static {
        createPotion("moveSpeed","speed", StatusEffects.SPEED,8194,Potions.SWIFTNESS,8258,Potions.LONG_SWIFTNESS,8226,Potions.STRONG_SWIFTNESS);
        createPotion("moveSlowdown","slowness",StatusEffects.SLOWNESS, 8202,Potions.SLOWNESS,8266,Potions.LONG_SLOWNESS,0,Potions.STRONG_SLOWNESS);
        createPotion("digSpeed","haste",StatusEffects.HASTE);
        createPotion("digSlowDown","mining_fatigue",StatusEffects.MINING_FATIGUE);
        createPotion("damageBoost","strength",StatusEffects.STRENGTH,8201,Potions.STRENGTH,8265,Potions.LONG_STRENGTH,8233,Potions.STRONG_STRENGTH);
        createPotion("heal","instant_health",StatusEffects.INSTANT_HEALTH,8197,Potions.HEALING, 8229,Potions.STRONG_HEALING);
        createPotion("harm","instant_damage",StatusEffects.INSTANT_DAMAGE,8204,Potions.HARMING,8236,Potions.STRONG_HARMING);
        createPotion("jump","jump_boost",StatusEffects.JUMP_BOOST,8203,Potions.LEAPING,8267,Potions.LONG_LEAPING,8235,Potions.STRONG_LEAPING);
        createPotion("confusion","nausea",StatusEffects.NAUSEA);
        createPotion("regeneration",StatusEffects.REGENERATION,8193,Potions.REGENERATION,8257,Potions.LONG_REGENERATION,8225,Potions.STRONG_REGENERATION);
        createPotion("resistance",StatusEffects.RESISTANCE);
        createPotion("fireResistance","fire_resistance",StatusEffects.FIRE_RESISTANCE,8195,Potions.FIRE_RESISTANCE,8259,Potions.LONG_FIRE_RESISTANCE);
        createPotion("waterBreathing","water_breathing",StatusEffects.WATER_BREATHING,8205,Potions.WATER_BREATHING,8269,Potions.LONG_WATER_BREATHING);
        createPotion("invisibility",StatusEffects.INVISIBILITY,8206,Potions.INVISIBILITY,8270,Potions.LONG_INVISIBILITY);
        createPotion("blindness",StatusEffects.BLINDNESS);
        createPotion("nightVision","night_vision",StatusEffects.NIGHT_VISION,8198,Potions.NIGHT_VISION,8262,Potions.LONG_NIGHT_VISION);
        createPotion("hunger",StatusEffects.HUNGER);
        createPotion("weakness",StatusEffects.WEAKNESS,8200,Potions.WEAKNESS,8264,Potions.LONG_WEAKNESS);
        createPotion("poison",StatusEffects.POISON,8196,Potions.POISON,8260,Potions.LONG_POISON,8228,Potions.STRONG_POISON);
        createPotion("wither",StatusEffects.WITHER);
        createPotion("healthBoost","health_boost",StatusEffects.HEALTH_BOOST);
        createPotion("absorption",StatusEffects.ABSORPTION);
        createPotion("saturation",StatusEffects.SATURATION);
        createPotion("water",null,0,Potions.WATER,16,Potions.AWKWARD,32,Potions.THICK,64,Potions.MUNDANE);


        createPotion("bad_omen",StatusEffects.BAD_OMEN);
        createPotion("conduit_power",StatusEffects.CONDUIT_POWER);
        createPotion("dolphins_grace",StatusEffects.DOLPHINS_GRACE);
        createPotion("glowing",StatusEffects.GLOWING);
        createPotion("hero_of_the_village",StatusEffects.HERO_OF_THE_VILLAGE);
        createPotion("levitation",StatusEffects.LEVITATION);
        createPotion("luck",StatusEffects.LUCK);
        createPotion("slow_falling",StatusEffects.SLOW_FALLING);
        createPotion("unluck",StatusEffects.UNLUCK);

//TURTLE_MASTER
//LUCK
//SLOW_FALLING
//WIND_CHARGED
//WEAVING
//OOZING
//INFESTED
    }

    private static int nextEffectId = 0;

    private static void createPotion(String name,String newName, RegistryEntry<StatusEffect> effect,Object... potionIds) {
        PotionInfo info = new PotionInfo();

        if(effect!=null) {

            StatusEffect effectV = effect.value();//Registries.STATUS_EFFECT.get(effect.getKey().get());
            effects.add(effectV);
            effect2Id.put(effect,nextEffectId++);
            effectRaw2Info.put(effectV,info);
        }

        info.effect = effect;
        info.name = name;
        info.newName = newName;

        if(potionIds.length!=0)
            info.hasItemForm=true;

        potionName2Info.put(name,info);
        if(newName!=null)
            potionName2Info.put(newName,info);
        for (int i = 0; i < potionIds.length/2; i++) {
            if(info.type2Damage==null)
                info.type2Damage=new HashMap<>();

            RegistryEntry<Potion> potion = (RegistryEntry<Potion>) potionIds[i*2+1];
            int damage = (int) potionIds[i*2];

            potionType2Damage.put(potion,damage);

            info.type2Damage.put(potion,damage);
            potionType2Info.put(potion,info);
        }
    }
    private static void createPotion(String name, RegistryEntry<StatusEffect> effect,Object... potionIds) {
        createPotion(name,null,effect,potionIds);
    }

    public static int getPotionDamage(RegistryEntry<Potion> potion,boolean splash) {

        Integer damage = potionType2Damage.get(potion);

        if(damage==null)return -1;

        //has drinkable bit
        if((damage&(0b1111111111111+1))!=0) {

            if(!splash)
                return damage;

            damage ^= 0b1111111111111+1;//remove it

        }
        else if(damage==0)
            return 0;//splash water didnt exist in damage value form

        if(splash)
            damage |= 16384;
        else
            damage |= 8192;

        return damage;
    }

    public static int getEffectId(RegistryEntry<StatusEffect> effect) {
        return effect2Id.getOrDefault(effect,-1);
    }

    public static PotionInfo getInfo(StatusEffect potion) {
        return effectRaw2Info.get(potion);
    }

    public static class PotionInfo {
        public String name;
        public String newName=null;
        public Map<RegistryEntry<Potion>,Integer> type2Damage=null;
        public RegistryEntry<StatusEffect> effect;
        public boolean hasItemForm = false;
    }

    //potion helper replacement
    public static String getMundaneName(int id) {
        return MUNDANE_NAMES[id>>1];//bit 1 -> ???
    }
}
