package com.aivech.tau.block;

import com.aivech.tau.Tau;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;

public class TauBlocks {
    public static final HashMap<String,BlockBase> TAU_BLOCKS = new HashMap();

    public static void register(BlockBase b) {
        TAU_BLOCKS.put(b.id,b);
        Registry.register(Registry.BLOCK, new Identifier(Tau.MODID,b.id),b);
    }

    public static void registerWithItemBlock(BlockBase b) {
        register(b);
        Registry.register(Registry.ITEM, new Identifier(Tau.MODID,b.id),new BlockItem(b,new Item.Settings().group(ItemGroup.MISC)));
    }

    public static void initBlocks() {
        registerWithItemBlock(new BlockBase(FabricBlockSettings.of(Material.STONE).build(),"testblock"));
    }
}
