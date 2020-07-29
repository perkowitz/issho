package net.perkowitz.issho.controller;

import java.awt.*;

public interface Controller {

    public void initialize();

    public void setPad(PadElement pad, Color color);
    public void setButton(ButtonElement button, Color color);
    public void setKnob(KnobElement knob, Color color);
    public void setLight(LightElement light, Color color);
}
