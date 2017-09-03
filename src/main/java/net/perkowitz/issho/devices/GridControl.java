package net.perkowitz.issho.devices;

import lombok.Getter;

/**
 * Created by optic on 10/27/16.
 */
public class GridControl {

    @Getter private Integer index = null;
    @Getter private GridPad pad = null;
    @Getter private GridButton button = null;
    @Getter private GridKnob knob = null;


    /***** constructors ****************************************/

    public GridControl(GridPad pad, Integer index) {
        this.pad = pad;
        this.index = index;
    }

    public GridControl(GridButton button, Integer index) {
        this.button = button;
        this.index = index;
    }

    public GridControl(GridKnob knob, Integer index) {
        this.knob = knob;
        this.index = index;
    }

    public GridControl(GridControl control, Integer index) {
        this.pad = control.pad;
        this.button = control.button;
        this.knob = control.knob;
        this.index = index;
    }


    /***** public methods ****************************************/

    public void draw(GridDisplay display, GridColor color) {
        if (pad != null) {
            display.setPad(pad, color);
        } else if (button != null) {
            display.setButton(button, color);
        } else if (knob != null) {
            //TODO display.setKnob(knob, color);
        }
    }


    /***** overrides ****************************************/

    @Override
    public String toString() {
        String s = "Control:";
        if (pad != null) {
            s += pad.toString();
        } else if (button != null) {
            s += button.toString();
        } else if (knob != null) {
            s += knob.toString();
        } else {
            s += "_";
        }
        return s;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof GridPad && pad != null) {
            return pad.equals((GridPad) object);
        } else if (object instanceof GridButton && button != null) {
            return button.equals((GridButton) object);
        } else if (object instanceof GridKnob && knob != null) {
            return knob.equals((GridKnob) object);
        } else if (object instanceof GridControl) {
            return this.toString().equals(((GridControl) object).toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }


}
