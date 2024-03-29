package com.aivech.tau.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.aivech.tau.Tau.MODID;

public class TauItems {
    public static final ItemGroup ITEMS = FabricItemGroupBuilder.build(new Identifier(MODID, "items"), () -> new ItemStack(TauItems.WRENCH));

    public static final ItemWrench WRENCH = new ItemWrench();

    public static void init() {
        register(WRENCH);
    }

    private static ItemBase createSimpleItem(String id) {
        return new ItemBase(new Item.Settings().group(ITEMS), id);
    }

    private static void register(ItemBase item) {
        Registry.register(Registry.ITEM, item.id, item);
    }
}