package net.perkowitz.issho.util;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import java.util.List;

public class MultiReceiver implements Receiver {

    private List<Receiver> receivers;

    public MultiReceiver(List<Receiver> receivers) {
        this.receivers = receivers;
    }



    @Override
    public void send(MidiMessage message, long timeStamp) {
        for (Receiver r : receivers) {
            r.send(message, timeStamp);
        }
    }

    @Override
    public void close() {
        for (Receiver r : receivers) {
            r.close();
        }
    }
}
