package com.aivech.tau.power;

public class RotaryNode {
    public int speed;
    public int torque;

    public final NodeType type;

    public RotaryNode(NodeType type) {
        this.type = type;
    }

    public enum NodeType {
        SOURCE, SINK, PATH
    }
}
