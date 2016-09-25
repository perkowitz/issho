package net.perkowitz.issho.hachi;

import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.devices.launchpadpro.LaunchpadPro;
import net.perkowitz.issho.devices.launchpadpro.LppRhythmController;
import net.perkowitz.issho.devices.launchpadpro.LppRhythmDisplay;
import net.perkowitz.issho.hachi.modules.*;
import net.perkowitz.issho.hachi.modules.rhythm.RhythmModule;
import net.perkowitz.issho.hachi.modules.rhythm.RhythmController;
import net.perkowitz.issho.hachi.modules.rhythm.RhythmDisplay;
import net.perkowitz.issho.util.MidiUtil;
import net.perkowitz.issho.util.PropertiesUtil;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * Created by optic on 9/19/16.
 */
public class Hachi {

    private static String CONTROLLER_NAME_PROPERTY = "controller.name";
    private static String CONTROLLER_TYPE_PROPERTY = "controller.type";

    private static Properties properties;

    private static MidiDevice controllerInput;
    private static MidiDevice controllerOutput;
    private static Transmitter controllerTransmitter;
    private static Receiver controllerReceiver;

    private static HachiController controller;
    private static CountDownLatch stop = new CountDownLatch(1);
    private static Timer timer = null;


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

        LaunchpadPro launchpadPro = findDevice();
        GridDisplay gridDisplay = launchpadPro;
        if (launchpadPro == null) {
            System.err.printf("Unable to find controller device matching name: %s\n", properties.getProperty(CONTROLLER_NAME_PROPERTY));
            System.exit(1);
//            gridDisplay = new Console();
        }

        Module[] modules = new Module[6];
        modules[0] = new LogoModule(Graphics.hachi, Color.BRIGHT_ORANGE);
        modules[1] = new LogoModule(Graphics.issho, Color.MED_GRAY);
        modules[2] = new PaletteModule(false);
        modules[3] = new PaletteModule(true);
        modules[4] = new ClockModule();
//        modules[5] = new ClockModule();
        modules[5] = rhythm(launchpadPro);

        System.out.println("Creating modules...");
        controller = new HachiController(modules, gridDisplay);
        launchpadPro.setListener(controller);

//        // send each module a random pad press
//        for (int index = 0; index < modules.length; index++) {
//            System.out.printf("Selecting module %d: ", index);
//            System.in.read();
//            controller.onButtonPressed(Button.at(Top, index), 64);
//
//            int x = (int)(Math.random() * 8);
//            int y = (int)(Math.random() * 8);
//            int v = (int)(Math.random() * 127 + 1);
//            Pad pad = Pad.at(x, y);
//            System.out.printf("Pressing pad %s, v=%d: ", pad, v);
//            System.in.read();
//            controller.onPadPressed(pad, v);
//
//            System.out.println();
//        }

        controller.run();

        startTimer();
        stop.await();

    }

    public static void startTimer() {

        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                controller.tick();
            }
        }, 125, 125);
    }

    private static Module rhythm(LaunchpadPro launchpadPro) {

        RhythmController rhythmController = new LppRhythmController();
        RhythmDisplay rhythmDisplay = new LppRhythmDisplay(launchpadPro);
        Module rhythm = new RhythmModule(rhythmController, rhythmDisplay, controllerTransmitter, controllerReceiver);

        return rhythm;
    }



    private static LaunchpadPro findDevice() {

        // find the controller midi device
        System.out.println("Finding controller device..");
        String[] controllerNames = properties.getProperty(CONTROLLER_NAME_PROPERTY).split("/");
        controllerInput = MidiUtil.findMidiDevice(controllerNames, false, true);
        controllerOutput = MidiUtil.findMidiDevice(controllerNames, true, false);
        if (controllerInput == null || controllerOutput == null) {
            return null;
        }

        try {
            String type = properties.getProperty(CONTROLLER_TYPE_PROPERTY);
            if (type.toLowerCase().equals("launchpadpro")) {
                controllerInput.open();
                controllerOutput.open();
                controllerTransmitter = controllerInput.getTransmitter();
                controllerReceiver = controllerOutput.getReceiver();
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


}
