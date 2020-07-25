package net.perkowitz.issho.controller;

public class ControllerListenerTest implements ControllerListener {

    public void onPadPressed(Pad pad, int velocity) {
        System.out.printf("Pad pressed: %s, %d\n", pad, velocity);
    }
    public void onPadReleased(Pad pad) {
        System.out.printf("Pad released: %s\n", pad);
    }

    public void onButtonPressed(Button button, int velocity) {
        System.out.printf("Button pressed: %s, %d\n", button, velocity);
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
