package net.perkowitz.issho.hachi.modules.seq;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;

import static net.perkowitz.issho.hachi.modules.seq.SeqStep.GateMode.*;

/**
 * Created by optic on 2/25/17.
 */
public class SeqStep {

    public enum GateMode {
        PLAY, TIE, REST
    }

    private static int MAX_NOTE = 11;
    private static int MAX_OCTAVE = 10;
    private static int DEFAULT_VELOCITY = 80;
    private static int MAX_VELOCITY = 127;

    @Getter private int index;
    @Getter private int semitone;
    @Getter private int octave;
    @Getter private int velocity;
    @Getter @Setter private boolean enabled = false;
    @Getter @Setter private GateMode gateMode = GateMode.REST;

    public SeqStep() {}

    public SeqStep(int index) {
        this.index = index;
        this.velocity = DEFAULT_VELOCITY;
    }

    public void toggleEnabled() {
        enabled = !enabled;
    }

    public void advanceGateMode(boolean tieEnabled) {
        switch (gateMode) {
            case PLAY:
                if (tieEnabled) {
                    gateMode = TIE;
                } else {
                    gateMode = REST;
                }
                break;
            case TIE:
                gateMode = REST;
                break;
            case REST:
                gateMode = PLAY;
                break;
        }
    }

    public void setSemitone(int semitone) {
        if (semitone < 0) {
            this.semitone = 0;
        } else if (semitone > MAX_NOTE) {
            this.semitone = MAX_NOTE;
        } else {
            this.semitone = semitone;
        }
    }

    public void setOctave(int octave) {
        if (octave < 0) {
            this.octave = 0;
        } else if (octave > MAX_OCTAVE) {
            this.octave = MAX_OCTAVE;
        } else {
            this.octave = octave;
        }
    }

    public void setVelocity(int velocity) {
        if (velocity < 1) {
            this.velocity = 1;
        } else if (velocity > MAX_VELOCITY) {
            this.velocity = MAX_VELOCITY;
        } else {
            this.velocity = velocity;
        }
    }

    public void incrementVelocity() {
        setVelocity(velocity + 1);
    }

    public void incrementVelocityMore() {
        setVelocity(velocity + 5);
    }

    public void decrementVelocity() {
        setVelocity(velocity - 1);
    }

    public void decrementVelocityMore() {
        setVelocity(velocity - 5);
    }

    public String toString() {
        return String.format("SeqStep:%02d", index);
    }

    @JsonIgnore
    public int getNote() {
        return octave * 12 + semitone;
    }

    /***** static methods **************************/

    public static SeqStep copy(SeqStep step, int newIndex) {
        SeqStep newStep = new SeqStep(newIndex);
        newStep.semitone = step.semitone;
        newStep.octave = step.octave;
        newStep.velocity = step.velocity;
        newStep.enabled = step.enabled;
        newStep.gateMode = step.gateMode;
        return newStep;
    }

}
