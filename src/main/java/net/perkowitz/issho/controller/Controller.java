package net.perkowitz.issho.controller;

import net.perkowitz.issho.controller.elements.*;

import java.awt.*;

public interface Controller {

    public void initialize();

    public void setPad(Pad pad, Color color);
    public void setButton(net.perkowitz.issho.controller.elements.Button button, Color color);
    public void setKnob(Knob knob, Color color);
    public void setLight(Light light, Color color);
}
