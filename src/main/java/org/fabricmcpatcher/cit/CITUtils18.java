package org.fabricmcpatcher.cit;

import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.fabricmcpatcher.resource.FaceInfo;
import org.fabricmcpatcher.utils.MCLogger;
import org.fabricmcpatcher.utils.MCPatcherUtils;

public class CITUtils18 {
    private static final MCLogger logger = MCLogger.getLogger(MCPatcherUtils.CUSTOM_ITEM_TEXTURES, "CIT");

    private static ItemStack currentItem;
    //private static CTMUtils18 ctm;//TODO
    private static ItemOverride itemOverride;
    private static boolean renderingEnchantment;

    public static void preRender(ItemStack itemStack) {
        if (renderingEnchantment) {
            // rendering custom enchantment -- keep current state
        } else if (itemStack == null) {
            clear();
        } else if (itemStack.getItem() instanceof BlockItem) {
            clear();
            /*ctm = CTMUtils18.getInstance();
            ctm.preRenderHeld(null, ((BlockItem) itemStack.getItem()).getBlock(), itemStack.getDamage());*///TODO
        } else {
            //ctm = null;//TODO
            currentItem = itemStack;
            itemOverride = CITUtils.findItemOverride(itemStack);
        }
    }

    public static void postRender() {
        /*if (ctm != null) {
            ctm.clear();
        }*/ //TODO
        clear();
    }

    public static ModelFace getModelFace(ModelFace origFace) {
        if (renderingEnchantment) {
            return FaceInfo.getFaceInfo(origFace).getNonAtlasFace();
        } /*else if (ctm != null) {
            int face = FaceInfo.getFaceInfo(origFace).getEffectiveFace();
            ctm.setDirection(face < 0 ? null : Direction.values()[face]);
            return ctm.getModelFace(origFace); //TODO
        }*/ else if (itemOverride == null) {
            return origFace;
        } else {
            FaceInfo faceInfo = FaceInfo.getFaceInfo(origFace);
            Sprite newIcon =  itemOverride.getReplacementIcon(faceInfo.getSprite());
            return faceInfo.getAltFace(newIcon);
        }
    }

    public static boolean renderEnchantments3D(RenderItemCustom renderItem, IModel model) {
        if (currentItem != null) {
            EnchantmentList enchantments = CITUtils.findEnchantments(currentItem);
            if (!enchantments.isEmpty()) {
                renderingEnchantment = true;
                Enchantment.beginOuter3D();
                for (int i = 0; i < enchantments.size(); i++) {
                    Enchantment enchantment = enchantments.getEnchantment(i);
                    float intensity = enchantments.getIntensity(i);
                    if (intensity > 0.0f && enchantment.bindTexture(null)) {
                        enchantment.begin(intensity);
                        renderItem.renderItem1(model, -1, null);
                        enchantment.end();
                    }
                }
                Enchantment.endOuter3D();
                TexturePackAPI.bindTexture(TexturePackAPI.ITEMS_PNG);
                renderingEnchantment = false;
            }
        }
        return !CITUtils.useGlint;
    }

    public static Identifier getArmorTexture(Identifier origTexture, ItemStack itemStack, int slot) {
        ArmorOverride override = CITUtils.findArmorOverride(itemStack);
        if (override == null) {
            return origTexture;
        } else {
            return override.getReplacementTexture(origTexture);
        }
    }

    public static boolean renderArmorEnchantments(LivingEntity entity, ModelBase model, ItemStack itemStack, int slot, float f1, float f2, float f3, float f4, float f5, float f6) {
        EnchantmentList enchantments = CITUtils.findEnchantments(itemStack);
        if (!enchantments.isEmpty()) {
            Enchantment.beginOuter3D();
            for (int i = 0; i < enchantments.size(); i++) {
                Enchantment enchantment = enchantments.getEnchantment(i);
                float intensity = enchantments.getIntensity(i);
                if (intensity > 0.0f && enchantment.bindTexture(null)) {
                    enchantment.begin(intensity);
                    model.render(entity, f1, f2, f3, f4, f5, f6);
                    enchantment.end();
                }
            }
            Enchantment.endOuter3D();
        }
        return !CITUtils.useGlint;
    }

    static void clear() {
        currentItem = null;
        //ctm = null;//TODO
        itemOverride = null;
        renderingEnchantment = false;
    }
}