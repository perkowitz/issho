// Test runs a simple drawing program to test the Yaeltex controller.
package net.perkowitz.issho.controller.yaeltex;

import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.midi.MidiSetup;

/**
 * Created by mikep on 7/28/20.
 */
public class Initialize {

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

        hachi.initialize();
        System.exit(0);
    }

}
