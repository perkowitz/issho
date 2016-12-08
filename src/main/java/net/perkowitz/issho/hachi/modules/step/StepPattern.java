package net.perkowitz.issho.hachi.modules.step;

import lombok.Getter;

/**
 * Created by optic on 10/24/16.
 */
public class StepPattern {

    public static int STAGE_COUNT = 8;

    @Getter private int index;
    @Getter private Stage[] stages = new Stage[STAGE_COUNT];


    public StepPattern() {}

    public StepPattern(int index) {
        this.index = index;
        for (int i = 0; i < STAGE_COUNT; i++) {
            stages[i] = new Stage(i);
        }
    }


    public Stage getStage(int index) {
        return stages[index];
    }

    public void shift(int shiftAmount) {

        Stage[] shiftedStages = new Stage[STAGE_COUNT];
        for (int i = 0; i < stages.length; i++) {
            int shifted = (i + shiftAmount + STAGE_COUNT) % STAGE_COUNT;
            Stage stage = new Stage(shifted);
            stage = Stage.copy(stages[i]);
            stage.setIndex(shifted);
            shiftedStages[shifted] = stage;
        }
        stages = shiftedStages;
    }


    /***** static methods **************************/

    public static StepPattern copy(StepPattern pattern, int newIndex) {
        StepPattern newPattern = new StepPattern(newIndex);
        for (int i = 0; i < STAGE_COUNT; i++) {
            newPattern.stages[i] = Stage.copy(pattern.stages[i]);
        }
        return newPattern;
    }

}
