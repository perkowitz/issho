package net.perkowitz.issho.midi;

import javax.sound.midi.*;

/**
 * Created by optic on 8/21/17.
 */
public class ClockSequence {

    public static int PPQN = 24;
    private static int LOOP_LENGTH = PPQN * 4;

    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;

    private ShortMessage message1;
    private ShortMessage message2;


    public ClockSequence(Receiver receiver) {

        try {

            // create a sequencer
            this.sequencer = MidiSystem.getSequencer();
            sequencer.open();

            // create a sequencer and fill it with data
            sequence = new Sequence(Sequence.PPQ, PPQN);
            track = sequence.createTrack();
            message1 = new ShortMessage();
            message1.setMessage(ShortMessage.NOTE_ON, 3, 60, 64);
            message2 = new ShortMessage();
            message2.setMessage(ShortMessage.NOTE_OFF, 3, 60, 0);
            MidiEvent event1 = new MidiEvent(message1, 0);
            MidiEvent event2 = new MidiEvent(message2, LOOP_LENGTH - 4);
            MidiEvent event3 = new MidiEvent(message2, LOOP_LENGTH);
            track.add(event1);
            track.add(event2);
            track.add(event3);

            // sequencer settings
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.setLoopStartPoint(0);
            sequencer.setLoopEndPoint(LOOP_LENGTH);
            Transmitter transmitter = sequencer.getTransmitter();
            transmitter.setReceiver(receiver);
            sequencer.setMasterSyncMode(Sequencer.SyncMode.MIDI_SYNC);
            sequencer.setSlaveSyncMode(Sequencer.SyncMode.MIDI_SYNC);
            System.out.printf("Seq sync=%s,%s\n", sequencer.getMasterSyncMode(), sequencer.getSlaveSyncMode());
            sequencer.setTempoInBPM(130);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public void close() {
        sequencer.close();
    }

    public void start() {
        sequencer.start();
    }

    public void stop() {
        sequencer.stop();
    }

}
