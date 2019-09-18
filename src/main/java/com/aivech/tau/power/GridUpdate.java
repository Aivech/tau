package com.aivech.tau.power;

public abstract class GridUpdate {
    public final GridAction action;

    public GridUpdate(GridAction action) {
        this.action = action;
    }

    public class Add extends GridUpdate {
        public Add() {
            super(GridAction.ADD);
        }
    }

    public enum GridAction {
        ADD, DEL, UPDATE
    }
}
