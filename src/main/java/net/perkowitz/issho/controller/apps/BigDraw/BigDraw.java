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

import com.google.common.collect.Lists;
import net.perkowitz.issho.controller.*;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.Element;
import net.perkowitz.issho.controller.elements.ElementSet;
import net.perkowitz.issho.controller.elements.Pad;
import net.perkowitz.issho.controller.novation.LaunchpadPro;
import net.perkowitz.issho.util.MidiUtil;
import org.apache.commons.lang3.StringUtils;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static net.perkowitz.issho.controller.Colors.*;

/**
 * Created by mikep on 7/30/20.
 */
public class BigDraw implements ControllerListener {

    // app constants
    public static final int MAX_ROWS = 16;
    public static final int MAX_COLUMNS = 16;

    // colors for the drawing palette
    public static final Color[] palette = new Color[]{
            BLACK, WHITE,
            BRIGHT_RED, BRIGHT_ORANGE, BRIGHT_YELLOW, BRIGHT_GREEN, BRIGHT_BLUE, BRIGHT_PURPLE,
            DARK_GRAY, GRAY,
            DIM_RED, DIM_ORANGE, DIM_YELLOW, DIM_GREEN, DIM_BLUE, DIM_PURPLE,
            BRIGHT_CYAN, BRIGHT_MAGENTA, BRIGHT_PINK,
            DIM_CYAN, DIM_MAGENTA, DIM_PINK};

    // reference numbers for element groups
    public static final int PALETTE_BUTTONS_GROUP = 0;
    public static final int CANVAS_PADS_GROUP = 0;
    public static final int MISC_BUTTONS_GROUP = 1;

    // declare all elements needed by the app here, and use these for updating the controller
    // then implement translators to map between these elements and those offered by the controller
    public static final ElementSet canvasPads = ElementSet.pads(CANVAS_PADS_GROUP, 0, MAX_ROWS, 0, MAX_COLUMNS);
    public static final ElementSet paletteButtons = ElementSet.buttons(PALETTE_BUTTONS_GROUP, 0, palette.length);
    public static final ElementSet miscButtons = ElementSet.buttons(MISC_BUTTONS_GROUP, 0, 8);
    public static final Button quitButton = Button.at(MISC_BUTTONS_GROUP, 0);
    public static final Button clearButton = Button.at(MISC_BUTTONS_GROUP, 1);
    public static final Button colorButton = Button.at(MISC_BUTTONS_GROUP, 2);

    // for managing app state
    private Color canvas[][] = new Color[MAX_ROWS][MAX_COLUMNS];
    private Color currentColor = Colors.BLACK;
    private Controller controller;
    private List<Controller> controllers = Lists.newArrayList();
    private CountDownLatch stop = new CountDownLatch(1);
    private MidiSetup midiSetup = null;

    public static void main(String args[]) throws Exception {
        BigDraw draw = new BigDraw();
        draw.run();
    }


    public void run() throws Exception {

        // MidiSetup does the work of matching available midi devices against supported controller types
        midiSetup = new MidiSetup();
        if (midiSetup.getControllers().size() == 0) {
            System.err.println("No supported MIDI controllers found.");
            System.exit(1);
        }

        // translators are app-specific, so create those based on controllers found
        for (Controller controller : midiSetup.getControllers()) {
            Translator translator = null;
            if (controller.toString() == LaunchpadPro.name()) {
                translator = new ExpansionTranslator((LaunchpadPro) controller, this);
                controller.setListener(translator);
                controllers.add(translator);
            } else {
                // use no translator; direct connection to controller
                controller.setListener(this);
                controllers.add(controller);
            }
        }

        // TODO: support more than one controller!!!
        controller = controllers.get(0);

        // start up the app
        initialize();

        // just respond to user input
        stop.await();

        // we shouldn't ever get here
        quit();
    }

    // initialize puts the controller in its starting state.
    private void initialize() {
        controller.initialize();
        for (int r = 0; r < MAX_ROWS; r++) {
            for (int c = 0; c < MAX_COLUMNS; c++) {
                canvas[r][c] = BLACK;
                controller.setPad(Pad.at(CANVAS_PADS_GROUP, r, c), BLACK);
            }
        }
        for (int i = 0; i < palette.length; i++) {
            controller.setButton(Button.at(PALETTE_BUTTONS_GROUP, i), palette[i]);
        }
    }

    // quit cleans up anything it needs to and exits.
    private void quit() {
        midiSetup.close();
        System.exit(0);
    }

    /***** ControllerListener implementation *****/

    // onElementPressed responds to user input from the controller.
    public void onElementPressed(Element element, int value) {
        if (canvasPads.contains(element)) {
            // draw at this pad in the current color
            controller.setPad((Pad)element, currentColor);
        } else if (paletteButtons.contains(element)) {
            // set the current color to the pressed palette value
            if (element.getIndex() >= 0 && element.getIndex() < paletteButtons.size()) {
                Button button = (Button) element;
                currentColor = palette[button.getIndex()];
                controller.setButton(colorButton, currentColor);
            }
        } else if (quitButton.equals(element)) {
            quit();
        } else if (clearButton.equals(element)) {
            for (int r = 0; r < MAX_ROWS; r++) {
                for (int c = 0; c < MAX_COLUMNS; c++) {
                    canvas[r][c] = BLACK;
                    controller.setPad(Pad.at(CANVAS_PADS_GROUP, r, c), BLACK);
                }
            }
        }
    }

    public void onElementChanged(Element element, int delta) {
    }

    public void onElementReleased(Element element) {
    }
    
}
