package com.aivech.tau.mixin;

import com.aivech.tau.Tau;
import com.aivech.tau.blockentity.IAddRemoveNotifiable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


public class BlockEntityRemoveEvent {
    @Mixin(World.class)
    public static class WorldRemoveBEMixin {
        @Inject(at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;"),
                method = "removeBlockEntity")
        private void tau_World_removeBlockEntity(BlockPos pos, CallbackInfo ci) {
            World w = (World)(Object)this;
            if (! w.isClient()) {
                BlockEntity be = w.getBlockEntity(pos);
                if (be instanceof IAddRemoveNotifiable) {
                    Tau.Log.debug("MIXIN-WORLD: Removed BE" + be.getClass().toString() + " @ " + pos.toString() + " in " + ((World)(Object)this).dimension.getType().toString());
                    ((IAddRemoveNotifiable)be).onRemove();
                }

            }

        }
    }

    @Mixin(ServerWorld.class)
    public static class ServerWorldUnloadBEMixin {
        @Inject(at = @At(value = "INVOKE",
                target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z"),
                method = "unloadEntities")
        private void tau_ServerWorld_removeBlockEntity(WorldChunk worldChunk_1, CallbackInfo ci) {
            worldChunk_1.getBlockEntities().values().forEach((blockEntity -> {
                if (blockEntity instanceof IAddRemoveNotifiable) {
                    Tau.Log.debug("MIXIN-SERVERWORLD: Removed BE" + blockEntity.getClass().toString() + " @ " + blockEntity.getPos().toString() + ((World)(Object)this).dimension.getType().toString());
                    ((IAddRemoveNotifiable)blockEntity).onRemove();
                }
            }));
        }
    }

}
