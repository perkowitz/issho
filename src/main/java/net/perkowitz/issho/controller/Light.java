package net.perkowitz.issho.controller;

import lombok.Getter;

public class Light implements Control {

    @Getter private Side side;
    @Getter private int index;

    public Light(Side side, int index) {
        this.side = side;
        this.index = index;
    }


    public Type getType() {
        return Type.LIGHT;
    }


    /***** overrides ****************************************/

    @Override
    public boolean equals(Object object) {
        if (object instanceof Light) {
            Light l = (Light) object;
            return side.equals(l.getSide()) && index == l.getIndex();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return "Light:" + side + ":" + index;
    }


    /***** static methods *****/

    public Light fromControl(Control control) {
        if (control.getType() != Type.LIGHT) {
            return null;
        }
        return (Light)control;
    }


}
