package com.aivech.tau.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.World;

public interface WorldUnloadCallback {
    Event<WorldUnloadCallback> EVENT = EventFactory.createArrayBacked(WorldUnloadCallback.class,
            (listeners) -> (world) -> {
                for (WorldUnloadCallback event : listeners) {
                    event.onUnload(world);
                }
            });

    void onUnload(World world);
}
