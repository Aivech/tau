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
    public static final HashMap<String,BlockBase> REGISTRY = new HashMap();

    public static void init() {
        registerWithItemBlock(new BlockBase(FabricBlockSettings.of(Material.STONE).build(),"testblock"));
        registerWithItemBlock(new TEBlockTest());
    }

    public static void register(BlockBase b) {
        REGISTRY.put(b.id,b);
        Registry.register(Registry.BLOCK, new Identifier(Tau.MODID,b.id),b);
    }

    public static void registerWithItemBlock(BlockBase b) {
        register(b);
        Registry.register(Registry.ITEM, new Identifier(Tau.MODID,b.id),new BlockItem(b,new Item.Settings().group(Tau.BLOCKS)));
    }


}
