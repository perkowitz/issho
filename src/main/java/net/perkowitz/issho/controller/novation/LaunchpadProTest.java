// LaunchpadProTest runs a simple drawing program to test the Launchpad controller.
package net.perkowitz.issho.controller.novation;

import lombok.extern.java.Log;
import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.elements.Pad;
import net.perkowitz.issho.controller.midi.MidiOut;
import net.perkowitz.issho.controller.midi.MidiIn;
import net.perkowitz.issho.controller.midi.MidiSetup;
import net.perkowitz.issho.util.MidiUtil;
import org.apache.commons.lang3.StringUtils;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

/**
 * Created by mikep on 7/28/20.
 */
@Log
public class LaunchpadProTest {

    private static MidiDevice lppInput;
    private static MidiDevice lppOutput;
    private static Transmitter transmitter;
    private static Receiver receiver;

    private static CountDownLatch stop = new CountDownLatch(1);
    private Timer timer = null;

    public static void main(String args[]) throws Exception {

        MidiSetup midiSetup = new MidiSetup();
        Controller controller = midiSetup.getController(LaunchpadPro.name());
        if (controller == null) {
            log.log(Level.SEVERE, "No LaunchpadPro found");
            System.exit(1);
        }

        LaunchPadProTestListener listener = new LaunchPadProTestListener();
        controller.setListener(listener);

        controller.initialize();
        for (int i = 0; i < 7; i++) {
            controller.setButton(Button.at(LaunchpadPro.BUTTONS_TOP, i + 1), Colors.rainbow[i]);
            controller.setButton(Button.at(LaunchpadPro.BUTTONS_BOTTOM, i + 1), Colors.rainbow[i]);
            controller.setButton(Button.at(LaunchpadPro.BUTTONS_LEFT, i + 1), Colors.rainbow[i]);
            controller.setButton(Button.at(LaunchpadPro.BUTTONS_RIGHT, i + 1), Colors.rainbow[i]);
        }

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                controller.setPad(Pad.at(LaunchpadPro.PADS_GROUP, r, c), Colors.WHITE);
            }
        }

        stop.await();

        midiSetup.close();
        System.exit(0);


    }

}
