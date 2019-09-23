package net.perkowitz.issho.hachi.modules.deprecated.rhythm.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 7/9/16.
 */
public class Step {

    @Getter private int index;
    @Getter @Setter private boolean selected = false;

    @Getter @Setter private boolean on = false;
    @Getter @Setter private int velocity = 100;

    // only used for deserializing JSON; Stage should always be created with an index
    public Step() {}

    public Step(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Step:" + getIndex();
    }


    /***** static methods ***********************/

    public static Step copy(Step step, int newIndex) {
        Step newStep = new Step(newIndex);
        newStep.selected = step.selected;
        newStep.on = step.on;
        newStep.velocity = step.velocity;
        return newStep;
    }

}
