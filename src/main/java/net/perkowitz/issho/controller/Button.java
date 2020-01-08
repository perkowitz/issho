package net.perkowitz.issho.controller;

import lombok.Getter;
import net.perkowitz.issho.devices.GridKnob;

public class Button implements Control {

    @Getter private Side side;
    @Getter private int index;

    public Button(Side side, int index) {
        this.side = side;
        this.index = index;
    }


    public Type getType() {
        return Type.BUTTON;
    }


    /***** overrides ****************************************/

    @Override
    public boolean equals(Object object) {
        if (object instanceof Button) {
            Button b = (Button) object;
            return side.equals(b.getSide()) && index == b.getIndex();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return "Button:" + side + ":" + index;
    }


    /***** static methods *****/

    public static Button at(Side side, int index) {
        return new Button(side, index);
    }

    public Button fromControl(Control control) {
        if (control.getType() != Type.BUTTON) {
            return null;
        }
        return (Button)control;
    }

}
