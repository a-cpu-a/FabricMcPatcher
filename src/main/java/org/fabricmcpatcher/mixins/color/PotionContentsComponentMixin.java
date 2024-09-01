package org.fabricmcpatcher.mixins.color;

import net.minecraft.component.type.PotionContentsComponent;
import org.fabricmcpatcher.color.ColorizeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PotionContentsComponent.class)
public class PotionContentsComponentMixin {

    @ModifyConstant(method = "getColor(Ljava/lang/Iterable;)I", constant = @Constant(intValue = -13083194))
    private static int constantM13083194(int constant) {

        return ColorizeItem.getWaterBottleColor();
    }
}
