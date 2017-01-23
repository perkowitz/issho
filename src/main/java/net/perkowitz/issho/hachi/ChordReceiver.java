package net.perkowitz.issho.hachi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import java.util.List;

import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;

/**
 * Created by optic on 1/14/17.
 */
public class ChordReceiver implements Receiver {

    private static int MIDI_REALTIME_COMMAND = 0xF0;

    private List<Chordable> chordables;
    private Chord chord;



    public ChordReceiver(List<Chordable> chordables) {
        this.chordables = chordables;
        this.chord = new Chord();
    }


    private void sendChord() {
        for (Chordable chordable : chordables) {
            chordable.setChord(chord);
        }
    }


    /***** midi receiver implementation **************************************************************/

    public void send(MidiMessage message, long timeStamp) {

        if (message instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage) message;
            int command = shortMessage.getCommand();

            if (command != MIDI_REALTIME_COMMAND) {
                switch (command) {
                    case NOTE_ON:
                        int note = shortMessage.getData1();
//                        System.out.printf("ChordReceiver NOTE ON: %d, %d, %d\n", shortMessage.getChannel(), note, shortMessage.getData2());
                        if (shortMessage.getData2() != 0) {
                            chord.add(note);
                            System.out.println(chord);
                            sendChord();
                        }
                        break;
                    case NOTE_OFF:
//                        System.out.printf("ChordReceiver NOTE OFF: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        note = shortMessage.getData1();
                        chord.remove(note);
                        System.out.println(chord);
                        sendChord();
                        break;
                    default:
                }
            }
        }
    }

    public void close() {

    }



}
