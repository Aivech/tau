package com.aivech.tau.power;

public interface IRotaryTransmuter extends IRotaryTile {
    /**
     * Get the ConvertMode for this object. Return ConvertMode.TORQUE to increase torque, SPEED to increase speed.
     * @return the dimension that will be increased
     */
    ConvertMode getConvertMode();

    /**
     * Get the multiplier applied to the selected mode. The other dimension will be divided by this number.
     * Multiplier should be a power of 2.
     * @return A multiplier, usually between 2 and 256.
     */
    long getMultiplier();


    enum ConvertMode {
        TORQUE, SPEED
    }
}
