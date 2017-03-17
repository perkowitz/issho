package net.perkowitz.issho.hachi.modules.step;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;

import java.util.List;

/**
 * Created by optic on 10/24/16.
 */
public class StepPattern implements MemoryObject {

    public static int STAGE_COUNT = 8;

    @Getter @Setter private int index;
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


    /***** overrides *****************************/

    @Override
    public String toString() {
        return "StepPattern:" + index;
    }


    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        List<MemoryObject> objects = Lists.newArrayList();
        for (Stage stage : stages) {
            objects.add(stage);
        }
        return objects;
    }

    public void put(int index, MemoryObject memoryObject) {
        if (memoryObject instanceof StepPattern) {
            Stage stage = (Stage) memoryObject;
            stage.setIndex(index);
            stages[index] = stage;
        } else {
            System.out.printf("Cannot put object %s of type %s in object %s\n", memoryObject, memoryObject.getClass().getSimpleName(), this);
        }
    }

    public boolean nonEmpty() {
        for (MemoryObject object : list()) {
            if (object.nonEmpty()) {
                return true;
            }
        }
        return false;
    }

    public MemoryObject clone() {
        return StepPattern.copy(this, this.index);
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
