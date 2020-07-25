package net.perkowitz.issho.controller;

import net.perkowitz.issho.util.MidiUtil;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.concurrent.CountDownLatch;

import static net.perkowitz.issho.controller.Control.Side.*;

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

        ControllerListener listener = new ControllerListenerTest();
        MidiOut midiOut = new MidiOut(midiReceiver);
        AkaiFire fire = new AkaiFire(midiOut, listener);

        Control.Side[] sides = {TOP, BOTTOM, LEFT, RIGHT, OTHER};
        for (Control.Side side : sides) {
            for (int i = 0; i < AkaiFire.buttonCount.get(side); i++) {
                Button button = new Button(side, i);
                fire.setButton(button, Colors.DIM);
            }
        }

//        stop.await();

    }

    private static MidiDevice getMidiDevice(String[] names) {
        return MidiUtil.findMidiDevice(names, true, false);
    }

}
