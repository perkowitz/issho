package net.perkowitz.issho.controller.apps.hachi.modules.step;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.controller.Log;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.MemoryUtil;

import java.util.List;

/**
 * Created by optic on 10/24/16.
 */
public class StepPattern implements MemoryObject {

    @Getter @Setter private int index;
    @Getter private List<Stage> stages;


    public StepPattern() {}

    public StepPattern(int index) {
        this.index = index;
        stages = Lists.newArrayList();
        for (int i = 0; i < StepModule.getStageCount(); i++) {
            stages.add(new Stage(i));
        }
    }


    public Stage getStage(int index) {
        if (index >= 0 && index < stages.size()) {
            return stages.get(index);
        }
        return null;
    }

    public void shift(int shiftAmount) {

        int stageCount = StepModule.getStageCount();
        Stage[] shiftedStages = new Stage[stageCount];
        for (int i = 0; i < stages.size(); i++) {
            int shifted = (i + shiftAmount + stageCount) % stageCount;
            Stage stage = new Stage(shifted);
            stage = Stage.copy(stages.get(i));
            stage.setIndex(shifted);
            shiftedStages[shifted] = stage;
        }
        stages = Lists.newArrayList(shiftedStages);
    }

    public void setStageCount(int stageCount) {
    }

    public int getStageCount() {
        return StepModule.getStageCount();
    }


    /***** overrides *****************************/

    @Override
    public String toString() {
        return String.format("StepPattern:%02d", index);
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
            stages.set(index, stage);
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

    public String render() {
        return MemoryUtil.countRender(this);
    }

    /***** static methods **************************/

    public static StepPattern copy(StepPattern pattern, int newIndex) {
        StepPattern newPattern = new StepPattern(newIndex);
        for (int i = 0; i < pattern.stages.size(); i++) {
            newPattern.stages.add(Stage.copy(pattern.stages.get(i)));
        }
        return newPattern;
    }

}
