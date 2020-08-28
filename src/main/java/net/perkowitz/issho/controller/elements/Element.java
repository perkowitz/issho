// Element represents any input or output (hardware) element that can appear on a controller.
package net.perkowitz.issho.controller.elements;

public interface Element {

    public enum Type {
        PAD, BUTTON, KNOB, LIGHT
    }

    public Type getType();
    public int getGroup();
    public int getIndex();

}
