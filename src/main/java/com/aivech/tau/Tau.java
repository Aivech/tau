package com.aivech.tau;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tau implements ModInitializer {

    public static Logger tauLog;

    @Override
    public void onInitialize() {
        tauLog = LogManager.getLogger("Tau");
    }
}
