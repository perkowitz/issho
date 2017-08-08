package net.perkowitz.issho.devices.behringerlc1;


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
public class BehringerLC1 implements GridDevice {

    public static final int ROW_COUNT = 8;
    public static final int COLUMN_COUNT = 4;
    public static final int BUTTON_COUNT = 8;
    public static final int KNOB_COUNT = 8;

    private static final int PAD_NOTE_MIN = 32;
    private static final int BUTTON_TOP_NOTE_MIN = 16;
    private static final int BUTTON_RIGHT_NOTE_MIN = 64;
    private static final int BUTTON_BOTTOM_NOTE_MIN = 72;

    private static int MIDI_REALTIME_COMMAND = 0xF0;

    private static int CHANNEL = 7;

    private Receiver receiver;
    @Setter private GridListener listener;

    public BehringerLC1(Receiver receiver, GridListener listener) {
        this.receiver = receiver;
        this.listener = listener;
    }


    /****** public logical implementation ***********************************************************/

    public void initialize(boolean pads, Set<GridButton.Side> buttonSides) {

        System.out.println("Initializing pads...");
        for (int y = 0; y < ROW_COUNT; y++) {
            if (pads) {
                for (int x = 0; x < COLUMN_COUNT; x++) {
                    setPad(GridPad.at(x, y), Color.fromIndex(127));
                }
            }
        }

        System.out.println("Initializing buttons...");
        for (int index = 0; index < BUTTON_COUNT; index++) {
            if (buttonSides != null) {
                for (GridButton.Side side : buttonSides) {
                    setButton(GridButton.at(side, index), Color.fromIndex(1));
                }
            }
        }

        System.out.println("Initializing knobs...");
        for (int index = 0; index < KNOB_COUNT; index++) {
            setKnob(GridKnob.at(GridKnob.Side.Top, index), 8);
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

        if (pad.getX() < 0 || pad.getX() >= COLUMN_COUNT ||
                pad.getY() < 0 || pad.getY() >= ROW_COUNT) {
            return;
        }

        int note = 32 + pad.getY()*4 + pad.getX();
        note(CHANNEL, note, color.getIndex());
    }

    public void setButton(GridButton button, GridColor color) {

        if (button.getSide() == Top) {
            int note = 16 + button.getIndex();
            note(CHANNEL, note, color.getIndex());
        } else if (button.getSide() == Right) {
            int note = 64 + button.getIndex();
            note(CHANNEL, note, color.getIndex());
        } else if (button.getSide() == Bottom) {
            if (button.getIndex() < 4) {
                int note = 72 + button.getIndex();
                note(CHANNEL, note, color.getIndex());
            }
        }
    }

    public void setKnob(GridKnob knob, int value) {
        cc(CHANNEL, knob.getIndex() + 16, value);
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
                        int velocity = shortMessage.getData2();
                        GridControl control = noteToControl(shortMessage.getData1());
                        if (control != null && control.getPad() != null) {
                            if (velocity == 0) {
                                listener.onPadReleased(control.getPad());
                            } else {
                                listener.onPadPressed(control.getPad(), velocity);
                            }
                        } else if (control != null && control.getButton() != null) {
                            if (velocity == 0) {
                                listener.onButtonReleased(control.getButton());
                            } else {
                                listener.onButtonPressed(control.getButton(), velocity);
                            }
                        }
                        break;

                    case NOTE_OFF:
//                        System.out.printf("NOTE OFF: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        control = noteToControl(shortMessage.getData1());
                        if (control != null && control.getPad() != null) {
                            listener.onPadReleased(control.getPad());
                        } else if (control != null && control.getButton() != null) {
                            listener.onButtonReleased(control.getButton());
                        }
                        break;

                    case CONTROL_CHANGE:
//                        System.out.printf("MIDI CC: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        int cc = shortMessage.getData1();
                        int value = shortMessage.getData2();
                        GridKnob knob = ccToKnob(cc);
                        listener.onKnobChanged(knob, value - 64);  // 65 = +1, 63 = -1; no other values are transmitted
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

    private GridControl noteToControl(int note) {

        if (note < BUTTON_TOP_NOTE_MIN) {
            // nope
        } else if (note < PAD_NOTE_MIN) {
            int index = note - BUTTON_TOP_NOTE_MIN;
            return new GridControl(GridButton.at(Top, index), index);
        } else if (note < BUTTON_RIGHT_NOTE_MIN) {
            int index = note - PAD_NOTE_MIN;
            int x = index % 4;
            int y = index / 4;
            return new GridControl(GridPad.at(x, y), index);
        } else if (note < BUTTON_BOTTOM_NOTE_MIN) {
            int index = note - BUTTON_RIGHT_NOTE_MIN;
            return new GridControl(GridButton.at(Right, index), index);
        } else if (note < BUTTON_BOTTOM_NOTE_MIN + BUTTON_COUNT) {
            int index = note - BUTTON_BOTTOM_NOTE_MIN;
            return new GridControl(GridButton.at(Bottom, index), index);
        }

        return null;
    }

    private GridKnob ccToKnob(int cc) {
        return GridKnob.at(GridKnob.Side.Top, cc - 16);
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
