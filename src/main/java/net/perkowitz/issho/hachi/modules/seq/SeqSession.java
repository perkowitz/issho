package net.perkowitz.issho.hachi.modules.seq;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.MemoryUtil;

import java.util.List;

/**
 * Created by optic on 2/25/17.
 */
public class SeqSession implements MemoryObject {

    @Getter @Setter private int index;
    @Getter private List<SeqPattern> patterns = Lists.newArrayList();

    // sessions remember their state so you can load them to specific patterns and mutes
    @Getter private List<Boolean> tracksEnabled = Lists.newArrayList();
    @Getter private int chainStartIndex = 0;
    @Getter private int chainEndIndex = 0;
    @Getter @Setter private int selectedTrackIndex = 8;
    @Getter @Setter private int swingOffset = 0;


    public SeqSession() {}

    public SeqSession(int index) {
        this.index = index;
        for (int i = 0; i < SeqUtil.PATTERN_COUNT; i++) {
            patterns.add(new SeqPattern(i));
        }
        for (int i = 0; i < SeqUtil.TRACK_COUNT; i++) {
            tracksEnabled.add(true);
        }
    }

    public SeqPattern getPattern(int index) {
        return patterns.get(index);
    }

    public Boolean trackIsEnabled(int index) { return tracksEnabled.get(index); }

    public void setTrackEnabled(int index, boolean isEnabled) { tracksEnabled.set(index, isEnabled); }

    public void toggleTrackEnabled(int index) {
        tracksEnabled.set(index, !tracksEnabled.get(index));
    }

    public void selectChain(int chainStartIndex, int chainEndIndex) {
        this.chainStartIndex = chainStartIndex;
        this.chainEndIndex = chainEndIndex;
    }

    public String toString() {
        return String.format("SeqSession:%02d", index);
    }


    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        List<MemoryObject> objects = Lists.newArrayList();
        for (SeqPattern pattern : patterns) {
            objects.add(pattern);
        }
        return objects;
    }

    public void put(int index, MemoryObject memoryObject) {
        if (memoryObject instanceof SeqPattern) {
            SeqPattern pattern = (SeqPattern) memoryObject;
            pattern.setIndex(index);
            patterns.set(index, pattern);
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
        return SeqSession.copy(this, this.getIndex());
    }

    public String render() {
        return MemoryUtil.countRender(this);
    }


    /***** static methods **************************/

    public static SeqSession copy(SeqSession session, int newIndex) {
        SeqSession newSession = new SeqSession(newIndex);
        try {

            for (int i = 0; i < session.patterns.size(); i++) {
                newSession.patterns.set(i, SeqPattern.copy(session.patterns.get(i), i));
            }

            for (Boolean trackEnabled : session.tracksEnabled) {
                newSession.tracksEnabled.add(trackEnabled);
            }

            newSession.chainStartIndex = session.chainStartIndex;
            newSession.chainEndIndex = session.chainEndIndex;
            newSession.selectedTrackIndex = session.selectedTrackIndex;
            newSession.swingOffset = session.swingOffset;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newSession;
    }

}
