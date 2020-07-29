package net.perkowitz.issho.controller;

/**
 * Created by mikep on 7/28/20.
 */
public class TestListener implements ControllerListener {
    public void onPadPressed(PadElement pad, int velocity) {
        System.out.printf("PadElement pressed: %s, %d\n", pad, velocity);
    }

    public void onPadReleased(PadElement pad) {
        System.out.printf("PadElement released: %s\n", pad);
    }

    public void onButtonPressed(ButtonElement button, int velocity) {
        System.out.printf("ButtonElement pressed: %s, %d\n", button, velocity);
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
