// LaunchpadProTest runs a simple drawing program to test the Launchpad controller.
package net.perkowitz.issho.controller.novation;

import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.midi.MidiOut;
import net.perkowitz.issho.controller.midi.MidiIn;
import net.perkowitz.issho.util.MidiUtil;
import org.apache.commons.lang3.StringUtils;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;

/**
 * Created by mikep on 7/28/20.
 */
public class LaunchpadProTest {

    private static MidiDevice lppInput;
    private static MidiDevice lppOutput;
    private static Transmitter transmitter;
    private static Receiver receiver;

    private static CountDownLatch stop = new CountDownLatch(1);
    private Timer timer = null;

    public static void main(String args[]) throws Exception {

        String[] lppNames = new String[] { "Launchpad", "Standalone" };
        lppInput = MidiUtil.findMidiDevice(lppNames, false, true);
        if (lppInput == null) {
            System.err.printf("Unable to find controller input device matching name: %s\n", StringUtils.join(lppNames, ", "));
            System.exit(1);
        }
        lppOutput = MidiUtil.findMidiDevice(lppNames, true, false);
        if (lppOutput == null) {
            System.err.printf("Unable to find controller output device matching name: %s\n", StringUtils.join(lppNames, ", "));
            System.exit(1);
        }

        lppInput.open();
        transmitter = lppInput.getTransmitter();
        lppOutput.open();
        receiver = lppOutput.getReceiver();

        MidiOut midiOut = new MidiOut(receiver);
        LaunchPadProTestListener listener = new LaunchPadProTestListener();
        LaunchpadPro lpp = new LaunchpadPro(midiOut, listener);

        MidiIn midiIn = new MidiIn();
        Transmitter transmitter = lppInput.getTransmitter();
        transmitter.setReceiver(midiIn);
        midiIn.addChannelListener(lpp);
        listener.setController(lpp);

        lpp.initialize();

        for (int i = 0; i < 7; i++) {
            lpp.setButton(Button.at(LaunchpadPro.BUTTONS_BOTTOM, i + 1), Colors.rainbow[i]);
        }

        stop.await();

        lppInput.close();
        lppOutput.close();

        System.exit(0);
    }

}
