package net.perkowitz.issho.hachi;

import com.google.common.collect.Lists;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.launchpadpro.*;
import net.perkowitz.issho.hachi.modules.*;
import net.perkowitz.issho.hachi.modules.mono.MonoModule;
import net.perkowitz.issho.hachi.modules.mono.MonoUtil;
import net.perkowitz.issho.hachi.modules.rhythm.RhythmModule;
import net.perkowitz.issho.hachi.modules.rhythm.RhythmController;
import net.perkowitz.issho.hachi.modules.rhythm.RhythmDisplay;
import net.perkowitz.issho.util.MidiUtil;
import net.perkowitz.issho.util.PropertiesUtil;
import net.perkowitz.issho.util.SettingsUtil;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

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

        String propertyFile = null;
        if (args.length > 0) {
            propertyFile = args[0];
        }
        // load settings
        if (propertyFile == null) {
            System.out.println("Getting app settings..");
            properties = PropertiesUtil.getProperties("hachi.properties");
        } else {
            System.out.printf("Getting app settings from %s..\n", propertyFile);
            properties = PropertiesUtil.getProperties(propertyFile);
        }

        String settingsFile = null;
        if (args.length > 1) {
            settingsFile = args[1];
        }
        if (settingsFile == null) {
            System.out.println("Getting app settings..");
            settings = SettingsUtil.getSettings("settings.json");
        } else {
            System.out.printf("Getting app settings from %s..\n", propertyFile);
            settings = SettingsUtil.getSettings(settingsFile);
        }


        LaunchpadPro launchpadPro = findDevice();
        GridDisplay gridDisplay = launchpadPro;
        if (launchpadPro == null) {
            System.err.printf("Unable to find controller device matching name: %s\n", properties.getProperty(CONTROLLER_NAME_PROPERTY));
            System.exit(1);
//            gridDisplay = new Console();
        }

        Module[] modules;
        if (settings.get("modules") != null) {
            modules = createModules(launchpadPro);
        } else {
            modules = defaultModules(launchpadPro);
        }

        System.out.println("Creating modules...");
        controller = new HachiController(modules, gridDisplay);
        launchpadPro.setListener(controller);

        // make the HachiController receive external midi
        midiInput.getTransmitter().setReceiver(controller);

        System.out.printf("Running controller...\n");
        controller.run();

        System.out.printf("Awaiting...\n");
        stop.await();

    }


    private static Module rhythm(LaunchpadPro launchpadPro, List<Color> palette, String filePrefix) {

        RhythmController rhythmController = new LppRhythmController();
        RhythmDisplay rhythmDisplay = new LppRhythmDisplay(launchpadPro, palette);
        Module rhythm = new RhythmModule(rhythmController, rhythmDisplay, midiTransmitter, midiReceiver, filePrefix);

        return rhythm;
    }



    private static LaunchpadPro findDevice() {

        // find the controller device
        System.out.println("Finding controller device..");
        String[] controllerNames = properties.getProperty(CONTROLLER_NAME_PROPERTY).split("/");
        controllerInput = MidiUtil.findMidiDevice(controllerNames, false, true);
        controllerOutput = MidiUtil.findMidiDevice(controllerNames, true, false);
        if (controllerInput == null || controllerOutput == null) {
            return null;
        }

        // find the midi device
        System.out.println("Finding midi device..");
        String[] midiNames = properties.getProperty(MIDI_NAME_PROPERTY).split("/");
        midiInput = MidiUtil.findMidiDevice(midiNames, false, true);
        midiOutput = MidiUtil.findMidiDevice(midiNames, true, false);
        if (midiInput == null || midiOutput == null) {
            return null;
        }

        try {
            String type = properties.getProperty(CONTROLLER_TYPE_PROPERTY);
            if (type.toLowerCase().equals("launchpadpro")) {

                controllerInput.open();
                controllerOutput.open();
                controllerTransmitter = controllerInput.getTransmitter();
                controllerReceiver = controllerOutput.getReceiver();

                midiInput.open();
                midiOutput.open();
                midiTransmitter = midiInput.getTransmitter();
                midiReceiver = midiOutput.getReceiver();

                LaunchpadPro launchpadPro = new LaunchpadPro(controllerOutput.getReceiver(), null);
                controllerInput.getTransmitter().setReceiver(launchpadPro);
                return launchpadPro;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }

    private static Module[] createModules(LaunchpadPro lpp) {

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
                List<Color> palette = LppRhythmUtil.PALETTE_BLUE;
                if (paletteName != null && paletteName.toUpperCase().equals("RED")) {
                    palette = LppRhythmUtil.PALETTE_RED;
                }
                module = rhythm(lpp, palette, filePrefix);

            } else if (className.equals("MonoModule")) {
                List<Color> palette = MonoUtil.PALETTE_FUCHSIA;
                if (paletteName != null && paletteName.toUpperCase().equals("ORANGE")) {
                    palette = MonoUtil.PALETTE_ORANGE;
                }
                module = new MonoModule(midiTransmitter, midiReceiver, palette, filePrefix);

            } else if (className.equals("DrawingModule")) {
                module = new DrawingModule(filePrefix);

            } else if (className.equals("LogoModule")) {
                Color color = Color.BRIGHT_ORANGE;
                module = new LogoModule(Graphics.hachi, color);

            } else if (className.equals("PaletteModule")) {
                module = new PaletteModule(false);

            }

            // if module was created, add it
            if (module != null) {
                moduleList.add(module);
            }

        }

        return moduleList.toArray(new Module[0]);
    }

    private static Module[] defaultModules(LaunchpadPro lpp) {

        Module[] modules = new Module[6];
        modules[0] = new LogoModule(Graphics.hachi, Color.BRIGHT_ORANGE);
        modules[1] = new PaletteModule(false);
//        modules[2] = new ClockModule();
        modules[2] = new DrawingModule("drawing");
        modules[3] = rhythm(lpp, LppRhythmUtil.PALETTE_BLUE, "rhythm");
//        modules[3] = new DrawingModule();
        modules[4] = new MonoModule(midiTransmitter, midiReceiver, MonoUtil.PALETTE_FUCHSIA, "mono1");
        modules[5] = new MonoModule(midiTransmitter, midiReceiver, MonoUtil.PALETTE_ORANGE, "mono2");
//        modules[4] = new KeyboardModule(midiTransmitter, midiReceiver, 10, 36);

        return modules;
    }

}
