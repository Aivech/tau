package com.aivech.tau.mixin;

import com.aivech.tau.Tau;
import com.aivech.tau.event.WorldLoadCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

public class WorldLoadEvent {
    @Mixin(MinecraftServer.class)
    public static class ServerLoadDimMixin {
        @Inject(at = @At(value = "INVOKE", shift = At.Shift.AFTER,
                target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                ordinal = 1),
                method = "createWorlds",
                locals = LocalCapture.CAPTURE_FAILHARD
        )
        private void tau_onWorldLoad_onServerLoadDims(
                WorldSaveHandler worldSaveHandler_1,
                LevelProperties levelProperties_1,
                LevelInfo levelInfo_1,
                WorldGenerationProgressListener worldGenerationProgressListener_1,
                CallbackInfo ci,
                ServerWorld serverWorld_1,
                ServerWorld serverWorld_2,
                Iterator var7,
                DimensionType dimensionType_1
        ) {
            WorldLoadCallback.EVENT.invoker().onLoad(((MinecraftServer)(Object)this).getWorld(dimensionType_1));
            Tau.Log.debug("Firing WorldLoadEvent for " + dimensionType_1.toString());
        }

        @Inject(at = @At(value = "INVOKE", shift = At.Shift.AFTER,
                target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0),
                method = "createWorlds")
        private void tau_onWorldLoad_onServerLoadOverworld(
                WorldSaveHandler worldSaveHandler_1,
                LevelProperties levelProperties_1,
                LevelInfo levelInfo_1,
                WorldGenerationProgressListener worldGenerationProgressListener_1,
                CallbackInfo ci
        ) {
            WorldLoadCallback.EVENT.invoker().onLoad(((MinecraftServer)(Object)this).getWorld(DimensionType.OVERWORLD));
            Tau.Log.debug("Firing WorldLoadEvent for Overworld");
        }
    }
}
