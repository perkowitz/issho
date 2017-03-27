package net.perkowitz.issho.hachi.modules.beatbox;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 2/25/17.
 */
public class BeatStep {

    private int DEFAULT_VELOCITY = 80;

    @Getter private int index;
    @Getter @Setter private int velocity;
    @Getter @Setter private boolean enabled = false;

    public BeatStep() {}

    public BeatStep(int index) {
        this.index = index;
        this.velocity = DEFAULT_VELOCITY;
    }

    public void toggleEnabled() {
        enabled = !enabled;
    }


    /***** static methods **************************/

    public static BeatStep copy(BeatStep step, int newIndex) {
        BeatStep newStep = new BeatStep(newIndex);
        newStep.velocity = step.velocity;
        newStep.enabled = step.enabled;
        return newStep;
    }

}
