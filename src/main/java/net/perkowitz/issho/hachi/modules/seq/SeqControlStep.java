package net.perkowitz.issho.hachi.modules.seq;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 2/25/17.
 */
public class SeqControlStep {

    public static int RESET_VALUE = 0;
    public static int MAX_VALUE = 127;
    private static int BLUR_RANGE = 32;

    @Getter private int index;
    @Getter @Setter private boolean enabled = false;
    @Getter private int value;
    @Getter @Setter private boolean blurred = false;

    public SeqControlStep() {}

    public SeqControlStep(int index) {
        this.index = index;
        this.value = 0;
        this.enabled = false;
    }

    public void toggleEnabled() {
        enabled = !enabled;
    }

    public void reset() { value = RESET_VALUE; }

    public String toString() {
        return String.format("SeqCtrlStep:%02d", index);
    }

    // Value returns the step value adjusted by blurring, if enabled.
    public int Value() {
        int v = value;
        if (blurred) {
            v += (int)Math.floor(Math.random() * 2 * BLUR_RANGE) - BLUR_RANGE;
        }
        return Math.max(0, Math.min(127, v));
    }

    public void setValue(int value) {
        if (value < 0) {
            this.value = 0;
        } else if (value > MAX_VALUE) {
            this.value = MAX_VALUE;
        } else {
            this.value = value;
        }
    }

    public void incrementValue() {
        setValue(value + 1);
    }

    public void incrementValueMore() {
        setValue(value + 5);
    }

    public void decrementValue() {
        setValue(value - 1);
    }

    public void decrementValueMore() {
        setValue(value - 5);
    }

    /***** static methods **************************/

    public static SeqControlStep copy(SeqControlStep step, int newIndex) {
        SeqControlStep newStep = new SeqControlStep(newIndex);
        newStep.enabled = step.enabled;
        newStep.value = step.value;
        return newStep;
    }

}
