package com.aivech.tau.power;

import java.util.concurrent.atomic.AtomicInteger;

public interface IRotaryConsumer extends IRotaryBlock {
    AtomicInteger setSpeed();
    AtomicInteger setTorque();
}
