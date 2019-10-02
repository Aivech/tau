package com.aivech.tau.power;

import java.util.concurrent.atomic.AtomicBoolean;

public interface IRotaryClutch extends IRotaryBE {
    AtomicBoolean isEngaged();
}
