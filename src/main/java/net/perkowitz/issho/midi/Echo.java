package net.perkowitz.issho.midi;

import com.google.common.collect.Lists;
import net.perkowitz.issho.util.MidiUtil;

import javax.sound.midi.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class Echo {


    private static MidiDevice input;
    private static MidiDevice output;
    private static CountDownLatch stop = new CountDownLatch(1);


    public static void main(String args[]) throws Exception {

        getMidiDevice();
//        echo();
        sequence();

        stop.await();

    }

    private static void echo() throws Exception {

        MidiEcho echo = new MidiEcho(output.getReceiver());
        input.getTransmitter().setReceiver(echo);

        System.out.printf("Awaiting echo...\n");
    }

    private static void sequence() throws Exception {

        ClockSequence clockSequence = new ClockSequence(output.getReceiver());
        clockSequence.start();

    }

    private static void getMidiDevice() {

        List<String> inputNames = Lists.newArrayList("Launchpad", "MIDI");
        List<String> outputNames = Lists.newArrayList("Launchpad", "MIDI");

        input = MidiUtil.findMidiDevice(inputNames.toArray(new String[0]), false, true);
        if (input == null) {
            System.err.printf("Unable to find midi device matching name: %s\n", inputNames);
            System.exit(1);
        }

        output = MidiUtil.findMidiDevice(outputNames.toArray(new String[0]), true, false);
        if (input == null) {
            System.err.printf("Unable to find midi device matching name: %s\n", outputNames);
            System.exit(1);
        }

        try {
            input.open();
            output.open();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

}
