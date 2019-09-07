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
    public static ItemGroup blocks;
    public static ItemGroup items;

    @Override
    public void onInitialize() {
        log = LogManager.getLogger(MODID);

        TauBlocks.init();
        TauItems.init();
        TauTileEntities.init();

        blocks = FabricItemGroupBuilder.create(
                new Identifier(MODID,"blocks"))
                .icon(()-> new ItemStack(TauBlocks.REGISTRY.get("testblock")))
                .appendItems(stacks ->
                {
                    for (BlockBase b :TauBlocks.REGISTRY.values()) {
                        stacks.add(new ItemStack(b));
                    }
                })
                .build();

        items = FabricItemGroupBuilder.create(
                new Identifier(MODID,"items"))
                .icon(()-> new ItemStack(TauItems.REGISTRY.get("testitem")))
                .appendItems(stacks ->
                {
                    for (ItemBase i :TauItems.REGISTRY.values()) {
                        stacks.add(new ItemStack(i));
                    }
                })
                .build();
    }
}
