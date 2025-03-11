package org.fabricmcpatcher.accessors;

import net.minecraft.item.ItemStack;

public interface IStackHolder {

    void fabricMcPatcher$setStack(ItemStack stack);
    ItemStack fabricMcPatcher$getStack();
}
