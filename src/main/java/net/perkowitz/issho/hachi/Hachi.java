package net.perkowitz.issho.hachi;

import com.google.common.collect.Lists;
import net.perkowitz.issho.devices.GridDevice;
import net.perkowitz.issho.devices.Keyboard;
import net.perkowitz.issho.devices.launchpad.Launchpad;
import net.perkowitz.issho.devices.launchpadpro.*;
import net.perkowitz.issho.hachi.modules.*;
import net.perkowitz.issho.hachi.modules.deprecated.beatbox.BeatModule;
import net.perkowitz.issho.hachi.modules.deprecated.beatbox.BeatUtil;
import net.perkowitz.issho.hachi.modules.example.ExampleModule;
import net.perkowitz.issho.hachi.modules.deprecated.minibeat.MinibeatModule;
import net.perkowitz.issho.hachi.modules.deprecated.minibeat.MinibeatUtil;
import net.perkowitz.issho.hachi.modules.deprecated.mono.MonoModule;
import net.perkowitz.issho.hachi.modules.deprecated.mono.MonoUtil;
import net.perkowitz.issho.hachi.modules.para.ParaModule;
import net.perkowitz.issho.hachi.modules.para.ParaUtil;
import net.perkowitz.issho.hachi.modules.deprecated.rhythm.RhythmModule;
import net.perkowitz.issho.hachi.modules.deprecated.rhythm.RhythmController;
import net.perkowitz.issho.hachi.modules.deprecated.rhythm.RhythmDisplay;
import net.perkowitz.issho.hachi.modules.seq.SeqModule;
import net.perkowitz.issho.hachi.modules.seq.SeqUtil;
import net.perkowitz.issho.hachi.modules.shihai.ShihaiModule;
import net.perkowitz.issho.hachi.modules.step.StepModule;
import net.perkowitz.issho.hachi.modules.Module;
import net.perkowitz.issho.util.MidiUtil;
import net.perkowitz.issho.util.SettingsUtil;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static net.perkowitz.issho.hachi.modules.seq.SeqUtil.SeqMode.*;

/**
 * Created by optic on 9/19/16.
 */
public class Hachi {

    private static String CONTROLLER_NAME_PROPERTY = "controller.name";
    private static String CONTROLLER_TYPE_PROPERTY = "controller.type";
    private static String MIDI_NAME_PROPERTY = "midi.name";

    private static Properties properties;
    private static Map settings;

    private static MidiDevice controllerInput;
    private static MidiDevice controllerOutput;
    private static Transmitter controllerTransmitter;
    private static Receiver controllerReceiver;

    private static MidiDevice midiInput;
    private static MidiDevice midiOutput;
    private static Transmitter midiTransmitter;
    private static Receiver midiReceiver;

    private static MidiDevice knobInput;
    private static MidiDevice knobOutput;

    private static Keyboard keyboard = null;

    private static HachiController controller;
    private static CountDownLatch stop = new CountDownLatch(1);

    /**
     * 1. get the midi devices
     * 2. open them and create GridListeners attached to them
     * 3. create each module, with its listeners and displays
     * 4. create the HachiController
     * 5. go into wait loop
     * 6. HachiController shuts down when it receives exit
     *
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {

        // settings
        String settingsFile = null;
        if (args.length > 0) {
            settingsFile = args[0];
        }
        if (settingsFile == null) {
            System.out.println("Getting app settings..");
            settings = SettingsUtil.getSettings("hachi-mac.json");
        } else {
            System.out.printf("Getting app settings from %s..\n", settingsFile);
            settings = SettingsUtil.getSettings(settingsFile);
        }

        Map<Object,Object> deviceConfigs = (Map<Object,Object>)settings.get("devices");


        List<GridDevice> gridDevices = getControllers();

        getMidiDevices();

//        GridDevice mainDevice = getGridDevice();
////        GridDevice mirrorDevice = getMirrorGridDevice();
////        GridDevice gridDevice = new MultiDevice(Lists.<GridDevice>newArrayList(mainDevice, mirrorDevice));
//        GridDevice gridDevice = mainDevice;

        System.out.println("Creating modules...");
        Module[] modules;
        if (settings.get("modules") != null) {
            modules = createModules();
        } else {
            modules = defaultModules();
        }


        GridDevice[] gridDevicesArray = new GridDevice[gridDevices.size()];
        for (int i = 0; i < gridDevices.size(); i++) {
            gridDevicesArray[i] = gridDevices.get(i);
        }

        // create the HachiController
        controller = new HachiController(modules, gridDevicesArray, stop);
        Boolean midiContinueAsStart = (Boolean)settings.get("midiContinueAsStart");
        if (midiContinueAsStart != null) {
            controller.setMidiContinueAsStart(midiContinueAsStart);
        }

        // if specified, create a knobby device and make the value control settings
        Knobby knobby = createKnobby();

        // make the HachiController receive external midi
        midiInput.getTransmitter().setReceiver(controller);

        if (deviceConfigs != null) {
            if (deviceConfigs.get("keyboard") != null) {
                List<String> names = (List<String>)((Map<Object,Object>)deviceConfigs.get("keyboard")).get("names");
                System.out.printf("Looking for keyboard: %s...\n", names);
                keyboard = Keyboard.fromMidiDevice(names, controller.getChordReceiver());

                Integer holdClearControllerNumber = (Integer)((Map<Object,Object>)deviceConfigs.get("keyboard")).get("holdClearControllerNumber");
                if (holdClearControllerNumber != null) {
                    controller.getChordReceiver().setHoldClearControllerNumber(holdClearControllerNumber);
                }

                Boolean chordHoldEnabled = (Boolean)((Map<Object,Object>)deviceConfigs.get("keyboard")).get("chordHoldEnabled");
                if (chordHoldEnabled != null) {
                    controller.getChordReceiver().setChordHold(chordHoldEnabled);
                }
            }
        }

        System.out.printf("Running controller...\n");
        controller.run();

        // if we want to be able to send commands to Hachi from command line someday
//        System.out.printf("Starting up command processor...\n");
//        Thread t = new Thread(new CommandLine(controller));
//        t.start();

        System.out.printf("Awaiting...\n");
        stop.await();

        System.out.printf("Exiting...\n");
        System.exit(0);

    }


    private static RhythmModule rhythm(LaunchpadPro launchpadPro, List<Color> palette, String filePrefix) {

        RhythmController rhythmController = new LppRhythmController();
        RhythmDisplay rhythmDisplay = new LppRhythmDisplay(launchpadPro, palette);
        RhythmModule rhythm = new RhythmModule(rhythmController, rhythmDisplay, midiTransmitter, midiReceiver, filePrefix);

        return rhythm;
    }

    private static List<GridDevice> getControllers() {

        Map<Object,Object> deviceConfigs = (Map<Object,Object>)settings.get("devices");
        List<Object> controllerConfigs = (List<Object>)deviceConfigs.get("controllers");

        if (controllerConfigs == null || controllerConfigs.size() == 0) {
            System.err.println("Unable to find config settings for controller devices.");
        }

        List<GridDevice> gridDevices = Lists.newArrayList();

        for (Object controllerConfig : controllerConfigs) {
            Map<Object, Object> config = (Map<Object,Object>)controllerConfig;
            List<String> names = (List<String>)config.get("names");
            String type = (String)config.get("type");
            MidiDevice input = MidiUtil.findMidiDevice(names.toArray(new String[0]), false, true);
            MidiDevice output = MidiUtil.findMidiDevice(names.toArray(new String[0]), true, false);
            if (input == null || output == null) {
                System.err.printf("Unable to find controller device matching name: %s\n", names);
            } else {
                try {
                    input.open();
                    output.open();

                    GridDevice gridDevice = null;
                    if (type == null) {
                        gridDevice = new LaunchpadPro(output.getReceiver(), null);
                    } else if (type.equals("launchpad")) {
                        gridDevice = new Launchpad(output.getReceiver(), null);
                    } else {
                        gridDevice = new LaunchpadPro(output.getReceiver(), null);
                    }
                    input.getTransmitter().setReceiver(gridDevice);

                    gridDevices.add(gridDevice);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }

        return gridDevices;
    }

    private static void getMidiDevices() {

        // get the device configs from the settings
        Map<Object,Object> deviceConfigs = (Map<Object,Object>)settings.get("devices");
        List<String> names = null;

        // find the midi input device
        Map<Object,Object> midiConfig = (Map<Object,Object>)deviceConfigs.get("midiInput");
        if (midiConfig != null) {
            names = (List<String>)midiConfig.get("names");
            midiInput = MidiUtil.findMidiDevice(names.toArray(new String[0]), false, true);
        } else {
            System.err.println("Unable to find config settings for midiInput device.");
        }
        if (midiInput == null) {
            System.err.printf("Unable to find midi-in device matching name: %s\n", names);
            MidiUtil.printMidiDevices();
            System.exit(1);
        }

        // find the midi output device
        midiConfig = (Map<Object,Object>)deviceConfigs.get("midiOutput");
        if (midiConfig != null) {
            names = (List<String>)midiConfig.get("names");
            midiOutput = MidiUtil.findMidiDevice(names.toArray(new String[0]), true, false);
        } else {
            System.err.println("Unable to find config settings for midiOutput device.");
        }
        if (midiOutput == null) {
            System.err.printf("Unable to find midi-out device matching name: %s\n", names);
            MidiUtil.printMidiDevices();
            System.exit(1);
        }

        try {
            midiInput.open();
            midiOutput.open();
            midiTransmitter = midiInput.getTransmitter();
//            midiReceiver = new LoggingMidiReceiver(midiOutput.getReceiver(), Lists.<LoggingMidiReceiver.LogType>newArrayList(LoggingMidiReceiver.LogType.CC));
            midiReceiver = midiOutput.getReceiver();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    private static GridDevice getMirrorGridDevice() {

        // get the device configs from the settings
        Map<Object,Object> deviceConfigs = (Map<Object,Object>)settings.get("devices");
        List<String> names = null;

        MidiDevice controllerInput = null;
        MidiDevice controllerOutput = null;

        // find the controller device
        System.out.println("Finding controller device..");
        Map<Object,Object> controllerConfig = (Map<Object,Object>)deviceConfigs.get("controllerMirror");
        if (controllerConfig != null) {
            names = (List<String>)controllerConfig.get("names");
            controllerInput = MidiUtil.findMidiDevice(names.toArray(new String[0]), false, true);
            controllerOutput = MidiUtil.findMidiDevice(names.toArray(new String[0]), true, false);
        } else {
            System.err.println("Unable to find config settings for controllerMirror device.");
        }
        if (controllerInput == null || controllerOutput == null) {
            System.err.printf("Unable to find controller device matching name: %s\n", names);
            System.exit(1);
        }

        try {
            controllerInput.open();
            controllerOutput.open();

            // assumes controller and midi device are same type
            String type = (String)controllerConfig.get("type");
            GridDevice gridDevice = null;
            if (type.equals("launchpad")) {
                gridDevice = new Launchpad(controllerOutput.getReceiver(), null);
            } else {
                gridDevice = new LaunchpadPro(controllerOutput.getReceiver(), null);
            }
            controllerInput.getTransmitter().setReceiver(gridDevice);
            return gridDevice;

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }

    private static Module[] createModules() {

        ShihaiModule shihaiModule = null;

        List<Module> moduleList = Lists.newArrayList();
        for (Map<Object,Object> moduleSettings : (List<Map<Object,Object>>) settings.get("modules")) {

            String className = (String)moduleSettings.get("class");
            String paletteName = (String)moduleSettings.get("palette");
            String filePrefix = (String)moduleSettings.get("filePrefix");
            if (filePrefix == null) {
                filePrefix = className.toLowerCase() + (moduleList.size() + 1);
            }

            // instantiate module
            Module module = null;
            if (className.equals("RhythmModule")) {
//                List<Color> palette = LppRhythmUtil.PALETTE_BLUE;
//                if (paletteName != null && paletteName.toUpperCase().equals("RED")) {
//                    palette = LppRhythmUtil.PALETTE_RED;
//                }
//                RhythmModule rhythmModule = rhythm(lpp, palette, filePrefix);
//                if (moduleSettings.get("midiNoteOffset") != null) {
//                    Integer offset = (Integer)moduleSettings.get("midiNoteOffset");
//                    if (offset != null) {
//                        rhythmModule.setMidiNoteOffset(offset);
//                    }
//                }
//                module = rhythmModule;

            } else if (className.equals("MonoModule")) {
                List<Color> palette = MonoUtil.PALETTE_FUCHSIA;
                if (paletteName != null && paletteName.toUpperCase().equals("ORANGE")) {
                    palette = MonoUtil.PALETTE_ORANGE;
                }
                module = new MonoModule(midiTransmitter, midiReceiver, palette, filePrefix);

            } else if (className.equals("ParaModule")) {
                Map<Integer, Color> palette = ParaUtil.PALETTE_YELLOW;
                if (paletteName != null && paletteName.toUpperCase().equals("ORANGE")) {
                    palette = ParaUtil.PALETTE_ORANGE;
                } else if (paletteName != null && paletteName.toUpperCase().equals("BLUE")) {
                    palette = ParaUtil.PALETTE_BLUE;
                } else if (paletteName != null && paletteName.toUpperCase().equals("PINK")) {
                    palette = ParaUtil.PALETTE_PINK;
                }
                ParaModule paraModule = new ParaModule(midiTransmitter, midiReceiver, palette, filePrefix);
                if (moduleSettings.get("monophonic") != null) {
                    Boolean monophonic = (Boolean)moduleSettings.get("monophonic");
                    if (monophonic != null) {
                        paraModule.setMonophonic(monophonic);
                    }
                }
                if (moduleSettings.get("controllers") != null) {
                    List<Integer> controllers = (List<Integer>)moduleSettings.get("controllers");
                    Integer[] controllersArray = new Integer[4];
                    paraModule.setControllerNumbers(controllers.toArray(controllersArray));  // jumping thru hoops to get a list as an array
                }
                if (moduleSettings.get("sessionPrograms") != null) {
                    List<Integer> sessionPrograms= (List<Integer>)moduleSettings.get("sessionPrograms");
                    paraModule.setSessionPrograms(sessionPrograms);
                }
                module = paraModule;

            } else if (className.equals("StepModule")) {
                module = new StepModule(midiTransmitter, midiReceiver, filePrefix);

            } else if (className.equals("BeatModule")) {
                Map<Integer, Color> palette = BeatUtil.PALETTE_PINK;
                if (paletteName != null && paletteName.toUpperCase().equals("PINK")) {
                    palette = BeatUtil.PALETTE_PINK;
                } else if (paletteName != null && paletteName.toUpperCase().equals("BLUE")) {
                    palette = BeatUtil.PALETTE_BLUE;
                } else if (paletteName != null && paletteName.toUpperCase().equals("GREEN")) {
                    palette = BeatUtil.PALETTE_GREEN;
                }
                BeatModule beatModule = new BeatModule(midiTransmitter, midiReceiver, palette, filePrefix);
                if (moduleSettings.get("midiNoteOffset") != null) {
                    Integer offset = (Integer)moduleSettings.get("midiNoteOffset");
                    if (offset != null) {
                        beatModule.setMidiNoteOffset(offset);
                    }
                }
                if (moduleSettings.get("tiesEnabled") != null) {
                    Boolean tiesEnabled = (Boolean)moduleSettings.get("tiesEnabled");
                    if (tiesEnabled != null) {
                        beatModule.setTiesEnabled(tiesEnabled);
                    }
                }
                if (moduleSettings.get("sessionPrograms") != null) {
                    List<Integer> sessionPrograms= (List<Integer>)moduleSettings.get("sessionPrograms");
                    beatModule.setSessionPrograms(sessionPrograms);
                }
                module = beatModule;

            } else if (className.equals("SeqModule")) {
                Map<Integer, Color> palette = SeqUtil.getPalette(paletteName.toLowerCase());
                SeqUtil.SeqMode mode = BEAT;
                if (moduleSettings.get("mode") != null) {
                    String m = ((String)moduleSettings.get("mode")).toLowerCase();
                    if (m.equals("mono")) {
                        mode = MONO;
                    } else {
                        mode = BEAT;
                    }
                }
                SeqModule seqModule = new SeqModule(midiTransmitter, midiReceiver, palette, filePrefix, mode);
                if (moduleSettings.get("midiNoteOffset") != null) {
                    Integer offset = (Integer)moduleSettings.get("midiNoteOffset");
                    if (offset != null) {
                        seqModule.setMidiNoteOffset(offset);
                    }
                }
                if (moduleSettings.get("tiesEnabled") != null) {
                    Boolean tiesEnabled = (Boolean)moduleSettings.get("tiesEnabled");
                    if (tiesEnabled != null) {
                        seqModule.setTiesEnabled(tiesEnabled);
                    }
                }
                if (moduleSettings.get("sessionPrograms") != null) {
                    List<Integer> sessionPrograms= (List<Integer>)moduleSettings.get("sessionPrograms");
                    seqModule.setSessionPrograms(sessionPrograms);
                }
                if (moduleSettings.get("controllerNumbers") != null) {
                    List<Integer> controllerNumbers = (List<Integer>) moduleSettings.get("controllerNumbers");
                    seqModule.setControllerNumbers(controllerNumbers);
                }
                module = seqModule;

            } else if (className.equals("MinibeatModule")) {
                Map<Integer, Color> palette = MinibeatUtil.PALETTE_GREEN;
                if (paletteName != null && paletteName.toUpperCase().equals("BLUE")) {
                    palette = MinibeatUtil.PALETTE_BLUE;
                }
                MinibeatModule minibeatModule = new MinibeatModule(midiTransmitter, midiReceiver, palette, filePrefix);
                if (moduleSettings.get("midiNoteOffset") != null) {
                    Integer offset = (Integer)moduleSettings.get("midiNoteOffset");
                    if (offset != null) {
                        minibeatModule.setMidiNoteOffset(offset);
                    }
                }
                module = minibeatModule;

            } else if (className.equals("ShihaiModule")) {
                shihaiModule = new ShihaiModule(midiTransmitter, midiReceiver);
                List<Integer> panicExclude = (List<Integer>)moduleSettings.get("panicExclude");
                if (panicExclude != null) {
                    shihaiModule.setPanicExclude(panicExclude);
                }
                module = shihaiModule;

            } else if (className.equals("DrawingModule")) {
                module = new DrawingModule(filePrefix);

            } else if (className.equals("LogoModule")) {
                Color color = Color.BRIGHT_ORANGE;
                module = new LogoModule(Graphics.hachi, color);

            } else if (className.equals("PaletteModule")) {
                module = new PaletteModule(false);

            } else if (className.equals("MinibeatModule")) {
                module = new ExampleModule(midiTransmitter, midiReceiver, filePrefix);

            }

            // if module was created, add it
            if (module != null) {
                moduleList.add(module);
            }

        }

        Module[] modules = moduleList.toArray(new Module[0]);
        if (shihaiModule != null) {
            shihaiModule.setModules(modules);
        }

        return modules;
    }

    private static Module[] defaultModules() {

        Module[] modules = new Module[6];
        modules[0] = new LogoModule(Graphics.hachi, Color.BRIGHT_ORANGE);
        modules[1] = new PaletteModule(false);
//        modules[2] = new ClockModule();
        modules[2] = new DrawingModule("drawing");
//        modules[3] = rhythm(lpp, LppRhythmUtil.PALETTE_BLUE, "rhythm");
        modules[3] = new MonoModule(midiTransmitter, midiReceiver, MonoUtil.PALETTE_FUCHSIA, "mono1");
        modules[4] = new MonoModule(midiTransmitter, midiReceiver, MonoUtil.PALETTE_ORANGE, "mono2");
//        modules[4] = new KeyboardModule(midiTransmitter, midiReceiver, 10, 36);

        return modules;
    }

    private static Knobby createKnobby() {

        // get the device configs from the settings
        Map<Object,Object> deviceConfigs = (Map<Object,Object>)settings.get("devices");

        // find the knobby device
        System.out.println("Finding knobby device..");
        Map<Object,Object> config = (Map<Object,Object>)deviceConfigs.get("knobby");
        if (config != null) {
            List<String> names = (List<String>)config.get("names");
            knobInput = MidiUtil.findMidiDevice(names.toArray(new String[0]), false, true);
            knobOutput = MidiUtil.findMidiDevice(names.toArray(new String[0]), true, false);
            if (knobInput == null || knobOutput == null) {
                System.out.printf("Unable to find knobby device matching name: %s\n", names);
                return null;
            }

            try {
                knobInput.open();
                knobOutput.open();
                Knobby knobby = new Knobby(knobInput.getTransmitter(), midiReceiver);
                if (config.get("valueControlChannel") != null && config.get("valueControlController") != null) {
                    knobby.setValueControl((Integer)config.get("valueControlChannel"), (Integer)config.get("valueControlController"), controller);
                }
                return knobby;
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        return null;
    }

    private static class CommandLine implements Runnable {

        private HachiController listener;

        public CommandLine(HachiController listener) {
            this.listener = listener;
        }

        public void run() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input = "";
            System.out.print("> ");
            while (true) {
                try {
                    input = br.readLine();
                    listener.processCommand(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }



}
