package org.fabricmcpatcher.mixins.color.block;

import net.minecraft.block.RedstoneWireBlock;
import org.fabricmcpatcher.color.ColorizeBlock;
import org.fabricmcpatcher.color.Colorizer;
import org.joml.Vector3f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin {
    @Inject(method = "getWireColor",at=@At(value = "HEAD"), cancellable = true)
    private static void getWireColorHead(int powerLevel, CallbackInfoReturnable<Integer> cir) {
        if(ColorizeBlock.computeRedstoneWireColor(powerLevel))
            cir.setReturnValue(ColorizeBlock.colorizeRedstoneWire(powerLevel));
    }
    @Redirect(method = "randomDisplayTick",at= @At(value = "FIELD",opcode = Opcodes.GETSTATIC,args = "array=get", target = "Lnet/minecraft/block/RedstoneWireBlock;COLORS:[Lorg/joml/Vector3f;"))
    private Vector3f randomDisplayTickColorsGetElement(Vector3f[] arr,int powerLevel) {
        if(ColorizeBlock.computeRedstoneWireColor(powerLevel))
            return new Vector3f(Colorizer.setColor[0],Colorizer.setColor[1],Colorizer.setColor[2]);
        return arr[powerLevel];
    }
}
