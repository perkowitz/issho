package net.perkowitz.issho.controller.midi;

import com.google.common.collect.Sets;
import lombok.extern.java.Log;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.Set;
import java.util.logging.Level;

import static javax.sound.midi.ShortMessage.*;

@Log
public class MidiMonitor implements Receiver {

    static { log.setLevel(Level.OFF); }

    private static int MIDI_REALTIME_COMMAND = 0xF0;

    private String name;
    private Set<Integer> commandsToMonitor = Sets.newHashSet();
    private Set<Integer> statusToMonitor = Sets.newHashSet();


    public MidiMonitor(String name) {
        this.name = name;
        commandsToMonitor.add(MIDI_REALTIME_COMMAND);
        commandsToMonitor.add(NOTE_ON);
        commandsToMonitor.add(NOTE_OFF);
        commandsToMonitor.add(CONTROL_CHANGE);
        statusToMonitor.add(START);
        statusToMonitor.add(STOP);
        statusToMonitor.add(CONTINUE);
        statusToMonitor.add(TIMING_CLOCK);
    }

    public void addCommand(Integer command) {
        commandsToMonitor.add(command);
    }

    public void removeCommand(Integer command) {
        commandsToMonitor.remove(command);
    }

    public void addStatus(Integer status) {
        statusToMonitor.add(status);
    }

    public void removeStatus(Integer status) {
        statusToMonitor.remove(status);
    }



    public void display(String type, Integer channel, Integer data1, Integer data2) {
        if (channel == null) {
            System.out.printf("%s: %s\n", name, type);
        } else {
            System.out.printf("%s: %s (%d) %d:%d\n", name, type, channel, data1, data2);
        }
    }

    public void display(String type) {
        display(type, null, null, null);
    }

        /***** Receiver implementation ***************/

    public void close() {
    }

    public void send(MidiMessage message, long timeStamp) {

        if (message instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage) message;
            int command = shortMessage.getCommand();
            int status = shortMessage.getStatus();

            if (command == MIDI_REALTIME_COMMAND && commandsToMonitor.contains(MIDI_REALTIME_COMMAND)) {
                if (status == START && statusToMonitor.contains(START)) {
                    display("START");
                } else if (status == STOP && statusToMonitor.contains(STOP)) {
                    display("STOP");
                } else if (status == CONTINUE && statusToMonitor.contains(CONTINUE)) {
                    display("CONTINUE");
                } else if (status == TIMING_CLOCK && statusToMonitor.contains(TIMING_CLOCK)) {
                    display("CLOCK");
                }
            } else if (command == NOTE_ON && commandsToMonitor.contains(NOTE_ON)) {
                display("NOTE ON", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
            } else if (command == NOTE_OFF && commandsToMonitor.contains(NOTE_OFF)) {
                display("NOTE OFF", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
            } else if (command == CONTROL_CHANGE && commandsToMonitor.contains(CONTROL_CHANGE)) {
                display("CONTROL", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
            }
        }

    }

}
