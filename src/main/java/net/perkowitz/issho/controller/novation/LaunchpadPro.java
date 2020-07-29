package net.perkowitz.issho.controller.novation;

import com.google.common.collect.Maps;
import net.perkowitz.issho.controller.ButtonElement;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.ControllerListener;
import net.perkowitz.issho.controller.KnobElement;
import net.perkowitz.issho.controller.LightElement;
import net.perkowitz.issho.controller.MidiOut;
import net.perkowitz.issho.controller.PadElement;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.awt.*;
import java.util.Map;

import static javax.sound.midi.ShortMessage.*;
import static javax.sound.midi.ShortMessage.CONTROL_CHANGE;
import static javax.sound.midi.ShortMessage.NOTE_OFF;


/**
 * Created by mikep on 7/28/20.
 */
public class LaunchpadPro implements Controller, Receiver {

    private static int MIDI_REALTIME_COMMAND = 0xF0;
    private static int CHANNEL = 0;

    // Launchpad buttons appear in 4 groups, one on each side of the device.
    public static final int BUTTONS_TOP = 0;
    public static final int BUTTONS_BOTTOM = 1;
    public static final int BUTTONS_LEFT = 2;
    public static final int BUTTONS_RIGHT = 3;

    // The Launchpad uses an indexed color table. These are constants for common colors.
    private static Map<Color, Integer> colorMap = Maps.newHashMap();
    static {
        colorMap.put(Colors.BLACK, 0);
        colorMap.put(Colors.WHITE, 3);
        colorMap.put(Colors.GRAY, 1);
        colorMap.put(Colors.LIGHT_GRAY, 2);
        colorMap.put(Colors.DARK_GRAY, 71);
        colorMap.put(Colors.BRIGHT_GREEN, 21);
        colorMap.put(Colors.DIM_GREEN, 64);
        colorMap.put(Colors.BRIGHT_RED, 5);
        colorMap.put(Colors.DIM_RED, 7);
        colorMap.put(Colors.BRIGHT_ORANGE, 9);
        colorMap.put(Colors.DIM_ORANGE, 11);
        colorMap.put(Colors.BRIGHT_BLUE, 41);
        colorMap.put(Colors.DIM_BLUE, 43);
        colorMap.put(Colors.BRIGHT_CYAN, 33);
        colorMap.put(Colors.DIM_CYAN, 35);
        colorMap.put(Colors.BRIGHT_YELLOW, 13);
        colorMap.put(Colors.DIM_YELLOW, 15);
        colorMap.put(Colors.BRIGHT_PINK, 57);
        colorMap.put(Colors.DIM_PINK, 59);
        colorMap.put(Colors.BRIGHT_MAGENTA, 53);
        colorMap.put(Colors.DIM_MAGENTA, 55);
        colorMap.put(Colors.BRIGHT_PURPLE, 49);
        colorMap.put(Colors.DIM_PURPLE, 51);
    }

    private MidiOut midiOut;
    private ControllerListener listener;

    public LaunchpadPro (MidiOut midiOut, ControllerListener listener) {
        this.midiOut = midiOut;
        this.listener = listener;
    }


    public void initialize() {
        for (int side = 0; side < 4; side++) {
            for (int index = 0; index < 8; index++) {
                setButton(ButtonElement.at(side, index), Colors.BLACK);
            }
        }

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                setPad(PadElement.at(row, column), Colors.BLACK);
            }
        }
    }

    public void setPad(PadElement pad, Color color) {
        midiOut.note(CHANNEL, padToNote(pad), colorToIndex(color));
    }

    public void setButton(ButtonElement button, Color color) {
        midiOut.cc(CHANNEL, buttonToCc(button), colorToIndex(color));
    }

    public void setKnob(KnobElement knob, Color color) {}
    public void setLight(LightElement light, Color color) {}


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
                            PadElement pad = noteToPad(shortMessage.getData1());
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
                            PadElement pad = noteToPad(shortMessage.getData1());
                            listener.onPadReleased(pad);
                        }
                        break;
                    case CONTROL_CHANGE:
//                        System.out.printf("MIDI CC: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        if (listener != null) {
                            ButtonElement button = ccToButton(shortMessage.getData1());
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
    
    private int colorToIndex(Color color) {
        Integer index = colorMap.get(color);
        if (index == null) {
            return 0;
        }
        return index;
    }

    private int padToNote(PadElement pad) {
        return (7-pad.getRow()) * 10 + pad.getColumn() + 11;
    }

    private PadElement noteToPad(int note) {
        int column = note % 10 - 1;
        int row = 7 - (note / 10 - 1);
        return PadElement.at(row, column);

    }

    private int buttonToCc(ButtonElement button) {

        int index = button.getIndex();
        int flippedIndex = 7 - index;
        switch (button.getGroup()) {
            case BUTTONS_TOP:
                return 90 + index + 1;
            case BUTTONS_BOTTOM:
                return index + 1;
            case BUTTONS_LEFT:
                return 10 + flippedIndex * 10;
            case BUTTONS_RIGHT:
                return 19 + flippedIndex * 10;
            default:
                return 100;
        }

    }

    public static ButtonElement ccToButton(int cc) {

        int group = BUTTONS_TOP;
        int index = 0;

        if (cc >= 10 && cc <= 89) {
            index = 7 - (cc / 10 - 1);
            group = (cc % 10 == 0) ? BUTTONS_LEFT : BUTTONS_RIGHT;
        } else {
            index = cc % 10 - 1;
            group = (cc < 10) ? BUTTONS_BOTTOM : BUTTONS_TOP;
        }

        return ButtonElement.at(group, index);
    }


}
