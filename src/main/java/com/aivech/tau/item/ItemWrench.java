package com.aivech.tau.item;

import com.aivech.tau.block.IRotatable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class ItemWrench extends ItemBase {
    public ItemWrench() {
        super(new Item.Settings().group(TauItems.ITEMS), "wrench");
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World w = context.getWorld();
        BlockState blockState = w.getBlockState(context.getBlockPos());
        Block block = blockState.getBlock();
        if (block instanceof IRotatable) {
            if (! w.isClient()) {
                if (! context.isPlayerSneaking()) {
                    ((IRotatable)block).rotate(blockState, w, context.getBlockPos(), context.getPlayer());
                } else {
                    ((IRotatable)block).invRotate(blockState, w, context.getBlockPos(), context.getPlayer());
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}