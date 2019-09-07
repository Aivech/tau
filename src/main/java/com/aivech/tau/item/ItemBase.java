package com.aivech.tau.item;

import net.minecraft.item.Item;

public class ItemBase extends Item {
    public final String id;

    public ItemBase(Settings settings, String id) {
        super(settings);

        this.id = id;
    }
}
