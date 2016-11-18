package net.perkowitz.issho.devices;

import lombok.Getter;

/**
 * Created by optic on 10/27/16.
 */
public class GridControl {

    @Getter private Integer index = null;
    @Getter private GridPad pad = null;
    @Getter private GridButton button = null;


    /***** constructors ****************************************/

    public GridControl(GridPad pad, Integer index) {
        this.pad = pad;
        this.index = index;
    }

    public GridControl(GridButton button, Integer index) {
        this.button = button;
        this.index = index;
    }


    /***** public methods ****************************************/

    public void draw(GridDisplay display, GridColor color) {
        if (pad != null) {
            display.setPad(pad, color);
        } else if (button != null) {
            display.setButton(button, color);
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
