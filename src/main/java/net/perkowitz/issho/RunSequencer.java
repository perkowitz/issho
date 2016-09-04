package net.perkowitz.issho;

import net.perkowitz.issho.launchpad.LaunchpadController;
import net.perkowitz.issho.launchpad.LaunchpadDisplay;
import net.perkowitz.issho.models.Memory;
import net.perkowitz.issho.models.Pattern;
import net.perkowitz.issho.models.Session;
import net.thecodersbreakfast.lp4j.api.Launchpad;
import net.thecodersbreakfast.lp4j.api.LaunchpadClient;
import net.thecodersbreakfast.lp4j.midi.MidiDeviceConfiguration;
import net.thecodersbreakfast.lp4j.midi.MidiLaunchpad;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import java.io.*;
import java.util.Properties;


public class RunSequencer {

    private static String CONTROLLER_NAME_PROPERTY = "controller.name";
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
        net.perkowitz.issho.models.Track.setStepCount(16);

        // find the controller midi device
        System.out.println("Finding controller device..");
        String[] controllerNames = properties.getProperty(CONTROLLER_NAME_PROPERTY).split(",");
        MidiDevice controllerInput = MidiUtil.findMidiDevice(controllerNames, false, true);
        if (controllerInput == null) {
            System.err.printf("Unable to find controller input device matching name: %s\n", controllerNames);
            System.exit(1);
        }
        MidiDevice controllerOutput = MidiUtil.findMidiDevice(controllerNames, true, false);
        if (controllerOutput == null) {
            System.err.printf("Unable to find controller output device matching name: %s\n", controllerNames);
            System.exit(1);
        }

        // find the midi device for midi input
        System.out.println("Finding input device..");
        String[] inputNames = properties.getProperty(INPUT_NAME_PROPERTY).split(",");
        MidiDevice midiInput = MidiUtil.findMidiDevice(inputNames, false, true);
        if (midiInput == null) {
            System.err.printf("Unable to find issho output device matching name: %s\n", inputNames);
            System.exit(1);
        }

        // find the midi device for sequencer output
        System.out.println("Finding output device..");
        String[] outputNames = properties.getProperty(SEQUENCE_NAME_PROPERTY).split(",");
        MidiDevice sequenceOutput = MidiUtil.findMidiDevice(outputNames, true, false);
        if (sequenceOutput == null) {
            System.err.printf("Unable to find issho output device matching name: %s\n", outputNames);
            System.exit(1);
        }

        try {

            Launchpad launchpad = new MidiLaunchpad(new MidiDeviceConfiguration(controllerInput, controllerOutput));
            LaunchpadClient launchpadClient = launchpad.getClient();
            SequencerDisplay launchpadDisplay = new LaunchpadDisplay(launchpadClient);
            LaunchpadController launchpadController = new LaunchpadController();
            launchpad.setListener(launchpadController);

            Sequencer sequencer = new Sequencer(launchpadController, launchpadDisplay, midiInput, sequenceOutput);

        } catch (MidiUnavailableException e) {
            System.err.printf("%s\n", e.getStackTrace().toString());
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
