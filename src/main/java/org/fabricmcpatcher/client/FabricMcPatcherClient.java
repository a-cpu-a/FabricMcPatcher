package org.fabricmcpatcher.client;

import net.fabricmc.api.ClientModInitializer;

public class FabricMcPatcherClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        try {
            Class.forName("org.fabricmcpatcher.color.Colorizer");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
