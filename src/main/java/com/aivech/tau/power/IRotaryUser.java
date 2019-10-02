package com.aivech.tau.power;

import java.util.concurrent.atomic.AtomicInteger;

public interface IRotaryUser extends IRotaryBE {
    void setSpeed(AtomicInteger s);
    void setTorque(AtomicInteger t);
}
