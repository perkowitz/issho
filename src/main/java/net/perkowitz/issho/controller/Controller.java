package net.perkowitz.issho.controller;

import javax.sound.midi.Receiver;
import java.awt.*;

public interface Controller extends Receiver {

    public void setPad(Pad pad, Color color);
    public void setButton(Button button, Color color);
    public void setKnob(Knob knob, Color color);
    public void setLight(Light light, Color color);

}
