package net.perkowitz.issho.controller.apps.monitor;

import com.google.common.collect.Sets;
import net.perkowitz.issho.controller.midi.MidiMonitor;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class Monitor {

    private static int MIDI_REALTIME_COMMAND = 0xF0;

    // for managing app state
    private CountDownLatch stop = new CountDownLatch(1);
    private Set<Integer> statusTypes = Sets.newHashSet();
    private Set<Integer> commandTypes = Sets.newHashSet();

    public static void main(String args[]) throws Exception {
        Monitor monitor = new Monitor();
        monitor.run();
    }


    public void run() throws Exception {

        registerAllDevices();

        // just respond to user input
        stop.await();

        quit();
    }

    // quit cleans up anything it needs to and exits.
    private void quit() {
        System.exit(0);
    }

    public void registerAllDevices() {
        try {
            MidiDevice.Info[] midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
            for (MidiDevice.Info midiDeviceInfo : midiDeviceInfos) {
                MidiDevice device = MidiSystem.getMidiDevice(midiDeviceInfo);
                String name = midiDeviceInfo.getName();
                if (device.getMaxTransmitters() != 0) {
                    device.open();
                    Transmitter transmitter = device.getTransmitter();
                    MidiMonitor monitor = new MidiMonitor(name);
//                    monitor.removeStatus(ShortMessage.TIMING_CLOCK);
                    transmitter.setReceiver(monitor);    
                    System.out.printf("Monitoring input device %s\n", name);
                }
            }

        } catch (MidiUnavailableException e) {
            System.err.printf("MIDI not available: %s\n", e);
        }
    }

}
