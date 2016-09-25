package net.perkowitz.sequence;

import javax.sound.midi.MidiSystem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static net.perkowitz.sequence.MidiUtil.midiDeviceInfos;


public class MidiCheck {

    private static String CONTROLLER_NAME_PROPERTY = "controller.name";
    private static String SEQUENCE_NAME_PROPERTY = "output.name";

    private static Properties properties = null;


    public static void main(String args[]) throws Exception {

        System.out.println("Loading device info..");
        midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
            for (int i = 0; i < midiDeviceInfos.length; i++) {
                System.out.printf("Found midi device: %s, %s, %s\n",
                        midiDeviceInfos[i].getName(), midiDeviceInfos[i].getVendor(), midiDeviceInfos[i].getDescription());
            }

    }


    private static Properties getProperties(String filename) throws IOException {

        InputStream inputStream = null;
        try {
            Properties properties = new Properties();

            inputStream = MidiCheck.class.getClassLoader().getResourceAsStream(filename);

            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + filename + "' not found in the classpath");
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
