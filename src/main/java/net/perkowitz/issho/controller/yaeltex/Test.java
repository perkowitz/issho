// Test runs a simple drawing program to test the Yaeltex controller.
package net.perkowitz.issho.controller.yaeltex;

import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.MidiOut;
import net.perkowitz.issho.controller.MidiSetup;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.Knob;
import net.perkowitz.issho.controller.elements.Pad;

import java.awt.*;

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
        hachi.setColorMap(ColorModes.twoBitMap);
        hachi.initialize();

        for (int i = 0; i < 1; i++) {
            francoTest();
            Thread.sleep(3000);
        }

        System.out.println("Shutting down...");
        hachi.initialize();
        midiSetup.close();
        System.exit(0);
    }

    private static void francoTest() throws Exception {
        int smallDelay = 0;
        int medDelay = 0;
        int bigDelay = 1000;

        hachi.initialize();
        Thread.sleep(bigDelay);

        hachi.setButton(Button.at(YaeltexHachiXL.BUTTONS_LEFT, 0), Colors.WHITE);
        Thread.sleep(medDelay);
        pads(Colors.BRIGHT_BLUE, smallDelay);
        Thread.sleep(medDelay);
        encoders(Colors.BLACK, smallDelay);
        Thread.sleep(bigDelay);

        hachi.setButton(Button.at(YaeltexHachiXL.BUTTONS_LEFT, 1), Colors.WHITE);
        Thread.sleep(medDelay);
        pads(Colors.BLACK, smallDelay);
        Thread.sleep(medDelay);
        encoders(Colors.BLACK, smallDelay);
        Thread.sleep(bigDelay);

        hachi.setButton(Button.at(YaeltexHachiXL.BUTTONS_LEFT, 2), Colors.WHITE);
        Thread.sleep(medDelay);
        pads(Colors.BRIGHT_RED, smallDelay);
        Thread.sleep(medDelay);
        encoders(Colors.BRIGHT_GREEN, smallDelay);
        Thread.sleep(bigDelay);

        hachi.setButton(Button.at(YaeltexHachiXL.BUTTONS_LEFT, 3), Colors.WHITE);
        Thread.sleep(medDelay);
        pads(Colors.BLACK, smallDelay);
        Thread.sleep(medDelay);
        encoders(Colors.BRIGHT_MAGENTA, smallDelay);
    }

    private static void pads(Color color, int delay) throws Exception {
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 16; column++) {
                hachi.setPad(Pad.at(0, row, column), color);
                Thread.sleep(delay);
            }
        }
    }

    private static void encoders(Color color, int delay) throws Exception {
        for (int index = 0; index < 8; index++) {
            hachi.setButton(Button.at(YaeltexHachiXL.KNOB_BUTTONS, index), color);
            Thread.sleep(delay);
        }
    }

    private static void palette() {
        int c = 0;
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 16; column++) {
                hachi.setPad(Pad.at(0, row, column), c);
                c++;
            }
        }
    }

}
