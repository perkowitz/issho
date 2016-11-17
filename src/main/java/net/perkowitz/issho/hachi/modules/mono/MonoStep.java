package net.perkowitz.issho.hachi.modules.mono;

import lombok.Getter;
import lombok.Setter;

import static net.perkowitz.issho.hachi.modules.mono.MonoUtil.Gate.REST;

/**
 * Created by optic on 10/24/16.
 */
public class MonoStep {

    private static int DEFAULT_NOTE = 60;
    private static int DEFAULT_VELOCITY = 100;
    private static MonoUtil.Gate DEFAULT_GATE = REST;

    @Getter @Setter private int index;

    @Getter @Setter private int octaveNote;
    @Getter @Setter private int octave;
    @Getter @Setter private int velocity;
    @Getter @Setter private int length;
    @Getter @Setter private MonoUtil.Gate gate;
    @Getter @Setter private boolean enabled = true;
    @Getter @Setter private boolean selected = false;

    public MonoStep() {}

    public MonoStep(int index) {
        this.index = index;
        this.octaveNote = DEFAULT_NOTE % 12;
        this.octave = DEFAULT_NOTE / 12;
        this.velocity = DEFAULT_VELOCITY;
        this.length = 1;
        this.gate = DEFAULT_GATE;
        this.enabled = true;
        this.selected = false;
    }

    public void toggleEnabled() {
//        enabled = !enabled;
    }

    public int getNote() {
        return octave * 12 + octaveNote;
    }

    public void setNote(int note) {
        octaveNote = note % 12;
        octave = note / 12;
    }

    /***** static methods **************************/

    public static MonoStep copy(MonoStep step) {
        MonoStep newStep = new MonoStep();
        newStep.index = step.index;
        newStep.octaveNote = step.octaveNote;
        newStep.octave = step.octave;
        newStep.velocity = step.velocity;
        newStep.length = step.length;
        newStep.gate = step.gate;
        newStep.enabled = step.enabled;
        newStep.selected = step.selected;
        return newStep;
    }
}
