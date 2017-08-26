package net.perkowitz.issho.midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import static javax.sound.midi.ShortMessage.*;

/**
 * Created by optic on 8/21/17.
 */
public class MidiEcho implements Receiver {

    private Receiver receiver;


    public MidiEcho(Receiver receiver) {
        this.receiver = receiver;
        System.out.printf("MidiEcho adding: %s\n", receiver);
    }


    public void close() {
        receiver.close();
    }

    public void send(MidiMessage message, long timeStamp) {

        if (message instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage) message;
            int command = shortMessage.getCommand();
            int status = shortMessage.getStatus();

            MidiRealtimeMessage newMessage = null;
            if (command == 0xF0) {    // midi realtime
                switch (status) {
                    case START:
                        System.out.printf("START: %d %d %d %d\n", shortMessage.getCommand(), shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        newMessage = MidiRealtimeMessage.create(MidiRealtimeMessage.RealtimeType.START);
                        break;
                    case STOP:
                        System.out.printf("STOP: %d %d %d %d\n", shortMessage.getCommand(), shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        newMessage = MidiRealtimeMessage.create(MidiRealtimeMessage.RealtimeType.STOP);
                        break;
                    case CONTINUE:
                        System.out.printf("CONTINUE: %d %d %d %d\n", shortMessage.getCommand(), shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        newMessage = MidiRealtimeMessage.create(MidiRealtimeMessage.RealtimeType.CONTINUE);
                        break;
                    case TIMING_CLOCK:
//                        System.out.printf("CLOCK: %d %d %d %d\n", shortMessage.getCommand(), shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
//                        newMessage = MidiRealtimeMessage.create(MidiRealtimeMessage.RealtimeType.PULSE);
                        break;
                    default:
//                        System.out.printf("REALTIME: %d\n", status);
                        break;
                }

            } else {
                switch (command) {
                    case NOTE_ON:
                        System.out.printf("NOTE ON: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        break;
                    case NOTE_OFF:
                        System.out.printf("NOTE OFF: %d, %d. %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        break;
                    case CONTROL_CHANGE:
                        System.out.printf("MIDI CC: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        break;
                    default:
                        System.out.printf("MSG: %d, %d, %d, %d\n", command, shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                }
            }

            if (newMessage != null) {
                ShortMessage m = new ShortMessage();
                try {
                    m.setMessage(status);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                receiver.send(m, timeStamp);
            }


        }

    }

}
