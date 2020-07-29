package net.perkowitz.issho.controller;

import javax.sound.midi.MidiMessage;
import java.awt.*;

/**
 * Created by mikep on 7/28/20.
 */
public class Test {

    public static void main(String args[]) throws Exception {

        TestController t = new TestController(new TestListener());

        for (int group = 0; group < 8; group++) {
            for (int index = 0; index < 4; index++) {
                t.setButton(ButtonElement.at(group, index), new Color(index, index, index));
//                t.setPad(PadElement.at(group, index), new Color(index, index, index));
//                t.setLight(LightElement.at(group, index), new Color(index, index, index));
//                t.setKnob(KnobElement.at(group, index), new Color(index, index, index));
            }
        }

    }

    private static class TestController implements Controller {

        private ControllerListener listener;

        public TestController (ControllerListener listener) {
            this.listener = listener;
        }

        public void initialize() {}

        public void setPad(PadElement pad, Color color) {
            System.out.printf("Set pad %s to color %s.\n", pad, color);
        }

        public void setButton(ButtonElement button, Color color) {
//            System.out.printf("Set button %s to color %s.\n", button, color);

            // the buttons activate test actions on other elements
            switch (button.getGroup()) {
                case 0:
                    listener.onButtonPressed(ButtonElement.at(0, button.getIndex()), 64);
                    break;
                case 1:
                    listener.onButtonReleased(ButtonElement.at(0, button.getIndex()));
                    break;
                case 2:
                    listener.onPadPressed(PadElement.at(0, button.getIndex()), 64);
                    break;
                case 3:
                    listener.onPadReleased(PadElement.at(0, button.getIndex()));
                    break;
                case 4:
                    listener.onKnobTouched(KnobElement.at(0, button.getIndex()));
                    break;
                case 5:
                    listener.onKnobReleased(KnobElement.at(0, button.getIndex()));
                    break;
                case 6:
                    listener.onKnobSet(KnobElement.at(0, button.getIndex()), 64);
                    break;
                case 7:
                    listener.onKnobChanged(KnobElement.at(0, button.getIndex()), 10);
                    break;
            }

        }

        public void setKnob(KnobElement knob, Color color) {
            System.out.printf("Set knob %s to color %s.\n", knob, color);
        }

        public void setLight(LightElement light, Color color) {
            System.out.printf("Set light %s to color %s.\n", light, color);
        }

        public void send(MidiMessage message, long timeStamp) {
            System.out.printf("Controller send midi message %s.\n", message);
        }

        public void close() {
            System.out.printf("Controller close.");
        }
    }

    private static class TestListener implements ControllerListener {
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
}
