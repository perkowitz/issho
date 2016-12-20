package net.perkowitz.issho.hachi.modules.step;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 10/24/16.
 */
public class StepSession {

    public static int PATTERN_COUNT = 8;

    @Getter private int index;
    @Getter private StepPattern[] patterns = new StepPattern[PATTERN_COUNT];
    @Getter @Setter private Scale scale;


    public StepSession() {}

    public StepSession(int index) {
        this.index = index;
        for (int i = 0; i < PATTERN_COUNT; i++) {
            patterns[i] = new StepPattern(i);
        }
    }


    public StepPattern getPattern(int index) {
        return patterns[index];
    }

    public void setPattern(int index, StepPattern pattern) {
        patterns[index] = pattern;
    }

}
