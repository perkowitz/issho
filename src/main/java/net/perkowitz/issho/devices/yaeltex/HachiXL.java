package net.perkowitz.issho.devices.yaeltex;


import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Setter;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.devices.launchpadpro.Color;

import javax.sound.midi.*;
import java.util.Map;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;
import static javax.sound.midi.ShortMessage.*;
import static net.perkowitz.issho.devices.GridButton.Side.*;

/**
 * Created by optic on 9/3/16.
 */
public class HachiXL implements GridDevice {

    Logger logger = Logger.getLogger("HachiXL");

    private static int MIDI_REALTIME_COMMAND = 0xF0;

    private static int CHANNEL = 0;

    private static int TOP_BUTTON_START_NOTE = 0;
    private static int BOTTOM_BUTTON_START_NOTE = 58;
    private static int LEFT_BUTTON_START_NOTE = 32;
    private static int RIGHT_BUTTON_START_NOTE = 40;

    private static Map<GridColor, Integer> launchpadToColorIndexMap = Maps.newHashMap();
    static {
        launchpadToColorIndexMap.put(Color.OFF, 0);
        launchpadToColorIndexMap.put(Color.WHITE, 127);
        launchpadToColorIndexMap.put(Color.MED_GRAY, 1);
        launchpadToColorIndexMap.put(Color.LIGHT_GRAY, 2);
        launchpadToColorIndexMap.put(Color.DARK_GRAY, 71);
        launchpadToColorIndexMap.put(Color.BRIGHT_GREEN, 40);
        launchpadToColorIndexMap.put(Color.DIM_GREEN, 44);
        launchpadToColorIndexMap.put(Color.BRIGHT_RED, 1);
        launchpadToColorIndexMap.put(Color.DIM_RED, 2);
        launchpadToColorIndexMap.put(Color.BRIGHT_ORANGE, 13);
        launchpadToColorIndexMap.put(Color.DIM_ORANGE, 14);
        launchpadToColorIndexMap.put(Color.LIGHT_BLUE, 78);
        launchpadToColorIndexMap.put(Color.BRIGHT_BLUE, 73);
        launchpadToColorIndexMap.put(Color.DIM_BLUE, 74);
        launchpadToColorIndexMap.put(Color.DARK_BLUE, 85);
        launchpadToColorIndexMap.put(Color.BRIGHT_BLUE_GREEN, 67);
        launchpadToColorIndexMap.put(Color.DIM_BLUE_GREEN, 69);
        launchpadToColorIndexMap.put(Color.DIM_BLUEGRAY, 90);
        launchpadToColorIndexMap.put(Color.BRIGHT_YELLOW, 22);
        launchpadToColorIndexMap.put(Color.DIM_YELLOW, 24);
        launchpadToColorIndexMap.put(Color.BRIGHT_PINK, 116);
        launchpadToColorIndexMap.put(Color.DIM_PINK, 117);
        launchpadToColorIndexMap.put(Color.BRIGHT_PINK_PURPLE, 106);
        launchpadToColorIndexMap.put(Color.DIM_PINK_PURPLE, 107);
        launchpadToColorIndexMap.put(Color.BRIGHT_PURPLE, 97);
        launchpadToColorIndexMap.put(Color.DIM_PURPLE, 98);
    }


    private Receiver receiver;
    @Setter private GridListener listener;

    public HachiXL(Receiver receiver, GridListener listener) {
        this.receiver = receiver;
        this.listener = listener;
        logger.setLevel(Level.OFF);
        logger.addHandler(new ConsoleHandler());
    }


    /****** public logical implementation ***********************************************************/

    public void initialize(boolean pads, Set<GridButton.Side> buttonSides) {
//        for (int y = 0; y < 8; y++) {
//            if (pads) {
//                for (int x = 0; x < 8; x++) {
//                    setPad(GridPad.at(x, y), Color.OFF);
//                }
//            }
//            if (buttonSides != null) {
//                for (GridButton.Side side : buttonSides) {
//                    setButton(GridButton.at(side, y), Color.OFF);
//                }
//            }
//        }
        for (int note = 0; note < 128; note++) {
            note(CHANNEL, note, 0);
            note(CHANNEL + 1, note, 0);
        }
        for (int cc = 0; cc < 8; cc++) {
            cc(CHANNEL, cc, 0);
        }
    }

    public void initialize() {
        initialize(true, Sets.newHashSet(Top, Bottom, Left, Right));
    }

    public void setPad(GridPad pad, GridColor color) {
        logger.log(Level.INFO, String.format("Haxl: setPad %s, %s", pad, color));
        note(CHANNEL, padToNote(pad), colorLookup(color));
    }

    public void setButton(GridButton button, GridColor color) {
        logger.log(Level.INFO, String.format("Haxl: setButton %s, %s", button, color));
        note(CHANNEL + 1, buttonToNote(button), colorLookup(color));
    }

    public void setKnob(GridKnob knob, int value) {}

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
                            if (shortMessage.getChannel() == CHANNEL) {
                                GridPad pad = noteToPad(shortMessage.getData1());
                                int velocity = shortMessage.getData2();
                                if (velocity == 0) {
                                    listener.onPadReleased(pad);
                                } else {
                                    listener.onPadPressed(pad, velocity);
                                }
                            } else if (shortMessage.getChannel() == CHANNEL + 1) {
                                GridButton button = noteToButton(shortMessage.getData1());
                                int velocity = shortMessage.getData2();
                                if (velocity == 0) {
                                    listener.onButtonReleased(button);
                                } else {
                                    listener.onButtonPressed(button, velocity);
                                }
                            }
                        }
                        break;
                    case NOTE_OFF:
//                        System.out.printf("NOTE OFF: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        if (listener != null) {
                            if (shortMessage.getChannel() == CHANNEL) {
                                GridPad pad = noteToPad(shortMessage.getData1());
                                listener.onPadReleased(pad);
                            } else if (shortMessage.getChannel() == CHANNEL + 1) {
                                GridButton button = noteToButton(shortMessage.getData1());
                                listener.onButtonReleased(button);
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

    private void note(int channel, int noteNumber, int velocity) {

        logger.log(Level.INFO, String.format("Haxl: note ch=%d, n=%d, v=%d", channel, noteNumber, velocity));
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
        int note = pad.getY() * 16 + pad.getX();
        return pad.getY() * 16 + pad.getX();
    }

    private GridPad noteToPad(int note) {
        int x = note % 16;
        int y = note / 16;
        return GridPad.at(x, y);

    }

    private int buttonToNote(GridButton button) {
        switch (button.getSide()) {
            case Top:
//                System.out.printf("Haxl: buttonToNote %s, %d\n", button, button.getIndex() + TOP_BUTTON_START_NOTE);
                return button.getIndex() + TOP_BUTTON_START_NOTE;
            case Bottom:
//                System.out.printf("Haxl: buttonToNote %s, %d\n", button, button.getIndex() + BOTTOM_BUTTON_START_NOTE);
                return button.getIndex() + BOTTOM_BUTTON_START_NOTE;
            case Left:
//                System.out.printf("Haxl: buttonToNote %s, %d\n", button, button.getIndex() + LEFT_BUTTON_START_NOTE);
                return button.getIndex() + LEFT_BUTTON_START_NOTE;
            case Right:
//                System.out.printf("Haxl: buttonToNote %s, %d\n", button, button.getIndex() + RIGHT_BUTTON_START_NOTE);
                return button.getIndex() + RIGHT_BUTTON_START_NOTE;
        }
        return 0;
    }

    private GridButton noteToButton(int note) {
        if (note >= TOP_BUTTON_START_NOTE && note < TOP_BUTTON_START_NOTE + 8) {
            return GridButton.at(Top, note - TOP_BUTTON_START_NOTE);
        } else if (note >= BOTTOM_BUTTON_START_NOTE && note < BOTTOM_BUTTON_START_NOTE + 8) {
            return GridButton.at(Bottom, note - BOTTOM_BUTTON_START_NOTE);
        } else if (note >= LEFT_BUTTON_START_NOTE && note < LEFT_BUTTON_START_NOTE + 8) {
            return GridButton.at(Left, note - LEFT_BUTTON_START_NOTE);
        } else if (note >= RIGHT_BUTTON_START_NOTE && note < RIGHT_BUTTON_START_NOTE + 8) {
            return GridButton.at(Right, note - RIGHT_BUTTON_START_NOTE);
        }
        return GridButton.at(Top, 0);
    }

    private int colorLookup(GridColor color)  {
        Integer index = launchpadToColorIndexMap.get(color);
        return index != null ? index : color.getIndex();
    }

}
