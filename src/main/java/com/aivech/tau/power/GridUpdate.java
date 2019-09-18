package com.aivech.tau.power;

public abstract class GridUpdate {
    protected final GridAction action;

    public GridUpdate(GridAction action) {
        this.action = action;
    }

    public class Add extends GridUpdate {
        final RotaryNode node;
        public Add(RotaryNode node) {
            super(GridAction.ADD);
            this.node = node;
        }
    }

    public enum GridAction {
        ADD, DEL, UPDATE
    }
}
