package net.perkowitz.issho.controller.novation;

import lombok.Setter;
import net.perkowitz.issho.controller.ButtonElement;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.ControllerListener;
import net.perkowitz.issho.controller.KnobElement;
import net.perkowitz.issho.controller.PadElement;

import java.awt.*;

/**
 * Created by mikep on 7/28/20.
 */
public class LaunchPadProTestListener implements ControllerListener {

    private Color color = Colors.BLACK;
    @Setter private Controller controller;


    public void onPadPressed(PadElement pad, int velocity) {
        controller.setPad(pad, color);
    }

    public void onPadReleased(PadElement pad) {
        System.out.printf("PadElement released: %s\n", pad);
    }

    public void onButtonPressed(ButtonElement button, int velocity) {
        if (button.getGroup() == LaunchpadPro.BUTTONS_BOTTOM && button.getIndex() == 0) {
            color = Colors.BLACK;
        } else if (button.getGroup() == LaunchpadPro.BUTTONS_BOTTOM) {
            color = Colors.rainbow[button.getIndex() - 1];
        } else if (button.getGroup() == LaunchpadPro.BUTTONS_LEFT) {
            controller.initialize();
            System.exit(0);
        } else if (button.getGroup() == LaunchpadPro.BUTTONS_RIGHT) {
            for (int i = 0; i < 8; i++) {
                controller.setPad(PadElement.at(button.getIndex(), i), color);
            }
        } else if (button.getGroup() == LaunchpadPro.BUTTONS_TOP) {
            for (int i = 0; i < 8; i++) {
                controller.setPad(PadElement.at(i, button.getIndex()), color);
            }
        }
    }

    public void onButtonReleased(ButtonElement button) {
        System.out.printf("ButtonElement released: %s\n", button);
    }

    public void onKnobChanged(KnobElement knob, int delta) {
        System.out.printf("KnobElement changed: %s, %d\n", knob, delta);
    }

    public void onKnobSet(KnobElement knob, int value) {
        System.out.printf("KnobElement set: %s, %d\n", knob, value);
    }

    public void onKnobTouched(KnobElement knob) {
        System.out.printf("KnobElement touched: %s\n", knob);
    }

    public void onKnobReleased(KnobElement knob) {
        System.out.printf("KnobElement released: %s\n", knob);
    }

}
