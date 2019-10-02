package com.aivech.tau.item;

import com.aivech.tau.Tau;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class ItemWrench extends ItemBase {
    public ItemWrench() {
        super(new Item.Settings().group(TauItems.ITEMS), "wrench");
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Tau.Log.debug(context.getBlockPos().toString());
        return ActionResult.PASS;
    }
}