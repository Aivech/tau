package com.aivech.tau.item;

import com.aivech.tau.Tau;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ItemBase extends Item {
    public final Identifier id;

    public ItemBase(Settings settings, String id) {
        super(settings);

        this.id = new Identifier(Tau.MODID, id);
    }
}
