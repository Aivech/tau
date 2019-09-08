package com.aivech.tau.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.World;

public interface WorldLoadCallback {
    Event<WorldLoadCallback> EVENT = EventFactory.createArrayBacked(WorldLoadCallback.class,
            (listeners) -> (world) -> {
                for (WorldLoadCallback event : listeners) {
                    event.onLoad(world);
                }
            });

    void onLoad(World world);
}
