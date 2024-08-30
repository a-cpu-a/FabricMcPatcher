package org.fabricmcpatcher.utils.id;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentIdUtils {

    //https://www.digminecraft.com/lists/enchantment_list_pc.php

    /*


            var table = document.getElementById("minecraft_items");
            var rowCount = table.rows.length;
            let index = 3;
            for (var i = 0; i < rowCount; i++) {
                table.rows[i].deleteCell(index);
            }


    */

    public static final Map<String,Integer> newId2OldId = new HashMap<>();
    public static final Map<Integer,String> oldId2NewId = new HashMap<>();

    static {
        addEnchant("aqua_affinity", 6);
        addEnchant("bane_of_arthropods", 18);
        addEnchant("blast_protection", 3);
        //addEnchant("breach", );
        addEnchant("channeling", 68);
        addEnchant("binding_curse", 10);
        addEnchant("vanishing_curse", 71);
        //addEnchant("density", );
        addEnchant("depth_strider", 8);
        addEnchant("efficiency", 32);
        addEnchant("feather_falling", 2);
        addEnchant("fire_aspect", 20);
        addEnchant("fire_protection", 1);
        addEnchant("flame", 50);
        addEnchant("fortune", 35);
        addEnchant("frost_walker", 9);
        addEnchant("impaling", 66);
        addEnchant("infinity", 51);
        addEnchant("knockback", 19);
        addEnchant("looting", 21);
        addEnchant("loyalty", 65);
        addEnchant("luck_of_the_sea", 61);
        addEnchant("lure", 62);
        addEnchant("mending", 70);
        //addEnchant("multishot", );
        //addEnchant("piercing", );
        addEnchant("power", 48);
        addEnchant("projectile_protection", 4);
        addEnchant("protection", 0);
        addEnchant("punch", 49);
        //addEnchant("quick_charge", );
        addEnchant("respiration", 5);
        addEnchant("riptide", 67);
        addEnchant("sharpness", 16);
        addEnchant("silk_touch", 33);
        addEnchant("smite", 17);
        //addEnchant("soul_speed", );
        //addEnchant("sweeping_edge", );
        //addEnchant("swift_sneak", );
        addEnchant("thorns", 7);
        addEnchant("unbreaking", 34);
        //addEnchant("wind_burst", );
    }

    private static void addEnchant(String newId, int oldId) {
        newId2OldId.put(newId,oldId);
        oldId2NewId.put(oldId,newId);
    }

    //returns -1, when no matching id is found
    public static int newId2Int(Identifier newId) {
        if(!newId.getNamespace().equals("minecraft"))
            return -1;

        Integer oldId = newId2OldId.get(newId.getPath());
        if(oldId==null)
            return -1;

        return oldId;
    }
    //returns -1, when no matching id is found
    public static int newId2Int(RegistryEntry<Enchantment> e) {
        Identifier newId = e.getKey().get().getValue();
        return newId2Int(newId);
    }
}
