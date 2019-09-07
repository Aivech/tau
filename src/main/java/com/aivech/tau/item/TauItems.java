package com.aivech.tau.item;

import com.aivech.tau.Tau;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;

public class TauItems {
    public static final HashMap<String, ItemBase> TAU_ITEMS = new HashMap();

    public static void register(ItemBase i) {
        TAU_ITEMS.put(i.id,i);
        Registry.register(Registry.ITEM,new Identifier(Tau.MODID,i.id),i);
    }

    public static void initItems() {
        register(new ItemBase(new Item.Settings().group(ItemGroup.MISC),"testitem"));
    }
}
