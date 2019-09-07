package com.aivech.tau.tileentity;

import com.aivech.tau.Tau;
import com.aivech.tau.block.BlockBase;
import com.aivech.tau.block.TauBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;

public class TauTileEntities {
    public static final HashMap<String, BlockEntityType<TileEntityBase>> REGISTRY = new HashMap<>();

    public static void init() {
        register(TauBlocks.REGISTRY.get("te_test"));
    }

    public static void register(BlockBase b) {
        TypeDelegate type = new TypeDelegate();
        type.type = Registry.register(
                Registry.BLOCK_ENTITY,
                new Identifier(Tau.MODID,b.id),
                BlockEntityType.Builder.create(() -> new TileEntityBase(type.type),b).build(null));
        REGISTRY.put(b.id, type.type);

    }

    public static class TypeDelegate {
        BlockEntityType<TileEntityBase> type;
    }
}
