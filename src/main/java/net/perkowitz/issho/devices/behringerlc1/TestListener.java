package net.perkowitz.issho.devices.behringerlc1;

import com.google.common.collect.Lists;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.List;

/**
 * Created by optic on 7/31/17.
 */
public class TestListener implements GridListener {

    private static int KNOB_COUNT = 16;

    private GridDevice gridDevice;
    private Color padColor;
    private Color buttonColor;

    private List<Integer> knobValues;

    public TestListener(GridDevice gridDevice, Color padColor, Color buttonColor) {
        this.gridDevice = gridDevice;
        this.padColor = padColor;
        this.buttonColor = buttonColor;

        knobValues = Lists.newArrayList();
        for (int index = 0; index < KNOB_COUNT; index++) {
            knobValues.add(8);
        }

        displayKnobs();
    }


    private void displayKnobs() {
        for (int index = 0; index < KNOB_COUNT; index++) {
            gridDevice.setKnob(GridKnob.at(GridKnob.Side.Top, index), knobValues.get(index));
        }
    }

    private void updateKnobValue(int index, int delta) {
        int value = knobValues.get(index);
        value += delta;
        if (value < 1) {
            value = 1;
        } else if (value > 15) {
            value = 15;
        }
        knobValues.set(index, value);
    }

    /***** listener implementation ******************************************/

    public void onPadPressed(GridPad pad, int velocity) {
        gridDevice.setPad(pad, padColor);
    }

    public void onPadReleased(GridPad pad) {}

    public void onButtonPressed(GridButton button, int velocity) {
        gridDevice.setButton(button, buttonColor);
    }

    public void onButtonReleased(GridButton button) {}

    public void onKnobChanged(GridKnob knob, int delta) {
        updateKnobValue(knob.getIndex(), delta);
        gridDevice.setKnob(knob, knobValues.get(knob.getIndex()));
    }

    public void onKnobSet(GridKnob knob, int value) {

    }


}
