package net.perkowitz.issho.hachi;

import com.google.common.collect.Sets;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridListener;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.devices.launchpadpro.LaunchpadPro;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import static javax.sound.midi.ShortMessage.CONTROL_CHANGE;
import static javax.sound.midi.ShortMessage.NOTE_ON;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by optic on 12/26/16.
 */
public class ChordTest {

    int[] defaultMapping = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };


    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testEmpty() throws Exception {
        Chord chord = new Chord();
        assertEquals(true, chord.isEmpty());

        chord.add(0);
        assertEquals(false, chord.isEmpty());

        chord.clear();
        assertEquals(true, chord.isEmpty());
    }

    @Test
    public void testAddRemove() throws Exception {
        Chord chord = new Chord();
        assertEquals(true, chord.isEmpty());
        assertEquals(0, chord.size());

        chord.add(0);
        assertEquals(false, chord.isEmpty());
        assertEquals(1, chord.size());

        chord.remove(0);
        assertEquals(true, chord.isEmpty());
        assertEquals(0, chord.size());

        chord.add(-1);
        chord.add(-1000);
        chord.add(null);
        assertEquals(true, chord.isEmpty());
    }

    @Test
    public void testMod() throws Exception {
        Chord chord = new Chord();
        assertEquals(0, chord.size());

        chord.add(0);
        assertEquals(1, chord.size());
        chord.add(12);
        assertEquals(1, chord.size());
        chord.add(24);
        assertEquals(1, chord.size());

        chord.add(3);
        assertEquals(2, chord.size());
        chord.add(123);
        assertEquals(2, chord.size());
    }

    @Test
    public void testDefaultMapping() throws Exception {
        Chord chord = new Chord();
        verifyMapping(chord, defaultMapping);

        chord.add(0);
        chord.clear();
        verifyMapping(chord, defaultMapping);
    }

    @Test
    public void testNoMapping() throws Exception {
        Chord chord = new Chord();
        chord.setNoteMapMode(Chord.NoteMapMode.NO_MAP);
        verifyMapping(chord, defaultMapping);

        chord.add(0);
        verifyMapping(chord, defaultMapping);
    }

    @Test
    public void testFloorMapping() throws Exception {
        Chord chord = new Chord();
        chord.setNoteMapMode(Chord.NoteMapMode.FLOOR);
        verifyMapping(chord, defaultMapping);

        chord.add(0);
        verifyMapping(chord, new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });

        chord.add(4);
        chord.add(7);
        verifyMapping(chord, new int[] { 0, 0, 0, 0, 4, 4, 4, 7, 7, 7, 7, 7 });
    }

    @Test
    public void testNearestMapping() throws Exception {
        Chord chord = new Chord();
        chord.setNoteMapMode(Chord.NoteMapMode.NEAREST);
        verifyMapping(chord, defaultMapping);

        chord.add(0);
        verifyMapping(chord, new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });

        chord.add(4);
        chord.add(7);
        verifyMapping(chord, new int[] { 0, 0, 4, 4, 4, 4, 7, 7, 7, 7, 0, 0 });
    }

    @Test
    public void testRoundRobinMapping() throws Exception {
        Chord chord = new Chord();
        chord.setNoteMapMode(Chord.NoteMapMode.ROUND_ROBIN);
        verifyMapping(chord, defaultMapping);

        chord.add(0);
        verifyMapping(chord, new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });

        chord.add(4);
        chord.add(7);
        verifyMapping(chord, new int[] { 0, 4, 7, 0, 4, 7, 0, 4, 7, 0, 4, 7 });
    }





    /***** helper methods *****************************************/

    private void verifyMapping(Chord chord, int[] mapping) {
        for (int n = 0; n < 12; n++) {
            assertEquals(mapping[n], chord.mapNote(n));
        }
    }


}