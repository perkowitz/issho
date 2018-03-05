package net.perkowitz.issho.util;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

/**
 * Created by optic on 7/8/16.
 */
public class MidiUtil {

    public static int MIDI_REALTIME_COMMAND = 0xF0;
    public static int MIDI_PITCH_BEND_ZERO = 8192;
    public static int MIDI_PITCH_BEND_MAX = 16383;
    public static int MIDI_PITCH_BEND_MIN = 0;

    public static MidiDevice.Info[] midiDeviceInfos = null;

    public static MidiDevice findMidiDevice(String[] deviceNames, boolean receive, boolean transmit) {

        if (midiDeviceInfos == null) {
            System.out.println("Loading device info..");
            midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
        }

        try {
            for (int i = 0; i < midiDeviceInfos.length; i++) {
                MidiDevice device = MidiSystem.getMidiDevice(midiDeviceInfos[i]);

                boolean canReceive = device.getMaxReceivers() != 0;
                boolean canTransmit = device.getMaxTransmitters() != 0;

                int matches = 0;
                for (String name : deviceNames) {
                    if (midiDeviceInfos[i].getName().toLowerCase().contains(name.toLowerCase()) ||
                            midiDeviceInfos[i].getDescription().toLowerCase().contains(name.toLowerCase())) {
                        matches++;
                    }
                }

                if (matches == deviceNames.length && receive == canReceive && transmit == canTransmit) {
                    return device;
                }

            }
        } catch (MidiUnavailableException e) {
            System.out.printf("MIDI not available: %s\n", e);
        }

        return null;
    }

    public static void printMidiDevices() {
        if (midiDeviceInfos == null) {
            System.out.println("Loading device info..");
            midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
        }

        System.out.println("\nBelow midi device names are available in the OS; the .json config should reference these: ");
        for (int i = 0; i < midiDeviceInfos.length; i++) {
            System.out.printf("midi device name: \"%s\" (vendor=%s, descr=%s)\n",
                    midiDeviceInfos[i].getName(), midiDeviceInfos[i].getVendor(),midiDeviceInfos[i].getDescription());
        }
    }

}
