package net.perkowitz.issho.hachi.modules.seq;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.MemoryUtil;
import net.perkowitz.issho.hachi.modules.deprecated.rhythm.models.Track;

import java.util.List;

import static net.perkowitz.issho.hachi.modules.seq.SeqUtil.SeqMode.BEAT;
import static net.perkowitz.issho.hachi.modules.seq.SeqUtil.SeqMode.MCBEAT;

/**
 * Created by optic on 2/25/17.
 */
public class SeqSession implements MemoryObject {

    @Getter @Setter private int index;
    @Getter private List<SeqPattern> patterns = Lists.newArrayList();
    private SeqUtil.SeqMode mode; // need to remember this for session copy

    // sessions remember their state so you can load them to specific patterns and mutes
    @Getter private List<Boolean> tracksEnabled = Lists.newArrayList();
    @Getter private List<Boolean> controlTracksEnabled = Lists.newArrayList();
    @Getter private int chainStartIndex = 0;
    @Getter private int chainEndIndex = 0;
    @Getter @Setter private int selectedTrackIndex = 0;
    @Getter @Setter private int selectedStepIndex = 0;
    @Getter @Setter private int swingOffset = 0;


    public SeqSession() {}

    public SeqSession(int index, SeqUtil.SeqMode mode) {
        this.index = index;
        this.mode = mode;
        for (int i = 0; i < SeqUtil.PATTERN_COUNT; i++) {
            patterns.add(new SeqPattern(i, mode));
        }

        // in beat mode, we have multiple tracks; in other modes, just one
        if (mode == BEAT || mode == MCBEAT) {
            for (int i = 0; i < SeqUtil.BEAT_TRACK_COUNT; i++) {
                tracksEnabled.add(true);
            }
        } else {
            tracksEnabled.add(true);
        }
        for (int i = 0; i < SeqUtil.CONTROL_TRACK_COUNT; i++) {
            controlTracksEnabled.add(true);
        }

    }

    public SeqPattern getPattern(int index) {
        return patterns.get(index);
    }

    public Boolean trackIsEnabled(int index) {
        if (index >= 0 && index < tracksEnabled.size()) {
            return tracksEnabled.get(index);
        }
        return false;
    }

    public Boolean controlTrackIsEnabled(int index) {
        if (index >= 0 && index < controlTracksEnabled.size()) {
            return controlTracksEnabled.get(index);
        }
        return false;
    }

    public void setTrackEnabled(int index, boolean isEnabled) { tracksEnabled.set(index, isEnabled); }

    public void toggleTrackEnabled(int index) {
        tracksEnabled.set(index, !tracksEnabled.get(index));
    }

    public void setControlTrackEnabled(int index, boolean isEnabled) { controlTracksEnabled.set(index, isEnabled); }

    public void toggleControlTrackEnabled(int index) {
        // TODO: this shouldn't be needed; however, find a way to fill out empty Seq objects on the fly in general
        if (controlTracksEnabled.size() == 0) {
            for (int i = 0; i < SeqUtil.CONTROL_TRACK_COUNT; i++) {
                controlTracksEnabled.add(false);
            }
        }
        controlTracksEnabled.set(index, !controlTracksEnabled.get(index));
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
        objects.addAll(patterns);
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
        SeqSession newSession = new SeqSession(newIndex, session.mode);
        try {

            for (int i = 0; i < session.patterns.size(); i++) {
                newSession.patterns.set(i, SeqPattern.copy(session.patterns.get(i), i));
            }

            newSession.tracksEnabled.addAll(session.tracksEnabled);
            newSession.controlTracksEnabled.addAll(session.controlTracksEnabled);

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
