package org.fabricmcpatcher.mixins.color;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profilers;
import org.fabricmcpatcher.color.Lightmap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow @Final private SimpleFramebuffer lightmapFramebuffer;

    @Inject(method = "update",at= @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",shift = At.Shift.AFTER),cancellable = true)
    void updatePush(float delta, CallbackInfo ci) {

        ClientWorld clientWorld = client.world;
        if (Lightmap.computeLightmap(client.gameRenderer, clientWorld, delta)) {


            ShaderProgram shaderProgram = Objects.requireNonNull(RenderSystem.setShader(Lightmap.CUSTOM_LIGHTMAP), "Custom lightmap shader not loaded");
            shaderProgram.getUniformOrDefault("sun").set(Lightmap.sun);
            shaderProgram.getUniformOrDefault("torch").set(Lightmap.torch);
            shaderProgram.getUniformOrDefault("nightVisionStrength").set(Lightmap.nightVisionStrength);
            shaderProgram.getUniformOrDefault("gamma").set(Lightmap.gamma);
            shaderProgram.getUniformOrDefault("customNightvision").set(Lightmap.usesCustomNightvision);
            shaderProgram.getUniformOrDefault("heightR").set(Lightmap.heightR);
            shaderProgram.addSamplerTexture("origMap",client.getTextureManager().getTexture(Lightmap.origMap).getGlId());
            this.lightmapFramebuffer.beginWrite(true);
            BufferBuilder bufferBuilder = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.BLIT_SCREEN);
            bufferBuilder.vertex(0.0F, 0.0F, 0.0F);
            bufferBuilder.vertex(1.0F, 0.0F, 0.0F);
            bufferBuilder.vertex(1.0F, 1.0F, 0.0F);
            bufferBuilder.vertex(0.0F, 1.0F, 0.0F);
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            this.lightmapFramebuffer.endWrite();
            Profilers.get().pop();


            ci.cancel();
        }
    }
}
