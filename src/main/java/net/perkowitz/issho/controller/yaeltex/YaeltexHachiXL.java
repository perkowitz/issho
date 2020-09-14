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
    private static int TOP_BUTTON_START_NOTE = 0;
    private static int BOTTOM_BUTTON_START_NOTE = 56;
    private static int LEFT_BUTTON_START_NOTE = 24;
    private static int RIGHT_BUTTON_START_NOTE = 40;
    private static int KNOBS_START_CC = 16;


    // The HachiXL uses an indexed color table. These are constants for common colors.
    private static Map<Color, Integer> colorMap = Maps.newHashMap();
    static {
        colorMap.put(Colors.BLACK, 0);
        colorMap.put(Colors.WHITE, 127);
        colorMap.put(Colors.GRAY, 1);
        colorMap.put(Colors.LIGHT_GRAY, 2);
        colorMap.put(Colors.DARK_GRAY, 71);
        colorMap.put(Colors.BRIGHT_GREEN, 40);
        colorMap.put(Colors.DIM_GREEN, 44);
        colorMap.put(Colors.BRIGHT_RED, 1);
        colorMap.put(Colors.DIM_RED, 2);
        colorMap.put(Colors.BRIGHT_ORANGE, 13);
        colorMap.put(Colors.DIM_ORANGE, 14);
        colorMap.put(Colors.BRIGHT_BLUE, 73);
        colorMap.put(Colors.DIM_BLUE, 74);
        colorMap.put(Colors.BRIGHT_CYAN, 67);
        colorMap.put(Colors.DIM_CYAN, 69);
        colorMap.put(Colors.BRIGHT_YELLOW, 22);
        colorMap.put(Colors.DIM_YELLOW, 24);
        colorMap.put(Colors.BRIGHT_PINK, 116);
        colorMap.put(Colors.DIM_PINK, 117);
        colorMap.put(Colors.BRIGHT_MAGENTA, 106);
        colorMap.put(Colors.DIM_MAGENTA, 107);
        colorMap.put(Colors.BRIGHT_PURPLE, 97);
        colorMap.put(Colors.DIM_PURPLE, 98);
    }

    private MidiOut midiOut;
    @Setter private ControllerListener listener;

    public YaeltexHachiXL(MidiOut midiOut, ControllerListener listener) {
        this.midiOut = midiOut;
        this.listener = listener;
    }


    public void initialize() {
        for (int row = 0; row < PADS_MAX_ROWS; row++) {
            for (int column = 0; column < PADS_MAX_COLUMNS; column++) {
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
        }
    }

    public void setPad(Pad pad, Color color) {
//        System.out.printf("Haxl setPad: p=%s, c=%s, n=%d, v=%d\n", pad, color, padToNote(pad), colorToIndex(color));
        midiOut.note(PADS_CHANNEL, padToNote(pad), colorToIndex(color));
    }

    public void setPad(Pad pad, int colorIndex) {
        midiOut.note(PADS_CHANNEL, padToNote(pad), colorIndex);
    }

    public void setButton(Button button, Color color) {
//        System.out.printf("Haxl setButton: b=%s, c=%s, n=%d\n", button, color, buttonToNote(button));
        midiOut.note(BUTTONS_CHANNEL, buttonToNote(button), colorToIndex(color));
    }

    public void setKnob(Knob knob, Color color) {
        // Knob colors are fixed at the moment
    }

    public void setKnobValue(Knob knob, int value) {
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

    }


    /***** private implementation **************************************************************/
    
    private int colorToIndex(Color color) {
        Integer index = colorMap.get(color);
        if (index == null) {
            return 0;
        }
        return index;
    }

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
        }
        return Button.at(BUTTONS_TOP, 0);
    }


}
