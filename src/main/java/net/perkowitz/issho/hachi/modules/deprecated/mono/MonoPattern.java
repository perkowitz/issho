package net.perkowitz.issho.hachi.modules.deprecated.mono;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.MemoryUtil;

import java.util.List;

import static net.perkowitz.issho.hachi.modules.deprecated.mono.MonoUtil.Gate.REST;

/**
 * Created by optic on 10/24/16.
 */
public class MonoPattern implements MemoryObject {

    public static int STEP_COUNT = 16;

    @Getter @Setter private int index;
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

    public String toString() {
        return String.format("MonoPattern:%02d", index);
    }


    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        return Lists.newArrayList();
    }

    public void put(int index, MemoryObject memoryObject) {
        System.out.println("Cannot add a MemoryObject to a MonoPattern");
    }


    public boolean nonEmpty() {
        for (MonoStep step : steps) {
            if (step.getGate() != REST) {
                return true;
            }
        }
        return false;
    }

    public MemoryObject clone() {
        return MonoPattern.copy(this, this.index);
    }

    public String render() {

        String stepString = "";
        for (MonoStep step : steps) {
            switch (step.getGate()) {
                case PLAY:
                    stepString += "O";
                    break;
                case TIE:
                    stepString += "-";
                    break;
                case REST:
                    stepString += ".";
                    break;
            }
        }

        return MemoryUtil.countRender(this, stepString);
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
