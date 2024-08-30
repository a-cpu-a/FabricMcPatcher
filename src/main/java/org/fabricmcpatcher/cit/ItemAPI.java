package org.fabricmcpatcher.cit;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.fabricmcpatcher.resource.TexturePackAPI;
import org.fabricmcpatcher.utils.MCPatcherUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class ItemAPI {
    private static final ItemAPI instance = new ItemAPI();

    private static final HashMap<String, Integer> canonicalIdByName = new HashMap<>();
    private static final HashMap<Integer, Identifier> nameByCanonicalId = new HashMap<>();

    static {
        createItem("minecraft:iron_shovel", 256);
        createItem("minecraft:iron_pickaxe", 257);
        createItem("minecraft:iron_axe", 258);
        createItem("minecraft:flint_and_steel", 259);
        createItem("minecraft:apple", 260);
        createItem("minecraft:bow", 261);
        createItem("minecraft:arrow", 262);
        createItem("minecraft:coal", 263);
        createItem("minecraft:diamond", 264);
        createItem("minecraft:iron_ingot", 265);
        createItem("minecraft:gold_ingot", 266);
        createItem("minecraft:iron_sword", 267);
        createItem("minecraft:wooden_sword", 268);
        createItem("minecraft:wooden_shovel", 269);
        createItem("minecraft:wooden_pickaxe", 270);
        createItem("minecraft:wooden_axe", 271);
        createItem("minecraft:stone_sword", 272);
        createItem("minecraft:stone_shovel", 273);
        createItem("minecraft:stone_pickaxe", 274);
        createItem("minecraft:stone_axe", 275);
        createItem("minecraft:diamond_sword", 276);
        createItem("minecraft:diamond_shovel", 277);
        createItem("minecraft:diamond_pickaxe", 278);
        createItem("minecraft:diamond_axe", 279);
        createItem("minecraft:stick", 280);
        createItem("minecraft:bowl", 281);
        createItem("minecraft:mushroom_stew", 282);
        createItem("minecraft:golden_sword", 283);
        createItem("minecraft:golden_shovel", 284);
        createItem("minecraft:golden_pickaxe", 285);
        createItem("minecraft:golden_axe", 286);
        createItem("minecraft:string", 287);
        createItem("minecraft:feather", 288);
        createItem("minecraft:gunpowder", 289);
        createItem("minecraft:wooden_hoe", 290);
        createItem("minecraft:stone_hoe", 291);
        createItem("minecraft:iron_hoe", 292);
        createItem("minecraft:diamond_hoe", 293);
        createItem("minecraft:golden_hoe", 294);
        createItem("minecraft:wheat_seeds", 295);
        createItem("minecraft:wheat", 296);
        createItem("minecraft:bread", 297);
        createItem("minecraft:leather_helmet", 298);
        createItem("minecraft:leather_chestplate", 299);
        createItem("minecraft:leather_leggings", 300);
        createItem("minecraft:leather_boots", 301);
        createItem("minecraft:chainmail_helmet", 302);
        createItem("minecraft:chainmail_chestplate", 303);
        createItem("minecraft:chainmail_leggings", 304);
        createItem("minecraft:chainmail_boots", 305);
        createItem("minecraft:iron_helmet", 306);
        createItem("minecraft:iron_chestplate", 307);
        createItem("minecraft:iron_leggings", 308);
        createItem("minecraft:iron_boots", 309);
        createItem("minecraft:diamond_helmet", 310);
        createItem("minecraft:diamond_chestplate", 311);
        createItem("minecraft:diamond_leggings", 312);
        createItem("minecraft:diamond_boots", 313);
        createItem("minecraft:golden_helmet", 314);
        createItem("minecraft:golden_chestplate", 315);
        createItem("minecraft:golden_leggings", 316);
        createItem("minecraft:golden_boots", 317);
        createItem("minecraft:flint", 318);
        createItem("minecraft:porkchop", 319);
        createItem("minecraft:cooked_porkchop", 320);
        createItem("minecraft:painting", 321);
        createItem("minecraft:golden_apple", 322);
        createItem("minecraft:sign", 323);
        createItem("minecraft:wooden_door", 324);
        createItem("minecraft:bucket", 325);
        createItem("minecraft:water_bucket", 326);
        createItem("minecraft:lava_bucket", 327);
        createItem("minecraft:minecart", 328);
        createItem("minecraft:saddle", 329);
        createItem("minecraft:iron_door", 330);
        createItem("minecraft:redstone", 331);
        createItem("minecraft:snowball", 332);
        createItem("minecraft:boat", 333);
        createItem("minecraft:leather", 334);
        createItem("minecraft:milk_bucket", 335);
        createItem("minecraft:brick", 336);
        createItem("minecraft:clay_ball", 337);
        createItem("minecraft:reeds", 338);
        createItem("minecraft:paper", 339);
        createItem("minecraft:book", 340);
        createItem("minecraft:slime_ball", 341);
        createItem("minecraft:chest_minecart", 342);
        createItem("minecraft:furnace_minecart", 343);
        createItem("minecraft:egg", 344);
        createItem("minecraft:compass", 345);
        createItem("minecraft:fishing_rod", 346);
        createItem("minecraft:clock", 347);
        createItem("minecraft:glowstone_dust", 348);
        createItem("minecraft:fish", 349);
        createItem("minecraft:cooked_fished", 350);
        createItem("minecraft:dye", 351);
        createItem("minecraft:bone", 352);
        createItem("minecraft:sugar", 353);
        createItem("minecraft:cake", 354);
        createItem("minecraft:bed", 355);
        createItem("minecraft:repeater", 356);
        createItem("minecraft:cookie", 357);
        createItem("minecraft:filled_map", 358);
        createItem("minecraft:shears", 359);
        createItem("minecraft:melon", 360);
        createItem("minecraft:pumpkin_seeds", 361);
        createItem("minecraft:melon_seeds", 362);
        createItem("minecraft:beef", 363);
        createItem("minecraft:cooked_beef", 364);
        createItem("minecraft:chicken", 365);
        createItem("minecraft:cooked_chicken", 366);
        createItem("minecraft:rotten_flesh", 367);
        createItem("minecraft:ender_pearl", 368);
        createItem("minecraft:blaze_rod", 369);
        createItem("minecraft:ghast_tear", 370);
        createItem("minecraft:gold_nugget", 371);
        createItem("minecraft:nether_wart", 372);
        createItem("minecraft:potion", 373);
        createItem("minecraft:glass_bottle", 374);
        createItem("minecraft:spider_eye", 375);
        createItem("minecraft:fermented_spider_eye", 376);
        createItem("minecraft:blaze_powder", 377);
        createItem("minecraft:magma_cream", 378);
        createItem("minecraft:brewing_stand", 379);
        createItem("minecraft:cauldron", 380);
        createItem("minecraft:ender_eye", 381);
        createItem("minecraft:speckled_melon", 382);
        createItem("minecraft:spawn_egg", 383);
        createItem("minecraft:experience_bottle", 384);
        createItem("minecraft:fire_charge", 385);
        createItem("minecraft:writable_book", 386);
        createItem("minecraft:written_book", 387);
        createItem("minecraft:emerald", 388);
        createItem("minecraft:item_frame", 389);
        createItem("minecraft:flower_pot", 390);
        createItem("minecraft:carrot", 391);
        createItem("minecraft:potato", 392);
        createItem("minecraft:baked_potato", 393);
        createItem("minecraft:poisonous_potato", 394);
        createItem("minecraft:map", 395);
        createItem("minecraft:golden_carrot", 396);
        createItem("minecraft:skull", 397);
        createItem("minecraft:carrot_on_a_stick", 398);
        createItem("minecraft:nether_star", 399);
        createItem("minecraft:pumpkin_pie", 400);
        createItem("minecraft:fireworks", 401);
        createItem("minecraft:firework_charge", 402);
        createItem("minecraft:enchanted_book", 403);
        createItem("minecraft:comparator", 404);
        createItem("minecraft:netherbrick", 405);
        createItem("minecraft:quartz", 406);
        createItem("minecraft:tnt_minecart", 407);
        createItem("minecraft:hopper_minecart", 408);
        createItem("minecraft:iron_horse_armor", 417);
        createItem("minecraft:golden_horse_armor", 418);
        createItem("minecraft:diamond_horse_armor", 419);
        createItem("minecraft:lead", 420);
        createItem("minecraft:name_tag", 421);
        //TODO: extend list, with 1.12 items, maybe some mod items aswell
        createItem("minecraft:record_13", 2256);
        createItem("minecraft:record_cat", 2257);
        createItem("minecraft:record_blocks", 2258);
        createItem("minecraft:record_chirp", 2259);
        createItem("minecraft:record_far", 2260);
        createItem("minecraft:record_mall", 2261);
        createItem("minecraft:record_mellohi", 2262);
        createItem("minecraft:record_stal", 2263);
        createItem("minecraft:record_strad", 2264);
        createItem("minecraft:record_ward", 2265);
        createItem("minecraft:record_11", 2266);
        createItem("minecraft:record_wait", 2267);
    }

    private static void createItem(String s, int i) {
        canonicalIdByName.put(s,i);
        nameByCanonicalId.put(i,Identifier.of(s));
    }

    public static Item getFixedItem(String name) {
        Item item = parseItemName(name);
        if (item == null) {
            throw new IllegalArgumentException("unknown item " + name);
        } else {
            return item;
        }
    }

    public static Item parseItemName(String name) {
        if (MCPatcherUtils.isNullOrEmpty(name)) {
            return null;
        }
        if (name.matches("\\d+")) {
            int id = Integer.parseInt(name);
            return instance.getItemById_Impl(id);
        }
        name = getFullName(name);
        return instance.getItemByName_Impl(name);
    }

    public static String getItemName(Item item) {
        return item == null ? "(null)" : instance.getItemName_Impl(item);
    }

    public static List<Item> getAllItems() {
        List<Item> items = new ArrayList<Item>();
        for (Iterator<Item> i = instance.iterator_Impl(); i.hasNext(); ) {
            Item item = i.next();
            if (item != null && !items.contains(item)) {
                items.add(item);
            }
        }
        return items;
    }

    public static String getFullName(String name) {
        return name == null ? null : name.indexOf(':') >= 0 ? name : "minecraft:" + name;
    }

    public static String expandTileName(String tileName) {
        return instance.expandTileName_Impl(tileName);
    }

    /*
    {
        File outputFile = new File("items17.txt");
        if (outputFile.isFile()) {
            PrintStream ps = null;
            try {
                ps = new PrintStream(outputFile);
                String[] nameList = new String[32000];
                for (String name17 : Item.itemRegistry.getKeys()) {
                    Item item = Item.itemRegistry.getValue(name17);
                    if (item != null) {
                        int id = Item.itemRegistry.getId(item);
                        if (id >= 256 && id < nameList.length) {
                            nameList[id] = name17;
                        }
                    }
                }
                for (int id = 0; id < nameList.length; id++) {
                    if (nameList[id] != null) {
                        ps.printf("createItem(\"%s\", %d);\n", nameList[id], id);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                MCPatcherUtils.close(ps);
            }
        }
    }*/

    protected Iterator<Item> iterator_Impl() {
        return Registries.ITEM.iterator();
    }

    protected Item getItemById_Impl(int id) {
        return Registries.ITEM.get((nameByCanonicalId.get(id)));
    }


    protected Item getItemByName_Impl(String name) {
        return Registries.ITEM.get(TexturePackAPI.parseIdentifier(name));
    }

    protected String getItemName_Impl(Item item) {
        Identifier name = Registries.ITEM.getId(item);//Item.itemRegistry.getKeyObject(item);

        if(name == null)
            throw new RuntimeException("getItemName_Impl -> Unregisterd item ("+item+")!");

        return name.toString(); //String.valueOf(Item.itemRegistry.getId(item))
    }

    protected String expandTileName_Impl(String tileName) {
        if (!tileName.contains(":")) {
            tileName = "minecraft:items/" + tileName;
        }
        return tileName;
    }

    ItemAPI() {
    }

}