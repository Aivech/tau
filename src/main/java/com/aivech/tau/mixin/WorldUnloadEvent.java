package com.aivech.tau.mixin;

import com.aivech.tau.Tau;
import com.aivech.tau.event.WorldUnloadCallback;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class WorldUnloadEvent {
    @Inject(at = @At(value="INVOKE",
            target = "net/minecraft/world/chunk/ChunkManager.close()V"),
            method = "close"
    )
    private void tau_onWorldUnload(CallbackInfo ci){
        WorldUnloadCallback.EVENT.invoker().onUnload((World)(Object)this);
        Tau.Log.debug("Firing WorldUnload event for "+((World)(Object)this).getDimension().getType().toString());
    }

}