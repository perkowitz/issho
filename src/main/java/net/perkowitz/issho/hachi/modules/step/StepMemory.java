package net.perkowitz.issho.hachi.modules.step;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;

import java.util.List;

/**
 * Created by optic on 10/24/16.
 */
public class StepMemory implements MemoryObject {

    private static int SESSION_COUNT = 16;

    @Getter private StepSession[] sessions = new StepSession[SESSION_COUNT];

    @Getter @Setter private int currentSessionIndex = 0;
    @Getter @Setter private int nextSessionIndex = 0;
    @Getter private int currentPatternIndex = 0;

    @Getter @Setter private int midiChannel = 0;



    public StepMemory() {
        for (int i = 0; i < SESSION_COUNT; i++) {
            sessions[i] = new StepSession(i);
        }
    }


    /***** getters for step/pattern/session by index or current **************************/

    public StepSession currentSession() {
        return sessions[currentSessionIndex];
    }

    public StepPattern currentPattern() {
        return currentSession().getPattern(currentPatternIndex);
    }

    public Scale currentScale() {
        return currentSession().getScale();
    }

    public void setCurrentPatternIndex(int currentPatternIndex) {
        this.currentPatternIndex = currentPatternIndex % StepSession.PATTERN_COUNT;
    }

    public String toString() {
        return "MonoMemory";
    }


    /***** select *******************************************************/


    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        List<MemoryObject> objects = Lists.newArrayList();
        for (StepSession session : sessions) {
            objects.add(session);
        }
        return objects;
    }

    public void put(int index, MemoryObject memoryObject) {
        if (memoryObject instanceof StepSession) {
            StepSession session = (StepSession) memoryObject;
            session.setIndex(index);
            sessions[index] = session;
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

    public int getIndex() { return 0; }
    public void setIndex(int index) {}

    public MemoryObject clone() {
        return null;
    }

    public String render() { return toString(); }



}
