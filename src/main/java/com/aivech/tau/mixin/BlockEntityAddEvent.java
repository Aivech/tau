package com.aivech.tau.mixin;

import com.aivech.tau.blockentity.BlockEntityBase;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class BlockEntityAddEvent {
    @Mixin(World.class)
    public static class WorldMixin {
        @Inject(at = @At(value = "HEAD"), method = "addBlockEntity")
        private void tau_World_setBlockEntity_1(BlockEntity blockEntity_1, CallbackInfoReturnable<Boolean> cir) {
            if (! ((World)(Object)this).isClient() && blockEntity_1 instanceof BlockEntityBase) {
                ((BlockEntityBase)blockEntity_1).onAdd();
            }
        }
    }
}
