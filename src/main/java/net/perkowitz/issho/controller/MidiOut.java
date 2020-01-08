package net.perkowitz.issho.controller;

import javax.sound.midi.Receiver;

public class MidiOut {

    private Receiver receiver;

    public MidiOut (Receiver receiver) {
        this.receiver = receiver;
    }

    public void note(int channel, int note, int velocity) {

    }

    public void cc(int channel, int cc, int value) {

    }

    public void sysex(byte[] bytes) {
        
    }

}
