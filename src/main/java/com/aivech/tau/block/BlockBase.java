package com.aivech.tau.block;

import net.minecraft.block.Block;

public class BlockBase extends Block {
    public final String id;

    public BlockBase(Settings settings, String id) {
        super(settings);

        this.id = id;
    }
}
