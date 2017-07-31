package net.perkowitz.issho.devices.launchpad;


import com.google.common.collect.Sets;
import lombok.Setter;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.devices.launchpadpro.Color;

import javax.sound.midi.*;
import java.util.Set;

import static javax.sound.midi.ShortMessage.*;
import static net.perkowitz.issho.devices.GridButton.Side.*;

/**
 * Created by optic on 9/3/16.
 */
public class Launchpad implements GridDevice {

    private static int MIDI_REALTIME_COMMAND = 0xF0;

    private static int CHANNEL = 0;

    private Receiver receiver;
    @Setter private GridListener listener;

    public Launchpad(Receiver receiver, GridListener listener) {
        this.receiver = receiver;
        this.listener = listener;
    }


    /****** public logical implementation ***********************************************************/

    public void initialize(boolean pads, Set<GridButton.Side> buttonSides) {
        for (int y = 0; y < 8; y++) {
            if (pads) {
                for (int x = 0; x < 8; x++) {
                    setPad(GridPad.at(x, y), Color.OFF);
                }
            }
            if (buttonSides != null) {
                for (GridButton.Side side : buttonSides) {
                    setButton(GridButton.at(side, y), Color.OFF);
                }
            }
        }
    }

    public void initialize() {
        initialize(true, Sets.newHashSet(Top, Bottom, Left, Right));
    }

    public void setPads(GridPad[] pads, Color color) {
        for (GridPad pad : pads) {
            setPad(pad, color);
        }
    }

    public void setPad(GridPad pad, GridColor color) {
        note(CHANNEL, padToNote(pad), color.getIndex());
    }

    public void setButton(GridButton button, GridColor color) {
        if (button.getSide() == GridButton.Side.Left) {
            // nope
        } else if (button.getSide() == GridButton.Side.Bottom) {
            // nope
        } else if (button.getSide() == GridButton.Side.Right) {
            // right side uses notes, as though they were a 9th column of the grid pad
            int note = (button.getIndex()) * 16 + 8;
            note(CHANNEL, note, color.getIndex());
        } else if (button.getSide() == GridButton.Side.Top) {
            // nope
            int cc = 104 + button.getIndex();
            cc(CHANNEL, cc, color.getIndex());
        }
    }

    public void setSide() {
        sysex();
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
                        System.out.println("START");
                        break;
                    case STOP:
                        System.out.println("STOP");
                        break;
                    case TIMING_CLOCK:
                        System.out.println("TICK");
                        break;
                    default:
//                        System.out.printf("REALTIME: %d\n", status);
                        break;
                }


            } else {
                switch (command) {
                    case NOTE_ON:
//                        System.out.printf("NOTE ON: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        if (listener != null) {
                            GridPad pad = noteToPad(shortMessage.getData1());
                            int velocity = shortMessage.getData2();
                            if (velocity == 0) {
                                listener.onPadReleased(pad);
                            } else {
                                listener.onPadPressed(pad, velocity);
                            }
                        }
                        break;
                    case NOTE_OFF:
//                        System.out.printf("NOTE OFF: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        if (listener != null) {
                            GridPad pad = noteToPad(shortMessage.getData1());
                            listener.onPadReleased(pad);
                        }
                        break;
                    case CONTROL_CHANGE:
//                        System.out.printf("MIDI CC: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        if (listener != null) {
                            GridButton button = ccToButton(shortMessage.getData1());
                            int velocity = shortMessage.getData2();
                            if (velocity == 0) {
                                listener.onButtonReleased(button);
                            } else {
                                listener.onButtonPressed(button, velocity);
                            }
                        }
                        break;
                    default:
                }
            }
        }
    }

    public void close() {

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

    // sysex via javax classes doesn't seem to work on osx
    private void sysex() {

        byte[] testMsg = {(byte) 0xf0, 0x00, 0x20, 0x29, 0x02, 0x10, 0x0b, 0x63, 0x00, 0x00, 0x3f, (byte) 0xf7 };

        try {
            SysexMessage message = new SysexMessage();
            message.setMessage(testMsg, testMsg.length);

            receiver.send(message, -1);

        } catch (InvalidMidiDataException e) {
            System.err.println(e);
        }

    }

    private int padToNote(GridPad pad) {
        return (pad.getY()) * 16 + pad.getX();
    }

    private GridPad noteToPad(int note) {
        int x = note % 16;
        int y = (note / 16);
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



}
