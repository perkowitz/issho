package net.perkowitz.issho.util;

import com.google.common.collect.Lists;

import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.List;

public class MultiTransmitter implements Transmitter {

    private List<Transmitter> transmitters;
    private MultiReceiver multiReceiver;

    public MultiTransmitter(List<Transmitter> transmitters) {
        this.transmitters = transmitters;
        List<Receiver> receivers = Lists.newArrayList();
        for (Transmitter t : transmitters) {
            receivers.add(t.getReceiver());
        }
        multiReceiver = new MultiReceiver(receivers);
    }


    @Override
    public void setReceiver(Receiver receiver) {
        for (Transmitter t : transmitters) {
            t.setReceiver(receiver);
        }
    }

    @Override
    public Receiver getReceiver() {
        return multiReceiver;
    }

    @Override
    public void close() {
        for (Transmitter t : transmitters) {
            t.close();
        }
    }
}
