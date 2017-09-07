package net.perkowitz.issho.hachi.modules.beatbox;

import lombok.Getter;
import lombok.Setter;

import static net.perkowitz.issho.hachi.modules.beatbox.BeatStep.GateMode.PLAY;
import static net.perkowitz.issho.hachi.modules.beatbox.BeatStep.GateMode.REST;
import static net.perkowitz.issho.hachi.modules.beatbox.BeatStep.GateMode.TIE;

/**
 * Created by optic on 2/25/17.
 */
public class BeatStep {

    public enum GateMode {
        PLAY, TIE, REST
    }

    private int DEFAULT_VELOCITY = 80;

    @Getter private int index;
    @Getter @Setter private int velocity;
    @Getter @Setter private boolean enabled = false;
    @Getter @Setter private GateMode gateMode = GateMode.REST;

    public BeatStep() {}

    public BeatStep(int index) {
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

    /***** static methods **************************/

    public static BeatStep copy(BeatStep step, int newIndex) {
        BeatStep newStep = new BeatStep(newIndex);
        newStep.velocity = step.velocity;
        newStep.enabled = step.enabled;
        newStep.gateMode = step.gateMode;
        return newStep;
    }

}
