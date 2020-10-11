package net.perkowitz.issho.controller.midi;

import com.google.common.collect.Lists;
import lombok.Setter;
import lombok.extern.java.Log;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.List;
import java.util.logging.Level;

import static javax.sound.midi.ShortMessage.*;

@Log
public class MidiIn implements Receiver {

    static { log.setLevel(Level.OFF); }

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

    /***** Receiver implementation ***************/

    public void close() {
    }

    public void send(MidiMessage message, long timeStamp) {

        log.info(String.format("send: %s", message));
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
                        for (ClockListener listener : clockListeners) {
                            listener.onStart(true);
                        }
                        break;
                    case STOP:
                        for (ClockListener listener : clockListeners) {
                            listener.onStop();
                        }
                        break;
                    case CONTINUE:
                        for (ClockListener listener : clockListeners) {
                            listener.onStart(midiContinueAsStart);
                        }
                        break;
                    case TIMING_CLOCK:
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
                        for (ChannelListener listener : channelListeners) {
                            listener.onNoteOff(shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        }
                        break;
                    case CONTROL_CHANGE:
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
