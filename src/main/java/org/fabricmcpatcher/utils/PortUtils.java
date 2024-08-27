package org.fabricmcpatcher.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.fabricmcpatcher.FabricMcPatcher;

import java.util.Optional;

public class PortUtils {
    public static int getWorldId(ClientWorld world) {
        return world.getDimensionEffects().getSkyType().ordinal()-1;
    }

    //in mcpatcher or optifine folder
    public static Identifier findResource(String resource) {
        for (String folder : FabricMcPatcher.CHECK_FOLDERS) {
            Identifier testId = Identifier.ofVanilla(folder+resource);
            Optional<Resource> testRes =  MinecraftClient.getInstance().getResourceManager().getResource(testId);
            if(testRes.isEmpty())
                continue;

            return testId;
        }
        return null;
    }
}
