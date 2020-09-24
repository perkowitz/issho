package net.perkowitz.issho.controller.yaeltex;

import com.google.common.collect.Maps;
import lombok.Setter;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.ControllerListener;
import net.perkowitz.issho.controller.MidiOut;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.*;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.awt.*;
import java.util.Map;

import static javax.sound.midi.ShortMessage.*;


/**
 * Created by mikep on 7/28/20.
 */
public class YaeltexHachiXL implements Controller, Receiver {

    private static int MIDI_REALTIME_COMMAND = 0xF0;
    private static int PADS_CHANNEL = 0;
    private static int BUTTONS_CHANNEL = 1;
    private static int KNOBS_CHANNEL = 0;

    // HachXL buttons appear in 4 groups, one on each side of the device.
    public static final int PADS_GROUP = 0;
    public static final int PADS_MAX_ROWS = 8;
    public static final int PADS_MAX_COLUMNS = 16;
    public static final int BUTTONS_TOP = 0;
    public static final int BUTTONS_BOTTOM = 1;
    public static final int BUTTONS_LEFT = 2;
    public static final int BUTTONS_RIGHT = 3;
    public static final int KNOB_BUTTONS = 4;
    public static final int MAX_BUTTONS = 16;
    public static final int MAX_BUTTONS_BOTTOM = 12;
    public static final int KNOBS_GROUP = 0;
    public static final int MAX_KNOBS = 8;

    // elements
    public static final ElementSet pads = ElementSet.pads(PADS_GROUP, 0, PADS_MAX_ROWS, 0, PADS_MAX_COLUMNS);
    public static final ElementSet topButtons = ElementSet.buttons(BUTTONS_TOP, 0, MAX_BUTTONS);
    public static final ElementSet bottomButtons = ElementSet.buttons(BUTTONS_BOTTOM, 0, MAX_BUTTONS_BOTTOM);
    public static final ElementSet leftButtons = ElementSet.buttons(BUTTONS_LEFT, 0, MAX_BUTTONS);
    public static final ElementSet rightButtons = ElementSet.buttons(BUTTONS_RIGHT, 0, MAX_BUTTONS);
    public static final ElementSet knobs = ElementSet.knobs(KNOBS_GROUP, 0, MAX_KNOBS);
    public static final ElementSet knobButtons = ElementSet.buttons(KNOB_BUTTONS, 0, MAX_KNOBS);
    private static int TOP_BUTTON_START_NOTE = 0;
    private static int BOTTOM_BUTTON_START_NOTE = 56;
    private static int LEFT_BUTTON_START_NOTE = 24;
    private static int RIGHT_BUTTON_START_NOTE = 40;
    private static int KNOBS_START_CC = 16;
    private static int KNOB_BUTTONS_START_NOTE = KNOBS_START_CC;

    // member variables
    @Setter private Map<Color, Integer> colorMap = Maps.newHashMap();
    private MidiOut midiOut;
    @Setter private ControllerListener listener;

    public YaeltexHachiXL(MidiOut midiOut, ControllerListener listener) {
        this.midiOut = midiOut;
        this.listener = listener;
        this.colorMap = ColorModes.twoBitMap;
    }


    public void initialize() {
        for (int row = 0; row < PADS_MAX_ROWS; row++) {
            for (int column = 0; column < PADS_MAX_COLUMNS; column++) {
//                System.out.printf("initPad: %d, %d\n", row, column);
                setPad(Pad.at(0, row, column), Colors.BLACK);
            }
        }

        for (int group : new int[]{ BUTTONS_TOP, BUTTONS_LEFT, BUTTONS_RIGHT }) {
            for (int index = 0; index < MAX_BUTTONS; index++) {
                setButton(Button.at(group, index), Colors.BLACK);
            }
        }
        for (int index = 0; index < MAX_BUTTONS_BOTTOM; index++) {
            setButton(Button.at(BUTTONS_BOTTOM, index), Colors.BLACK);
        }

        for (int index = 0; index < MAX_KNOBS; index++) {
            setKnobValue(Knob.at(KNOBS_GROUP, index), 0);
            setButton(Button.at(KNOB_BUTTONS, index), Colors.BLACK);
        }

        setButton(Button.at(BUTTONS_BOTTOM, 0), Colors.BLACK);
    }

    public void setPad(Pad pad, Color color) {
        if (pad.getRow() >= PADS_MAX_ROWS || pad.getColumn() >= PADS_MAX_COLUMNS
                || pad.getRow() < 0 || pad.getColumn() < 0) {
            return;
        }
        midiOut.note(PADS_CHANNEL, padToNote(pad), colorToIndex(color));
    }

    public void setPad(Pad pad, int colorIndex) {
        if (pad.getRow() >= PADS_MAX_ROWS || pad.getColumn() >= PADS_MAX_COLUMNS
                || pad.getRow() < 0 || pad.getColumn() < 0) {
            return;
        }
        midiOut.note(PADS_CHANNEL, padToNote(pad), colorIndex);
    }

    public void setButton(Button button, Color color) {
        // TODO: check button within range
        midiOut.note(BUTTONS_CHANNEL, buttonToNote(button), colorToIndex(color));
    }

    public void setKnob(Knob knob, Color color) {
        // Knob colors are fixed at the moment
        // TODO: check knob within range
    }

    public void setKnobValue(Knob knob, int value) {
        // TODO: check knob within range
        midiOut.cc(KNOBS_CHANNEL, knob.getIndex() + KNOBS_START_CC, value);
    }

    public void setLight(Light light, Color color) {}

    @Override
    public String toString() {
        return name();
    }

    public static String name() {
        return "YaeltexHachiXL";
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
                            if (shortMessage.getChannel() == PADS_CHANNEL) {
                                Pad pad = noteToPad(shortMessage.getData1());
                                int velocity = shortMessage.getData2();
                                if (velocity == 0) {
                                    listener.onElementReleased(pad);
                                } else {
                                    listener.onElementPressed(pad, velocity);
                                }
                            } else if (shortMessage.getChannel() == BUTTONS_CHANNEL) {
                                Button button = noteToButton(shortMessage.getData1());
                                int velocity = shortMessage.getData2();
                                if (velocity == 0) {
                                    listener.onElementReleased(button);
                                } else {
                                    listener.onElementPressed(button, velocity);
                                }
                            }
                        }
                        break;
                    case NOTE_OFF:
//                        System.out.printf("NOTE OFF: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        if (listener != null) {
                            if (shortMessage.getChannel() == PADS_CHANNEL) {
                                Pad pad = noteToPad(shortMessage.getData1());
                                listener.onElementReleased(pad);
                            } else if (shortMessage.getChannel() == BUTTONS_CHANNEL) {
                                Button button = noteToButton(shortMessage.getData1());
                                listener.onElementReleased(button);
                            }
                        }
                        break;
                    case CONTROL_CHANGE:
//                        System.out.printf("MIDI CC: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        break;
                    default:
                }
            }
        }
    }

    public void close() {
        midiOut.close();
    }


    /***** private implementation **************************************************************/
    
    private int colorToIndex(Color color) {
        Integer index = colorMap.get(color);
        if (index == null) {
            return 0;
        }
        return index;
    }

//    private int colorToIndex(Color color) {
//        int r = color.getRed() & 0b11000000;
//        int g = color.getGreen() & 0b11000000;
//        int b = color.getRGB() & 0b11100000;
//        int i = (r >> 1) + (g >> 3) + (b >> 5);
//        if (r != 0 || g != 0 || b != 0) {
////            System.out.printf("colorToIndex: c=%s, r=%d, g=%d, b=%d, i=%d\n", color, r, g, b, i);
//            System.out.printf("colorToIndex: c=%32s, r=%8s, g=%8s, b=%8s, i=%8s\n", color,
//                    Integer.toString(r, 2),
//                    Integer.toString(g, 2),
//                    Integer.toString(b, 2),
//                    Integer.toString(i, 2));
//        }
//        return i;
//    }

    private int padToNote(Pad pad) {
        int note = pad.getRow() * 16 + pad.getColumn();
//        System.out.printf("Haxl padToNote p=%s, n=%d\n", pad, note);
        return pad.getRow() * 16 + pad.getColumn();
    }

    private Pad noteToPad(int note) {
        int column = note % 16;
        int row = note / 16;
        return Pad.at(0, row, column);
    }


    private int buttonToNote(Button button) {
        switch (button.getGroup()) {
            case BUTTONS_TOP:
                return button.getIndex() + TOP_BUTTON_START_NOTE;
            case BUTTONS_BOTTOM:
                return button.getIndex() + BOTTOM_BUTTON_START_NOTE;
            case BUTTONS_LEFT:
                return button.getIndex() + LEFT_BUTTON_START_NOTE;
            case BUTTONS_RIGHT:
                return button.getIndex() + RIGHT_BUTTON_START_NOTE;
            case KNOB_BUTTONS:
                return button.getIndex() + KNOB_BUTTONS_START_NOTE;
        }
        return 0;
    }

    private Button noteToButton(int note) {
        if (note >= TOP_BUTTON_START_NOTE && note < TOP_BUTTON_START_NOTE + MAX_BUTTONS) {
            return Button.at(BUTTONS_TOP, note - TOP_BUTTON_START_NOTE);
        } else if (note >= BOTTOM_BUTTON_START_NOTE && note < BOTTOM_BUTTON_START_NOTE + MAX_BUTTONS_BOTTOM) {
            return Button.at(BUTTONS_BOTTOM, note - BOTTOM_BUTTON_START_NOTE);
        } else if (note >= LEFT_BUTTON_START_NOTE && note < LEFT_BUTTON_START_NOTE + MAX_BUTTONS) {
            return Button.at(BUTTONS_LEFT, note - LEFT_BUTTON_START_NOTE);
        } else if (note >= RIGHT_BUTTON_START_NOTE && note < RIGHT_BUTTON_START_NOTE + MAX_BUTTONS) {
            return Button.at(BUTTONS_RIGHT, note - RIGHT_BUTTON_START_NOTE);
        } else if (note >= KNOB_BUTTONS_START_NOTE && note < KNOB_BUTTONS_START_NOTE + MAX_KNOBS) {
            return Button.at(KNOB_BUTTONS, note - KNOB_BUTTONS_START_NOTE);
        }
        return Button.at(BUTTONS_TOP, 0);
    }


}
