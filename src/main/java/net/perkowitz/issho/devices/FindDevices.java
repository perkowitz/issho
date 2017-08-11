package net.perkowitz.issho.devices;

import net.perkowitz.issho.util.MidiUtil;
import org.apache.commons.lang3.StringUtils;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;


public class FindDevices {

    private static MidiDevice input;
    private static MidiDevice output;

    public static void main(String args[]) throws Exception {

        String[] names = new String[] { "NoSuchDevice" };
        input = MidiUtil.findMidiDevice(names, false, true);
        if (input == null) {
            System.err.printf("Unable to find controller input device matching name: %s\n", StringUtils.join(names, ", "));
            System.exit(1);
        }
//        output = MidiUtil.findMidiDevice(names, true, false);
//        if (output == null) {
//            System.err.printf("Unable to find controller output device matching name: %s\n", StringUtils.join(names, ", "));
//            System.exit(1);
//        }

    }

}
