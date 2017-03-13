package net.perkowitz.issho.hachi.modules.minibeat;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

/**
 * Created by optic on 10/24/16.
 */
public class MinibeatMemory {

    @Getter private int currentSessionIndex = 0;
    @Getter private int nextSessionIndex = 0;
    @Getter private int playingPatternIndex = 0;
    @Getter private int selectedPatternIndex = 0;

    @Getter @Setter private int midiChannel = 0;

    @Getter private List<MinibeatSession> sessions = Lists.newArrayList();


    public MinibeatMemory() {
        for (int i = 0; i < MinibeatUtil.SESSION_COUNT; i++) {
            sessions.add(new MinibeatSession(i));
        }
    }


    /***** get current *************************************/

    @JsonIgnore
    public MinibeatSession getCurrentSession() {
        return sessions.get(currentSessionIndex);
    }

    @JsonIgnore
    public MinibeatPattern getPlayingPattern() {
        return getCurrentSession().getPattern(playingPatternIndex);
    }

    @JsonIgnore
    public MinibeatPattern getSelectedPattern() {
        return getCurrentSession().getPattern(selectedPatternIndex);
    }

    @JsonIgnore
    public MinibeatTrack getSelectedTrack() {
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


}
