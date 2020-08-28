package net.perkowitz.issho.controller;

public interface Control {

    public enum Type {
        PAD, BUTTON, KNOB, LIGHT
    }

    public enum Side {
        TOP, BOTTOM, LEFT, RIGHT, OTHER
    }


    public Type getType();

}
