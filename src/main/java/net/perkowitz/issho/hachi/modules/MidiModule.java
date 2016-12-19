package net.perkowitz.issho.hachi.modules;

import javax.sound.midi.*;

import static javax.sound.midi.ShortMessage.*;
import static javax.sound.midi.ShortMessage.CONTROL_CHANGE;
import static javax.sound.midi.ShortMessage.NOTE_OFF;

/**
 * Created by optic on 10/24/16.
 */
public class MidiModule extends BasicModule implements Receiver {

    public static int MIDI_ALL_NOTES_OFF_CC = 123;
    private static int MIDI_REALTIME_COMMAND = 0xF0;

    protected Transmitter inputTransmitter;
    protected Receiver outputReceiver;
    protected boolean isMuted;


    public MidiModule(Transmitter inputTransmitter, Receiver outputReceiver) {
        // connect the provided midi input to the sequencer's clock receiver
        this.inputTransmitter = inputTransmitter;
        this.inputTransmitter.setReceiver(this);

        // where to send the sequencer's midi output
        this.outputReceiver = outputReceiver;
    }



    /************************************************************************
     * midi output implementation
     *
     */

    public void close() {

    }

    public void mute(boolean muted) {
        this.isMuted = muted;
    }

    protected void sendAllNotesOff(int channel) {
        sendMidiCC(channel, MidiModule.MIDI_ALL_NOTES_OFF_CC, 0);
    }

    protected void sendMidiNote(int channel, int noteNumber, int velocity) {
//        System.out.printf("Note: ch=%d, note=%d, vel=%d\n", channel, noteNumber, velocity);

        if (isMuted && velocity > 0) return;

        try {
            ShortMessage noteMessage = new ShortMessage();
            noteMessage.setMessage(ShortMessage.NOTE_ON, channel, noteNumber, velocity);
            outputReceiver.send(noteMessage, -1);

        } catch (InvalidMidiDataException e) {
            System.err.println(e);
        }
    }

    protected void sendMidiCC(int channel, int ccNumber, int value) {
//        System.out.printf("Note: %d, %d, %d\n", channel, noteNumber, velocity);

        if (isMuted && value > 0) return;

        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(ShortMessage.CONTROL_CHANGE, channel, ccNumber, value);
            outputReceiver.send(message, -1);

        } catch (InvalidMidiDataException e) {
            System.err.println(e);
        }
    }

    public void send(MidiMessage message, long timeStamp) {
//        System.out.printf("MSG (%d, %d): ", message.getLength(), message.getStatus());
//        for (byte b : message.getMessage()) {
//            System.out.printf("%d ", b);
//        }
//        System.out.printf("\n");

        if (isMuted) return;

        if (message instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage) message;
            int command = shortMessage.getCommand();
            int status = shortMessage.getStatus();

            if (command == MIDI_REALTIME_COMMAND) {
                switch (status) {
                    case START:
//                        System.out.println("START");
                        break;
                    case STOP:
//                        System.out.println("STOP");
                        break;
                    case TIMING_CLOCK:
//                        System.out.println("TICK");
                        break;
                    default:
//                        System.out.printf("REALTIME: %d\n", status);
                        break;
                }


            } else {
                switch (command) {
                    case NOTE_ON:
                        break;
                    case NOTE_OFF:
//                        System.out.println("NOTE OFF");
                        break;
                    case CONTROL_CHANGE:
//                        System.out.println("MIDI CC");
                        break;
                    default:
//                        System.out.printf("MSG: %d\n", command);
                }
            }
        }

    }

}
