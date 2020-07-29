package net.perkowitz.issho.controller;

public interface ControllerListener {

    public void onPadPressed(PadElement pad, int velocity);
    public void onPadReleased(PadElement pad);
    public void onButtonPressed(ButtonElement button, int velocity);
    public void onButtonReleased(ButtonElement button);
    public void onKnobChanged(KnobElement knob, int delta);
    public void onKnobSet(KnobElement knob, int value);
    public void onKnobTouched(KnobElement knob);
    public void onKnobReleased(KnobElement knob);

}
