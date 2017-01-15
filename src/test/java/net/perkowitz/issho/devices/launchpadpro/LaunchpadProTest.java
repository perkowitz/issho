package net.perkowitz.issho.devices.launchpadpro;

import com.google.common.collect.Sets;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridListener;
import net.perkowitz.issho.devices.GridPad;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import static javax.sound.midi.ShortMessage.CONTROL_CHANGE;
import static javax.sound.midi.ShortMessage.NOTE_ON;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by optic on 12/26/16.
 */
public class LaunchpadProTest {

    LaunchpadPro launchpadPro;
    Receiver receiver;
    GridListener listener;

    @Before
    public void setUp() throws Exception {
        receiver = mock(Receiver.class);
        listener = mock(GridListener.class);
        launchpadPro = new LaunchpadPro(receiver, listener);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void initialize() throws Exception {
        // full initialize should set all 64 pads and 32 buttons to OFF
        launchpadPro.initialize();
        verify(receiver, times(96)).send(any(MidiMessage.class), any(Long.class));
        reset(receiver);

        // init nothing
        launchpadPro.initialize(false, null);
        verify(receiver, times(0)).send(any(MidiMessage.class), any(Long.class));
        reset(receiver);

        // init pads only
        launchpadPro.initialize(true, null);
        verify(receiver, times(64)).send(any(MidiMessage.class), any(Long.class));
        reset(receiver);

        // init sides only
        launchpadPro.initialize(false, Sets.newHashSet(GridButton.Side.Top, GridButton.Side.Bottom, GridButton.Side.Left, GridButton.Side.Right));
        verify(receiver, times(32)).send(any(MidiMessage.class), any(Long.class));
        reset(receiver);

        // init one side only
        launchpadPro.initialize(false, Sets.newHashSet(GridButton.Side.Top));
        verify(receiver, times(8)).send(any(MidiMessage.class), any(Long.class));
        reset(receiver);
    }

    @Test
    public void setPad() throws Exception {
        int velocity = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                GridPad pad = GridPad.at(x, y);
                ArgumentCaptor<MidiMessage> messageArgumentCaptor = ArgumentCaptor.forClass(MidiMessage.class);
                launchpadPro.setPad(pad, Color.fromIndex(velocity));
                verify(receiver).send(messageArgumentCaptor.capture(), eq(-1L));
                MidiMessage message = messageArgumentCaptor.getValue();
                verifyNoteMessage(message, pad2note(x, y), velocity);
                reset(receiver);
                velocity++;
            }
        }
    }

    @Test
    public void setButton() throws Exception {
        int velocity = 0;
        for (GridButton.Side side : GridButton.Side.values()) {
            for (int index = 0; index < 8; index++) {
                GridButton button = GridButton.at(side, index);
                ArgumentCaptor<MidiMessage> messageArgumentCaptor = ArgumentCaptor.forClass(MidiMessage.class);
                launchpadPro.setButton(button, Color.fromIndex(velocity));
                verify(receiver).send(messageArgumentCaptor.capture(), eq(-1L));
                MidiMessage message = messageArgumentCaptor.getValue();
                verifyCCMessage(message, button2cc(side, index), velocity);
                reset(receiver);
                velocity++;
            }
        }
    }

    @Test
    public void padPressed() throws Exception {
        int channel = 0;
        int velocity = 100;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                ShortMessage message = new ShortMessage();
                message.setMessage(NOTE_ON, channel, pad2note(x, y), velocity);
                ArgumentCaptor<GridPad> padArgumentCaptor = ArgumentCaptor.forClass(GridPad.class);
                launchpadPro.send(message, -1);
                verify(listener).onPadPressed(padArgumentCaptor.capture(), eq(velocity));
                GridPad pad = padArgumentCaptor.getValue();
                assertEquals(x, pad.getX());
                assertEquals(y, pad.getY());
                reset(listener);
            }
        }
    }

    @Test
    public void padReleased() throws Exception {
        int channel = 0;
        int velocity = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                ShortMessage message = new ShortMessage();
                message.setMessage(NOTE_ON, channel, pad2note(x, y), velocity);
                ArgumentCaptor<GridPad> padArgumentCaptor = ArgumentCaptor.forClass(GridPad.class);
                launchpadPro.send(message, -1);
                verify(listener).onPadReleased(padArgumentCaptor.capture());
                GridPad pad = padArgumentCaptor.getValue();
                assertEquals(x, pad.getX());
                assertEquals(y, pad.getY());
                reset(listener);
            }
        }
    }


    @Test
    public void buttonPressed() throws Exception {
        int channel = 0;
        int velocity = 100;
        for (GridButton.Side side : GridButton.Side.values()) {
            for (int index = 0; index < 8; index++) {
                ShortMessage message = new ShortMessage();
                message.setMessage(CONTROL_CHANGE, channel, button2cc(side, index), velocity);
                ArgumentCaptor<GridButton> buttonArgumentCaptor = ArgumentCaptor.forClass(GridButton.class);
                launchpadPro.send(message, -1);
                verify(listener).onButtonPressed(buttonArgumentCaptor.capture(), eq(velocity));
                GridButton button = buttonArgumentCaptor.getValue();
                assertEquals(side, button.getSide());
                assertEquals(index, button.getIndex());
                reset(listener);
            }
        }
    }

    @Test
    public void buttonReleased() throws Exception {
        int channel = 0;
        int velocity = 0;
        for (GridButton.Side side : GridButton.Side.values()) {
            for (int index = 0; index < 8; index++) {
                ShortMessage message = new ShortMessage();
                message.setMessage(CONTROL_CHANGE, channel, button2cc(side, index), velocity);
                ArgumentCaptor<GridButton> buttonArgumentCaptor = ArgumentCaptor.forClass(GridButton.class);
                launchpadPro.send(message, -1);
                verify(listener).onButtonReleased(buttonArgumentCaptor.capture());
                GridButton button = buttonArgumentCaptor.getValue();
                assertEquals(side, button.getSide());
                assertEquals(index, button.getIndex());
                reset(listener);
            }
        }
    }


    /***** helper methods *****************************************/

    private int pad2note(int x, int y) {
        // see launchpad pro programmers reference guide, p 17
        return (8-y) * 10 + x + 1;
    }

    private int button2cc(GridButton.Side side, int index) {
        // see launchpad pro programmers reference guide, p 17
        switch (side) {
            case Bottom:
                return index + 1;
            case Top:
                return index + 91;
            case Left:
                return (8-index) * 10;
            case Right:
                return (8-index) * 10 + 9;
        }
        return -1;
    }

    private void verifyNoteMessage(MidiMessage message, int note, int velocity) {
        ShortMessage shortMessage = (ShortMessage) message;
        int command = shortMessage.getCommand();
        assertEquals(NOTE_ON, command);
        assertEquals(note, shortMessage.getData1());
        assertEquals(velocity, shortMessage.getData2());
    }

    private void verifyCCMessage(MidiMessage message, int cc, int velocity) {
        ShortMessage shortMessage = (ShortMessage) message;
        int command = shortMessage.getCommand();
        assertEquals(CONTROL_CHANGE, command);
        assertEquals(cc, shortMessage.getData1());
        assertEquals(velocity, shortMessage.getData2());
    }

}