package net.perkowitz.issho.controller.apps.hachi;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.ControllerListener;
import net.perkowitz.issho.controller.midi.MidiSetup;
import net.perkowitz.issho.controller.novation.LaunchpadPro;
import net.perkowitz.issho.controller.yaeltex.YaeltexHachiXL;

import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;


public class Hachi implements HachiListener {

    public static int MAX_ROWS = 8;
    public static int MAX_COLUMNS = 16;
    public static int MAX_MODULES = 8;
    public static int MAX_KNOBS = 8;

    public enum KnobMode {
        MAIN, SHIHAI, MODULE1, MODULE2
    }

    // for managing app state
    private HachiController controller;
    private List<HachiController> controllers = Lists.newArrayList();
    private MidiSetup midiSetup = null;
    private List<Module> modules;

    // clock and timing
    private CountDownLatch stop = new CountDownLatch(1);
    private static Timer timer = null;
    @Getter private boolean clockRunning = false;
    private boolean midiClockRunning = false;
    private int tickCount = 0;
    private int measureCount = 0;
    @Setter private boolean midiContinueAsStart = true;
    private int tempo = 120;
    private int tempoIntervalInMillis = 125 * 120 / tempo;

    // palettes
    private Palette mainPalette = Palette.RED;
    private Palette shihaiPalette = Palette.GREEN;
    private Palette modulePalette = Palette.BLUE;
    private boolean drawModuleSelectInColor = true;

    // modes and selections
    private KnobMode knobMode = KnobMode.MAIN;
    private int selectedModuleIndex = 0;
    private Module selectedModule = null;
    private boolean shihaiSelected = false;


    public static void main(String args[]) throws Exception {
        Hachi hachi = new Hachi();
        hachi.run();
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
                // instantiate launchpad translator
            } else if (c.toString() == YaeltexHachiXL.name()) {
                HachiController t = new YaeltexHachiTranslator((YaeltexHachiXL) c, this);
                controllers.add(t);
                c.setListener((ControllerListener) t);
            }
        }

        // TODO: support more than one controller!!!
        controller = controllers.get(0);

        // load modules
        loadModules();

        initialize();
        Thread.sleep(1000);
        draw();
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

    private void startTimer() {

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (clockRunning) {
                    tick(tickCount % 16 == 0);
                    tickCount++;
                    if (tickCount % 16 == 0) {
                        measureCount++;
                    }

                }
                tempoIntervalInMillis = 125 * 120 / tempo;
                timer.cancel();
                timer = null;
                startTimer();
            }
        }, tempoIntervalInMillis, tempoIntervalInMillis);
    }

    public void tick(boolean andReset) {
        drawClock();
    }

    private void loadModules() {
        modules = Lists.newArrayList();
        modules.add(new MockModule(controller, Palette.BLUE));
        modules.add(new MockModule(controller, Palette.ORANGE));
        modules.add(new MockModule(controller, Palette.YELLOW));
        modules.add(new MockModule(controller, Palette.MAGENTA));
        modules.add(new MockModule(controller, Palette.CYAN));
        modules.add(new MockModule(controller, Palette.PURPLE));
        modules.add(new MockModule(controller, Palette.PINK));
        selectedModuleIndex = 0;
        selectedModule = modules.get(selectedModuleIndex);
    }

    /***** draw *****/

    public void draw() {
        drawMain();
        drawShihai();
        drawModule();
        drawClock();
    }

    public void drawMain() {
        for (int index=0; index < modules.size(); index++) {
            Palette palette = mainPalette;
            if (drawModuleSelectInColor) {
                palette = modules.get(index).getPalette();
            }
            controller.setModuleSelect(index, index == selectedModuleIndex ? mainPalette.On : palette.KeyDim);
            controller.setModuleMute(index, modules.get(index).isMuted() ? mainPalette.Off : palette.KeyDim);
        }
        for (int index=0; index < 4; index++) {
            controller.setMainButton(index, mainPalette.KeyDim);
        }
        if (clockRunning) {
            controller.setMainButton(0, mainPalette.On);
        }
        controller.setKnobModeButton(0, knobMode == KnobMode.MAIN ? mainPalette.Key : mainPalette.KeyDim);
        controller.setKnobModeButton(1, knobMode == KnobMode.SHIHAI ? shihaiPalette.Key : shihaiPalette.KeyDim);
        controller.setKnobModeButton(2, knobMode == KnobMode.MODULE1 ? modulePalette.Key : modulePalette.KeyDim);
        controller.setKnobModeButton(3, knobMode == KnobMode.MODULE2 ? modulePalette.Key : modulePalette.KeyDim);

        Color color = Colors.OFF;
        switch (knobMode) {
            case MAIN:
                color = mainPalette.Key;
                break;
            case SHIHAI:
                color = shihaiPalette.Key;
                break;
            case MODULE1:
            case MODULE2:
                color = selectedModule.isMuted() ? modulePalette.KeyDim : modulePalette.Key;
                break;
        }
        for (int index=0; index < MAX_KNOBS; index++) {
            controller.setKnobColor(index, color);
        }

    }

    public void drawShihai() {
        controller.setShihaiButton(0, shihaiSelected ? shihaiPalette.Key : shihaiPalette.KeyDim);
//        controller.setShihaiButton(0, shihaiPalette.KeyDim);
        controller.setShihaiButton(1, shihaiPalette.KeyDim);
    }

    public void drawModule() {
        selectedModule.draw();
    }

    private void drawClock() {
        controller.showClock(measureCount, tickCount, mainPalette.On, modulePalette.Key, Colors.BLACK);
    }


    /***** HachiListener implementation *****/

    public void onModuleSelectPressed(int index) {
        if (index >= 0 && index < modules.size()) {
            selectedModuleIndex = index;
            selectedModule = modules.get(selectedModuleIndex);
            modulePalette = selectedModule.getPalette();
            draw();
        }
    }

    public void onModuleMutePressed(int index) {
        if (index >= 0 && index < modules.size()) {
            modules.get(index).flipMuted();
            draw();
        }
    }

    public void onMainButtonPressed(int index) {
        switch (index) {
            case 0:
                tickCount = 0;
                measureCount = 0;
                clockRunning = !clockRunning;
                draw();
                break;
            case 2:
                quit();
                break;
        }
    }

    public void onShihaiButtonPressed(int index) {}

    public void onKnobSet(int index, int value) {}

    public void onKnobModePressed(int index) {
        switch (index) {
            case 0:
                knobMode = KnobMode.MAIN;
                break;
            case 1:
                knobMode = KnobMode.SHIHAI;
                break;
            case 2:
                knobMode = KnobMode.MODULE1;
                break;
            case 3:
                knobMode = KnobMode.MODULE2;
                break;
        }
        draw();
    }


}
