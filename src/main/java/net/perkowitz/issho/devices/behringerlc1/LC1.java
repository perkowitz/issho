package net.perkowitz.issho.devices.behringerlc1;

import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.devices.launchpadpro.LaunchpadPro;
import net.perkowitz.issho.util.MidiUtil;
import org.apache.commons.lang3.StringUtils;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import java.util.Timer;
import java.util.concurrent.CountDownLatch;

import static net.perkowitz.issho.devices.GridButton.Side.*;


public class LC1 {

    private static MidiDevice input;
    private static MidiDevice output;
    private static Transmitter transmitter;
    private static Receiver receiver;

    private static CountDownLatch stop = new CountDownLatch(1);
    private Timer timer = null;


    public static void main(String args[]) throws Exception {

        String[] names = new String[] { "CMD LC-1" };
        input = MidiUtil.findMidiDevice(names, false, true);
        if (input == null) {
            System.err.printf("Unable to find controller input device matching name: %s\n", StringUtils.join(names, ", "));
            System.exit(1);
        }
        output = MidiUtil.findMidiDevice(names, true, false);
        if (output == null) {
            System.err.printf("Unable to find controller output device matching name: %s\n", StringUtils.join(names, ", "));
            System.exit(1);
        }

        input.open();
        transmitter = input.getTransmitter();
        output.open();
        receiver = output.getReceiver();


        BehringerLC1 lc1 = new BehringerLC1(receiver, null);
        TestListener listener = new TestListener(lc1, Color.fromIndex(1), Color.fromIndex(0));
        transmitter.setReceiver(lc1);
        lc1.setListener(listener);

        lc1.initialize();

        stop.await();



    }



}
