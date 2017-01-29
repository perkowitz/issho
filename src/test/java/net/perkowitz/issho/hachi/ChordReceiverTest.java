package net.perkowitz.issho.hachi;

import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.sound.midi.ShortMessage;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by optic on 12/26/16.
 */
public class ChordReceiverTest {

    ChordReceiver chordReceiver;
    Chordable chordable;

    @Before
    public void setUp() throws Exception {
        chordable = mock(Chordable.class);
        chordReceiver = new ChordReceiver(Lists.newArrayList(chordable));
        chordReceiver.setChordHold(false);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testChordSent() throws Exception {
        ShortMessage message = createNoteOnMessage(0, 60, 64);
        chordReceiver.send(message, -1);
        verify(chordable, times(1)).setChord(any(Chord.class));
        reset(chordable);

        message = createNoteOnMessage(0, 60, 0);
        chordReceiver.send(message, -1);
        verify(chordable, times(1)).setChord(any(Chord.class));
        reset(chordable);

        message = createNoteOffMessage(0, 60, 64);
        chordReceiver.send(message, -1);
        verify(chordable, times(1)).setChord(any(Chord.class));
        reset(chordable);
    }

    @Test
    public void testNoteOnOff() throws Exception {

        int note = 40;
        ShortMessage noteOnMessage = createNoteOnMessage(0, note, 64);
        ShortMessage noteOffMessage = createNoteOffMessage(0, note, 64);

        // make sure chord includes note
        chordReceiver.send(noteOnMessage, -1);
        ArgumentCaptor<Chord> chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(1)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note, 1, true);
        reset(chordable);

        // make sure chord no longer contains note
        chordReceiver.send(noteOffMessage, -1);
        chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(1)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note, 0, false);
        reset(chordable);

    }

    @Test
    public void testDuplicateNote() throws Exception {

        int note = 56;
        ShortMessage noteOnMessage = createNoteOnMessage(0, note, 64);
        ShortMessage noteOffMessage = createNoteOffMessage(0, note, 64);
        ShortMessage noteOnMessage2 = createNoteOnMessage(0, note + 12, 64);
        ShortMessage noteOnMessage3 = createNoteOnMessage(0, note - 12, 64);

        // make sure chord includes note
        chordReceiver.send(noteOnMessage, -1);
        ArgumentCaptor<Chord> chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(1)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note, 1, true);
        reset(chordable);

        // make sure chord includes note only once
        chordReceiver.send(noteOnMessage, -1);
        chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(1)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note, 1, true);
        reset(chordable);

        // make sure chord includes note only once
        chordReceiver.send(noteOnMessage2, -1);
        chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(1)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note, 1, true);
        reset(chordable);

        // make sure chord includes note only once
        chordReceiver.send(noteOnMessage3, -1);
        chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(1)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note, 1, true);
        reset(chordable);

        // make sure chord no longer contains note
        chordReceiver.send(noteOffMessage, -1);
        chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(1)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note, 0, false);
        reset(chordable);

    }

    @Test
    public void testMultipleNotes() throws Exception {

        int note1 = 36;
        int note2 = 43;
        ShortMessage note1OnMessage = createNoteOnMessage(0, note1, 64);
        ShortMessage note1OffMessage = createNoteOffMessage(0, note1, 64);
        ShortMessage note2OnMessage = createNoteOnMessage(0, note2, 64);
        ShortMessage note2OffMessage = createNoteOffMessage(0, note2, 64);

        // make sure chord includes note1
        chordReceiver.send(note1OnMessage, -1);
        ArgumentCaptor<Chord> chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(1)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note1, 1, true);
        reset(chordable);

        // make sure chord includes note2 (and note1)
        chordReceiver.send(note2OnMessage, -1);
        chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(1)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note2, 2, true);
        checkChordForNote(chordCaptor.getValue(), note1, 2, true);
        reset(chordable);

        // make sure chord no longer contains note1 (but still contains note2)
        chordReceiver.send(note1OffMessage, -1);
        chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(1)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note1, 1, false);
        checkChordForNote(chordCaptor.getValue(), note2, 1, true);
        reset(chordable);

        // make sure chord no longer contains note2 (or note1)
        chordReceiver.send(note2OffMessage, -1);
        chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(1)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note2, 0, false);
        checkChordForNote(chordCaptor.getValue(), note1, 0, false);
        reset(chordable);

    }

    @Test
    public void testChordHold() throws Exception {

        chordReceiver.setChordHold(true);

        int note1 = 36;
        int note2 = 43;
        int note3 = 40;
        ShortMessage note1OnMessage = createNoteOnMessage(0, note1, 64);
        ShortMessage note1OffMessage = createNoteOffMessage(0, note1, 64);
        ShortMessage note2OnMessage = createNoteOnMessage(0, note2, 64);
        ShortMessage note2OffMessage = createNoteOffMessage(0, note2, 64);
        ShortMessage note3OnMessage = createNoteOnMessage(0, note3, 64);
        ShortMessage note3OffMessage = createNoteOffMessage(0, note3, 64);

        // play first note - chord should contain 1 note
        chordReceiver.send(note1OnMessage, -1);
        ArgumentCaptor<Chord> chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(1)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note1, 1, true);
        reset(chordable);

        // play second note - chord should contain 2 notes
        chordReceiver.send(note2OnMessage, -1);
        chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(1)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note1, 2, true);
        checkChordForNote(chordCaptor.getValue(), note2, 2, true);
        reset(chordable);

        // release first note - chord should still contain 2 notes
        chordReceiver.send(note1OffMessage, -1);
        chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(1)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note1, 2, true);
        checkChordForNote(chordCaptor.getValue(), note2, 2, true);
        reset(chordable);

        // play third note - chord should contain 3 notes
        chordReceiver.send(note3OnMessage, -1);
        chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(1)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note1, 3, true);
        checkChordForNote(chordCaptor.getValue(), note2, 3, true);
        checkChordForNote(chordCaptor.getValue(), note3, 3, true);
        reset(chordable);

        // release remaining notes - chord should still contain 3 notes
        chordReceiver.send(note2OffMessage, -1);
        chordReceiver.send(note3OffMessage, -1);
        chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(2)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note1, 3, true);
        checkChordForNote(chordCaptor.getValue(), note2, 3, true);
        checkChordForNote(chordCaptor.getValue(), note3, 3, true);
        reset(chordable);

        // play third note again - should replace chord
        chordReceiver.send(note3OnMessage, -1);
        chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(1)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note3, 1, true);
        reset(chordable);

    }

    @Test
    public void testChordHoldClear() throws Exception {

        chordReceiver.setChordHold(true);

        int note1 = 36;
        int note2 = 43;
        ShortMessage note1OnMessage = createNoteOnMessage(0, note1, 64);
        ShortMessage note1OffMessage = createNoteOffMessage(0, note1, 64);
        ShortMessage note2OnMessage = createNoteOnMessage(0, note2, 64);
        ShortMessage note2OffMessage = createNoteOffMessage(0, note2, 64);

        // play 2 notes - chord should contain both
        chordReceiver.send(note1OnMessage, -1);
        chordReceiver.send(note2OnMessage, -1);
        chordReceiver.send(note1OffMessage, -1);
        chordReceiver.send(note2OffMessage, -1);
        ArgumentCaptor<Chord> chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(4)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note1, 2, true);
        checkChordForNote(chordCaptor.getValue(), note2, 2, true);
        reset(chordable);

        // send the "hold clear" message
        ShortMessage holdOffCcMessage = createCcMessage(0, 64, 0);
        chordReceiver.send(holdOffCcMessage, -1);
        chordCaptor = ArgumentCaptor.forClass(Chord.class);
        verify(chordable, times(1)).setChord(chordCaptor.capture());
        checkChordForNote(chordCaptor.getValue(), note1, 0, false);
        reset(chordable);


    }


    /***** helper methods *****************************************/

    private ShortMessage createNoteOnMessage(int channel, int noteNumber, int velocity) throws Exception {
        ShortMessage message = new ShortMessage();
        message.setMessage(ShortMessage.NOTE_ON, channel, noteNumber, velocity);
        return message;
    }

    private ShortMessage createNoteOffMessage(int channel, int noteNumber, int velocity) throws Exception {
        ShortMessage message = new ShortMessage();
        message.setMessage(ShortMessage.NOTE_OFF, channel, noteNumber, velocity);
        return message;
    }

    private ShortMessage createCcMessage(int channel, int controllerNumber, int value) throws Exception {
        ShortMessage message = new ShortMessage();
        message.setMessage(ShortMessage.CONTROL_CHANGE, channel, controllerNumber, value);
        return message;
    }

    private void checkChordForNote(Chord chord, int note, int totalSize, boolean shouldContainNote) {
        assertEquals(totalSize, chord.size());
        List<Integer> baseNotes = chord.getBaseNotes();
        assertEquals(totalSize, baseNotes.size());
        assertEquals(shouldContainNote, baseNotes.contains(note % 12));
    }

}