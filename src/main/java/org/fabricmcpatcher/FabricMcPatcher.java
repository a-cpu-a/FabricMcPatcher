package org.fabricmcpatcher;

import net.fabricmc.api.ModInitializer;

public class FabricMcPatcher implements ModInitializer {


    public static final String[] CHECK_FOLDERS = {"mcpatcher/","optifine/"};

    static String[] suffixFolders(String suffix)
    {
        String[] ret = CHECK_FOLDERS.clone();

        for (int i = 0; i < ret.length; i++) {
            ret[i]=ret[i]+suffix;
        }
        return ret;
    }

    //public static final String[] SKY = suffixFolders("sky");


    @Override
    public void onInitialize() {

    }
}
