package net.perkowitz.issho.hachi.modules.deprecated.beatbox;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

/**
 * Created by optic on 10/24/16.
 */
public class BeatMemory implements MemoryObject {

    @Getter @Setter private int currentSessionIndex = 0;
    @Getter @Setter private int nextSessionIndex = 0;
    @Getter @Setter private int playingPatternIndex = 0;
    @Getter @Setter private int selectedPatternIndex = 0;

    @Getter @Setter private int midiChannel = 0;

    @Getter private List<BeatSession> sessions = Lists.newArrayList();


    public BeatMemory() {
        for (int i = 0; i < BeatUtil.SESSION_COUNT; i++) {
            sessions.add(new BeatSession(i));
        }
    }


    public String toString() {
        return String.format("BeatMemory");
    }


    /***** get current *************************************/

    @JsonIgnore
    public BeatSession getCurrentSession() {
        return sessions.get(currentSessionIndex);
    }

    @JsonIgnore
    public BeatPattern getPlayingPattern() {
        return getCurrentSession().getPattern(playingPatternIndex);
    }

    @JsonIgnore
    public BeatPattern getSelectedPattern() {
        return getCurrentSession().getPattern(selectedPatternIndex);
    }

    @JsonIgnore
    public BeatTrack getSelectedTrack() {
        return getSelectedPattern().getTrack(getSelectedTrackIndex());
    }

    @JsonIgnore
    public int getChainStartIndex() {
        return getCurrentSession().getChainStartIndex();
    }

    @JsonIgnore
    public int getChainEndIndex() {
        return getCurrentSession().getChainEndIndex();
    }

    @JsonIgnore
    public int getSelectedTrackIndex() {
        return getCurrentSession().getSelectedTrackIndex();
    }




    /***** make selections *************************************/

    public void selectSession(int index) {
        currentSessionIndex = index;
    }

    public void selectPattern(int index) {
        selectedPatternIndex = index;
    }

    public void selectChain(int startIndex, int endIndex) {
        getCurrentSession().selectChain(startIndex, endIndex);
        playingPatternIndex = startIndex;
    }

    public void resetChain() {
        playingPatternIndex = getCurrentSession().getChainEndIndex(); // set it to end so it will restart on next advance
    }

    public void selectTrack(int index) {
        getCurrentSession().setSelectedTrackIndex(index);
    }


    /***** play logic *************************************/

    public void advancePattern() {
        playingPatternIndex++;
        if (playingPatternIndex > getChainEndIndex()) {
            playingPatternIndex = getChainStartIndex();
        }
    }

    public boolean patternIsChained(int index) {
        return index >= getChainStartIndex() && index <= getChainEndIndex();
    }


    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        List<MemoryObject> objects = Lists.newArrayList();
        for (BeatSession session : sessions) {
            objects.add(session);
        }
        return objects;
    }

    public void put(int index, MemoryObject memoryObject) {
        if (memoryObject instanceof BeatSession) {
            BeatSession session = (BeatSession) memoryObject;
            session.setIndex(index);
            sessions.set(index, session);
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
