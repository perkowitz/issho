package net.perkowitz.issho.controller.akai;

import net.perkowitz.issho.controller.*;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.midi.MidiOut;
import net.perkowitz.issho.util.MidiUtil;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.concurrent.CountDownLatch;

public class AkaiFireTest {

    private static String[] FireNames = {"fire", "Jack 1"};
    private static CountDownLatch stop = new CountDownLatch(1);

    public static void main(String args[]) throws Exception {

        Transmitter midiTransmitter = null;
        Receiver midiReceiver = null;

        MidiDevice midiDevice = getMidiDevice(FireNames);
        try {
            midiDevice.open();
//            midiTransmitter = midiDevice.getTransmitter();
            midiReceiver = midiDevice.getReceiver();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        ControllerListener listener = new TestListener();
        MidiOut midiOut = new MidiOut(midiReceiver);
        AkaiFire fire = new AkaiFire(midiOut, listener);

        for (int group = 0; group < 4; group++) {
            for (int i = 0; i < AkaiFire.buttonCount.get(group); i++) {
                Button button = new Button(group, i);
                fire.setButton(button, Colors.DIM);
            }
        }

//        stop.await();

    }

    private static MidiDevice getMidiDevice(String[] names) {
        return MidiUtil.findMidiDevice(names, true, false);
    }

}
