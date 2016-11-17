package net.perkowitz.issho.hachi;

import javax.sound.midi.*;

import static javax.sound.midi.ShortMessage.*;
import static javax.sound.midi.ShortMessage.CONTROL_CHANGE;
import static javax.sound.midi.ShortMessage.NOTE_OFF;

/**
 * Created by optic on 11/16/16.
 */
public class Knobby implements Receiver {

    private static int MIDI_REALTIME_COMMAND = 0xF0;

    private Transmitter inputTransmitter;
    private Receiver outputReceiver;


    public Knobby(Transmitter inputTransmitter, Receiver outputReceiver) {
        this.inputTransmitter = inputTransmitter;
        this.inputTransmitter.setReceiver(this);
        this.outputReceiver = outputReceiver;
    }


    protected void sendMidiNote(int channel, int noteNumber, int velocity) {
//        System.out.printf("Note: %d, %d, %d\n", channel, noteNumber, velocity);

        try {
            ShortMessage noteMessage = new ShortMessage();
            noteMessage.setMessage(ShortMessage.NOTE_ON, channel, noteNumber, velocity);
            outputReceiver.send(noteMessage, -1);

        } catch (InvalidMidiDataException e) {
            System.err.println(e);
        }
    }


    /***** midi receiver implementation **************************************************************/

    public void send(MidiMessage message, long timeStamp) {

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
//                        System.out.printf("NOTE ON: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        break;
                    case NOTE_OFF:
//                        System.out.printf("NOTE OFF: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        break;
                    case CONTROL_CHANGE:
//                        System.out.printf("MIDI CC: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        outputReceiver.send(message, timeStamp);
                        break;
                    default:
                }
            }
        }
    }

    public void close() {

    }


}
