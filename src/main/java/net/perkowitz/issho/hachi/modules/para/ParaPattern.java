package net.perkowitz.issho.hachi.modules.para;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.MemoryUtil;

import java.util.List;


/**
 * Created by optic on 10/24/16.
 */
public class ParaPattern implements MemoryObject {

    public static int STEP_COUNT = 16;

    @Getter @Setter private int index;
    @Getter private ParaStep[] steps = new ParaStep[STEP_COUNT];


    public ParaPattern() {}

    public ParaPattern(int index) {
        this.index = index;
        for (int i = 0; i < STEP_COUNT; i++) {
            steps[i] = new ParaStep(i);
        }
    }


    public ParaStep getStep(int index) {
        return steps[index];
    }

    public void shift(int shiftAmount) {

        ParaStep[] shiftedSteps = new ParaStep[STEP_COUNT];
        for (int i = 0; i < steps.length; i++) {
            int shifted = (i + shiftAmount + STEP_COUNT) % STEP_COUNT;
            ParaStep step = new ParaStep(shifted);
            step = ParaStep.copy(steps[i], shifted);
            shiftedSteps[shifted] = step;
        }
        steps = shiftedSteps;
    }

    public String toString() {
        return String.format("ParaPattern:%02d", index);
    }


    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        return Lists.newArrayList();
    }

    public void put(int index, MemoryObject memoryObject) {
        System.out.println("Cannot add a MemoryObject to a ParaPattern");
    }


    public boolean nonEmpty() {
        for (ParaStep step : steps) {
            if (step.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    public MemoryObject clone() {
        return ParaPattern.copy(this, this.index);
    }

    public String render() {

        String stepString = "";
        for (ParaStep step : steps) {
            if (step.isEnabled()) {
                switch (step.getGate()) {
                    case PLAY:
                        stepString += "O";
                        break;
                    case TIE:
                        stepString += "-";
                        break;
                }
            } else {
                stepString += ".";
            }
        }

        return MemoryUtil.countRender(this, stepString);
    }



    /***** static methods **************************/

    public static ParaPattern copy(ParaPattern pattern, int newIndex) {
        ParaPattern newPattern = new ParaPattern(newIndex);
        for (int i = 0; i < STEP_COUNT; i++) {
            newPattern.steps[i] = ParaStep.copy(pattern.steps[i], i);
        }
        return newPattern;
    }

}
