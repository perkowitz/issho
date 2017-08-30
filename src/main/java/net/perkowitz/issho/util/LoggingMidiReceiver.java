package net.perkowitz.issho.util;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import java.util.List;

import static javax.sound.midi.ShortMessage.*;

/**
 * Created by optic on 8/27/17.
 */
public class LoggingMidiReceiver implements Receiver {

    public enum LogType {
        CLOCK, NOTE, CC
    }

    private Receiver receiver;
    private List<LogType> logTypes;


    public LoggingMidiReceiver(Receiver receiver, List<LogType> logTypes) {
        this.receiver = receiver;
        this.logTypes = logTypes;
    }

    public void close() {
        receiver.close();
    }

    public void send(MidiMessage message, long timestamp) {
        receiver.send(message, timestamp);

        if (message instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage) message;
            int command = shortMessage.getCommand();
            int status = shortMessage.getStatus();

            if (command == MidiUtil.MIDI_REALTIME_COMMAND && (logTypes == null || logTypes.contains(LogType.CLOCK))) {
                switch (status) {
                    case START:
                        System.out.println("START");
                        break;
                    case STOP:
                        System.out.println("STOP");
                        break;
                    case CONTINUE:
                        System.out.println("CONTINUE");
                        break;
                    case TIMING_CLOCK:
//                        System.out.println("TICK");
                        break;
                    default:
//                        System.out.printf("REALTIME: %d\n", status);
                        break;
                }

            } else {
                switch (command) {
                    case NOTE_ON:
                        if (logTypes == null || logTypes.contains(LogType.NOTE)) {
                            System.out.printf("NOTE ON: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        }
                        break;
                    case NOTE_OFF:
                        if (logTypes == null || logTypes.contains(LogType.NOTE)) {
                            System.out.printf("NOTE OFF: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        }
                        break;
                    case CONTROL_CHANGE:
                        if (logTypes == null || logTypes.contains(LogType.CC)) {
                            System.out.printf("MIDI CC: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        }
                        break;
                    default:
//                        System.out.printf("MSG: %d\n", command);
                }
            }
        }

    }

}
