package net.perkowitz.issho.controller.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class MidiOut {

    private Receiver receiver;

    public MidiOut (Receiver receiver) {
        this.receiver = receiver;
    }

    public void close() {
        receiver.close();
    }

    public void note(int channel, int noteNumber, int velocity) {

//        System.out.printf("-- note: n=%d, v=%d\n", noteNumber, velocity);
        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(ShortMessage.NOTE_ON, channel, noteNumber, velocity);
            receiver.send(message, -1);

        } catch (InvalidMidiDataException e) {
            System.err.println(e);
        }

    }

    public void cc(int channel, int ccNumber, int value) {

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
