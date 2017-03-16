package net.perkowitz.issho.hachi.modules.mono;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;

import java.util.List;

/**
 * Created by optic on 10/24/16.
 */
public class MonoSession implements MemoryObject {

    private static int PATTERN_COUNT = 16;

    @Getter @Setter private int index;
    @Getter private MonoPattern[] patterns = new MonoPattern[PATTERN_COUNT];

    public MonoSession() {}

    public MonoSession(int index) {
        this.index = index;
        for (int i = 0; i < PATTERN_COUNT; i++) {
            patterns[i] = new MonoPattern(i);
        }
    }


    public MonoPattern getPattern(int index) {
        return patterns[index];
    }

    public String toString() {
        return "MonoSession:" + index;
    }

    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        List<MemoryObject> objects = Lists.newArrayList();
        for (MonoPattern pattern : patterns) {
            objects.add(pattern);
        }
        return objects;
    }

    public void put(int index, MemoryObject memoryObject) {
        if (memoryObject instanceof MonoPattern) {
            MonoPattern pattern = (MonoPattern) memoryObject;
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
        return MonoSession.copy(this, this.index);
    }


    /***** static methods **************************/

    public static MonoSession copy(MonoSession session, int newIndex) {
        MonoSession newSession = new MonoSession(newIndex);
        for (int i = 0; i < PATTERN_COUNT; i++) {
            newSession.patterns[i] = MonoPattern.copy(session.patterns[i], i);
        }
        return newSession;
    }


}
