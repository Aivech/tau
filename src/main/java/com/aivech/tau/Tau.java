package com.aivech.tau;

import com.aivech.tau.block.TauBlocks;
import com.aivech.tau.item.TauItems;
import com.aivech.tau.tileentity.TauBlockEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class Tau implements ModInitializer {

    public static Logger log;
    public static final String MODID = "tau";
    public static final ItemGroup BLOCKS = FabricItemGroupBuilder.build(new Identifier(MODID, "blocks"), () -> new ItemStack(TauBlocks.REGISTRY.get("testblock")));
    public static final ItemGroup ITEMS = FabricItemGroupBuilder.build(new Identifier(MODID, "items"), () -> new ItemStack(TauItems.REGISTRY.get("testitem")));

    @Override
    public void onInitialize() {
        log = LogManager.getLogger(MODID);

        Configurator.setLevel("tau", Level.DEBUG);

        TauBlocks.init();
        TauItems.init();
        TauBlockEntities.init();
    }
}
