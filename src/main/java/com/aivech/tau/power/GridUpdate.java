package com.aivech.tau.power;

public class GridUpdate {
    public final Action action;

    public GridUpdate(Action action) {
        this.action = action;
    }


    public enum Action {
        ADD, DEL, UPDATE
    }
}
