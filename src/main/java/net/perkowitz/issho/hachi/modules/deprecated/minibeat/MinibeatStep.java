package net.perkowitz.issho.hachi.modules.deprecated.minibeat;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 2/25/17.
 */
public class MinibeatStep {

    private int DEFAULT_VELOCITY = 100;

    @Getter private int index;
    @Getter @Setter private int velocity;
    @Getter @Setter private boolean enabled = false;

    public MinibeatStep() {}

    public MinibeatStep(int index) {
        this.index = index;
        this.velocity = DEFAULT_VELOCITY;
    }

    public void toggleEnabled() {
        enabled = !enabled;
    }


    /***** static methods **************************/

    public static MinibeatStep copy(MinibeatStep step, int newIndex) {
        MinibeatStep newStep = new MinibeatStep(newIndex);
        newStep.velocity = step.velocity;
        newStep.enabled = step.enabled;
        return newStep;
    }

}
