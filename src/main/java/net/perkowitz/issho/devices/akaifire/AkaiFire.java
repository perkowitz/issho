package net.perkowitz.issho.devices.akaifire;

import net.perkowitz.issho.devices.*;

import javax.sound.midi.*;
import java.util.Set;

import static net.perkowitz.issho.devices.GridButton.Side.*;

public class AkaiFire implements GridDevice {

    private Receiver receiver;

    public void initialize() {

    }

    public void initialize(boolean pads, Set<GridButton.Side> buttonSides) {
    }

    public void setPad(GridPad pad, GridColor color) {

    }

    public void setButton(GridButton button, GridColor color) {

    }

    public void setKnob(GridKnob knob, int value) {

    }

    public void setListener(GridListener listener) {
        
    }

    /***** private implementation **************************************************************/

    private void note(int channel, int noteNumber, int velocity) {

        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(ShortMessage.NOTE_ON, channel, noteNumber, velocity);
            receiver.send(message, -1);

        } catch (InvalidMidiDataException e) {
            System.err.println(e);
        }

    }

    private void cc(int channel, int ccNumber, int value) {

        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(ShortMessage.CONTROL_CHANGE, channel, ccNumber, value);
            receiver.send(message, -1);

        } catch (InvalidMidiDataException e) {
            System.err.println(e);
        }

    }

    private int padToNote(GridPad pad) {
        return (7-pad.getY()) * 10 + pad.getX() + 11;
    }

    private GridPad noteToPad(int note) {
        int x = note % 10 - 1;
        int y = 7 - (note / 10 - 1);
        return GridPad.at(x, y);

    }

    private int buttonToCc(GridButton button) {

        GridButton.Side side = button.getSide();
        int index = button.getIndex();
        int flippedIndex = 7 - index;
        switch (side) {
            case Top:
                return 90 + index + 1;
            case Bottom:
                return index + 1;
            case Left:
                return 10 + flippedIndex * 10;
            case Right:
                return 19 + flippedIndex * 10;
            default:
                return 100;
        }

    }

    public static GridButton ccToButton(int cc) {

        GridButton.Side side = Top;
        int index = 0;

        if (cc >= 10 && cc <= 89) {
            index = 7 - (cc / 10 - 1);
            side = (cc % 10 == 0) ? Left : Right;
        } else {
            index = cc % 10 - 1;
            side = (cc < 10) ? Bottom : Top;
        }

        return GridButton.at(side, index);
    }

    public void send(MidiMessage message, long timeStamp) {
    }

    public void close() {

    }
    
}
