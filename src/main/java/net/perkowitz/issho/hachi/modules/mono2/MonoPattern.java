package net.perkowitz.issho.hachi.modules.mono2;

import lombok.Getter;

/**
 * Created by optic on 10/24/16.
 */
public class MonoPattern {

    public static int STEP_COUNT = 16;

    @Getter private int index;
    @Getter private MonoStep[] steps = new MonoStep[STEP_COUNT];


    public MonoPattern(int index) {
        this.index = index;
        for (int i = 0; i < STEP_COUNT; i++) {
            steps[i] = new MonoStep(i);
        }
    }



    public MonoStep getStep(int index) {
        return steps[index];
    }

}
