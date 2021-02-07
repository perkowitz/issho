package net.perkowitz.issho.controller;

import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.Knob;
import net.perkowitz.issho.controller.elements.Light;
import net.perkowitz.issho.controller.elements.Pad;

import java.awt.*;

public interface Controller {

    public void initialize();
    public String toString();
    public void setListener(ControllerListener listener);

    public void setPad(Pad pad, Color color);
    public void setButton(Button button, Color color);
    public void setKnob(Knob knob, Color color);
    public void setKnobValue(Knob knob, int value);
    public void setLight(Light light, Color color);
}
