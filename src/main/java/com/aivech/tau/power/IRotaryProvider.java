package com.aivech.tau.power;

import java.util.concurrent.atomic.AtomicInteger;

public interface IRotaryProvider extends IRotaryBlock {
    AtomicInteger getSpeed();
    AtomicInteger getTorque();
}
