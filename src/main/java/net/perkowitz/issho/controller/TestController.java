package net.perkowitz.issho.controller;

import javax.sound.midi.MidiMessage;
import java.awt.*;

/**
 * Created by mikep on 7/28/20.
 */
public class TestController {

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
