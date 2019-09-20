package com.aivech.tau.power;

import java.util.concurrent.atomic.AtomicInteger;

public interface IRotaryTransform {
    AtomicInteger getTorqueFactor();
    AtomicInteger getSpeedFactor();
}
