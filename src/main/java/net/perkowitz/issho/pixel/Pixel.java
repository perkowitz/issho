package net.perkowitz.issho.pixel;

import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.launchpadpro.LaunchpadPro;
import net.perkowitz.issho.util.MidiUtil;
import net.perkowitz.issho.util.PropertiesUtil;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * Created by optic on 9/19/16.
 */
public class Pixel {

    private static String CONTROLLER_NAME_PROPERTY = "controller.name";
    private static String CONTROLLER_TYPE_PROPERTY = "controller.type";

    private static Properties properties;

    private static MidiDevice controllerInput;
    private static MidiDevice controllerOutput;
    private static Transmitter controllerTransmitter;
    private static Receiver controllerReceiver;

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
            properties = PropertiesUtil.getProperties("pixel.properties");
        } else {
            System.out.printf("Getting app settings from %s..\n", propertyFile);
            properties = PropertiesUtil.getProperties(propertyFile);
        }

        LaunchpadPro launchpadPro = findLaunchpadPro();
        GridDisplay display = launchpadPro;
        if (launchpadPro == null) {
            System.err.printf("Unable to find controller device matching name: %s\n", properties.getProperty(CONTROLLER_NAME_PROPERTY));
            System.exit(1);
        }

        PixelDevice device = new PixelDevice(display);
        launchpadPro.setListener(device);

//        Pixelator pixelator = new PixelTest(device);
        Pixelator pixelator = new Rainbow(device);
        device.setPixelator(pixelator);
        device.draw();

        System.out.printf("Awaiting...\n");
        stop.await();

    }


    private static LaunchpadPro findLaunchpadPro() {

        // find the controller device
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
