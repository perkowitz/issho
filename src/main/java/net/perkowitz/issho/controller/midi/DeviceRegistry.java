package net.perkowitz.issho.controller.midi;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Setter;
import lombok.extern.java.Log;
import net.perkowitz.issho.controller.novation.LaunchpadPro;
import net.perkowitz.issho.controller.yaeltex.YaeltexHachiXL;

import javax.sound.midi.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/** DeviceRegistry
 *
 * Used for mapping the name and description fields found in javax.sound.midi.MidiDevice objects
 * to the devices. Useful because the strings may differ from one OS to another.
 *
 */
@Log
public class DeviceRegistry {

    static { log.setLevel(Level.INFO); }

    // contains default mappings from strings to device type names
    private static Map<List<String>, String> defaultNameMap = Maps.newHashMap();
    static {
        defaultNameMap.put(Lists.newArrayList("Launchpad", "Standalone"), LaunchpadPro.name());  // on Mac
//        defaultNameMap.put(Lists.newArrayList("Launchpad", "Midi Port"), LaunchpadPro.name());  // on Mac
//        defaultNameMap.put(Lists.newArrayList("Launchpad", ",0,2"), LaunchpadPro.name());  // on Raspberry Pi
        defaultNameMap.put(Lists.newArrayList("Hachi"), YaeltexHachiXL.name());
    }

    @Setter private Map<List<String>, String> deviceNameMap = Maps.newHashMap();
    private Map<String, MidiDevice> inputDevices = Maps.newHashMap();
    private Map<String, MidiDevice> outputDevices = Maps.newHashMap();

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

    // registerDevices finds all MIDI devices that match a registry entry.
    public void registerDevices() {
        MidiDevice.Info[] midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : midiDeviceInfos) {
//            log.info(String.format("Found device: %s, %s", info.getName(), info.getDescription()));
        }

        try {
            for (List<String> names : deviceNameMap.keySet()) {
                log.info(String.format("Searching for device matching: %s", names));
                for (int i = 0; i < midiDeviceInfos.length; i++) {
                    int matches = 0;
                    for (String name : names) {
                        if (midiDeviceInfos[i].getName().toLowerCase().contains(name.toLowerCase()) ||
                                midiDeviceInfos[i].getDescription().toLowerCase().contains(name.toLowerCase())) {
                            matches++;
                        }
                    }

                    String name = deviceNameMap.get(names);
                    MidiDevice device = MidiSystem.getMidiDevice(midiDeviceInfos[i]);
                    // an INPUT has a transmitter so you can get stuff from it
                    boolean isInput = device.getMaxTransmitters() != 0;
                    // an OUTPUT has a receiver so you can send to it
                    boolean isOutput = device.getMaxReceivers() != 0;
                    if (matches == names.size()) {
                        log.info("Found device " + name);
                        addDevice(name, device, isInput, isOutput);
                    }
                }
            }

        } catch (MidiUnavailableException e) {
            System.err.printf("MIDI not available: %s\n", e);
        }

    }
    

    /***** static methods *****/

    // fromMap returns a registry containing the entries in the map, without defaults.
    public static DeviceRegistry fromMap(Map<List<String>, String> deviceNameMap) {
        DeviceRegistry r = new DeviceRegistry();
        r.deviceNameMap = deviceNameMap;
        return r;
    }

    // withDefaults() returns a registry containing only the default values.
    public static DeviceRegistry withDefaults() {
        DeviceRegistry r = new DeviceRegistry();
        r.deviceNameMap = new HashMap<List<String>, String>(defaultNameMap);
        return r;
    }

    // withDefaults(r) returns a registry containing both the entries in r and the defaults.
    // Note that duplicate keys in r will override the defaults.
    // TODO: this might not be true depending on the Lists.equal() function
    public static DeviceRegistry withDefaults(DeviceRegistry registry) {
        DeviceRegistry r = withDefaults();
        for (Map.Entry<List<String>, String> e : r.deviceNameMap.entrySet()) {
            r.deviceNameMap.put(e.getKey(), e.getValue());
        }
        return r;
    }

}
