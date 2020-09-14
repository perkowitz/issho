// Test runs a simple drawing program to test the Yaeltex controller.
package net.perkowitz.issho.controller.yaeltex;

import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.MidiSetup;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.Knob;
import net.perkowitz.issho.controller.elements.Pad;

/**
 * Created by mikep on 7/28/20.
 */
public class Test {

    private static int DELAY = 500;

    private static MidiSetup midiSetup = null;
    private static YaeltexHachiXL hachi = null;


    public static void main(String args[]) throws Exception {

        midiSetup = new MidiSetup();
        for (Controller controller : midiSetup.getControllers()) {
            if (controller.toString().equals(YaeltexHachiXL.name())) {
                hachi = (YaeltexHachiXL)controller;
            }
        }
        if (hachi == null) {
            System.err.println("No YaeltexHachiXL controller found.");
            System.exit(1);
        }

        System.out.println("Initializing controller...");
        hachi.initialize();
        Thread.sleep(DELAY);

        System.out.println("Setting all pads blue...");
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 16; column++) {
                hachi.setPad(Pad.at(0, row, column), Colors.BRIGHT_BLUE);
            }
        }
        Thread.sleep(DELAY);

        System.out.println("Setting all buttons green...");
        for (int group : new int[]{ YaeltexHachiXL.BUTTONS_TOP, YaeltexHachiXL.BUTTONS_LEFT, YaeltexHachiXL.BUTTONS_RIGHT }) {
            for (int index = 0; index < YaeltexHachiXL.MAX_BUTTONS; index++) {
                hachi.setButton(Button.at(group, index), Colors.BRIGHT_GREEN);
            }
        }
        for (int index = 0; index < YaeltexHachiXL.MAX_BUTTONS_BOTTOM; index++) {
            hachi.setButton(Button.at(YaeltexHachiXL.BUTTONS_BOTTOM, index), Colors.BRIGHT_GREEN);
        }
        Thread.sleep(DELAY);

        System.out.println("Setting all knobs to full...");
        for (int index = 0; index < YaeltexHachiXL.MAX_KNOBS; index++) {
            hachi.setKnobValue(Knob.at(YaeltexHachiXL.KNOBS_GROUP, index), 127);
        }
        Thread.sleep(DELAY);

        System.out.println("Setting all pads to off...");
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 16; column++) {
                hachi.setPad(Pad.at(0, row, column), Colors.BLACK);
                Thread.sleep(20);
            }
        }
        hachi.initialize();
        Thread.sleep(DELAY);

        System.out.println("Setting all pads by index...");
        int c = 0;
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 16; column++) {
                hachi.setPad(Pad.at(0, row, column), c);
                c++;
            }
        }
        Thread.sleep(DELAY);

        Thread.sleep(5000);
        hachi.initialize();



        System.exit(0);
    }

}
