package org.fabricmcpatcher.utils.id;

import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class EntityIdUtils {

    //https://minecraft.wiki/w/Java_Edition_data_values/Pre-flattening/Entity_IDs?oldid=531914
    //https://minecraft.wiki/w/Java_Edition_data_values/Pre-flattening/Entity_IDs

/*
    var table = document.getElementById("Apple");
    var rowCount = table.rows.length;
    let index = 0;
            for (var i = 0; i < rowCount; i++) {
        try{table.rows[i].deleteCell(index);}catch(e){}
    }
    */

    public static final Map<String,EntityType<?>> name2Type = new HashMap<>();
    public static final Map<Identifier,EntityType<?>> oldId2Type = new HashMap<>();

    public static final Map<EntityType<?>,String> type2Name = new HashMap<>();
    public static final Map<EntityType<?>,String> type2OldId = new HashMap<>();

    private static void addEntity(EntityType<?> type, String name) {
        name2Type.put(name,type);
        type2Name.put(type,name);
    }
    private static void addEntityNs(EntityType<?> type, String oldId) {
        oldId2Type.put(Identifier.ofVanilla(oldId),type);
        type2OldId.put(type,oldId);
    }




    static {
        //addEntity(EntityType.ELDER_GUARDIAN, "Elder guardian");
        //addEntity(EntityType.WITHER_SKELETON, "Wither skeleton");
        addEntity(EntityType.STRAY, "Stray");
        addEntity(EntityType.HUSK, "Husk");
        //addEntity(EntityType.ZOMBIE_VILLAGER, "Zombie Villager");
        addEntity(EntityType.EVOKER, "Evoker");
        addEntity(EntityType.VEX, "Vex");
        addEntity(EntityType.VINDICATOR, "Vindicator");
        addEntity(EntityType.ILLUSIONER, "Illusioner");
        addEntity(EntityType.CREEPER, "Creeper");
        addEntity(EntityType.SKELETON, "Skeleton");
        addEntity(EntityType.SPIDER, "Spider");
        addEntity(EntityType.GIANT, "Giant");
        addEntity(EntityType.ZOMBIE, "Zombie");
        addEntity(EntityType.SLIME, "Slime");
        addEntity(EntityType.GHAST, "Ghast");
        //addEntity(EntityType.ZOMBIFIED_PIGLIN, "Zombie pigman");
        addEntity(EntityType.ENDERMAN, "Enderman");
        //addEntity(EntityType.CAVE_SPIDER, "Cave spider");
        addEntity(EntityType.SILVERFISH, "Silverfish");
        addEntity(EntityType.BLAZE, "Blaze");
        //addEntity(EntityType.MAGMA_CUBE, "Magma cube");
        //addEntity(EntityType.ENDER_DRAGON, "Ender dragon");
        //addEntity(EntityType.WITHER, "Wither");
        addEntity(EntityType.WITCH, "Witch");
        addEntity(EntityType.ENDERMITE, "Endermite");
        addEntity(EntityType.GUARDIAN, "Guardian");
        addEntity(EntityType.SHULKER, "Shulker");
        //addEntity(EntityType.SKELETON_HORSE, "Skeleton horse");
        //addEntity(EntityType.ZOMBIE_HORSE, "Zombie horse");
        addEntity(EntityType.DONKEY, "Donkey");
        addEntity(EntityType.MULE, "Mule");
        addEntity(EntityType.BAT, "Bat");
        addEntity(EntityType.PIG, "Pig");
        addEntity(EntityType.SHEEP, "Sheep");
        addEntity(EntityType.COW, "Cow");
        addEntity(EntityType.CHICKEN, "Chicken");
        addEntity(EntityType.SQUID, "Squid");
        addEntity(EntityType.WOLF, "Wolf");
        addEntity(EntityType.MOOSHROOM, "Mooshroom");
        //addEntity(EntityType.SNOW_GOLEM, "Snow golem");
        addEntity(EntityType.OCELOT, "Ocelot");
        //addEntity(EntityType.IRON_GOLEM, "Iron golem");
        addEntity(EntityType.HORSE, "Horse");
        addEntity(EntityType.RABBIT, "Rabbit");
        //addEntity(EntityType.POLAR_BEAR, "Polar bear");
        addEntity(EntityType.LLAMA, "Llama");
        addEntity(EntityType.PARROT, "Parrot");
        addEntity(EntityType.VILLAGER, "Villager");

        addEntity(EntityType.ZOMBIFIED_PIGLIN, "PigZombie");
        addEntity(EntityType.CAVE_SPIDER, "CaveSpider");
        addEntity(EntityType.MAGMA_CUBE, "LavaSlime");
        addEntity(EntityType.POLAR_BEAR, "PolarBear");
        addEntity(EntityType.MOOSHROOM, "MushroomCow");
        addEntity(EntityType.SNOW_GOLEM, "SnowMan");
        addEntity(EntityType.OCELOT, "Ozelot");
        addEntity(EntityType.IRON_GOLEM, "VillagerGolem");
        addEntity(EntityType.HORSE, "EntityHorse");

        //after identifier-ification
        addEntityNs(EntityType.EVOKER, "evocation_illager");
        addEntityNs(EntityType.VINDICATOR, "vindication_illager");
        addEntityNs(EntityType.ILLUSIONER, "illusion_illager");
        addEntityNs(EntityType.ZOMBIFIED_PIGLIN, "zombie_pigman");
        addEntityNs(EntityType.SNOW_GOLEM, "snowman");
        addEntityNs(EntityType.IRON_GOLEM, "villager_golem");

    }
}
