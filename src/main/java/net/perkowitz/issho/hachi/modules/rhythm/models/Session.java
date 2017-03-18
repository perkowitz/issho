package net.perkowitz.issho.hachi.modules.rhythm.models;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.MemoryUtil;

import java.util.List;

/**
 * Created by optic on 7/9/16.
 */
public class Session implements MemoryObject {

    @Getter @Setter private static int patternCount = 16;
    @Getter private Pattern[] patterns;

    @Getter @Setter private static int fillCount = 8;
    @Getter private FillPattern[] fills;

    @Getter @Setter private int index;
    @Getter @Setter private boolean selected = false;
    @Getter @Setter private boolean next = false;

    // only used for deserializing JSON; Session should always be created with an index
    public Session() {}

    public Session(int index) {

        this.index = index;

        this.patterns = new Pattern[patternCount];
        for (int i = 0; i < patternCount; i++) {
            patterns[i] = new Pattern(i);
        }

        this.fills = new FillPattern[fillCount];
        for (int i = 0; i < fillCount; i++) {
            fills[i] = new FillPattern(i, (int)Math.pow(2, (i / 2) + 1));
        }

    }

    public Pattern getPattern(int index) {
        return patterns[index % patternCount];
    }
    public FillPattern getFill(int index) { return fills[index % fillCount]; }

    public String toString() {
        return String.format("RhythmSession:%02d", index);
    }


    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        List<MemoryObject> objects = Lists.newArrayList();
        for (Pattern pattern : patterns) {
            objects.add(pattern);
        }
        for (FillPattern fillPattern : fills) {
            objects.add(fillPattern);
        }
        return objects;
    }

    public void put(int index, MemoryObject memoryObject) {
        if (memoryObject instanceof Pattern) {
            Pattern pattern = (Pattern) memoryObject;
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
        return null;
    }

    public String render() {
        return MemoryUtil.countRender(this);
    }

    /***** static methods ***********************/

    public static Session copy(Session session, int newIndex) {
        Session newSession = new Session(newIndex);
        for (int index = 0; index < patternCount; index++) {
            newSession.patterns[index] = Pattern.copy(session.patterns[index], index);
        }
        for (int index = 0; index < fillCount; index++) {
            newSession.fills[index] = (FillPattern) Pattern.copy(session.fills[index], index);
        }
        return newSession;
    }

}
