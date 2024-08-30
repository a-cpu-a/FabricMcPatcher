package org.fabricmcpatcher.cit;


import net.minecraft.client.render.Tessellator;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.fabricmcpatcher.resource.*;
import org.fabricmcpatcher.utils.Config;
import org.fabricmcpatcher.utils.MCLogger;
import org.fabricmcpatcher.utils.MCPatcherUtils;
import org.fabricmcpatcher.utils.PortUtils;
import org.fabricmcpatcher.utils.id.EnchantmentIdUtils;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.*;

public class CITUtils {
    private static final MCLogger logger = MCLogger.getLogger(MCPatcherUtils.CUSTOM_ITEM_TEXTURES, "CIT");

    static final String CIT_PROPERTIES = "cit.properties";
    private static Identifier CIT_PROPERTIES1;
    private static Identifier CIT_PROPERTIES2;
    static final Identifier FIXED_ARMOR_RESOURCE = Identifier.ofVanilla("textures/models/armor/iron_layer_1.png");

    static final int MAX_ENCHANTMENTS = 256;

    private static Item itemEnchantedBook;
    static Item itemCompass;
    static Item itemClock;

    static final boolean enableItems = Config.getBoolean(MCPatcherUtils.CUSTOM_ITEM_TEXTURES, "items", true);
    static final boolean enableEnchantments = Config.getBoolean(MCPatcherUtils.CUSTOM_ITEM_TEXTURES, "enchantments", true);
    static final boolean enableArmor = Config.getBoolean(MCPatcherUtils.CUSTOM_ITEM_TEXTURES, "armor", true);

    //private static TileLoader tileLoader;
    private static final Map<Item, List<ItemOverride>> items = new IdentityHashMap<Item, List<ItemOverride>>();
    private static final Map<Item, List<Enchantment>> enchantments = new IdentityHashMap<Item, List<Enchantment>>();
    private static final List<Enchantment> allItemEnchantments = new ArrayList<Enchantment>();
    private static final Map<Item, List<ArmorOverride>> armors = new IdentityHashMap<Item, List<ArmorOverride>>();

    static boolean useGlint;

    private static EnchantmentList armorMatches;
    private static int armorMatchIndex;

    private static ItemStack lastItemStack;
    private static int lastRenderPass;
    static SpriteContents lastOrigIcon;
    private static SpriteContents lastIcon;

    static {
        TexturePackChangeHandler.register(new TexturePackChangeHandler(MCPatcherUtils.CUSTOM_ITEM_TEXTURES, 3) {
            @Override
            public void beforeChange() {
                itemEnchantedBook = ItemAPI.getFixedItem("minecraft:enchanted_book");
                itemCompass = ItemAPI.getFixedItem("minecraft:compass");
                itemClock = ItemAPI.getFixedItem("minecraft:clock");

                //tileLoader = new TileLoader("textures/items", logger);
                items.clear();
                enchantments.clear();
                allItemEnchantments.clear();
                armors.clear();
                lastOrigIcon = null;
                lastIcon = null;
                try {
                    CITUtils18.clear();
                } catch (NoClassDefFoundError e) {
                    // nothing
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                BufferedImage image = TexturePackAPI.getImage(FIXED_ARMOR_RESOURCE);
                if (image == null) {
                    Enchantment.baseArmorWidth = 64.0f;
                    Enchantment.baseArmorHeight = 32.0f;
                } else {
                    Enchantment.baseArmorWidth = image.getWidth();
                    Enchantment.baseArmorHeight = image.getHeight();
                }

                CIT_PROPERTIES1 = TexturePackAPI.newMCPatcherIdentifier(CIT_PROPERTIES);
                CIT_PROPERTIES2 = TexturePackAPI.newMCPatcherIdentifier("cit/" + CIT_PROPERTIES);

                PropertiesFile properties = PropertiesFile.get(logger, CIT_PROPERTIES1);
                if (properties == null) {
                    properties = PropertiesFile.getNonNull(logger, CIT_PROPERTIES2);
                }
                useGlint = properties.getBoolean("useGlint", true);
                EnchantmentList.setProperties(properties);

                if (enableItems || enableEnchantments || enableArmor) {
                    for (Identifier resource : ResourceList.getInstance().listResources(TexturePackAPI.MCPATCHER_SUBDIR + "cit", ".properties", true)) {
                        registerOverride(OverrideBase.create(resource));
                    }
                    if (enableItems) {
                        PotionReplacer replacer = new PotionReplacer();
                        for (ItemOverride override : replacer.overrides) {
                            registerOverride(override);
                        }
                    }
                }
            }

            @Override
            public void afterChange() {
                for (List<ItemOverride> list : items.values()) {
                    for (OverrideBase override : list) {
                        ((ItemOverride) override).registerIcon(tileLoader);
                    }
                    Collections.sort(list);
                }
                for (List<Enchantment> list : enchantments.values()) {
                    list.addAll(allItemEnchantments);
                    Collections.sort(list);
                }
                Collections.sort(allItemEnchantments);
                for (List<ArmorOverride> list : armors.values()) {
                    Collections.sort(list);
                }
            }

            @SuppressWarnings("unchecked")
            private void registerOverride(OverrideBase override) {
                if (override != null && override.properties.valid()) {
                    Map map;
                    if (override instanceof ItemOverride) {
                        ((ItemOverride) override).preload(tileLoader);
                        map = items;
                    } else if (override instanceof Enchantment) {
                        map = enchantments;
                    } else if (override instanceof ArmorOverride) {
                        map = armors;
                    } else {
                        logger.severe("unknown ItemOverride type %d", override.getClass().getName());
                        return;
                    }
                    if (override.items == null) {
                        if (override instanceof Enchantment) {
                            logger.fine("registered %s to all items", override);
                            allItemEnchantments.add((Enchantment) override);
                        }
                    } else {
                        int i = 0;
                        for (Item item : override.items) {
                            registerOverride(map, item, override);
                            if (i < 10) {
                                logger.fine("registered %s to item %s", override, ItemAPI.getItemName(item));
                            } else if (i == 10) {
                                logger.fine("... %d total", override.items.size());
                            }
                            i++;
                        }
                    }
                }
            }

            private void registerOverride(Map<Item, List<OverrideBase>> map, Item item, OverrideBase override) {
                List<OverrideBase> list = map.get(item);
                if (list == null) {
                    list = new ArrayList<OverrideBase>();
                    map.put(item, list);
                }
                list.add(override);
            }
        });
    }

    public static void init() {
    }

    public static SpriteContents getIcon(SpriteContents icon, ItemStack itemStack, int renderPass) {
        if (icon == lastIcon && itemStack == lastItemStack && renderPass == lastRenderPass) {
            return icon;
        }
        lastOrigIcon = lastIcon = icon;
        lastItemStack = itemStack;
        lastRenderPass = renderPass;
        if (enableItems) {
            ItemOverride override = findItemOverride(itemStack);
            if (override != null) {
                SpriteContents newIcon = override.getReplacementIcon(icon);
                if (newIcon != null) {
                    lastIcon = newIcon;
                }
            }
        }
        return lastIcon;
    }

    public static SpriteContents getEntityIcon(SpriteContents icon, Entity entity) {
        if (entity instanceof PotionEntity) {
            return getIcon(icon, ((PotionEntity) entity).getStack(), 1);
        }
        return icon;
    }

    public static Identifier getArmorTexture(Identifier texture, LivingEntity entity, ItemStack itemStack) {
        if (enableArmor) {
            ArmorOverride override = findArmorOverride(itemStack);
            if (override != null) {
                Identifier newTexture = override.getReplacementTexture(texture);
                if (newTexture != null) {
                    return newTexture;
                }
            }
        }
        return texture;
    }

    private static <T extends OverrideBase> T findMatch(Map<Item, List<T>> overrides, ItemStack itemStack) {
        Item item = itemStack.getItem();
        List<T> list = overrides.get(item);
        if (list != null) {
            int[] enchantmentLevels = getEnchantmentLevels(item, itemStack);
            boolean hasEffect = itemStack.hasEffectVanilla();
            for (T override : list) {
                if (override.match(itemStack, enchantmentLevels, hasEffect)) {
                    return override;
                }
            }
        }
        return null;
    }

    static ItemOverride findItemOverride(ItemStack itemStack) {
        return findMatch(items, itemStack);
    }

    static ArmorOverride findArmorOverride(ItemStack itemStack) {
        return findMatch(armors, itemStack);
    }

    static EnchantmentList findEnchantments(ItemStack itemStack) {
        return new EnchantmentList(enchantments, allItemEnchantments, itemStack);
    }

    public static boolean renderEnchantmentHeld(ItemStack itemStack, int renderPass) {
        if (itemStack == null || renderPass != 0) {
            return true;
        }
        if (!enableEnchantments) {
            return false;
        }
        EnchantmentList matches = findEnchantments(itemStack);
        if (matches.isEmpty()) {
            return !useGlint;
        }
        int width;
        int height;
        if (lastIcon == null) {
            width = height = 256;
        } else {
            width = IconAPI.getIconWidth(lastIcon);
            height = IconAPI.getIconHeight(lastIcon);
        }
        Enchantment.beginOuter3D();
        for (int i = 0; i < matches.size(); i++) {
            matches.getEnchantment(i).render3D(TessellatorAPI.getTessellator(), matches.getIntensity(i), width, height);
        }
        Enchantment.endOuter3D();
        return !useGlint;
    }

    public static boolean renderEnchantmentDropped(ItemStack itemStack) {
        return renderEnchantmentHeld(itemStack, lastRenderPass);
    }

    public static boolean renderEnchantmentGUI(ItemStack itemStack, int x, int y, float z) {
        if (!enableEnchantments || itemStack == null) {
            return false;
        }
        EnchantmentList matches = findEnchantments(itemStack);
        if (matches.isEmpty()) {
            return !useGlint;
        }
        Enchantment.beginOuter2D();
        for (int i = 0; i < matches.size(); i++) {
            matches.getEnchantment(i).render2D(Tessellator.getInstance(), matches.getIntensity(i), x, y, x + 16, y + 16, z);
        }
        Enchantment.endOuter2D();
        return !useGlint;
    }

    public static boolean setupArmorEnchantments(LivingEntity entity, int pass) {
        return setupArmorEnchantments(entity.getCurrentItemOrArmor(4 - pass));
    }

    public static boolean setupArmorEnchantments(ItemStack itemStack) {
        armorMatches = null;
        armorMatchIndex = 0;
        if (enableEnchantments && itemStack != null) {
            EnchantmentList tmpList = findEnchantments(itemStack);
            if (!tmpList.isEmpty()) {
                armorMatches = tmpList;
            }
        }
        return isArmorEnchantmentActive() || !useGlint;
    }

    public static boolean preRenderArmorEnchantment() {
        if (isArmorEnchantmentActive()) {
            Enchantment enchantment = armorMatches.getEnchantment(armorMatchIndex);
            if (enchantment.bindTexture(lastOrigIcon)) {
                enchantment.beginArmor(armorMatches.getIntensity(armorMatchIndex));
                return true;
            } else {
                return false;
            }
        } else {
            armorMatches = null;
            armorMatchIndex = 0;
            return false;
        }
    }

    public static boolean isArmorEnchantmentActive() {
        return armorMatches != null && armorMatchIndex < armorMatches.size();
    }

    public static void postRenderArmorEnchantment() {
        armorMatches.getEnchantment(armorMatchIndex).endArmor();
        armorMatchIndex++;
    }

    static int[] getEnchantmentLevels(Item item, ItemStack nbt) {

        ItemEnchantmentsComponent enchList = EnchantmentHelper.getEnchantments(nbt);

        if(enchList==null || enchList.isEmpty())
            return null;

        int[] levels = null;

        for (RegistryEntry<Enchantment> e : enchList.getEnchantments()) {
            int lvl = enchList.getLevel(e);
            if(lvl<=0)
                continue;

            int id = EnchantmentIdUtils.newId2Int(e);
            if(id<0 || id>=MAX_ENCHANTMENTS)
                continue;

            if(levels==null) levels = new int[MAX_ENCHANTMENTS];

            levels[id]+=lvl;
        }

        /*
        if (nbt != null) {
            NbtElement base;
            if (item == itemEnchantedBook) {
                base = nbt.get("StoredEnchantments");
            } else {
                base = nbt.get("ench");
            }
            if (base instanceof AbstractNbtList<?> list) {
                for (int i = 0; i < list.size(); i++) {
                    base = list.get(i);
                    if (base instanceof NbtCompound) {
                        short id = ((NbtCompound) base).getShort("id");
                        short level = ((NbtCompound) base).getShort("lvl");
                        if (id >= 0 && id < MAX_ENCHANTMENTS && level > 0) {
                            if (levels == null) {
                                levels = new int[MAX_ENCHANTMENTS];
                            }
                            levels[id] += level;
                        }
                    }
                }
            }
        }*/
        return levels;
    }
}