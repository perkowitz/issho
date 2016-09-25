package net.perkowitz.issho.util;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

/**
 * Created by optic on 7/8/16.
 */
public class MidiUtil {

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

        // if device not found, print out a list of devices
        for (int i = 0; i < midiDeviceInfos.length; i++) {
            System.out.printf("Found midi device: %s, %s\n", midiDeviceInfos[i].getName(), midiDeviceInfos[i].getDescription());
        }

        return null;
    }

}
