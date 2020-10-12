package net.perkowitz.issho.controller.apps.draw;

import com.google.common.collect.Lists;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.ControllerListener;
import net.perkowitz.issho.controller.midi.MidiSetup;
import net.perkowitz.issho.controller.novation.LaunchpadPro;
import net.perkowitz.issho.controller.yaeltex.YaeltexHachiXL;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static net.perkowitz.issho.controller.Colors.*;
import static net.perkowitz.issho.controller.Colors.BLACK;

public class Draw implements DrawListener {

    // app constants
    public static final int MAX_ROWS = 16;
    public static final int MAX_COLUMNS = 16;

    // various buttons for the UI
    public enum ButtonId {
        QUIT, CLEAR, CURRENT_COLOR
    }

    // colors for the drawing palette
    public static final Color[] palette = new Color[]{
            BLACK, WHITE,
            BRIGHT_RED, BRIGHT_ORANGE, BRIGHT_YELLOW, BRIGHT_GREEN, BRIGHT_BLUE, BRIGHT_PURPLE,
            BRIGHT_CYAN, BRIGHT_MAGENTA, BRIGHT_PINK,
            DARK_GRAY, GRAY,
            DIM_RED, DIM_ORANGE, DIM_YELLOW, DIM_GREEN, DIM_BLUE, DIM_PURPLE,
            DIM_CYAN, DIM_MAGENTA, DIM_PINK};

    // for managing app state
    private Color canvas[][] = new Color[MAX_ROWS][MAX_COLUMNS];
    private Color currentColor = Colors.BLACK;
    private DrawController controller;
    private List<DrawController> controllers = Lists.newArrayList();
    private CountDownLatch stop = new CountDownLatch(1);
    private MidiSetup midiSetup = null;

    public static void main(String args[]) throws Exception {
        Draw draw = new Draw();
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
        for (Controller c : midiSetup.getControllers()) {
            if (c.toString() == LaunchpadPro.name()) {
//                DrawController t = new LaunchpadProPassthruTranslator((LaunchpadPro) c, this);
                DrawController t = new LaunchpadProExpansionTranslator((LaunchpadPro) c, this);
                controllers.add(t);
                c.setListener((ControllerListener) t);
            } else if (c.toString() == YaeltexHachiXL.name()) {
                DrawController t = new YaeltexHachiTranslator((YaeltexHachiXL) c, this);
                controllers.add(t);
                c.setListener((ControllerListener) t);
            }
        }

        controller = new MultiController(controllers);

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
        drawPalette();
        drawButtons();
    }

    private void clearCanvas() {
        for (int r = 0; r < MAX_ROWS; r++) {
            for (int c = 0; c < MAX_COLUMNS; c++) {
                canvas[r][c] = BLACK;
                controller.setCanvas(r, c, BLACK);
            }
        }
    }

    // quit cleans up anything it needs to and exits.
    private void quit() {
        controller.initialize();
        midiSetup.close();
        System.exit(0);
    }

    /***** ControllerListener implementation *****/

    public void onPalettePressed(int index) {
        if (index >= 0 && index < palette.length) {
            currentColor = palette[index];
            controller.setButton(ButtonId.CURRENT_COLOR, currentColor);
        }
    }

    public void onCanvasPressed(int row, int column) {
        controller.setCanvas(row, column, currentColor);
    }

    public void onButtonPressed(Draw.ButtonId buttonId) {
        switch (buttonId) {
            case QUIT:
                quit();
                break;
            case CLEAR:
                clearCanvas();
                break;
        }
    }

    public void drawPalette() {
        for (int i = 0; i < palette.length; i++) {
            controller.setPalette(i, palette[i]);
        }
    }

    public void drawButtons() {
        controller.setButton(ButtonId.QUIT, BRIGHT_RED);
        controller.setButton(ButtonId.CLEAR, DARK_GRAY);
        controller.setButton(ButtonId.CURRENT_COLOR, currentColor);
    }

    // TODO: make this a public class
    private class MultiController implements DrawController {

        private List<DrawController> controllers;

        public MultiController(List<DrawController> controllers) {
            this.controllers = controllers;
        }

        public void initialize() {
            for (DrawController controller : controllers) {
                controller.initialize();
            }
        }
        public void setPalette(int index, Color color) {
            for (DrawController controller : controllers) {
                controller.setPalette(index, color);
            }
        }

        public void setCanvas(int row, int column, Color color) {
            for (DrawController controller : controllers) {
                controller.setCanvas(row, column, color);
            }

        }

        public void setButton(Draw.ButtonId buttonId , Color color) {
            for (DrawController controller : controllers) {
                controller.setButton(buttonId, color);
            }
        }


    }

}
