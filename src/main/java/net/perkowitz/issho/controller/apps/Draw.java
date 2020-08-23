package net.perkowitz.issho.controller.apps;

import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.ControllerListener;
import net.perkowitz.issho.controller.MidiOut;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.Knob;
import net.perkowitz.issho.controller.elements.Pad;
import net.perkowitz.issho.controller.novation.LaunchpadPro;
import net.perkowitz.issho.util.MidiUtil;
import org.apache.commons.lang3.StringUtils;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.awt.*;
import java.util.concurrent.CountDownLatch;

import static net.perkowitz.issho.controller.Colors.*;

/**
 * Created by mikep on 7/30/20.
 */
public class Draw implements ControllerListener {

    private Color color = Colors.BLACK;
    private Controller controller;
    private LaunchpadPro lpp;

    public static final Color[] palette = new Color[]{
        BLACK, WHITE,
        BRIGHT_RED, BRIGHT_ORANGE, BRIGHT_YELLOW, BRIGHT_GREEN, BRIGHT_BLUE, BRIGHT_PURPLE,
        DARK_GRAY, GRAY,
        DIM_RED, DIM_ORANGE, DIM_YELLOW, DIM_GREEN, DIM_BLUE, DIM_PURPLE,
        BRIGHT_CYAN, BRIGHT_MAGENTA, BRIGHT_PINK,
        DIM_CYAN, DIM_MAGENTA, DIM_PINK};

    private MidiDevice controllerMidiInput;
    private MidiDevice controllerMidiOutput;
    private Transmitter transmitter;
    private Receiver receiver;

    private CountDownLatch stop = new CountDownLatch(1);

    public static void main(String args[]) throws Exception {
        Draw draw = new Draw();
        draw.run();
    }


    public void run() throws Exception {

        String[] lppNames = new String[] { "Launchpad", "Standalone" };
        controllerMidiInput = MidiUtil.findMidiDevice(lppNames, false, true);
        if (controllerMidiInput == null) {
            System.err.printf("Unable to find controller input device matching name: %s\n", StringUtils.join(lppNames, ", "));
            System.exit(1);
        }
        controllerMidiOutput = MidiUtil.findMidiDevice(lppNames, true, false);
        if (controllerMidiOutput == null) {
            System.err.printf("Unable to find controller output device matching name: %s\n", StringUtils.join(lppNames, ", "));
            System.exit(1);
        }

        controllerMidiInput.open();
        transmitter = controllerMidiInput.getTransmitter();
        controllerMidiOutput.open();
        receiver = controllerMidiOutput.getReceiver();

        MidiOut midiOut = new MidiOut(receiver);
        lpp = new LaunchpadPro(midiOut, this);
        transmitter.setReceiver(lpp);
        controller = lpp;

        initialize();

        stop.await();

        controllerMidiInput.close();
        controllerMidiOutput.close();

        System.exit(0);

    }

    private void initialize() {
        controller.initialize();
        for (int i = 0; i < 7; i++) {
            lpp.setButton(Button.at(LaunchpadPro.BUTTONS_BOTTOM, i), palette[i]);
        }
    }

    /***** ControllerListener implementation ******************************************/

    public void onPadPressed(Pad pad, int velocity) {
        controller.setPad(pad, color);
    }

    public void onPadReleased(Pad pad) {}

    public void onButtonPressed(Button button, int velocity) {
        if (button.getGroup() == LaunchpadPro.BUTTONS_BOTTOM) {
            color = palette[button.getIndex()];
        } else if (button.getGroup() == LaunchpadPro.BUTTONS_LEFT) {
            controller.initialize();
            System.exit(0);
        } else if (button.getGroup() == LaunchpadPro.BUTTONS_RIGHT) {
            for (int i = 0; i < 8; i++) {
                controller.setPad(Pad.at(0, button.getIndex(), i), color);
            }
        } else if (button.getGroup() == LaunchpadPro.BUTTONS_TOP) {
            for (int i = 0; i < 8; i++) {
                controller.setPad(Pad.at(0, i, button.getIndex()), color);
            }
        }
    }

    public void onButtonReleased(Button button) {
    }

    public void onKnobChanged(Knob knob, int delta) {
    }

    public void onKnobSet(Knob knob, int value) {
    }

    public void onKnobTouched(Knob knob) {
    }

    public void onKnobReleased(Knob knob) {
    }

}
