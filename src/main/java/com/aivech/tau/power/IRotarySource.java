package com.aivech.tau.power;

import java.util.concurrent.atomic.AtomicInteger;

public interface IRotarySource extends IRotaryBlock {
    AtomicInteger getSpeed();
    AtomicInteger getTorque();
}
