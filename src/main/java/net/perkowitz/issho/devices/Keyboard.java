package net.perkowitz.issho.devices;

import net.perkowitz.issho.util.MidiUtil;
import javax.sound.midi.*;
import java.util.List;

import static javax.sound.midi.ShortMessage.*;
import static javax.sound.midi.ShortMessage.CONTROL_CHANGE;
import static javax.sound.midi.ShortMessage.NOTE_OFF;

/**
 * Created by optic on 1/14/17.
 */
public class Keyboard implements Receiver {

    private static int MIDI_REALTIME_COMMAND = 0xF0;

    private Transmitter inputTransmitter;
    private Receiver outputReceiver;

    public Keyboard(Transmitter inputTransmitter, Receiver outputReceiver) {
        this.inputTransmitter = inputTransmitter;
        this.inputTransmitter.setReceiver(this);
        this.outputReceiver = outputReceiver;
    }



    /***** midi receiver implementation **************************************************************/

    public void send(MidiMessage message, long timeStamp) {

        if (message instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage) message;
            int command = shortMessage.getCommand();
            int status = shortMessage.getStatus();

            if (command != MIDI_REALTIME_COMMAND) {
                switch (command) {
                    case NOTE_ON:
//                        System.out.printf("Keyboard NOTE ON: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        outputReceiver.send(message, timeStamp);
                        break;
                    case NOTE_OFF:
//                        System.out.printf("Keyboard NOTE OFF: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        outputReceiver.send(message, timeStamp);
                        break;
                    default:
                }
            }
        }
    }

    public void close() {

    }


    /***** static methods **********************************************/

    public static Keyboard fromMidiDevice(List<String> names, Receiver targetReceiver) {

        MidiDevice midiInput = MidiUtil.findMidiDevice(names.toArray(new String[0]), false, true);
        if (midiInput == null) {
            System.out.printf("Unable to find keyboard device matching name: %s\n", names);
            return null;
        }

        try {
            midiInput.open();
            Keyboard keyboard = new Keyboard(midiInput.getTransmitter(), targetReceiver);
            return keyboard;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }


}
