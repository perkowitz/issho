// MidiSetup contains midi devices found in the running environment.

package net.perkowitz.issho.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.perkowitz.issho.controller.novation.LaunchpadPro;
import net.perkowitz.issho.controller.yaeltex.YaeltexHachiXL;

import javax.sound.midi.*;
import java.util.List;
import java.util.Map;

public class MidiSetup {

    // contains known mappings from strings to device types
    private static Map<List<String>, String> deviceNameMap = Maps.newHashMap();
    static {
        deviceNameMap.put(Lists.newArrayList("Launchpad", "Standalone"), LaunchpadPro.name());  // on Mac
        deviceNameMap.put(Lists.newArrayList("Launchpad", "Midi Port"), LaunchpadPro.name());  // on Mac
        deviceNameMap.put(Lists.newArrayList("Launchpad", ",0,2"), LaunchpadPro.name());  // on Raspberry Pi
        deviceNameMap.put(Lists.newArrayList("Hachi"), YaeltexHachiXL.name());
    }

    @Getter private List<Controller> controllers = Lists.newArrayList();
    private List<MidiDevice> openDevices = Lists.newArrayList();


    public MidiSetup() {
        MidiDevice.Info[] midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
//        for (MidiDevice.Info info : midiDeviceInfos) {
//            System.out.printf("Found device: %s, %s\n", info.getName(), info.getDescription());
//        }

        try {
            for (List<String> names : deviceNameMap.keySet()) {
                System.out.printf("Searching for device matching: %s\n", names);

                MidiDevice receiveDevice = null;
                MidiDevice transmitDevice = null;

                for (int i = 0; i < midiDeviceInfos.length; i++) {
                    int matches = 0;
                    for (String name : names) {
                        if (midiDeviceInfos[i].getName().toLowerCase().contains(name.toLowerCase()) ||
                                midiDeviceInfos[i].getDescription().toLowerCase().contains(name.toLowerCase())) {
                            matches++;
                        }
                    }

                    MidiDevice device = MidiSystem.getMidiDevice(midiDeviceInfos[i]);
                    boolean canReceive = device.getMaxReceivers() != 0;
                    boolean canTransmit = device.getMaxTransmitters() != 0;
                    if (matches == names.size() && canReceive) {
                        receiveDevice = device;
                    }
                    if (matches == names.size() && canTransmit) {
                        transmitDevice = device;
                    }
                }

                if (receiveDevice != null && transmitDevice != null) {
                    receiveDevice.open();
                    Receiver receiver = receiveDevice.getReceiver();
                    MidiOut midiOut = new MidiOut(receiver);
                    openDevices.add(receiveDevice);

                    transmitDevice.open();
                    Transmitter transmitter = transmitDevice.getTransmitter();
                    openDevices.add(transmitDevice);

                    if (LaunchpadPro.name().equals(deviceNameMap.get(names))) {
                        LaunchpadPro lpp = new LaunchpadPro(midiOut, null);
                        controllers.add(lpp);
                        transmitter.setReceiver(lpp);
                    } if (YaeltexHachiXL.name().equals(deviceNameMap.get(names))) {
                        YaeltexHachiXL hachi = new YaeltexHachiXL(midiOut, null);
                        controllers.add(hachi);
                        transmitter.setReceiver(hachi);
                    } else {
                        receiveDevice.close();
                        transmitDevice.close();
                    }
                }
            }
        } catch (MidiUnavailableException e) {
            System.err.printf("MIDI not available: %s\n", e);
        }
    }

    public void close() {
        for (MidiDevice device : openDevices) {
            device.close();
        }
    }

}
