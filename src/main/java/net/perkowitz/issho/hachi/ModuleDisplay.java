package net.perkowitz.issho.hachi;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridColor;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridPad;

import java.util.Set;

import static net.perkowitz.issho.devices.GridButton.Side.*;

/**
 * Created by optic on 9/19/16.
 */
public class ModuleDisplay implements GridDisplay {

    private static final Set<GridButton.Side> validSides = Sets.newHashSet(Left, Right, Bottom);

    private GridDisplay display;
    @Getter @Setter private boolean enabled = false;

    public ModuleDisplay(GridDisplay display) {
        this.display = display;
    }


    /***** GridDisplay implementation ***************************/

    public void initialize() {
        initialize(true, validSides);
    }

    public void initialize(boolean pads, Set<GridButton.Side> buttonSides) {
        if (enabled) {
            // modules don't control the top row of buttons, so only initialize the rest
            if (buttonSides != null) {
                buttonSides.retainAll(validSides);
            }
            display.initialize(pads, buttonSides);
        }
    }

    public void setPad(GridPad pad, GridColor color) {
        if (enabled) {
            display.setPad(pad, color);
        }
    }

    public void setButton(GridButton button, GridColor color){
        if (enabled) {
            display.setButton(button, color);
        }
    }


}
