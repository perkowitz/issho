package net.perkowitz.issho.controller.apps.viz;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.ControllerListener;
import net.perkowitz.issho.controller.MidiSetup;
import net.perkowitz.issho.controller.novation.LaunchpadPro;
import net.perkowitz.issho.controller.yaeltex.YaeltexHachiXL;

import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import static net.perkowitz.issho.controller.Colors.*;

public class Viz implements VizListener {

    public enum Pattern {
        CLEAR, RANDOM
    }

    // various buttons for the UI
    public enum ButtonId {
        START, QUIT, CLEAR
    }

    public static int MAX_ROWS = 16;
    public static int MAX_COLUMNS = 16;

    // for managing app state
    private VizController controller;
    private List<VizController> controllers = Lists.newArrayList();
    private CountDownLatch stop = new CountDownLatch(1);
    private MidiSetup midiSetup = null;
    private Pattern currentPattern = Pattern.CLEAR;
    private int rows = MAX_ROWS;
    private int columns = MAX_COLUMNS;

    // clock and timing
    private static Timer timer = null;
    @Getter private boolean clockRunning = false;
    private boolean midiClockRunning = false;
    private int tickCount = 0;
    @Setter private boolean midiContinueAsStart = true;
    private int tempo = 120;
    private int tempoIntervalInMillis = 125 * 120 / tempo;



    public static void main(String args[]) throws Exception {
        Viz viz = new Viz();
        viz.run();
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
            } else if (c.toString() == YaeltexHachiXL.name()) {
                VizController t = new YaeltexHachiTranslator((YaeltexHachiXL) c, this);
                controllers.add(t);
                c.setListener((ControllerListener) t);
            }
        }

        // TODO: support more than one controller!!!
        controller = controllers.get(0);

        // start up the app
        initialize();
        redraw();
        patternInit();

        startTimer();
        stop.await();
        shutdown();
    }

    // initialize puts the controller in its starting state.
    private void initialize() {
        controller.initialize();
    }

    // quit cleans up anything it needs to and exits.
    private void quit() {
        controller.initialize();
        stop.countDown();
    }

    private void shutdown() {
        timer.cancel();
        controller.close();
        midiSetup.close();
        System.exit(0);
    }

    private void clearCanvas() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                controller.setCanvas(r, c, BLACK);
            }
        }
    }

    public void startTimer() {

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (clockRunning) {
                    tick(tickCount % 16 == 0);
                    tickCount++;
                }
                tempoIntervalInMillis = 125 * 120 / tempo;
                timer.cancel();
                timer = null;
                startTimer();
            }
        }, tempoIntervalInMillis, tempoIntervalInMillis);
    }

    public void tick(boolean andReset) {
        patternTick(andReset);
    }

    /***** draw *****/

    private void redraw() {
        drawButtons();
        drawPatterns();
    }

    public void drawButtons() {
        controller.setButton(Viz.ButtonId.START, clockRunning ? BRIGHT_YELLOW : DARK_GRAY);
        controller.setButton(Viz.ButtonId.QUIT, BRIGHT_RED);
        controller.setButton(Viz.ButtonId.CLEAR, DARK_GRAY);
    }

    public void drawPatterns() {
        controller.setPattern(0, DARK_GRAY);
        controller.setPattern(1, BRIGHT_BLUE);
    }


    /***** Patterns *****/

    private void patternInit() {
        switch (currentPattern) {
            case CLEAR:
                clearCanvas();
                break;
            case RANDOM:
            default:
                break;
        }
    }

    private void patternTick(boolean andReset) {
        switch (currentPattern) {
            case CLEAR:
                break;
            case RANDOM:
                int r = (int)(Math.random() * rows);
                int c = (int)(Math.random() * columns);
                Color color = Colors.randomFromPalette();
                controller.setCanvas(r, c, color);
                break;
            default:
                break;
        }

    }


    /***** VizListener implementation *****/

    public void onCanvasPressed(int row, int column) {}

    public void onPatternPressed(int index) {
        if (index < Pattern.values().length) {
            currentPattern = Pattern.values()[index];
            patternInit();
        }
    }

    public void onButtonPressed(Viz.ButtonId buttonId) {
        switch (buttonId) {
            case START:
                tickCount = 0;
                clockRunning = !clockRunning;
                redraw();
                break;
            case QUIT:
                quit();
                break;
            case CLEAR:
                clearCanvas();
                break;
        }
    }


}
