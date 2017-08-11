package net.perkowitz.issho.devices;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

import static net.perkowitz.issho.devices.GridButton.Side.*;

/**
 * Created by optic on 9/19/16.
 */
public class MultiDisplay implements GridDisplay {

    private static final Set<GridButton.Side> validSides = Sets.newHashSet(Left, Right, Bottom);

    private Set<GridDisplay> displays;
    @Getter @Setter private boolean enabled = false;

    public MultiDisplay(GridDisplay[] displays) {
        this.displays = Sets.newHashSet();
        for (GridDisplay display : displays) {
            this.displays.add(display);
        }
        enabled = true;
    }

    public MultiDisplay(Set<GridDisplay> displays) {
        this.displays = displays;
        enabled = true;
    }


    public void add(GridDisplay display) {
        displays.add(display);
    }

    public void remove(GridDisplay display) {
        displays.remove(display);
    }

    public void clear() {
        displays.clear();
    }


    /***** GridDisplay implementation ***************************/

    public void initialize() {
        initialize(true, validSides);
    }

    public void initialize(boolean pads, Set<GridButton.Side> buttonSides) {
        if (enabled) {
            for (GridDisplay display : displays) {
                // modules don't control the top row of buttons, so only initialize the rest
                if (buttonSides != null) {
                    buttonSides.retainAll(validSides);
                }
                display.initialize(pads, buttonSides);
            }
        }
    }

    public void setPad(GridPad pad, GridColor color) {
        if (enabled) {
            for (GridDisplay display : displays) {
                display.setPad(pad, color);
            }
        }
    }

    public void setButton(GridButton button, GridColor color){
        if (enabled) {
            for (GridDisplay display : displays) {
                display.setButton(button, color);
            }
        }
    }

    public void setKnob(GridKnob knob, int value) {}


}
