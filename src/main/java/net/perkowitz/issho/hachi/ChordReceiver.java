package net.perkowitz.issho.hachi;

import com.google.common.collect.Sets;
import lombok.Setter;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import java.util.List;
import java.util.Set;

import static javax.sound.midi.ShortMessage.CONTROL_CHANGE;
import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;
import static net.perkowitz.issho.hachi.ChordReceiver.SustainToggleMode.HIGH_TOGGLE;
import static net.perkowitz.issho.hachi.ChordReceiver.SustainToggleMode.NORMAL;

/**
 * Created by optic on 1/14/17.
 */
public class ChordReceiver implements Receiver {

    public enum SustainToggleMode {
        NORMAL, HIGH_TOGGLE
    }

    private static int MIDI_REALTIME_COMMAND = 0xF0;

    private List<Chordable> chordables;
    private Chord chord;
    private Set<Integer> currentlyHeldNotes = Sets.newHashSet();
    private boolean chordHold = true;
    private int sustainControllerNumber = 64;



    public ChordReceiver(List<Chordable> chordables) {
        this.chordables = chordables;
        this.chord = new Chord();
    }


    private void sendChord() {
//        System.out.printf("Chord: %s\n", chord);
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
                        if (shortMessage.getData2() == 0) {
                            //chord.remove(note);  // we don't remove until all notes are lifted
                            currentlyHeldNotes.remove(note);
                        } else {
                            // as long as you hold down notes, you can add more to the chord; when you release all, next play will start a new chord
                            if (currentlyHeldNotes.isEmpty()) {
                                chord.clear();
                            }
                            chord.add(note);
                            currentlyHeldNotes.add(note);
                        }
                        sendChord();
                        break;
                    case NOTE_OFF:
//                        System.out.printf("ChordReceiver NOTE OFF: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        note = shortMessage.getData1();
                        //chord.remove(note);  // we don't remove until all notes are lifted
                        currentlyHeldNotes.remove(note);
                        sendChord();
                        break;
                    case CONTROL_CHANGE:
//                        System.out.printf("ChordReceiver MIDI CC: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        int controllerNumber = shortMessage.getData1();
                        if (controllerNumber == sustainControllerNumber) {
                            chord.clear();
                        }
                        break;
                    default:
                }
            }
        }
    }

    public void close() {

    }



}
