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
public class ParaSession implements MemoryObject {

    private static int PATTERN_COUNT = 16;

    @Getter @Setter private int index;
    @Getter private ParaPattern[] patterns = new ParaPattern[PATTERN_COUNT];

    public ParaSession() {}

    public ParaSession(int index) {
        this.index = index;
        for (int i = 0; i < PATTERN_COUNT; i++) {
            patterns[i] = new ParaPattern(i);
        }
    }


    public ParaPattern getPattern(int index) {
        return patterns[index];
    }

    public String toString() {
        return String.format("ParaSession:%02d", index);
    }

    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        List<MemoryObject> objects = Lists.newArrayList();
        for (ParaPattern pattern : patterns) {
            objects.add(pattern);
        }
        return objects;
    }

    public void put(int index, MemoryObject memoryObject) {
        if (memoryObject instanceof ParaPattern) {
            ParaPattern pattern = (ParaPattern) memoryObject;
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
        return ParaSession.copy(this, this.index);
    }

    public String render() {
        return MemoryUtil.countRender(this);
    }


    /***** static methods **************************/

    public static ParaSession copy(ParaSession session, int newIndex) {
        ParaSession newSession = new ParaSession(newIndex);
        for (int i = 0; i < PATTERN_COUNT; i++) {
            newSession.patterns[i] = ParaPattern.copy(session.patterns[i], i);
        }
        return newSession;
    }


}
