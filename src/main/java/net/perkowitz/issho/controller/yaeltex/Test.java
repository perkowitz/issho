// Test runs a simple drawing program to test the Yaeltex controller.
package net.perkowitz.issho.controller.yaeltex;

import com.google.common.collect.Maps;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.Log;
import net.perkowitz.issho.controller.midi.DeviceRegistry;
import net.perkowitz.issho.controller.midi.MidiSetup;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.Pad;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by mikep on 7/28/20.
 */
public class Test {

    private static int DELAY = 500;

    private static MidiSetup midiSetup = null;
    private static YaeltexHachiXL hachi = null;
    private static int testNumber = 1;

    private static Map<String, List<List<String>>> deviceNameStrings = Maps.newHashMap();


    public static void main(String args[]) throws Exception {

        // settings
        String controllerName = "hachi";
        if (args.length > 0) {
            controllerName = args[0];
            deviceNameStrings.put(YaeltexHachiXL.name(), Arrays.asList(Arrays.asList(controllerName)));
        }
        System.out.printf("Testing controller \"%s\"\n", controllerName);

        DeviceRegistry registry = DeviceRegistry.withDefaults(DeviceRegistry.fromMap(deviceNameStrings));
        midiSetup = new MidiSetup(registry);
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

        simpleTests2();
        Log.delay(1000);

//        for (int i = 0; i < 1; i++) {
//            francoTest();
//            Log.delay(3000);
//        }
//
        System.out.println("Shutting down...");
        hachi.initialize();
        midiSetup.close();
        System.exit(0);
    }

    private static void simpleTests() throws Exception {

        int initDelay = 3000;

        Color colors[] = {
                Colors.BRIGHT_RED, Colors.BRIGHT_ORANGE, Colors.BRIGHT_YELLOW, Colors.BRIGHT_GREEN,
                Colors.BRIGHT_BLUE, Colors.BRIGHT_MAGENTA, Colors.WHITE, Colors.GRAY
        };

        for (int start=40; start < 2000; start += 1000) {
            System.out.printf("***** start-delay=%d, buttons before\n", start);
            int c = 0;
            for (int delay=0; delay < 20; delay += 5) {
                hachi.initialize();
                Log.delay(initDelay);
                simpleTest(delay, start, colors[c], true);
                c = (c + 1) % colors.length;
                simpleTest(delay, start, colors[c], true);
                c = (c + 1) % colors.length;
            }
            System.out.println("");
            Log.delay(1000);
        }
    }

    private static void simpleTests2() throws Exception {

        int bigDelay = 2000;
        int delay = 0;

        Color colors[] = {
                Colors.BRIGHT_RED, Colors.BRIGHT_ORANGE, Colors.BRIGHT_YELLOW, Colors.BRIGHT_GREEN,
                Colors.BRIGHT_BLUE, Colors.BRIGHT_MAGENTA, Colors.WHITE, Colors.GRAY
        };

        Log.delay(bigDelay);
        System.out.println("Initialize..");
        pads(Colors.BRIGHT_PINK, delay);
        Log.delay(bigDelay);

        buttons(Colors.BRIGHT_BLUE, 0);


        int c = 0;
        for (int after=0; after <= 80; after += 20) {
            System.out.printf("***** after-delay=%d, buttons before\n", after);
//            hachi.initialize();
            Log.delay(bigDelay);
            for (int i = 0; i < 24; i++) {
                Color color = colors[i % colors.length];
                System.out.printf("Test #%d: AfterDelay=%d, Color=%s\n", testNumber, after, color);
                pads(color, delay);
                Log.delay(after);
            }
            pads(colors[c % colors.length], delay);
            Log.delay(after);
            System.out.println("");
            Log.delay(bigDelay);
            c++;
        }
    }

    private static void simpleTest(int delay, int afterDelay, Color color, boolean before) {
        System.out.printf("Test #%d: Delay=%d, AfterDelay=%d, Before=%s, Color=%s\n", testNumber, delay, afterDelay, before, color);
//        if (before) buttons(color, delay);
        pads(color, delay);
//        if (!before) buttons(color, delay);
        Log.delay(afterDelay);
        testNumber++;
    }

    private static void francoTest() {
        int smallDelay = 0;
        int medDelay = 0;
        int bigDelay = 1000;

        hachi.initialize();
        Log.delay(bigDelay);

        hachi.setButton(Button.at(YaeltexHachiXL.BUTTONS_LEFT, 0), Colors.WHITE);
        Log.delay(medDelay);
        pads(Colors.BRIGHT_BLUE, smallDelay);
        Log.delay(medDelay);
        encoders(Colors.BLACK, smallDelay);
        Log.delay(bigDelay);

        hachi.setButton(Button.at(YaeltexHachiXL.BUTTONS_LEFT, 1), Colors.WHITE);
        Log.delay(medDelay);
        pads(Colors.BLACK, smallDelay);
        Log.delay(medDelay);
        encoders(Colors.BLACK, smallDelay);
        Log.delay(bigDelay);

        hachi.setButton(Button.at(YaeltexHachiXL.BUTTONS_LEFT, 2), Colors.WHITE);
        Log.delay(medDelay);
        pads(Colors.BRIGHT_RED, smallDelay);
        Log.delay(medDelay);
        encoders(Colors.BRIGHT_GREEN, smallDelay);
        Log.delay(bigDelay);

        hachi.setButton(Button.at(YaeltexHachiXL.BUTTONS_LEFT, 3), Colors.WHITE);
        Log.delay(medDelay);
        pads(Colors.BLACK, smallDelay);
        Log.delay(medDelay);
        encoders(Colors.BRIGHT_MAGENTA, smallDelay);
    }

    private static void pads(Color color, int delay) {
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 16; column++) {
                hachi.setPad(Pad.at(0, row, column), color);
                Log.delay(delay);
            }
        }
    }

    private static void buttons(int group, int max, Color color, int delay) {
        for (int index = 0; index < max; index++) {
            hachi.setButton(Button.at(group, index), color);
            Log.delay(delay);
        }
    }

    private static void buttons(Color color, int delay) {
        buttons(YaeltexHachiXL.BUTTONS_TOP, YaeltexHachiXL.MAX_BUTTONS, color, delay);
        buttons(YaeltexHachiXL.BUTTONS_BOTTOM, YaeltexHachiXL.MAX_BUTTONS_BOTTOM, color, delay);
        buttons(YaeltexHachiXL.BUTTONS_LEFT, YaeltexHachiXL.MAX_BUTTONS, color, delay);
        buttons(YaeltexHachiXL.BUTTONS_RIGHT, YaeltexHachiXL.MAX_BUTTONS, color, delay);
    }

    private static void encoders(Color color, int delay) {
        for (int index = 0; index < 8; index++) {
            hachi.setButton(Button.at(YaeltexHachiXL.KNOB_BUTTONS, index), color);
            Log.delay(delay);
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
