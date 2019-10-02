package com.aivech.tau.block;

import com.aivech.tau.Tau;
import com.aivech.tau.block.power.BlockShaft;
import com.aivech.tau.block.power.DebugEngineBlock;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TauBlocks {
    public static BlockBase TEST_BLOCK = createSimpleBlock(Material.STONE, "testblock");
    public static BlockShaft SHAFT = new BlockShaft();
    public static DebugEngineBlock DEBUG_ENGINE = new DebugEngineBlock();

    public static final ItemGroup BLOCKS = FabricItemGroupBuilder.build(new Identifier(Tau.MODID, "blocks"), () -> new ItemStack(TEST_BLOCK));

    public static void init() {
        registerWithItemBlock(TEST_BLOCK);
        registerWithItemBlock(SHAFT);
    }

    public static void register(BlockBase b) {
        Registry.register(Registry.BLOCK, b.id, b);
    }

    public static void registerWithItemBlock(BlockBase b) {
        register(b);
        Registry.register(Registry.ITEM, b.id, new BlockItem(b, new Item.Settings().group(BLOCKS)));
    }

    public static BlockBase createSimpleBlock(Material material, String id) {
        return new BlockBase(FabricBlockSettings.of(material).build(), id);
    }

}
