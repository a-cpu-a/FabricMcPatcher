package org.fabricmcpatcher.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.item.ItemStack;

public class FabricMcPatcherClient implements ClientModInitializer {


    public static ItemStack itemRendererCurrentStack;

    @Override
    public void onInitializeClient() {
        try {
            Class.forName("org.fabricmcpatcher.color.Colorizer");
            Class.forName("org.fabricmcpatcher.color.ColorizeBlock18");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
