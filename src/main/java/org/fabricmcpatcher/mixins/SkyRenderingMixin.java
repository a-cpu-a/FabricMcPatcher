package org.fabricmcpatcher.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.SkyRendering;
import net.minecraft.util.Identifier;
import org.fabricmcpatcher.sky.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SkyRendering.class)
public class SkyRenderingMixin {

    /*
    *
    * Disable default blending if custom skies are enabled
    * And change the texture
    *
    * */

    @Redirect(method = "renderSun",at= @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;overlayBlendFunc()V",remap = false))
    void renderSunOverlayBlendFunc() {
        if(SkyRenderer.sunTex!=null)return;
        RenderSystem.overlayBlendFunc();
    }
    @Redirect(method = "renderSun",at= @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V"))
    void renderSetShaderTexture(int texture, Identifier id) {
        if(SkyRenderer.sunTex!=null) {
            RenderSystem.setShaderTexture(texture,SkyRenderer.sunTex);
            return;
        }
        RenderSystem.setShaderTexture(texture,id);
    }


    @Redirect(method = "renderMoon",at= @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;overlayBlendFunc()V",remap = false))
    void renderMoonOverlayBlendFunc() {
        if(SkyRenderer.moonTex!=null)return;
        RenderSystem.overlayBlendFunc();
    }
    @Redirect(method = "renderMoon",at= @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V"))
    void renderMoonShaderTexture(int texture, Identifier id) {
        if(SkyRenderer.moonTex!=null) {
            RenderSystem.setShaderTexture(texture,SkyRenderer.moonTex);
            return;
        }
        RenderSystem.setShaderTexture(texture,id);
    }
}
