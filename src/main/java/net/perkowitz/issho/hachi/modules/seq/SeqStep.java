package net.perkowitz.issho.hachi.modules.seq;

import lombok.Getter;
import lombok.Setter;

import static net.perkowitz.issho.hachi.modules.seq.SeqStep.GateMode.*;

/**
 * Created by optic on 2/25/17.
 */
public class SeqStep {

    public enum GateMode {
        PLAY, TIE, REST
    }

    private int DEFAULT_VELOCITY = 80;
    public static int MAX_VELOCITY = 127;

    @Getter private int index;
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


    /***** static methods **************************/

    public static SeqStep copy(SeqStep step, int newIndex) {
        SeqStep newStep = new SeqStep(newIndex);
        newStep.velocity = step.velocity;
        newStep.enabled = step.enabled;
        newStep.gateMode = step.gateMode;
        return newStep;
    }

}
