package net.perkowitz.issho.hachi;

import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridColor;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.hachi.modules.Module;

/**
 * Created by optic on 9/19/16.
 */
public class SwitchableDisplay implements GridDisplay {

    private GridDisplay display;
    @Getter @Setter private boolean enabled = false;

    public SwitchableDisplay(GridDisplay display) {
        this.display = display;
    }


    /***** GridDisplay implementation ***************************/

    public void initialize() {
        if (enabled) {
            display.initialize();
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
