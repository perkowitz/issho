package net.perkowitz.issho.hachi.modules.mono;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridColor;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridPad;

import java.util.Map;

/**
 * Created by optic on 10/27/16.
 */
public class GridControl {

    @Getter private GridPad pad = null;
    @Getter private GridButton button = null;


    /***** constructors ****************************************/

    public GridControl(GridPad pad) {
        this.pad = pad;
    }

    public GridControl(GridButton button) {
        this.button = button;
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
