/**
 * Translator to implement the BigDraw app on a LaunchpadPro.
 * This translator simply throws away any commands for pads/buttons outside of the
 * launchpad's range.
 */
package net.perkowitz.issho.controller.apps.bigdraw;

import lombok.Setter;
import net.perkowitz.issho.controller.ControllerListener;
import net.perkowitz.issho.controller.Translator;
import net.perkowitz.issho.controller.elements.*;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.novation.LaunchpadPro;

import java.awt.*;

public class TruncationTranslator implements Translator {

    private LaunchpadPro launchpad;
    @Setter private ControllerListener listener;

    public TruncationTranslator(LaunchpadPro launchpad, ControllerListener listener) {
        this.launchpad = launchpad;
        this.listener = listener;
    }


    /***** Controller implementation *****/

    public void initialize() {
        launchpad.initialize();
    }

    public void setPad(Pad pad, Color color) {
        if (pad.getGroup() == BigDraw.CANVAS_PADS_GROUP &&
                pad.getRow() >= 0 && pad.getRow() < 8 && pad.getColumn() >= 0 && pad.getColumn() < 8) {
            launchpad.setPad(Pad.to(0, pad), color);
        }
    }

    public void setButton(Button button, Color color) {
        if (button.getGroup() == BigDraw.PALETTE_BUTTONS_GROUP && button.getIndex() >= 0 && button.getIndex() < 8) {
            launchpad.setButton(Button.to(LaunchpadPro.BUTTONS_BOTTOM, button), color);
        }
    }

    public void setKnob(Knob knob, Color color) {}

    public void setLight(Light light, Color color) {}


    /***** ControllerListener implementation *****/

    public void onElementPressed(Element element, int value) {
        listener.onElementPressed(element, value);
    }

    public void onElementChanged(Element element, int delta) {
        listener.onElementChanged(element, delta);
    }

    public void onElementReleased(Element element) {
        listener.onElementReleased(element);
    }

}
