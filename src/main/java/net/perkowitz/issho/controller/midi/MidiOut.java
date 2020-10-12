package net.perkowitz.issho.controller.midi;

import lombok.extern.java.Log;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.logging.Level;

@Log
public class MidiOut {

    static { log.setLevel(Level.OFF); }

    private Receiver receiver;

    public MidiOut (Receiver receiver) {
        this.receiver = receiver;
    }

    public void close() {
        receiver.close();
    }

    public void note(int channel, int noteNumber, int velocity) {

        log.info(String.format("note: ch=%s, n=%d, v=%d", channel, noteNumber, velocity));
        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(ShortMessage.NOTE_ON, channel, noteNumber, velocity);
            receiver.send(message, -1);

        } catch (InvalidMidiDataException e) {
            System.err.println(e);
        }

    }

    public void cc(int channel, int ccNumber, int value) {

        log.info(String.format("cc: ch=%s, n=%d, v=%d", channel, ccNumber, value));
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

}
