// MidiSetup contains midi devices found in the running environment.

package net.perkowitz.issho.controller.midi;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.extern.java.Log;
import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.novation.LaunchpadPro;
import net.perkowitz.issho.controller.yaeltex.YaeltexHachiXL;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

@Log
public class MidiSetup {

    static { log.setLevel(Level.INFO); }

    @Getter private List<Controller> controllers = Lists.newArrayList();
    @Getter private DeviceRegistry registry;
    private Set<MidiDevice> openDevices = Sets.newHashSet();
    private Set<MidiDevice> clocks = Sets.newHashSet();
    private Map<String, MidiIn> inputs = Maps.newHashMap();
    private Map<String, MidiOut> outputs = Maps.newHashMap();


    public MidiSetup() throws MidiUnavailableException{
        this(DeviceRegistry.withDefaults());
//        this.registry = DeviceRegistry.withDefaults();
//        this.registry.registerNamedDevices();
//        findInputs();
//        findOutputs();
//        createControllers();
    }

    public MidiSetup(DeviceRegistry registry) throws MidiUnavailableException{
        this.registry = registry;
        this.registry.registerNamedDevices();
        findInputs();
        findOutputs();
        createControllers();
    }


    public void close() {
        for (MidiDevice device : openDevices) {
            device.close();
        }
    }

    private void findInputs() throws MidiUnavailableException {
        for (String name : registry.getInputDeviceNames()) {
            MidiDevice device = registry.getInputDevice(name);
            MidiIn input = new MidiIn();
            openDevice(device);
            Transmitter transmitter = device.getTransmitter();
            transmitter.setReceiver(input);
            inputs.put(name, input);
        }
    }

    private void findOutputs() throws MidiUnavailableException {
        for (String name : registry.getOutputDeviceNames()) {
            MidiDevice device = registry.getOutputDevice(name);
            openDevice(device);
            Receiver receiver = device.getReceiver();
            MidiOut output = new MidiOut(receiver);
            outputs.put(name, output);
        }
    }

    private void createControllers() {
        Controller lpp = getController(LaunchpadPro.name());
        if (lpp != null) controllers.add(lpp);
        Controller hachi = getController(YaeltexHachiXL.name());
        if (hachi != null) controllers.add(hachi);
    }

    // getController looks for device types for which there are designed controllers.
    public Controller getController(String name) {
        MidiIn input = inputs.get(name);
        MidiOut output = outputs.get(name);
        if (input != null && output != null) {
            if (name.equals(LaunchpadPro.name())) {
                LaunchpadPro lpp = new LaunchpadPro(output, null);
                input.addChannelListener(lpp);
                return lpp;
            } else if (name.equals(YaeltexHachiXL.name())) {
                YaeltexHachiXL hachi = new YaeltexHachiXL(output, null);
                input.addChannelListener(hachi);
                return hachi;
            }

        }
        return null;
    }
    
    // getMidiIn creates a MidiIn object and connects it to the specified device.
    // Note that this will replace any previously-connected MidiIn.
    public MidiIn getMidiIn(String name) throws MidiUnavailableException {
        MidiDevice device = registry.getInputDevice(name);
        if (device == null) return null;

        MidiIn input = new MidiIn();
        openDevice(device);
        Transmitter transmitter = device.getTransmitter();
        transmitter.setReceiver(input);
        return input;
    }

    // getMidiOut creates a MidiOut object and connects it to the specified device.
    // Note that this will replace any previously-connected MidiOut.
    public MidiOut getMidiOut(String name) throws MidiUnavailableException {
        MidiDevice device = registry.getOutputDevice(name);
        if (device == null) return null;

        MidiOut output = new MidiOut(device.getReceiver());
        return output;
    }


    /***** private implementation *****/

    private void openDevice(MidiDevice device) throws MidiUnavailableException {
        if (!openDevices.contains(device)) {
            device.open();
            openDevices.add(device);
        }
    }

}
