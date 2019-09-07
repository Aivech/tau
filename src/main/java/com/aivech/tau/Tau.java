package com.aivech.tau;

import com.aivech.tau.block.BlockBase;
import com.aivech.tau.block.TauBlocks;
import com.aivech.tau.item.ItemBase;
import com.aivech.tau.item.TauItems;
import com.aivech.tau.tileentity.TauTileEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tau implements ModInitializer {

    public static Logger log;
    public static final String MODID = "tau";
    public static final ItemGroup BLOCKS = FabricItemGroupBuilder.build(new Identifier(MODID,"blocks"),()-> new ItemStack(TauBlocks.REGISTRY.get("testblock")));;
    public static final ItemGroup ITEMS = FabricItemGroupBuilder.build(new Identifier(MODID,"items"),()-> new ItemStack(TauItems.REGISTRY.get("testitem")));;

    @Override
    public void onInitialize() {
        log = LogManager.getLogger(MODID);

        TauBlocks.init();
        TauItems.init();
        TauTileEntities.init();
    }
}
