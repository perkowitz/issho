package net.perkowitz.issho.controller;

public interface ControllerListener {

    public void onPadPressed(Pad pad, int velocity);
    public void onPadReleased(Pad pad);
    public void onButtonPressed(Button button, int velocity);
    public void onButtonReleased(Button button);
    public void onKnobChanged(Knob knob, int delta);
    public void onKnobSet(Knob knob, int value);
    public void onKnobTouched(Knob knob);
    public void onKnobReleased(Knob knob);

}
