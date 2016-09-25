package net.perkowitz.sequence;

import net.perkowitz.sequence.devices.GridListener;
import net.perkowitz.sequence.devices.launchpad.LaunchpadController;
import net.perkowitz.sequence.devices.launchpad.LaunchpadDisplay;
import net.perkowitz.sequence.devices.launchpadpro.*;
import net.perkowitz.sequence.models.Memory;
import net.perkowitz.sequence.models.Pattern;
import net.perkowitz.sequence.models.Session;
import net.thecodersbreakfast.lp4j.api.Launchpad;
import net.thecodersbreakfast.lp4j.api.LaunchpadClient;
import net.thecodersbreakfast.lp4j.midi.MidiDeviceConfiguration;
import net.thecodersbreakfast.lp4j.midi.MidiLaunchpad;
import org.apache.commons.lang3.StringUtils;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.io.*;
import java.util.Properties;


public class RunSequencer {

    private static String CONTROLLER_NAME_PROPERTY = "controller.name";
    private static String CONTROLLER_TYPE_PROPERTY = "controller.type";
    private static String INPUT_NAME_PROPERTY = "input.name";
    private static String SEQUENCE_NAME_PROPERTY = "output.name";

    private static Properties properties = null;


    public static void main(String args[]) throws Exception {

        String propertyFile = null;
        if (args.length > 0) {
            propertyFile = args[0];
        }

        // load settings
        if (propertyFile == null) {
            System.out.println("Getting app settings..");
            properties = getProperties("sequence.properties");
        } else {
            System.out.printf("Getting app settings from %s..\n", propertyFile);
            properties = getProperties(propertyFile);
        }

        // set all the counts of sessions, patterns, tracks, steps
        System.out.println("Setting memory sizes..");
        Memory.setSessionCount(new Integer((String)properties.get("sessions")));
        Session.setPatternCount(new Integer((String)properties.get("patterns")));
        Pattern.setTrackCount(new Integer((String) properties.get("tracks")));
        net.perkowitz.sequence.models.Track.setStepCount(16);

        // find the controller midi device
        System.out.println("Finding controller device..");
        String[] controllerNames = properties.getProperty(CONTROLLER_NAME_PROPERTY).split("/");
        MidiDevice controllerInput = MidiUtil.findMidiDevice(controllerNames, false, true);
        if (controllerInput == null) {
            System.err.printf("Unable to find controller input device matching name: %s\n", StringUtils.join(controllerNames, ","));
            System.exit(1);
        }
        MidiDevice controllerOutput = MidiUtil.findMidiDevice(controllerNames, true, false);
        if (controllerOutput == null) {
            System.err.printf("Unable to find controller output device matching name: %s\n", StringUtils.join(controllerNames, ","));
            System.exit(1);
        }

        // find the midi device for clock input
        System.out.println("Finding input device..");
        String[] inputNames = properties.getProperty(INPUT_NAME_PROPERTY).split("/");
        MidiDevice midiInput = MidiUtil.findMidiDevice(inputNames, false, true);
        if (midiInput == null) {
            System.err.printf("Unable to find midi input device matching name: %s\n", StringUtils.join(inputNames, ","));
            System.exit(1);
        }

        // find the midi device for sequencer output
        System.out.println("Finding output device..");
        String[] outputNames = properties.getProperty(SEQUENCE_NAME_PROPERTY).split("/");
        MidiDevice midiOutput = MidiUtil.findMidiDevice(outputNames, true, false);
        if (midiOutput == null) {
            System.err.printf("Unable to find midi output device matching name: %s\n", StringUtils.join(outputNames, ","));
            System.exit(1);
        }

        // open the midi i/o
        controllerInput.open();
        Transmitter controllerTransmitter = controllerInput.getTransmitter();
        controllerOutput.open();
        midiInput.open();
        Transmitter inputTransmitter = midiInput.getTransmitter();
        midiOutput.open();
        Receiver outputReceiver = midiOutput.getReceiver();

        String type = properties.getProperty(CONTROLLER_TYPE_PROPERTY);
        SequencerController sequencerController = null;
        SequencerDisplay sequencerDisplay = null;
        if (type.toLowerCase().equals("launchpad")) {
            Launchpad launchpad = new MidiLaunchpad(new MidiDeviceConfiguration(controllerInput, controllerOutput));
            LaunchpadClient launchpadClient = launchpad.getClient();
            sequencerController = new LaunchpadController();
            sequencerDisplay = new LaunchpadDisplay(launchpadClient);
            launchpad.setListener((LaunchpadController)sequencerController);

        } else if (type.toLowerCase().equals("launchpadpro")) {
            sequencerController = new LaunchpadProController();
            LaunchpadPro launchpadPro = new LaunchpadPro(controllerOutput.getReceiver(), (GridListener) sequencerController);
            controllerTransmitter.setReceiver(launchpadPro);
            launchpadPro.initialize();
            launchpadPro.setPads(Sprites.issho, Color.WHITE);
            Thread.sleep(1000);
            launchpadPro.setPads(Sprites.issho, Color.OFF);
            launchpadPro.setPads(Sprites.hachi, Color.LIGHT_BLUE);
            Thread.sleep(500);
            sequencerDisplay = new LaunchpadProDisplay(launchpadPro);

        }

        if (sequencerController != null && sequencerDisplay != null) {
            Sequencer sequencer = new Sequencer(sequencerController, sequencerDisplay, inputTransmitter, outputReceiver);
        } else {
            System.out.printf("Unable to create sequencer, controller=%s, display=%s\n", sequencerController, sequencerDisplay);
        }


    }


    private static Properties getProperties(String filename) throws IOException {

        InputStream inputStream = null;
        try {
            Properties properties = new Properties();

            File file = new File(filename);
            if (file.exists()) {
                inputStream = new FileInputStream(file);
            } else {
                inputStream = RunSequencer.class.getClassLoader().getResourceAsStream(filename);
            }

            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + filename + "' not found in the classpath or path");
            }

            return properties;

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return null;
    }




}
