package com.aivech.tau;

import com.aivech.tau.block.TauBlocks;
import com.aivech.tau.blockentity.TauBEs;
import com.aivech.tau.item.TauItems;
import com.aivech.tau.power.RotaryGrid;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class Tau implements ModInitializer {

    private static Logger log;
    public static final String MODID = "tau";

    @Override
    public void onInitialize() {
        log = LogManager.getLogger(MODID);
        Log.info("For the greater good.");

        Configurator.setLevel(MODID, Level.DEBUG);

        TauBlocks.init();
        TauItems.init();
        TauBEs.init();

        RotaryGrid.registerHandlers();
    }

    public static class Log {
        public static void debug(String s) {
            log.debug("[Tau] "+s);
        }
        public static void info(String s) {
            log.info("[Tau] "+s);
        }
        public static void error(String s) { log.error("[Tau] "+s);}
        public static void fatal(String s) { log.fatal("[Tau] "+s); }

    }

    public static boolean containsWord(String s, String word) {
        if (s == null || s.length() < word.length()) return false;

        int len = word.length();
        if (s.substring(0, len).equals(word)) {
            if (s.length() == word.length() || ! isWordChar(s.charAt(len))) return true;
        }
        for (int i = 0; i < s.length() - len; i++) {
            if (! isWordChar(s.charAt(i))) {
                if (s.substring(i + 1, i + 1 + len).equals(word)) {
                    if (word.length() + i + 1 == s.length() || ! isWordChar(s.charAt(i + 1 + len))) return true;
                }
            }
        }
        return false;
    }

    public static boolean isWordChar(char c) {
        int codepoint = c;
        if (codepoint == 0x27) return true; // an apostrophe
        if (codepoint >= 0x30 && codepoint <= 0x39) return true; // 0-9
        if (codepoint >= 0x41 && codepoint <= 0x5A) return true; // A-Z
        return codepoint >= 0x61 && codepoint <= 0x7a;          // a-z
    }
}
