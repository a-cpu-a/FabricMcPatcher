package org.fabricmcpatcher.mixins;

import net.minecraft.client.gui.hud.BossBarHud;
import org.fabricmcpatcher.color.ColorizeWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BossBarHud.class)
public class BossBarHudMixin
{
    @ModifyConstant(method = "render",constant = @Constant(intValue = 16777215))
    int render16777215(int constant) {
        if(ColorizeWorld.colorizeBossBarText()!=-1)
            return ColorizeWorld.colorizeBossBarText();

        return constant;
    }
}
