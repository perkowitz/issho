package net.perkowitz.issho.controller.midi;

import com.google.common.collect.Lists;
import lombok.Setter;
import net.perkowitz.issho.controller.Log;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.List;

import static javax.sound.midi.ShortMessage.*;

public class MidiIn implements Receiver {

    private static final int LOG_LEVEL = Log.OFF;
    private static int MIDI_REALTIME_COMMAND = 0xF0;

    @Setter private boolean midiContinueAsStart = true;
    private List<ChannelListener> channelListeners = Lists.newArrayList();
    private List<ClockListener> clockListeners = Lists.newArrayList();

    // echo midi inputs directly to an output
    @Setter private Receiver echoReceiver = null;
    @Setter private boolean echoMidi = true;
    @Setter private boolean echoMidiRealtime = true;


    public MidiIn() {
    }


    public void addChannelListener(ChannelListener listener) {
        if (!channelListeners.contains(listener)) {
            channelListeners.add(listener);
        }
    }

    public void removeChannelListener(ChannelListener listener) {
        channelListeners.remove(listener);
    }

    public void addClockListener(ClockListener listener) {
        if (!clockListeners.contains(listener)) {
            clockListeners.add(listener);
        }
    }

    public void removeClockListener(ClockListener listener) {
        clockListeners.remove(listener);
    }


    /***** Receiver implementation ***************/

    public void close() {
    }

    public void send(MidiMessage message, long timeStamp) {

        if (message instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage) message;
            int command = shortMessage.getCommand();
            int status = shortMessage.getStatus();

            if (command == MIDI_REALTIME_COMMAND) {
                // echo realtime commands to the midi output
                if (echoReceiver != null && echoMidi && echoMidiRealtime) {
                    echoReceiver.send(message, timeStamp);
                }
                switch (status) {
                    case START:
//                        System.out.printf("MidiIn %s: START\n", this);
                        for (ClockListener listener : clockListeners) {
                            listener.onStart(true);
                        }
                        break;
                    case STOP:
//                        System.out.printf("MidiIn %s: STOP\n", this);
                        for (ClockListener listener : clockListeners) {
                            listener.onStop();
                        }
                        break;
                    case CONTINUE:
//                        System.out.printf("MidiIn %s: CONTINUE\n", this);
                        for (ClockListener listener : clockListeners) {
                            listener.onStart(midiContinueAsStart);
                        }
                        break;
                    case TIMING_CLOCK:
//                        System.out.printf("MidiIn %s: TICK\n", this);
                        for (ClockListener listener : clockListeners) {
                            listener.onTick();
                        }
                        break;
                    default:
                        break;
                }

            } else {
                // echo other commands to the midi output
                if (echoReceiver != null && echoMidi) {
                    echoReceiver.send(message, timeStamp);
                }
                switch (command) {
                    case NOTE_ON:
                        Log.log(this, LOG_LEVEL, "Note On (%d) %d:%d",
                                shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        int velocity = shortMessage.getData2();
                        if (velocity == 0) {
                            for (ChannelListener listener : channelListeners) {
                                listener.onNoteOff(shortMessage.getChannel(), shortMessage.getData1(), 0);
                            }
                        } else {
                            for (ChannelListener listener : channelListeners) {
                                listener.onNoteOn(shortMessage.getChannel(), shortMessage.getData1(), velocity);
                            }
                        }
                        break;
                    case NOTE_OFF:
                        Log.log(this, LOG_LEVEL, "Note Off (%d) %d:%d",
                                shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        for (ChannelListener listener : channelListeners) {
                            listener.onNoteOff(shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        }
                        break;
                    case CONTROL_CHANGE:
                        Log.log(this, LOG_LEVEL, "CC (%d) %d:%d",
                                shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        for (ChannelListener listener : channelListeners) {
                            listener.onCc(shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        }
                        break;
                    default:
                }
            }
        }

    }

}
