package com.aivech.tau;

import com.aivech.tau.block.TauBlocks;
import com.aivech.tau.item.TauItems;
import com.aivech.tau.tileentity.TauTileEntities;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tau implements ModInitializer {

    public static Logger log;
    public static final String MODID = "tau";

    @Override
    public void onInitialize() {
        log = LogManager.getLogger(MODID);

        TauBlocks.init();
        TauItems.init();
        TauTileEntities.init();
    }
}
