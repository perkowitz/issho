/**
 * BigDraw is a simple drawing app for Launchpad-like devices.
 * It has three UI elements:
 * - a palette, consisting of 16 colors to choose from
 * - a canvas, a 16x16 grid that can be drawn on
 * - a quit button
 * BigDraw is implemented independently of a controller; translators can
 * be used to implement it on a controller with a smaller grid or fewer buttons.
 */
package net.perkowitz.issho.controller.apps.BigDraw;

import net.perkowitz.issho.controller.*;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.Element;
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
public class BigDraw implements ControllerListener {

    public static final int MAX_ROWS = 16;
    public static final int MAX_COLUMNS = 16;

    // element groups
    public static final int PALETTE_BUTTON_GROUP = 0;
    public static final int CANVAS_PADS_GROUP = 0;

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

    private Color canvas[][] = new Color[MAX_ROWS][MAX_COLUMNS];
    private Color currentColor = Colors.BLACK;
    private Controller controller;

    private CountDownLatch stop = new CountDownLatch(1);

    public static void main(String args[]) throws Exception {
        BigDraw draw = new BigDraw();
        draw.run();
    }


    public void run() throws Exception {

        // find the indicated devices in the system
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

        // open the midi devices and find the transmitter and receiver
        controllerMidiInput.open();
        transmitter = controllerMidiInput.getTransmitter();
        controllerMidiOutput.open();
        receiver = controllerMidiOutput.getReceiver();

        MidiOut midiOut = new MidiOut(receiver);
        LaunchpadPro lpp = new LaunchpadPro(midiOut, null);
        // the translator will take commands from the app and send them to the launchpad
        // the app will listen to events coming from the translator
        // the translator will listen to events coming from the launchpad
        Translator translator = new ExpansionTranslator(lpp, this);
        lpp.setListener(translator);
        transmitter.setReceiver(lpp);
        controller = translator;

        initialize();

        stop.await();

        controllerMidiInput.close();
        controllerMidiOutput.close();

        System.exit(0);

    }

    private void initialize() {
        controller.initialize();
        for (int r = 0; r < MAX_ROWS; r++) {
            for (int c = 0; c < MAX_COLUMNS; c++) {
                canvas[r][c] = BLACK;
                controller.setPad(Pad.at(CANVAS_PADS_GROUP, r, c), BLACK);
            }
        }
        for (int i = 0; i < palette.length; i++) {
            controller.setButton(Button.at(PALETTE_BUTTON_GROUP, i), palette[i]);
        }
    }

    /***** ControllerListener implementation *****/

    public void onElementPressed(Element element, int value) {
        if (element.getType() == Element.Type.PAD) {
            controller.setPad((Pad)element, currentColor);
        } else if (element.getType() == Element.Type.BUTTON) {
            Button button = (Button)element;
            if (button.getGroup() == PALETTE_BUTTON_GROUP) {
                currentColor = palette[button.getIndex()];
            } else if (button.getGroup() == LaunchpadPro.BUTTONS_LEFT && button.getIndex() == 0) {
                controller.initialize();
                System.exit(0);
            }
        }
    }

    public void onElementChanged(Element element, int delta) {
    }

    public void onElementReleased(Element element) {
    }
    
}
