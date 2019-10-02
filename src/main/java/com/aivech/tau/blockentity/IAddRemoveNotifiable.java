package com.aivech.tau.blockentity;

public interface IAddRemoveNotifiable {
    /**
     * Called on the server thread when this BE is placed or loaded
     */
    void onAdd();

    /**
     * Called on the server thread when this BE is destroyed or unloaded
     */
    void onRemove();
}
