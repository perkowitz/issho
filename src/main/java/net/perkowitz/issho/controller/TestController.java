package net.perkowitz.issho.controller;

import net.perkowitz.issho.controller.elements.*;
import net.perkowitz.issho.controller.elements.Button;

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

    public void setPad(Pad pad, Color color) {
        System.out.printf("Set pad %s to color %s.\n", pad, color);
    }

    public void setButton(Button button, Color color) {
//            System.out.printf("Set button %s to color %s.\n", button, color);

        // the buttons activate test actions on other elements
        switch (button.getGroup()) {
            case 0:
                listener.onElementPressed(net.perkowitz.issho.controller.elements.Button.at(0, button.getIndex()), 64);
                break;
            case 1:
                listener.onElementReleased(Button.at(0, button.getIndex()));
                break;
            case 2:
                listener.onElementPressed(Pad.at(0, 0, button.getIndex()), 64);
                break;
            case 3:
                listener.onElementReleased(Pad.at(0, 0, button.getIndex()));
                break;
            case 4:
                listener.onElementPressed(Knob.at(0, button.getIndex()), 64);
                break;
            case 5:
                listener.onElementReleased(Knob.at(0, button.getIndex()));
                break;
            case 6:
                listener.onElementChanged(Knob.at(0, button.getIndex()), 10);
                break;
        }

    }

    public void setKnob(Knob knob, Color color) {
        System.out.printf("Set knob %s to color %s.\n", knob, color);
    }

    public void setLight(Light light, Color color) {
        System.out.printf("Set light %s to color %s.\n", light, color);
    }

    public void send(MidiMessage message, long timeStamp) {
        System.out.printf("Controller send midi message %s.\n", message);
    }

    public void close() {
        System.out.printf("Controller close.");
    }

}
