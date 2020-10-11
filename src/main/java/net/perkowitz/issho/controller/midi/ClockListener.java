package net.perkowitz.issho.controller.midi;

public interface ClockListener {

    public void onNoteOn(int channel, int noteNumber, int velocity);
    public void onNoteOff(int channel, int noteNumber, int velocity);
    public void onCc(int channel, int ccNumber, int value);
    public void onStart(boolean restart);
    public void onStop();
    public void onTick();

}
