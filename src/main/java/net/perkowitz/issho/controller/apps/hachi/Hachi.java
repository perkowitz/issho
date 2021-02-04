package net.perkowitz.issho.controller.apps.hachi;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.controller.*;
import net.perkowitz.issho.controller.apps.hachi.modules.MockModule;
import net.perkowitz.issho.controller.apps.hachi.modules.Module;
import net.perkowitz.issho.controller.apps.hachi.modules.step.StepModule;
import net.perkowitz.issho.controller.midi.*;
import net.perkowitz.issho.controller.novation.LaunchpadPro;
import net.perkowitz.issho.controller.yaeltex.YaeltexHachiXL;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CountDownLatch;


public class Hachi implements HachiListener, ClockListener {

    private static final int LOG_LEVEL = Log.OFF;
    private static final int LOG_PERF = Log.OFF;

    public static int MAX_ROWS = 8;
    public static int MAX_COLUMNS = 16;
    public static int MAX_MODULES = 8;
    public static int MAX_KNOBS = 8;

    private static int PULSE_PER_BEAT = 24;
    private static int BEAT_PER_MEASURE = 4;

    public enum KnobMode {
        MAIN, SHIHAI, MODULE1, MODULE2
    }

    // for managing app state
    // for managing app state
    private HachiController controller;
    private List<HachiController> controllers = Lists.newArrayList();
    private MidiSetup midiSetup = null;
    private List<Module> modules;
    private List<ModuleTranslator> moduleTranslators;
    private MidiOut midiOut;
    private List<MidiIn> midiIns;

    // clock and timing
    private CountDownLatch stop = new CountDownLatch(1);
    private static Timer timer = null;
    @Getter private boolean clockRunning = false;
    private boolean midiClockRunning = false;
    private int tickCount = 0;
    private int measureCount = 0;
    private int beatCount = 0;
    private int pulseCount = 0;
    private int pulseDelta = 6; // currently internal clock only, which just counts 16th notes
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

        Config config = new Config("h2-dev.json", "h2-registry.json");

        // MidiSetup does the work of matching available midi devices against supported controller types
        // Any devices you want to open should be listed in the registry json file
        DeviceRegistry registry = DeviceRegistry.withDefaults(DeviceRegistry.fromMap(config.getDeviceNameStrings()));
        midiSetup = new MidiSetup(registry);
        if (midiSetup.getControllers().size() == 0) {
            System.err.println("No supported MIDI controllers found.");
            System.exit(1);
        }

        midiIns = config.getMidiIns(midiSetup, this);
        List<MidiOut> midiOuts = config.getMidiOuts(midiSetup);
        if (midiOuts.size() == 0) {
            System.err.println("No MIDI output found.");
//            System.exit(1);
        } else {
            // TODO: support multiple midi outs
            midiOut = midiOuts.get(0);
        }

        // translators are app-specific, so create those based on controllers found
        for (Controller c : midiSetup.getControllers()) {
            if (c.toString() == LaunchpadPro.name()) {
                // instantiate launchpad translator
            } else if (c.toString() == YaeltexHachiXL.name()) {
                HachiController t = new YaeltexHachiTranslator((YaeltexHachiXL) c, this);
                controllers.add(t);
                c.setListener((ControllerListener) t);
                MidiIn input = midiSetup.getMidiIn(YaeltexHachiXL.name());
                if (input != null) {
                    Log.log(this, Log.ALWAYS, "Found MIDI input for %s: %s", YaeltexHachiXL.name(), input);
                    input.addClockListener(this);
                } else {
                    Log.log(this, Log.WARNING, "Unable to find MIDI input for %s", YaeltexHachiXL.name());
                }
            }
        }

        // TODO: support more than one controller!!!
        controller = controllers.get(0);

        loadModules(config, controller, midiOut);

        initialize();
        draw();
        startTimer();
        Log.delay(200);
        onModuleSelectPressed(0);
        stop.await();
        quit();
    }

    // initialize puts the controller in its starting state.
    private void initialize() {
        controller.initialize();
    }

    // quit cleans up anything it needs to and exits.
    private void quit() {
        controller.initialize();
        if (midiOut != null) {
            midiOut.allNotesOff();
        }
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
                    advance(pulseDelta);
                }
                tempoIntervalInMillis = 125 * 120 / tempo;
                timer.cancel();
                timer = null;
                startTimer();
            }
        }, tempoIntervalInMillis, tempoIntervalInMillis);
    }

    private void loadModules(Config config, HachiController controller, MidiOut midiOut) {

        config.loadModules(controller, midiOut);
        modules = config.getModules();
        moduleTranslators = config.getModuleTranslators();

        selectedModuleIndex = 0;
        selectedModule = modules.get(selectedModuleIndex);
        moduleTranslators.get(selectedModuleIndex).setEnabled(true);
    }

    private void advance(int pulseDelta) {
        // calculating and displaying times to track clock correctness/stability
        String clock = String.format("%02d:%02d:%02d", measureCount, beatCount, pulseCount);
        if (pulseCount == 0) {
            if (beatCount == 0) {
                Log.resetStopWatch(0);
                Log.memory(this, LOG_PERF, "");
                Log.log(this, LOG_PERF, "Measure -----");
            }
            Log.resetStopWatch(1);
        }
        boolean pulseBeat = pulseCount % 6 == 0;
        if (pulseBeat) {
            Log.resetStopWatch(2);
            Log.log(this, LOG_PERF, "ADV %s - %s", clock, Log.stopWatchTimes());
        }

        // actual work of advancing the modules
        for (Module module : modules) {
            module.onClock(measureCount, beatCount, pulseCount);
        }
//        drawClock();
//        Log.log(this, LOG_PERF, "ADV %s - %s (post-modules)", clock, Log.stopWatchTimes());
        pulseCount = (pulseCount + pulseDelta) % PULSE_PER_BEAT;
        if (pulseCount == 0) {
            beatCount = (beatCount + 1) % BEAT_PER_MEASURE;
            if (beatCount == 0) {
                measureCount++;
            }
        }
//        Log.log(this, LOG_PERF, "ADV %s - %s (done)", clock, Log.stopWatchTimes());
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
            drawModuleSelectAndMute(index);
        }
        for (int index=0; index < 4; index++) {
            controller.setMainButton(index, mainPalette.KeyDim);
        }
        if (clockRunning || midiClockRunning) {
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

    private void drawModuleSelectAndMute(int index) {
        Palette palette = mainPalette;
        if (drawModuleSelectInColor) {
            palette = modules.get(index).getPalette();
        }
        controller.setModuleSelect(index, index == selectedModuleIndex ? mainPalette.On : palette.KeyDim);
        controller.setModuleMute(index, modules.get(index).isMuted() ? mainPalette.Off : palette.KeyDim);
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
        controller.showClock(measureCount, beatCount, pulseCount, mainPalette.On, modulePalette.Key, Colors.BLACK);
    }


    /***** HachiListener implementation *****/

    public void onModuleSelectPressed(int index) {
        if (index >= 0 && index < modules.size()) {
            moduleTranslators.get(selectedModuleIndex).setEnabled(false);
            selectedModuleIndex = index;
            selectedModule = modules.get(selectedModuleIndex);
            modulePalette = selectedModule.getPalette();
            moduleTranslators.get(selectedModuleIndex).setEnabled(true);
            draw();
        }
    }

    public void onModuleMutePressed(int index) {
        if (index >= 0 && index < modules.size()) {
            modules.get(index).flipMuted();
            drawModuleSelectAndMute(index);
            if (index == selectedModuleIndex) {
                drawModule();
            }
        }
    }

    public void onMainButtonPressed(int index) {
        Log.log(this, LOG_LEVEL, "%d", index);
        switch (index) {
            case 0:
                measureCount = 0;
                beatCount = 0;
                pulseCount = 0;
                clockRunning = !clockRunning;
                if (!clockRunning && midiOut != null) {
                    midiOut.allNotesOff();
                }
                drawMain();
                controller.setMainButton(index, mainPalette.On);
                break;
            case 2:
                controller.setMainButton(index, mainPalette.On);
                quit();
                break;
            case 3:
                controller.setMainButton(index, mainPalette.On);
                midiOut.allNotesOff();
                break;
        }
    }

    public void onMainButtonReleased(int index) {
        controller.setMainButton(index, mainPalette.KeyDim);
    }

    public void onModuleButtonPressed(int group, int index, int value) {
        Log.log(this, LOG_LEVEL, "%d:%d %d", group, index, value);
        selectedModule.onButtonPressed(group, index, value);
    }

    public void onModuleButtonReleased(int group, int index) {
        Log.log(this, LOG_LEVEL, "%d:%d", group, index);
        selectedModule.onButtonReleased(group, index);
    }

    public void onModulePadPressed(int row, int column, int value) {
        Log.log(this, LOG_LEVEL, "%d:%d %d", row, column, value);
        selectedModule.onPadPressed(row, column, value);
    }

    public void onModulePadReleased(int row, int column) {
        Log.log(this, LOG_LEVEL, "%d:%d", row, column);
        selectedModule.onPadReleased(row, column);
    }


    public void onShihaiButtonPressed(int index) {
        Log.log(this, LOG_LEVEL, "%d", index);
    }

    public void onKnobSet(int index, int value) {
        Log.log(this, LOG_LEVEL, "%d %d", index, value);
        switch (knobMode) {
            case MAIN:
                break;
            case SHIHAI:
                break;
            case MODULE1:
            case MODULE2:
                // TODO work out how to deal with the knob mode switching
                selectedModule.onKnob(index, value);
        }
    }

    public void onKnobModePressed(int index) {
        Log.log(this, LOG_LEVEL, "%d", index);
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


    /***** ClockListener implementation *****/

    public void onStart(boolean restart) {
        measureCount = beatCount = pulseCount = 0;
        Log.log(this, LOG_LEVEL, "onStart %02d:%02d:%02d", measureCount, beatCount, pulseCount);
        Log.resetAllStopWatches();
        for (Module module : modules) {
            module.onStart(restart);
        }
        midiClockRunning = true;
        drawMain();
    }

    public void onStop() {
        midiClockRunning = false;
        drawMain();
        for (Module module : modules) {
            module.onStop();
        }
//        Log.gc(this, Log.INFO);
    }

    public void onTick() {
        if (midiClockRunning) {
            Log.log(this, LOG_PERF, "onTick %02d:%02d:%02d", measureCount, beatCount, pulseCount);
            advance(1);
        }
    }

    public void onClock(int measure, int beat, int pulse) { }

}
