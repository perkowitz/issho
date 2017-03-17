package net.perkowitz.issho.hachi.modules.step;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.modules.mono.MonoPattern;
import net.perkowitz.issho.hachi.modules.mono.MonoSession;

import java.util.List;

/**
 * Created by optic on 10/24/16.
 */
public class StepSession implements MemoryObject {

    public static int PATTERN_COUNT = 8;

    @Getter @Setter private int index;
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

    public String toString() {
        return "StepSession:" + index;
    }

    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        List<MemoryObject> objects = Lists.newArrayList();
        for (StepPattern pattern : patterns) {
            objects.add(pattern);
        }
        return objects;
    }

    public void put(int index, MemoryObject memoryObject) {
        if (memoryObject instanceof StepPattern) {
            StepPattern pattern = (StepPattern) memoryObject;
            pattern.setIndex(index);
            patterns[index] = pattern;
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
        return StepSession.copy(this, this.index);
    }


    /***** static methods ********************************/

    public static StepSession copy(StepSession session, int newIndex) {
        StepSession newSession = new StepSession(newIndex);
        for (int i = 0; i < PATTERN_COUNT; i++) {
            newSession.patterns[i] = StepPattern.copy(session.patterns[i], i);
        }
        newSession.scale = session.scale;
        return newSession;
    }


}
