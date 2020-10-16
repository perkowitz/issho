package net.perkowitz.issho.controller.akai;

import com.google.common.collect.Maps;
import lombok.Setter;
import net.perkowitz.issho.controller.*;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.Element;
import net.perkowitz.issho.controller.elements.Knob;
import net.perkowitz.issho.controller.elements.Light;
import net.perkowitz.issho.controller.elements.Pad;
import net.perkowitz.issho.controller.midi.MidiOut;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.awt.*;
import java.util.Map;

import static javax.sound.midi.ShortMessage.*;

public class AkaiFire implements Receiver, Controller {

    // count of various types of controls on various sizes
    public static final int BUTTONS_TOP = 5;
    public static final int BUTTONS_BOTTOM = 10;
    public static final int BUTTONS_LEFT = 4;
    public static final int BUTTONS_RIGHT = 0;
    public static final int BUTTONS_OTHER = 1;
    public static final int LIGHTS_COUNT = 4;
    public static final int KNOBS_COUNT = 4;
    public static final int PAD_ROWS = 4;
    public static final int PAD_COLUMNS = 16;

    // starting note or CC numbers for various groups of controls
    private static final int BUTTONS_TOP_START = 0x1F;
    private static final int BUTTONS_BOTTOM_START = 0x2C;
    private static final int BUTTONS_LEFT_START = 0x24;
    private static final int BUTTONS_RIGHT_START = 0x00;
    private static final int BUTTONS_OTHER_START = 0x1A;
    private static final int KNOBS_TOUCH_START = 0x10;
    private static final int LIGHTS_START = 0x28;
    private static final int PADS_START = 0x36;

    private static final int GROUP_TOP = 0;
    private static final int GROUP_BOTTOM= 1;
    private static final int GROUP_LEFT = 2;
    private static final int GROUP_RIGHT = 3;
    private static final int GROUP_OTHER = 4;

    private static final byte[] padMessage = { (byte)0xF0, 0x47, 0x7F, 0x43, 0x65, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, (byte)0xF7 };

    public static Map<Integer, Integer> buttonCount = Maps.newHashMap();
    private static Map<Integer, Integer> buttonStart = Maps.newHashMap();
    static {
        buttonCount.put(GROUP_TOP, BUTTONS_TOP);
        buttonCount.put(GROUP_BOTTOM, BUTTONS_BOTTOM);
        buttonCount.put(GROUP_LEFT, BUTTONS_LEFT);
        buttonCount.put(GROUP_RIGHT, BUTTONS_RIGHT);
        buttonCount.put(GROUP_OTHER, BUTTONS_OTHER);
        buttonStart.put(GROUP_TOP, BUTTONS_TOP_START);
        buttonStart.put(GROUP_BOTTOM, BUTTONS_BOTTOM_START);
        buttonStart.put(GROUP_LEFT, BUTTONS_LEFT_START);
        buttonStart.put(GROUP_RIGHT, BUTTONS_RIGHT_START);
        buttonStart.put(GROUP_OTHER, BUTTONS_OTHER_START);
    }

    private int channel = 0;
    private MidiOut midiOut;
    @Setter private ControllerListener listener;


    public AkaiFire(MidiOut midiOut, ControllerListener listener) {
        this.midiOut = midiOut;
        this.listener = listener;
    }


    public void initialize() {

    }

    public void setPad(Pad pad, Color color) {
        if (pad.getRow() < 0 || pad.getRow() >= PAD_ROWS || pad.getColumn() < 0 || pad.getColumn() >= PAD_COLUMNS) {
            return;
        }

        int index = pad.getRow() * 16 + pad.getColumn();
        byte[] bytes = padBytes(index, color.getRed(), color.getGreen(), color.getBlue());
        midiOut.sysex(bytes);
    }

    public void setButton(Button button, Color color) {
        int index = button.getIndex();
        Integer c = buttonCount.get(button.getGroup());
        if (index < 0 || c == null || index >= c) {
            return;
        }
        Integer s = buttonStart.get(button.getGroup());
        if (s == null) return;

        int note = s + index;
        int velocity = 0;

        // monochrome buttons
        if (button.getGroup() == GROUP_TOP || button.getGroup() == GROUP_LEFT
                || button.equals(Button.at(GROUP_BOTTOM, 5))
                || button.equals(Button.at(GROUP_BOTTOM, 8))) {
            if (color.equals(Colors.OFF)) {
                velocity = 0;
            } else if (color.equals(Colors.DIM) || color.equals(Colors.DIM_RED)
                    || color.equals(Colors.DIM_GREEN) || color.equals(Colors.DIM_YELLOW)) {
                velocity = 1;
            } else {
                velocity = 2;
            }
        } else {
            if (color.equals(Colors.OFF)) {
                velocity = 0;
            } else if (color.equals(Colors.DIM_YELLOW)) {
                velocity = 1;
            } else if (color.equals(Colors.DIM_RED) || color.equals(Colors.DIM_GREEN)) {
                velocity = 2;
            } else if (color.equals(Colors.BRIGHT_YELLOW)) {
                velocity = 3;
            } else {
                velocity = 4;
            }
        }

        midiOut.note(channel, note, velocity);
    }

    public void setKnob(Knob knob, Color color) {
        // in fact there is no display for the knobs
    }
    public void setKnobValue(Knob knob, int value) {}

    public void setLight(Light light, Color color) {
        if (light.getGroup() != GROUP_LEFT || light.getIndex() < 0 || light.getIndex() >= LIGHTS_COUNT) {
            return;
        }

        int cc = LIGHTS_START + light.getIndex();
        int value = 0;
        if (color.equals(Colors.OFF)) {
            value = 0;
        } else if (color.equals(Colors.DIM_RED)) {
            value = 1;
        } else if (color.equals(Colors.DIM_GREEN)) {
            value = 2;
        } else if (color.equals(Colors.BRIGHT_RED)) {
            value = 3;
        } else {
            value = 4;
        }

        midiOut.cc(channel, cc, value);
    }


    /***** midi receiver implementation *****/

    public void send(MidiMessage message, long timeStamp) {

        System.out.printf("MidiMessage: %s\n", message);
        if (message instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage) message;
            int command = shortMessage.getCommand();
            int status = shortMessage.getStatus();
            if (command == NOTE_ON) {
                int note = shortMessage.getData1();
                int velocity = shortMessage.getData2();
                Element element = noteToControl(note);
                if (element == null) return;
                if (listener != null) {
                    if (velocity == 0) {
                        listener.onElementReleased(element);
                    } else {
                        listener.onElementPressed(element, velocity);
                    }
                }
            } else if (command == NOTE_OFF) {
                int note = shortMessage.getData1();
                Element element = noteToControl(note);
                if (element == null) return;
                if (listener != null) {
                    listener.onElementReleased(element);
                }
            }

        }
    }

    public void close() {

    }


    /***** private implementation *****/

    private byte[] padBytes(int index, int red, int green, int blue) {
        byte[] bytes = padMessage.clone();
        bytes[7] = (byte) index;
        bytes[8] = (byte) red;
        bytes[9] = (byte) green;
        bytes [10] = (byte) blue;
        return bytes;
    }

    private Pad noteToPad(int note) {
        int index = note - PADS_START;
        return new Pad(0, index / PAD_COLUMNS, index % PAD_COLUMNS);
    }

    private Element noteToControl(int note) {
        if (note >= PADS_START && note < PADS_START + PAD_ROWS * PAD_COLUMNS) {
            int index = note - PADS_START;
            return new Pad(0, index / PAD_COLUMNS, index % PAD_COLUMNS);
        } else if (note >= BUTTONS_TOP_START && note < BUTTONS_TOP_START + BUTTONS_TOP) {
            return new Button(GROUP_TOP, note - BUTTONS_TOP_START);
        } else if (note >= BUTTONS_BOTTOM_START && note < BUTTONS_BOTTOM_START + BUTTONS_BOTTOM) {
            return new Button(GROUP_BOTTOM, note - BUTTONS_BOTTOM_START);
        } else if (note >= BUTTONS_LEFT_START && note < BUTTONS_LEFT_START + BUTTONS_LEFT) {
            return new Button(GROUP_LEFT, note - BUTTONS_LEFT_START);
        } else if (note >= BUTTONS_RIGHT_START && note < BUTTONS_RIGHT_START + BUTTONS_RIGHT) {
            return new Button(GROUP_RIGHT, note - BUTTONS_RIGHT_START);
        } else if (note >= BUTTONS_OTHER_START && note < BUTTONS_OTHER_START + BUTTONS_OTHER) {
            return new Button(GROUP_OTHER, note - BUTTONS_OTHER_START);
        } else if (note >= KNOBS_TOUCH_START && note < KNOBS_TOUCH_START + KNOBS_COUNT) {
            return new Knob(GROUP_TOP, note - KNOBS_TOUCH_START);
        }
        return null;
    }

}

