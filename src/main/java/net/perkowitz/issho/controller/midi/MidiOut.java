package net.perkowitz.issho.controller.midi;


import net.perkowitz.issho.controller.Log;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class MidiOut {

    private static final int LOG_LEVEL = Log.OFF;

    //    public static int MIDI_ALL_NOTES_OFF_CC = 123;
    public static int MIDI_ALL_NOTES_OFF_CC = 120;
    public static int MIDI_RESET_ALL_CONTROLLERS = 121;

    private Receiver receiver;

    public MidiOut (Receiver receiver) {
        this.receiver = receiver;
    }

    public void close() {
        receiver.close();
    }

    public void note(int channel, int noteNumber, int velocity) {
        Log.log(this, LOG_LEVEL, "(%d) %d %d", channel, noteNumber, velocity);
        if (receiver == null) return;

        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(ShortMessage.NOTE_ON, channel, noteNumber, velocity);
            receiver.send(message, -1);

        } catch (InvalidMidiDataException e) {
            System.err.println(e);
        }

    }

    public void cc(int channel, int ccNumber, int value) {
        Log.log(this, LOG_LEVEL, "(%d) %d %d", channel, ccNumber, value);
        if (receiver == null) return;

        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(ShortMessage.CONTROL_CHANGE, channel, ccNumber, value);
            receiver.send(message, -1);

        } catch (InvalidMidiDataException e) {
            System.err.println(e);
        }

    }

    public void sysex(byte[] bytes) {
        
    }

    public void allNotesOff(int channel) {
        if (receiver == null) return;
        cc(channel, MIDI_ALL_NOTES_OFF_CC, 0);
        cc(channel, MIDI_RESET_ALL_CONTROLLERS, 0);
    }



}
