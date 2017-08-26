package net.perkowitz.issho.midi;

import javax.sound.midi.MidiMessage;

/**
 * Created by optic on 8/21/17.
 */
public class MidiRealtimeMessage extends MidiMessage {

    private int REALTIME_COMMAND = 0xF0;

    public enum RealtimeType {
        START, STOP, CONTINUE, PULSE
    }


    public MidiRealtimeMessage(byte[] data) {
        super(data);
    }

    public MidiRealtimeMessage clone() {
        return new MidiRealtimeMessage(this.data);
    }


    // static generator
    public static MidiRealtimeMessage create(RealtimeType type) {
        byte[] bytes = new byte[2];
        bytes[0] = 15;
        bytes[1] = 15;
        switch (type) {
            case START:
                bytes[1] = 10;
                break;
            case STOP:
                bytes[1] = 12;
                break;
            case CONTINUE:
                bytes[1] = 11;
                break;
            case PULSE:
                bytes[1] = 8;
                break;
            default:
                bytes[1] = 0;
        }

        return new MidiRealtimeMessage(bytes);
    }



}
