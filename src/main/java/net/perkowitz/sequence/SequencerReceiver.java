package net.perkowitz.sequence;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import static javax.sound.midi.ShortMessage.*;

/**
 * Created by optic on 8/8/16.
 */
public class SequencerReceiver implements Receiver {

    private static int STEP_MIN = 0;
    private static int STEP_MAX = 110;
    private static int RESET_MIN = 111;
    private static int RESET_MAX = 127;
    private static int MIDI_REALTIME_COMMAND = 0xF0;

    private SequencerInterface sequencer;
    private int triggerChannel = 9;//15;
    private int stepNote = 65;//36;


    int tick = 0;

    public SequencerReceiver(SequencerInterface sequencer) {
        this.sequencer = sequencer;
    }

    public void close() {

    }

    public void send(MidiMessage message, long timeStamp) {
//        System.out.printf("MSG (%d, %d): ", message.getLength(), message.getStatus());
//        for (byte b : message.getMessage()) {
//            System.out.printf("%d ", b);
//        }
//        System.out.printf("\n");

        if (message instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage) message;
            int command = shortMessage.getCommand();
            int status = shortMessage.getStatus();

            if (command == MIDI_REALTIME_COMMAND) {
                switch (status) {
                    case START:
//                        System.out.println("START");
                        sequencer.clockStart();
                        break;
                    case STOP:
//                        System.out.println("STOP");
                        sequencer.clockStop();
                        break;
                    case TIMING_CLOCK:
//                        System.out.println("TICK");
                        sequencer.clockTick();
                        break;
                    default:
//                        System.out.printf("REALTIME: %d\n", status);
                        break;
                }


            } else {
                switch (command) {
                    case NOTE_ON:
//                        System.out.printf("NOTE ON: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        if (shortMessage.getChannel() == triggerChannel && shortMessage.getData1() == stepNote &&
                                shortMessage.getData2() >= STEP_MIN && shortMessage.getData2() <= STEP_MAX) {
                            sequencer.trigger(false);
                        } else if (shortMessage.getChannel() == triggerChannel && shortMessage.getData1() == stepNote &&
                                shortMessage.getData2() >= RESET_MIN && shortMessage.getData2() <= RESET_MAX) {
                            sequencer.trigger(true);
                        }
                        break;
                    case NOTE_OFF:
//                        System.out.println("NOTE OFF");
                        break;
                    case CONTROL_CHANGE:
//                        System.out.println("MIDI CC");
                        break;
                    default:
//                        System.out.printf("MSG: %d\n", command);
                }
            }
        }

    }

}
