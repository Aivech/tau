package com.aivech.tau.power;

import java.util.concurrent.atomic.AtomicInteger;

public interface IRotaryTransform extends IRotaryBE {
    AtomicInteger getTorqueFactor();
    AtomicInteger getSpeedFactor();
}
