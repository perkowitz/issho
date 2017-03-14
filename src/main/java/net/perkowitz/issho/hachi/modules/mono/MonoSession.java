package net.perkowitz.issho.hachi.modules.mono;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.perkowitz.issho.hachi.MemoryObject;

import java.util.List;

/**
 * Created by optic on 10/24/16.
 */
public class MonoSession implements MemoryObject {

    private static int PATTERN_COUNT = 16;

    @Getter private int index;
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

    public boolean nonEmpty() {
        for (MemoryObject object : list()) {
            if (object.nonEmpty()) {
                return true;
            }
        }
        return false;
    }

}
