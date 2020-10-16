// Test runs a simple drawing program to test the Yaeltex controller.
package net.perkowitz.issho.controller.yaeltex;

import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.midi.MidiSetup;
import net.perkowitz.issho.controller.elements.Pad;

/**
 * Created by mikep on 7/28/20.
 */
public class Palette {

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

        hachi.setColorMap(ColorModes.twoBitMap);
        System.out.println("Initialize...");
        hachi.initialize();

        System.out.println("Setting all pads by index...");
        int c = 0;
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 16; column++) {
                hachi.setPad(Pad.at(0, row, column), c);
                c++;
            }
        }
//        hachi.setPad(Pad.at(0,0,0), BLACK);

        midiSetup.close();
        System.exit(0);
    }

}
