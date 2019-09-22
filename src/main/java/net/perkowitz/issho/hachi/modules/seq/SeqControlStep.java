package net.perkowitz.issho.hachi.modules.seq;

import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.util.MidiUtil;

/**
 * Created by optic on 2/25/17.
 */
public class SeqControlStep {

    public static int RESET_VALUE = 0;

    @Getter private int index;
    @Getter @Setter private boolean enabled = false;
    @Getter @Setter private int value;

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


    /***** static methods **************************/

    public static SeqControlStep copy(SeqControlStep step, int newIndex) {
        SeqControlStep newStep = new SeqControlStep(newIndex);
        newStep.enabled = step.enabled;
        newStep.value = step.value;
        return newStep;
    }

}
