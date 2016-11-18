package net.perkowitz.issho.hachi.modules.mono;

import lombok.Getter;

/**
 * Created by optic on 10/24/16.
 */
public class MonoPattern {

    public static int STEP_COUNT = 16;

    @Getter private int index;
    @Getter private MonoStep[] steps = new MonoStep[STEP_COUNT];


    public MonoPattern() {}

    public MonoPattern(int index) {
        this.index = index;
        for (int i = 0; i < STEP_COUNT; i++) {
            steps[i] = new MonoStep(i);
        }
    }


    public MonoStep getStep(int index) {
        return steps[index];
    }

    public void shift(int shiftAmount) {

        MonoStep[] shiftedSteps = new MonoStep[STEP_COUNT];
        for (int i = 0; i < steps.length; i++) {
            int shifted = (i + shiftAmount + STEP_COUNT) % STEP_COUNT;
            MonoStep step = new MonoStep(shifted);
            step = MonoStep.copy(steps[i]);
            step.setIndex(shifted);             // consider creating a pattern.putStep() that keeps the index in sync
            shiftedSteps[shifted] = step;
        }
        steps = shiftedSteps;
    }


    /***** static methods **************************/

    public static MonoPattern copy(MonoPattern pattern, int newIndex) {
        MonoPattern newPattern = new MonoPattern(newIndex);
        for (int i = 0; i < STEP_COUNT; i++) {
            newPattern.steps[i] = MonoStep.copy(pattern.steps[i]);
        }
        return newPattern;
    }

}
