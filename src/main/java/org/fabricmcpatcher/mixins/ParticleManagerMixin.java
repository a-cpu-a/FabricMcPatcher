package org.fabricmcpatcher.mixins;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import org.fabricmcpatcher.sky.FireworksHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @Mutable
    @Shadow @Final private static List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS;

    static {
        PARTICLE_TEXTURE_SHEETS = ImmutableList.<ParticleTextureSheet>builder()
                .addAll(PARTICLE_TEXTURE_SHEETS)
                .add(FireworksHelper.PARTICLE_SHEET_FIREWORKS)
                .build();
    }
}
