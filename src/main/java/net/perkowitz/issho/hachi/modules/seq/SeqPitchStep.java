package net.perkowitz.issho.hachi.modules.seq;

import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.util.MidiUtil;

/**
 * Created by optic on 2/25/17.
 */
public class SeqPitchStep {

    private static float MIDI_PITCH_BEND_ONE_TWELFTH = (MidiUtil.MIDI_PITCH_BEND_MAX - MidiUtil.MIDI_PITCH_BEND_MIN) / 12;
    public static int[] CONTROL_VALUES = {
            0,
            (int)(MidiUtil.MIDI_PITCH_BEND_ZERO - 2 * MIDI_PITCH_BEND_ONE_TWELFTH),
            (int)(MidiUtil.MIDI_PITCH_BEND_ZERO - MIDI_PITCH_BEND_ONE_TWELFTH),
            MidiUtil.MIDI_PITCH_BEND_ZERO,
            MidiUtil.MIDI_PITCH_BEND_ZERO,
            (int)(MidiUtil.MIDI_PITCH_BEND_ZERO + MIDI_PITCH_BEND_ONE_TWELFTH),
            (int)(MidiUtil.MIDI_PITCH_BEND_ZERO + 2 * MIDI_PITCH_BEND_ONE_TWELFTH),
            MidiUtil.MIDI_PITCH_BEND_MAX
    };

    @Getter private int index;
    @Getter @Setter private boolean enabled = false;
    @Getter @Setter private int pitchBend;

    public SeqPitchStep() {}

    public SeqPitchStep(int index) {
        this.index = index;
        this.pitchBend = MidiUtil.MIDI_PITCH_BEND_ZERO;
    }

    public void toggleEnabled() {
        enabled = !enabled;
    }

    public void setPitchBendByIndex(int index) {
        pitchBend = pitchBendByIndex(index);
    }

    /***** static methods **************************/

    public static SeqPitchStep copy(SeqPitchStep step, int newIndex) {
        SeqPitchStep newStep = new SeqPitchStep(newIndex);
        newStep.enabled = step.enabled;
        newStep.pitchBend = step.pitchBend;
        return newStep;
    }

    public static int pitchBendByIndex(int index) {
        if (index < 0) { index = 0; }
        if (index >= CONTROL_VALUES.length) { index = CONTROL_VALUES.length - 1; }
        return CONTROL_VALUES[index];
    }

}
