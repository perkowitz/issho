package net.perkowitz.issho.devices;

import net.perkowitz.issho.devices.launchpadpro.LaunchpadPro;

import javax.sound.midi.MidiMessage;
import java.util.List;
import java.util.Set;

/**
 * Created by optic on 7/29/17.
 */
public class MultiDevice implements GridDevice {

    private List<GridDevice> gridDevices;

    public MultiDevice(List<GridDevice> gridDevices) {
        this.gridDevices = gridDevices;
    }

    public void setListener(GridListener listener) {
        for (GridDevice gridDevice : gridDevices) {
            gridDevice.setListener(listener);
        }
    }

    public void initialize() {
        for (GridDevice gridDevice : gridDevices) {
            gridDevice.initialize();
        }
    }

    public void initialize(boolean pads, Set<GridButton.Side> buttonSides) {
        for (GridDevice gridDevice : gridDevices) {
            gridDevice.initialize(pads, buttonSides);
        }
    }

    public void setPad(GridPad pad, GridColor color) {
        for (GridDevice gridDevice : gridDevices) {
            gridDevice.setPad(pad, color);
        }
    }

    public void setButton(GridButton button, GridColor color) {
        for (GridDevice gridDevice : gridDevices) {
            gridDevice.setButton(button, color);
        }
    }

    public void setKnob(GridKnob knob, int value) {}


    public void send(MidiMessage message, long timeStamp) {
        for (GridDevice gridDevice : gridDevices) {
            gridDevice.send(message, timeStamp);
        }
    }

    public void close() {
        for (GridDevice gridDevice : gridDevices) {
            gridDevice.close();
        }
    }

}
