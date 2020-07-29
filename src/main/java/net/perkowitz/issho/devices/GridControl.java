package net.perkowitz.issho.devices;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by optic on 10/27/16.
 */
public class GridControl {

    @Getter private Integer index = null;
    @Getter private GridPad pad = null;
    @Getter private GridButton button = null;
    @Getter private GridKnob knob = null;

    // Each control remembers when it was pressed so it can report hold time on release.
    // However, since grid controls are created and destroyed all the time, maintain a static map
    // keyed by the control's nice name.
    static private Map<String,Long> pressMap = Maps.newHashMap();


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

    public void press() {
        pressMap.put(this.toString(), System.currentTimeMillis());
    }

    public long release() {
        long e = elapsed();
        pressMap.remove(this.toString());
        return e;
    }

    public long elapsed() {
        Long pressTime = pressMap.get(this.toString());
        if (pressTime == null) {
            return System.currentTimeMillis();
        }
        return System.currentTimeMillis() - pressTime;
    }


    /***** overrides ****************************************/

    @Override
    public String toString() {
        String s = "Element:";
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
