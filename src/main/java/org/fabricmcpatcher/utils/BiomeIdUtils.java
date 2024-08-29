package org.fabricmcpatcher.utils;

import java.util.HashMap;
import java.util.Map;

public class BiomeIdUtils {

    public static final Map<String,BiomeInfo> propName2Info = new HashMap<>();
    public static final Map<String,BiomeInfo> name2Info = new HashMap<>();
    public static final Map<String,BiomeInfo> newId2Info = new HashMap<>();
    public static final Map<Integer,BiomeInfo> id2Info = new HashMap<>();

    private static void addBiome(String name,String newId, int id, String... altNames) {

        BiomeInfo info = new BiomeInfo();

        info.propName = name.replace(" ","");
        info.name = name;
        info.newId = newId;
        info.id = id;
        info.altNames = altNames;
        info.altPropNames = new String[altNames.length];

        int i = 0;
        for (String altName : altNames) {
            info.altPropNames[i] =altName.replace(" ","");

            propName2Info.put(info.altPropNames[i],info);
            name2Info.put(altName,info);

            i++;
        }

        propName2Info.put(info.propName,info);
        name2Info.put(info.name,info);
        newId2Info.put(info.newId,info);
        id2Info.put(info.id,info);
    }

    static {
        //https://minecraft.fandom.com/wiki/Java_Edition_data_values/Biomes
        //https://minecraft.fandom.com/wiki/Biome?oldid=1223165
        //https://minecraft.wiki/w/Java_Edition_1.13#World_generation_2

        addBiome("Badlands Plateau", "badlands_plateau", 39,"Mesa Plateau");
        addBiome("Badlands", "badlands", 37,"Mesa");
        addBiome("Bamboo Jungle Hills", "bamboo_jungle_hills", 169);
        addBiome("Bamboo Jungle", "bamboo_jungle", 168);
        addBiome("Basalt Deltas", "basalt_deltas", 173);
        addBiome("Beach", "beach", 16);
        addBiome("Birch Forest Hills", "birch_forest_hills", 28);
        addBiome("Birch Forest", "birch_forest", 27);
        addBiome("Cold Ocean", "cold_ocean", 46);
        addBiome("Crimson Forest", "crimson_forest", 171);
        addBiome("Dark Forest Hills", "dark_forest_hills", 157,"Roofed Forest M");
        addBiome("Dark Forest", "dark_forest", 29,"Roofed Forest");
        addBiome("Deep Cold Ocean", "deep_cold_ocean", 49);
        addBiome("Deep Frozen Ocean", "deep_frozen_ocean", 50);
        addBiome("Deep Lukewarm Ocean", "deep_lukewarm_ocean", 48);
        addBiome("Deep Ocean", "deep_ocean", 24);
        addBiome("Deep Warm Ocean", "deep_warm_ocean", 47);
        addBiome("Desert Hills", "desert_hills", 17,"DesertHills");
        addBiome("Desert Lakes", "desert_lakes", 130,"Desert M");
        addBiome("Desert", "desert", 2);
        addBiome("End Barrens", "end_barrens", 43);
        addBiome("End Highlands", "end_highlands", 42);
        addBiome("End Midlands", "end_midlands", 41);
        addBiome("Eroded Badlands", "eroded_badlands", 165,"Mesa (Bryce)");
        addBiome("Flower Forest", "flower_forest", 132);
        addBiome("Forest", "forest", 4);
        addBiome("Frozen Ocean", "frozen_ocean", 10);
        addBiome("Frozen River", "frozen_river", 11);
        addBiome("Giant Spruce Taiga Hills", "giant_spruce_taiga_hills", 161,"Redwood Taiga Hills M");
        addBiome("Giant Spruce Taiga", "giant_spruce_taiga", 160,"Mega Spruce Taiga");
        addBiome("Giant Tree Taiga Hills", "giant_tree_taiga_hills", 33,"Mega Taiga Hills");
        addBiome("Giant Tree Taiga", "giant_tree_taiga", 32,"Mega Taiga");
        addBiome("Gravelly Mountains", "gravelly_mountains", 131,"Extreme Hills M");
        addBiome("Gravelly Mountains+", "modified_gravelly_mountains", 162,"Extreme Hills+ M");
        addBiome("Ice Spikes", "ice_spikes", 140,"Ice Plains Spikes");
        addBiome("Jungle Edge", "jungle_edge", 23);
        addBiome("Jungle Hills", "jungle_hills", 22,"JungleHills");
        addBiome("Jungle", "jungle", 21);
        addBiome("Lukewarm Ocean", "lukewarm_ocean", 45);
        addBiome("Modified Badlands Plateau", "modified_badlands_plateau", 167,"Mesa Plateau M");
        addBiome("Modified Jungle Edge", "modified_jungle_edge", 151,"Jungle Edge M");
        addBiome("Modified Jungle", "modified_jungle", 149,"Jungle M");
        addBiome("Modified Wooded Badlands Plateau", "modified_wooded_badlands_plateau", 166,"Mesa Plateau F M");
        addBiome("Mountain Edge", "mountain_edge", 20,"Extreme Hills Edge");
        addBiome("Mountains", "mountains", 3,"Extreme Hills");
        addBiome("Mushroom Field Shore", "mushroom_field_shore", 15,"Mushroom Island Shore");
        addBiome("Mushroom Fields", "mushroom_fields", 14,"Mushroom Island");
        addBiome("Nether Wastes", "nether_wastes", 8,"Nether","Hell");
        addBiome("Ocean", "ocean", 0);
        addBiome("Plains", "plains", 1);
        addBiome("River", "river", 7);
        addBiome("Savanna Plateau", "savanna_plateau", 36);
        addBiome("Savanna", "savanna", 35);
        addBiome("Shattered Savanna Plateau", "shattered_savanna_plateau", 164,"Savanna Plateau M");
        addBiome("Shattered Savanna", "shattered_savanna", 163,"Savanna M");
        addBiome("Small End Islands", "small_end_islands", 40);
        addBiome("Snowy Beach", "snowy_beach", 26,"Cold Beach");
        addBiome("Snowy Mountains", "snowy_mountains", 13,"Ice Mountains");
        addBiome("Snowy Taiga Hills", "snowy_taiga_hills", 31,"Cold Taiga Hills");
        addBiome("Snowy Taiga Mountains", "snowy_taiga_mountains", 158,"Cold Taiga M");
        addBiome("Snowy Taiga", "snowy_taiga", 30,"Cold Taiga");
        addBiome("Snowy Tundra", "snowy_tundra", 12,"Ice Plains");
        addBiome("Soul Sand Valley", "soul_sand_valley", 170);
        addBiome("Stone Shore", "stone_shore", 25,"Stone Beach");
        addBiome("Sunflower Plains", "sunflower_plains", 129);
        addBiome("Swamp Hills", "swamp_hills", 134,"Swampland M");
        addBiome("Swamp", "swamp", 6,"Swampland");
        addBiome("Taiga Hills", "taiga_hills", 19,"TaigaHills");
        addBiome("Taiga Mountains", "taiga_mountains", 133,"Taiga M");
        addBiome("Taiga", "taiga", 5);
        addBiome("Tall Birch Forest", "tall_birch_forest", 155,"Birch Forest M");
        addBiome("Tall Birch Hills", "tall_birch_hills", 156,"Birch Forest Hills M");
        addBiome("The End", "the_end", 9,"Sky");
        addBiome("The Void", "the_void", 127);
        addBiome("Warm Ocean", "warm_ocean", 44);
        addBiome("Warped Forest", "warped_forest", 172);
        addBiome("Wooded Badlands Plateau", "wooded_badlands_plateau", 38,"Mesa Plateau F");
        addBiome("Wooded Hills", "wooded_hills", 18,"ForestHills");
        addBiome("Wooded Mountains", "wooded_mountains", 34,"Extreme Hills+");

    }

    public static class BiomeInfo {
        public String propName;
        public String name;
        public String newId;
        public int id;
        public String[] altPropNames;
        public String[] altNames;
    }
}
