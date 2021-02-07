package net.perkowitz.issho.controller.midi;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.java.Log;
import net.perkowitz.issho.controller.novation.LaunchpadPro;
import net.perkowitz.issho.controller.yaeltex.YaeltexHachiXL;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.util.*;
import java.util.logging.Level;

/** DeviceRegistry
 *
 * Used for mapping the name and description fields found in javax.sound.midi.MidiDevice objects
 * to the devices. Useful because the strings may differ from one OS to another.
 *
 */
@Log
public class DeviceRegistry {

    static { log.setLevel(Level.OFF); }

    // name strings for supported hachi controllers
    private static Map<String, List<List<String>>> defaultNameStrings = Maps.newHashMap();
    static {
        defaultNameStrings.put(LaunchpadPro.name(),
                Arrays.asList(
                        Arrays.asList("Launchpad", "Standalone"),
                        Arrays.asList("Launchpad", ",0,2")
                ));
        defaultNameStrings.put(YaeltexHachiXL.name(),
                Arrays.asList(
                        Arrays.asList("Hachi")
                ));
        defaultNameStrings.put("LaunchpadPro_MidiOut",
                Arrays.asList(
                        Arrays.asList("Launchpad", "Midi Port")
                ));
    }

    private Map<String, List<List<String>>> nameStrings = Maps.newHashMap();
    @Getter private Map<String, MidiDevice> inputDevices = Maps.newHashMap();
    @Getter private Map<String, MidiDevice> outputDevices = Maps.newHashMap();

    // DeviceRegistry empty constructor should just be used for deserialization.
    public DeviceRegistry() {}


    public void addDevice(String name, MidiDevice device, boolean isInput, boolean isOutput) {
        if (isInput) {
            inputDevices.put(name, device);
        }
        if (isOutput) {
            outputDevices.put(name, device);
        }
    }

    public MidiDevice getInputDevice(String name) {
        return inputDevices.get(name);
    }

    public Set<String> getInputDeviceNames() {
        return inputDevices.keySet();
    }

    public Set<String> getOutputDeviceNames() {
        return outputDevices.keySet();
    }

    public MidiDevice getOutputDevice(String name) {
        return outputDevices.get(name);
    }

    public void registerNamedDevices() {
        try {
            MidiDevice.Info[] midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
            for (String name : nameStrings.keySet()) {
                List<List<String>> matchLists = nameStrings.get(name);
                log.info(String.format("Searching for %s device", name));
                boolean found = false;
                int i = 0;
                while (i < midiDeviceInfos.length && !found) {
                    int j = 0;
                    while (j < matchLists.size() && !found) {
                        List<String> matchStrings = matchLists.get(j);
                        int matches = 0;
                        for (String m : matchStrings) {
                            m = m.toLowerCase();
                            if (midiDeviceInfos[i].getName().toLowerCase().contains(m) ||
                                    midiDeviceInfos[i].getDescription().toLowerCase().contains(m)) {
                                matches++;
                            }
                        }

                        if (matches == matchStrings.size()) {
                            MidiDevice device = MidiSystem.getMidiDevice(midiDeviceInfos[i]);
                            log.info(String.format("Found device %s: %s", name, device));
                            // an INPUT has a transmitter so you can get stuff from it
                            boolean isInput = device.getMaxTransmitters() != 0;
                            // an OUTPUT has a receiver so you can send to it
                            boolean isOutput = device.getMaxReceivers() != 0;
                            addDevice(name, device, isInput, isOutput);
//                            found = true;
                        }
                        j++;
                    }
                    i++;
                }
            }


        } catch (MidiUnavailableException e) {
            System.err.printf("MIDI not available: %s\n", e);
        }
    }

    public void registerAllDevices() {
        try {
            MidiDevice.Info[] midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
            int i = 0;
            for (MidiDevice.Info midiDeviceInfo : midiDeviceInfos) {
                MidiDevice device = MidiSystem.getMidiDevice(midiDeviceInfos[i]);
                String name = midiDeviceInfo.getName();
                System.out.printf("Found device %s\n", name);
                // an INPUT has a transmitter so you can get stuff from it
                boolean isInput = device.getMaxTransmitters() != 0;
                // an OUTPUT has a receiver so you can send to it
                boolean isOutput = device.getMaxReceivers() != 0;
                addDevice(name, device, isInput, isOutput);
            }

        } catch (MidiUnavailableException e) {
            System.err.printf("MIDI not available: %s\n", e);
        }
    }


    /***** static methods *****/

    // fromMap returns a registry containing the entries in the map, without defaults.
    public static DeviceRegistry fromMap(Map<String, List<List<String>>> nameStrings) {
        DeviceRegistry r = new DeviceRegistry();
        r.nameStrings = nameStrings;
        return r;
    }

    // withDefaults() returns a registry containing only the default values.
    public static DeviceRegistry withDefaults() {
        DeviceRegistry r = new DeviceRegistry();
        r.nameStrings = new HashMap<String, List<List<String>>>(defaultNameStrings);
        return r;
    }

    // withDefaults(r) returns a registry containing both the entries in r and the defaults.
    // Note that duplicate keys in r will override the defaults.
    public static DeviceRegistry withDefaults(DeviceRegistry registry) {
        DeviceRegistry r = withDefaults();
        for (Map.Entry<String, List<List<String>>> e : registry.nameStrings.entrySet()) {
            r.nameStrings.put(e.getKey(), e.getValue());
        }
        return r;
    }

}
