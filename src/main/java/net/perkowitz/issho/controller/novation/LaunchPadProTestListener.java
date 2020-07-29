package net.perkowitz.issho.controller.novation;

import lombok.Setter;
import net.perkowitz.issho.controller.elements.*;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.ControllerListener;

import java.awt.*;

/**
 * Created by mikep on 7/28/20.
 */
public class LaunchPadProTestListener implements ControllerListener {

    private Color color = Colors.BLACK;
    @Setter private Controller controller;


    public void onPadPressed(Pad pad, int velocity) {
        controller.setPad(pad, color);
    }

    public void onPadReleased(Pad pad) {
        System.out.printf("Pad released: %s\n", pad);
    }

    public void onButtonPressed(net.perkowitz.issho.controller.elements.Button button, int velocity) {
        if (button.getGroup() == LaunchpadPro.BUTTONS_BOTTOM && button.getIndex() == 0) {
            color = Colors.BLACK;
        } else if (button.getGroup() == LaunchpadPro.BUTTONS_BOTTOM) {
            color = Colors.rainbow[button.getIndex() - 1];
        } else if (button.getGroup() == LaunchpadPro.BUTTONS_LEFT) {
            controller.initialize();
            System.exit(0);
        } else if (button.getGroup() == LaunchpadPro.BUTTONS_RIGHT) {
            for (int i = 0; i < 8; i++) {
                controller.setPad(Pad.at(button.getIndex(), i), color);
            }
        } else if (button.getGroup() == LaunchpadPro.BUTTONS_TOP) {
            for (int i = 0; i < 8; i++) {
                controller.setPad(Pad.at(i, button.getIndex()), color);
            }
        }
    }

    public void onButtonReleased(Button button) {
        System.out.printf("Button released: %s\n", button);
    }

    public void onKnobChanged(Knob knob, int delta) {
        System.out.printf("Knob changed: %s, %d\n", knob, delta);
    }

    public void onKnobSet(Knob knob, int value) {
        System.out.printf("Knob set: %s, %d\n", knob, value);
    }

    public void onKnobTouched(Knob knob) {
        System.out.printf("Knob touched: %s\n", knob);
    }

    public void onKnobReleased(Knob knob) {
        System.out.printf("Knob released: %s\n", knob);
    }

}
