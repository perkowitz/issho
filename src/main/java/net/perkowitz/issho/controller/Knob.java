package net.perkowitz.issho.controller;

import lombok.Getter;

public class Knob implements Control {

    @Getter private Side side;
    @Getter private int index;


    public Knob(Side side, int index) {
        this.side = side;
        this.index = index;
    }


    public Type getType() {
        return Type.KNOB;
    }


    /***** overrides ****************************************/

    @Override
    public boolean equals(Object object) {
        if (object instanceof Knob) {
            Knob k = (Knob) object;
            return side.equals(k.getSide()) && index == k.getIndex();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return "Knob:" + side + ":" + index;
    }


    /***** static methods *****/

    public Knob fromControl(Control control) {
        if (control.getType() != Type.KNOB) {
            return null;
        }
        return (Knob) control;
    }


}
