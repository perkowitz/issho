package net.perkowitz.issho.hachi.modules;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.perkowitz.issho.hachi.Chord;
import net.perkowitz.issho.hachi.Chordable;

import javax.sound.midi.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by optic on 10/24/16.
 */
public class ChordModule extends MidiModule implements Chordable {

    protected Chord chord = null;

    // remember what notes we actually played for input note numbers, so we can stop them later
    // maps to a list rather than a set because some synths (e.g. Sub 37) track multiple ons/offs for same note
    private Map<Integer, List<Integer>> playedNotesMap = Maps.newHashMap();


    public ChordModule(Transmitter inputTransmitter, Receiver outputReceiver) {
        super(inputTransmitter, outputReceiver);
    }


    /***** chordable implementation ********************************/

    public void setChord(Chord chord) {
        this.chord = chord;
    }


    /************************************************************************
     * midi output implementation
     *
     */

    @Override
    protected void sendMidiNote(int channel, int noteNumber, int velocity) {

        if (isMuted && velocity > 0) return;
//        System.out.printf("ChordModule Note: ch=%d, note=%d, vel=%d\n", channel, noteNumber, velocity);

        if (velocity == 0) {
            // note off -- send note off for any notes we mapped this note number to
            Collection<Integer> mappedNotes = playedNotesMap.get(noteNumber);
            if (mappedNotes == null || mappedNotes.isEmpty()) {
                mappedNotes = Sets.newHashSet(noteNumber);
            }
            for (Integer mappedNote : mappedNotes) {
//                System.out.printf("- mapped note off for %d to %d\n", noteNumber, mappedNote);
                send(channel, mappedNote, velocity);
            }
            playedNotesMap.put(noteNumber, Lists.<Integer>newArrayList());

        } else {
            // note on -- once we compute any mapping, add those mappings to the playedNotesMap
            int mappedNote = noteNumber;
            if (chord != null && !chord.isEmpty()) {
                mappedNote = chord.mapNote(noteNumber);
//                System.out.printf("- mapped %d to %d\n", noteNumber, mappedNote);
            }

            if (playedNotesMap.get(noteNumber) == null) {
                playedNotesMap.put(noteNumber, Lists.<Integer>newArrayList(mappedNote));
            } else {
                playedNotesMap.get(noteNumber).add(mappedNote);
            }
            send(channel, mappedNote, velocity);

        }


    }

    protected void send(int channel, int noteNumber, int velocity) {
        try {
            ShortMessage noteMessage = new ShortMessage();
            noteMessage.setMessage(ShortMessage.NOTE_ON, channel, noteNumber, velocity);
            outputReceiver.send(noteMessage, -1);
        } catch (InvalidMidiDataException e) {
            System.err.println(e);
        }
    }

}
