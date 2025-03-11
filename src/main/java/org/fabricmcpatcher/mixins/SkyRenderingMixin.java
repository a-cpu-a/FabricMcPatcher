package org.fabricmcpatcher.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.SkyRendering;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.fabricmcpatcher.color.ColorizeWorld;
import org.fabricmcpatcher.sky.SkyRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRendering.class)
public abstract class SkyRenderingMixin {

    @Mutable
    @Shadow @Final private VertexBuffer endSkyBuffer;

    @Shadow protected abstract void tessellateEndSky(VertexConsumer vertexConsumer);

    /*
    *
    * Disable default blending if custom skies are enabled
    * And change the texture
    *
    * */
//TODO:
    @Redirect(method = "renderSun",at= @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;overlayBlendFunc()V",remap = false))
    void renderSunOverlayBlendFunc() {
        if(SkyRenderer.sunTex!=null)return;
        RenderSystem.overlayBlendFunc();
    }
    @WrapOperation(method = "renderSun",at= @At(value = "FIELD", target = "Lnet/minecraft/client/render/SkyRendering;SUN_TEXTURE:Lnet/minecraft/util/Identifier;"))
    Identifier renderSetShaderTexture(Operation<Identifier> original) {
        if(SkyRenderer.sunTex!=null) {
            return SkyRenderer.sunTex;
        }
        return original.call();
    }


    //TODO:
    @Redirect(method = "renderMoon",at= @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;overlayBlendFunc()V",remap = false))
    void renderMoonOverlayBlendFunc() {
        if(SkyRenderer.moonTex!=null)return;
        RenderSystem.overlayBlendFunc();
    }
    @WrapOperation(method = "renderMoon",at= @At(value = "FIELD", target = "Lnet/minecraft/client/render/SkyRendering;MOON_PHASES_TEXTURE:Lnet/minecraft/util/Identifier;"))
    Identifier renderMoonShaderTexture(Operation<Identifier> original) {
        if(SkyRenderer.moonTex!=null) {
            return SkyRenderer.moonTex;
        }
        return original.call();
    }
    @ModifyConstant(method = "tessellateEndSky",constant = @Constant(intValue = -14145496))
    int tessellateEndSkyM14145496(int constant) {
        return ColorizeWorld.endSkyColor;
    }
    @Inject(method = "renderEndSky",at=@At(value = "HEAD"))
    void renderEndSkyHead(CallbackInfo ci) {
        if(ColorizeWorld.endSkyColorPrev != ColorizeWorld.endSkyColor) {
            ColorizeWorld.endSkyColorPrev = ColorizeWorld.endSkyColor;

            endSkyBuffer.close();
            endSkyBuffer = VertexBuffer.createAndUpload(
                    VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR, this::tessellateEndSky
            );

        }
    }
}
