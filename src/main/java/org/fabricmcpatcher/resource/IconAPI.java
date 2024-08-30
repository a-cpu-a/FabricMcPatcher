package org.fabricmcpatcher.resource;


import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.Sprite;

public class IconAPI {
    private static final IconAPI instance = new IconAPI();

    public static boolean needRegisterTileAnimations() {
        return instance.needRegisterTileAnimations_Impl();
    }

    public static int getIconX0(Sprite icon) {
        return instance.getIconX0_Impl(icon);
    }

    public static int getIconY0(Sprite icon) {
        return instance.getIconY0_Impl(icon);
    }

    public static int getIconWidth(SpriteContents icon) {
        return instance.getIconWidth_Impl(icon);
    }

    public static int getIconHeight(SpriteContents icon) {
        return instance.getIconHeight_Impl(icon);
    }

    public static String getIconName(SpriteContents icon) {
        return instance.getIconName_Impl(icon);
    }


    protected boolean needRegisterTileAnimations_Impl() {
        return true;
    }

    protected int getIconX0_Impl(Sprite icon) {
        return icon.getX();
    }

    protected int getIconY0_Impl(Sprite icon) {
        return icon.getY();
    }

    protected int getIconWidth_Impl(SpriteContents icon) {
        return icon.getWidth();
    }

    protected int getIconHeight_Impl(SpriteContents icon) {
        return icon.getHeight();
    }

    protected String getIconName_Impl(SpriteContents icon) {
        return icon.getId().toString();
    }
}