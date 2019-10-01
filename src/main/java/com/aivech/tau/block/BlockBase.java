package com.aivech.tau.block;

import com.aivech.tau.Tau;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

public class BlockBase extends Block {
    public final Identifier id;

    public BlockBase(Settings settings, String id) {
        super(settings);

        this.id = new Identifier(Tau.MODID, id);
    }
}
